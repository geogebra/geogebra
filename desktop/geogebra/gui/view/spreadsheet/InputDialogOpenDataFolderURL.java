package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.gui.dialog.InputDialogD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;


/**
 * modified version of gui.InputDialogOpenURL
 * 
 *  G.Sturr 2010-2-12
 *
 */
public class InputDialogOpenDataFolderURL extends InputDialogD{
	private SpreadsheetView view;
	
	public InputDialogOpenDataFolderURL(AppD app, SpreadsheetView view, String initString) {
		super(app.getFrame(), false,app.getLocalization());
		this.app = app;	
		this.view = view;
		//initString = "http://";
		this.initString = initString;
		
		String title = app.getMenu("OpenFromWebpage");
		String message =  app.getMenu("EnterWebAddress"); 
		boolean showApply = false;
		createGUI(title, message, false, DEFAULT_COLUMNS, 1, false, true, false, showApply, DialogType.TextArea);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		inputPanel.selectText();
		
	}

	@Override
	public void setLabels(String title) {
		wrappedDialog.setTitle(title);
		
		btOK.setText(loc.getPlain("Open"));
	//	btApply.setText(app.getPlain("Apply"));
		btCancel.setText(loc.getPlain("Cancel"));

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
				} else if (source == btCancel) {
					setVisible(false);
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisible(false);
		}
	}
	
	
	private boolean processInput() {
		return	view.setFileBrowserDirectory(inputPanel.getText(),FileBrowserPanel.MODE_URL);
	}


}
