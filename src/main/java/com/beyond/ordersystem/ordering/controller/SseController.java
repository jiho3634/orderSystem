package com.beyond.ordersystem.ordering.controller;

import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SseController implements MessageListener {

    //  SSeEmitter 는 연결된 사용자 정보를 의미
    //  ConcurrentHashMap 은 Thread-safe 한 map(동시성 이슈 발생 x)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //  여러 번 구독을 방지하기 위함.
    private Set<String> subscribeList = ConcurrentHashMap.newKeySet();

    @Qualifier("4")
    private final RedisTemplate<String, Object> sseRedisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public SseController(@Qualifier("4") RedisTemplate<String, Object> sseRedisTemplate
                                        , RedisMessageListenerContainer redisMessageListenerContainer) {
        this.sseRedisTemplate = sseRedisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    //  email 에 해당되는 메시지를 listen
    public void subscribeChannel(String email) {
        //  이미 구독한 email 인 경우에 더 이상 구독하지 않는 분기 처리
        if (!subscribeList.contains(email)) {
            MessageListenerAdapter listenerAdapter = createListenerAdapter(this);
            redisMessageListenerContainer.addMessageListener(listenerAdapter, new PatternTopic(email));
            subscribeList.add(email);
        }
    }

    //  redis 에 메시지가 발행되면 listen 하게 되고, 아래 코드를 통해 특정 메서드를 실행하도록 설정
    private MessageListenerAdapter createListenerAdapter(SseController sseController) {
        return new MessageListenerAdapter(sseController, "onMessage");
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(1000 * 60 * 60 * 24L);  //  유효시간 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emitters.put(email, emitter);
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!!!!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        subscribeChannel(email);
        return emitter;
    }

    public void publishMessage(OrderListResDto dto, String email) {
        SseEmitter emitter = emitters.get(email);
        //  내 서버에 해당 emitter 가 있다면 서버에서 직접 알림을 보냄
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("ordered").data(dto));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        //  내 서버에 해당 emitter 가 없으면 redis 에 message 를 보내서 모든 서버에 뿌림
        } else {
            sseRedisTemplate.convertAndSend(email, dto);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        //  message 내용 parsing
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OrderListResDto dto = objectMapper.readValue(message.getBody(), OrderListResDto.class);
            String email = new String(pattern, StandardCharsets.UTF_8);
            SseEmitter emitter = emitters.get(email);
            //  해당 emitter 가 이 서버에 있다면 이 서버에서 알림을 보낸다.
            if (emitter != null) {
                emitter.send(SseEmitter.event().name("ordered").data(dto));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}