//package geogebra3D.euclidian3D.plots;
//
//import geogebra3D.euclidian3D.TriList;
//import geogebra3D.euclidian3D.TriListElem;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//
///**
// * A triangle list for dynamic meshes
// * 
// * @author André Eriksson
// */
//abstract class DynamicMeshTriList extends TriList {
//
//	/**
//	 * @param capacity
//	 *            the maximum number of triangles
//	 * @param margin
//	 *            free triangle amount before considered full
//	 * @param trisInChunk
//	 *            amount of triangles in each chunk
//	 */
//	DynamicMeshTriList(int capacity, int margin, int trisInChunk) {
//		super(capacity, margin, trisInChunk, true);
//	}
//
//	/**
//	 * @param e
//	 *            the element to add
//	 */
//	abstract public void add(DynamicMeshElement e);
//
//	/**
//	 * @param e
//	 *            the element to remove
//	 * @param i
//	 */
//	abstract public void add(DynamicMeshElement e, int i);
//
////	/**
////	 * @return the total visible error
////	 */
////	abstract public double getError();
//
//	/**
//	 * @param e
//	 *            the element to remove
//	 * @return true if the element was removed, otherwise false
//	 */
//	abstract public boolean remove(DynamicMeshElement e);
//
//	/**
//	 * @param e
//	 *            the element to remove
//	 * @param i
//	 * @return true if the element was removed, otherwise false
//	 */
//	abstract public boolean remove(DynamicMeshElement e, int i);
//
//	/**
//	 * @param t
//	 *            the element to attempt to hide
//	 * @return true if the element was hidden, otherwise false
//	 */
//	abstract public boolean hide(DynamicMeshElement t);
//
//	/**
//	 * @param t
//	 *            the elemet to attempt to show
//	 * @return true if the element was shown, otherwise false
//	 */
//	abstract public boolean show(DynamicMeshElement t);
//
//	public void recalculate(int currentVersion) {
//		TriListElem e = front;
//		LinkedList<DynamicMeshElement> list = new LinkedList<DynamicMeshElement>();
//		DynamicMeshElement el;
//		int j = 0;
//		while (e != null) {
//			el = (DynamicMeshElement) e.getOwner();
//			if(el.lastVersion!=currentVersion)
//				list.add(el);
//			e=e.getNext();
//		}
//		Iterator<DynamicMeshElement> it = list.iterator();
//		while(it.hasNext()){
//			DynamicMeshElement a = it.next();
//			reinsert(a,currentVersion);
//		}
//	}
//
//	abstract protected void reinsert(DynamicMeshElement a, int currentVersion);
//}
