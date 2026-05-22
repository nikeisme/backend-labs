package com.labs.urlshortener.service;

import com.labs.urlshortener.util.Base62;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class UrlShortenerService {

    // 임시 In-Memory 저장소
    private final Map<String,String> codeToUrl = new HashMap<>();
    private final Map<String,String> urlToCode = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    //단축 URL 생성
    public String shorten(String originalUrl){

        //이미 단축된 URL이면 그대로 반환
        if(urlToCode.containsKey(originalUrl)){
            return urlToCode.get(originalUrl);
        }

        long id = counter.getAndIncrement();
        String code = Base62.encode(id);

        codeToUrl.put(code,originalUrl);
        urlToCode.put(originalUrl,code);

        return code;
    }

    // 단축 코드를 원본 URL로 조회
    public String getOriginalUrl(String code){
        return codeToUrl.get(code);
    }
}
