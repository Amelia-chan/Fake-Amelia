package pw.mihou.alisa.modules.rss.properties.scribblehub;

import pw.mihou.alisa.modules.exceptions.handler.AlisaExceptionHandler;
import pw.mihou.alisa.modules.rss.properties.AlisaRssProperty;
import pw.mihou.alisa.modules.rss.properties.dates.AlisaRssDateFormats;
import pw.mihou.alisa.modules.rss.properties.scribblehub.modules.AlisaStoryProperty;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AlisaChapterItem {

    @Nonnull private final String title;
    @Nonnull private final String link;
    @Nonnull private final String creator;
    @Nonnull private final Date pubDate;
    @Nonnull private final AlisaStoryProperty story;

    /**
     * Creates a new {@link AlisaChapterItem} out of a single {@link AlisaRssProperty} node that
     * should be of `item` node name.
     *
     * @param property  The property to reference from.
     */
    public AlisaChapterItem(AlisaRssProperty property) {
        this.title = Objects.requireNonNull(property.getPropertyWithName("title").orElseThrow().value());
        this.link = Objects.requireNonNull(property.getPropertyWithName("link").orElseThrow().value());
        AtomicReference<String> catName = new AtomicReference<>(null);
        AtomicInteger catId = new AtomicInteger(-1);

        property.getPropertiesWithName("category").forEach(category -> {
            if (category.value() == null) {
                AlisaExceptionHandler.accept(new IllegalStateException("A category property returned a value of null."));
                return;
            }

            try {
                catId.set(Integer.parseInt(category.value()));
            } catch (NumberFormatException e) {
                catName.set(category.value());
            }
        });

        this.story = new AlisaStoryProperty(catName.get(), catId.get());
        this.creator = Objects.requireNonNull(property.getPropertyWithName("dc:creator").orElseThrow().value());
        this.pubDate = property.getPropertyWithName("pubDate").orElseThrow()
                .asDate(AlisaRssDateFormats.SCRIBBLEHUB_RSS)
                .orElseThrow();
    }

    /**
     * Gets the title of the chapter.
     *
     * @return  The title of the chapter.
     */
    public String title() {
        return title;
    }

    /**
     * Gets the link of the chapter.
     *
     * @return  The link of the chapter.
     */
    public String link() {
        return link;
    }

    /**
     * Gets the story properties of the chapter.
     *
     * @return  The story properties of the chapter.
     */
    public AlisaStoryProperty story() {
        return story;
    }

    /**
     * Gets the creator of the chapter.
     *
     * @return  The creator of the chapter.
     */
    public String creator() {
        return creator;
    }

    /**
     * Gets the published date of the chapter.
     *
     * @return  The published date of the chapter.
     */
    public Date pubDate() {
        return pubDate;
    }
}
