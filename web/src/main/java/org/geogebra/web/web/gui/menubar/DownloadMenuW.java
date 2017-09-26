package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;

/**
 * Help menu
 */
public class DownloadMenuW extends GMenuBar {
	/**
	 * app
	 */
	AppW app;

	/**
	 * @param app
	 *            - application
	 */
	public DownloadMenuW(final AppW app) {
		super(true, "DownloadAs", app);
		this.app = app;
		if (app.isUnbundled() || app.isWhiteboardActive()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		init();
	}

	private void init() {
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "ggb", true), true,
				new MenuCommand(this.app) {

					@Override
					public void doExecute() {
						dialogEvent("exportGGB");
						app.getFileManager().export(app);
					}
				});
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "png", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.toggleMenu();
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String url = ((EuclidianViewWInterface) app
								.getActiveEuclidianView())
										.getExportImageDataUrl(1.0, false);

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "png", "ExportAsPicture",
								app);
						dialogEvent("exportPNG");
					}
				});
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "PSTricks", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportPSTricks();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent("exportPSTricks");
					}
				});
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "PGF/TikZ", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportPGF();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent("exportPGF");
					}
				});
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "Asymptote", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportAsymptote();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent("exportPGF");
					}
				});
		if (app.has(Feature.EXPORT_SCAD)) {
			addItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(),
					"OpenSCAD", true), true, new MenuCommand(app) {
						@Override
						public void doExecute() {
							app.setFlagForSCADexport();
						}
					});
		}
	}

	/**
	 * Fire dialog open event
	 * 
	 * @param string
	 *            dialog name
	 */
	protected void dialogEvent(String string) {
		app.dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, string));
	}
}

