package com.labs.urlshortener.util;

public class Base62 {

    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = 62;

    // 숫자 -> 단축 코드
    public static String encode (long id) {
        StringBuilder sb = new StringBuilder();

        while (id>0) {
            sb.append(CHARACTERS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    // 단축코드 -> 숫자
    public static long decode (String code){
        long result =0;
        for (char c: code.toCharArray()){
            result = result * BASE + CHARACTERS.indexOf(c);
        }
        return result;
    }

}
