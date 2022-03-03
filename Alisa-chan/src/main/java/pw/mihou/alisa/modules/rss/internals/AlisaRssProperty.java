package pw.mihou.alisa.modules.rss.internals;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

// TODO: Handle different property types like integer, etc.
public record AlisaRssProperty(
        @Nullable String value,
        @Nonnull Map<String, AlisaRssProperty> properties
) {


}
