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

package org.geogebra.web.full.gui.view.probcalculator;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.gwtutil.JsRunnable;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.gwtproject.dom.style.shared.Unit;

public class MathTextFieldW extends MathFieldEditor implements MathFieldListener, ErrorHandler {
	private final ArrayList<JsRunnable> inputHandlers = new ArrayList<>();
	private final ArrayList<JsConsumer<Boolean>> changeHandlers = new ArrayList<>();

	/**
	 * Constructor
	 * @param app The application.
	 */
	public MathTextFieldW(App app) {
		this(app, getDefaultCatalog());
	}

	/**
	 * Constructor
	 * @param app The application.
	 * @param catalog editor model
	 */
	public MathTextFieldW(App app, TemplateCatalog catalog) {
		super(app);
		createMathField(this, catalog);
		addBlurHandler(event -> {
			this.asWidget().getParent().removeStyleName("focusState");
			scrollCursorVisibleHorizontally();
			notifyListeners(false);
		});
		addStyleName("mathTextField");
		setUseKeyboardButton(!NavigatorUtil.isMobile());
	}

	@Override
	public void onEnter() {
		scrollCursorVisibleHorizontally();
		notifyListeners(true);
	}

	private void notifyListeners(boolean isEnter) {
		for (JsConsumer<Boolean> listener: changeHandlers) {
			listener.accept(isEnter);
		}
	}

	@Override
	public void onKeyTyped(String key) {
		scrollCursorVisibleHorizontally();
		for (JsRunnable listener: inputHandlers) {
			listener.run();
		}
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		scrollCursorVisibleHorizontally();
		return false;
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		// TODO handle tab
		return true;
	}

	/**
	 * @param width in pixels
	 */
	public void setPxWidth(int width) {
		asWidget().getElement().getStyle().setProperty("minWidth", width + "px");
		asWidget().getElement().getStyle().setWidth(width, Unit.PX);
	}

	public String getText() {
		return getMathField().getText();
	}

	/**
	 * Add input handler.
	 * @param inputHandler input handler
	 */
	public void addInputHandler(JsRunnable inputHandler) {
		this.inputHandlers.add(inputHandler);
	}

	/**
	 * Add change handler.
	 * @param inputHandler change handler
	 */
	public void addChangeHandler(JsConsumer<Boolean> inputHandler) {
		this.changeHandlers.add(inputHandler);
	}

	@Override
	public void showError(String msg) {
		setErrorText(getErrorMessage());
	}

	@Override
	public void showCommandError(String command, String message) {
		setErrorText(getErrorMessage());
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void resetError() {
		setErrorText(null);
	}
}
