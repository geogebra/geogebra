package geogebra.geogebra3D.web.main;

import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.geogebra3D.main.App3DCompanion;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidianForPlane.EuclidianControllerForPlaneW;
import geogebra.geogebra3D.web.euclidianForPlane.EuclidianViewForPlaneW;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanelForPlaneW;
import geogebra.web.gui.layout.DockManagerW;
import geogebra.web.gui.layout.LayoutW;

import java.util.ArrayList;

/**
 * 
 * @author mathieu
 *
 *         Companion for 3D application in desktop
 */
public class App3DCompanionW extends App3DCompanion {

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 */
	public App3DCompanionW(App app) {
		super(app);
	}

	@Override
	protected EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
	        ViewCreator plane, EuclidianSettings evSettings,
	        boolean panelSettings) {

		// create dock panel
		panel = new EuclidianDockPanelForPlaneW(app);
		panel.loadComponent();
		EuclidianViewForPlaneW view = new EuclidianViewForPlaneW(panel,
		        new EuclidianControllerForPlaneW(app.getKernel()), plane,
		        evSettings, panel.getViewId());
		panel.setView(view);

		((LayoutW) app.getGuiManager().getLayout()).registerPanel(panel);

		if (panelSettings) {
			// panel.setFrameBounds(new Rectangle(600, 400));
			panel.setVisible(true);
			// panel.toggleStyleBar();

			((LayoutW) app.getGuiManager().getLayout()).getDockManager().show(
			        panel);

		}

		return view.getCompanion();
	}

	private EuclidianDockPanelForPlaneW panel;

	/**
	 * 
	 * @return current dockpanel for plane
	 */
	@Override
	public DockPanel getPanelForPlane() {
		return panel;
	}

	private ArrayList<EuclidianDockPanelForPlaneW> panelForPlaneList;

	@Override
	public void storeViewCreators() {

		if (panelForPlaneList == null)
			panelForPlaneList = new ArrayList<EuclidianDockPanelForPlaneW>();
		else
			panelForPlaneList.clear();

		DockPanel[] panels = ((DockManagerW) app.getGuiManager().getLayout()
		        .getDockManager()).getPanels();
		for (int i = 0; i < panels.length; i++) {
			if (panels[i] instanceof EuclidianDockPanelForPlaneW) {
				panelForPlaneList.add((EuclidianDockPanelForPlaneW) panels[i]);
			}
		}

	}

	@Override
	public void recallViewCreators() {

		for (EuclidianDockPanelForPlaneW p : panelForPlaneList) {
			EuclidianViewForPlaneW view = p.getView();
			GeoElement geo = app.getKernel().lookupLabel(
			        ((GeoElement) view.getCompanion().getPlane())
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

	@Override
	public void resetEuclidianViewForPlaneIds() {
		EuclidianDockPanelForPlaneW.resetIds();
	}

	/**
	 * recalculates views environments.
	 */
	public void recalculateEnvironments() {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}
		for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
			vfpc.getView().getEuclidianController().calculateEnvironment();
		}
	}

	/**
	 * update view for plane sizes
	 */
	public void updateViewSizes() {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}
		for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
			((EuclidianViewForPlaneW) vfpc.getView()).getDockPanel()
			        .deferredOnResize();
		}
	}

}
