package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.main.App;

/**
 * class for static methods used in 3D
 * @author mathieu
 *
 */
public class CommandProcessor3D {

	
	/**
	 * return current view orientation if not in a loading mode and not in a macro
	 * @param kernelA current kernel
	 * @param app application
	 * @return current view orientation
	 */
	static final public GeoDirectionND getCurrentViewOrientation(Kernel kernelA, App app){
		
		EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
	
		//first check if it's an input line call, with 2D/3D view active
		if (!kernelA.isMacroKernel() && !kernelA.getLoadingMode() && view!=null){
			if (view.isDefault2D()){
				//xOy view is active 
	    		return kernelA.getXOYPlane();
			}

			if (view instanceof EuclidianViewForPlaneInterface){
				//plane view is active 
				return ((EuclidianViewForPlaneCompanion) ((EuclidianView) view).getCompanion()).getPlane();
			}
			
			//3D view is active 
			return kernelA.getSpace();
		}
		
		return null;
	}
}
