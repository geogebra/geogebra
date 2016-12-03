package org.geogebra.web.geogebra3D.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.geogebra3D.web.euclidianForPlane.EuclidianViewForPlaneW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelForPlaneW extends EuclidianDockPanelWAbstract
        implements EuclidianPanelWAbstract {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	// id of the first view
	private static int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;

	private EuclidianViewForPlaneW view;

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * 
	 */
	public EuclidianDockPanelForPlaneW(App app) {
		super(viewId, // view id
		        "GraphicsViewForPlaneA", // view title
		        ToolBar.getAllToolsNoMacrosForPlane(), // toolbar string
		        true, // style bar?
		        -1, // menu order
		        'p');

		this.app = (AppW) app;
		this.setOpenInFrame(true);

		this.setEmbeddedSize(DEFAULT_WIDTH);

		viewId++; // id of next view

		// this.app = app;
	}

	@Override
	public boolean canCustomizeToolbar() {
		return false;
	}

	/**
	 * set the view attached
	 * 
	 * @param view
	 */
	public void setView(EuclidianViewForPlaneW view) {
		this.view = view;
		view.getCompanion().setDockPanel(this);
	}

	@Override
	public Widget loadComponent() {
		setViewImage(getResources().styleBar_graphics_extra());
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.getAbsolutePanel().add(eview1);
		}

		return euclidianpanel;
	}

	@Override
	protected Widget loadStyleBar() {
		return (Widget) view.getStyleBar();
	}

	/**
	 * 
	 * @return view attached in this panel
	 */
	public EuclidianViewForPlaneW getView() {
		return view;
	}

	/**
	 * reset views ids
	 */
	public static void resetIds() {
		viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	@Override
	public EuclidianView getEuclidianView() {
		return view;
	}

	@Override
	public DockPanelData createInfo() {
		return new DockPanelData(id, getToolbarString(), visible,
				openInFrame, showStyleBar, new Rectangle(frameBounds),
				embeddedDef, embeddedSize, view.getFromPlaneString());
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics_extra();
	}

	@Override
	public boolean hasPlane() {
		return true;
	}

	@Override
	public void calculateEnvironment() {
		view.getEuclidianController().calculateEnvironment();

	}

	@Override
	public void resizeView(int width, int height) {

		final EuclidianSettings settings = app.getSettings()
				.getEuclidianForPlane(
						((GeoElement) view.getCompanion().getPlane())
								.getLabelSimple());
		settings.setPreferredSize(
				AwtFactory.getPrototype()
				.newDimension(width, height));

		view.synCanvasSize();
		view.doRepaint2();
		app.stopCollectingRepaints();

	}
}
