package com.beyond.ordersystem.ordering.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SseController {
    //  SSeEmitter 는 연결된 사용자 정보를 의미
    //  ConcurrentHashMap 은 Thread-safe 한 map(동시성 이슈 발생 x)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(1000 * 60 * 60 * 24L );  //  유효시간 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emitters.put(email, emitter);
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!!!!"));
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return emitter;
    }
}
