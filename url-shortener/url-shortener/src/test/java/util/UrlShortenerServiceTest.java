package util;

import com.labs.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UrlShortenerServiceTest {

    private UrlShortenerService service;

    @BeforeEach
    void setUp(){
        service = new UrlShortenerService();
    }

    @Test
    void URL_단축_테스트(){
        String code = service.shorten("https://www.naver.com");
        System.out.println("단축 코드: " + code);
        assertNotNull(code);
    }

    @Test
    void 같은_URL은_같은_코드_반환() {
        String code1 = service.shorten("https://www.naver.com");
        String code2 = service.shorten("https://www.naver.com");
        assertEquals(code1, code2);
    }

    @Test
    void 단축코드로_원본URL_조회() {
        String originalUrl = "https://www.naver.com";
        String code = service.shorten(originalUrl);
        String result = service.getOriginalUrl(code);
        System.out.println("코드: " + code + " → " + result);
        assertEquals(originalUrl, result);
    }

}
