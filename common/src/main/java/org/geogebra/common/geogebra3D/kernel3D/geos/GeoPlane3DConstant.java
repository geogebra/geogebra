package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

public class GeoPlane3DConstant extends GeoPlane3D implements
		SymbolicParametersBotanaAlgo {

	public static final int XOY_PLANE = 1;

	/**
	 * construct the plane xOy, ...
	 * 
	 * @param c
	 * @param type
	 */
	public GeoPlane3DConstant(Construction c, int type) {

		super(c);

		switch (type) {
		case XOY_PLANE:
			coordsys.addPoint(Coords.O);
			coordsys.addVector(Coords.VX);
			coordsys.addVector(Coords.VY);
			coordsys.makeOrthoMatrix(false, false);
			coordsys.setEquationVector(0, 0, 1, 0);
			// setCoord(EuclidianView3D.o,EuclidianView3D.vx,EuclidianView3D.vy);
			label = "xOyPlane";
			labelSet = true;
			setObjColor(GColor.GRAY);
			setLabelVisible(false);
			break;

		}

		setFixed(true);
	}

	/*
	 * public GgbVector getPoint(double x2d, double y2d){
	 * 
	 * if (x2d>getXmax()) x2d=getXmax(); else if (x2d<getXmin()) x2d=getXmin();
	 * 
	 * if (y2d>getYmax()) y2d=getYmax(); else if (y2d<getYmin()) y2d=getYmin();
	 * 
	 * return super.getPoint(x2d,y2d); }
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
			return getLoc().getPlain(label);
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

	@Override
	public boolean isRenameable() {
		return false;
	}
}
