package util;

import com.labs.urlshortener.util.Base62;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Base62Test {


    @Test
    void encode_테스트() {
        String result = Base62.encode(12345);
        System.out.println("12345 → " + result);
        assertNotNull(result);
    }

    @Test
    void decode_테스트() {
        String code = Base62.encode(12345);
        long result = Base62.decode(code);
        System.out.println(code + " → " + result);
        assertEquals(12345, result);
    }

    @Test
    void encode_decode_왕복_테스트() {
        long original = 99999L;
        String encoded = Base62.encode(original);
        long decoded = Base62.decode(encoded);
        System.out.println(original + " → " + encoded + " → " + decoded);
        assertEquals(original, decoded);
    }
}
