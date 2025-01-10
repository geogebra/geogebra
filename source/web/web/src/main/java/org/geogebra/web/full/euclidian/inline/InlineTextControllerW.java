package org.geogebra.web.full.euclidian.inline;

import java.util.Locale;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.HasVerticalAlignment;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.FontLoader;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.richtext.Editor;
import org.geogebra.web.richtext.EditorChangeListener;
import org.geogebra.web.richtext.impl.CarotaEditor;
import org.geogebra.web.richtext.impl.CarotaUtil;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Widget;

import jsinterop.base.Js;

/**
 * Web implementation of the inline text controller.
 */
public class InlineTextControllerW implements InlineTextController {

	private static final String INVISIBLE = "invisible";
	private final GeoInline geo;

	private final Element parent;
	private Editor editor;
	private Style style;

	private int contentDefaultSize;
	private Element textareaWrapper;

	/**
	 * @param geo
	 *            text
	 * @param parent
	 *            parent div
	 */
	public InlineTextControllerW(GeoInline geo, EuclidianView view, Element parent) {
		this.geo = geo;
		this.parent = parent;
		CarotaUtil.ensureInitialized(view.getFontSize());
		if (view.getApplication().isMebis()) {
			CarotaUtil.setSelectionColor(GColor.MOW_SELECTION_COLOR.toString());
		}
		this.contentDefaultSize = getCurrentFontSize();
		checkFonts(getFormat(geo.getContent()), getWebFontsUrl(), getCallback());
	}

	/**
	 * @param s string inserted by user
	 * @param geo construction element
	 * @return string to be actually inserted
	 */
	public static String checkEncodedPaste(String s, GeoInline geo) {
		if (Js.isTruthy(s) && CopyPasteW.pasteIfEncoded(geo.getApp(), s)) {
			return null;
		}
		return s;
	}

	@Override
	public boolean updateFontSize() {
		if (contentDefaultSize != getCurrentFontSize()) {
			try {
				JSONArray words = getFormat(geo.getContent());
				for (int i = 0; i < words.length(); i++) {
					JSONObject word = words.optJSONObject(i);
					if (word.has("size")) {
						double size = word.getDouble("size")
								* getCurrentFontSize()
								/ contentDefaultSize;
						word.put("size", size);
					}
				}

				geo.setContent(words.toString());
				contentDefaultSize = getCurrentFontSize();
				return true;
			} catch (JSONException | RuntimeException e) {
				Log.debug(getCurrentFontSize());
			}
		}
		return false;
	}

	private int getCurrentFontSize() {
		return geo.getKernel().getApplication().getSettings().getFontSettings()
				.getAppFontSize();
	}

	/**
	 * @return format of individual words
	 */
	private JSONArray getFormat(String content) {
		if (!StringUtil.empty(content)) {
			try {
				return new JSONArray(content);
			} catch (JSONException e) {
				Log.debug(e);
			}
		}
		return new JSONArray();
	}

	private String getWebFontsUrl() {
		return ((AppW) geo.getApp()).getAppletParameters().getParamWebfontsUrl();
	}

	/**
	 * Check for bundled fonts in content, and load them
	 * @param words array of Murok runs
	 * @param baseUrl Url from where to load the font from
	 * @param callback to be executed after font is loaded
	 */
	public static void checkFonts(JSONArray words, String baseUrl, Runnable callback) {
		try {
			for (int i = 0; i < words.length(); i++) {
				JSONObject word = words.optJSONObject(i);
				if (word.has("font")) {
					FontLoader.loadFont(word.getString("font"), baseUrl, callback);
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
		editor.addInsertFilter(s -> checkEncodedPaste(s, geo));
		final Widget widget = editor.getWidget();
		widget.addStyleName(INVISIBLE);
		EventUtil.stopPointerEvents(widget.getElement(), btn -> btn <= 0);
		style = widget.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		Element editorElement = editor.getWidget().getElement();
		parent.appendChild(editorElement);
		// re-parent the textarea to make sure focus stays in view (MOW-1330)
		textareaWrapper = Dom.querySelectorForElement(editorElement, ".murokTextArea");
		parent.appendChild(textareaWrapper);

		updateContent();
		editor.setListener(new EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				String oldContent = geo.getContent();
				double oldHeight = geo.getHeight();
				double oldContentHeight = geo.getContentHeight();
				if (!content.equals(oldContent)) {
					geo.setContent(content);
					storeUndoAction(geo, oldHeight, oldContentHeight, oldContent);
					geo.notifyUpdate();
				}
			}

			@Override
			public void onInput() {
				double oldMinHeight = geo.getMinHeight();
				int actualMinHeight =
						(int) ((editor.getMinHeight() + 2 * DrawInlineText.PADDING) * geo.getWidth()
								/ geo.getContentWidth());
				if (oldMinHeight != actualMinHeight) {
					geo.setSize(geo.getWidth(), Math.max(actualMinHeight, geo.getHeight()));
					geo.setMinHeight(actualMinHeight);
					if (oldMinHeight < actualMinHeight) {
						geo.updateRepaint();
					}
					updateVerticalAlign();
				}
			}

			@Override
			public void onSelectionChanged() {
				geo.getKernel().notifyUpdateVisualStyle(geo, GProperty.TEXT_SELECTION);
				geo.getKernel().notifyRepaint();
			}

			@Override
			public void onEscape() {
				toBackground();
			}
		});
	}

	private void storeUndoAction(GeoInline geo, double oldHeight, double oldContentHeight,
			String oldContent) {
		if (oldContent != null) {
			String label = geo.getLabelSimple();
			geo.getConstruction().getUndoManager()
					.buildAction(ActionType.SET_CONTENT, label, Double.toString(geo.getHeight()),
							Double.toString(geo.getContentHeight()), geo.getContent())
					.withUndo(ActionType.SET_CONTENT, label, Double.toString(oldHeight),
							Double.toString(oldContentHeight), oldContent)
					.withLabels(label)
					.storeAndNotifyUnsaved();
		} else {
			geo.getConstruction().getUndoManager().storeAddGeo(geo);
		}
	}

	@Override
	public GeoInline getInline() {
		return geo;
	}

	private void updateVerticalAlign() {
		style.setPaddingTop(getValignPadding(), Unit.PX);
	}

	@Override
	public void discard() {
		editor.getWidget().getElement().removeFromParent();
		textareaWrapper.removeFromParent();
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Unit.PX);
		style.setTop(y, Unit.PX);
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
		style.setWidth(width, Unit.PX);
		editor.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		style.setHeight(height, Unit.PX);
	}

	@Override
	public void toBackground() {
		editor.deselect();
		if (!editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			editor.getWidget().addStyleName(INVISIBLE);
			textareaWrapper.removeFromParent(); // make sure no editable element on Android
			geo.updateRepaint();
			geo.unlockForMultiuser();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		editor.getWidget().removeStyleName(INVISIBLE);
		parent.appendChild(textareaWrapper);
		editor.focus(x, y);
	}

	@Override
	public void format(String key, Object val) {
		editor.format(key, val);
		saveContent();
		geo.updateRepaint();
		if ("font".equals(key)) {
			FontLoader.loadFont(String.valueOf(val), getWebFontsUrl(), getCallback());
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
	public void draw(GGraphics2D g2) {
		GAffineTransform res = AwtFactory.getTranslateInstance(DrawInlineText.PADDING,
				DrawInlineText.PADDING + getValignPadding());
		g2.transform(res);
		g2.setColor(GColor.BLACK);
		editor.draw(((GGraphics2DWI) g2).getContext());
	}

	private int getValignPadding() {
		if (getVerticalAlignment() == VerticalAlignment.MIDDLE) {
			return (int) (geo.getContentHeight() - editor.getMinHeight()) / 2
					- DrawInlineText.PADDING;
		} else if (getVerticalAlignment() == VerticalAlignment.BOTTOM) {
			return (int) (geo.getContentHeight() - editor.getMinHeight())
					- 2 * DrawInlineText.PADDING;
		} else {
			return 0;
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
	public VerticalAlignment getVerticalAlignment() {
		return ((HasVerticalAlignment) geo).getVerticalAlignment();
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment alignment) {
		((HasVerticalAlignment) geo).setVerticalAlignment(alignment);
		updateVerticalAlign();
		geo.updateRepaint();
	}

	@Override
	public HorizontalAlignment getHorizontalAlignment() {
		return HorizontalAlignment.fromString(getFormat("align", "left"));
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		format("align", alignment.name().toLowerCase(Locale.US));
	}

	@Override
	public void switchListTo(String listType) {
		editor.switchListTo(listType);
	}

	@Override
	public String getListStyle() {
		return editor.getListStyle();
	}

	@Override
	public void setTransform(double angle, double sx, double sy) {
		updateVerticalAlign();
		style.setProperty("transform", "rotate(" + angle + "rad) scale(" + sx + "," + sy + ")");
		editor.setExternalScale(sx);
	}

	@Override
	public boolean isEditing() {
		return !editor.getWidget().getElement().hasClassName(INVISIBLE);
	}

}
