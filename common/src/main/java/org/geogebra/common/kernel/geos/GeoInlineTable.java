package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

public class GeoInlineTable extends GeoElement implements TextStyle, GeoInline {

	private ArrayList<ArrayList<String>> contents = new ArrayList<>();
	private boolean defined = true;

	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 72;

	private GPoint2D location;
	private double width;
	private double height;

	private double minHeight;

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 *
	 * @param location on-screen location
	 */
	public GeoInlineTable(Construction c, GPoint2D location) {
		super(c);
		this.location = location;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		ensureSize(2, 2);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TABLE;
	}

	@Override
	public GeoElement copy() {
		GeoInlineTable copy = new GeoInlineTable(cons,  new GPoint2D(location.getX(), location.getY()));
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoInlineTable) {
			contents.clear();
			for (ArrayList<String> row : ((GeoInlineTable) geo).contents) {
				contents.add(new ArrayList<>(row));
			}
		} else {
			setUndefined();
		}
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
		return null;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		return geo == this;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	public String getContents(int row, int col) {
		return contents.get(row).get(col);
	}

	public int getColumns() {
		return contents.isEmpty() ? 0 : contents.get(0).size();
	}

	public int getRows() {
		return contents.size();
	}

	public void setContents(int row, int col, String value) {
		contents.get(row).set(col, value);
	}

	@Override
	public void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		sb.append("\t<table columns=\"");
		sb.append(getColumns());
		int counter = 0;
		for (ArrayList<String> row: contents) {
			for (String cell: row) {
				sb.append("\" cell").append(counter).append("=\"")
						.append(StringUtil.encodeXML(cell));
				counter++;
			}
		}
		sb.append("\"/>\n");

		XMLBuilder.appendPosition(sb, this);
	}

	/**
	 * @param columns columns
	 * @param rows rows
	 */
	public void ensureSize(int columns, int rows) {
		for (int i = contents.size(); i < rows; i++) {
			contents.add(new ArrayList<String>());
		}
		for (ArrayList<String> row : contents) {
			for (int j = row.size(); j < columns; j++) {
				row.add("");
			}
		}
	}

	@Override
	public int getFontStyle() {
		return GFont.PLAIN;
	}

	@Override
	public double getFontSizeMultiplier() {
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getAngle() {
		return 0;
	}

	@Override
	public GPoint2D getLocation() {
		return location;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public void setAngle(double angle) {
		// nothing for now
	}

	@Override
	public void setLocation(GPoint2D location) {
		this.location = location;
	}

	@Override
	public void setContent(String content) {
		// nothing for now
	}

	@Override
	public double getMinWidth() {
		return DEFAULT_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, DEFAULT_HEIGHT);
	}
}
