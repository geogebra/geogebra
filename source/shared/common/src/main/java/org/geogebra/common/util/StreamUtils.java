package org.geogebra.common.util;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

/**
 * Utilities for compressing long and noisy stream calls.
 */
public final class StreamUtils {
	private StreamUtils() {
	}

	/**
	 * Creates a new stream {@code Stream} from an iterable.
	 * @param iterable an iterable to provide the elements for the stream
	 * @return stream of elements from the iterable
	 * @param <T> the type of iterable elements
	 */
	public static<T> Stream<T> streamOf(@Nonnull Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * Creates a new set by taking the values provided by the iterable
	 * and filtering them according to the predicate.
	 * @param iterable an iterable to provide the elements to filter
	 * @param predicate predicate to apply to each element to determine if it should be included
	 * @return a new set containing the filtered values
	 * @param <T> the type of the iterable elements
	 */
	public static<T> Set<T> filter(@Nonnull Iterable<T> iterable, @Nonnull Predicate<T> predicate) {
		return streamOf(iterable).filter(predicate).collect(Collectors.toSet());
	}

	/**
	 * Returns a set consisting of the results of the elements
	 * created by the mapper function applied to each element in the collection.
	 * @param collection a collection to provide the elements for mapping
	 * @param mapper a function to apply to each element which produces a collection of new values
	 * @return the new set
	 * @param <T> the type of the collection elements
	 * @param <R> the type of the new set elements
	 */
	public static<T, R> Set<R> flatMap(
			@Nonnull Collection<T> collection,
			@Nonnull Function<? super T, ? extends Collection<? extends R>> mapper
	) {
		return collection.stream().flatMap(element -> mapper.apply(element).stream())
				.collect(Collectors.toSet());
	}
}
