package com.labs.urlshortener.repository;

import redis.clients.jedis.Jedis;

/*
* Redis를 저장소로 사용하는 URL 레포지토리
* 서버 재시작해도 데이터가 유지됨 (In-Memory HashMap과의 차이)
*/
public class UrlRepository {

    //Jedis : Java에서 Redis에 명령어를 보낼 수 있게 해주는 클라이언트 라이브러리
    private final Jedis jedis;

    public UrlRepository(){
        // Redis 서버 주소와 포트 연결
        // 로컬에서 기본 포트 (6379)로 실행 중인 Redis 접속
        this.jedis = new Jedis("localhost",6379);
    }

    /*
    * 단축 코드 -> 원본 저장
    * Redis에 "url:{code}" 키로 저장
    * */
    public void save(String code, String originalUrl){
        jedis.set("url: " + code,originalUrl);
    }

    /*
    * 원본 URL -> 단축 코드 저장 (중복 방지용)
    * 같은 URL이 들어왔을 떄 새 코드를 만들지 말고 기존 코드로 반환하기 위함
    * ex ) "reverse : http://www.naver.com" -> "1"
    * */
    public void saveReverse(String originalUrl, String code){
        jedis.set("reverse: " + originalUrl, code);
    }

    /*
    * 단축 코드로 원본 URL 조회
    * ex ) "1" 입력 -> "http://www.naver.com"  반환
    * 없으면 null 반환
    * */
    public String findByCode(String code){
        return jedis.get("url: "+ code);
    }

    /*
    * 원본 URL로 단축 코드 조회(중복 체크용)
    * ex) "http://www.naver.com" 입력 -> "1" 반환
    * 없으면 null 반환
    * */
    public String findCodeByUrl(String originalUrl){
        return jedis.get("reverse : " + originalUrl );
    }

}
