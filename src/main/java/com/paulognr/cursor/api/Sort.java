package com.paulognr.cursor.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sort {

    private static final Sort UNSORTED = Sort.by(new Sort.Order[0]);

    public static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.ASC;

    private final List<Sort.Order> orders;

    public static Sort unsorted() {
        return UNSORTED;
    }

    protected Sort(List<Sort.Order> orders) {
        this.orders = orders;
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param direction defaults to {@link Sort#DEFAULT_DIRECTION} (for {@literal null} cases, too)
     * @param properties must not be {@literal null} or contain {@literal null} or empty strings.
     */
    private Sort(Sort.Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = properties.stream() //
                .map(it -> new Sort.Order(direction, it)) //
                .collect(Collectors.toList());
    }

    /**
     * Creates a new {@link Sort} for the given properties.
     *
     * @param properties must not be {@literal null}.
     * @return
     */
    public static Sort by(String... properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Properties must not be null!");
        }

        return properties.length == 0 //
                ? Sort.unsorted() //
                : new Sort(DEFAULT_DIRECTION, Arrays.asList(properties));
    }

    /**
     * Creates a new {@link Sort} for the given {@link Sort.Order}s.
     *
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @return
     */
    public static Sort by(Sort.Direction direction, String... properties) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction must not be null!");
        }

        if (properties == null) {
            throw new IllegalArgumentException("Properties must not be null!");
        }

        if (properties.length == 0) {
            throw new IllegalArgumentException("At least one property must be given!");
        }

        return Sort.by(Arrays.stream(properties)//
                .map(it -> new Sort.Order(direction, it))//
                .collect(Collectors.toList()));
    }

    /**
     * Creates a new {@link Sort} for the given {@link Sort.Order}s.
     *
     * @param orders must not be {@literal null}.
     * @return
     */
    public static Sort by(List<Sort.Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException("Orders must not be null!");
        }

        return orders.isEmpty() ? Sort.unsorted() : new Sort(orders);
    }

    /**
     * Creates a new {@link Sort} for the given {@link Sort.Order}s.
     *
     * @param orders must not be {@literal null}.
     * @return
     */
    public static Sort by(Sort.Order... orders) {
        if (orders == null) {
            throw new IllegalArgumentException("Orders must not be null!");
        }

        return new Sort(Arrays.asList(orders));
    }


    public static enum Direction {

        ASC, DESC;

        /**
         * Returns whether the direction is ascending.
         *
         * @return
         */
        public boolean isAscending() {
            return this.equals(ASC);
        }

        /**
         * Returns whether the direction is descending.
         *
         * @return
         */
        public boolean isDescending() {
            return this.equals(DESC);
        }

        /**
         * Returns the {@link Direction} enum for the given {@link String} value.
         *
         * @param value
         * @throws IllegalArgumentException in case the given value cannot be parsed into an enum value.
         * @return
         */
        public static Direction fromString(String value) {

            try {
                return Direction.valueOf(value.toUpperCase(Locale.US));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format(
                        "Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
            }
        }

        /**
         * Returns the {@link Direction} enum for the given {@link String} or null if it cannot be parsed into an enum
         * value.
         *
         * @param value
         * @return
         */
        public static Optional<Direction> fromOptionalString(String value) {

            try {
                return Optional.of(fromString(value));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Enumeration for null handling hints that can be used in {@link Sort.Order} expressions.
     */
    public static enum NullHandling {

        /**
         * Lets the data store decide what to do with nulls.
         */
        NATIVE,

        /**
         * A hint to the used data store to order entries with null values before non null entries.
         */
        NULLS_FIRST,

        /**
         * A hint to the used data store to order entries with null values after non null entries.
         */
        NULLS_LAST;
    }

    /**
     * PropertyPath implements the pairing of an {@link Sort.Direction} and a property. It is used to provide input for
     * {@link Sort}
     */
    public static class Order implements Serializable {

        private static final long serialVersionUID = 1522511010900108987L;
        private static final boolean DEFAULT_IGNORE_CASE = false;
        private static final NullHandling DEFAULT_NULL_HANDLING = NullHandling.NATIVE;

        private final Sort.Direction direction;
        private final String property;
        private final boolean ignoreCase;
        private final NullHandling nullHandling;

        /**
         * Creates a new {@link Sort.Order} instance. if order is {@literal null} then order defaults to
         * {@link Sort#DEFAULT_DIRECTION}
         *
         * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
         * @param property must not be {@literal null} or empty.
         */
        public Order(Sort.Direction direction, String property) {
            this(direction, property, DEFAULT_IGNORE_CASE, DEFAULT_NULL_HANDLING);
        }

        /**
         * Creates a new {@link Sort.Order} instance. if order is {@literal null} then order defaults to
         * {@link Sort#DEFAULT_DIRECTION}
         *
         * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
         * @param property must not be {@literal null} or empty.
         * @param nullHandlingHint must not be {@literal null}.
         */
        public Order(Sort.Direction direction, String property, NullHandling nullHandlingHint) {
            this(direction, property, DEFAULT_IGNORE_CASE, nullHandlingHint);
        }

        /**
         * Creates a new {@link Sort.Order} instance. Takes a single property. Direction defaults to
         * {@link Sort#DEFAULT_DIRECTION}.
         *
         * @param property must not be {@literal null} or empty.
         */
        public static Sort.Order by(String property) {
            return new Sort.Order(DEFAULT_DIRECTION, property);
        }

        /**
         * Creates a new {@link Sort.Order} instance. Takes a single property. Direction is {@link Sort.Direction#ASC} and
         * NullHandling {@link NullHandling#NATIVE}.
         *
         * @param property must not be {@literal null} or empty.
         */
        public static Sort.Order asc(String property) {
            return new Sort.Order(Sort.Direction.ASC, property, DEFAULT_NULL_HANDLING);
        }

        /**
         * Creates a new {@link Sort.Order} instance. Takes a single property. Direction is {@link Sort.Direction#DESC} and
         * NullHandling {@link NullHandling#NATIVE}.
         *
         * @param property must not be {@literal null} or empty.
         */
        public static Sort.Order desc(String property) {
            return new Sort.Order(Sort.Direction.DESC, property, DEFAULT_NULL_HANDLING);
        }

        /**
         * Creates a new {@link Sort.Order} instance. if order is {@literal null} then order defaults to
         * {@link Sort#DEFAULT_DIRECTION}
         *
         * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
         * @param property must not be {@literal null} or empty.
         * @param ignoreCase true if sorting should be case insensitive. false if sorting should be case sensitive.
         * @param nullHandling must not be {@literal null}.
         */
        private Order(Sort.Direction direction, String property, boolean ignoreCase, NullHandling nullHandling) {
            if (property == null || property.trim().isEmpty()) {
                throw new IllegalArgumentException("Property must not null or empty!");
            }

            this.direction = direction == null ? DEFAULT_DIRECTION : direction;
            this.property = property;
            this.ignoreCase = ignoreCase;
            this.nullHandling = nullHandling;
        }

        /**
         * Returns the order the property shall be sorted for.
         *
         * @return
         */
        public Sort.Direction getDirection() {
            return direction;
        }

        /**
         * Returns the property to order for.
         *
         * @return
         */
        public String getProperty() {
            return property;
        }

        /**
         * Returns whether sorting for this property shall be ascending.
         *
         * @return
         */
        public boolean isAscending() {
            return this.direction.isAscending();
        }

        /**
         * Returns whether sorting for this property shall be descending.
         *
         * @return
         */
        public boolean isDescending() {
            return this.direction.isDescending();
        }

        /**
         * Returns whether or not the sort will be case sensitive.
         *
         * @return
         */
        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        /**
         * Returns a new {@link Sort.Order} with the given {@link Sort.Direction}.
         *
         * @param direction
         * @return
         */
        public Sort.Order with(Sort.Direction direction) {
            return new Sort.Order(direction, this.property, this.ignoreCase, this.nullHandling);
        }

        /**
         * Returns a new {@link Sort.Order}
         *
         * @param property must not be {@literal null} or empty.
         * @return
         */
        public Sort.Order withProperty(String property) {
            return new Sort.Order(this.direction, property, this.ignoreCase, this.nullHandling);
        }

        /**
         * Returns a new {@link Sort} instance for the given properties.
         *
         * @param properties
         * @return
         */
        public Sort withProperties(String... properties) {
            return Sort.by(this.direction, properties);
        }

        /**
         * Returns a new {@link Sort.Order} with case insensitive sorting enabled.
         *
         * @return
         */
        public Sort.Order ignoreCase() {
            return new Sort.Order(direction, property, true, nullHandling);
        }

        /**
         * Returns a {@link Sort.Order} with the given {@link NullHandling}.
         *
         * @param nullHandling can be {@literal null}.
         * @return
         */
        public Sort.Order with(NullHandling nullHandling) {
            return new Sort.Order(direction, this.property, ignoreCase, nullHandling);
        }

        /**
         * Returns a {@link Sort.Order} with {@link NullHandling#NULLS_FIRST} as null handling hint.
         *
         * @return
         */
        public Sort.Order nullsFirst() {
            return with(NullHandling.NULLS_FIRST);
        }

        /**
         * Returns a {@link Sort.Order} with {@link NullHandling#NULLS_LAST} as null handling hint.
         *
         * @return
         */
        public Sort.Order nullsLast() {
            return with(NullHandling.NULLS_LAST);
        }

        /**
         * Returns a {@link Sort.Order} with {@link NullHandling#NATIVE} as null handling hint.
         *
         * @return
         */
        public Sort.Order nullsNative() {
            return with(NullHandling.NATIVE);
        }

        /**
         * Returns the used {@link NullHandling} hint, which can but may not be respected by the used datastore.
         *
         * @return
         */
        public NullHandling getNullHandling() {
            return nullHandling;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {

            int result = 17;

            result = 31 * result + direction.hashCode();
            result = 31 * result + property.hashCode();
            result = 31 * result + (ignoreCase ? 1 : 0);
            result = 31 * result + nullHandling.hashCode();

            return result;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }

            if (!(obj instanceof Sort.Order)) {
                return false;
            }

            Sort.Order that = (Sort.Order) obj;

            return this.direction.equals(that.direction) && this.property.equals(that.property)
                    && this.ignoreCase == that.ignoreCase && this.nullHandling.equals(that.nullHandling);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            String result = String.format("%s: %s", property, direction);

            if (!NullHandling.NATIVE.equals(nullHandling)) {
                result += ", " + nullHandling;
            }

            if (ignoreCase) {
                result += ", ignoring case";
            }

            return result;
        }
    }
}
