package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

public class SaveExamAction implements MenuAction<Void> {

	@Override
	public boolean isAvailable(Void item) {
		return true;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		DialogData data = app.getDialogManager().getSaveDialogData();
		TempStorage tempStorage = app.getExam().getTempStorage();
		InputHandler inputHandler = (input, handler, callback) -> {
			String msg = app.getLocalization().getMenu("SavedSuccessfully");
			try {
				Material material = app.getActiveMaterial();
				material.setTitle(input);
				material.setBase64(app.getGgbApi().getBase64());
				tempStorage.saveTempMaterial(material);
			} catch (RuntimeException ex) {
				msg = app.getLocalization().getError("SaveFileFailed");
			}
			ToolTipManagerW.sharedInstance().showBottomMessage(
					app.getLocalization().getMenu(msg),
					true, app);
			if (callback != null) {
				callback.callback(true);
			}
		};
		Material activeMaterial = app.getActiveMaterial();
		String initString = activeMaterial != null ? activeMaterial.getTitle() : null;
		if (StringUtil.empty(initString)) {
			initString = app.getLocalization().getMenu("Untitled");
		}
		ComponentInputDialog examSave = new ComponentInputDialog(app, data, false,
				true, inputHandler, "Title", initString, false);
		examSave.addInputHandler(() -> {
			examSave.setPosBtnDisabled(examSave.getInputText().length() < 1);
		});
		examSave.center();
	}
}
