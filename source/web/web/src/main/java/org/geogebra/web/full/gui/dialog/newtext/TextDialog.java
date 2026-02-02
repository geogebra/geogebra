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

package org.geogebra.web.full.gui.dialog.newtext;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.TextBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.TextInputErrorHandler;
import org.geogebra.web.full.gui.dialog.text.GeoTextEditor;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.user.client.ui.TextBox;

public class TextDialog extends ComponentDialog implements TextInputDialog {
	private final AppWFull appW;
	private GeoText geoText;
	private TextTopBar topBar;
	private TextEditPanel editPanel;
	private PreviewPanel previewPanel;
	/** start point */
	GeoPointND startPoint;
	/** whether to position text in RW coords or EV */
	boolean rw;

	/**
	 * text dialog
	 * @param app see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 * @param startPoint position for newly created text
	 * @param rw whether position is in RW coordinates
	 */
	public TextDialog(AppWFull app, DialogData dialogData, GeoPointND startPoint, boolean rw) {
		super(app, dialogData, false, true);
		this.appW = app;
		this.startPoint = startPoint;
		this.rw = rw;
		geoText = new GeoText(app.getKernel().getConstruction());
		addStyleName("textDialog");
		createContent();
		editPanel.setEditGeo(geoText);
		setOnPositiveAction(this::processInput);
		setOnNegativeAction(app::closePopups);
		setPosBtnDisabled(true);
		show();
	}

	private void createContent() {
		editPanel = new TextEditPanel(appW);
		editPanel.getTextArea().addDomHandler(
				event -> {
					setPosBtnDisabled(editPanel.getText().isEmpty());
					if (previewPanel != null && editPanel.isLatex()) {
						previewPanel.selectLatexCheckbox();
					}
				}, KeyUpEvent.getType());

		topBar = new TextTopBar(appW, geoText,
				() -> editPanel.updatePreviewPanel(false));
		setDialogContent(topBar);

		GeoTextEditor editor = editPanel.getTextArea();
		editor.addStyleName("textEditor");
		addDialogContent(editor);

		previewPanel = new PreviewPanel(appW, topBar.getTextStyle(),
				editPanel, () -> centerAndResize(appW.getAppletFrame()
				.getKeyboardHeight()));
		addDialogContent(previewPanel);
	}

	private void processInput() {
		closeIOSKeyboard();
		String inputText = editPanel.getText();
		new TextDialog.TextInputHandler().processInput(inputText, new TextInputErrorHandler(appW),
				ok -> {
					if (ok) {
						hide();
					}
				});
	}

	/*
	 * Close iOS keyboard at creating text. At using TextInputDialog iOS
	 * keyboard has to be closed programmatically at clicking on OK or Cancel,
	 * otherwise it won't be closed after the dialog will be hidden.
	 */
	protected void closeIOSKeyboard() {
		if (appW.isWhiteboardActive()) {
			return;
		}
		if (editPanel == null || editPanel.getText().isEmpty()) {
			return;
		}
		TextBox dummyTextBox = new TextBox();
		editPanel.add(dummyTextBox);
		dummyTextBox.setFocus(true);
		dummyTextBox.setFocus(false);
		editPanel.remove(dummyTextBox);
	}

	@Override
	public void show() {
		super.show();
		setPosBtnDisabled(editPanel.getText().isEmpty());
		// prevent topbar's actions from closing this dialog
		Scheduler.get().scheduleDeferred(() -> appW.unregisterPopup(this));
	}

	/**
	 * Shows the keyboard.
	 */
	protected void showKeyboard() {
		appW.showKeyboard(editPanel.getTextArea(), true);
		appW.updateKeyboardField(editPanel.getTextArea());
		KeyboardManagerInterface keyboardManager = appW.getKeyboardManager();
		keyboardManager.setOnScreenKeyboardTextField(editPanel.getTextArea());
		appW.getAppletFrame()
				.showKeyboard(true, editPanel.getTextArea(), false);
		CancelEventTimer.keyboardSetVisible();
	}

	/**
	 * Handles creating or redefining GeoText using the current editor string.
	 *
	 */
	private class TextInputHandler implements InputHandler {
		private final Kernel kernel;

		TextInputHandler() {
			kernel = appW.getKernel();
		}

		@Override
		public void processInput(String input, ErrorHandler handler,
				AsyncOperation<Boolean> callback) {
			if (input == null) {
				setPosBtnDisabled(false);
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
				setPosBtnDisabled(false);
				callback.callback(false);
				return;
			}
			// create new GeoText
			handler.resetError();
			TextBuilder textBuilder = new TextBuilder(appW, startPoint, rw, topBar.getTextStyle());
			textBuilder.createText(inputValue, handler, callback);
		}
	}

	@Override
	public void reInitEditor(GeoText text, GeoPointND startPoint, boolean rw) {
		geoText = text == null ? new GeoText(appW.getKernel().getConstruction()) : text;
		this.startPoint = startPoint;
		this.rw = rw;
		createContent();
		editPanel.setText(text);
		show();
	}
}
