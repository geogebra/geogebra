/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	void compute();

	/**
	 * AlgoElement.remove()
	 */
	void remove();

	/**
	 * AlgoElement.getOutput(int i)
	 * 
	 * @param i
	 *            index
	 * @return output
	 */
	GeoElement getOutput(int i);

	/**
	 * sets the extrusion computer
	 * 
	 * @param extrusionComputer
	 *            extrusion computer
	 */
	void setExtrusionComputer(ExtrusionComputer extrusionComputer);

	/**
	 * AlgoElement.removeOutputFromAlgebraView()
	 */
	void removeOutputFromAlgebraView();

	/**
	 * AlgoElement.removeOutputFromPicking()
	 */
	void removeOutputFromPicking();

	/**
	 * set output points invisible (use for previewable)
	 * 
	 * @param b
	 *            flag
	 */
	void setOutputPointsEuclidianVisible(boolean b);

	/**
	 * notify kernel update of output points
	 */
	void notifyUpdateOutputPoints();

	/**
	 * 
	 * @return top face
	 */
	GeoElement getGeoToHandle();

	/**
	 * set visibility of output other than points
	 * 
	 * @param b
	 *            flag
	 */
	void setOutputOtherEuclidianVisible(boolean b);

	/**
	 * notify kernel update of output other than points
	 */
	void notifyUpdateOutputOther();

}
