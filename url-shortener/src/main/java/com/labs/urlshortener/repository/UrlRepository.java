package com.labs.urlshortener.repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
* Redis를 저장소로 사용하는 URL 레포지토리
*
* [Connection Pool 적용]
* Jedis 단일 연결 방식은 멀티스레드 환경에서 안전하지 않음
* JedisPool을 사용해 연결을 재사용하고 동시 요청을 안전하게 처리
*
* Spring Data Redis의 RedisTemplate도 내부적으로 이 방식을 사용함
*/
public class UrlRepository {

    // 단일 Jedis 인스턴스 대신 Pool 사용
    private final JedisPool jedisPool;

    public UrlRepository(){

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // 최대 연결 수 : 동시에 Redis에 접근할 수 있는 최대 연결 개수
        poolConfig.setMaxTotal(10);

        // 최대 대기 연결 개수 : Pool에서 놀고 있는 연결 최대 개수
        poolConfig.setMaxIdle(5);

        // 최소 대기 연결 개수 : 항상 유지할 최소 연결  개수
        poolConfig.setMinIdle(2);

        // 연결 고갈 시 대기 여부: true면 연결 생길 때까지 대기, false면 즉시 예외
        poolConfig.setBlockWhenExhausted(true);

        this.jedisPool = new JedisPool(poolConfig,"localhost",6379);
    }

    /*
    * 단축 코드 -> 원본 저장
    * Redis에 "url:{code}" 키로 저장
    * */
    public void save(String code, String originalUrl){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.set("url:" + code,originalUrl);
        }
    }

    /*
    * 원본 URL -> 단축 코드 저장 (중복 방지용)
    * 같은 URL이 들어왔을 떄 새 코드를 만들지 말고 기존 코드로 반환하기 위함
    * ex ) "reverse : http://www.naver.com" -> "1"
    * */
    public void saveReverse(String originalUrl, String code){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.set("reverse:" + originalUrl, code);
        }
    }

    /*
    * 단축 코드로 원본 URL 조회
    * ex ) "1" 입력 -> "http://www.naver.com"  반환
    * 없으면 null 반환
    * */
    public String findByCode(String code){
        try (Jedis jedis = jedisPool.getResource()){
            return  jedis.get("url:"+ code);
        }
    }

    /*
    * 원본 URL로 단축 코드 조회(중복 체크용)
    * ex) "http://www.naver.com" 입력 -> "1" 반환
    * 없으면 null 반환
    * */
    public String findCodeByUrl(String originalUrl){
        try (Jedis jedis = jedisPool.getResource()){
            return jedis.get("reverse:" + originalUrl );
        }
    }

}
