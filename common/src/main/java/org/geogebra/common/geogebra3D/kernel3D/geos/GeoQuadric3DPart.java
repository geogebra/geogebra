package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DPartInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for part of a quadric (e.g. side of a limited cone, cylinder, ...)
 * 
 * @author mathieu
 * 
 */
public class GeoQuadric3DPart extends GeoQuadric3D implements GeoNumberValue,
		FromMeta, GeoQuadric3DPartInterface, GeoQuadric3DLimitedOrPart {

	/** min value for limites */
	private double min;
	/** max value for limites */
	private double max;

	/**
	 * constructor
	 * 
	 * @param c
	 */
	public GeoQuadric3DPart(Construction c) {
		super(c);
	}

	public GeoQuadric3DPart(GeoQuadric3DPart quadric) {
		super(quadric);
	}

	@Override
	public void set(GeoElement geo) {
		super.set(geo);
		GeoQuadric3DPart quadric = (GeoQuadric3DPart) geo;
		setLimits(quadric.bottom, quadric.top);
		area = quadric.getArea();
	}

	/**
	 * sets the min and max values for limits
	 * 
	 * @param min
	 * @param max
	 */
	public void setLimits(double min, double max) {

		bottom = min;
		top = max;

		if (min < max) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;
		}
	}

	private double bottom, top;

	public double getBottomParameter() {
		return bottom;
	}

	public double getTopParameter() {
		return top;
	}

	@Override
	public double getMinParameter(int index) {

		if (index == 1)
			return min;

		return super.getMinParameter(index);
	}

	@Override
	public double getMaxParameter(int index) {
		if (index == 1)
			return max;

		return super.getMaxParameter(index);
	}

	public void set(Coords origin, Coords direction, double r) {
		switch (type) {
		case QUADRIC_CYLINDER:
			setCylinder(origin, direction, r);
			break;

		case QUADRIC_CONE:
			setCone(origin, direction, r);
			break;
		}
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.QUADRIC_PART;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		switch (type) {
		case QUADRIC_CYLINDER:
		case QUADRIC_CONE:
			return kernel.format(area, tpl);

		}

		return "todo-GeoQuadric3DPart";
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		return new StringBuilder(toValueString(tpl));
	}

	@Override
	public GeoElement copy() {
		return new GeoQuadric3DPart(this);
	}

	// ////////////////////////
	// REGION
	// ////////////////////////

	@Override
	protected void getNormalProjectionParameters(Coords coords, double[] parameters) {

		super.getNormalProjectionParameters(coords, parameters);

		if (parameters[1] < getMinParameter(1)){
			parameters[1] = getMinParameter(1);
		}else if (parameters[1] > getMaxParameter(1)){
			parameters[1] = getMaxParameter(1);
		}


	}

	@Override
	protected Coords[] getProjection(Coords willingCoords,
			Coords willingDirection, double t1, double t2) {

		if (Kernel.isGreater(t2, t1)) {
			return getProjectionSorted(willingCoords, willingDirection, t1, t2);
		}

		if (Kernel.isGreater(t1, t2)) {
			return getProjectionSorted(willingCoords, willingDirection, t2, t1);
		}

		return super.getProjection(willingCoords, willingDirection, t1, t2);

	}
	
	@Override
	protected boolean checkParameters(double[] parameters){
		if (Kernel.isGreater(getMinParameter(1), parameters[1])) {
			parameters[1] = getMinParameter(1);
			return false;
		} 
		if (Kernel.isGreater(parameters[1], getMaxParameter(1))) {
			parameters[1] = getMaxParameter(1);
			return false;
		}
		return super.checkParameters(parameters);
	}
	

	/**
	 * try with t1, then with t2, assuming t1 < t2
	 * 
	 * @param willingCoords
	 *            willing coords
	 * @param willingDirection
	 *            willing direction
	 * @param t1
	 *            first possible parameter
	 * @param t2
	 *            second possible parameter
	 * @return closest point
	 */
	private Coords[] getProjectionSorted(Coords willingCoords,
			Coords willingDirection, double t1, double t2) {

		super.getNormalProjectionParameters(willingCoords
				.add(willingDirection.mul(t1)), tmpDouble2);

		// check if first parameters are inside
		if (Kernel.isGreater(getMinParameter(1), tmpDouble2[1])) {
			tmpDouble2[1] = getMinParameter(1);
		} else if (Kernel.isGreater(tmpDouble2[1], getMaxParameter(1))) {
			tmpDouble2[1] = getMaxParameter(1);
		} else {
			return new Coords[] { getPoint(tmpDouble2[0], tmpDouble2[1]), new Coords(tmpDouble2) }; // first
																		// parameters
																		// are
																		// inside
		}

		// first parameters are outside, check second parameters
		super.getNormalProjectionParameters(willingCoords
				.add(willingDirection.mul(t2)), tmpDouble2bis);
		if (Kernel.isGreater(getMinParameter(1), tmpDouble2bis[1])) {
			tmpDouble2bis[1] = getMinParameter(1);
		} else if (Kernel.isGreater(tmpDouble2bis[1], getMaxParameter(1))) {
			tmpDouble2bis[1] = getMaxParameter(1);
		} else {
			return new Coords[] { getPoint(tmpDouble2bis[0], tmpDouble2bis[1]), new Coords(tmpDouble2bis) }; // first
																		// parameters
																		// are
																		// inside
		}

		// first and second parameters are outside: check nearest limit point
		Coords l1 = getPoint(tmpDouble2[0], tmpDouble2[1]);
		Coords l2 = getPoint(tmpDouble2bis[0], tmpDouble2bis[1]);
		double d1 = l1.distLine(willingCoords, willingDirection);
		double d2 = l2.distLine(willingCoords, willingDirection);
		if (Kernel.isGreater(d1, d2)) {
			return new Coords[] { getPoint(tmpDouble2bis[0], tmpDouble2bis[1]), new Coords(tmpDouble2bis) };
		}
		return new Coords[] { getPoint(tmpDouble2[0], tmpDouble2[1]), new Coords(tmpDouble2)  };

	}

	@Override
	public boolean isInRegion(Coords coords) {

		// check first if coords is in unlimited quadric
		if (!super.isInRegion(coords)) {
			return false;
		}

		// check if coords respect limits
		super.getNormalProjectionParameters(coords, tmpDouble2);
		if (tmpDouble2[1] < getMinParameter(1))
			return false;
		if (tmpDouble2[1] > getMaxParameter(1))
			return false;

		// all ok
		return true;
	}

	@Override
	protected Coords getPointInRegion(double u, double v) {

		double v0;
		if (v < getMinParameter(1)) {
			v0 = getMinParameter(1);
		} else if (v > getMaxParameter(1)) {
			v0 = getMaxParameter(1);
		} else {
			v0 = v;
		}

		return super.getPointInRegion(u, v0);
	}

	// ////////////////////////
	// AREA
	// ////////////////////////

	private double area;

	public void calcArea() {

		// Application.debug("geo="+getLabel()+", half="+getHalfAxis(0)+", min="+min+", max="+max+", type="+type);

		switch (type) {
		case QUADRIC_CYLINDER:
			area = 2 * getHalfAxis(0) * Math.PI * (max - min);
			break;
		case QUADRIC_CONE:
			double r2 = getHalfAxis(0);
			r2 *= r2;
			double h2;
			if (min * max < 0) { // "double-cone"
				h2 = min * min + max * max;
			} else { // truncated cone
				h2 = Math.abs(max * max - min * min);
			}
			area = Math.PI * h2 * r2 * Math.sqrt(1 + 1 / r2);
			break;
		}
	}

	public double getArea() {
		if (defined)
			return area;
		else
			return Double.NaN;
	}

	// ////////////////////////////////
	// NumberValue
	// ////////////////////////////////

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	public double getDouble() {
		return getArea();
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	// //////////////////////////
	// META
	// //////////////////////////

	private GeoElement meta = null;

	@Override
	public int getMetasLength() {
		if (meta == null) {
			return 0;
		}

		return 1;
	}

	public GeoElement[] getMetas() {
		return new GeoElement[] { meta };
	}

	/**
	 * @param quadric
	 *            cone/cylinder that created it
	 */
	public void setFromMeta(GeoElement quadric) {
		meta = quadric;
	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		super.dilate(rval, S);
		double r = rval.getDouble();
		area *= r * r;

		double rAbs = Math.abs(r);
		bottom *= rAbs;
		top *= rAbs;
		min *= rAbs;
		max *= rAbs;

	}

	@Override
	protected void getXMLtagsMatrix(StringBuilder sb) {
		// no matrix needed since it comes from an algo
	}

	@Override
	protected void classifyQuadric() {
		App.error("GeoQuadric3DPart should not need classification");
	}

	@Override
	public String getTypeString() {
		return "Surface";
	}

	/**
	 * 
	 * @return GeoQuadric3D type string
	 */
	public String getQuadricTypeString() {
		return super.getTypeString();
	}

}
