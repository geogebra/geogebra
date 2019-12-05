package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Inline Geo Text element.
 */
public class GeoInlineText extends GeoElement {

	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 30;

	private GPoint2D location;
	private int width;
	private int height;

	/**
	 * Creates new GeoInlineText instance.
	 *
	 * @param c construction
	 */
	public GeoInlineText(Construction c, GPoint2D location) {
		this(c, location, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * Creates new GeoInlineText instance.
	 * @param c construction
	 * @param location location
	 * @param width width
	 * @param height height
	 */
	public GeoInlineText(Construction c, GPoint2D location, int width, int height) {
		super(c);
		this.location = location;
		this.width = width;
		this.height = height;
	}

	/**
	 * Get the location of the text.
	 *
	 * @return location
	 */
	public GPoint2D getLocation() {
		return location;
	}

	/**
	 * Get the widht of the element.
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the element.
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set the width of the element.
	 *
	 * @param width element width in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Set the height of the element.
	 *
	 * @param height height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.INLINE_TEXT;
	}

	@Override
	public GeoElement copy() {
		return new GeoInlineText(cons,
				AwtFactory.getPrototype().newPoint2D(location.getX(), location.getY()));
	}

	@Override
	public void set(GeoElementND geo) {
		cons = geo.getConstruction();
		if (geo instanceof GeoInlineText) {
			GeoInlineText text = (GeoInlineText) geo;
			location = text.location;
			width = text.width;
			height = text.height;
		}
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() {
		// unimplemented
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_BOUNDARY;
	}
}
