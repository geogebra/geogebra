package geogebra.geogebra3D.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.io.layout.DockPanelDataForPlane;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidianForPlane.EuclidianViewForPlaneW;
import geogebra.html5.euclidian.EuclidianPanelWAbstract;
import geogebra.html5.main.AppW;
import geogebra.html5.openjdk.awt.geom.Rectangle;
import geogebra.web.gui.layout.panels.EuclidianDockPanelWAbstract;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;
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
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.add(eview1);
		}

		return euclidianpanel;
	}

	class EuclidianPanel extends AbsolutePanel implements RequiresResize {

		EuclidianDockPanelForPlaneW dockPanel;

		int oldHeight = 0;
		int oldWidth = 0;

		public EuclidianPanel(EuclidianDockPanelForPlaneW dockPanel) {
			this.dockPanel = dockPanel;
		}

		public void onResize() {

			if (app != null) {

				int h = dockPanel.getComponentInteriorHeight();
				int w = dockPanel.getComponentInteriorWidth();

				// TODO handle this better?
				// exit if new size cannot be determined
				if (h <= 0 || w <= 0) {
					return;
				}

				if (h != oldHeight || w != oldWidth) {

					final EuclidianSettings settings = app.getSettings()
					        .getEuclidianForPlane(
					                ((GeoElement) view.getCompanion()
					                        .getPlane()).getLabelSimple());
					settings.setPreferredSize(geogebra.common.factories.AwtFactory.prototype
					        .newDimension(w, h));

					view.synCanvasSize();
					view.doRepaint2();
					app.stopCollectingRepaints();

					oldHeight = h;
					oldWidth = w;

				} else {
					// it's possible that the width/height didn't change but the
					// position of EV did
					view.getEuclidianController().calculateEnvironment();
				}
			}
		}
	}

	@Override
	public void showView(boolean b) {
		// TODO Auto-generated method stub
	}

	public void updateNavigationBar() {
		// TODO Auto-generated method stub
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

	public Canvas getCanvas() {
		return eview1;
	}

	public AbsolutePanel getAbsolutePanel() {
		return euclidianpanel;
	}

	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
	}

	@Override
	public EuclidianView getEuclidianView() {
		return view;
	}

	@Override
	public DockPanelData createInfo() {
		return new DockPanelDataForPlane(id, getToolbarString(), visible,
		        openInFrame,
		        showStyleBar, new Rectangle(frameBounds), embeddedDef,
 embeddedSize, view.getFromPlaneString());
	}

}
