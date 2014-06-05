package geogebra.common.main;

import geogebra.common.euclidian.EuclidianViewCompanion;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.kernelND.ViewCreator;

/**
 * 
 * @author mathieu
 *
 * Companion for application
 */
public class AppCompanion {
	
	protected App app;
	
	/**
	 * Constructor
	 * @param app application
	 */
	public AppCompanion(App app){
		this.app = app;
	}
	
	/**
	 * 
	 * @return new kernel
	 */
	public Kernel newKernel(){
		return new Kernel(app);
	}
	
	
	/**
	 * return true if commands of this table should be visible in input bar help
	 * and autocomplete
	 * 
	 * @param table
	 *            table number, see CommandConstants.TABLE_*
	 * @return true for visible tables
	 */
	protected boolean tableVisible(int table) {
		return !(table == CommandsConstants.TABLE_CAS ||
				table == CommandsConstants.TABLE_3D || table == CommandsConstants.TABLE_ENGLISH);
	}
	
	
	/**
	 * XML settings for both EVs
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether we need this for preference XML
	 */
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		app.getEuclidianView1().getXML(sb, asPreference);
		if (app.hasEuclidianView2EitherShowingOrNot()) {
			app.getEuclidianView2().getXML(sb, asPreference);
		}
	}
	
	
	/**
	 * @param plane plane creator
	 * @param panelSettings panel settings
	 * @return create a new euclidian view for the plane
	 */
	public EuclidianViewCompanion createEuclidianViewForPlane(ViewCreator plane, boolean panelSettings){
		return null;
	}

	
	/**
	 * store view creators (for undo)
	 */
	public void storeViewCreators(){
		// used in 3D
	}
	
	/**
	 * recall view creators (for undo)
	 */
	public void recallViewCreators(){
		// used in 3D
	}
	
	
	/**
	 * reset ids for 2D view created by planes, etc. Used in 3D.
	 */
	public void resetEuclidianViewForPlaneIds() {
		// used in 3D
		
	}

	/**
	 * @return new EuclidianDockPanelForPlane
	 */
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {
		return null;
	}
	
	/**
	 * repaint views for plane
	 */
	public void doRepaintViewsForPlane(){
		// used in 3D
	}


}
