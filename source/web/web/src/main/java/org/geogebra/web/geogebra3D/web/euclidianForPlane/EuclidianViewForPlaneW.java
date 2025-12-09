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

package org.geogebra.web.geogebra3D.web.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.gui.layout.panels.EuclidianDockPanelForPlaneW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.gwtproject.user.client.ui.Widget;

/**
 * 2D view for plane.
 * 
 * @author Mathieu
 *
 */
public class EuclidianViewForPlaneW extends EuclidianViewW implements
        EuclidianViewForPlaneInterface {

	private int panelID;

	/**
	 * 
	 * @param euclidianViewPanel
	 *            view panel
	 * @param ec
	 *            controller
	 * @param plane
	 *            plane creating this view
	 * @param settings
	 *            euclidian settings
	 * @param panelID
	 *            id of the view
	 */
	public EuclidianViewForPlaneW(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController ec, ViewCreator plane,
	        EuclidianSettings settings, int panelID) {
		super(euclidianViewPanel, ec, EVNO_GENERAL, settings);
		this.panelID = panelID;
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

	/**
	 * @return panel component
	 */
	public Widget getComponent() {
		return getAbsolutePanel();
	}

	// @Override
	// public final void repaint() {
	//
	// // temporary hack : use timer instead
	// doRepaint();
	// }

	/**
	 * 
	 * @return dock panel
	 */
	@Override
	public EuclidianDockPanelForPlaneW getDockPanel() {
		return (EuclidianDockPanelForPlaneW) evPanel;
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		if (getApplication().getGuiManager() == null) {
			return null;
		}
		return new EuclidianStyleBarForPlaneW(this, panelID);
	}

	@Override
	public int getViewID() {
		return panelID;
	}

}
