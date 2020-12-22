package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class GeoPieChart extends GeoElement {
	private final ArrayList<Double> data = new ArrayList<>();
	private GPoint2D center;
	private double radius = 3;

	/**
	 * Creates new GeoElement for given construction
	 * @param c Construction
	 */
	public GeoPieChart(Construction c) {
		super(c);
		setAlphaValue(1);
		setLineThickness(0);
		setLabelVisible(false);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PIECHART;
	}

	@Override
	public GeoElement copy() {
		GeoPieChart copy = new GeoPieChart(cons);
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoPieChart) {
			data.clear();
			GeoPieChart pieChart = (GeoPieChart) geo;
			data.addAll(pieChart.data);
			radius = pieChart.radius;
			center = pieChart.center;
		} else {
			setUndefined();
		}
	}

	@Override
	public boolean isDefined() {
		return !data.isEmpty();
	}

	@Override
	public void setUndefined() {
		data.clear();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLabelSimple() + tpl.getEqualsWithSpace() + getDefinition(tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return getDefinition(tpl);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	public ArrayList<Double> getData() {
		return data;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setCenter(GPoint2D center) {
		this.center = center;
	}

	public GPoint2D getCenter() {
		return center;
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		return DescriptionMode.DEFINITION;
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean showLineProperties() {
		return true;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	@Override
	public boolean isFixable() {
		return false; // consistent with barcharts
	}

	@Override
	protected void getXMLtags(final StringBuilder sb) {
		super.getXMLtags(sb);
		getLineStyleXML(sb);
	}

}
