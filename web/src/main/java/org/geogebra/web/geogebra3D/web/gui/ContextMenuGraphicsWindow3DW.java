package org.geogebra.web.geogebra3D.web.gui;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.StyleBarResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.full.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GCollapseMenuItem;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.images.StyleBar3DResources;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.Command;

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
		buildGUI();
	}

	private void buildGUI() {
		if (app.isUnbundled()) {
			buildGUI3DUnbundled();
			return;
		}

		addAxesAndGridCheckBoxes();
		addNavigationBar();
		addZoomMenu();
		addShowAllObjectsViewMenuItem();
		addStandardViewMenuItem();
		addMiProperties("GraphicsView3D", OptionType.EUCLIDIAN3D);
	}

	private void buildGUI3DUnbundled() {
		super.addAxesMenuItem(3);
		addPlaneMenuItem();
		addGridMenuItem();
		addProjectionMenuItem();
		super.addSnapToGridMenuItem();
		addShowAllObjectsViewMenuItem();
		addMiProperties("GraphicsView3D", OptionType.EUCLIDIAN3D);
	}

	private void addProjectionMenuItem() {
		String htmlString = MainMenu
				.getMenuBarHtmlClassic(
						MaterialDesignResources.INSTANCE
								.projection_orthographic()
								.getSafeUri().asString(),
						loc.getMenu("Projection"));
		final GCollapseMenuItem ci = new GCollapseMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, wrappedPopup);
		wrappedPopup.addItem(ci.getMenuItem(), false);
		ProjectionSubmenu projSubMenu = new ProjectionSubmenu(ci);
		projSubMenu.update();
		ci.attachToParent();
	}

	private void addPlaneMenuItem() {
		String img = MaterialDesignResources.INSTANCE.plane_black().getSafeUri()
				.asString();
		final GCheckmarkMenuItem showPlane = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("ShowPlane")),
				MaterialDesignResources.INSTANCE.check_black(),
				((Kernel3D) app.getKernel()).getXOYPlane().isPlateVisible());
		showPlane.setCommand(
				((GuiManager3DW) app.getGuiManager()).getShowPlane3DAction());
		showPlane.setChecked(
				((Kernel3D) app.getKernel()).getXOYPlane()
						.isPlateVisible());
		wrappedPopup.addItem(showPlane);
	}

	private void addGridMenuItem() {
		String img = MaterialDesignResources.INSTANCE.grid_black().getSafeUri()
				.asString();
		final GCheckmarkMenuItem showGrid = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("ShowGrid")),
				MaterialDesignResources.INSTANCE.check_black(),
				((Kernel3D) app.getKernel()).getXOYPlane().isGridVisible());
		showGrid.setCommand(
				((GuiManager3DW) app.getGuiManager()).getShowGrid3DAction());
		showGrid.setChecked(
				((Kernel3D) app.getKernel()).getXOYPlane().isGridVisible());
		wrappedPopup.addItem(showGrid);
	}

	private void addStandardViewMenuItem() {
		String img;
		if (app.isWhiteboardActive()) {
			img = AppResources.INSTANCE.standard_view20().getSafeUri()
					.asString();
		} else if (app.isUnbundled()) {
			img = MaterialDesignResources.INSTANCE.home_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}
		AriaMenuItem miStandardView = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("StandardView")),
				true,
				new Command() {

			        @Override
					public void execute() {
						((EuclidianView3DW) app.getEuclidianView3D())
								.setStandardView(true);
			        }
		        });
		wrappedPopup.addItem(miStandardView);
	}

	private void addShowAllObjectsViewMenuItem() {
		String img;
		if (app.isWhiteboardActive()) {
			img = AppResources.INSTANCE.show_all_objects20().getSafeUri()
					.asString();
		} else if (app.isUnbundled()) {
			img = MaterialDesignResources.INSTANCE.show_all_objects_black()
					.getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}
		AriaMenuItem miShowAllObjectsView = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("ShowAllObjects")),
				true,
				new Command() {

			        @Override
					public void execute() {
						setViewShowAllObject();
			        }

		        });
		wrappedPopup.addItem(miShowAllObjectsView);
	}

	@Override
	protected void addAxesAndGridCheckBoxes() {
		// AXES
		String img;
		if (app.isUnbundledOrWhiteboard()) {
			img = AppResources.INSTANCE.axes20().getSafeUri().asString();
		} else {
			img = StyleBarResources.INSTANCE.axes().getSafeUri().asString();
		}
		String htmlString = MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowAxes3DAction(),
				true, app);
		cbShowAxes.setSelected(((EuclidianView3DW) app.getEuclidianView3D())
				.axesAreAllVisible(), wrappedPopup.getPopupMenu());
		getWrappedPopup().addItem(cbShowAxes);

		// GRID
		String img2;
		if (app.isUnbundledOrWhiteboard()) {
			img2 = AppResources.INSTANCE.grid20().getSafeUri().asString();
		} else {
			img2 = StyleBarResources.INSTANCE.grid().getSafeUri().asString();
		}
		htmlString = MainMenu.getMenuBarHtmlClassic(img2, loc.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowGrid3DAction(),
				true, app);
		cbShowGrid.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
				.isGridVisible(), wrappedPopup.getPopupMenu());
		getWrappedPopup().addItem(cbShowGrid);

		// PLANE
		String img3;
		if (app.isUnbundledOrWhiteboard()) {
			img3 = AppResources.INSTANCE.plane20().getSafeUri().asString();
		} else {
			img3 = StyleBar3DResources.INSTANCE.plane().getSafeUri().asString();
		}
		htmlString = MainMenu.getMenuBarHtmlClassic(img3, loc.getMenu("Plane"));
		GCheckBoxMenuItem cbShowPlane = new GCheckBoxMenuItem(htmlString,
				((GuiManager3DW) app.getGuiManager()).getShowPlane3DAction(),
				true, app);
		cbShowPlane.setSelected(((Kernel3D) app.getKernel()).getXOYPlane()
				.isPlateVisible(), wrappedPopup.getPopupMenu());
		getWrappedPopup().addItem(cbShowPlane);
	}

	@Override
	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
		if (app.getActiveEuclidianView().isEuclidianView3D()) {
			((EuclidianView3DW) app.getActiveEuclidianView()).doRepaint();
		}
	}

	/**
	 * expand/collapse submenu for projection types in 3D
	 * 
	 * @author csilla
	 */
	public class ProjectionSubmenu extends CheckMarkSubMenu {
		/**
		 * @param parentMenu
		 *            - parent menu item
		 */
		public ProjectionSubmenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
		}

		@Override
		protected void initActions() {
			addOrthographicProjection();
			addPerspectiveProjection();
			addGlassesProjection();
			addObliqueProjection();
		}

		private void addOrthographicProjection() {
			addProjectionMenuItemForType(
					EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC);
		}

		private void addPerspectiveProjection() {
			addProjectionMenuItemForType(
					EuclidianView3DInterface.PROJECTION_PERSPECTIVE);
		}

		private void addGlassesProjection() {
			addProjectionMenuItemForType(
					EuclidianView3DInterface.PROJECTION_GLASSES);
		}

		private void addObliqueProjection() {
			addProjectionMenuItemForType(
					EuclidianView3DInterface.PROJECTION_OBLIQUE);
		}

		/**
		 * @param projectionType
		 *            type of projection
		 */
		private void addProjectionMenuItemForType(final int projectionType) {
			String text = "";
			SVGResource img = null;
			switch (projectionType) {
			case EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC:
				text = "stylebar.OrthographicProjection";
				img = MaterialDesignResources.INSTANCE
						.projection_orthographic();
				break;
			case EuclidianView3DInterface.PROJECTION_PERSPECTIVE:
				text = "stylebar.PerspectiveProjection";
				img = MaterialDesignResources.INSTANCE.projection_perspective();
				break;
			case EuclidianView3DInterface.PROJECTION_GLASSES:
				text = "stylebar.GlassesProjection";
				img = MaterialDesignResources.INSTANCE.projection_glasses();
				break;
			case EuclidianView3DInterface.PROJECTION_OBLIQUE:
				text = "stylebar.ObliqueProjection";
				img = MaterialDesignResources.INSTANCE.projection_oblique();
				break;
			default:
				Log.warn("Unknown projection:" + projectionType);
				return;
			}
			boolean isSelected = isProjectionType(projectionType);
			addItem(MainMenu.getMenuBarHtmlClassic(img.getSafeUri().asString(),
					app.getLocalization().getMenu(text)),
					isSelected, new Command() {

						@Override
						public void execute() {
							setProjectionType(projectionType);
						}
					}, true);
		}

		/**
		 * @param projectionType
		 *            - projection type
		 * @return true if is parameter projection type
		 */
		public boolean isProjectionType(int projectionType) {
			if (app.getActiveEuclidianView().isEuclidianView3D()) {
				return ((EuclidianView3DW) app.getActiveEuclidianView())
						.getProjection() == projectionType;
			}
			return false;
		}

		/**
		 * @param projectionType
		 *            - type of projection
		 */
		public void setProjectionType(int projectionType) {
			((EuclidianSettings3D) app.getSettings()
					.getEuclidianForView(app.getActiveEuclidianView(), app))
							.setProjection(projectionType);
			if (app.getActiveEuclidianView().isEuclidianView3D()) {
				((EuclidianView3DW) app.getActiveEuclidianView())
						.setProjection(projectionType);
			}
			app.getActiveEuclidianView().repaintView();
			app.storeUndoInfo();
			getWrappedPopup().hideMenu();
		}

		@Override
		public void update() {
			// do nothing now
		}
	}

}
