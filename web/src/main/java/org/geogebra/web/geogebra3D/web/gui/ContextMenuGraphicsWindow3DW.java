package org.geogebra.web.geogebra3D.web.gui;

import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.images.StyleBar3DResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.web.gui.images.AppResources;
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

		setTitle(loc.getMenu("GraphicsView3D"));


		addPasteItem();

		addAxesAndGridCheckBoxes();

		addNavigationBar();


		addZoomMenu();

		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.standard_view20().getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		MenuItem miStandardView = new MenuItem(MainMenu.getMenuBarHtml(img, loc.getMenu("StandardView")), true,
				new Command() {

			        @Override
					public void execute() {
				        ((EuclidianView3DW) app.getEuclidianView3D())
				                .setStandardView(true);
			        }
		        });

		wrappedPopup.addItem(miStandardView);

		String img2;
		if (isWhiteboard()) {
			img2 = AppResources.INSTANCE.show_all_objects20().getSafeUri().asString();
		} else {
			img2 = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		MenuItem miShowAllObjectsView = new MenuItem(MainMenu.getMenuBarHtml(img2, loc.getMenu("ShowAllObjects")), true,
				new Command() {

			        @Override
					public void execute() {
						setViewShowAllObject();
			        }

		        });

		wrappedPopup.addItem(miShowAllObjectsView);

		addMiProperties("GraphicsView3D", OptionType.EUCLIDIAN3D);
	}

	@Override
	protected void addAxesAndGridCheckBoxes() {

		// checkboxes for axes and grid
		// AXES
		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.axes20().getSafeUri().asString();
		} else {
			img = StyleBarResources.INSTANCE.axes().getSafeUri().asString();
		}
		String htmlString = MainMenu.getMenuBarHtml(img, loc.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowAxes3DAction(),
				true, app);
		cbShowAxes.setSelected(((EuclidianView3DW) app.getEuclidianView3D())
		        .axesAreAllVisible());
		getWrappedPopup().addItem(cbShowAxes);

		// GRID
		String img2;
		if (isWhiteboard()) {
			img2 = AppResources.INSTANCE.grid20().getSafeUri().asString();
		} else {
			img2 = StyleBarResources.INSTANCE.grid().getSafeUri().asString();
		}
		htmlString = MainMenu.getMenuBarHtml(img2, loc.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowGrid3DAction(),
				true, app);
		cbShowGrid.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
		        .isGridVisible());
		getWrappedPopup().addItem(cbShowGrid);

		// PLANE
		String img3;
		if (isWhiteboard()) {
			img3 = AppResources.INSTANCE.plane20().getSafeUri().asString();
		} else {
			img3 = StyleBar3DResources.INSTANCE.plane().getSafeUri().asString();
		}
		htmlString = MainMenu.getMenuBarHtml(img3, loc.getMenu("Plane"));
		GCheckBoxMenuItem cbShowPlane = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowPlane3DAction(),
				true, app);
		cbShowPlane.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
		        .isPlateVisible());
		getWrappedPopup().addItem(cbShowPlane);

	}

	@Override
	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
		if (app.getActiveEuclidianView().isEuclidianView3D()) {
			((EuclidianView3DW) app.getActiveEuclidianView()).doRepaint();
		}
	}

}
