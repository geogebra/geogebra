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

package org.geogebra.common.euclidianForPlane;

/**
 * interface for EuclidianViewForPlaneCompanion
 * 
 */
public interface EuclidianViewForPlaneCompanionInterface {

	/**
	 * 
	 * @return view id
	 */
	int getId();

	/**
	 * set transformation regarding 3D view
	 */
	void setTransformRegardingView();

	/**
	 * remove the view when the creator doesn't exist anymore
	 */
	void doRemove();

	/**
	 * update the view
	 */
	void updateForPlane();

	/**
	 * update the matrix
	 */
	void updateMatrix();

	/**
	 * update all drawables
	 * 
	 * @param repaint
	 *            says if repaint is needed
	 */
	void updateAllDrawables(boolean repaint);

}
