package geogebra3D;

import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.geogebra3D.main.App3DCompanion;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.LayoutD;
import geogebra.main.AppD;
import geogebra3D.euclidianForPlane.EuclidianControllerForPlaneD;
import geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;
import geogebra3D.gui.layout.panels.EuclidianDockPanelForPlaneD;

import java.awt.Rectangle;
import java.util.ArrayList;

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
		panel = new EuclidianDockPanelForPlaneD((AppD) app, view);

		((LayoutD) app.getGuiManager().getLayout()).registerPanel(panel);

		if (panelSettings) {
			panel.setFrameBounds(new Rectangle(600, 400));
			panel.setVisible(true);
			panel.toggleStyleBar();

			((LayoutD) app.getGuiManager().getLayout()).getDockManager().show(
					panel);

		}

		return view.getCompanion();
	}

	private EuclidianDockPanelForPlaneD panel;

	@Override
	public DockPanel getPanelForPlane() {
		return panel;
	}

	private ArrayList<EuclidianDockPanelForPlaneD> panelForPlaneList;

	@Override
	public void storeViewCreators() {

		if (panelForPlaneList == null)
			panelForPlaneList = new ArrayList<EuclidianDockPanelForPlaneD>();
		else
			panelForPlaneList.clear();

		DockPanel[] panels = ((LayoutD) app.getGuiManager().getLayout())
				.getDockManager().getPanels();
		for (int i = 0; i < panels.length; i++) {
			if (panels[i] instanceof EuclidianDockPanelForPlaneD) {
				panelForPlaneList.add((EuclidianDockPanelForPlaneD) panels[i]);
			}
		}

	}

	@Override
	public void recallViewCreators() {

		for (EuclidianDockPanelForPlaneD p : panelForPlaneList) {
			EuclidianViewForPlaneD view = p.getView();
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
		EuclidianDockPanelForPlaneD.resetIds();
	}

}
