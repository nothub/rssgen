package cc.neckbeard.rssgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

class DateTests {

    public static final int TEST_EPOCH = 123456789;
    public static final String TEST_RESULT = "Fri, 30 Nov 1973 07:03:09 ACST";

    @BeforeEach
    void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Darwin"));
    }

    @Test
    void date() {
        Assertions.assertEquals(TEST_RESULT, RSS.Date.of(Date.from(Instant.ofEpochSecond(TEST_EPOCH))).rfc822);
    }

    @Test
    void instant() {
        Assertions.assertEquals(TEST_RESULT, RSS.Date.of(Instant.ofEpochSecond(TEST_EPOCH)).rfc822);
    }

}
