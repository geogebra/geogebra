package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.util.SaveDialogW;

public class DialogManagerT extends DialogManagerW {

	public DialogManagerT(final App app) {
	    super(app);
    }

	@Override
	public SaveDialogW getSaveDialog() {
		if (saveDialog == null) {
			saveDialog = new SaveDialogT(app);
		}
		return saveDialog;
	}
}
