package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.ExportImage;
import org.geogebra.web.full.gui.menubar.action.SaveAction;
import org.geogebra.web.full.gui.menubar.action.SaveLocalAction;
import org.geogebra.web.full.gui.menubar.action.ShareAction;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends Submenu implements BooleanRenderable {

	private AriaMenuItem shareItem;

	private final Localization loc;

	/**
	 * @param app
	 *            application
	 */
	public FileMenuW(final AppW app) {
		super("file", app);
		addExpandableStyleWithColor(false);
		this.loc = app.getLocalization();
		initActions();
	}

	private void initActions() {
		if (!GlobalScope.examController.isIdle()) {
			addItem("exam_menu_exit",
					new ExitExamAction(), MaterialDesignResources.INSTANCE.signout_black());
			return;
		}

		buildFileMenu();

		getApp().getNetworkOperation().getView().add(this);
		if (!getApp().getNetworkOperation().isOnline()) {
			render(false);
		}
	}

	private void buildFileMenu() {
		if (getApp().enableOnlineFileFeatures()) {
			addFileNewItem();
			addOpenFileItem();
			addSaveItems();
			addSeparator();
		}
		addExportImageItem();
		if (getApp().enableOnlineFileFeatures()) {
			addShareItem();
		}
		addDownloadAsItem();
		addPrintItem();
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
		if (GeoGebraJSNativeBridge.get() == null) {
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
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.fileMenu();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "File";
	}

	private void addFileNewItem() {
		addItem("New",
				new ClearAllAction(true),
				MaterialDesignResources.INSTANCE.newFileMenu());
	}

	private void addShareItem() {
		shareItem = addItem("Share",
				new ShareAction(),
				DefaultMenuIconProvider.INSTANCE.exportFile());
	}

	private void addExportImageItem() {
		addItem("exportImage",
				new ExportImage(),
				MaterialDesignResources.INSTANCE.export_image_black());
	}

	private void addSaveItems() {
		if (getApp().getLAF().undoRedoSupported()) {
			addItem("SaveOnline",
					new SaveAction(),
					DefaultMenuIconProvider.INSTANCE.saveOnline());

			addItem("SaveToYourPC",
					new SaveLocalAction(),
					DefaultMenuIconProvider.INSTANCE.save());
		}
	}

	private void addOpenFileItem() {
		addItem(MainMenu.getMenuBarItem(
				MaterialDesignResources.INSTANCE.openFileMenu(),
				loc.getMenu("Open"),
				new MenuCommand(getApp()) {

			@Override
			public void doExecute() {
				app.openSearch(null);
			}
		}));
	}

	private void addDownloadAsItem() {
		if (getApp().getLAF().exportSupported()) {
			AriaMenuItem export = addItem(new AriaMenuItem(
					loc.getMenu("DownloadAs") + Unicode.ELLIPSIS,
					MaterialDesignResources.INSTANCE.file_download_black(),
								new ExportMenuW(getApp())));
			export.setScheduledCommand(getSubmenuCommand(export, true));
		}
	}

	private void addPrintItem() {
		if (getApp().getLAF().printSupported()) {
			AriaMenuItem printItem = MainMenu.getMenuBarItem(
							MaterialDesignResources.INSTANCE.print_black(),
							loc.getMenu("PrintPreview"),
					new MenuCommand(getApp()) {
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
