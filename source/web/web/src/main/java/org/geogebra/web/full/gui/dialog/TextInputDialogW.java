package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.TextBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.TextBox;

/**
 * Web implementation of Text Dialog
 */
public class TextInputDialogW extends ComponentDialog implements TextInputDialog {
	/** edited text */
	GeoText editGeo;
	/** start point */
	GeoPointND startPoint;
	/** whether to position text in RW coords or EV */
	boolean rw;
	private TextEditPanel editor;
	private boolean isTextMode;

	/**
	 * @param app2
	 *            app
	 * @param title
	 *            title
	 * @param editGeo
	 *            text to edit
	 * @param startPoint
	 *            start point
	 * @param rw
	 *            whether to use RW for position
	 * @param isTextMode
	 *            whether text mode was active when this was called
	 */
	public TextInputDialogW(AppW app2, String title, GeoText editGeo,
            GeoPointND startPoint, boolean rw, boolean isTextMode) {
		super(app2, new DialogData(title), false, false);
		this.startPoint = startPoint;
		this.rw = rw;
		this.isTextMode = isTextMode;
		this.editGeo = editGeo;
		setOnPositiveAction(this::processInput);
		addStyleName("TextInputDialog");
		createTextGUI();
		addCloseHandler(event -> {
			resetEditor();
			resetMode();
		});
		setPreventHide(true);
		show();
	}

	private void createTextGUI() {
		((AppW) app).unregisterPopup(this);
		editor = new TextEditPanel(app);
		editor.setText(editGeo);
		// make sure we resize the dialog if advanced panel opened and not enough space
		editor.getDisclosurePanel().addOpenHandler(event ->
				super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight()));
		setDialogContent(editor);
	}

	private void processInput() {
		closeIOSKeyboard();
		String inputText = editor.getText();
		new TextInputHandler().processInput(inputText, new TextInputErrorHandler(app), ok -> {
			setVisible(!ok);
			if (ok) {
				resetMode();
			}
		});
	}

	@Override
	public void show() {
		super.show();
		focus();
	}

	/*
	 * Close iOS keyboard at creating text. At using TextInputDialog iOS
	 * keyboard has to be closed programmatically at clicking on OK or Cancel,
	 * otherwise it won't be closed after the dialog will be hidden.
	 */
	protected void closeIOSKeyboard() {
		if (app.isWhiteboardActive()) {
			return;
		}
		if (editor == null || editor.getText().equals("")) {
			return;
		}
		TextBox dummyTextBox = new TextBox();
		editor.add(dummyTextBox);
		dummyTextBox.setFocus(true);
		dummyTextBox.setFocus(false);
		editor.remove(dummyTextBox);
	}

	/**
	 * Removes current editor
	 */
	void resetEditor() {
		editor = null;
	}

	protected void resetMode() {
		if (isTextMode) {
			app.setMode(EuclidianConstants.MODE_TEXT);
		}
	}

	/**
	 * Updates latex / serif / font size of the text from GUI
	 * 
	 * @param t
	 *            text
	 */
	void updateTextStyle(TextBuilder t) {
		if (editor == null) {
			Log.debug("null editor");
			return;
		}
		editor.updateTextStyle(t);
	}

	/**
	 * @return whether latex checkbox is active
	 */
	boolean isLatex() {
		if (editor == null) {
			Log.debug("null editor");
			return false;
		}
		return editor.isLatex();
	}

	// =============================================================
	// TextInputHandler
	// =============================================================

	/**
	 * Handles creating or redefining GeoText using the current editor string.
	 * 
	 */
	private class TextInputHandler implements InputHandler {

		private final Kernel kernel;

		TextInputHandler() {
			kernel = app.getKernel();
		}

		@Override
		public void processInput(String input, ErrorHandler handler,
				AsyncOperation<Boolean> callback) {
			if (input == null) {
				callback.callback(false);
				return;
			}
			String inputValue = input;
			// no quotes?
			if (inputValue.indexOf('"') < 0) {
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text

				// ad (1) OBJECT LABEL
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
				}
				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}
			} else {
				// replace \n\" by \"\n, this is useful for e.g.:
				// "a = " + a +
				// "b = " + b
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}

			if ("\"\"".equals(inputValue)) {
				callback.callback(false);
				return;
			}
			// create new GeoText
			boolean createText = editGeo == null;
			handler.resetError();
			if (createText) {
				TextBuilder textBuilder = new TextBuilder(app, startPoint, rw, isLatex());
				updateTextStyle(textBuilder);
				textBuilder.createText(inputValue, handler, callback);
				return;
			}

			// change existing text
			try {
				kernel.getAlgebraProcessor().changeGeoElement(editGeo,
						inputValue, true, true, handler,
						newText -> {
							if (newText instanceof GeoText) {
								// make sure newText is using correct LaTeX
								// setting
								((GeoText) newText).setLaTeX(isLatex(),
										true);

								if (newText.getParentAlgorithm() != null) {
									newText.getParentAlgorithm().update();
								} else {
									newText.updateRepaint();
								}

								app.doAfterRedefine(newText);

								// make redefined text selected
								app.getSelectionManager()
										.addSelectedGeo(newText);
							}

						});

				callback.callback(true);
			} catch (Exception e) {
				app.showGenericError(e);
				callback.callback(false);
			}
		}
	}

	private void focus() {
		if (editor != null) {
			// probably this branch will run (rows > 1)
			if (NavigatorUtil.isFirefox()) {
				// Code that works in Firefox but not in IE and Chrome
				editor.getTextArea().getElement().blur();
				editor.getTextArea().getElement().focus();
			} else {
				// Code that works in Chrome but not in Firefox
				editor.getTextArea().setFocus(false);
				editor.getTextArea().setFocus(true);
			}
			showKeyboard();
		}
	}

	/**
	 * Shows the keyboard.
	 */
	protected void showKeyboard() {
		((AppW) app).showKeyboard(editor.getTextArea(), true);
		((AppW) app).updateKeyboardField(editor.getTextArea());
		KeyboardManagerInterface keyboardManager = ((AppW) app).getKeyboardManager();
		if (keyboardManager != null) {
			keyboardManager.setOnScreenKeyboardTextField(editor.getTextArea());
		}
		((AppWFull) app).getAppletFrame()
				.showKeyboard(true, editor.getTextArea(), false);
		CancelEventTimer.keyboardSetVisible();
	}

	@Override
	public void reInitEditor(GeoText text, GeoPointND startPoint2,
			boolean rw1) {
		if (editor == null) {
			createTextGUI();
		}
		isTextMode = app.getMode() == EuclidianConstants.MODE_TEXT;
		this.startPoint = startPoint2;
		this.rw = rw1;
		setGeoText(text);
		show();
    }

	private void setGeoText(GeoText geo) {
		editGeo = geo;
		editor.setEditGeo(geo);
		editor.setText(geo);
	}
	
	@Override
	public void setLabels() {
		if (editor != null) {
			editor.setLabels();
		}
		updateBtnLabels("OK", "Cancel");
	}
}