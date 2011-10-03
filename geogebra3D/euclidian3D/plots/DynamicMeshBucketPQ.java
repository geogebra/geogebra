//package geogebra3D.euclidian3D.plots;
//
//import geogebra3D.euclidian3D.BucketAssigner;
//import geogebra3D.euclidian3D.BucketPQ;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//
///**
// * A bucket priority queue designed specifically for dynamic meshes. Inserts
// * culled diamonds into the zeroth bucket.
// * 
// * @author André Eriksson
// */
//public class DynamicMeshBucketPQ extends BucketPQ<DynamicMeshElement> {
//
//	/**
//	 * @param ba
//	 *            the bucket assigner to use
//	 */
//	DynamicMeshBucketPQ(BucketAssigner<DynamicMeshElement> ba,
//			boolean reverse) {
//		super(ba, reverse);
//	}
//
//	public boolean add(DynamicMeshElement object) {
//		if (findLink(object) != null) // already in queue
//			return false;
//
//		// put invisible diamonds in first bucket
//		if (object.cullInfo == CullInfo.OUT)
//			return addToZeroBucket(object);
//
//		return super.add(object);
//	}
//
//	private boolean addToZeroBucket(DynamicMeshElement object) {
//		if (null == object)
//			throw new NullPointerException();
//
//		Link<DynamicMeshElement> elem = findLink(object);
//
//		// ignore element if already in queue
//		if (elem != null)
//			return false;
//
//		int bucketIndex = 0;
//
//		elem = new Link<DynamicMeshElement>(object);
//
//		// update pointers
//		elem.prev = backs[bucketIndex];
//		if (backs[bucketIndex] != null)
//			backs[bucketIndex].next = elem;
//		backs[bucketIndex] = elem;
//		if (buckets[bucketIndex] == null)
//			buckets[bucketIndex] = elem;
//
//		// update max bucket index if needed
//		if (bucketIndex > maxBucket)
//			maxBucket = bucketIndex;
//
//		elem.bucketIndex = bucketIndex;
//
//		count++;
//
//		linkAssociations.put(object, elem);
//
//		return true;
//	}
//
//	public void recalculate(int currentVersion) {
//		LinkedList<DynamicMeshElement> list = new LinkedList<DynamicMeshElement>();
//		for (int i = 0; i <= maxBucket; i++) {
//			Link<DynamicMeshElement> el = buckets[i];
//			while (el != null) {
//				DynamicMeshElement d = el.data;
//				if(d.recalculate(currentVersion,true))
//					list.add(d);
//				el = el.next;
//			}
//		}
//		
//		Iterator<DynamicMeshElement> it = list.iterator();
//		while(it.hasNext()){
//			DynamicMeshElement a = it.next();
//			reinsert(a);
//		}
//	}
//
//	private void reinsert(DynamicMeshElement a) {
//		remove(a);
//		add(a);
//	}
//
//}