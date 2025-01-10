package org.geogebra.web.geogebra3D.web.gui;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import org.geogebra.web.geogebra3D.web.gui.view.properties.PropertiesView3DW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.Command;

/**
 * web gui manager for 3D
 * 
 * @author mathieu
 *
 */
public class GuiManager3DW extends GuiManagerW {

	private DockPanelW euclidian3Dpanel;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param device
	 *            device (browser / tablet)
	 */
	public GuiManager3DW(AppW app, GDevice device) {
		super(app, device);
	}

	@Override
	protected boolean initLayoutPanels() {

		if (super.initLayoutPanels()) {
			this.euclidian3Dpanel = new EuclidianDockPanel3DW(getApp());
			layout.registerPanel(this.euclidian3Dpanel);
			return true;
		}

		return false;

	}

	@Override
	public DockPanelW getEuclidian3DPanel() {
		return this.euclidian3Dpanel;
	}

	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view, GPoint p) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu3D(p.x, p.y).showScaled(
				((EuclidianView3DW) view).getG2P().getElement(), p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu3D(int x, int y) {
		currentPopup = new ContextMenuGraphicsWindow3DW(getApp(), x, y);
		return (ContextMenuGeoElementW) currentPopup;
	}

	/**
	 * 
	 * @return command to show/hide 3D axis
	 */
	public Command getShowAxes3DAction() {
		return () -> {
			// toggle axes
			((EuclidianView3DW) getApp().getEuclidianView3D()).toggleAxis();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D grid
	 */
	public Command getShowGrid3DAction() {
		return () -> {
			// toggle grid
			((EuclidianView3DW) getApp().getEuclidianView3D()).toggleGrid();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D plane
	 */
	public Command getShowPlane3DAction() {
		return () -> {
			// toggle plane
			((EuclidianView3DW) getApp().getEuclidianView3D())
					.getSettings().togglePlane();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	@Override
	protected PropertiesViewW newPropertiesViewW(AppW app1, OptionType optionType) {
		return new PropertiesView3DW(app1, optionType);
	}

}
