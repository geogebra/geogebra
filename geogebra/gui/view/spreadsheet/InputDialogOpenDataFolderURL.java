package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * modified version of gui.InputDialogOpenURL
 * 
 *  G.Sturr 2010-2-12
 *
 */
public class InputDialogOpenDataFolderURL extends InputDialog{
	
	private SpreadsheetView view;
	
	public InputDialogOpenDataFolderURL(Application app, SpreadsheetView view, String initString) {
		super(app.getFrame(), false);
		this.app = app;	
		this.view = view;
		//initString = "http://";
		this.initString = initString;
		
		String title = app.getMenu("OpenFromWebpage");
		String message =  app.getMenu("EnterWebAddress"); 
		boolean showApply = false;
		createGUI(title, message, false, DEFAULT_COLUMNS, 1, false, false, true, false, false, showApply, false);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		inputPanel.selectText();
		
	}

	public void setLabels(String title) {
		setTitle(title);
		
		btOK.setText(app.getPlain("Open"));
	//	btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getPlain("Cancel"));

	}

	/**
	 * Handles button clicks for dialog.
	 */
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
