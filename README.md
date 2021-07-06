# rssgen

[![maven central](https://maven-badges.herokuapp.com/maven-central/cc.neckbeard/rssgen/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cc.neckbeard/rssgen) [![LGTM](https://img.shields.io/lgtm/grade/java/github/nothub/rssgen?label=code%20quality&logo=lgtm)](https://lgtm.com/projects/g/nothub/rssgen)

A generator for RSS 2.0 conform xml files.

---

Just about compliant with the RSS 2.0 specification.

HTML control tokens are escaped correctly, to get unescaped and displayed later on for Consumers.

Implemented as single class file with zero external dependencies.

---

This [example code](./src/test/java/cc/neckbeard/rssgen/Example.java):

```java
        RSS rss=RSS.Builder
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
        .source("Scripting News",new URL("http://static.userland.com/gems/backend/rssTwoExample2.xml"))
        .build();

        rss.addItem()
        .title("Sharp tools for emergencies and the --clowntown flag")
        .link(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
        .guid(new URL("https://rachelbythebay.com/w/2020/10/27/argv/"))
        .category("sysadmin war stories")
        .source("rachelbythebay : Writing",new URL("https://rachelbythebay.com/w/atom.xml"))
        .build();

        rss.writeFile(new File("rss.xml"),1);
```

Produces the following output:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
    <channel>
        <title>Example News Headlines</title>
        <link>https://news.example.org/</link>
        <description>Example news on the internet.</description>
        <language>en</language>
        <copyright>Copyright 2021, Owner of thoughts and words</copyright>
        <lastBuildDate>Tue, 06 Jul 2021 18:01:13 CEST</lastBuildDate>
        <ttl>1440</ttl>
        <generator>rssgen 1.0.0-SNAPSHOT</generator>
        <item>
            <title>Really early morning no-coffee notes</title>
            <description>&lt;p&gt;When someone accuses you of a &lt;a href="http://www.dictionary.com/search?q=deceit"&gt;deceit&lt;/a&gt;, there's a very good chance the accuser practices that form of deceit, and a reasonable chance that he or she is doing it as they point the finger.&lt;/p&gt;</description>
            <link>https://scriptingnews.userland.com/backissues/2002/09/29#reallyEarlyMorningNocoffeeNotes</link>
            <guid isPermaLink="true">https://scriptingnews.userland.com/backissues/2002/09/29#reallyEarlyMorningNocoffeeNotes</guid>
            <pubDate>Sun, 29 Sep 2002 11:13:10 GMT</pubDate>
            <source url="http://static.userland.com/gems/backend/rssTwoExample2.xml">Scripting News</source>
        </item>
        <item>
            <title>Sharp tools for emergencies and the --clowntown flag</title>
            <link>https://rachelbythebay.com/w/2020/10/27/argv/</link>
            <guid isPermaLink="true">https://rachelbythebay.com/w/2020/10/27/argv/</guid>
            <category>sysadmin war stories</category>
            <source url="https://rachelbythebay.com/w/atom.xml">rachelbythebay : Writing</source>
        </item>
    </channel>
</rss>
```

---

For more information, check the: [RSS 2.0 specification](https://validator.w3.org/feed/docs/rss2.html) ([mirror](https://www.rssboard.org/rss-specification), [mirror](https://cyber.harvard.edu/rss/rss.html))
