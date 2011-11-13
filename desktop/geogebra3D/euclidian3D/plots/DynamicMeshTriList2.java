package geogebra3D.euclidian3D.plots;

import java.nio.FloatBuffer;

/**
 * A triangle list for dynamic meshes
 * 
 * @author André Eriksson
 */
public interface DynamicMeshTriList2 {

	// /**
	// * @param capacity
	// * the maximum number of triangles
	// * @param margin
	// * free triangle amount before considered full
	// * @param trisInChunk
	// * amount of triangles in each chunk
	// */
	// protected DynamicMeshTriList2(int capacity, int margin, int trisInChunk)
	// {
	// super(capacity, margin, trisInChunk, true);
	// }

	/**
	 * @param e
	 *            the element to add
	 */
	abstract public void add(DynamicMeshElement2 e);

	/**
	 * @param e
	 *            the element to remove
	 * @param i
	 */
	abstract public void add(DynamicMeshElement2 e, int i);

	// /**
	// * @return the total visible error
	// */
	// abstract public double getError();

	/**
	 * @param e
	 *            the element to remove
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(DynamicMeshElement2 e);

	/**
	 * @param e
	 *            the element to remove
	 * @param i
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(DynamicMeshElement2 e, int i);

	/**
	 * @param t
	 *            the element to attempt to hide
	 * @return true if the element was hidden, otherwise false
	 */
	abstract public boolean hide(DynamicMeshElement2 t);

	/**
	 * @param t
	 *            the elemet to attempt to show
	 * @return true if the element was shown, otherwise false
	 */
	abstract public boolean show(DynamicMeshElement2 t);

	public void recalculate(int currentVersion);

	// TriListElem e = front;
	// LinkedList<DynamicMeshElement2> list = new
	// LinkedList<DynamicMeshElement2>();
	// DynamicMeshElement2 el;
	// while (e != null) {
	// el = (DynamicMeshElement2) e.getOwner();
	// if(el.lastVersion!=currentVersion)
	// list.add(el);
	// e=e.getNext();
	// }
	// Iterator<DynamicMeshElement2> it = list.iterator();
	// while(it.hasNext()){
	// DynamicMeshElement2 a = it.next();
	// reinsert(a,currentVersion);
	// }
	// }

	abstract void reinsert(DynamicMeshElement2 a, int currentVersion);

	public abstract FloatBuffer getTriangleBuffer();

	public abstract FloatBuffer getNormalBuffer();

	public abstract int getTriAmt();

	public abstract int getChunkAmt();
}
