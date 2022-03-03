package pw.mihou.alisa.modules.rss.properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public Optional<Date> asDate(SimpleDateFormat formatter) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(formatter.parse(value));
        } catch (ParseException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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

    /**
     * Maps all the properties into the specific type, this is an alias of the
     * {@link Stream#map(Function)} which is longer when typed out.
     *
     * @param mappingFunction   The mapping function that should be provided by the
     *                          type of class.
     * @param <T>               The type of class to map into.
     * @return                  A stream of the mapped properties.
     */
    public <T> Stream<T> map(Function<AlisaRssProperty, T> mappingFunction) {
        return properties.stream().map(mappingFunction);
    }

}
