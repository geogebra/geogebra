package geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * interface for openGL buffers
 * @author mathieu
 *
 */
public interface GLBuffer{
	
	/**
	 * 
	 * @return current float
	 */
	public Float get();

	/**
	 * rewind the buffer
	 */
	public void rewind();

	/**
	 * 
	 * @return capacity
	 */
	public int capacity();

	/**
	 * 
	 * @return float array
	 */
	public float[] array();

}
