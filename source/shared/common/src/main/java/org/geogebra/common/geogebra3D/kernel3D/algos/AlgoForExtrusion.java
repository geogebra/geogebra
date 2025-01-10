package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for algos used for extrusion
 * 
 * @author matthieu
 *
 */
public interface AlgoForExtrusion {

	/**
	 * AlgoElement.compute()
	 */
	public void compute();

	/**
	 * AlgoElement.remove()
	 */
	public void remove();

	/**
	 * AlgoElement.getOutput(int i)
	 * 
	 * @param i
	 *            index
	 * @return output
	 */
	public GeoElement getOutput(int i);

	/**
	 * sets the extrusion computer
	 * 
	 * @param extrusionComputer
	 *            extrusion computer
	 */
	public void setExtrusionComputer(ExtrusionComputer extrusionComputer);

	/**
	 * AlgoElement.removeOutputFromAlgebraView()
	 */
	public void removeOutputFromAlgebraView();

	/**
	 * AlgoElement.removeOutputFromPicking()
	 */
	public void removeOutputFromPicking();

	/**
	 * set output points invisible (use for previewable)
	 * 
	 * @param b
	 *            flag
	 */
	public void setOutputPointsEuclidianVisible(boolean b);

	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints();

	/**
	 * 
	 * @return top face
	 */
	public GeoElement getGeoToHandle();

	/**
	 * set visibility of output other than points
	 * 
	 * @param b
	 *            flag
	 */
	public void setOutputOtherEuclidianVisible(boolean b);

	/**
	 * notify kernel update of output other than points
	 */
	public void notifyUpdateOutputOther();

}
