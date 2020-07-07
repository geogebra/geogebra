package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.menubar.FileChooser;
import org.geogebra.web.full.main.AppWFull;

public class OpenOfflineFileAction extends DefaultMenuAction<Void>  {

	private FileChooser fileChooser;

	@Override
	public void execute(Void item, final AppWFull app) {
		if (fileChooser == null) {
			fileChooser = new FileChooser(app);
			fileChooser.addStyleName("hidden");
		}
		app.getPanel().add(fileChooser);
		fileChooser.open();
	}
}
