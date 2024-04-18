/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.jung.collections;

import java.util.Iterator;

/**
 * Decorates an iterator such that it cannot be modified.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public final class UnmodifiableIterator<E>
		implements Iterator<E> {

	/** The iterator being decorated */
	private final Iterator<E> iterator;

	// -----------------------------------------------------------------------
	/**
	 * Decorates the specified iterator such that it cannot be modified.
	 * <p>
	 * If the iterator is already unmodifiable it is returned directly.
	 *
	 * @param iterator
	 *            the iterator to decorate
	 * @throws IllegalArgumentException
	 *             if the iterator is null
	 */
	public static <E> Iterator<E> decorate(Iterator<E> iterator) {
		if (iterator == null) {
			throw new IllegalArgumentException("Iterator must not be null");
		}
		if (iterator instanceof UnmodifiableIterator) {
			return iterator;
		}
		return new UnmodifiableIterator<>(iterator);
	}

	// -----------------------------------------------------------------------
	/**
	 * Constructor.
	 *
	 * @param iterator
	 *            the iterator to decorate
	 */
	private UnmodifiableIterator(Iterator<E> iterator) {
		super();
		this.iterator = iterator;
	}

	// -----------------------------------------------------------------------
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported");
	}

}
