package not.hub;

import cc.neckbeard.rssgen.RSS;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {

        RSS rss = RSS.Builder
            .getInstance()
            .title("TEST")
            .link(new URL("https://example.org/"))
            .description("TEST")
            .language("en")
            .copyright("MIT")
            .managingEditor("null@example.org")
            .webMaster("null@example.org")
            .pubDate(RSS.Date.of(Instant.now()))
            .lastBuildDate(RSS.Date.of(Instant.now()))
            .category("TEST")
            .category("TEST", new URL("https://example.org/"))
            .docs(new URL("https://example.org/"))
            .cloudDomain(new URI("example.org"))
            .cloudPort(9000)
            .cloudPath("TEST")
            .cloudRegisterProcedure("TEST")
            .cloudProtocol("TEST")
            .ttl(9000)
            .imageUrl(new URL("https://example.org/"))
            .imageTitle("TEST")
            .imageLink(new URL("https://example.org/"))
            .imageWidth(144)
            .imageHeight(400)
            .imageDescription("TEST")
            .textInputTitle("TEST")
            .textInputDescription("TEST")
            .textInputName("TEST")
            .textInputLink(new URL("https://example.org/"))
            .skipHours(1, 2, 3, 4)
            .skipDays("Monday", "Sunday")
            .build();

        rss.addItem()
            .title("TEST A")
            .link(new URL("https://example.org/"))
            .description("TEST")
            .author("null@example.org")
            .category("TEST")
            .docs(new URL("https://example.org/"))
            .comments(new URL("https://example.org/"))
            .enclosure(new URL("https://example.org/media.mp3"), 9001, "audio/mpeg")
            .guid(new URL("https://example.org/"))
            .pubDate(RSS.Date.now())
            .source("TEST", new URL("https://example.org/rss.xml"))
            .build();

        rss.addItem()
            .title("TEST B")
            .link(new URL("https://example.org/"))
            .description("TEST")
            .author("null@example.org")
            .category("TEST", new URL("https://example.org/"))
            .docs(new URL("https://example.org/"))
            .comments(new URL("https://example.org/"))
            .enclosure(new URL("https://example.org/media.mp3"), 9001, "audio/mpeg")
            .guid("TEST", false)
            .pubDate(RSS.Date.of(new Date()))
            .source("TEST", new URL("https://example.org/rss.xml"))
            .build();

        rss.writeFile(new File("rss.xml"));

    }

}
