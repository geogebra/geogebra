package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

public class GeoEmbed extends GeoElement implements Locateable {

	private boolean defined = true;
	private GeoPoint[] corner;
	private int embedID;
	private double w = 800;
	private double h = 600;
	/**
	 * @param c
	 *            construction
	 */
	public GeoEmbed(Construction c) {
		super(c);
		corner = new GeoPoint[3];
	}

	public void initPosition(EuclidianViewInterfaceCommon ev) {
		double x = ev.toRealWorldCoordX(ev.getViewWidth() / 2) - w / ev.getXscale() / 2;
		double y = ev.toRealWorldCoordY(ev.getViewHeight() / 2) - h / ev.getYscale() / 2;
		corner[0] = new GeoPoint(cons);
		corner[0].setCoords(x, y, 1);
		corner[1] = new GeoPoint(cons);
		corner[1].setCoords(x + w / ev.getXscale(), y, 1);
		corner[2] = new GeoPoint(cons);
		corner[2].setCoords(x, y + h / ev.getXscale(), 1);
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
		if (corner[i] == null) {
			Log.printStacktrace("No corner " + i);
			GeoPoint ret = new GeoPoint(cons);
			ret.setCoords(0, 0, 1);
			return ret;
		}
		return corner[i];
	}

	@Override
	public void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<embed id=\"");
		sb.append(embedID);
		sb.append("\"/>\n");
		for (int i = 0; i < corner.length; i++) {
			XMLBuilder.getCornerPointXML(sb, i, corner);
		}
	}

	/**
	 * @return embed ID: needs to be unique in construction
	 */
	public int getEmbedID() {
		return embedID;
	}

	/**
	 * @param embedID
	 *            embed ID: needs to be unique in construction
	 */
	public void setEmbedId(int embedID) {
		this.embedID = embedID;
	}

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {
		corner[0] = (GeoPoint) p;
	}

	@Override
	public void removeStartPoint(GeoPointND p) {
		// TODO Auto-generated method stub
	}

	@Override
	public GeoPointND getStartPoint() {
		return corner[0];
	}

	@Override
	public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException {
		corner[number] = (GeoPoint) p;
	}

	@Override
	public GeoPointND[] getStartPoints() {
		return corner;
	}

	@Override
	public void initStartPoint(GeoPointND p, int number) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasAbsoluteLocation() {
		return false;
	}

	@Override
	public boolean isAlwaysFixed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWaitForStartPoint() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLocation() {
		// TODO Auto-generated method stub

	}

}
