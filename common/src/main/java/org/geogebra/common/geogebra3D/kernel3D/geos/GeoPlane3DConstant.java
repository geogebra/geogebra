package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.EventType;

/**
 * Constant plane
 *
 */
public class GeoPlane3DConstant extends GeoPlane3D
		implements SymbolicParametersBotanaAlgo {
	/** xYo plane */
	public static final int XOY_PLANE = 1;
	private boolean gridVisible;

	/**
	 * construct the plane xOy, ...
	 * 
	 * @param c
	 *            construction
	 * @param type
	 *            XOY_PLANE
	 */
	public GeoPlane3DConstant(Construction c, int type) {

		super(c);

		switch (type) {
		default:
			// do nothing
			break;
		case XOY_PLANE:
			coordsys.addPoint(Coords.O);
			coordsys.addVector(Coords.VX);
			coordsys.addVector(Coords.VY);
			coordsys.makeOrthoMatrix(false, false);
			coordsys.setEquationVector(0, 0, 1, 0);
			// setCoord(EuclidianView3D.o,EuclidianView3D.vx,EuclidianView3D.vy);
			label = "xOyPlane";
			setLabelSet(true);
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
			return getLoc().getMenu(label);
		}
		return label;

	}

	@Override
	public boolean isTraceable() {
		return false;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRenameable() {
		return false;
	}

	@Override
	public boolean is6dofMoveable() {
		return false;
	}

	@Override
	public boolean showLineProperties() {
		return false;
	}

	/** returns if there is a grid to plot or not */
	@Override
	public boolean isGridVisible() {
		return gridVisible && isEuclidianVisible();
	}

	/**
	 * Show or hide grid
	 * 
	 * @param grid
	 *            grid visibility flag
	 * @return whether it changed
	 */
	public boolean setGridVisible(boolean grid) {
		if (gridVisible == grid) {
			return false;
		}
		gridVisible = grid;
		return true;
	}

	@Override
	public boolean isProtected(EventType type) {
		return true;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

}
