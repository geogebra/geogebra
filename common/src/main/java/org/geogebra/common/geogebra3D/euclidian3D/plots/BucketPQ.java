package org.geogebra.common.geogebra3D.euclidian3D.plots;

import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An approximate priority queue using buckets and linked lists. Insertion and
 * deletion are fast.
 * 
 * @author Andre Eriksson
 * @param <E>
 */
public class BucketPQ<E> extends AbstractQueue<E> {
	/** total amount of buckets */
	private static final int DEFAULT_BUCKET_AMT = 1024;
	/** array of front of buckets */
	protected Link<E>[] buckets;
	/** array of back of buckets */
	protected Link<E>[] backs;

	/** used for figuring out which buckets to insert elements into */
	protected BucketAssigner<E> bucketAssigner;

	/** hash map that lets us retrieve object's links quickly */
	protected HashMap<E, Link<E>> linkAssociations;

	/** the amount of buckets used */
	protected final int bucketAmt;

	/** the amount of elements in the queue */
	protected int count;

	/** the current highest bucket */
	protected int maxBucket = 0;

	/** indicates the direction of the PQ */
	protected final boolean reverse;

	/**
	 * Class that holds data for elements inserted into the queue
	 * 
	 * @author Andre Eriksson
	 * @param <ET>
	 */
	protected static final class Link<ET> {
		/** the element associated with the link */
		public ET data;

		/** the bucket the element is placed in */
		public int bucketIndex;

		/** the previous element in the bucket list */
		public Link<ET> prev;

		/** the next element in the bucket list */
		public Link<ET> next;

		/**
		 * @param o
		 *            the object to link with
		 */
		public Link(ET o) {
			data = o;
		}
	}

	/**
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 */
	protected BucketPQ(BucketAssigner<E> ba, boolean reverse) {
		this(DEFAULT_BUCKET_AMT, ba, reverse);
	}

	/**
	 * @param bucketAmt
	 *            the number of buckets to use
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 */
	@SuppressWarnings("unchecked")
	public BucketPQ(int bucketAmt, BucketAssigner<E> ba, boolean reverse) {
		this.bucketAmt = bucketAmt;
		buckets = (Link<E>[]) new Link[bucketAmt];
		backs = (Link<E>[]) new Link[bucketAmt];
		this.bucketAssigner = ba;
		this.reverse = reverse;

		linkAssociations = new HashMap<E, BucketPQ.Link<E>>();
	}

	private int getIndex(E el) {
		int i = bucketAssigner.getBucketIndex(el, bucketAmt);
		return reverse ? bucketAmt - 1 - i : i;
	}

	/**
	 * Adds an element to the queue.
	 * 
	 * @param ob
	 *            the object to be added.
	 * @return false if the element is already in the queue. Otherwise true.
	 */
	public boolean add(Object ob) {
		@SuppressWarnings("unchecked")
		E object = (E) ob;

		if (null == object)
			throw new NullPointerException();

		Link<E> elem = findLink(object);

		// ignore element if already in queue
		if (elem != null)
			return false;

		int bucketIndex = getIndex(object);

		elem = new Link<E>(object);

		// update pointers
		elem.prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].next = elem;
		backs[bucketIndex] = elem;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = elem;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		elem.bucketIndex = bucketIndex;

		count++;

		linkAssociations.put(object, elem);

		return true;
	}

	/**
	 * @param o
	 *            an object in the queue
	 * @return the link associated with the object. null if the object is not in
	 *         the queue.
	 */
	protected Link<E> findLink(Object o) {
		return linkAssociations.get(o);
	}

	@Override
	public boolean remove(Object o) {
		Link<E> elem = findLink(o);

		return remove(elem);
	}

	/**
	 * @param elem
	 *            the element to remove
	 * @return true if the object was in the queue - otherwise false.
	 */
	public boolean remove(Link<E> elem) {
		// ignore element if not in queue
		if (elem == null)
			return false;

		int bi = elem.bucketIndex;

		// update pointers of elements before/after in queue
		if (elem.next != null)
			elem.next.prev = elem.prev;
		if (elem.prev != null)
			elem.prev.next = elem.next;

		// update bucket list and max bucket index as needed
		if (buckets[bi] == elem)
			buckets[bi] = elem.next;

		if (backs[bi] == elem)
			backs[bi] = elem.prev;

		while (maxBucket > 0 && buckets[maxBucket] == null)
			maxBucket--;

		elem.next = elem.prev = null;

		linkAssociations.remove(elem.data);

		count--;

		return true;
	}

	/**
	 * @return the first element in the top bucket
	 */
	public E peek() {
		return buckets[maxBucket].data;
	}

	public boolean offer(E e) {
		return add(e);
	}

	public E poll() {
		if (maxBucket == 0)
			return null;
		Link<E> elem = buckets[maxBucket];
		remove(elem);
		return elem.data;
	}

	public E forcePoll() {
		Link<E> elem = buckets[maxBucket];
		remove(elem);
		return elem.data;
	}

	public Iterator<E> iterator() {

		return new Iterator<E>() {
			private Link<E> el = buckets[0];
			private int bucket;

			public boolean hasNext() {
				Link<E> a = el == null ? null : el.next;
				int b2 = bucket;
				while (a == null) {
					b2++;
					if (b2 > BucketPQ.this.maxBucket)
						return false;
					a = buckets[b2];
				}
				return true;
			}

			public E next() {
				el = el == null ? null : el.next;
				while (el == null) {
					bucket++;
					if (bucket > BucketPQ.this.maxBucket)
						throw new NoSuchElementException();
					el = buckets[bucket];
				}
				return el.data;
			}

			public void remove() {
				BucketPQ.this.remove(el);
				el = el.next;
			}
		};
	}

	@Override
	public int size() {
		return count;
	}
}