package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Audio / video dialog.
 * 
 * @author Zbynek
 */
public abstract class MediaDialog extends DialogBoxW
		implements FastClickHandler, ErrorHandler {
	/** http prefix */
	protected static final String HTTP = "http://";
	/** https prefix */
	protected static final String HTTPS = "https://";
	/** input */
	protected InputPanelW inputField;
	/** input panel */
	protected FlowPanel inputPanel;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 */
	public MediaDialog(Panel root, App app) {
		super(root, app);
	}

	/**
	 * Add handler for input event
	 */
	public void addInputHandler() {
		nativeon(inputField.getTextComponent().getInputElement());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	protected void addHoverHandlers() {
		inputField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getInputPanel().addStyleName("hoverState");
					}
				});
		inputField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getInputPanel().removeStyleName("hoverState");
					}
				});

	}

	/**
	 * @return input field
	 */
	public InputPanelW getInputField() {
		return inputField;
	}

	private native void nativeon(Element img) /*-{
		var that = this;
		img.addEventListener("input", function() {
			that.@org.geogebra.web.full.gui.dialog.MediaDialog::onInput()();
		});
	}-*/;

	/**
	 * Input changed (paste or key event happened)
	 */
	public abstract void onInput();

	@Override
	public final void showCommandError(String command, String message) {
		// not used but must be implemented
	}

	@Override
	public final String getCurrentCommand() {
		return null;
	}

	@Override
	public final boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}
}
