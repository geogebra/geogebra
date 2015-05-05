package org.geogebra.common.geogebra3D.euclidian3D.plots;

import java.util.Iterator;

import org.geogebra.common.geogebra3D.euclidian3D.plots.java.nio.FloatBuffer;

/**
 * A list of triangles representing a triangle mesh.
 * 
 * @author Andre Eriksson
 */
public class TriList implements Iterable<TriListElem> {
	/** the total amount of chunks available for allocation */
	private int capacity;

	/** the amount of floats in each chunk */
	private final int chunkSize;

	/** current chunk amt */
	private int count = 0;

	private final int margin;

	/**
	 * A buffer containing data for all the triangles. Each triangle is stored
	 * as 9 consecutive floats (representing x/y/z values for three points). The
	 * triangles are packed tightly.
	 */
	private FloatBuffer vertexBuf;
	/** A counterpart to tribuf containing normals */
	private FloatBuffer normalBuf;

	/** Pointer to the front of the queue */
	protected TriListElem front;
	/** Pointer to the back of the queue */
	protected TriListElem back;

	/** true if the TriList size should be dynamic */
	private boolean dynamicSize;

	/** multiplication factor for the amount of elements when expanding */
	private int sizeMultiplier = 2;

	/**
	 * @param capacity
	 *            the maximum number of triangles (initial amount if dynamicSize
	 *            is true)
	 * @param margin
	 *            free triangle amount before considered full
	 * @param floatsInChunk
	 *            amount of floats in each chunk
	 * @param dynamicSize
	 *            true if the size should be dynamic
	 */
	public TriList(int capacity, int margin, int floatsInChunk,
			boolean dynamicSize) {
		this.capacity = capacity;
		this.chunkSize = floatsInChunk;
		this.margin = margin;
		this.dynamicSize = dynamicSize;
		vertexBuf = FloatBuffer.allocate((capacity + margin) * chunkSize);
		normalBuf = FloatBuffer.allocate((capacity + margin) * chunkSize);
	}

	/**
	 * Allocates new, larger buffers and copies all elements.
	 */
	private void expand() {
		capacity *= sizeMultiplier;
		FloatBuffer verts = FloatBuffer.allocate((capacity + margin)
				* chunkSize);
		FloatBuffer norms = FloatBuffer.allocate((capacity + margin)
				* chunkSize);
		vertexBuf.rewind();
		normalBuf.rewind();
		verts.put(vertexBuf);
		norms.put(normalBuf);
		vertexBuf = verts;
		normalBuf = norms;
	}

	/**
	 * @return the current amount of triangles. this number will be incorrect if
	 *         triangle strips are used
	 */
	public int getTriAmt() {
		return count * (chunkSize / 9);
	}

	/**
	 * @return the current amount of chunks
	 */
	public int getChunkAmt() {
		return count;
	}

	/**
	 * @return a reference to vertexBuf
	 */
	public FloatBuffer getTriangleBuffer() {
		return vertexBuf;
	}

	/**
	 * @return a reference to normalBuf
	 */
	public FloatBuffer getNormalBuffer() {
		return normalBuf;
	}

	/**
	 * @return true if count>=maxCount - otherwise false.
	 */
	public boolean isFull() {
		return count >= capacity - margin;
	}

	/**
	 * sets elements in the float buffers to the provided values
	 * 
	 * @param vertices
	 *            9 floats representing 3 vertices
	 * @param normals
	 *            9 floats representing 3 normals
	 * @param index
	 *            the index of the first float to be changed
	 */
	protected void setFloats(float[] vertices, float[] normals, int index) {
		if (dynamicSize
				&& index + vertices.length >= (capacity + margin) * chunkSize)
			expand();

		vertexBuf.position(index);
		vertexBuf.put(vertices);
		normalBuf.position(index);
		normalBuf.put(normals);
	}

	/**
	 * gets the vertices of an element
	 * 
	 * @param el
	 *            the element
	 * @return the vertices of the element
	 */
	protected float[] getVertices(TriListElem el) {
		return getVertices(el.getIndex());
	}

	/**
	 * gets the vertices of an element
	 * 
	 * @param el
	 *            the element
	 * @return the vertices of the element
	 */
	protected float[] getNormals(TriListElem el) {
		return getNormals(el.getIndex());
	}

	/**
	 * sets the vertices of the specified element
	 * 
	 * @param el
	 *            the element to set
	 * @param vertices
	 *            the new vertices
	 */
	protected void setVertices(TriListElem el, float[] vertices) {
		vertexBuf.position(el.getIndex());
		vertexBuf.put(vertices);
	}

	/**
	 * sets the normals of the specified element
	 * 
	 * @param el
	 *            the element to set
	 * @param normals
	 *            the new normals
	 */
	protected void setNormals(TriListElem el, float[] normals) {
		normalBuf.position(el.getIndex());
		normalBuf.put(normals);
	}

	/**
	 * @param index
	 * @return float array of vertices (chunkSize floats)
	 */
	protected float[] getVertices(int index) {
		float[] vertices = new float[chunkSize];
		vertexBuf.position(index);
		vertexBuf.get(vertices);
		return vertices;
	}

	/**
	 * @param index
	 * @return float array of normals (chunkSize floats)
	 */
	protected float[] getNormals(int index) {
		float[] normals = new float[chunkSize];
		normalBuf.position(index);
		normalBuf.get(normals);
		return normals;
	}

	/**
	 * Adds a triangle to the list.
	 * 
	 * @param vertices
	 *            the tree vertices in the triangle stored as (chunkSize) floats
	 * @param normals
	 *            the normals of the vertices stored as (chunkSize) floats
	 * @return a reference to the created triangle element
	 */
	public TriListElem add(float[] vertices, float[] normals) {

		TriListElem t = new TriListElem();
		t.setPrev(back);
		if (front == null)
			front = t;
		if (back != null)
			back.setNext(t);
		back = t;

		int index = chunkSize * count;

		setFloats(vertices, normals, index);

		t.setIndex(index);

		count++;

		return t;
	}

	/**
	 * Adds a triangle to the list.
	 * 
	 * @param vertices
	 *            the tree vertices in the triangle stored as (chunkSize) floats
	 * @param normals
	 *            the normals of the vertices stored as (chunkSize) floats
	 * @return a reference to the created triangle element
	 */
	public void add(TriListElem t, float[] vertices, float[] normals) {

		t.setPrev(back);
		if (front == null)
			front = t;
		if (back != null)
			back.setNext(t);
		back = t;

		int index = chunkSize * count;

		setFloats(vertices, normals, index);

		t.setIndex(index);

		count++;
	}

	private boolean inputValid(float[] vertices, float[] normals) {
		for (int i = 0; i < chunkSize; i++)
			if (Double.isInfinite(vertices[i]) || Double.isNaN(vertices[i])
					|| Double.isInfinite(normals[i])
					|| Double.isNaN(normals[i]))
				return false;
		return true;

	}

	/**
	 * transfers nine consecutive floats from one place in the buffers to
	 * another
	 * 
	 * @param oldIndex
	 *            the old index of the first float
	 * @param newIndex
	 *            the new index of the first float
	 */
	protected void transferFloats(int oldIndex, int newIndex) {
		float[] f = new float[chunkSize];
		float[] g = new float[chunkSize];

		vertexBuf.position(oldIndex);
		vertexBuf.get(f);
		vertexBuf.position(newIndex);

		normalBuf.position(oldIndex);
		normalBuf.get(g);
		normalBuf.position(newIndex);

		for (int i = 0; i < chunkSize; i++) {
			vertexBuf.put(f[i]);
			normalBuf.put(g[i]);
		}
	}

	/**
	 * Tests the list for consistency.
	 */
	protected void consistencyCheck() {
		int i;
		TriListElem o = front;
		for (i = 0; o != back; i++) {
			try {
				if (!o.getNext().getPrev().equals(o))
					System.err.println("Error in TriangleList: invalid order");
				o = o.getNext();
			} catch (NullPointerException e) {
				System.err.println(e);
			}
		}
		if (i != (count - 1 < 0 ? 0 : count - 1))
			System.err.println("Error in TriangleList: invalid count");
	}

	/**
	 * Removes a triangle from the queue.
	 * 
	 * @param t
	 */
	public boolean remove(TriListElem t) {
		return hide(t);
	}

	/**
	 * removes a chunk from the list, but does not erase it
	 * 
	 * @param t
	 *            any chunk in the list
	 * @return false if the chunk is null or already hidden, otherwise true
	 */
	public boolean hide(TriListElem t) {
		if (t == null || t.getIndex() == -1)
			return false;

		t.pushVertices(getVertices(t.getIndex()));
		t.pushNormals(getNormals(t.getIndex()));

		// swap back for current position
		int n = t.getIndex();
		if (count == 1) {
			back = front = null;
		} else if (t == back) {
			// update pointers
			back = t.getPrev();
			back.setNext(null);
		} else if (t == back.getPrev()) {
			// transfer prevBack's floats to new position
			transferFloats(back.getIndex(), n);
			back.setIndex(n);

			TriListElem prev = t.getPrev();
			// update pointers
			back.setPrev(prev);
			if (prev != null)
				prev.setNext(back);

			if (front == t)
				front = back;
		} else {
			// transfer prevBack's floats to new position
			transferFloats(back.getIndex(), n);
			back.setIndex(n);

			// update pointers
			TriListElem prevBack = back;

			back = prevBack.getPrev();
			back.setNext(null);

			TriListElem next = t.getNext();
			TriListElem prev = t.getPrev();

			prevBack.setNext(next);
			prevBack.setPrev(prev);

			if (prev != null)
				prev.setNext(prevBack);
			next.setPrev(prevBack);

			if (front == t)
				front = prevBack;
		}

		t.setIndex(-1);
		t.setNext(null);
		t.setPrev(null);

		count--;
		return true;
	}

	public Iterator<TriListElem> iterator() {

		return new Iterator<TriListElem>() {
			private TriListElem el = front;
			private int bucket = 0;

			public boolean hasNext() {
				return el.getNext() != null;
			}

			public TriListElem next() {
				el = el.getNext();
				return el;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * shows a triangle that has been hidden
	 * 
	 * @param t
	 *            any hidden triangle in the list
	 * @return false if the triangle is null or already visible, otherwise true
	 */
	public boolean show(TriListElem t) {

		if (t == null || t.getIndex() != -1)
			return false;

		if (front == null)
			front = t;
		if (back != null) {
			back.setNext(t);
			t.setPrev(back);
		}
		back = t;

		setFloats(t.popVertices(), t.popNormals(), chunkSize * count);

		t.setIndex(chunkSize * count);

		count++;

		return true;
	}
}