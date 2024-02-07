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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

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
    public static final String COMIC_SOURCE_URL_TEMPLATE = "https://workchronicles.com/comics/page/{0}";
    public static final List<String> EMOJIS = Arrays.asList(":wave:");
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
        // pick a random page from the website
        var randomPageNo = getRandomPageNumber();
        // extract all comic image URLs
        var pageUrl = MessageFormat.format(COMIC_SOURCE_URL_TEMPLATE, randomPageNo);
        var comicUrls = extractComicUrlsFromPage(pageUrl);
        // return a random image URL
        var randomComicUrl = comicUrls.get(ThreadLocalRandom.current().nextInt(comicUrls.size()));
        return randomComicUrl;
    }

    /**
     * Picks a random page number from the comic website.
     */
    private static int getRandomPageNumber() {
        // the total page count can be found in the <title> tag (starting from page No. 2nd)
        var pageCountDiscoveryUrl = MessageFormat.format(COMIC_SOURCE_URL_TEMPLATE, 2);
        var totalPageCount = getTotalPageCount(getWebsiteSource(pageCountDiscoveryUrl));
        return ThreadLocalRandom.current().nextInt(1, totalPageCount + 1);
    }

    /**
     * Extracts all comic URLs from a given HTML page.
     */
    private static List<String> extractComicUrlsFromPage(String url) {
        var htmlSource = getWebsiteSource(url);
        var pattern = Pattern.compile("(<figure .* src=\")(https://.*.png)(\" .*)");
        var matcher = pattern.matcher(htmlSource);
        var comicUrls = new ArrayList<String>();
        while (matcher.find()) {
            comicUrls.add(matcher.group(2));
        }
        return comicUrls;
    }

    /**
     * Extracts the total page count from a website.
     */
    private static int getTotalPageCount(String htmlSource) {
        var pattern = Pattern.compile("(<title>.*?Page )(\\d+)( of )(\\d+)(.*</title>)");
        var matcher = pattern.matcher(htmlSource);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(4));
        }
        return 0;
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
