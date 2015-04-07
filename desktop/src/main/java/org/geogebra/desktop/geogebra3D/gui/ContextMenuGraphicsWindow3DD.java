package org.geogebra.desktop.geogebra3D.gui;

import javax.swing.JCheckBoxMenuItem;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.gui.ContextMenuGraphicsWindowD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

/**
 * Extending ContextMenuGraphicsWindow class for 3D
 * 
 * @author matthieu
 *
 */
public class ContextMenuGraphicsWindow3DD extends ContextMenuGraphicsWindowD {
	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 * 
	 * @param app
	 * @param px
	 * @param py
	 */
	public ContextMenuGraphicsWindow3DD(AppD app, double px, double py) {
		super(app);

		setTitle("<html>" + app.getPlain("GraphicsView3D") + "</html>");

		addAxesAndGridCheckBoxes();

		getWrappedPopup().addSeparator();

		addShowAllObjectsView(app);

		addStandardViewItem();

		getWrappedPopup().addSeparator();

		addMiProperties();

	}

	@Override
	protected void setStandardView() {
		((EuclidianView3D) app.getEuclidianView3D()).setStandardView(true);
	}

	@Override
	protected void addAxesAndGridCheckBoxes() {

		// checkboxes for axes and grid
		JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(
				((GuiManager3D) app.getGuiManager()).getShowAxes3DAction());
		// cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
		((App3D) app).setShowAxesSelected3D(cbShowAxes);
		cbShowAxes.setBackground(getWrappedPopup().getBackground());
		getWrappedPopup().add(cbShowAxes);

		JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(
				((GuiManager3D) app.getGuiManager()).getShowGrid3DAction());
		// cbShowGrid.setSelected(ev.getShowGrid());
		((App3D) app).setShowGridSelected3D(cbShowGrid);
		cbShowGrid.setBackground(getWrappedPopup().getBackground());
		getWrappedPopup().add(cbShowGrid);

		JCheckBoxMenuItem cbShowPlane = new JCheckBoxMenuItem(
				((GuiManager3D) app.getGuiManager()).getShowPlaneAction());
		((App3D) app).setShowPlaneSelected(cbShowPlane);
		cbShowPlane.setBackground(getWrappedPopup().getBackground());
		getWrappedPopup().add(cbShowPlane);
	}

	@Override
	protected void showOptionsDialog() {
		app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
		((GuiManagerD) app.getGuiManager()).setFocusedPanel(((App3D) app)
				.getEuclidianView3D().getViewID(), true);

	}

}
