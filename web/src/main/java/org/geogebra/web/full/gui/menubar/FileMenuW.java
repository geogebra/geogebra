package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.menubar.item.ExitExamItem;
import org.geogebra.web.full.gui.menubar.item.FileNewItem;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends Submenu implements BooleanRenderable, EventRenderable {

	private AriaMenuItem shareItem;
	private AriaMenuItem openFileItem;

	private Localization loc;
	private VendorSettings vendorSettings;
	private GeoGebraActivity activity;

	/**
	 * @param app
	 *            application
	 */
	public FileMenuW(final AppW app) {
		super("file", app);
		addExpandableStyleWithColor(false);
		this.loc = app.getLocalization();
		this.activity = ((AppWFull) getApp()).getActivity();
		vendorSettings = getApp().getVendorSettings();
		initActions();
	}

	/**
	 * @return whether native JS function for sharing is present
	 */
	public native static boolean nativeShareSupported()/*-{
		if ($wnd.android && $wnd.android.share) {
			return true;
		}
		return false;
	}-*/;

	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (getApp().isExam()) {
			addItem(new ExitExamItem());
			return;
		}

		buildFileMenu();

		getApp().getNetworkOperation().getView().add(this);
		if (!getApp().getNetworkOperation().isOnline()) {
			render(false);
		}
		if (getApp().getLoginOperation() != null) {
			getApp().getLoginOperation().getView().add(this);
		}
	}

	private void buildFileMenu() {
		addFileNewItem();
		addOpenFileItem();
		addSaveItem();
		addSeparator();
		addExportImageItem();
		addShareItem();
		addDownloadAsItem();
		addPrintItem();
	}

	private void updateOpenFileButton() {
		openFileItem.setHTML(MainMenu.getMenuBarHtmlClassic(
				activity.getResourceIconProvider().openFileMenu().getSafeUri().asString(),
				loc.getMenu(vendorSettings.getMenuLocalizationKey("Open"))));
	}

	/**
	 * Open share dialog for given app
	 *
	 * @param app
	 *            application
	 * @param anchor
	 *            relative element
	 */
	public static void share(AppW app, Widget anchor) {
		ShareControllerW sc = (ShareControllerW) app.getShareController();
		if (!nativeShareSupported()) {
			sc.setAnchor(anchor);
			sc.share();
		} else {
			sc.getBase64();
		}
	}

	/**
	 * @param online
	 *            whether the application is online renders a the online -
	 *            offline state of the FileMenu
	 */
	@Override
	public void render(boolean online) {
		shareItem.setEnabled(online);
		if (!online) {
			shareItem.setTitle(loc.getMenu("Offline"));
		} else {
			shareItem.setTitle("");
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			updateOpenFileButton();
		}
	}

	@Override
	public SVGResource getImage() {
		return activity.getResourceIconProvider().fileMenu();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "File";
	}

	private void addFileNewItem() {
		addItem(new FileNewItem(getApp()));
	}

	private void addShareItem() {
		shareItem = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.share_black(),
						getApp().getLocalization().getMenu("Share")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						share(app, null);
					}
				});
	}

	private void addExportImageItem() {
		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.export_image_black(),
				getApp().getLocalization().getMenu("exportImage")), true,
				new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						app.getDialogManager().showExportImageDialog(null);
					}
				});
	}

	private void addSaveItem() {
		if (getApp().getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.save_black(),
					loc.getMenu("Save")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							app.getGuiManager().save();
						}
					});
		}
	}

	private void addOpenFileItem() {
		openFileItem =
				addItem(MainMenu.getMenuBarHtml(
						activity.getResourceIconProvider().openFileMenu(),
						loc.getMenu(vendorSettings.getMenuLocalizationKey("Open"))),
						true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						app.openSearch(null);
					}
				});
	}

	private void addDownloadAsItem() {
		if (getApp().getLAF().exportSupported()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.file_download_black(),
					loc.getMenu("DownloadAs") + Unicode.ELLIPSIS), true,
					new ExportMenuW(getApp()), true);
		}
	}

	private void addPrintItem() {
		if (getApp().getLAF().printSupported()) {
			AriaMenuItem printItem = new AriaMenuItem(
					MainMenu.getMenuBarHtml(
							MaterialDesignResources.INSTANCE.print_black(),
							loc.getMenu("PrintPreview")),
					true, new MenuCommand(getApp()) {

				@Override
				public void doExecute() {
					getApp().getDialogManager()
							.showPrintPreview();
				}
			});
			// updatePrintMenu();
			addItem(printItem);
		}
	}
}
