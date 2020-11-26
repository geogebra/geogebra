package org.geogebra.web.full.evaluator;

import java.util.HashMap;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.evaluator.EvaluatorAPI;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.export.Canvas2Svg;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.CursorBox;

import elemental2.core.Global;
import jsinterop.base.Js;

/**
 * Evaluator Web implementation.
 *
 * @author Laszlo
 */
public class EvaluatorEditor implements IsWidget, MathFieldListener, BlurHandler {

	private static final String SVG_PREFIX = "data:image/svg+xml;utf8,";
	private AppW app;
	private MathFieldEditor mathFieldEditor;
	private EvaluatorAPI evaluatorAPI;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public EvaluatorEditor(AppW app) {
		this.app = app;
		mathFieldEditor = new MathFieldEditor(app, this);
		mathFieldEditor.setTextMode(app.getAppletParameters().getParamTextMode());
		mathFieldEditor.addStyleName("evaluatorEditor");
		mathFieldEditor.addBlurHandler(this);
		mathFieldEditor.setFontSize(app.getAppletParameters().getParamFontSize(18));
		mathFieldEditor.setUseKeyboardButton(false);

		String bgColor = app.getAppletParameters().getDataParamEditorBackgroundColor();
		String fgColor = app.getAppletParameters().getDataParamEditorForegroundColor();

		mathFieldEditor.getMathField().setBackgroundCssColor(bgColor);
		mathFieldEditor.getMathField().setForegroundCssColor(fgColor);

		app.getFrameElement().getStyle().setBackgroundColor(bgColor);

		MathFieldInternal mathFieldInternal = mathFieldEditor.getMathField().getInternal();
		evaluatorAPI = new EvaluatorAPI(app.getKernel(), mathFieldInternal);
	}

	@Override
	public void onEnter() {
		mathFieldEditor.reset();
		dispatchKeyTypedEvent("\n");
		dispatchEditorStop();
	}

	@Override
	public void onKeyTyped(String key) {
		scrollContentIfNeeded();
		dispatchKeyTypedEvent(key);
	}

	private void dispatchKeyTypedEvent(String key) {
		HashMap<String, Object> evaluatorValue = evaluatorAPI.getEvaluatorValue();
		Event event = new Event(EventType.EDITOR_KEY_TYPED)
				.setJsonArgument(evaluatorValue);
		if (key != null) {
			evaluatorValue.put("key", key);
		}
		app.dispatchEvent(event);
	}

	private void dispatchEditorStop() {
		Event event = new Event(EventType.EDITOR_STOP);
		app.dispatchEvent(event);
	}

	@Override
	public void onCursorMove() {
		scrollContentIfNeeded();
	}

	private void scrollContentIfNeeded() {
		mathFieldEditor.scrollHorizontally();
		mathFieldEditor.scrollVertically();
	}

	@Override
	public void onUpKeyPressed() {
	 	// nothing to do.
	}

	@Override
	public void onDownKeyPressed() {
		// nothing to do.
	}

	@Override
	public void onInsertString() {
		scrollContentIfNeeded();
	}

	@Override
	public boolean onEscape() {
		return true;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO: implement this.
	}

	@Override
	public Widget asWidget() {
		return mathFieldEditor.asWidget();
	}

	public void requestFocus() {
		mathFieldEditor.requestFocus();
	}

	public MathFieldEditor getMathFieldEditor() {
		return mathFieldEditor;
	}

	/**
	 * @return evaluator API
	 */
	public EvaluatorAPI getAPI() {
		return evaluatorAPI;
	}

	@Override
	public void onBlur(BlurEvent event) {
		app.sendKeyboardEvent(false);
		mathFieldEditor.setKeyboardVisibility(false);
		dispatchEditorStop();
	}

	/**
	 * @param type image type -- only SVG supported
	 * @return {svg: base64 encoded SVG, baseline: relative baseline position}
	 */
	public Object exportImage(String type) {
		EquationExportImage ret = new EquationExportImage();
		if (!"svg".equals(type) || !ExportLoader.ensureCanvas2SvgLoaded()) {
			ret.setError("Something went wrong");
			return ret;
		}

		MathFieldW mathField = mathFieldEditor.getMathField();
		mathField.repaintWeb();

		int height = mathField.getIconHeight();
		int depth = mathField.getIconDepth();
		int width = mathField.getIconWidth();
		if (height < 1 || width < 1) {
			ret.setError("Invalid dimensions");
			return ret;
		}
		Canvas2Svg ctx = new Canvas2Svg(width, height + depth);
		CursorBox.setBlink(false);
		mathField.paint(Js.uncheckedCast(ctx), 0);
		ret.setBaseline(height / (double) (height + depth));
		ret.setSvg(SVG_PREFIX + Global.escape(ctx.getSerializedSvg(true)));

		return ret;
	}
}
