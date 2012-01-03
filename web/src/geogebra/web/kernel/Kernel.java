package geogebra.web.kernel;

import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.MacroKernelInterface;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.web.main.Application;

public class Kernel extends AbstractKernel {

	public Kernel(Application app) {
		this();
		this.app = app;
			newConstruction();
		getExpressionNodeEvaluator();
	}
	
	public Kernel() {
		super();
	}

	

	

	

	@Override
    public GeoElementSpreadsheet getGeoElementSpreadsheet() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
	
	

	

	

	@Override
    public MacroKernelInterface newMacroKernel() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}