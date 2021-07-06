package cc.neckbeard.rssgen;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Example {

    public static void main(String[] args) throws MalformedURLException {

        RSS rss = RSS.Builder
            .getInstance()
            .title("Example News Headlines")
            .link(new URL("https://news.example.org/"))
            .description("Example news on the internet.")
            .language("en")
            .copyright("Copyright 2021, Owner of thoughts and words")
            .lastBuildDate(RSS.Date.now())
            .ttl(1440)
            .build();

        rss.addItem()
            .title("Really early morning no-coffee notes")
            .description("<p>When someone accuses you of a <a href=\"http://www.dictionary.com/search?q=deceit\">deceit</a>, there's a very good chance the accuser practices that form of deceit, and a reasonable chance that he or she is doing it as they point the finger.</p>")
            .link(new URL("https://scriptingnews.userland.com/backissues/2002/09/29#reallyEarlyMorningNocoffeeNotes"))
            .guid(new URL("https://scriptingnews.userland.com/backissues/2002/09/29#reallyEarlyMorningNocoffeeNotes"))
            .pubDate(RSS.Date.of("Sun, 29 Sep 2002 11:13:10 GMT"))
            .source("Scripting News", new URL("http://static.userland.com/gems/backend/rssTwoExample2.xml"))
            .build();

        rss.addItem()
            .title("Sharp tools for emergencies and the --clowntown flag")
            .link(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
            .guid(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
            .category("sysadmin war stories")
            .source("rachelbythebay : Writing", new URL("https://rachelbythebay.com/w/atom.xml"))
            .build();

        rss.writeFile(new File("rss.xml"), 1);

    }

}
