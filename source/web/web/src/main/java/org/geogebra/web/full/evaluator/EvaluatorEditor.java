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

package org.geogebra.web.full.evaluator;

import java.util.HashMap;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.evaluator.EvaluatorAPI;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.editor.MathFieldExporter;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

/**
 * Evaluator Web implementation.
 *
 * @author Laszlo
 */
public class EvaluatorEditor implements IsWidget, MathFieldListener, BlurHandler {

	private final AppW app;
	private final MathFieldEditor mathFieldEditor;
	private final EvaluatorAPI evaluatorAPI;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public EvaluatorEditor(AppW app) {
		this.app = app;
		mathFieldEditor = new MathFieldEditor(app, this);
		MathFieldW mathField = mathFieldEditor.getMathField();
		mathField.setUseSimpleScripts(false);
		mathField.asWidget().getElement().setAttribute("role", "application");
		mathFieldEditor.setTextMode(app.getAppletParameters().getParamTextMode());
		mathField.setMaxHeight(app.getAppletParameters().getMaxHeight());
		mathFieldEditor.addStyleName("evaluatorEditor");
		mathFieldEditor.addBlurHandler(this);
		mathFieldEditor.setFontSize(app.getAppletParameters().getParamFontSize(18));

		String bgColor = app.getAppletParameters().getDataParamEditorBackgroundColor();
		String fgColor = app.getAppletParameters().getDataParamEditorForegroundColor();

		mathField.setBackgroundColor(bgColor);
		mathField.setForegroundColor(fgColor);

		app.getFrameElement().getStyle().setBackgroundColor(bgColor);

		MathFieldInternal mathFieldInternal = mathField.getInternal();
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

	/**
	 * Focus the editor.
	 */
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
