package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.polynomial.Polynomial;
import geogebra.common.kernel.prover.polynomial.Variable;
import geogebra3D.euclidian3D.EuclidianView3D;

public class GeoPlane3DConstant extends GeoPlane3D implements SymbolicParametersBotanaAlgo {
	
	public static final int XOY_PLANE = 1;

	/** construct the plane xOy, ...
	 * @param c
	 * @param type
	 */
	public GeoPlane3DConstant(Construction c, int type) {
		
		super(c);
		
		
		switch (type) {
		case XOY_PLANE:
			coordsys.addPoint(EuclidianView3D.o);
			coordsys.addVector(EuclidianView3D.vx);
			coordsys.addVector(EuclidianView3D.vy);
			coordsys.makeOrthoMatrix(false,false);
			coordsys.setEquationVector(0, 0, 1, -1);
			//setCoord(EuclidianView3D.o,EuclidianView3D.vx,EuclidianView3D.vy);
			label = "xOyPlane";
			labelSet = true;
			setObjColor(new geogebra.awt.GColorD(0.5f,0.5f,0.5f));
			setLabelVisible(false);
			break;

		}
		
		setFixed(true);
	}
	
	
	/*
	public GgbVector getPoint(double x2d, double y2d){
		
		if (x2d>getXmax())
			x2d=getXmax();
		else if (x2d<getXmin())
			x2d=getXmin();
		
		if (y2d>getYmax())
			y2d=getYmax();
		else if (y2d<getYmin())
			y2d=getYmin();		
		
		return super.getPoint(x2d,y2d);
	}

*/
	
	@Override
	public boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}
	
	@Override
	public String toValueString(StringTemplate tpl) {
		return label;
	}
	

	@Override
	public String getLabel(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			return loc.getPlain(label);
		}
		return label;

	}
	
	
	@Override
	public boolean isTraceable() {
		return false;
	}


	public Variable[] getBotanaVars(GeoElement geo) {
		// TODO Auto-generated method stub
		return null;
	}


	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}
}
