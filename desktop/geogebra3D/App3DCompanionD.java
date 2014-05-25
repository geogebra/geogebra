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
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.gui.layout.panels.EuclidianDockPanelForPlane;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * 
 * @author mathieu
 *
 * Companion for 3D application in desktop
 */
public class App3DCompanionD extends App3DCompanion {

	/**
	 * constructor
	 * @param app application
	 */
	public App3DCompanionD(App app) {
		super(app);
	}
	
	@Override
	protected EuclidianViewForPlaneCompanion createEuclidianViewForPlane(ViewCreator plane, EuclidianSettings evSettings){
		EuclidianViewForPlane view = new EuclidianViewForPlane(new EuclidianControllerForPlaneD(app.getKernel()), plane, evSettings);
		return view.getCompanion();
	}
	

	
	private EuclidianDockPanelForPlane panel;

	@Override
	protected void createDockPanel(boolean panelSettings, EuclidianViewForPlaneCompanion vfpc){
		// create dock panel
		panel = new EuclidianDockPanelForPlane((AppD) app, (EuclidianViewForPlane) vfpc.getView());

		((LayoutD) app.getGuiManager().getLayout()).registerPanel(panel);


		if (panelSettings){
			// panel.setToolbarString(dpInfo[i].getToolbarString());
			panel.setFrameBounds(new Rectangle(600, 400));
			// panel.setEmbeddedDef(dpInfo[i].getEmbeddedDef());
			//panel.setEmbeddedSize(300);
			// panel.setShowStyleBar(dpInfo[i].showStyleBar());
			// panel.setOpenInFrame(dpInfo[i].isOpenInFrame());
			panel.setVisible(true);
			panel.toggleStyleBar();


			((LayoutD) app.getGuiManager().getLayout()).getDockManager().show(panel);

		}
	}
	
	/**
	 * 
	 * @return current dockpanel for plane
	 */
	public DockPanel getPanelForPlane(){
		return panel;
	}



	private ArrayList<EuclidianDockPanelForPlane> panelForPlaneList;
	
	@Override
	public void storeViewCreators(){
		
		if (panelForPlaneList==null)
			panelForPlaneList = new ArrayList<EuclidianDockPanelForPlane>();
		else
			panelForPlaneList.clear();
		
		DockPanel[] panels = ((LayoutD) app.getGuiManager().getLayout()).getDockManager().getPanels();
		for (int i=0; i<panels.length; i++){
			if (panels[i] instanceof EuclidianDockPanelForPlane){
				panelForPlaneList.add((EuclidianDockPanelForPlane) panels[i]);
			}
		}
		
	}
	

	@Override
	public void recallViewCreators(){

		for (EuclidianDockPanelForPlane p : panelForPlaneList){
			EuclidianViewForPlane view = p.getView();
			GeoElement geo = app.getKernel().lookupLabel(((GeoElement) view.getCompanion().getPlane()).getLabelSimple());
			if (geo!=null && (geo instanceof ViewCreator)){
				ViewCreator plane = (ViewCreator) geo;
				view.getCompanion().setPlane(plane);
				plane.setEuclidianViewForPlane(view.getCompanion());
				view.getCompanion().updateForPlane();
			}else{
				//no more creator : remove
				p.getView().getCompanion().doRemove();
			}
		}
	}
	
	

	@Override
	public void resetEuclidianViewForPlaneIds() {
		EuclidianDockPanelForPlane.resetIds();
	}
	
	

}
