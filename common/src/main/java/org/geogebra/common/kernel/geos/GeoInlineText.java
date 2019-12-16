package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Inline Geo Text element.
 */
public class GeoInlineText extends GeoElement
		implements Translateable, TextStyle {

	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 30;

	private GPoint2D location;
	private double width;
	private double height;

	private String content;

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

	public void setLocation(GPoint2D location) {
		this.location = location;
	}

	/**
	 * Get the widht of the element.
	 *
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Get the height of the element.
	 *
	 * @return height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Set the width of the element.
	 *
	 * @param width element width in pixels
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Set the height of the element.
	 *
	 * @param height height in pixels
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
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

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		getXMLfixedTag(sb);
		getXMLvisualTags(sb);

		sb.append("\t<content val=\"");
		StringUtil.encodeXML(sb, content);
		sb.append("\"/>\n");

		sb.append("\t<startPoint x=\"");
		sb.append(location.getX());
		sb.append("\" y=\"");
		sb.append(location.getY());
		sb.append("\"/>\n");

		sb.append("\t<dimensions width=\"");
		sb.append(width);
		sb.append("\" height=\"");
		sb.append(height);
		sb.append("\"/>\n");
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

	@Override
	public void translate(Coords v) {
		location.setLocation(location.getX() + v.getX(), location.getY() + v.getY());
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public int getFontStyle() {
		JSONObject firstWord = getFormat();
		if (firstWord != null) {
			boolean bold = firstWord.optBoolean("bold");
			boolean italic = firstWord.optBoolean("italic");
			return (bold ? GFont.BOLD : 0) | (italic ? GFont.ITALIC : 0);
		}

		return GFont.PLAIN;
	}

	private JSONObject getFormat() {
		if (!StringUtil.empty(content)) {
			try {
				JSONArray json = new JSONArray(content);
				return json.optJSONObject(0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public double getFontSizeMultiplier() {
		JSONObject firstWord = getFormat();
		if (firstWord != null) {
			int viewFontSize = kernel.getApplication()
					.getActiveEuclidianView().getFontSize();
			double size = firstWord.optDouble("size", viewFontSize);
			return size / viewFontSize;
		}
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}
}
