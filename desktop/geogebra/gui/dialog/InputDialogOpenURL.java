package geogebra.gui.dialog;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class InputDialogOpenURL extends InputDialogD{
	private static final long serialVersionUID = 1L;
	
	public InputDialogOpenURL(AppD app) {
		super(app.getFrame(), false);
		this.app = app;	
		
		initString = "http://";
		
		
		// check if there's a string starting http:// already on the clipboard
		// (quite likely!!)
		String clipboardString = app.getStringFromClipboard();
		if (clipboardString != null && (clipboardString.startsWith("http://") || clipboardString.startsWith("https://") || clipboardString.startsWith("www")))
			initString = clipboardString;

		createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		inputPanel.selectText();	
	}

	@Override
	public void setLabels(String title) {
		wrappedDialog.setTitle(title);
		
		btOK.setText(app.getPlain("Open"));
		btCancel.setText(app.getPlain("Cancel"));
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
			return app.getGuiManager().loadURL(inputPanel.getText());
	}

}
