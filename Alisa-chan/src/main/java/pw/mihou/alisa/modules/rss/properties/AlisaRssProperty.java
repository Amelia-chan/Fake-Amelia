package pw.mihou.alisa.modules.rss.properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record AlisaRssProperty(
        @Nonnull String name,
        @Nullable String value,
        @Nonnull List<AlisaRssProperty> properties
) {

    /**
     * Gets the value of this {@link AlisaRssProperty} as a {@link Integer}.
     *
     * @return  The {@link Integer} value of this property.
     */
    public Optional<Integer> asInteger() {
        try {
            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the value of this {@link AlisaRssProperty} as a {@link Double}.
     *
     * @return  The {@link Double} value of this property.
     */
    public Optional<Double> asDouble() {
        try {
            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the value of this {@link AlisaRssProperty} as a {@link Long}.
     *
     * @return  The {@link Long} value of this property.
     */
    public Optional<Long> asLong() {
        try {
            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the value of this {@link AlisaRssProperty} as a {@link Boolean}.
     *
     * @return  The {@link Boolean} value of this property.
     */
    public Optional<Boolean> asBoolean() {
        if (value == null) {
            return Optional.empty();
        }

        if (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("false")) {
            return Optional.of(Boolean.parseBoolean(value));
        }

        return Optional.empty();
    }

    /**
     * Gets the first property that has the name specified.
     *
     * @param name  The name of the property to find.
     * @return      The first property that matches the name specified.
     */
    public Optional<AlisaRssProperty> getPropertyWithName(String name) {
        return properties.stream()
                .filter(property -> property.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Gets the properties that has the name specified.
     *
     * @param name  The name of the properties to find.
     * @return      The properties that match the name specified.
     */
    public List<AlisaRssProperty> getPropertiesWithName(String name) {
        return properties.stream()
                .filter(property -> property.name().equalsIgnoreCase(name))
                .toList();
    }

}
