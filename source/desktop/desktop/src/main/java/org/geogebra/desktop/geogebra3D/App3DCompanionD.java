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

package org.geogebra.desktop.geogebra3D;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.geogebra3D.euclidianForPlane.EuclidianControllerForPlaneD;
import org.geogebra.desktop.geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;
import org.geogebra.desktop.geogebra3D.gui.layout.panels.EuclidianDockPanelForPlaneD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;

/**
 * 
 * @author mathieu
 *
 *         Companion for 3D application in desktop
 */
public class App3DCompanionD extends App3DCompanion {

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 */
	public App3DCompanionD(App app) {
		super(app);
	}

	@Override
	protected EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, EuclidianSettings evSettings,
			boolean panelSettings) {
		EuclidianViewForPlaneD view = new EuclidianViewForPlaneD(
				new EuclidianControllerForPlaneD(app.getKernel()), plane,
				evSettings);

		// create dock panel
		panel = new EuclidianDockPanelForPlaneD((AppD) app, view, incViewID());

		view.setPanelID(panel.getViewId());

		((LayoutD) app.getGuiManager().getLayout()).registerPanel(panel);

		if (panelSettings) {
			panel.setFrameBounds(new Rectangle(600, 400));
			panel.setVisible(true);
			panel.toggleStyleBar();

			((LayoutD) app.getGuiManager().getLayout()).getDockManager()
					.show(panel);

		}

		return view.getCompanion();
	}

	private EuclidianDockPanelForPlaneD panel;

	@Override
	public DockPanelD getPanelForPlane() {
		return panel;
	}

	private ArrayList<EuclidianDockPanelForPlaneD> panelForPlaneList;

	@Override
	public void storeViewCreators() {

		if (panelForPlaneList == null) {
			panelForPlaneList = new ArrayList<>();
		} else {
			panelForPlaneList.clear();
		}
		if (app.getGuiManager() != null) {
			DockPanelD[] panels = ((LayoutD) app.getGuiManager().getLayout())
					.getDockManager().getPanels();
			for (int i = 0; i < panels.length; i++) {
				if (panels[i] instanceof EuclidianDockPanelForPlaneD) {
					panelForPlaneList
							.add((EuclidianDockPanelForPlaneD) panels[i]);
				}
			}
		}

	}

	@Override
	public void recallViewCreators() {

		for (EuclidianDockPanelForPlaneD p : panelForPlaneList) {
			EuclidianViewForPlaneD view = p.getView();
			GeoElement geo = app.getKernel()
					.lookupLabel(((GeoElement) view.getCompanion().getPlane())
							.getLabelSimple());
			if (geo != null && (geo instanceof ViewCreator)) {
				ViewCreator plane = (ViewCreator) geo;
				view.getCompanion().setPlane(plane);
				plane.setEuclidianViewForPlane(view.getCompanion());
				view.getCompanion().updateForPlane();
			} else {
				// no more creator : remove
				p.getView().getCompanion().doRemove();
			}
		}
	}

}
