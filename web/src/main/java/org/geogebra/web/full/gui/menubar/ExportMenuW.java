package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatSTL;
import org.geogebra.common.main.HTML5Export;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.web.full.gui.dialog.ExportImageDialog;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.Widget;

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

		menu.addItem(menuText(app.getLocalization().getMenu("Download.GeoGebraFile"),
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						menu.hide();
						app.getFileManager().export(app);
					}
				}));

		menu.addItem(menuText(app.getLocalization().getMenu("Download.PNGImage"),
				new MenuCommand(app) {
					@Override
					public void execute() {
						menu.hide();
						app.hideMenu();
						app.getSelectionManager().clearSelectedGeos();

						String url = ExportImageDialog.getExportDataURL(app);

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "png", "ExportAsPicture",
								app);
					}
				}));

		menu.addItem(menuText(app.getLocalization().getMenu("Download.SVGImage"),
				new MenuCommand(app) {

					@Override
					public void execute() {
						menu.hide();
						app.hideMenu();
						app.getSelectionManager().clearSelectedGeos();
						EuclidianViewWInterface ev
								= (EuclidianViewWInterface) app.getActiveEuclidianView();
						ev.getExportSVG(false, (svg) ->
								app.getFileManager().showExportAsPictureDialog(
										Browser.encodeSVG(svg), app.getExportTitle(), "svg",
										"ExportAsPicture", app));
					}
				}));
		menu.addItem(menuText(app.getLocalization().getMenu("Download.PDFDocument"),
				new MenuCommand(app) {
					@Override
					public void execute() {

						menu.hide();
						app.hideMenu();
						app.getSelectionManager().clearSelectedGeos();

						app.getGgbApi().exportPDF(1, null, (pdf) -> {
							app.getFileManager().showExportAsPictureDialog(pdf,
									app.getExportTitle(), "pdf", "ExportAsPicture",
									app);
						}, null);
					}
				}));
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
		if (!app.isWhiteboardActive()) {
			menu.addItem(menuText("PSTricks (.txt)", new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().setSelectionRectangle(null);
					app.getSelectionManager().clearSelectedGeos();

					menu.hide();
					app.getGgbApi()
							.exportPSTricks(exportCallback("Pstricks", app));
				}
			}));

			menu.addItem(menuText("PGF/TikZ (.txt)", new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().getEuclidianController()
							.clearSelectionAndRectangle();
					menu.hide();
					app.getGgbApi().exportPGF(exportCallback("PGF", app));
				}
			}));

			menu.addItem(
					menuText(app.getLocalization()
							.getMenu("ConstructionProtocol") + " (."
							+ FileExtensions.HTML + ")",
					new MenuCommand(app) {
						@Override
						public void doExecute() {
							menu.hide();
							app.exportStringToFile("html",
									app.getGgbApi().exportConstruction("color",
											"name", "definition", "value"), true);
						}
					}));

			menu.addItem(
					menuText(app.getLocalization()
							.getMenu("DynamicWorksheetAsWebpage") + " (."
							+ FileExtensions.HTML + ")",
					new MenuCommand(app) {
						@Override
						public void doExecute() {
							menu.hide();
							app.exportStringToFile("html",
									HTML5Export.getFullString(app), true);
						}
					}));

			menu.addItem(menuText("Asymptote (.txt)", new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().getEuclidianController()
							.clearSelectionAndRectangle();
					menu.hide();
					app.getGgbApi()
							.exportAsymptote(exportCallback("Asymptote", app));
				}
			}));

			menu.addItem(menuText(app.getLocalization()
					.getMenu("Download.3DPrint"), new MenuCommand(app) {
				@Override
				public void doExecute() {
					menu.hide();
					app.setExport3D(new FormatSTL());
				}
			}));

			if (app.is3D()) {
				menu.addItem(menuText(app.getLocalization()
						.getMenu("Download.ColladaDae"), new MenuCommand(app) {
					@Override
					public void doExecute() {
						menu.hide();
						app.exportCollada(false);
					}
				}));

				menu.addItem(menuText(app.getLocalization()
								.getMenu("Download.ColladaHtml"),
						new MenuCommand(app) {
							@Override
							public void doExecute() {
								menu.hide();
								app.exportCollada(true);
							}
						}));
			}
		}
	}

	/**
	 * @param string
	 *            file type (for event logging)
	 * @param app
	 *            application
	 * @return callback for saving text export / images
	 */
	protected static AsyncOperation<String> exportCallback(final String string,
			final AppW app) {
		return obj -> {
			String url = Browser.addTxtMarker(obj);
			app.getFileManager().showExportAsPictureDialog(url,
					app.getExportTitle(), "txt", "Export", app);
		};
	}

	private static AriaMenuItem menuText(String string, Scheduler.ScheduledCommand cmd) {
		return MainMenu.getMenuBarHtmlEmptyIcon(string, cmd);
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
