package pw.mihou.akari.activities;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.Request;
import pw.mihou.akari.Akari;
import pw.mihou.akari.databases.AkariDatabases;
import pw.mihou.akari.websocket.facade.AkariWebsocket;
import pw.mihou.alisa.modules.AlisaFeed;
import pw.mihou.alisa.modules.exceptions.handler.AlisaExceptionHandler;
import pw.mihou.alisa.modules.http.AlisaHttpCall;
import pw.mihou.alisa.modules.rss.AlisaRssReader;
import pw.mihou.alisa.modules.rss.properties.scribblehub.AlisaChapterItem;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AkariFeeds {

    private static final int MAXIMUM_RATE = 2;
    private static final Duration RATE_TIMER = Duration.ofSeconds(4);
    private static final AtomicInteger RATE = new AtomicInteger();
    private static final AtomicLong LAST_REQUEST = new AtomicLong(-1);
    private static final Cache<String, List<AlisaChapterItem>> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();

    /**
     * Starts reading through all the feeds and sending them a notification through the websocket
     * if any of the published dates are newer than the current listed.
     *
     * @param websocket The websocket to transport the data towards.
     */
    public static void start(@Nonnull AkariWebsocket websocket) {
        try {
            List<AlisaFeed> feeds = AkariDatabases.FEEDS.all().mapAndList();

            for (AlisaFeed feed : feeds) {
                if (RATE.incrementAndGet() > MAXIMUM_RATE) {

                    if (LAST_REQUEST.get() > System.nanoTime() + RATE_TIMER.toNanos()) {
                        Thread.sleep(2000L);
                    }

                    RATE.set(0);
                }

                List<AlisaChapterItem> chapters = getAndUpdate(feed);
                LAST_REQUEST.set(System.nanoTime());

                Akari.getLogger().info("A feed has finished synchronization. [unique={}, url={}, count={}]",
                        feed.unique(), feed.url(), chapters.size()
                );
                chapters.forEach(websocket::send);
            }
        } catch (Exception exception) {
            AlisaExceptionHandler.accept(exception);
        }
    }

    /**
     * Gets and update the feed with the data acquired before returning the chapters
     * that were newly added within that short timespan.
     *
     * @param feed  The feed to perform a lookup into.
     * @return      All the new chapters that were received from the feed.
     */
    public static List<AlisaChapterItem> getAndUpdate(AlisaFeed feed) {
        List<AlisaChapterItem> chapters =  peek(feed).stream()
                .filter(chapter -> chapter.pubDate().after(feed.date()))
                .toList();

        chapters.stream().findFirst().ifPresent(chapter -> AkariDatabases.FEEDS.upsert(feed.date(chapter.pubDate())));
        return chapters;
    }

    /**
     * Peeks into the available feed data and returns the data without
     * performing any filtration af any form.
     *
     * @param feed  The feed to peek into.
     * @return      All the chapters that were available in the feed.
     */
    public static List<AlisaChapterItem> peek(AlisaFeed feed) {
        List<AlisaChapterItem> chapters = CACHE.getIfPresent(feed.url());

        if (chapters == null) {
            chapters = new AlisaHttpCall(new Request.Builder()
                    .url(feed.url()))
                    .content()
                    .thenApply(AlisaRssReader::fromContent)
                    .join()
                    .map(AlisaChapterItem::new)
                    .toList();
        }

        return chapters;
    }

}
