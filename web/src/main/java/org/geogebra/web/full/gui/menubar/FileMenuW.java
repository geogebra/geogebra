package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.FileNewAction;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends Submenu implements BooleanRenderable, EventRenderable {

	private AriaMenuItem shareItem;
	private AriaMenuItem openFileItem;

	private Localization loc;
	/** file chooser */
	FileChooser fileChooser;

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

	/**
	 * @return whether native JS function for sharing is present
	 */
	public native static boolean nativeShareSupported()/*-{
		if ($wnd.android && $wnd.android.share) {
			return true;
		}
		return false;
	}-*/;

	private static native JavaScriptObject getCallback(String marker) /*-{
		return function(url) {
			@org.geogebra.web.html5.Browser::exportImage(Ljava/lang/String;Ljava/lang/String;)(marker+url, "screenshot.png");
		};
	}-*/;

	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (getApp().isExam()) {
			addItem(new ExitExamAction(getApp()));
			return;
		}
		/*
		 * } else { if (app.isExam()) { return; } }
		 */
		// this is enabled always
		addItem(new FileNewAction(getApp()));

		// open menu is always visible in menu
		openFileItem = addItem(
				MainMenu.getMenuBarHtml(
						getApp().isWhiteboardActive()
								? MaterialDesignResources.INSTANCE.folder_open()
								: MaterialDesignResources.INSTANCE
										.search_black(),
						loc.getMenu(getApp().isWhiteboardActive()
								? "mow.myfiles" : "Open")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						if (getApp().isWhiteboardActive() && isMowLoggedOut()) {
							getApp().getLoginOperation().showLoginDialog();
							getApp().getLoginOperation().getView()
									.add(new EventRenderable() {
										@Override
										public void renderEvent(
												BaseEvent event) {
											if (event instanceof LoginEvent
													&& ((LoginEvent) event)
															.isSuccessful()) {
												getApp().openSearch(null);
											}
											if (event instanceof LoginEvent
													|| event instanceof StayLoggedOutEvent) {
												getApp().getLoginOperation()
														.getView().remove(this);
											}
										}
									});
						} else {
							getApp().openSearch(null);
						}
					}
				});

		if (getApp().isWhiteboardActive()) {
			addItem(
					MainMenu.getMenuBarHtml(
							MaterialDesignResources.INSTANCE
									.mow_pdf_open_folder(),
							loc.getMenu("mow.offlineMyFiles")),
					true, new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							if (fileChooser == null) {
								fileChooser = new FileChooser();
								fileChooser.addStyleName("hidden");
							}
							app.getPanel().add(fileChooser);
							fileChooser.open();
						}
					});
		}

		if (getApp().getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.save_black(),
					loc.getMenu("Save")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							getApp().getGuiManager().save();
						}
					});
		}
		addSeparator();
		if (!getApp().isWhiteboardActive()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.export_image_black(),
					loc.getMenu("exportImage")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							app.getDialogManager().showExportImageDialog(null);
						}
					});
		}
		shareItem = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.share_black(),
						loc.getMenu("Share")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						share(getApp(), null);
					}
				});
		if (getApp().getLAF().exportSupported()
				&& !getApp().isUnbundledOrWhiteboard()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.file_download_black(),
					loc.getMenu("DownloadAs") + Unicode.ELLIPSIS), true,
					new ExportMenuW(getApp()), true);
		}
		if (getApp().getLAF().printSupported()) {
			AriaMenuItem printItem = new AriaMenuItem(
					MainMenu.getMenuBarHtml(
							MaterialDesignResources.INSTANCE.print_black(),
							loc.getMenu("PrintPreview")),
					true, new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							((DialogManagerW) getApp().getDialogManager())
									.showPrintPreview();
						}
					});
			// updatePrintMenu();
			addItem(printItem);
		}
		getApp().getNetworkOperation().getView().add(this);
		if (!getApp().getNetworkOperation().isOnline()) {
			render(false);
		}
		if (getApp().getLoginOperation() != null) {
			getApp().getLoginOperation().getView().add(this);
		}
		updateShareButton();
	}

	private void updateShareButton() {
		shareItem.setVisible(getApp().getLoginOperation() != null
				&& getApp().getLoginOperation().canUserShare());
	}

	private void updateOpenFileButton() {
		openFileItem.setHTML(MainMenu.getMenuBarHtmlClassic(
				getApp().isWhiteboardActive() ? MaterialDesignResources.INSTANCE
						.folder_open().getSafeUri().asString()
						: MaterialDesignResources.INSTANCE.search_black()
						.getSafeUri().asString(),
				loc.getMenu(getApp().isWhiteboardActive() ? "mow.myfiles" : "Open")));
	}

	/**
	 * @return true if the whiteboard is active and the user logged in
	 */
	boolean isMowLoggedOut() {
		return getApp().isWhiteboardActive()
				&& getApp().getLoginOperation() != null
				&& !getApp().getLoginOperation().isLoggedIn();
	}

	private class FileChooser extends FileUpload implements ChangeHandler {
		private BrowseGUI bg;

		public FileChooser() {
			super();
			bg = new BrowseGUI(getApp(), this);
			addChangeHandler(this);
			getElement().setAttribute("accept", ".ggs");
		}

		public void open() {
			click();
		}

		@Override
		public void onChange(ChangeEvent event) {
			openFile(getSelectedFile(), bg);
			this.removeFromParent();
		}

		private void openFile(JavaScriptObject fileToHandle, BrowseGUI brG) {
			brG.openFile(fileToHandle, null);
		}

		private native JavaScriptObject getSelectedFile()/*-{
			return $doc.querySelector('input[type=file]').files[0];
		}-*/;
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
			updateShareButton();
			updateOpenFileButton();
		}
	}

	@Override
	public SVGResource getImage() {
		return getApp().isWhiteboardActive() ? MaterialDesignResources.INSTANCE.file()
				: MaterialDesignResources.INSTANCE.insert_file_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "File";
	}

}
