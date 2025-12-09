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

package org.geogebra.desktop.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.EuclidianViewD;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author Mathieu
 * 
 */
public class EuclidianViewFor3DD extends EuclidianViewD {

	/**
	 * @param ec
	 *            controller
	 * @param showAxes
	 *            show the axes
	 * @param showGrid
	 *            shos the grid
	 * @param evno
	 *            dock panel id
	 * @param settings
	 *            euclidian settings
	 */
	public EuclidianViewFor3DD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {
		super(ec, showAxes, showGrid, evno, settings);

	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewFor3DCompanion(this);
	}

}
