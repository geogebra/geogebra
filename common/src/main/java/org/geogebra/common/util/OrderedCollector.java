package org.geogebra.common.util;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class OrderedCollector {
    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@link LinkedHashMap} whose keys and values are the result of
     * applying the provided mapping functions to the input elements.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a {@code Collector} which collects elements into a {@link LinkedHashMap} whose keys
     * and values are the result of applying mapping functions to the input elements
     */
    public static <T, K, U>
    Collector<T, ?, LinkedHashMap<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (a, b) -> b, LinkedHashMap::new);
    }
}
