package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

public class RenameInputDialog extends ComponentInputDialog {

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 * @param inputHandler - input handler
	 * @param labelText
	 * @param initText
	 * @param rows
	 * @param columns
	 * @param showSymbolPopupIcon
	 */
	public RenameInputDialog(AppW app,
			DialogData dialogData, boolean autoHide,
			boolean hasScrim, InputHandler inputHandler,
			String labelText, String initText, int rows, int columns, boolean showSymbolPopupIcon) {
		super(app, dialogData, autoHide, hasScrim, inputHandler, labelText, initText, rows, columns,
				showSymbolPopupIcon);
	}

	@Override
	public void processInput() {
		getInputHandler().processInput(getInputText(), this,
				ok -> {
					if (ok) {
						hide();
					}
		});
	}
}