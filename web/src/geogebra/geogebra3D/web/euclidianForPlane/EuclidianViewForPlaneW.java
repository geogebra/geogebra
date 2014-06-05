package geogebra.geogebra3D.web.euclidianForPlane;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianViewCompanion;
import geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanelForPlaneW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;

import com.google.gwt.user.client.ui.Widget;

/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlaneW extends EuclidianViewW implements EuclidianViewForPlaneInterface {


	
	/**
	 * 
	 * @param euclidianViewPanel view panel
	 * @param ec controller
	 * @param plane plane creating this view
	 * @param settings euclidian settings
	 */
	public EuclidianViewForPlaneW(EuclidianPanelWAbstract euclidianViewPanel, EuclidianController ec, ViewCreator plane, EuclidianSettings settings) {
		super(euclidianViewPanel, ec, new boolean[]{ false, false }, false, 0, settings);
		
		((EuclidianViewForPlaneCompanion) companion).initView(plane);
	}
	
	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion(){
		return new EuclidianViewForPlaneCompanion(this);
	}
	


	
	@Override
	public EuclidianViewForPlaneCompanion getCompanion(){
		return (EuclidianViewForPlaneCompanion) super.getCompanion();
	}
	

	/**
	 * @return panel component
	 */
	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	
//	@Override
//    public final void repaint() {
//
//		// temporary hack : use timer instead
//		doRepaint();
//     }
	
	/**
	 * 
	 * @return dock panel
	 */
	public EuclidianDockPanelForPlaneW getDockPanel(){
		return (EuclidianDockPanelForPlaneW) EVPanel;
	}

	
	
	
}
