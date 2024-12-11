package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.kernelND.RotatableND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.debug.Log;

/**
 * Class for limited quadrics (e.g. limited cones, cylinders, ...)
 * 
 * @author mathieu
 * 
 */
public class GeoQuadric3DLimited extends GeoQuadricND
		implements GeoNumberValue, HasVolume, HasHeight, RotatableND,
		Translateable, MirrorableAtPlane, Transformable, Dilateable,
		GeoQuadric3DLimitedInterface, GeoQuadric3DLimitedOrPart {

	/** side of the quadric */
	private GeoQuadric3DPart side;
	/** bottom and top of the quadric */
	private GeoConicND bottom;
	private GeoConic3D top;

	// private GeoPointND bottomPoint, topPoint;

	private double bottomParameter;
	private double topParameter;
	private Coords direction;
	private Coords origin;
	private double radius;
	private boolean silentTop = false;
	private double volume;

	/*
	 * constructor
	 * 
	 * @param c
	 * 
	 * public GeoQuadric3DLimited(Construction c) { this(c, null, null); }
	 */

	/**
	 * 
	 * @param c
	 *            construction
	 * @param type
	 *            type
	 */
	public GeoQuadric3DLimited(Construction c, int type) {
		super(c);
		setType(type);
		setConstructionDefaults();
	}

	/*
	 * public void setPoints(GeoPointND bottomPoint, GeoPointND topPoint) {
	 * this.bottomPoint = bottomPoint; this.topPoint = topPoint; }
	 */

	/**
	 * @param bottom2
	 *            new bottom
	 * @param top
	 *            new top
	 */
	public void setBottomTop(GeoConicND bottom2, GeoConic3D top) {
		this.bottom = bottom2;
		this.top = top;

		bottom.addMeta(this);
		top.addMeta(this);
	}

	/**
	 * @param side
	 *            side
	 */
	public void setSide(GeoQuadric3DPart side) {
		this.side = side;
		side.setFromMeta(this);
	}

	@Override
	public void remove() {
		bottom.removeMeta(this);
		// no need to remove meta for side and top: theses parts will be removed
		// too

		super.remove();
	}

	/**
	 * Copy constructor
	 * 
	 * @param quadric
	 *            original
	 */
	public GeoQuadric3DLimited(GeoQuadric3DLimited quadric) {
		this(quadric.getConstruction(), quadric.getType());
		this.bottom = new GeoConic3D(quadric.getConstruction());
		this.top = new GeoConic3D(quadric.getConstruction());
		this.side = new GeoQuadric3DPart(quadric.getConstruction());
		set(quadric);
	}

	/**
	 * @return bottom conic
	 */
	public GeoConicND getBottom() {
		return bottom;
	}

	/**
	 * @return top conic
	 */
	public GeoConic3D getTop() {
		return top;
	}

	/**
	 * @return side
	 */
	public GeoQuadric3DPart getSide() {
		return side;
	}

	/**
	 * Update visual style of top / bottom / side
	 */
	public void updatePartsVisualStyle() {
		setObjColor(getObjectColor());
		setLineThickness(getLineThickness());
		setAlphaValue(getAlphaValue());
		setEuclidianVisible(isEuclidianVisible());
	}

	/**
	 * init the labels
	 * 
	 * @param labels
	 *            labels for self, bottom, top, side
	 */
	public void initLabelsIncludingBottom(String[] labels) {
		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		kernel.batchAddStarted();
		if (labels == null || labels.length == 0) {
			setLabel(null);
			initSideLabels(null);
		} else {
			setLabel(labels[0]);
			initSideLabels(labels);
		}
		kernel.batchAddComplete();
	}

	private void initSideLabels(String[] labels) {
		if (labels == null || labels.length < 3) {
			bottom.setLabel(null);
			if (!silentTop) {
				top.setLabel(null);
			}
			side.setLabel(null);
		} else if (labels.length == 3) {
			bottom.setLabel(labels[1]);
			side.setLabel(labels[2]);
		} else {
			bottom.setLabel(labels[1]);
			top.setLabel(labels[2]);
			side.setLabel(labels[3]);
		}
	}

	/**
	 * init the labels
	 * 
	 * @param labels
	 *            labels for self, top, side
	 */
	public void initLabelsNoBottom(String[] labels) {
		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		if (labels == null || labels.length == 0) {
			initLabelsNoBottom(new String[1]);
			return;
		}

		setLabel(labels[0]);

		if (labels.length < 3) {
			if (!silentTop) {
				top.setLabel(null);
			}
			side.setLabel(null);
			return;
		}
		// else
		top.setLabel(labels[1]);
		side.setLabel(labels[2]);

	}

	@Override
	public double getBottomParameter() {
		return bottomParameter;
	}

	@Override
	public double getTopParameter() {
		return topParameter;
	}

	/**
	 * @return main direction
	 */
	public Coords getDirection() {
		return direction;
	}

	/**
	 * @return bottom center
	 */
	public Coords getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            top center
	 * @param direction
	 *            axis direction
	 * @param r
	 *            radius
	 * @param bottomParameter
	 *            bottom parameter
	 * @param topParameter
	 *            top parameter
	 * 
	 */
	public void setCylinder(Coords origin, Coords direction, double r,
			double bottomParameter, double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);

		// set center
		this.origin = origin;

		// set direction
		this.direction = direction;

		// set bottom radius
		this.radius = r;

		// set type
		setType(QUADRIC_CYLINDER);

	}

	/**
	 * @param origin
	 *            midpoint
	 * @param direction
	 *            main direction
	 * @param r
	 *            parameter
	 * @param bottomParameter
	 *            bottom parameter
	 * @param topParameter
	 *            top parameter
	 */
	public void setHyperbolicCylinder(Coords origin, Coords direction, double r,
			double bottomParameter, double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);

		// set center
		this.origin = origin;

		// set direction
		this.direction = direction;

		// set bottom radius
		this.radius = r;

		// set type
		setType(QUADRIC_HYPERBOLIC_CYLINDER);

	}

	/**
	 * @param origin
	 *            vertex
	 * @param direction
	 *            main direction
	 * @param r
	 *            parameter
	 * @param bottomParameter
	 *            bottom parameter
	 * @param topParameter
	 *            top parameter
	 */
	public void setParabolicCylinder(Coords origin, Coords direction, double r,
			double bottomParameter, double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);

		// set center
		this.origin = origin;

		// set direction
		this.direction = direction;

		// set bottom radius
		this.radius = r;

		// set type
		setType(QUADRIC_PARABOLIC_CYLINDER);

	}

	/**
	 * sets the bottom and top values for limits
	 * 
	 * @param bottomParameter
	 *            bottom parameter
	 * @param topParameter
	 *            top parameter
	 */
	public void setLimits(double bottomParameter, double topParameter) {
		this.bottomParameter = bottomParameter;
		this.topParameter = topParameter;

	}

	/**
	 * @return radius at height 1
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param origin
	 *            vertex
	 * @param direction
	 *            axis direction
	 * @param r
	 *            radius at height 1
	 * @param bottomParameter
	 *            bottom parameter
	 * @param topParameter
	 *            top parameter
	 */
	public void setCone(Coords origin, Coords direction, double r,
			double bottomParameter, double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);

		// set center
		this.origin = origin;

		// set direction
		this.direction = direction;

		// set bottom radius
		this.radius = r;

		// set type
		type = QUADRIC_CONE;
	}

	// ///////////////////////
	// GEOELEMENT
	// ///////////////////////

	@Override
	public void setObjColor(GColor color) {
		super.setObjColor(color);
		if (bottom == null) {
			return;
		}
		bottom.setObjColor(color);
		if (!silentTop) {
			top.setObjColor(color);
		}
		side.setObjColor(color);
	}

	@Override
	public void setTrace(boolean trace) {
		super.setTrace(trace);
		if (bottom == null) {
			return;
		}
		bottom.setTrace(trace);
		if (!silentTop) {
			top.setTrace(trace);
		}
		side.setTrace(trace);

	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		super.setEuclidianVisible(visible);
		bottom.setEuclidianVisible(visible);
		if (!silentTop) {
			top.setEuclidianVisible(visible);
		}
		side.setEuclidianVisible(visible);

	}

	@Override
	public void setShowObjectCondition(final GeoBoolean cond)
			throws CircularDefinitionException {

		super.setShowObjectCondition(cond);

		if (bottom == null) {
			return;
		}

		bottom.setShowObjectCondition(cond);
		if (!silentTop) {
			top.setShowObjectCondition(cond);
		}
		side.setShowObjectCondition(cond);

	}

	@Override
	public void updateVisualStyle(GProperty prop) {
		super.updateVisualStyle(prop);

		if (bottom == null) {
			return;
		}

		bottom.updateVisualStyle(prop);
		if (!silentTop) {
			top.updateVisualStyle(prop);
		}
		side.updateVisualStyle(prop);

	}

	@Override
	public void setLineType(int type) {
		super.setLineType(type);

		if (bottom == null) {
			return;
		}

		bottom.setLineType(type);
		bottom.update();

		if (!silentTop) {
			top.setLineType(type);
			top.update();
		}
	}

	@Override
	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);

		if (bottom == null) {
			return;
		}

		bottom.setLineTypeHidden(type);
		bottom.update();

		if (!silentTop) {
			top.setLineTypeHidden(type);
			top.update();
		}
	}

	@Override
	public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (bottom == null) {
			return;
		}

		bottom.setLineThickness(th);
		bottom.update();

		if (!silentTop) {
			top.setLineThickness(th);
			top.update();
		}
	}

	@Override
	public void setAlphaValue(double alpha) {
		super.setAlphaValue(alpha);

		if (bottom == null) {
			return;
		}

		bottom.setAlphaValue(alpha);
		bottom.updateVisualStyle(GProperty.COLOR);
		if (!silentTop) {
			top.setAlphaValue(alpha);
			top.updateVisualStyle(GProperty.COLOR);
		}
		side.setAlphaValue(alpha);
		side.updateVisualStyle(GProperty.COLOR);

		getKernel().notifyRepaint();

	}

	@Override
	public GeoElement copy() {
		return new GeoQuadric3DLimited(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.QUADRIC_LIMITED;
	}

	@Override
	public String getTypeString() {
		return side.getQuadricTypeString();
	}

	@Override
	public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		return ExtendedBoolean.newExtendedBoolean(this == geo); // TODO
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoQuadric3DLimited) {
			GeoQuadric3DLimited quadric = (GeoQuadric3DLimited) geo;

			bottomParameter = quadric.bottomParameter;
			topParameter = quadric.topParameter;
			quadric.calcVolume();
			volume = quadric.volume;

			bottom.set(quadric.bottom);
			top.set(quadric.top);
			silentTop = quadric.silentTop;
			side.set(quadric.side);

			// TODO merge with GeoQuadric3D
			// copy everything
			toStringMode = quadric.toStringMode;
			type = quadric.type;

			radius = quadric.getRadius();

			// set from side
			origin = side.getMidpoint3D();
			direction = side.getEigenvec3D(2);

			defined = quadric.defined;

			super.set(geo);
		}
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	// ///////////////////////////////////
	// GEOQUADRICND
	// ///////////////////////////////////
	/**
	 * Compute volume from radius, halfAxes and parameters
	 */
	public void calcVolume() {
		if (bottom == null) {
			volume = Double.NaN;
			return;
		}
		double pih = Math.PI * Math.abs(topParameter - bottomParameter);
		switch (type) {
		default:
		case QUADRIC_CYLINDER:
			if (bottom.halfAxes == null) {
				volume = radius * radius * pih;
			} else {
				volume = bottom.getHalfAxis(0) * bottom.getHalfAxis(1) * pih;
			}
			break;
		case QUADRIC_CONE:
			double h = Math.abs(topParameter - bottomParameter);

			if (bottom.halfAxes == null) {
				double r = radius * h; // "radius" is the radius value for h = 1
				volume = r * r * pih / 3;
			} else {
				volume = bottom.getHalfAxis(0) * bottom.getHalfAxis(1) * pih
						/ 3;
			}
			break;
		// default:
		// volume=Double.NaN;
		}
	}

	@Override
	public double getVolume() {
		if (defined) {
			return volume;
		}
		return Double.NaN;
	}

	@Override
	public boolean hasFiniteVolume() {
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		switch (type) {
		case QUADRIC_CYLINDER:
		case QUADRIC_CONE:
			return kernel.format(volume, tpl);
		case QUADRIC_EMPTY:
			return kernel.format(0, tpl);
		default:
			Log.debug("todo-GeoQuadric3DLimited");
			return "?";
		}
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		return new StringBuilder(toValueString(tpl));
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub
	}

	// ////////////////////////////////
	// NumberValue
	// ////////////////////////////////

	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	@Override
	public double getDouble() {
		return getVolume();
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	/**
	 * set top as silent part (not in construction)
	 */
	public void setSilentTop() {
		silentTop = true;

	}

	@Override
	public double getOrientedHeight() {
		return topParameter - bottomParameter;
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	protected void setColorVisualStyle(final GeoElement geo) {
		setObjColor(geo.getObjectColor());
		setAlphaValue(geo.getAlphaValue());
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		bottom.rotate(r, S);
		top.rotate(r, S);
		side.rotate(r, S);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);
	}

	@Override
	public void rotate(NumberValue r) {
		bottom.rotate(r);
		top.rotate(r);
		side.rotate(r);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);
	}

	@Override
	public void rotate(NumberValue r, Coords S,
			GeoDirectionND orientation) {

		((GeoConic3D) bottom).rotate(r, S, orientation);
		top.rotate(r, S, orientation);
		side.rotate(r, S, orientation);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		bottom.translate(v);
		top.translate(v);
		side.translate(v);

		// get infos from side
		origin = side.getMidpoint3D();
	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		bottom.mirror(Q);
		top.mirror(Q);
		side.mirror(Q);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);
	}

	@Override
	public void mirror(GeoLineND g) {
		bottom.mirror(g);
		top.mirror(g);
		side.mirror(g);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		((MirrorableAtPlane) bottom).mirror(plane);
		top.mirror(plane);
		side.mirror(plane);

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		bottom.dilate(rval, S);
		top.dilate(rval, S);
		side.dilate(rval, S);

		double r = Math.abs(rval.getDouble());
		volume *= r * r * r;
		bottomParameter *= r;
		topParameter *= r;

		// get infos from side
		origin = side.getMidpoint3D();
		direction = side.getEigenvec3D(2);

	}

	@Override
	final protected void singlePoint() {
		type = GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;

	}

	@Override
	public Coords getMidpoint2D() {
		return side.getMidpoint2D();
	}

	@Override
	public Coords getMidpoint() {
		return side.getMidpoint();
	}

	@Override
	public Coords getMidpoint3D() {
		return side.getMidpoint3D();
	}

	@Override
	public CoordMatrix getSymmetricMatrix() {
		return side.getSymmetricMatrix();
	}

	@Override
	public double getHalfAxis(int i) {
		return side.getHalfAxis(i);
	}

	@Override
	public int getDimension() {
		return side.getDimension();
	}

	@Override
	public Coords getEigenvec3D(int i) {
		return side.getEigenvec3D(i);
	}

	@Override
	public double[] getFlatMatrix() {
		return side.getFlatMatrix();
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	@Override
	public boolean setHighlighted(final boolean flag) {
		boolean ret = super.setHighlighted(flag);
		if (ret && bottom != null) {
			kernel.notifyUpdateHighlight(side);
			kernel.notifyUpdateHighlight(bottom);
			kernel.notifyUpdateHighlight(top);
		}
		return ret;
	}

	@Override
	public boolean setSelected(final boolean flag) {
		boolean ret = super.setSelected(flag);
		if (ret && bottom != null) {
			kernel.notifyUpdateHighlight(side);
			kernel.notifyUpdateHighlight(bottom);
			kernel.notifyUpdateHighlight(top);
		}
		return ret;
	}

	@Override
	public boolean showLineProperties() {
		return true;
	}
}