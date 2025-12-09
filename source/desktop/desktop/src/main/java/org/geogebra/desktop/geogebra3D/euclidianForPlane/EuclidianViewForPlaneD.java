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

package org.geogebra.desktop.geogebra3D.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.EuclidianStyleBarD;
import org.geogebra.desktop.euclidian.EuclidianViewD;

/**
 * 2D view for plane.
 * 
 * @author Mathieu
 *
 */
public class EuclidianViewForPlaneD extends EuclidianViewD
		implements EuclidianViewForPlaneInterface {

	/**
	 * 
	 * @param ec
	 *            controller
	 * @param plane
	 *            plane creating this view
	 * @param settings
	 *            euclidian settings
	 */
	public EuclidianViewForPlaneD(EuclidianController ec, ViewCreator plane,
			EuclidianSettings settings) {
		super(ec, new boolean[] { false, false }, false, EVNO_GENERAL,
				settings); // TODO
							// euclidian
							// settings

		((EuclidianViewForPlaneCompanion) companion).initView(plane);
	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewForPlaneCompanion(this);
	}

	@Override
	public EuclidianViewForPlaneCompanion getCompanion() {
		return (EuclidianViewForPlaneCompanion) super.getCompanion();
	}

	@Override
	protected EuclidianStyleBarD newEuclidianStyleBar() {
		return new EuclidianStyleBarForPlaneD(this);
	}

	@Override
	public int getViewID() {
		return panelID;
	}

	private int panelID;

	/**
	 * set panel id
	 * 
	 * @param panelID
	 *            panel id
	 */
	public void setPanelID(int panelID) {
		this.panelID = panelID;
	}

}
