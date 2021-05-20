package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Exports SVG.
 */
public class DownloadSvgAction extends DefaultMenuAction<Void> {

	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadSvgAction(AppWFull app) {
		imageExporter = new ImageExporter(app, "svg");
	}

	@Override
	public void execute(Void item, AppWFull app) {
		EuclidianViewWInterface ev = (EuclidianViewWInterface) app.getActiveEuclidianView();
		ev.getExportSVG(1, false, (svg) -> {
			imageExporter.export(Browser.encodeSVG(svg));
		});
	}
}
