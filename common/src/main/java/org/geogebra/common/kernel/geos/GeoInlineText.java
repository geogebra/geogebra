package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
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
public class GeoInlineText extends GeoInline implements TextStyle, HasTextFormatter {

	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 30;

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
		setLocation(location);
		setWidth(DEFAULT_WIDTH);
		setHeight(DEFAULT_HEIGHT);
		this.contentDefaultSize = getCurrentFontSize();
	}

	/**
	 * Create inline text from standard text (for compatibility with first notes release)
	 * @param geoText standard text
	 */
	public GeoInlineText(GeoText geoText) {
		super(geoText.getConstruction());
		this.contentDefaultSize = getCurrentFontSize();
		setLocation(new GPoint2D(geoText.getStartPoint().getInhomX(),
				geoText.getStartPoint().getInhomY()));
		setContentFromText(geoText);
	}

	private int getCurrentFontSize() {
		return kernel.getApplication().getSettings().getFontSettings()
				.getAppFontSize();
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

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.INLINE_TEXT;
	}

	@Override
	public GeoElement copy() {
		return new GeoInlineText(cons, new GPoint2D(getLocation().getX(), getLocation().getY()));
	}

	@Override
	public void set(GeoElementND geo) {
		cons = geo.getConstruction();
		if (geo instanceof GeoInlineText) {
			GeoInlineText text = (GeoInlineText) geo;
			setLocation(text.getLocation());
			setWidth(text.getWidth());
			setHeight(text.getHeight());
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
	public int getFontStyle() {
		return getFontStyle(getFormatter());
	}

	/**
	 * Compute the font style of the inline object
	 * @param hasTextFormat inline object with format (text, table)
	 * @return font style (see GFont)
	 */
	public static int getFontStyle(HasTextFormat hasTextFormat) {
		try {
			boolean bold = hasTextFormat.getFormat("bold", false);
			boolean italic = hasTextFormat.getFormat("italic", false);
			boolean underline = hasTextFormat.getFormat("underline", false);
			return ((bold ? GFont.BOLD : 0) | (italic ? GFont.ITALIC : 0)) | (underline
					? GFont.UNDERLINE : 0);
		} catch (RuntimeException e) {
			Log.warn("No format for " + hasTextFormat);
		}

		return GFont.PLAIN;
	}

	@Override
	public HasTextFormat getFormatter() {
		DrawInlineText drawable = (DrawInlineText) kernel.getApplication()
				.getActiveEuclidianView().getDrawableFor(this);
		return drawable == null ? null : drawable.getTextController();
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
		HasTextFormat drawable = getFormatter();
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
