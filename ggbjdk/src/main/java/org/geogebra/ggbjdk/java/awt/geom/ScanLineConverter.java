package org.geogebra.ggbjdk.java.awt.geom;


public interface ScanLineConverter {

	/**
	 * Add a shape to the scanline converter.
	 * 
	 * @param path
	 */
	public abstract void addShape(PathIterator path);

}