/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
