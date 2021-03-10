package org.geogebra.web.full.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.FontLoader;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.richtext.Editor;
import org.geogebra.web.richtext.EditorChangeListener;
import org.geogebra.web.richtext.impl.CarotaEditor;
import org.geogebra.web.richtext.impl.CarotaUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of the inline text controller.
 */
public class InlineTextControllerW implements InlineTextController {

	private static final String INVISIBLE = "invisible";
	private GeoInlineText geo;

	private Element parent;
	private Editor editor;
	private Style style;
	private final GBasicStroke border1 = AwtFactory.getPrototype().newBasicStroke(1f,
	GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);
	private final GBasicStroke border3 = AwtFactory.getPrototype().newBasicStroke(3f,
			GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);

	/**
	 * @param geo
	 *            text
	 * @param parent
	 *            parent div
	 */
	public InlineTextControllerW(GeoInlineText geo, EuclidianView view, Element parent) {
		this.geo = geo;
		this.parent = parent;
		CarotaUtil.ensureInitialized(view.getFontSize());
		if (view.getApplication().isMebis()) {
			CarotaUtil.setSelectionColor(GColor.MOW_SELECTION_COLOR.toString());
		}
		checkFonts(geo.getFormat(), getCallback());
	}

	/**
	 * Check for bundled fonts in content, and load them
	 * @param words array of Murok runs
	 * @param callback to be executed after font is loaded
	 */
	public static void checkFonts(JSONArray words, Runnable callback) {
		try {
			for (int i = 0; i < words.length(); i++) {
				JSONObject word = words.optJSONObject(i);
				if (word.has("font")) {
					FontLoader.loadFont(word.getString("font"), callback);
				}
			}
		} catch (JSONException | RuntimeException e) {
			Log.debug("cannot parse fonts");
		}
	}

	private Runnable getCallback() {
		return () -> {
			editor.reload();
			geo.getKernel().notifyRepaint();
		};
	}

	@Override
	public void create() {
		editor = new CarotaEditor(DrawInlineText.PADDING);
		final Widget widget = editor.getWidget();
		widget.addStyleName(INVISIBLE);
		style = widget.getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		parent.appendChild(editor.getWidget().getElement());

		updateContent();
		editor.setListener(new EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				if (!content.equals(geo.getContent())) {
					geo.setContent(content);
					geo.getKernel().storeUndoInfo();
					geo.notifyUpdate();
				}
			}

			@Override
			public void onInput() {
				int actualMinHeight = editor.getMinHeight() + 2 * DrawInlineText.PADDING;
				if (geo.getMinHeight() != actualMinHeight) {
					geo.setSize(geo.getWidth(), Math.max(actualMinHeight, geo.getHeight()));
					geo.setMinHeight(actualMinHeight);
					geo.updateRepaint();
				}
			}

			@Override
			public void onSelectionChanged() {
				geo.getKernel().notifyUpdateVisualStyle(geo, GProperty.TEXT_SELECTION);
			}
		});
	}

	@Override
	public void discard() {
		editor.getWidget().getElement().removeFromParent();
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void updateContent() {
		if (geo.getContent() != null && !geo.getContent().isEmpty()) {
			editor.setContent(geo.getContent());
		}
	}

	@Override
	public void updateContentIfChanged() {
		if (geo.getContent() != null && !geo.getContent().equals(editor.getContent())) {
			updateContent();
		}
	}

	@Override
	public void setWidth(int width) {
		style.setWidth(width, Style.Unit.PX);
		editor.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		style.setHeight(height, Style.Unit.PX);
	}

	@Override
	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	@Override
	public void toBackground() {
		editor.deselect();
		if (!editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			editor.getWidget().addStyleName(INVISIBLE);
			geo.updateRepaint();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		editor.getWidget().removeStyleName(INVISIBLE);
		editor.focus(x, y);
	}

	@Override
	public void format(String key, Object val) {
		editor.format(key, val);
		saveContent();
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
		if ("font".equals(key)) {
			FontLoader.loadFont(String.valueOf(val), getCallback());
		}
	}

	@Override
	public void saveContent() {
		geo.setContent(editor.getContent());
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return editor.getFormat(key, fallback);
	}

	@Override
	public String getHyperLinkURL() {
		return editor.getHyperLinkURL();
	}

	@Override
	public String getHyperlinkRangeText() {
		return editor.getHyperlinkRangeText();
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {
		g2.saveTransform();

		g2.transform(transform);

		if (geo.getBackgroundColor() != null) {
			g2.setPaint(geo.getBackgroundColor());
			g2.fillRect(0, 0, (int) geo.getWidth(), (int) geo.getHeight());
		}
		if (geo.getBorderThickness() != GeoInlineText.NO_BORDER) {
			g2.setPaint(geo.getBorderColor());
			g2.setStroke(getBorderStroke());
			g2.drawRect(0, 0, (int) geo.getWidth(), (int) geo.getHeight());
		}
		if (editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			GAffineTransform res = AwtFactory.getTranslateInstance(DrawInlineText.PADDING,
					DrawInlineText.PADDING);
			g2.transform(res);
			g2.setColor(GColor.BLACK);
			editor.draw(((GGraphics2DWI) g2).getContext());
		}

		g2.restoreTransform();
	}

	private GBasicStroke getBorderStroke() {
		if (geo.getBorderThickness() == 1) {
			return border1;
		} else {
			return border3;
		}
 	}

	@Override
	public void insertHyperlink(String url, String text) {
		editor.insertHyperlink(url, text);
	}

	@Override
	public void setHyperlinkUrl(String url) {
		editor.setHyperlinkUrl(url);
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return editor.urlByCoordinate(x, y);
	}

	@Override
	public boolean copySelection() {
		String text = editor.getSelectionRangeText();
		return CopyPasteW.writeToExternalClipboardIfNonempty(text);
	}

	@Override
	public void setSelectionText(String text) {
		editor.setSelection(text);
	}

	@Override
	public void switchListTo(String listType) {
		editor.switchListTo(listType);
	}

	@Override
	public String getListStyle() {
		return editor.getListStyle();
	}

}
