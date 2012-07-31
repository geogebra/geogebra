package geogebra3D.euclidian3D.plots;

import geogebra3D.euclidian3D.BucketAssigner;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * An approximate priority queue using buckets and linked lists. Insertion and
 * deletion are fast.
 * 
 */
public class FastBucketPQ {

	/** total amount of buckets */
	private static final int DEFAULT_BUCKET_AMT = 2048;

	/** array of front of buckets */
	protected DynamicMeshElement2[] buckets;

	/** array of back of buckets */
	protected DynamicMeshElement2[] backs;

	/** used for figuring out which buckets to insert elements into */
	protected BucketAssigner<DynamicMeshElement2> bucketAssigner;

	/** the amount of buckets used */
	protected final int bucketAmt;

	/** the amount of elements in the queue */
	protected int count;

	/** the current highest bucket */
	protected int maxBucket = 0;

	/** indicates the direction of the PQ */
	protected final boolean reverse;

	/**
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 *            if true, elements are sorted reversely
	 */
	protected FastBucketPQ(BucketAssigner<DynamicMeshElement2> ba,
			boolean reverse) {
		this(DEFAULT_BUCKET_AMT, ba, reverse);
	}

	/**
	 * @param bucketAmt
	 *            the number of buckets to use
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 *            if true, elements are sorted reversely
	 */
	public FastBucketPQ(int bucketAmt, BucketAssigner<DynamicMeshElement2> ba,
			boolean reverse) {
		this.bucketAmt = bucketAmt;
		buckets = new DynamicMeshElement2[bucketAmt];
		backs = new DynamicMeshElement2[bucketAmt];
		this.bucketAssigner = ba;
		this.reverse = reverse;
	}

	private int getIndex(DynamicMeshElement2 el) {
		int i = bucketAssigner.getBucketIndex(el, bucketAmt);
		return reverse ? bucketAmt - 1 - i : i;
	}

	private boolean addToZeroBucket(DynamicMeshElement2 obj) {
		if (null == obj)
			throw new NullPointerException();

		if (obj.bucket_owner != null)
			return false;

		int bucketIndex = 0;

		// update pointers
		obj.bucket_prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].bucket_next = obj;
		backs[bucketIndex] = obj;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = obj;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		obj.bucket_index = bucketIndex;

		count++;

		obj.bucket_owner = this;

		return true;
	}

	/**
	 * Adds an element to the queue.
	 * 
	 * @param obj
	 *            the object to be added.
	 * @return false if the element is already in the queue. Otherwise true.
	 */
	public boolean add(DynamicMeshElement2 obj) {

		if (obj.cullInfo == CullInfo2.OUT)
			return addToZeroBucket(obj);

		if (obj.bucket_owner != null)
			return false;

		int bucketIndex = getIndex(obj);

		// update pointers
		obj.bucket_prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].bucket_next = obj;
		backs[bucketIndex] = obj;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = obj;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		obj.bucket_index = bucketIndex;

		count++;

		obj.bucket_owner = this;

		return true;
	}

	/**
	 * @param elem
	 *            the element to remove
	 * @return true if the object was in the queue - otherwise false.
	 */
	public boolean remove(DynamicMeshElement2 elem) {
		// ignore element if not in queue
		if (elem == null || elem.bucket_owner != this)
			return false;

		int bi = elem.bucket_index;

		// update pointers of elements before/after in queue
		if (elem.bucket_next != null)
			elem.bucket_next.bucket_prev = elem.bucket_prev;
		if (elem.bucket_prev != null)
			elem.bucket_prev.bucket_next = elem.bucket_next;

		// update bucket list and max bucket index as needed
		if (buckets[bi] == elem)
			buckets[bi] = elem.bucket_next;

		if (backs[bi] == elem)
			backs[bi] = elem.bucket_prev;

		while (maxBucket > 0 && buckets[maxBucket] == null)
			maxBucket--;

		elem.bucket_next = elem.bucket_prev = null;

		elem.bucket_owner = null;

		count--;

		return true;
	}

	/**
	 * Finds the bucket of maximum error and compares it to the front element.
	 * Used for debugging purposes only.
	 */
	public void debugErrorTest() {
		double maxError = -100;
		int buck = -1;
		double frontError = peek().getError();

		for (int i = 0; i <= maxBucket; i++) {
			DynamicMeshElement2 e = buckets[i];
			while (e != null) {
				double err = e.getError();
				if (err > maxError) {
					maxError = err;
					buck = i;
				}
				e = e.bucket_next;
			}
		}
		System.out.println("Maximum error: " + maxError + "\tBucket: " + buck
				+ "\tFront error: " + frontError);
	}

	/**
	 * @return the first element in the top bucket
	 */
	public DynamicMeshElement2 peek() {
		return buckets[maxBucket];
	}

	/**
	 * Retrieves and removes the first element in the queue.
	 * @return top element in first bucket, or null if all buckets appear to be empty
	 */
	public DynamicMeshElement2 poll() {
		if (maxBucket == 0)
			return null;
		DynamicMeshElement2 elem = buckets[maxBucket];
		remove(elem);
		return elem;
	}

	/**
	 * Retrieves and removes the first element in the queue. Not stable if queue is empty.
	 * @return top element in first bucket
	 */
	public DynamicMeshElement2 forcePoll() {
		DynamicMeshElement2 elem = buckets[maxBucket];
		remove(elem);
		return elem;
	}

	/** 
	 * Forces all elements in queue to recalculate vertices, error, etc.
	 * @param currentVersion Current version of the mesh - increments when the function is changed
	 * @param triList Triangle list used.
	 */
	public void recalculate(int currentVersion, DynamicMeshTriList2 triList) {
		LinkedList<DynamicMeshElement2> list = new LinkedList<DynamicMeshElement2>();
		for (int i = 0; i <= maxBucket; i++) {
			DynamicMeshElement2 e = buckets[i];
			while (e != null) {
				if (e.lastVersion != currentVersion)
					list.add(e);
				e = e.bucket_next;
			}
		}
		Iterator<DynamicMeshElement2> it = list.iterator();
		while (it.hasNext()) {
			DynamicMeshElement2 a = it.next();
			triList.reinsert(a, currentVersion);
		}
	}

	/**
	 * @return number of elements in queue
	 */
	public int size() {
		return count;
	}
}