import static org.junit.jupiter.api.Assertions.*;

import okhttp3.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.alisa.AlisaGlobal;
import pw.mihou.alisa.modules.http.AlisaHttpCall;
import pw.mihou.alisa.modules.rss.AlisaRssReader;
import pw.mihou.alisa.modules.rss.properties.AlisaRssProperty;
import pw.mihou.alisa.modules.rss.properties.scribblehub.AlisaChapterItem;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AlisaFeedTests {

    private static final AtomicReference<AlisaChapterItem> item = new AtomicReference<>(null);

    @Test
    @Order(1)
    @DisplayName("Rss Feed Connecting and Parsing")
    public void testRssParser() {
        AlisaRssProperty property = new AlisaHttpCall(new Request.Builder()
                .url("https://www.scribblehub.com/rssfeed.php?type=author&uid=24680"))
                .content()
                .thenApply(AlisaRssReader::fromContent)
                .join();

        assertNotNull(property);
        System.out.println("Validating that the RSS Feed results is not empty...");
        List<AlisaChapterItem> chapterItems = property.map(AlisaChapterItem::new).toList();
        assertFalse(chapterItems.isEmpty());

        chapterItems.forEach(chapter -> {
            // Asserting chapter data
            assertNotNull(chapter.creator());
            assertNotNull(chapter.title());
            assertNotNull(chapter.link());
            assertNotNull(chapter.pubDate());

            // Asserting chapter story data.
            assertNotNull(chapter.story());
            assertNotEquals(-1, chapter.story().id());
            assertNotNull(chapter.story().name());

            item.set(chapter);
        });
        System.out.println("Rss Feed Connecting and Parsing ✔️");
    }

    @Test
    @Order(2)
    @DisplayName("Moshi JSON conversion")
    public void testJSONConversion() throws IOException {
        AlisaChapterItem chapter = item.get();
        assertNotNull(chapter);

        String json = AlisaGlobal.MOSHI.adapter(AlisaChapterItem.class)
                .toJson(chapter);

        assertNotEquals("", json);
        assertNotEquals("{}", json);

        AlisaChapterItem converted = AlisaGlobal.MOSHI.adapter(AlisaChapterItem.class)
                .fromJson(json);

        assertNotNull(converted);
        assertEquals(chapter.link(), converted.link());
        assertEquals(chapter.title(), converted.title());
        assertEquals(chapter.pubDate(), converted.pubDate());
        assertEquals(chapter.creator(), converted.creator());
        assertEquals(chapter.story().name(), converted.story().name());
        assertEquals(chapter.story().id(), converted.story().id());
        System.out.println("Moshi JSON conversion ✔️");
    }

}
