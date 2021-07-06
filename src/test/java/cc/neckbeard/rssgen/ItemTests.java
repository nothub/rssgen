package cc.neckbeard.rssgen;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTests {

    @Test
    void none() {
        assertThrows(IllegalArgumentException.class, () ->
            RSS.Builder
                .getInstance()
                .title("TEST")
                .link(new URL("https://example.org/"))
                .description("TEST")
                .build()
                .addItem()
                .build());
    }

    @Test
    void title() {
        assertDoesNotThrow(() ->
            RSS.Builder
                .getInstance()
                .title("TEST")
                .link(new URL("https://example.org/"))
                .description("TEST")
                .build()
                .addItem()
                .title("TEST")
                .build());
    }

    @Test
    void description() {
        assertDoesNotThrow(() ->
            RSS.Builder
                .getInstance()
                .title("TEST")
                .link(new URL("https://example.org/"))
                .description("TEST")
                .build()
                .addItem()
                .description("TEST")
                .build());
    }

}
