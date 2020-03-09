package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Exports SVG.
 */
public class DownloadSvgAction extends DefaultMenuAction<Void> {

	private AppWFull app;
	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadSvgAction(AppWFull app) {
		this.app = app;
		imageExporter = new ImageExporter(app, "svg");
	}

	@Override
	public void execute(Void item, AppWFull app) {
		imageExporter.export(getUrl());
	}

	private String getUrl() {
		EuclidianViewWInterface ev = (EuclidianViewWInterface) app.getActiveEuclidianView();
		return Browser.encodeSVG(ev.getExportSVG(1, false));
	}
}
