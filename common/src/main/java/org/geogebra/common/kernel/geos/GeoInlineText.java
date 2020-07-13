package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Inline Geo Text element.
 */
public class GeoInlineText extends GeoElement
		implements Translateable, TextStyle, PointRotateable, GeoInline {

	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 30;

	private GPoint2D location;
	private double width;
	private double height;

	private double angle;

	private double minHeight;

	private String content;
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
		super(c);
		this.location = location;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.contentDefaultSize = getCurrentFontSize();
	}

	/**
	 * Create inline text from standard text (for compatibility with first notes release)
	 * @param geoText standard text
	 */
	public GeoInlineText(GeoText geoText) {
		super(geoText.getConstruction());
		this.contentDefaultSize = getCurrentFontSize();
		location = new GPoint2D(geoText.getStartPoint().getInhomX(),
				geoText.getStartPoint().getInhomY());
		setContentFromText(geoText);
	}

	private int getCurrentFontSize() {
		return kernel.getApplication().getSettings().getFontSettings()
				.getAppFontSize();
	}

	@Override
	public GPoint2D getLocation() {
		return location;
	}

	@Override
	public void setLocation(GPoint2D location) {
		this.location = location;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getMinWidth() {
		return DEFAULT_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, DEFAULT_HEIGHT);
	}

	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * VERY IMPORTANT! If you are setting the content from the outside
	 * (i.e. not from Carota) do not forget to call updateContent on
	 * the Drawable
	 * @param content
	 *            JSON representation of the document (used by Carota)
	 */
	@Override
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
		return new GeoInlineText(cons, new GPoint2D(location.getX(), location.getY()));
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

		XMLBuilder.appendPosition(sb, this);
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
	public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
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

	/**
	 *
	 * @return the corresponding drawable.
	 */
	public DrawInlineText getDrawable() {
		return (DrawInlineText) kernel.getApplication()
				.getActiveEuclidianView().getDrawableFor(this);
	}

	/**
	 * @return format of individual words
	 */
	public JSONArray getFormat() {
		if (!StringUtil.empty(content)) {
			try {
				return new JSONArray(content);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new JSONArray();
	}

	@Override
	public double getFontSizeMultiplier() {
		DrawInlineText drawable = getDrawable();
		double viewFontSize = getCurrentFontSize();
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
				Log.debug(getCurrentFontSize());
			}
		}
		return false;
	}

	@Override
	public void rotate(NumberValue r) {
		angle -= r.getDouble();
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		angle -= r.getDouble();
		rotate(location, r, S);
	}

	protected static void rotate(GPoint2D location, NumberValue r, GeoPointND S) {
		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);
		double qx = S.getInhomCoords().getX();
		double qy = S.getInhomCoords().getY();

		double x = location.getX();
		double y = location.getY();

		location.setLocation((x - qx) * cos + (qy - y) * sin + qx,
				(x - qx) * sin + (y - qy) * cos + qy);
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void setSize(double w, double h) {
		width = w;
		height = h;
	}

    private void setContentFromText(GeoText geo) {
		int fontStyle = geo.getFontStyle();
		double fontSize = geo.getFontSizeMultiplier();
		try {
			JSONArray content = new JSONArray();
			JSONObject text = new JSONObject().put("text", geo.getTextString());
			content.put(text);
			if ((fontStyle & GFont.BOLD) > 0) {
				text.put("bold", true);
			}
			if ((fontStyle & GFont.ITALIC) > 0) {
				text.put("italic", true);
			}
			if (fontSize != 1.0) {
				text.put("size", fontSize * getCurrentFontSize());
			}
			if (!GColor.BLACK.equals(geo.getObjectColor())) {
				text.put("color", StringUtil.toHtmlColor(geo.getObjectColor()));
			}
			setContent(content.toString());
		} catch (JSONException e) {
			// unlikely
		}
    }
}
