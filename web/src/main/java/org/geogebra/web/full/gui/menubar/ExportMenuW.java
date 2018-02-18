package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatCollada;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatJscad;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatObj;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author bencze The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends AriaMenuBar implements MenuBarI {

	/**
	 * Constructs the "Insert Image" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public ExportMenuW(AppW app) {
		super();

		addStyleName("GeoGebraMenuBar");
		MainMenu.addSubmenuArrow(this, app.isUnbundledOrWhiteboard());
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

		menu.addItem(menuText(app.getFileExtension().substring(1)), true,
				new MenuCommand(app) {

			@Override
			public void doExecute() {
				menu.hide();
				dialogEvent(app, "exportGGB");
				app.getFileManager().export(app);
			}
		});

		menu.addItem(menuText("png"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				menu.hide();
				app.toggleMenu();
				app.getActiveEuclidianView().getEuclidianController()
						.clearSelections();
				String url = ((EuclidianViewWInterface) app
						.getActiveEuclidianView()).getExportImageDataUrl(1.0,
								false);

				app.getFileManager().showExportAsPictureDialog(url,
						app.getExportTitle(), "png", "ExportAsPicture", app);
				dialogEvent(app, "exportPNG");
			}
		});

		menu.addItem(menuText("svg"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				menu.hide();
				app.toggleMenu();
				app.getActiveEuclidianView().getEuclidianController()
						.clearSelections();

				String svg = Browser.encodeSVG(
						((EuclidianViewWInterface) app.getActiveEuclidianView())
								.getExportSVG(1, false));

				app.getFileManager().showExportAsPictureDialog(svg,
						app.getExportTitle(), "svg", "ExportAsPicture", app);
				dialogEvent(app, "exportSVG");
			}
		});
		menu.addItem(menuText("pdf"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				menu.hide();
				app.toggleMenu();
				app.getActiveEuclidianView().getEuclidianController()
						.clearSelections();
				String pdf = ((EuclidianViewWInterface) app
						.getActiveEuclidianView()).getExportPDF(1.0);

				app.getFileManager().showExportAsPictureDialog(pdf,
						app.getExportTitle(), "pdf", "ExportAsPicture", app);
				dialogEvent(app, "exportPDF");
			}
		});
		// TODO add gif back when ready
		// if (!app.getLAF().isTablet()) {
		// addItem(menuText(
		// app.getLocalization().getMenu("AnimatedGIF")), true,
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

		menu.addItem(menuText("PSTricks"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				app.getActiveEuclidianView().setSelectionRectangle(null);
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

		menu.addItem(menuText("PGF/TikZ"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				app.getActiveEuclidianView().getEuclidianController()
						.clearSelectionAndRectangle();
				menu.hide();
				String url = "data:text/plain;charset=utf-8,"
						+ app.getGgbApi().exportPGF();

				app.getFileManager().showExportAsPictureDialog(url,
						app.getExportTitle(), "txt", "Export", app);
				dialogEvent(app, "exportPGF");
			}
		});

		menu.addItem(menuText("Asymptote"), true, new MenuCommand(app) {

			@Override
			public void execute() {
				app.getActiveEuclidianView().getEuclidianController()
						.clearSelectionAndRectangle();
				menu.hide();
				String url = "data:text/plain;charset=utf-8,"
						+ app.getGgbApi().exportAsymptote();

				app.getFileManager().showExportAsPictureDialog(url,
						app.getExportTitle(), "txt", "Export", app);
				dialogEvent(app, "exportPGF");
			}
		});

		if (app.has(Feature.EXPORT_SCAD_IN_MENU) && app.is3D()) {
			menu.addItem(menuText("OpenSCAD"), true, new MenuCommand(app) {
				@Override
				public void doExecute() {
					menu.hide();
					app.setExport3D(new FormatJscad());
				}
			});
		}

		if (app.has(Feature.EXPORT_COLLADA_IN_MENU) && app.is3D()) {
			menu.addItem(menuText("Collada"), true, new MenuCommand(app) {
				@Override
				public void doExecute() {
					menu.hide();
					app.setExport3D(new FormatCollada());
				}
			});
		}

		if (app.has(Feature.EXPORT_OBJ_IN_MENU) && app.is3D()) {
			menu.addItem(menuText("Obj"), true, new MenuCommand(app) {
				@Override
				public void doExecute() {
					menu.hide();
					app.setExport3D(new FormatObj());
				}
			});
		}

	}

	private static String menuText(String string) {
		return MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(), string,
				true);
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
	@Override
	public void hide() {
		Widget p = getParent();
		if (p instanceof GPopupPanel) {
			((GPopupPanel) p).hide();
		}
	}
}
