package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.desktop.main.AppD;

public class InputDialogOpenURL extends InputDialogD {

	public InputDialogOpenURL(AppD app) {
		super(app.getFrame(), false, app.getLocalization());
		this.app = app;

		setInitString("https://");

		// check if there's a string starting http:// already on the clipboard
		// (quite likely!!)
		String clipboardString = app.getStringFromClipboard();
		if (clipboardString != null && (clipboardString.startsWith("http://")
				|| clipboardString.startsWith("https://")
				|| clipboardString.startsWith("www"))) {
			setInitString(clipboardString);
		}

		createGUI(loc.getMenu("OpenWebpage"), loc.getMenu("EnterAppletAddress"),
				false, DEFAULT_COLUMNS, 1, false, true, false, false,
				DialogType.TextArea);
		optionPane.add(inputPanel, BorderLayout.CENTER);
		centerOnScreen();

		inputPanel.selectText();
	}

	@Override
	public void setLabels(String title) {
		wrappedDialog.setTitle(title);

		btOK.setText(loc.getMenu("Open"));
		btCancel.setText(loc.getMenu("Cancel"));
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				setVisible(!processInput());
			} else if (source == btApply) {
				processInput();
				// app.setDefaultCursor();
			} else if (source == btCancel) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
		}
	}

	private boolean processInput() {
		return app.getGuiManager().loadURL(inputPanel.getText(), true);
	}

}
