package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Exports SVG.
 */
public class DownloadSvgAction extends DefaultMenuAction<AppWFull> {

	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadSvgAction(AppWFull app) {
		imageExporter = new ImageExporter(app, "svg");
	}

	@Override
	public void execute(AppWFull app) {
		EuclidianViewWInterface ev = (EuclidianViewWInterface) app.getActiveEuclidianView();
		ev.getEuclidianController().widgetsToBackground();
		ev.getExportSVG(false, (svg) -> {
			imageExporter.export(Browser.encodeSVG(svg));
		});
	}
}
