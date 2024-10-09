package com.himamis.retex.renderer.web;

import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;
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

	public FormulaRenderingResult drawLatex(JsPropertyMap<?> opts) {
		//ctx, latex, size, style, x, y, fgColor, bgColor, cb
		if (Js.isFalsy(opts.get("context")) && Js.isFalsy(opts.get("element"))) {
			throw new IllegalArgumentException("drawLatex(opts): opts.context must not be null");
		}
		Object latex = opts.get("latex");
		Object ascii = opts.get("ascii");
		if (!"string".equals(Js.typeof(latex)) && !"string".equals(Js.typeof(ascii))) {
			throw new IllegalArgumentException("drawLatex(opts): "
					+ "opts.latex or opts.ascii must be of type string.");
		}
		CanvasRenderingContext2D ctx = Js.uncheckedCast(getContext(opts));
		TeXFormula formula = ascii == null ? new TeXFormula((String) latex)
				: library.fromAsciiMath((String) ascii);
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
		FactoryProviderGWT.ensureLoaded();
		Insets insets = new Insets(topInset, leftInset, bottomInset, rightInset);
		HTMLCanvasElement canvas = (HTMLCanvasElement) opts.get("element");
		FormulaRenderingResult result = library.drawLatex(ctx, formula, size, type, x, y,
				insets, fgColor, bgColor, cb, canvas);
		if (canvas != null) {
			canvas.setAttribute("aria-label", latex == null ? (String) ascii : (String) latex);
		}
		return result;
	}

	private Object getContext(JsPropertyMap<?> opts) {
		return Js.isTruthy(opts.get("context"))	? opts.get("context")
				: ((HTMLCanvasElement) opts.get("element")).getContext("2d");
	}

	private int getInt(JsPropertyMap<?> opts, String key, int fallback) {
		Object val = opts.nestedGet(key);
		return val == null ? fallback : Js.asInt(val);
	}

	public void setFontBaseUrl(String url) {
		Opentype.setFontBaseUrl(url);
	}
}
