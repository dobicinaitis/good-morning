import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Spice up your daily "Good morning" greeting with a link to a random <a href="https://www.monkeyuser.com">MonkeyUser.com</a> comic.
 * This script will fetch a random comic URL, add it as a link to your greeting text and paste it.
 * <br><br>
 * Usage (configure a keyboard shortcut for):
 * <br>
 * <code>java GoodMorning.java</code>
 */
public class GoodMorning {

    public static final String GREETING_TEMPLATE = "[Good morning]({0}) \uD83D\uDC4B";
    public static final String COMIC_FEED_URL = "https://www.monkeyuser.com/feed.xml";
    public static final String IMAGE_XPATH_EXPRESSION = "/rss/channel/item/description[contains(text(),'.png')]";

    public static void main(String[] args) throws Exception {
        final String imageUrl = getRandomImageUrl();
        final String message = MessageFormat.format(GREETING_TEMPLATE, imageUrl);
        pasteText(message);
    }

    private static String getRandomImageUrl() {
        final List<String> comicUrls = getAllImageUrls();
        return comicUrls.get(ThreadLocalRandom.current().nextInt(comicUrls.size()));
    }

    private static List<String> getAllImageUrls() {
        final Document comicFeed = loadRssFeed();
        final XPath xPath = XPathFactory.newInstance().newXPath();
        final NodeList descriptionNodes;

        try {
            descriptionNodes = (NodeList) xPath.compile(IMAGE_XPATH_EXPRESSION).evaluate(comicFeed, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        if (descriptionNodes.getLength() == 0) {
            throw new RuntimeException("Could not extract \"description\" tags from RSS feed.");
        }

        // now we need to extract URLs from the <description> elements that contain some extra HTML
        Pattern urlPattern = Pattern.compile("src=\"(.*?)\"");
        final List<String> imageUrls = IntStream.range(0, descriptionNodes.getLength())
                .parallel()
                .mapToObj(descriptionNodes::item)
                // let's look at items with only one paragraph tag to avoid some challenges and other special entries
                .filter(item -> item.getTextContent().lastIndexOf("<p>") == 0)
                .flatMap(item -> urlPattern.matcher(item.getTextContent()).results())
                .map(match -> match.group(1))
                .collect(Collectors.toList());

        if (imageUrls.size() == 0) {
            throw new RuntimeException("Could not extract image URLs from \"description\" tags.");
        }

        return imageUrls;
    }

    private static Document loadRssFeed() {
        try {
            final URLConnection connection = new URL(COMIC_FEED_URL).openConnection();
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(connection.getInputStream());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println("No comic today ;(");
            throw new RuntimeException(e);
        }
    }

    private static void pasteText(String text) throws AWTException {
        final StringSelection stringSelection = new StringSelection(text);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        final Robot robot = new Robot();
        robot.keyPress(controlOrCommandKey());
        robot.delay(200);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(controlOrCommandKey());
    }

    private static int controlOrCommandKey() {
        return isRunningOnAMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;
    }

    private static boolean isRunningOnAMac() {
        final String osName = System.getProperty("os.name");
        return osName.toLowerCase().startsWith("mac os");
    }
}
