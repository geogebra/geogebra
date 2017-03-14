package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author bencze The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends MenuBar {

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
		MainMenu.addSubmenuArrow(this, app.isWhiteboardActive()
				? "subMenuRightSide" : "subMenuLeftSide");

		initActions();
	}

	private void initActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				// translation not needed
				.getSafeUri().asString(), "ggb", true), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						hide();
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
						app.getActiveEuclidianView()
								.setSelectionRectangle(null);
						app.getActiveEuclidianView().getEuclidianController()
								.clearSelections();
						hide();
						String url = ((EuclidianViewWInterface) app
								.getActiveEuclidianView())
										.getExportImageDataUrl(1.0, false);

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), app);
						dialogEvent("exportPNG");
					}
				});
		if (!app.getLAF().isTablet()) {
			addItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(),
					app.getLocalization().getMenu("AnimatedGIF"), true), true,
					new MenuCommand(app) {
						@Override
						public void doExecute() {
							hide();
							dialogEvent("exportGIF");
							((DialogManagerW) app.getDialogManager())
									.showAnimGifExportDialog();
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

	/** hide the submenu */
	void hide() {
		PopupPanel p = (PopupPanel) getParent();
		p.hide();
	}
}
