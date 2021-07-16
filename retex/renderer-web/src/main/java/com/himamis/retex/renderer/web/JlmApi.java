package com.himamis.retex.renderer.web;

import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType
public class JlmApi {

	protected final JlmLib library;

	@SuppressWarnings("unusable-by-js")
	public JlmApi(JlmLib library) {
		this.library = library;
	}

	public void initWith(String str) {
		library.initWith(str);
	}

	public final int SERIF = TeXFont.SERIF;
	public final int SANSSERIF = TeXFont.SANSSERIF;
	public final int BOLD = TeXFont.BOLD;
	public final int ITALIC = TeXFont.ITALIC;
	public final int ROMAN = TeXFont.ROMAN;
	public final int TYPEWRITER = TeXFont.TYPEWRITER;

	public JsPropertyMap<Object> drawLatex(JsPropertyMap opts) {
		//ctx, latex, size, style, x, y, fgColor, bgColor, cb
		if (Js.isFalsy(opts.get("context"))) {
			throw new IllegalArgumentException("drawLatex(opts): opts.context must not be null");
		}
		if (!"string".equals(Js.typeof(opts.get("latex")))) {
			throw new IllegalArgumentException("drawLatex(opts): opts.latex must be of type string.");
		}
		CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) opts.get("context");
		String latex = (String) opts.get("latex");
		int size = getInt(opts, "size", 12);
		int type = getInt(opts, "type", 0);
		int x = getInt(opts, "x", 0);
		int y = getInt(opts, "y", 0);
		int topInset = getInt(opts, "insets.top", 0);
		int bottomInset = getInt(opts, "insets.bottom", 0);
		int leftInset = getInt(opts, "insets.left", 0);
		int rightInset = getInt(opts, "insets.right", 0);
		String fgColor = opts.get("foregroundColor") == null ? "#000000" : (String) opts.get("foregroundColor");
		String bgColor = (String) opts.get("backgroundColor"); // undefined === invisible
		DrawingFinishedCallback cb = Js.uncheckedCast(opts.get("callback"));

		return library.drawLatex(ctx, latex, size, type, x, y,
				topInset, leftInset, bottomInset, rightInset, fgColor, bgColor, cb);
	}

	private int getInt(JsPropertyMap opts, String key, int fallback) {
		Object val = opts.nestedGet(key);
		return val == null ? fallback : Js.asInt(val);
	}

	public void setFontBaseUrl(String url) {
		Opentype.setFontBaseUrl(url);
	}
}
