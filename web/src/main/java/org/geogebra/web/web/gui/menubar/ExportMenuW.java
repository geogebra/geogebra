package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author bencze The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends MenuBar implements MenuBarI {

	/**
	 * Application instance
	 */
	AppW app;

	/**
	 * Constructs the "Insert Image" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public ExportMenuW(AppW app) {
		super(true);

		this.app = app;
		addStyleName("GeoGebraMenuBar");
		MainMenu.addSubmenuArrow(this,
				app.isUnbundledOrWhiteboard());
		if (app.isUnbundled()) {
			addStyleName("floating-Popup");
		}

		initActions(this, app);
	}

	/**
	 * @param menu
	 *            menu
	 * @param app
	 *            application
	 */
	protected static void initActions(final MenuBarI menu, final AppW app) {

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "ggb", true), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						menu.hide();
						dialogEvent(app, "exportGGB");
						app.getFileManager().export(app);
					}
				});

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "png", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						menu.hide();
						app.toggleMenu();
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String url = ((EuclidianViewWInterface) app
								.getActiveEuclidianView())
										.getExportImageDataUrl(1.0, false);

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "png", "ExportAsPicture", app);
						dialogEvent(app, "exportPNG");
					}
				});

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "svg", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						menu.hide();
						app.toggleMenu();
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						String svg = "data:text/plain;charset=utf-8,"
								+ ((EuclidianViewWInterface) app
								.getActiveEuclidianView()).getExportSVG(1,
										false);

						app.getFileManager().showExportAsPictureDialog(svg,
								app.getExportTitle(), "svg", "ExportAsPicture",
								app);
						dialogEvent(app, "exportSVG");
					}
				});
		// TODO add gif back when ready
		// if (!app.getLAF().isTablet()) {
		// addItem(MainMenu.getMenuBarHtml(
		// AppResources.INSTANCE.empty().getSafeUri().asString(),
		// app.getLocalization().getMenu("AnimatedGIF"), true), true,
		// new MenuCommand(app) {
		// @Override
		// public void doExecute() {
		// hide();
		// dialogEvent("exportGIF");
		// ((DialogManagerW) app.getDialogManager())
		// .showAnimGifExportDialog();
		// }
		// });
		// }

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "PSTricks", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						menu.hide();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportPSTricks();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent(app, "exportPSTricks");
					}
				});

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "PGF/TikZ", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						menu.hide();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportPGF();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent(app, "exportPGF");
					}
				});

		menu.addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "Asymptote", true), true,
				new MenuCommand(app) {

					@Override
					public void execute() {
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						menu.hide();
						String url = "data:text/plain;charset=utf-8,"
								+ app.getGgbApi().exportAsymptote();

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "txt", "Export", app);
						dialogEvent(app, "exportPGF");
					}
				});
		
		
		if (app.has(Feature.EXPORT_SCAD)) {
			menu.addItem(
					MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(),
					"OpenSCAD", true), true,
					new MenuCommand(app) {
						@Override
						public void doExecute() {
							menu.hide();
							app.setFlagForSCADexport();
						}
					});
		}

	}

	/**
	 * Fire dialog open event
	 * 
	 * @param app
	 *            application to receive the evt
	 * 
	 * @param string
	 *            dialog name
	 */
	protected static void dialogEvent(AppW app, String string) {
		app.dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, string));

	}

	/** hide the submenu */
	public void hide() {
		PopupPanel p = (PopupPanel) getParent();
		p.hide();
	}
}
