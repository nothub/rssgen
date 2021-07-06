package cc.neckbeard.rssgen;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;

public class AllElements {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {

        RSS rss = RSS.Builder
            .getInstance()
            .title("Example News Headlines")
            .link(new URL("https://news.example.org/"))
            .description("Example news on the internet.")
            .language("en")
            .copyright("Copyright 2021, Owner of thoughts and words")
            .managingEditor("null@news.example.org")
            .webMaster("null@news.example.org")
            .pubDate(RSS.Date.of(Instant.ofEpochSecond(1123581321)))
            .lastBuildDate(RSS.Date.of(Instant.now()))
            .category("Blogs")
            .category("Example News", new URL("https://news.example.org/"))
            .docs(new URL("https://cyber.harvard.edu/rss/rss.html"))
            .cloud(new URI("cloud.news.example.org"), 9000, "/RPC2", "xmlStorageSystem.rssPleaseNotify", "xml-rpc")
            .ttl(1440)
            .image(new URL("https://via.placeholder.com/100.png"), "Placeholder", new URL("https://news.example.org/"))
            .imageWidth(100)
            .imageHeight(100)
            .imageDescription("Blank placeholder image")
            .textInput("Reader Feedback", "Send your complaints here", "text", new URL("https://example.org/feedback.pl"))
            .skipHours(1, 2, 3, 4)
            .skipDays("Monday", "Sunday")
            .build();

        rss.addItem()
            .title("Really early morning no-coffee notes")
            .link(new URL("http://scripting.com/2002/09/29.html#reallyEarlyMorningNocoffeeNotes"))
            .description("<p>One of the lessons I've learned in 47.4 years: When someone accuses you of a <a href=\"http://www.dictionary.com/search?q=deceit\">deceit</a>, there's a very good chance the accuser practices that form of deceit, and a reasonable chance that he or she is doing it as they point the finger. </p> <p><a href=\"http://www.docuverse.com/blog/donpark/2002/09/28.html#a66\">Don Park</a>: \"He poured a barrel full of pig urine all over the Korean Congress because he was pissed off about all the dirty politics going on.\"</p> <p><a href=\"http://davenet.userland.com/1995/01/04/demoingsoftwareforfunprofi\">1/4/95</a>: \"By the way, the person with the big problem is probably a competitor.\"</p> <p>I've had a fair amount of experience in the last few years with what you might call standards work. XML-RPC, SOAP, RSS, OPML. Each has been different from the others. In all this work, the most positive experience was XML-RPC, and not just because of the technical excellence of the people involved. In the end, what matters more to me is <a href=\"http://www.dictionary.com/search?q=collegiality\">collegiality</a>. Working together, person to person, for the sheer pleasure of it, is even more satisfying than a good technical result. Now, getting both is the best, and while XML-RPC is not perfect, it's pretty good. I also believe that if you have collegiality, technical excellence follows as a natural outcome.</p> <p>One more bit of philosophy. At my checkup earlier this week, one of the things my cardiologist asked was if I was experiencing any kind of intellectual dysfunction. In other words, did I lose any of my sharpness as a result of the surgery in June. I told him yes I had and thanked him for asking. In an amazing bit of synchronicity, the next day John Robb <a href=\"http://jrobb.userland.com/2002/09/25.html#a2598\">located</a> an article in New Scientist that said that scientists had found a way to prevent this from happening. I hadn't talked with John about my experience or the question the doctor asked. Yesterday I was telling the story to my friend Dave Jacobs. He said it's not a problem because I always had excess capacity in that area. Exactly right Big Dave and thanks for the vote of confidence.</p>")
            .author("scriptingnews1mail@hidden")
            .category("1765", "Syndic8")
            .category("backissues", "http://scriptingnews.userland.com/backissues")
            .category("backissues", new URL("http://scripting.com/backissues"))
            .guid("http://scripting.com/2002/09/29.html#reallyEarlyMorningNocoffeeNotes", true)
            .enclosure(new URL("https://cloud.news.example.org/podcast.mp3"), 1069871, "audio/mpeg")
            .comments(new URL("https://cloud.news.example.org/cgi/comments.cgi?id=42"))
            .pubDate(RSS.Date.of("Sun, 29 Sep 2002 11:13:10 GMT"))
            .source("Scripting News", new URL("https://static.userland.com/gems/backend/rssTwoExample2.xml"))
            .build();

        rss.addItem()
            .title("Sharp tools for emergencies and the --clowntown flag")
            .link(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
            .category("sysadmin war stories")
            .guid(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
            .source("rachelbythebay : Writing", new URL("https://rachelbythebay.com/w/atom.xml"))
            .build();

        rss.writeFile(new File("rss.xml"));

    }

}
