package org.geogebra.web.editor;

import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.html5.export.Canvas2Svg;
import org.geogebra.web.html5.export.ExportLoader;

import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.web.graphics.ColorW;

import elemental2.core.Global;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;

public class MathFieldExporter {
	private static final String SVG_PREFIX = "data:image/svg+xml;utf8,";
	private final MathFieldW mathField;

	/** Image consumer */
	@JsFunction
	public interface ImageConsumer {
		/**
		 * @param image exported image
		 */
		void accept(EquationExportImage image);
	}

	public MathFieldExporter(MathFieldW mathField) {
		this.mathField = mathField;
	}

	/**
	 * @param type  export type (svg supported, error otherwise)
	 * @param transparent  whether to use transparent background
	 * @param callback  callback
	 */
	public void export(String type, boolean transparent, ImageConsumer callback) {
		EquationExportImage ret = new EquationExportImage();
		if (!"svg".equals(type)) {
			ret.setError("Only type = 'svg' is supported");
			callback.accept(ret);
			return;
		}

		ExportLoader.onCanvas2SvgLoaded(() -> {
			mathField.repaintWeb();

			int height = mathField.getIconHeight();
			int depth = mathField.getIconDepth();
			int width = mathField.getIconWidth();
			if (height < 1 || width < 1) {
				ret.setError("Invalid dimensions");
				callback.accept(ret);
				return;
			}
			Canvas2Svg ctx = new Canvas2Svg(width, height);
			CursorBox.setBlink(false);
			ColorW bgColor = transparent ? null : mathField.getBackgroundColor();
			mathField.paintFormulaNoPlaceholder(Js.uncheckedCast(ctx), 0, bgColor);
			ret.setBaseline((height - depth) / (double) height);
			ret.setSvg(SVG_PREFIX + Global.escape(ctx.getSerializedSvg(true)));

			callback.accept(ret);
		});
	}
}
