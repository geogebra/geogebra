package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public abstract class DownloadImageAction extends DefaultMenuAction<AppWFull> {

	private final ImageExporter imageExporter;

	public DownloadImageAction(AppWFull app, String extension) {
		imageExporter = new ImageExporter(app, extension);
	}

	@Override
	public final void execute(AppWFull app) {
		app.getSelectionManager().clearSelectedGeos();
		export(app);
	}

	protected abstract void export(AppWFull app);

	protected void exportImage(String url) {
		imageExporter.export(url);
	}
}
