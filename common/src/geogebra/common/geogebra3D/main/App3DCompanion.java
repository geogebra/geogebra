package geogebra.common.geogebra3D.main;

import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.geogebra3D.kernel3D.Kernel3D;
import geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.App;
import geogebra.common.main.AppCompanion;
import geogebra.common.main.settings.EuclidianSettings;

import java.util.ArrayList;

/**
 * 
 * @author mathieu
 *
 * Companion for 3D application
 */
public abstract class App3DCompanion extends AppCompanion {
	
	/**
	 * Constructor
	 * @param app application
	 */
	public App3DCompanion(App app){
		super(app);
	}
	
	@Override
	public Kernel newKernel(){
		return new Kernel3D(app);
	}
	
	@Override
	protected boolean tableVisible(int table) {
		return !(table == CommandsConstants.TABLE_CAS || table == CommandsConstants.TABLE_ENGLISH);
	}

	// ///////////////////////////////
	// EUCLIDIAN VIEW FOR PLANE
	// ///////////////////////////////
	
	private ArrayList<EuclidianViewForPlaneCompanion> euclidianViewForPlaneCompanionList;
	
	private EuclidianViewForPlaneCompanion euclidianViewForPlaneCompanion;

	/**
	 * add euclidian views for plane settings
	 * @param sb string builder
	 * @param asPreference save as preference flag
	 */
	public void addCompleteUserInterfaceXMLForPlane(StringBuilder sb, boolean asPreference) {
		if (euclidianViewForPlaneCompanionList!=null){
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList){
				vfpc.getView().getXML(sb, asPreference);
			}
		}
	}
	
	
	@Override
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		super.getEuclidianViewXML(sb, asPreference);
		app.getEuclidianView3D().getXML(sb,asPreference);

		if (euclidianViewForPlaneCompanionList!=null)
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList)
				vfpc.getView().getXML(sb, asPreference);

	}
	
	/**
	 * create new euclidian view for plane
	 * @param plane plane
	 * @param evSettings settings
	 * @return view companion
	 */
	protected abstract EuclidianViewForPlaneCompanion createEuclidianViewForPlane(ViewCreator plane, EuclidianSettings evSettings);
	
	/**
	 * create dock panel for the view for plane
	 * @param panelSettings says if does panel settings
	 * @param vfpc view for plane
	 */
	protected abstract void createDockPanel(boolean panelSettings, EuclidianViewForPlaneCompanion vfpc);
	
	@Override
	public EuclidianViewForPlaneCompanion createEuclidianViewForPlane(ViewCreator plane, boolean panelSettings) {
		// create new view for plane and controller
		EuclidianSettings evSettings = app.getSettings().getEuclidianForPlane(((GeoElement) plane).getLabelSimple());
		if (evSettings == null){
			evSettings = new EuclidianSettingsForPlane(app.getEuclidianView1().getSettings());
		}
		euclidianViewForPlaneCompanion = createEuclidianViewForPlane(plane, evSettings);
		euclidianViewForPlaneCompanion.getView().updateFonts();
		euclidianViewForPlaneCompanion.addExistingGeos();
		
		//add it to list
		if (euclidianViewForPlaneCompanionList==null)
			euclidianViewForPlaneCompanionList = new ArrayList<EuclidianViewForPlaneCompanion>();
		euclidianViewForPlaneCompanionList.add(euclidianViewForPlaneCompanion);
		

		// create dock panel
		createDockPanel(panelSettings, euclidianViewForPlaneCompanion);

		return euclidianViewForPlaneCompanion;
	}
	
	
	/**
	 * Update font sizes of euclidian views for plane
	 */
	public void resetFonts() {


		if (euclidianViewForPlaneCompanion != null) {
			euclidianViewForPlaneCompanion.getView().updateFonts();
		}
	}
	
	/**
	 * remove the view from the list
	 * @param vfpc view for plane companion
	 */
	public void removeEuclidianViewForPlaneFromList(EuclidianViewForPlaneCompanion vfpc){
		euclidianViewForPlaneCompanionList.remove(vfpc);
	}
	
	
	/**
	 * remove all euclidian views for plane
	 */
	public void removeAllEuclidianViewForPlane(){
		
		if (euclidianViewForPlaneCompanionList == null)
			return;
		
		for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList)
			vfpc.removeFromGuiAndKernel();
		
		euclidianViewForPlaneCompanionList.clear();
	}


}
