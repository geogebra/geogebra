package org.geogebra.web.full.evaluator;

import java.util.HashMap;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.evaluator.EvaluatorAPI;
import org.geogebra.web.editor.MathFieldExporter;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;

/**
 * Evaluator Web implementation.
 *
 * @author Laszlo
 */
public class EvaluatorEditor implements IsWidget, MathFieldListener, BlurHandler {

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
		mathFieldEditor.getMathField().setUseSimpleScripts(false);
		mathFieldEditor.setTextMode(app.getAppletParameters().getParamTextMode());
		mathFieldEditor.getMathField().setMaxHeight(app.getAppletParameters().getMaxHeight());
		mathFieldEditor.addStyleName("evaluatorEditor");
		mathFieldEditor.addBlurHandler(this);
		mathFieldEditor.setFontSize(app.getAppletParameters().getParamFontSize(18));

		String bgColor = app.getAppletParameters().getDataParamEditorBackgroundColor();
		String fgColor = app.getAppletParameters().getDataParamEditorForegroundColor();

		mathFieldEditor.getMathField().setBackgroundColor(bgColor);
		mathFieldEditor.getMathField().setForegroundColor(fgColor);

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
			evaluatorValue.put("label", "");
		}
		app.dispatchEvent(event);
	}

	private void dispatchEditorStop() {
		Event event = new Event(EventType.EDITOR_STOP);
		app.dispatchEvent(event);
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		scrollContentIfNeeded();
		return false;
	}

	private void scrollContentIfNeeded() {
		mathFieldEditor.scrollCursorVisibleHorizontally();
		mathFieldEditor.scrollVertically();
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
	public boolean onTab(boolean shiftDown) {
		return app.getGlobalKeyDispatcher().handleTab(shiftDown);
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
	 * @param callback called with {svg: base64 encoded SVG,
	 *     baseline: relative baseline position} or error
	 */
	public void exportImage(String type, boolean transparent,
			MathFieldExporter.ImageConsumer callback) {
		new MathFieldExporter(mathFieldEditor.getMathField()).export(type, transparent, callback);
	}
}
