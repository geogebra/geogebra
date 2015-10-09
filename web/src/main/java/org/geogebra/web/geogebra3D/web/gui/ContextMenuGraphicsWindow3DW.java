package org.geogebra.web.geogebra3D.web.gui;

import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.images.StyleBar3DResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author mathieu
 *
 */
public class ContextMenuGraphicsWindow3DW extends ContextMenuGraphicsWindowW {

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param px
	 *            mouse x
	 * @param py
	 *            mouse y
	 */
	public ContextMenuGraphicsWindow3DW(final AppW app, double px, double py) {
		super(app);

		this.px = px;
		this.py = py;

		setTitle(app.getPlain("GraphicsView3D"));

		addAxesAndGridCheckBoxes();

		addNavigationBar();

		MenuItem miStandardView = new MenuItem(app.getPlain("StandardView"),
		        new Command() {

			        public void execute() {
				        ((EuclidianView3DW) app.getEuclidianView3D())
				                .setStandardView(true);
			        }
		        });
		miStandardView.addStyleName("mi_no_image");
		wrappedPopup.addItem(miStandardView);

		MenuItem miShowAllObjectsView = new MenuItem(
		        app.getPlain("ShowAllObjects"), new Command() {

			        public void execute() {
				        setViewShowAllObject();
			        }

		        });
		miShowAllObjectsView.addStyleName("mi_no_image");
		wrappedPopup.addItem(miShowAllObjectsView);

		addMiProperties("GraphicsView3D");
	}

	@Override
	protected void addAxesAndGridCheckBoxes() {

		// checkboxes for axes and grid
		String htmlString = MainMenu.getMenuBarHtml(StyleBarResources.INSTANCE
		        .axes().getSafeUri().asString(), app.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowAxes3DAction(),
				true);
		cbShowAxes.setSelected(((EuclidianView3DW) app.getEuclidianView3D())
		        .axesAreAllVisible());
		getWrappedPopup().addItem(cbShowAxes);

		htmlString = MainMenu.getMenuBarHtml(StyleBarResources.INSTANCE.grid()
		        .getSafeUri().asString(), app.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowGrid3DAction(),
				true);
		cbShowGrid.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
		        .isGridVisible());
		getWrappedPopup().addItem(cbShowGrid);

		htmlString = MainMenu.getMenuBarHtml(StyleBar3DResources.INSTANCE
		        .plane().getSafeUri().asString(), app.getMenu("Plane"));
		GCheckBoxMenuItem cbShowPlane = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowPlane3DAction(),
				true);
		cbShowPlane.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
		        .isPlateVisible());
		getWrappedPopup().addItem(cbShowPlane);

	}

}
