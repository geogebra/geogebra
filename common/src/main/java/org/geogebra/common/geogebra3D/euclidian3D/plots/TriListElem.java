package org.geogebra.common.geogebra3D.euclidian3D.plots;

/**
 * A class representing a triangle in TriList
 * 
 * @author Andre Eriksson
 */
public class TriListElem {
	protected int index;
	protected TriListElem next, prev;

	protected float[] vertices;
	protected float[] normals;

	public char flags = 0;

	public TriListElem() {
	}

	public TriListElem(char flags) {
		this.flags = flags;
	}

	/** an (optional) reference to the object associated with the element */
	protected Object owner;

	/**
	 * saves the specified vertices
	 * 
	 * @param vertices
	 *            floats representing the vertices of the chunk
	 */
	public void pushVertices(float[] vertices) {
		this.vertices = vertices;
	}

	/**
	 * sets the owner associated with the element
	 * 
	 * @param owner
	 */
	public void setOwner(Object owner) {
		this.owner = owner;
	}

	/**
	 * @return the owner associated with the element
	 */
	public Object getOwner() {
		return owner;
	}

	/**
	 * saves the specified normals
	 * 
	 * @param normals
	 *            floats representing the normals of the chunk
	 */
	public void pushNormals(float[] normals) {
		this.normals = normals;
	}

	/**
	 * removes and returns any saved vertices
	 * 
	 * @return the contents of vertices
	 */
	public float[] popVertices() {
		float[] temp = vertices;
		vertices = null;
		return temp;
	}

	/**
	 * removes and returns any saved normals
	 * 
	 * @return the contents of normals
	 */
	public float[] popNormals() {
		float[] temp = normals;
		normals = null;
		return temp;
	}

	/**
	 * Sets the triangle's index in the float buffer.
	 * 
	 * @param i
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * @return the triangle's index in the float buffer.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return a reference to the next triangle in the queue.
	 */
	public TriListElem getNext() {
		return next;
	}

	/**
	 * @param next
	 *            a reference to the next triangle in the queue.
	 */
	public void setNext(TriListElem next) {
		this.next = next;
	}

	/**
	 * @return a reference to the previous triangle in the queue.
	 */
	public TriListElem getPrev() {
		return prev;
	}

	/**
	 * @param prev
	 *            a reference to the previous triangle in the queue.
	 */
	public void setPrev(TriListElem prev) {
		this.prev = prev;
	}
}