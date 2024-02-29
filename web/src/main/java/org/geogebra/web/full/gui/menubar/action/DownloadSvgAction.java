package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Exports SVG.
 */
public class DownloadSvgAction extends DownloadImageAction {

	/**
	 * @param app app
	 */
	public DownloadSvgAction(AppWFull app) {
		super(app, "svg");
	}

	@Override
	protected void export(AppWFull app) {
		EuclidianViewWInterface ev = (EuclidianViewWInterface) app.getActiveEuclidianView();
		ev.getEuclidianController().widgetsToBackground();
		ev.getExportSVG(false, (svg) -> {
			exportImage(Browser.encodeSVG(svg));
		});
	}
}
