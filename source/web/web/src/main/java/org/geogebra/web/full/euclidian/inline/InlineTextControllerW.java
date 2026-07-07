/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.euclidian.inline;

import java.util.Locale;
import java.util.Objects;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.HasVerticalAlignment;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.main.undo.UndoableDeletionExecutor;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.awt.GGraphics2DWI;
import org.geogebra.web.html5.euclidian.FontLoader;
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

import elemental2.core.Global;
import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Web implementation of the inline text controller.
 */
public class InlineTextControllerW implements InlineTextController {

	private static final String INVISIBLE = "invisible";
	private final GeoInline geo;

	private final Element parent;
	private Editor editor;
	private Style style;

	private Element textareaWrapper;
	private String lastNonemptyContent;
	private final EuclidianView view;

	/**
	 * @param geo
	 *            text
	 * @param parent
	 *            parent div
	 */
	public InlineTextControllerW(GeoInline geo, EuclidianView view, Element parent) {
		this.geo = geo;
		this.parent = parent;
		this.view = view;
		CarotaUtil.ensureInitialized(FontSettings.DEFAULT_FONT_SIZE);
		if (view.getApplication().isByCS()) {
			CarotaUtil.setSelectionColor(GColor.MOW_SELECTION_COLOR.toString());
		}
		checkFonts(getFormat(geo.getContent()), getWebFontsUrl(), this::onFontLoaded);
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

	private void onFontLoaded() {
		editor.reload();
		view.getKernel().notifyRepaint();
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
				onEditorChange(content);
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
				toBackground(DrawInline.SuspensionTrigger.BLUR);
			}
		});
	}

	private void onEditorChange(String content) {
		String oldContent = geo.getContent();
		double oldHeight = geo.getHeight();
		double oldContentHeight = geo.getContentHeight();
		if (!content.equals(oldContent)) {
			geo.setContent(content);
			if (isNonemptyDocument(content)) {
				lastNonemptyContent = content;
				storeUndoAction(geo, oldHeight, oldContentHeight, oldContent);
			}
			geo.notifyUpdate();
		}
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
		String content = geo.getContent();
		if (content != null && !content.isEmpty()) {
			if (isNonemptyDocument(content)) {
				lastNonemptyContent = content;
			}
			editor.setContent(content);
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
	public void toBackground(DrawInline.SuspensionTrigger trigger) {
		editor.deselect();
		if (!editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			String content = editor.getContent();
			editor.getWidget().addStyleName(INVISIBLE);
			textareaWrapper.removeFromParent(); // make sure no editable element on Android
			if (isNonemptyDocument(content)) {
				onEditorChange(content);
				geo.updateRepaint();
				geo.unlockForMultiuser();
			} else if (lastNonemptyContent != null
					&& trigger == DrawInline.SuspensionTrigger.BLUR) {
				geo.setContent(lastNonemptyContent);
				UndoableDeletionExecutor undoableDeletionExecutor =
						new UndoableDeletionExecutor();
				undoableDeletionExecutor.delete(geo);
				undoableDeletionExecutor.storeUndoAction(view.getKernel());
			} else if (trigger == DrawInline.SuspensionTrigger.BLUR) {
				// this was added to construction but not to undo stack => just remove
				geo.remove();
			}
		}
	}

	private boolean isNonemptyDocument(String content) {
		JsArray<?> parts = Js.uncheckedCast(Global.JSON.parse(content));
		if (parts == null) {
			return false;
		}
		for (int i = 0; i < parts.length; i++) {
			JsPropertyMap<Object> part = Objects.requireNonNull(Js.asPropertyMap(parts.at(i)));
			String text = Js.uncheckedCast(part.get("text"));
			if (!StringUtil.emptyTrim(text)) {
				return true;
			}
		}
		return false;
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
			FontLoader.loadFont(String.valueOf(val), getWebFontsUrl(), this::onFontLoaded);
		}
	}

	@Override
	public void formatFont(String val) {
		format("font", val);
		if (val != null && val.contains("By")) {
			format("bold", false);
			format("italic", false);
			format("underline", false);
		}
	}

	@Override
	public boolean hasIndeterminableFont() {
		return editor.getFormat("font", "").isEmpty();
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

	@Override
	public boolean hasContent() {
		return isNonemptyDocument(editor.getContent());
	}

}
