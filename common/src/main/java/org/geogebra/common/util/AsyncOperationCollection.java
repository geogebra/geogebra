package org.geogebra.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of async operations.
 *
 * @param <T>
 *            callback argument type
 */
public class AsyncOperationCollection<T> implements AsyncOperation<T> {

	private List<AsyncOperation<T>> operations;

	/**
	 * Construct a collection of async operations.
	 *
	 * @param operations operations
	 */
	public AsyncOperationCollection(AsyncOperation<T>... operations) {
		this.operations = Arrays.asList(operations);
	}

	@Override
	public void callback(T obj) {
		for (AsyncOperation<T> operation: operations) {
			operation.callback(obj);
		}
	}
}
