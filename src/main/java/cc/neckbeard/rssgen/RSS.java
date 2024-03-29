package cc.neckbeard.rssgen;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implements Really Simple Syndication (RSS) 2.0 <a href="https://validator.w3.org/feed/docs/rss2.html">specification</a> (<a href="https://www.rssboard.org/rss-specification">mirror</a>, <a href="https://cyber.harvard.edu/rss/rss.html">mirror</a>).
 * <p>
 * RSS date format is RFC822, implemented as {@link RSS.Date}.
 * RSS 2.0 does not implement content length restrictions. Nevertheless practically, content maximum length is determined by the runtimes maximum string length.
 * <p>
 * Rest in peace Aaron Swartz.
 */
public class RSS {

    /**
     * This set contains all valid language values defined in: <a href="http://backend.userland.com/discuss/msgReader$16">Allowable values for language in RSS</a>
     * <p>
     * Additionally, contains all 2-letter language codes defined in ISO639 (all language codes defined by the W3C are explicitly valid RSS language codes).
     */
    public static final Set<String> LANGUAGE_IDS = new HashSet<>();

    static {
        LANGUAGE_IDS.addAll(Arrays.asList(Locale.getISOLanguages()));
        LANGUAGE_IDS.addAll(new HashSet<>(Arrays.asList("af", "sq", "eu", "be", "bg", "ca", "zh-cn", "zh-tw", "hr", "cs", "da", "nl", "nl-be", "nl-nl", "en", "en-au", "en-bz", "en-ca", "en-ie", "en-jm", "en-nz", "en-ph", "en-za", "en-tt", "en-gb", "en-us", "en-zw", "et", "fo", "fi", "fr", "fr-be", "fr-ca", "fr-fr", "fr-lu", "fr-mc", "fr-ch", "gl", "gd", "de", "de-at", "de-de", "de-li", "de-lu", "de-ch", "el", "haw", "hu", "is", "in", "ga", "it", "it-it", "it-ch", "ja", "ko", "mk", "no", "pl", "pt", "pt-br", "pt-pt", "ro", "ro-mo", "ro-ro", "ru", "ru-mo", "ru-ru", "sr", "sk", "sl", "es", "es-ar", "es-bo", "es-cl", "es-co", "es-cr", "es-do", "es-ec", "es-sv", "es-gt", "es-hn", "es-mx", "es-ni", "es-pa", "es-py", "es-pe", "es-pr", "es-es", "es-uy", "es-ve", "sv", "sv-fi", "sv-se", "tr", "uk")));
    }

    private final Document doc;
    private final Element channel;

    private RSS(Document doc, Element channel) {
        this.doc = doc;
        this.channel = channel;
    }

    private static Element appendChild(String title, String content, Element parent, Document doc) {
        Element element = doc.createElement(title);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
        return element;
    }

    private static void appendAttribute(String title, String value, Element parent, Document doc) {
        Attr attribute = doc.createAttribute(title);
        attribute.setValue(value);
        parent.setAttributeNode(attribute);
    }

    private static Node getFirstNode(String title, Element parent) {
        final NodeList nodes = parent.getElementsByTagName(title);
        return nodes.getLength() > 0 ? nodes.item(0) : null;
    }

    private static boolean containsDuplicates(Object[] arr) {
        try {
            if (new HashSet<>(Arrays.asList(arr)).size() != arr.length) return true;
        } catch (IllegalArgumentException ignored) {
            return true;
        }
        return false;
    }

    /**
     * Returns a new Item builder instance.
     * <p>
     * The Item will automatically be attached to the feed, when {@link Item#build()} is invoked.
     *
     * @return Item builder
     */
    public Item addItem() {
        return new Item(doc, channel);
    }

    /**
     * Write rss file to disk.
     *
     * @param file target file
     */
    public void writeFile(File file) {
        writeFile(file, 0);
    }

    /**
     * Write rss file to disk.
     *
     * @param file   target file
     * @param indent level of indentation (*2)
     */
    public void writeFile(File file, int indent) {
        Transformer transformer;
        try {
            transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Transformer default configuration is invalid: " + e.getMessage());
        }
        if (indent > 0) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //noinspection HttpUrlsUsage
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
        }
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (TransformerException e) {
            throw new IllegalStateException("Error while transforming xml: " + e.getMessage());
        }
    }

    /**
     * RSS Builder
     */
    public static final class Builder {

        private final Document doc;
        private final Element channel;

        private boolean containsTitle;
        private boolean containsLink;
        private boolean containsDescription;

        private Element image;

        private Builder() {

            try {
                doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException("DocumentBuilder default configuration is invalid: " + e.getMessage());
            }
            doc.setXmlStandalone(true);

            Element rss = doc.createElement("rss");
            doc.appendChild(rss);

            Attr rssVersion = doc.createAttribute("version");
            rssVersion.setValue("2.0");
            rss.setAttributeNode(rssVersion);

            channel = doc.createElement("channel");
            rss.appendChild(channel);

        }

        /**
         * Returns a new {@link RSS} builder instance.
         *
         * @return RSS builder
         */
        public static Builder getInstance() {
            return new Builder();
        }

        /**
         * The name of the channel. It's how people refer to your service. If you have an HTML website that contains the same information as your RSS file, the title of your channel should be the same as the title of your website.
         *
         * @param value feed title
         * @return RSS builder
         */
        public Builder title(String value) {
            appendChild("title", value, channel, doc);
            this.containsTitle = true;
            return this;
        }

        /**
         * The URL to the HTML website corresponding to the channel.
         *
         * @param value website url
         * @return RSS builder
         */
        public Builder link(URL value) {
            appendChild("link", value.toString(), channel, doc);
            this.containsLink = true;
            return this;
        }

        /**
         * Phrase or sentence describing the channel.
         *
         * @param value scription
         * @return RSS builder
         */
        public Builder description(String value) {
            appendChild("description", value, channel, doc);
            this.containsDescription = true;
            return this;
        }

        /**
         * The language the channel is written in.
         * <p>
         * This allows aggregators to group all Italian language sites, for example, on a single page.
         * A list of allowable values for this element, as provided by Netscape, is <a href="http://backend.userland.com/discuss/msgReader$16">here</a>.
         * <p>
         * You may also use <a href="http://www.w3.org/TR/REC-html40/struct/dirlang.html#langcodes">values defined</a> by the W3C.
         *
         * @param value language id
         * @return RSS builder
         */
        public Builder language(String value) {
            if (!LANGUAGE_IDS.contains(value))
                throw new IllegalArgumentException("Not a valid language id. Allowed values: " + String.join(", ", LANGUAGE_IDS));
            appendChild("language", value, channel, doc);
            return this;
        }

        /**
         * Copyright notice for content in the channel.
         *
         * @param value copyright notice
         * @return RSS builder
         */
        public Builder copyright(String value) {
            appendChild("copyright", value, channel, doc);
            return this;
        }

        /**
         * Email address for person responsible for editorial content.
         *
         * @param value email address
         * @return RSS builder
         */
        public Builder managingEditor(String value) {
            appendChild("managingEditor", value, channel, doc);
            return this;
        }

        /**
         * Email address for person responsible for technical issues relating to channel.
         *
         * @param value email address
         * @return RSS builder
         */
        public Builder webMaster(String value) {
            appendChild("webMaster", value, channel, doc);
            return this;
        }

        /**
         * The publication date for the content in the channel.
         * <p>
         * For example, the New York Times publishes on a daily basis,
         * the publication date flips once every 24 hours.
         * That's when the pubDate of the channel changes.
         * <p>
         * All date-times in RSS conform to the Date and Time Specification of <a href="http://asg.web.cmu.edu/rfc/rfc822.html">RFC 822</a>,
         * with the exception that the year may be expressed with two characters or four characters (four preferred).
         *
         * @param value rfc822 date
         * @return RSS builder
         */
        public Builder pubDate(Date value) {
            appendChild("pubDate", value.rfc822, channel, doc);
            return this;
        }

        /**
         * The last time the content of the channel changed.
         *
         * @param value rfc822 date
         * @return RSS builder
         */
        public Builder lastBuildDate(Date value) {
            appendChild("lastBuildDate", value.rfc822, channel, doc);
            return this;
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the item category.
         *
         * @param value  category
         * @param domain category domain
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, URL)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, URL)
         */
        public Builder category(String value, String domain) {
            Element element = appendChild("category", value, channel, doc);
            if (domain != null) {
                appendAttribute("domain", domain, element, doc);
            }
            return this;
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the item category.
         *
         * @param value  category url
         * @param domain category domain url
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, String)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, URL)
         */
        public Builder category(String value, URL domain) {
            return category(value, domain.toString());
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the item category.
         *
         * @param value category
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, String)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, URL)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, URL)
         */
        public Builder category(String value) {
            return category(value, (String) null);
        }

        /**
         * A URL that points to the documentation for the format used in the RSS file.
         * It's probably a pointer to this page.
         * It's for people who might stumble across an RSS file on a Web server 25 years from now and wonder what it is.
         *
         * @param value url
         * @return RSS builder
         */
        public Builder docs(URL value) {
            appendChild("docs", value.toString(), channel, doc);
            return this;
        }

        /**
         * Allows processes to register with a cloud to be notified of updates to the channel,
         * implementing a lightweight publish-subscribe protocol for RSS feeds.
         * <p>
         * Supported protocols:
         * <ul>
         * <li>HTTP-POST</li>
         * <li>XML-RPC</li>
         * <li>SOAP 1.1</li>
         * </ul>
         * <p>
         *
         * @param domain            cloud url
         * @param port              cloud port
         * @param path              cloud path
         * @param registerProcedure cloud registration procedure
         * @param protocol          cloud protocol
         * @return RSS builder
         * @see <a href="https://cyber.harvard.edu/rss/soapMeetsRss.html">SOAP Meets RSS</a>
         */
        public Builder cloud(URI domain, Integer port, String path, String registerProcedure, String protocol) {
            Element element = doc.createElement("cloud");
            appendAttribute("domain", domain.toString(), element, doc);
            appendAttribute("port", String.valueOf(port), element, doc);
            appendAttribute("path", path, element, doc);
            appendAttribute("registerProcedure", registerProcedure, element, doc);
            appendAttribute("protocol", protocol, element, doc);
            channel.appendChild(element);
            return this;
        }

        /**
         * Minutes until consumer cache invalidation.
         *
         * @param value minutes
         * @return RSS builder
         */
        public Builder ttl(Integer value) {
            appendChild("ttl", String.valueOf(value), channel, doc);
            return this;
        }

        /**
         * GIF, JPEG or PNG image that can be displayed with the channel.
         *
         * @param url   image url
         * @param title image title
         * @param link  target link
         * @return RSS builder
         */
        public Builder image(URL url, String title, URL link) {
            if (image == null) image = doc.createElement("image");
            appendAttribute("url", url.toString(), image, doc);
            appendAttribute("title", title, image, doc);
            appendAttribute("link", link.toString(), image, doc);
            return this;
        }

        /**
         * Optional attribute for Image Element.
         * <p>
         * Maximum image width is 144.
         * <p>
         * Image must be defined prio to this operation.
         *
         * @param value image width
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#image(URL, String, URL)
         */
        public Builder imageWidth(Integer value) throws IllegalArgumentException {
            if (value > 144) throw new IllegalArgumentException("Maximum image width is 144.");
            if (image == null) throw new IllegalArgumentException("Image must be defined prio to this operation.");
            appendChild("width", String.valueOf(value), image, doc);
            return this;
        }

        /**
         * Optional attribute for Image Element.
         * <p>
         * Maximum image height is 400.
         * <p>
         * Image must be defined prio to this operation.
         *
         * @param value image height
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#image(URL, String, URL)
         */
        public Builder imageHeight(Integer value) throws IllegalArgumentException {
            if (value > 400) throw new IllegalArgumentException("Maximum image height is 400.");
            if (image == null) throw new IllegalArgumentException("Image must be defined prio to this operation.");
            appendChild("height", String.valueOf(value), image, doc);
            return this;
        }

        /**
         * Optional attribute for Image Element.
         * <p>
         * Image must be defined prio to this operation.
         *
         * @param value image description
         * @return RSS builder
         * @see cc.neckbeard.rssgen.RSS.Builder#image(URL, String, URL)
         */
        public Builder imageDescription(String value) throws IllegalArgumentException {
            if (image == null) throw new IllegalArgumentException("Image must be defined prio to this operation.");
            appendChild("description", value, image, doc);
            return this;
        }

        /**
         * @param title       text titel
         * @param description text description
         * @param name        text name
         * @param link        text link
         * @return RSS builder
         */
        public Builder textInput(String title, String description, String name, URL link) {
            Element element = doc.createElement("textInput");
            appendAttribute("title", title, element, doc);
            appendAttribute("description", description, element, doc);
            appendAttribute("name", name, element, doc);
            appendAttribute("link", link.toString(), element, doc);
            channel.appendChild(element);
            return this;
        }

        /**
         * Up to 24 number values between 0 and 23, representing a time in GMT.
         * <p>
         * Aggregators, if they support the feature, may not read the channel during listed hours.
         * <p>
         * The hour beginning at midnight is hour zero.
         *
         * @param values up to 24 hours
         * @return RSS builder
         */
        public Builder skipHours(Integer... values) {
            if (values.length > 24) throw new IllegalArgumentException("Maximum 24 hours allowed.");
            if (containsDuplicates(values)) throw new IllegalArgumentException("Values must be unique.");
            Element element = doc.createElement("skipHours");
            Arrays
                .stream(values)
                .filter(hour -> {
                    if (IntStream.range(0, 24).anyMatch(i -> i == hour)) {
                        return true;
                    } else {
                        throw new IllegalArgumentException("Invalid hour value. Allowed values: " + IntStream.range(0, 24).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
                    }
                })
                .map(String::valueOf)
                .forEach(hour -> appendChild("hour", hour, element, doc));
            channel.appendChild(element);
            return this;
        }

        /**
         * Up to seven String values:
         * <ul>
         * <li>Monday</li>
         * <li>Tuesday</li>
         * <li>Wednesday</li>
         * <li>Thursday</li>
         * <li>Friday</li>
         * <li>Saturday</li>
         * <li>Sunday</li>
         * </ul>
         * <p>
         * Aggregators, if they support the feature, may not read the channel during listed days.
         *
         * @param values up to 7 days
         * @return RSS builder
         */
        public Builder skipDays(String... values) {
            if (values.length > 7) throw new IllegalArgumentException("Maximum 7 days allowed.");
            if (containsDuplicates(values)) throw new IllegalArgumentException("Values must be unique.");
            Element element = doc.createElement("skipDays");
            Arrays
                .stream(values)
                .filter(day -> {
                    if (Arrays.asList(DateFormatSymbols.getInstance(Locale.US).getWeekdays()).contains(day)) {
                        return true;
                    } else {
                        throw new IllegalArgumentException("Invalid day value. Allowed values: "
                            + Arrays.stream(DateFormatSymbols.getInstance(Locale.US).getWeekdays())
                            .filter(s -> !s.isEmpty()) // cause there is actually an empty string in that array lol
                            .collect(Collectors.joining(", ")));
                    }
                })
                .forEach(day -> appendChild("day", day, element, doc));
            channel.appendChild(element);
            return this;
        }

        /**
         * Creates and validates the RSS object.
         *
         * @return RSS object
         */
        public RSS build() throws IllegalArgumentException {
            RSS rss = new RSS(doc, channel);
            if (!this.containsTitle || !this.containsLink || !this.containsDescription) {
                throw new IllegalArgumentException("All of title, link and description are required channel elements.");
            }
            if (image != null) {
                // default width
                Node width = getFirstNode("width", image);
                if (width == null) imageWidth(88);
                // default height
                Node height = getFirstNode("height", image);
                if (height == null) imageHeight(31);
                channel.appendChild(image);
            }
            appendChild("generator", Generated.NAME + " " + Generated.VERSION, channel, doc);
            return rss;
        }

    }

    /**
     * An item may represent a "story" -- much like a story in a newspaper or magazine;
     * if so its description is a synopsis of the story, and the link points to the full story.
     * <p>
     * An item may also be complete in itself, if so, the description
     * contains the text (entity-encoded HTML is allowed), and the link and title may be omitted.
     * <p>
     * All elements of an item are optional, however at least one of title or description must be present.
     * <p>
     * The feed may contain any number of items.
     */
    public static class Item {

        private final Document doc;
        private final Element item;
        private final Element channel;

        private boolean containsTitle;
        private boolean containsDescription;

        private Item(Document doc, Element channel) {
            this.doc = doc;
            this.channel = channel;
            this.item = doc.createElement("item");
        }

        /**
         * The title of the item.
         * At least one of title or description must be present.
         *
         * @param value title
         * @return Item builder
         */
        public Item title(String value) {
            appendChild("title", value, item, doc);
            this.containsTitle = true;
            return this;
        }

        /**
         * The URL of the item.
         *
         * @param value url
         * @return Item builder
         */
        public Item link(URL value) {
            appendChild("link", value.toString(), item, doc);
            return this;
        }

        /**
         * The item synopsis.
         * At least one of title or description must be present.
         *
         * @param value synopsis
         * @return Item builder
         */
        public Item description(String value) {
            appendChild("description", value, item, doc);
            this.containsDescription = true;
            return this;
        }

        /**
         * Email address of the author of the item.
         *
         * @param value email address
         * @return Item builder
         */
        public Item author(String value) {
            appendChild("author", value, item, doc);
            return this;
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the channel category.
         *
         * @param value  category
         * @param domain category domain
         * @return Item builder
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, URL)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, URL)
         */
        public Item category(String value, String domain) {
            Element element = appendChild("category", value, item, doc);
            if (domain != null) {
                appendAttribute("domain", domain, element, doc);
            }
            return this;
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the channel category.
         *
         * @param value  category
         * @param domain category domain url
         * @return Item builder
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, String)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, URL)
         */
        public Item category(String value, URL domain) {
            return category(value, domain.toString());
        }

        /**
         * Specify one or more categories that the channel belongs to. Follows the same rules as the channel category.
         *
         * @param value category
         * @return Item builder
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, String)
         * @see cc.neckbeard.rssgen.RSS.Item#category(String, URL)
         * @see cc.neckbeard.rssgen.RSS.Builder#category(String, URL)
         */
        public Item category(String value) {
            return category(value, (String) null);
        }

        /**
         * URL of a page for comments relating to the item.
         *
         * @param value url
         * @return Item builder
         */
        public Item comments(URL value) {
            appendChild("comments", value.toString(), item, doc);
            return this;
        }

        /**
         * Describes a media object that is attached to the item.
         *
         * @param url    url to media file
         * @param length content length
         * @param type   MIME type
         * @return Item builder
         */
        public Item enclosure(URL url, Integer length, String type) {
            Element element = doc.createElement("enclosure");
            appendAttribute("url", url.toString(), element, doc);
            appendAttribute("length", String.valueOf(length), element, doc);
            appendAttribute("type", type, element, doc);
            item.appendChild(element);
            return this;
        }

        /**
         * A URL that uniquely identifies the item.
         *
         * @param value url
         * @return Item builder
         */
        public Item guid(URL value) {
            return guid(value.toString(), true);
        }

        /**
         * A string that uniquely identifies the item.
         *
         * @param value       unique identifier
         * @param isPermaLink if true, value is an url
         * @return Item builder
         */
        public Item guid(String value, boolean isPermaLink) {
            Element element = appendChild("guid", value, item, doc);
            if (isPermaLink) {
                appendAttribute("isPermaLink", String.valueOf(true), element, doc);
            }
            return this;
        }

        /**
         * Indicates when the item was published.
         *
         * @param value rfc822 date
         * @return Item builder
         */
        public Item pubDate(Date value) {
            appendChild("pubDate", value.rfc822, item, doc);
            return this;
        }

        /**
         * The RSS channel that the item came from.
         *
         * @param value name of source
         * @param url   url to source rss feed
         * @return Item builder
         */
        public Item source(String value, URL url) {
            Element element = appendChild("source", value, item, doc);
            appendAttribute("url", url.toString(), element, doc);
            return this;
        }

        /**
         * Creates validates and appends the Item to the RSS feed.
         *
         * @throws IllegalArgumentException on missing title or description
         */
        public void build() throws IllegalArgumentException {
            if (!this.containsTitle && !this.containsDescription) {
                throw new IllegalArgumentException("One of title or description are required item elements");
            }
            channel.appendChild(item);
        }

    }

    /**
     * Provides a RFC822 compliant date.
     */
    public static class Date {

        private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

        /**
         * Date in RFC822 format.
         */
        public final String rfc822;

        private Date(String raw) {
            rfc822 = raw;
        }

        private Date(java.util.Date date) {
            this(formatter.format(date));
        }

        private Date(Instant instant) {
            this(java.util.Date.from(instant));
        }

        /**
         * Stores {@link java.lang.String} as RFC822 date.
         *
         * @param raw valid rfc822 formatted date
         * @return rfc822 date
         */
        public static Date of(String raw) {
            return new Date(raw);
        }

        /**
         * Converts {@link java.util.Date} to RFC822 date.
         *
         * @param date timestamp
         * @return rfc822 date
         */
        public static Date of(java.util.Date date) {
            return new Date(date);
        }

        /**
         * Converts {@link java.time.Instant} to RFC822 date.
         *
         * @param instant timestamp
         * @return rfc822 date
         */
        public static Date of(Instant instant) {
            return new Date(instant);
        }

        /**
         * Stores a RFC822 date of the current time.
         *
         * @return rfc822 date
         */
        public static Date now() {
            return new Date(new java.util.Date());
        }

    }

}
