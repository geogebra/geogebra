package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class GeoEmbed extends GeoElement {

	private boolean defined = true;
	private GeoPoint[] corner;
	private GeoPoint corner2;
	private GeoPoint corner4;

	/**
	 * @param c
	 *            construction
	 */
	public GeoEmbed(Construction c) {
		super(c);
		corner = new GeoPoint[3];
		corner[0] = new GeoPoint(c);
		corner[0].setCoords(-5, -5, 1);
		corner[1] = new GeoPoint(c);
		corner[1].setCoords(5, -5, 1);
		corner[2] = new GeoPoint(c);
		corner[2].setCoords(-5, 5, 1);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.EMBED;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.UNKNOWN;
	}

	@Override
	public GeoElement copy() {
		GeoEmbed ret = new GeoEmbed(cons);
		ret.set(this);
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
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

	public GeoPoint getCorner(int i) {
		return corner[i];
	}

}
