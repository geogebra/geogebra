package geogebra.touch.gui.dialog;

import geogebra.common.main.App;
import geogebra.touch.gui.dialog.image.ImageInputDialogT;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.dialog.image.UploadImageDialog;
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
	
	@Override
	public UploadImageDialog getImageInputDialog() {
		if (imageDialog == null) {
			imageDialog = new ImageInputDialogT(app);
		}
		return imageDialog;
	}
}
