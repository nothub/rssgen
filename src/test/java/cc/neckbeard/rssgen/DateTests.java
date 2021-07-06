package cc.neckbeard.rssgen;

import org.junit.jupiter.api.Test;

import java.time.Instant;

class DateTests {

    // TODO: Somehow set timezone for automatic string comparison testing in ci

    @Test
    void date() {
        System.out.println(RSS.Date.of(new java.util.Date()).rfc822);
    }

    @Test
    void instant() {
        System.out.println(RSS.Date.of(Instant.now()).rfc822);
    }

    @Test
    void now() {
        System.out.println(RSS.Date.now().rfc822);
    }

}
