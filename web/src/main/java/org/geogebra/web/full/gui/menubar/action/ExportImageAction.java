package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.dialog.ExportImageDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports image
 */
public class ExportImageAction extends DefaultMenuAction<Void> {

    @Override
    public void execute(Void item, AppWFull app) {
        app.getSelectionManager().clearSelectedGeos();
        String url = ExportImageDialog.getExportDataURL(app);
        app
                .getFileManager()
                .showExportAsPictureDialog(url, app.getExportTitle(), "png", "ExportAsPicture", app);
    }
}
