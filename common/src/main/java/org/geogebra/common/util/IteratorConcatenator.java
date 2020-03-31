package org.geogebra.common.util;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class IteratorConcatenator<T> implements Iterable<T> {

	private Iterable<? extends Iterable<T>> iterables;

	public IteratorConcatenator(Iterable<? extends Iterable<T>> iterables) {
		this.iterables = iterables;
	}

	@Override
	public @Nonnull Iterator<T> iterator() {
		return new ConcatenatedIterator();
	}

	private class ConcatenatedIterator implements Iterator<T> {

		private Iterator<? extends Iterable<T>> outsideIterator = iterables.iterator();
		private Iterator<T> current = outsideIterator.next().iterator();

		@Override
		public boolean hasNext() {
			while (!current.hasNext()) {
				if (outsideIterator.hasNext()) {
					current = outsideIterator.next().iterator();
				} else {
					break;
				}
			}

			return current.hasNext();
		}

		@Override
		public T next() {
			while (!current.hasNext()) {
				current = outsideIterator.next().iterator();
			}

			return current.next();
		}
	}
}
