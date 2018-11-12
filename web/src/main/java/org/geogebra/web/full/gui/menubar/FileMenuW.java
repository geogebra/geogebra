package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.ExamLogBuilder;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.app.HTMLLogBuilder;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.exam.ExamDialog;
import org.geogebra.web.full.gui.exam.ExamExitConfirmDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar
		implements BooleanRenderable, EventRenderable {

	private static final double PADDING = 24;
	/** Canvas line height */
	protected static final int LINE_HEIGHT = 24;
	private AriaMenuItem shareItem;
	private AriaMenuItem openFileItem;
	/** clear construction and reset GUI */
	AsyncOperation<Boolean> newConstruction;
	private AriaMenuItem printItem;
	private Localization loc;
	/** file chooser */
	FileChooser fileChooser;

	/**
	 * @param app
	 *            application
	 */
	public FileMenuW(final AppW app) {
		super("file", app);
		this.loc = app.getLocalization();
		this.newConstruction = new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean active) {
				// ignore active: don't save means we want new construction
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();

				if (!app.isUnbundledOrWhiteboard()) {
					app.showPerspectivesPopup();
				}
				if (app.has(Feature.MOW_MULTI_PAGE)
						&& app.getPageController() != null) {
					app.getPageController().resetPageControl();
				}
			}
		};
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
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

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExamGraphing() {
		getApp().getLAF().toggleFullscreen(false);
		saveScreenshot(loc.getMenu("ExamGraphingCalc.long"));
		getApp().fileNew();
		resetAfterExam();
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		getApp().getLAF().toggleFullscreen(false);
		ExamEnvironment exam = getApp().getExam();
		exam.exit();
		boolean examFile = getApp().getArticleElement()
				.hasDataParamEnableGraphing();
		String buttonText = null;
		AsyncOperation<String[]> handler = null;
		AsyncOperation<String[]> welcomeHandler = null;
		if (examFile && !getApp().isUnbundledGraphing()) {
			if (getApp().getVersion().isAndroidWebview()) {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						// for android tablets we just want to exit app
						ExamDialog.exitApp();
					}
				};
				buttonText = loc.getMenu("Exit");
			} else {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						getApp().setNewExam();
						ExamDialog.startExam(null, getApp());
					}
				};
				welcomeHandler = new AsyncOperation<String[]>() {

					@Override
					public void callback(String[] obj) {
						getApp().getLAF().toggleFullscreen(true);
						getApp().setNewExam();
						getApp().examWelcome();
					}
				};
				buttonText = loc.getMenu("Restart");
			}
			exam.setHasGraph(true);
			boolean supportsCAS = getApp().getSettings().getCasSettings()
					.isEnabled();
			boolean supports3D = getApp().getSettings().getEuclidian(-1)
					.isEnabled();
			if (!supports3D && supportsCAS) {
				showFinalLog(loc.getMenu("ExamCAS"), buttonText, handler);
			} else if (!supports3D && !supportsCAS) {
				if (getApp().enableGraphing()) {
					showFinalLog(loc.getMenu("ExamGraphingCalc.long"),
							buttonText, handler);
				} else {
					showFinalLog(loc.getMenu("ExamSimpleCalc.long"), buttonText,
							handler);
				}
			} else {
				showFinalLog(loc.getMenu("exam_log_header") + " "
								+ getApp().getVersionString(),
						buttonText, welcomeHandler);
			}
		} else {
			handler = new AsyncOperation<String[]>() {
				@Override
				public void callback(String[] dialogResult) {
					getApp().fileNew();
				}
			};
			buttonText = loc.getMenu("OK");
			showFinalLog(loc.getMenu("exam_log_header") + " "
					+ getApp().getVersionString(), buttonText, handler);
		}
		resetAfterExam();
	}

	private void showFinalLog(String menu, String buttonText,
			AsyncOperation<String[]> handler) {
		getApp().fileNew();
		HTMLLogBuilder htmlBuilder = new HTMLLogBuilder();
		getApp().getExam().getLog(loc, getApp().getSettings(), htmlBuilder);
		getApp().showMessage(htmlBuilder.getHTML(), menu, buttonText, handler);
		saveScreenshot(menu);

	}

	private void saveScreenshot(String menu) {
		final int header = 78;
		Canvas canvas = Canvas.createIfSupported();
		final GGraphics2DW g2 = new GGraphics2DW(canvas);

		g2.setCoordinateSpaceSize(500,
				getApp().getExam().getEventCount() * LINE_HEIGHT + 350);
		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, canvas.getCoordinateSpaceWidth(),
				canvas.getCoordinateSpaceHeight());
		g2.setPaint(GColor.newColorRGB(
				getApp().getExam().isCheating() ? 0xD32F2F : 0x3DA196));
		g2.fillRect(0, 0, 500, header);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
		g2.setColor(GColor.WHITE);
		g2.drawString(menu, PADDING, PADDING);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
		g2.drawString(ExamUtil.status(getApp()), PADDING,
				PADDING + LINE_HEIGHT);
		g2.setColor(GColor.BLACK);
		ExamLogBuilder canvasLogBuilder = new ExamLogBuilder() {
			private int yOffset = header + LINE_HEIGHT;

			@Override
			public void addLine(StringBuilder sb) {
				g2.setColor(GColor.BLACK);
				g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
				g2.drawString(sb.toString(), PADDING, yOffset);
				yOffset += LINE_HEIGHT;
			}

			@Override
			public void addField(String name, String value) {
				g2.setColor(GColor.GRAY);
				g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
				g2.drawString(name, PADDING, yOffset);
				yOffset += LINE_HEIGHT;
				// no empty line after "Activity"
				if (!StringUtil.empty(value)) {
					g2.setColor(GColor.BLACK);
					g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
					g2.drawString(value, PADDING, yOffset);
					yOffset += LINE_HEIGHT;
				}
			}

		};

		getApp().getExam().getLog(loc, getApp().getSettings(),
				canvasLogBuilder);
		Browser.exportImage(canvas.toDataUrl(), "ExamLog.png");

	}

	private static native JavaScriptObject getCallback(String marker) /*-{
		return function(url) {
			@org.geogebra.web.html5.Browser::exportImage(Ljava/lang/String;Ljava/lang/String;)(marker+url, "screenshot.png");
		};
	}-*/;

	private void resetAfterExam() {
		getApp().setExam(null);
		getApp().resetViewsEnabled();
		LayoutW.resetPerspectives(getApp());
		getApp().getLAF().addWindowClosingHandler(getApp());
		getApp().fireViewsChangedEvent();
		getApp().getGuiManager().updateToolbarActions();
		getApp().getGuiManager().setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, false, getApp()));
		getApp().getGuiManager().resetMenu();
		getApp().setActivePerspective(0);
	}

	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.signout_black(),
					loc.getMenu("exam_menu_exit")), true,
					new MenuCommand(getApp()) { // Close

						@Override
						public void doExecute() {
							showExamExitDialog();
						}
					});
			return;
		}
		/*
		 * } else { if (app.isExam()) { return; } }
		 */
		// this is enabled always
		addItem(MainMenu
				.getMenuBarHtml(
						getApp().isWhiteboardActive()
								? MaterialDesignResources.INSTANCE.file_plus()
								: MaterialDesignResources.INSTANCE.add_black(),
						loc.getMenu(getApp().isWhiteboardActive()
								? "mow.newFile" : "New")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						fileNew();
					}
				});

		// open menu is always visible in menu
		openFileItem = addItem(
				MainMenu.getMenuBarHtml(
						getApp().isWhiteboardActive()
								? MaterialDesignResources.INSTANCE.folder_open()
								: MaterialDesignResources.INSTANCE
										.search_black(),
						loc.getMenu(getApp().isWhiteboardActive()
								? (isMowLoggedOut() ? "mow.offlineMyFiles"
										: "mow.myfiles")
								: "Open")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						if (isMowLoggedOut()) {
							if (fileChooser == null) {
								fileChooser = new FileChooser();
								fileChooser.addStyleName("hidden");
							}
							app.getPanel().add(fileChooser);
							fileChooser.open();
							return;
						}
						getApp().openSearch(null);
					}
				});
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
		if (!getApp().has(Feature.MOW_BURGER_MENU_CLEANUP)) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.export_image_black()
							.getSafeUri().asString(),
					loc.getMenu("exportImage"), true), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							app.getDialogManager().showExportImageDialog(null);
						}
					});
		}
		shareItem = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.share_black()
								.getSafeUri().asString(),
						loc.getMenu("Share"), true),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						share(getApp(), null);
					}
				});
		if (getApp().getLAF().exportSupported()
				&& !getApp().isUnbundledOrWhiteboard()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.file_download_black()
							.getSafeUri().asString(),
					loc.getMenu("DownloadAs") + Unicode.ELLIPSIS, true), true,
					new ExportMenuW(getApp()), true);
		}
		if (getApp().getLAF().printSupported()) {
			printItem = new AriaMenuItem(
					MainMenu.getMenuBarHtml(
							MaterialDesignResources.INSTANCE.print_black()
									.getSafeUri().asString(),
							loc.getMenu("PrintPreview"), true),
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
		openFileItem.setHTML(MainMenu.getMenuBarHtml(
				getApp().isWhiteboardActive() ? MaterialDesignResources.INSTANCE
						.folder_open().getSafeUri().asString()
						: MaterialDesignResources.INSTANCE.search_black()
								.getSafeUri().asString(),
				loc.getMenu(getApp().isWhiteboardActive() ? (isMowLoggedOut()
						? "mow.offlineMyFiles" : "mow.myfiles") : "Open")));
	}

	/**
	 * Create new file, maybe ask for save
	 */
	public void fileNew() {
		((DialogManagerW) getApp().getDialogManager()).getSaveDialog()
				.showIfNeeded(newConstruction);
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
	 * Show exit exam dialog
	 */
	protected void showExamExitDialog() {
		// set Firefox dom.allow_scripts_to_close_windows in about:config to
		// true to make this work
		String[] optionNames = { loc.getMenu("Cancel"), loc.getMenu("Exit") };

		if (getApp().isUnbundledGraphing()
				&& getApp().has(Feature.GRAPH_EXAM_MODE)) {
			new ExamExitConfirmDialog(getApp(), new AsyncOperation<String>() {
				@Override
				public void callback(String obj) {
					if ("exit".equals(obj)) {
						exitAndResetExamGraphing();
					}
				}
			}).show();
		} else {
			getApp().getGuiManager().getOptionPane().showOptionDialog(
					loc.getMenu("exam_exit_confirmation"), // ExitExamConfirm
					loc.getMenu("exam_exit_header"), // ExitExamConfirmTitle
					1, GOptionPane.WARNING_MESSAGE, null, optionNames,
					new AsyncOperation<String[]>() {
						@Override
						public void callback(String[] obj) {
							if ("1".equals(obj[0])) {
								exitAndResetExam();
							}
						}
					});
		}
	}

	/**
	 * @param online
	 *            wether the application is online renders a the online -
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

}
