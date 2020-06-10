package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Base class for locateable geos like button and video.
 *
 */
public abstract class GeoWidget extends GeoElement implements Translateable {

	/** Corners */
	protected final GeoPointND startPoint;

	private double width;
	private double height;

	/**
	 * @param c
	 *            the construction.
	 */
	public GeoWidget(Construction c) {
		super(c);
		startPoint = new GeoPoint(c);
	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(GeoPointND startPoint) {
		this.startPoint.set(startPoint);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	@Override
	public void setUndefined() {
		// do nothing
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public boolean showInEuclidianView() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return !isIndependent();
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	public void translate(Coords v) {
		startPoint.translate(v);
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<startPoint x=\"")
			.append(startPoint.getInhomX())
			.append("\" y=\"")
			.append(startPoint.getInhomY())
			.append("\">\n");
	}
}
