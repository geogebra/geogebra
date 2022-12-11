package org.geogebra.web.full.gui.view.probcalculator;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.util.JsRunnable;
import org.gwtproject.dom.client.Style;

import com.himamis.retex.editor.share.event.MathFieldListener;

public class MathTextFieldW extends MathFieldEditor implements MathFieldListener, ErrorHandler {
	private final ArrayList<JsRunnable> inputHandlers = new ArrayList<>();
	private final ArrayList<JsRunnable> changeHandlers = new ArrayList<>();

	/**
	 * Constructor
	 * @param app The application.
	 */
	public MathTextFieldW(App app) {
		super(app);
		createMathField(this);
		addBlurHandler(event -> {
			this.asWidget().getParent().removeStyleName("focusState");
			onEnter();
		});
		addStyleName("mathTextField");
		setUseKeyboardButton(!NavigatorUtil.isMobile());
	}

	@Override
	public void onEnter() {
		scrollHorizontally();
		for (JsRunnable listener: changeHandlers) {
			listener.run();
		}
	}

	@Override
	public void onKeyTyped(String key) {
		scrollHorizontally();
		for (JsRunnable listener: inputHandlers) {
			listener.run();
		}
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		scrollHorizontally();
		return false;
	}

	@Override
	public void onInsertString() {
		// nothing to do
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO handle tab
	}

	/**
	 * @param width in pixels
	 */
	public void setPxWidth(int width) {
		asWidget().getElement().getStyle().setProperty("minWidth", width + "px");
		asWidget().getElement().getStyle().setWidth(width, Style.Unit.PX);
	}

	public String getText() {
		return getMathField().getText();
	}

	public void addInputHandler(JsRunnable inputHandler) {
		this.inputHandlers.add(inputHandler);
	}

	public void addChangeHandler(JsRunnable inputHandler) {
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
