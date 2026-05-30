package com.labs.urlshortener.service;

import com.labs.urlshortener.repository.UrlRepository;
import com.labs.urlshortener.util.Base62;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
* 이전 : HashMap (In-Memory) -> 서버 시작하면 다아 날라감
 * 이후 : Redis -> 서버 재시작해도 데이터 유지
* */
public class UrlShortenerService {

    private final UrlRepository repository;

    // Redis에 저장된 마지막 ID를 기준으로 카운터 시작
    // 서버 재시작해도 중복 코드 생성 방지
    private final AtomicLong counter = new AtomicLong(1);

    public UrlShortenerService() {
        this.repository = new UrlRepository();
    }

    /**
     * 원본 URL -> 단축 코드 생성
     * 이미 단축된 URL이면 기존 코드 반환 (중복 방지)
     */
    public String shorten (String originalUrl) {
        //이미 단축된 URL이면 기존 토드 반환
        String existingCode = repository.findCodeByUrl(originalUrl);
        if(existingCode != null){
            return existingCode;
        }

        // 새로운 코드 생성
        long id = counter.getAndIncrement();
        String code = Base62.encode(id);

        //Redis에 저장
        repository.save(code,originalUrl);
        repository.saveReverse(originalUrl,code);

        return code;
    }

    /**
     * 단축 코드 -> 원본 url 조회
     * 없으면 null 반환
     */

    public String getOriginalUrl(String code){
        return repository.findByCode(code);
    }

}
