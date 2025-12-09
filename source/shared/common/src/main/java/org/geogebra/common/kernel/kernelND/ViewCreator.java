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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneCompanionInterface;

/**
 * Geo that can create a new view (e.g. planes, polygons)
 * 
 * @author mathieu
 *
 */
public interface ViewCreator extends GeoCoordSys2D {

	/** create a 2D view about this coord sys */
	public void createView2D();

	/** remove the 2D view */
	public void removeView2D();

	/**
	 * set the euclidian view created
	 * 
	 * @param view
	 *            view
	 */
	public void setEuclidianViewForPlane(
			EuclidianViewForPlaneCompanionInterface view);

	/**
	 * tells if the view2D is visible
	 * 
	 * @return true if the view2D is visible
	 */
	public boolean hasView2DVisible();

	/**
	 * sets the view 2D visibility
	 * 
	 * @param flag
	 *            visibility
	 */
	public void setView2DVisible(boolean flag);

	/**
	 * 
	 * @return view id (if has a view)
	 */
	public int getViewID();

}
