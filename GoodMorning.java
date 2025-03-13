import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Spice up your daily "Good morning" greeting with a link to a random comic.
 * This script will fetch a random comic URL, add it as a link to your greeting text and paste it.
 * <br><br>
 * Usage (configure a keyboard shortcut for):
 * <br>
 * <code>java GoodMorning.java</code>
 */
public class GoodMorning {

    public static final String GREETING_TEMPLATE = "[Good morning]({0}) {1}"; // link, emoji
    public static final String COMIC_SOURCE_URL = "https://www.reddit.com/r/workchronicles/hot.json?limit=200";
    public static final List<String> EMOJIS = List.of(":wave:");
    private static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public static void main(String[] args) throws Exception {
        var comicUrl = getRandomComicUrl();
        var emoji = getRandomEmoji();
        var message = MessageFormat.format(GREETING_TEMPLATE, comicUrl, emoji);
        //System.out.println(message);
        pasteText(message);
        openUrl(comicUrl);
    }

    private static String getRandomEmoji() {
        return EMOJIS.get(ThreadLocalRandom.current().nextInt(EMOJIS.size()));
    }

    /**
     * Fetches a random comic URL from WorkChronicles.com.
     */
    private static String getRandomComicUrl() {
        // extract all comic image URLs
        var comicUrls = extractComicUrlsFromPage(COMIC_SOURCE_URL);
        // return a random image URL
        return comicUrls.get(ThreadLocalRandom.current().nextInt(comicUrls.size()));
    }

    /**
     * Extracts all comic URLs from a given HTML page.
     */
    private static List<String> extractComicUrlsFromPage(String url) {
        var htmlSource = getWebsiteSource(url);
        var pattern = Pattern.compile("(\"url\": \")(https://[^\\s\"]+_4800x4800.png)(\")");
        var matcher = pattern.matcher(htmlSource);
        var comicUrls = new ArrayList<String>();
        while (matcher.find()) {
            var imageUrl = extractFinalImageUrl(matcher.group(2));
            comicUrls.add(imageUrl);
        }
        return comicUrls;
    }

    /**
     * Extracts the S3 image link from a nested URL.
     *
     * @param nestedUrl the URL containing an S3 image link in the query string
     * @return the extracted S3 image link
     */
    private static String extractFinalImageUrl(String nestedUrl) {
        var urlEncoded = nestedUrl.split("steep/")[1];
        return decode(urlEncoded, UTF_8);
    }

    /**
     * Fetches the HTML source code of a given URL.
     */
    private static String getWebsiteSource(String url) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not fetch HTML content.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Pastes the given text to whichever app is currently in focus.
     */
    private static void pasteText(String text) throws AWTException {
        var stringSelection = new StringSelection(text);
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        var robot = new Robot();
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
        var osName = System.getProperty("os.name");
        return osName.toLowerCase().startsWith("mac os");
    }

    /**
     * Opens a URL in the default web browser.
     */
    private static void openUrl(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                var uri = new URI(url);
                Desktop.getDesktop().browse(uri);
            } catch (IOException | URISyntaxException e) {
                System.out.println("Could not open [" + url + "] in the default browser.");
                throw new RuntimeException(e);
            }
        }
    }
}
