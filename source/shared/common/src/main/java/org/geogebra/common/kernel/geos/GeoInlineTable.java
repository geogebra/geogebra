package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

public class GeoInlineTable extends GeoInline implements TextStyle, HasTextFormatter {

	private boolean defined = true;
	private String content;

	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 72;

	private static final int MIN_CELL_SIZE = 16;

	// By default two columns and rows
	private double minWidth = 2 * MIN_CELL_SIZE;
	private double minHeight = 2 * MIN_CELL_SIZE;

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 *
	 * @param location on-screen location
	 */
	public GeoInlineTable(Construction c, GPoint2D location) {
		super(c);
		setLocation(location);
		setContentWidth(DEFAULT_WIDTH);
		setContentHeight(DEFAULT_HEIGHT);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TABLE;
	}

	@Override
	public GeoElement copy() {
		GeoInlineTable copy = new GeoInlineTable(cons,
				new GPoint2D(getLocation().getX(), getLocation().getY()));
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoInlineTable) {
			this.content = ((GeoInlineTable) geo).content;
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
	public void setBackgroundColor(GColor backgroundColor) {
		((InlineTableController) getFormatter()).setBackgroundColor(backgroundColor);
	}

	@Override
	public GColor getBackgroundColor() {
		return ((InlineTableController) getFormatter()).getBackgroundColor();
	}

	@Override
	public int getFontStyle() {
		return GeoInlineText.getFontStyle(getFormatter());
	}

	@Override
	public double getFontSizeMultiplier() {
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public double getMinWidth() {
		return minWidth;
	}

	@Override
	public double getMinHeight() {
		return minHeight;
	}

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * @return first column of the table as list of doubles
	 */
	public List<Double> extractData(int column) {
		ArrayList<Double> values = new ArrayList<>();
		try {
			JSONArray rows = new JSONObject(content).getJSONArray("content");
			for (int i = 0; i < rows.length(); i++) {
				Double val = getCellValue(rows, i, column);
				if (val != null) {
					values.add(val);
				}
			}
		} catch (JSONException e) {
			Log.debug(e);
		}
		return values;
	}

	private Double getCellValue(JSONArray rows, int row, int column) {
		try {
			JSONObject cell = rows.getJSONArray(row).getJSONObject(column);
			JSONArray words = cell.getJSONArray("content");
			String cellContent = words.getJSONObject(0).getString("text").trim();
			return Double.parseDouble(cellContent);
		} catch (NumberFormatException | JSONException e) {
			return null;
		}
	}

	/** @param column index of column
	 * @return column and next column of the table as list of doubles
	 */
	public List<Double>[] extractTwoColumnData(int column) {
		List<Double> col0 = new ArrayList<>();
		List<Double> col1 = new ArrayList<>();

		try {
			JSONArray rows = new JSONObject(content).getJSONArray("content");
			for (int i = 0; i < rows.length(); i++) {
				Double val0 = getCellValue(rows, i, column);
				Double val1 = getCellValue(rows, i, column + 1);

				if (val0 != null && val1 != null) {
					col0.add(val0);
					col1.add(val1);
				}
			}
		} catch (JSONException e) {
			Log.debug(e);
		}

		if (col0.isEmpty()) {
			col1 = extractData(column);
			for (double x = 1; x <= col1.size(); x++) {
				col0.add(x);
			}
		}

		return new List[] {col0, col1};
	}
}
