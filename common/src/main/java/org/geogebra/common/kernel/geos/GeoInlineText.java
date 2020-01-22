package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInlineText;
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
import org.geogebra.common.util.debug.Log;

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

	private double minHeight;

	private String content = "[]";
	private int contentDefaultSize;

	/**
	 * Creates new GeoInlineText instance.
	 *
	 * @param c
	 *            construction
	 * @param location
	 *            on-screen location
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
		this.contentDefaultSize = getCurrentFontSize();
	}

	private int getCurrentFontSize() {
		return kernel.getApplication().getSettings().getFontSettings()
				.getAppFontSize();
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
	 * @param location
	 *            on-screen location
	 */
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

	public double getMinHeight() {
		return Math.max(minHeight, DEFAULT_HEIGHT);
	}

	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * @param content
	 *            JSON representation of the document (used by Carota)
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return JSON representation of the document (used by Carota)
	 */
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
		DrawInlineText drawable = getDrawable();
		try {
			boolean bold = drawable.getFormat("bold", false);
			boolean italic = drawable.getFormat("italic", false);
			boolean underline = drawable.getFormat("underline", false);
			return ((bold ? GFont.BOLD : 0) | (italic ? GFont.ITALIC : 0)) | (underline
					? GFont.UNDERLINE : 0);
		} catch (RuntimeException e) {
			Log.warn("No format for " + this);
		}

		return GFont.PLAIN;
	}

	private DrawInlineText getDrawable() {
		return (DrawInlineText) kernel.getApplication()
				.getActiveEuclidianView().getDrawableFor(this);
	}

	private JSONArray getFormat() {
		if (!StringUtil.empty(content)) {
			try {
				JSONArray json = new JSONArray(content);
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new JSONArray();
	}

	@Override
	public double getFontSizeMultiplier() {
		DrawInlineText drawable = getDrawable();
		double viewFontSize = kernel.getApplication().getActiveEuclidianView().getFontSize();
		double defaultMultiplier = GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
		try {
			double size = drawable.getFormat("size", viewFontSize * defaultMultiplier);
			return size / viewFontSize;
		} catch (RuntimeException e) {
			Log.warn("No format for " + this);
		}
		return defaultMultiplier;
	}

	/**
	 * @return whether size change was needed
	 */
	public boolean updateFontSize() {
		if (contentDefaultSize != getCurrentFontSize()) {
			try {
				JSONArray words = getFormat();
				for (int i = 0; i < words.length(); i++) {
					JSONObject word = words.optJSONObject(i);
					if (word.has("size")) {
						double size = word.getDouble("size")
								* getCurrentFontSize()
								/ contentDefaultSize;
						word.put("size", size);
					}
				}

				content = words.toString();
				contentDefaultSize = getCurrentFontSize();
				return true;
			} catch (JSONException | RuntimeException e) {
				Log.debug(e);
			}
		}
		return false;
	}

}
