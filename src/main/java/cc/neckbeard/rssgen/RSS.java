package cc.neckbeard.rssgen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;

/**
 * Implements Really Simple Syndication (RSS) 2.0 <a href="https://validator.w3.org/feed/docs/rss2.html">Specification</a>.
 * <p>
 * - RSS date format is rfc822, implemented as {@link RSS.Date}.
 * <p>
 * - RSS 2.0 does not implement content length restrictions. Nevertheless practically, content maximum length is determined by the runtimes maximum string length.
 */
public class RSS {

    private final Document doc;
    private final Element channel;

    private RSS(Document doc, Element channel) {
        this.doc = doc;
        this.channel = channel;
    }

    private static Element appendChild(String title, String content, Element parent, Document doc) {
        var element = doc.createElement(title);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
        return element;
    }

    private static void appendAttribute(String title, String value, Element parent, Document doc) {
        var attribute = doc.createAttribute(title);
        attribute.setValue(value);
        parent.setAttributeNode(attribute);
    }

    private static Node getFirstNode(String title, Element parent) {
        final var nodes = parent.getElementsByTagName(title);
        return nodes.getLength() > 0 ? nodes.item(0) : null;
    }

    public Item addItem() {
        return new Item(doc, channel);
    }

    public void writeFile(File file) {
        Transformer transformer;
        try {
            transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Transformer default configuration is invalid: " + e.getMessage());
        }
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (TransformerException e) {
            throw new IllegalStateException("Error while transforming xml: " + e.getMessage());
        }
    }

    public static final class Builder {

        private final Document doc;
        private final Element channel;

        private boolean containsTitle;
        private boolean containsLink;
        private boolean containsDescription;

        private Element cloud;
        private Element image;
        private Element textInput;

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

            var rss = doc.createElement("rss");
            doc.appendChild(rss);

            var rssVersion = doc.createAttribute("version");
            rssVersion.setValue("2.0");
            rss.setAttributeNode(rssVersion);

            channel = doc.createElement("channel");
            rss.appendChild(channel);

        }

        public static Builder getInstance() {
            return new Builder();
        }

        /**
         * The name of the channel. It's how people refer to your service. If you have an HTML website that contains the same information as your RSS file, the title of your channel should be the same as the title of your website.
         */
        public Builder title(String value) {
            appendChild("title", value, channel, doc);
            this.containsTitle = true;
            return this;
        }

        /**
         * The URL to the HTML website corresponding to the channel.
         */
        public Builder link(URL value) {
            appendChild("link", value.toString(), channel, doc);
            this.containsLink = true;
            return this;
        }

        /**
         * Phrase or sentence describing the channel.
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
         * A list of allowable values for this element, as provided by Netscape, is <a href="http://backend.userland.com/stories/storyReader$16">here</a>.
         * <p>
         * You may also use <a href="http://www.w3.org/TR/REC-html40/struct/dirlang.html#langcodes">values defined</a> by the W3C.
         */
        public Builder language(String value) {
            appendChild("language", value, channel, doc);
            return this;
        }

        /**
         * Copyright notice for content in the channel.
         */
        public Builder copyright(String value) {
            appendChild("copyright", value, channel, doc);
            return this;
        }

        /**
         * Email address for person responsible for editorial content.
         */
        public Builder managingEditor(String value) {
            appendChild("managingEditor", value, channel, doc);
            return this;
        }

        /**
         * Email address for person responsible for technical issues relating to channel.
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
         */
        public Builder pubDate(Date value) {
            appendChild("pubDate", value.rfc822, channel, doc);
            return this;
        }

        /**
         * The last time the content of the channel changed.
         */
        public Builder lastBuildDate(Date value) {
            appendChild("lastBuildDate", value.rfc822, channel, doc);
            return this;
        }

        public Builder category(String value) {
            return category(value, null);
        }

        public Builder category(String value, URL url) {
            var element = appendChild("category", value, channel, doc);
            if (url != null) {
                appendAttribute("url", url.toString(), element, doc);
            }
            return this;
        }

        public Builder docs(URL value) {
            appendChild("docs", value.toString(), channel, doc);
            return this;
        }

        public Builder cloudDomain(URI value) {
            if (cloud == null) cloud = doc.createElement("cloud");
            appendAttribute("domain", value.toString(), cloud, doc);
            return this;
        }

        public Builder cloudPort(Integer value) {
            if (cloud == null) cloud = doc.createElement("cloud");
            appendAttribute("port", String.valueOf(value), cloud, doc);
            return this;
        }

        public Builder cloudPath(String value) {
            if (cloud == null) cloud = doc.createElement("cloud");
            appendAttribute("path", value, cloud, doc);
            return this;
        }

        public Builder cloudRegisterProcedure(String value) {
            if (cloud == null) cloud = doc.createElement("cloud");
            appendAttribute("registerProcedure", value, cloud, doc);
            return this;
        }

        public Builder cloudProtocol(String value) {
            if (cloud == null) cloud = doc.createElement("cloud");
            appendAttribute("protocol", value, cloud, doc);
            return this;
        }

        public Builder ttl(Integer value) {
            appendChild("ttl", String.valueOf(value), channel, doc);
            return this;
        }

        public Builder imageUrl(URL value) {
            if (image == null) image = doc.createElement("image");
            appendChild("url", value.toString(), image, doc);
            return this;
        }

        public Builder imageTitle(String value) {
            if (image == null) image = doc.createElement("image");
            appendChild("title", value, image, doc);
            return this;
        }

        public Builder imageLink(URL value) {
            if (image == null) image = doc.createElement("image");
            appendChild("link", value.toString(), image, doc);
            return this;
        }

        public Builder imageWidth(Integer value) {
            if (value > 144) throw new IllegalArgumentException("max image width is 144");
            if (image == null) image = doc.createElement("image");
            appendChild("width", String.valueOf(value), image, doc);
            return this;
        }

        /**
         * max height is 400
         */
        public Builder imageHeight(Integer value) {
            if (value > 400) throw new IllegalArgumentException("max image height is 400");
            if (image == null) image = doc.createElement("image");
            appendChild("height", String.valueOf(value), image, doc);
            return this;
        }

        /**
         * max width is 144
         */
        public Builder imageDescription(String value) {
            if (image == null) image = doc.createElement("image");
            appendChild("description", value, image, doc);
            return this;
        }

        public Builder textInputTitle(String value) {
            if (textInput == null) textInput = doc.createElement("textInput");
            appendChild("title", value, textInput, doc);
            return this;
        }

        public Builder textInputDescription(String value) {
            if (textInput == null) textInput = doc.createElement("textInput");
            appendChild("description", value, textInput, doc);
            return this;
        }

        public Builder textInputName(String value) {
            if (textInput == null) textInput = doc.createElement("textInput");
            appendChild("name", value, textInput, doc);
            return this;
        }

        public Builder textInputLink(URL value) {
            if (textInput == null) textInput = doc.createElement("textInput");
            appendChild("link", value.toString(), textInput, doc);
            return this;
        }

        /**
         * Up to 24 number values between 0 and 23, representing a time in GMT.
         * <p>
         * Aggregators, if they support the feature, may not read the channel during listed hours.
         * <p>
         * The hour beginning at midnight is hour zero.
         */
        public Builder skipHours(Integer... values) {
            var element = doc.createElement("skipHours");
            Arrays
                .stream(values)
                .map(String::valueOf)
                .forEach(hour -> appendChild("hour", hour, element, doc));
            channel.appendChild(element);
            return this;
        }

        /**
         * Up to seven String values:
         * <p>
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
         */
        public Builder skipDays(String... values) {
            var element = doc.createElement("skipDays");
            Arrays
                .stream(values)
                .forEach(day -> appendChild("day", day, element, doc));
            channel.appendChild(element);
            return this;
        }

        public RSS build() throws IllegalArgumentException {
            RSS rss = new RSS(doc, channel);
            if (!this.containsTitle || !this.containsLink || !this.containsDescription) {
                throw new IllegalArgumentException("title, link and description are required channel elements");
            }
            appendChild("generator", Globals.NAME + " " + Globals.VERSION, channel, doc);
            if (cloud != null) channel.appendChild(cloud);
            if (image != null) {
                // default width
                Node width = getFirstNode("width", image);
                if (width == null) {
                    imageWidth(88);
                }
                // default height
                Node height = getFirstNode("height", image);
                if (height == null) {
                    imageHeight(31);
                }
                channel.appendChild(image);
            }
            if (textInput != null) channel.appendChild(textInput);
            return rss;
        }

    }

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

        public Item title(String value) {
            appendChild("title", value, item, doc);
            this.containsTitle = true;
            return this;
        }

        public Item link(URL value) {
            appendChild("link", value.toString(), item, doc);
            return this;
        }

        public Item description(String value) {
            appendChild("description", value, item, doc);
            this.containsDescription = true;
            return this;
        }

        public Item author(String value) {
            appendChild("author", value, item, doc);
            return this;
        }

        public Item category(String value) {
            return category(value, null);
        }

        public Item category(String value, URL url) {
            var element = appendChild("category", value, item, doc);
            if (url != null) {
                appendAttribute("url", url.toString(), element, doc);
            }
            return this;
        }

        public Item docs(URL value) {
            appendChild("docs", value.toString(), item, doc);
            return this;
        }

        public Item comments(URL value) {
            appendChild("comments", value.toString(), item, doc);
            return this;
        }

        public Item enclosure(URL url, Integer length, String type) {
            var element = doc.createElement("enclosure");
            appendAttribute("url", url.toString(), element, doc);
            appendAttribute("length", String.valueOf(length), element, doc);
            appendAttribute("type", type, element, doc);
            item.appendChild(element);
            return this;

        }

        public Item guid(URL value) {
            return guid(value.toString(), true);
        }

        public Item guid(String value, boolean isPermaLink) {
            var element = appendChild("guid", value, item, doc);
            if (isPermaLink) {
                appendAttribute("isPermaLink", String.valueOf(true), element, doc);
            }
            return this;
        }

        public Item pubDate(Date value) {
            appendChild("pubDate", value.rfc822, item, doc);
            return this;
        }

        public Item source(String value, URL url) {
            var element = appendChild("source", value, item, doc);
            appendAttribute("url", url.toString(), element, doc);
            return this;
        }

        public void build() {
            if (!this.containsTitle && !this.containsDescription) {
                throw new IllegalArgumentException("title or description are required item elements");
            }
            channel.appendChild(item);
        }

    }

    public static class Date {

        private static final SimpleDateFormat RFC_822_DATE_TIME = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        public final String rfc822;

        public Date(String date) {
            rfc822 = date;
        }

        private Date(java.util.Date date) {
            rfc822 = RFC_822_DATE_TIME.format(date);
        }

        private Date(Instant instant) {
            this(java.util.Date.from(instant));
        }

        public static Date of(java.util.Date date) {
            return new Date(date);
        }

        public static Date of(Instant instant) {
            return new Date(instant);
        }

        public static Date now() {
            return new Date(new java.util.Date());
        }

    }

}
