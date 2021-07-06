package cc.neckbeard.rssgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ChannelTests {

    @Test
    void none() {
        assertThrows(IllegalArgumentException.class, () ->
            RSS.Builder
                .getInstance()
                .build());
    }

    @Test
    void title() {
        assertThrows(IllegalArgumentException.class, () ->
            RSS.Builder
                .getInstance()
                .title("TEST")
                .build());
    }

    @Test
    void link() {
        assertThrows(IllegalArgumentException.class, () ->
            RSS.Builder
                .getInstance()
                .link(new URL("https://example.org/"))
                .build());
    }

    @Test
    void description() {
        assertThrows(IllegalArgumentException.class, () ->
            RSS.Builder
                .getInstance()
                .description("TEST")
                .build());
    }

    @Test
    void valid() {
        Assertions.assertDoesNotThrow(() ->
            RSS.Builder
                .getInstance()
                .title("TEST")
                .link(new URL("https://example.org/"))
                .description("TEST")
                .build());
    }

}
