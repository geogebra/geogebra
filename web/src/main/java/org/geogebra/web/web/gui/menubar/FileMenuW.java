package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.export.PrintPreviewW;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.exam.ExamDialog;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.gui.util.ShareDialogW;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable {
	
	/** Application */
	AppW app;
	private MenuItem shareItem;
	/** clear construction and reset GUI */
	Runnable newConstruction;
	private MenuItem printItem;
	private Localization loc;
	
	/**
	 * @param app application
	 */
	public FileMenuW(final AppW app) {
		super(true, "file");
	    this.app = app;
		this.loc = app.getLocalization();
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();

				app.showPerspectivesPopup();

			}
		};
	    addStyleName("GeoGebraMenuBar");
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
	protected void exitAndResetExam() {
		app.getLAF().toggleFullscreen(false);

		ExamEnvironment exam = app.getExam();
		exam.exit();
		boolean examFile = app.getArticleElement().hasDataParamEnableGraphing();
		String buttonText = null;
		AsyncOperation<String[]> handler = null;

		if (examFile) {
			if (app.has(Feature.BIND_ANDROID_TO_EXAM_APP)
					&& app.getVersion().isAndroidWebview()) {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						// for android tablets we just want to exit app
						ExamDialog.exitApp();
					}
				};
				buttonText = loc.getPlain("Exit");
			} else {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						app.setNewExam();
						ExamDialog.startExam(null, app);
					}
				};
				buttonText = loc.getPlain("Restart");
			}
			exam.setHasGraph(true);
			boolean supportsCAS = app.getSettings().getCasSettings()
					.isEnabled();
			boolean supports3D = app.getSettings().getEuclidian(-1).isEnabled();
			if (!supports3D && supportsCAS) {
					app.showMessage(
						exam.getLog(app.getLocalization(), app.getSettings()),
						loc.getMenu("ExamCAS"), buttonText, handler);
			} else if (!supports3D && !supportsCAS) {
				if (app.enableGraphing()) {
					app.showMessage(
							exam.getLog(app.getLocalization(),
									app.getSettings()),
							loc.getMenu("ExamGraphingCalc.long"), buttonText,
							handler);
				} else {
					app.showMessage(
							exam.getLog(app.getLocalization(),
									app.getSettings()),
							loc.getMenu("ExamSimpleCalc.long"), buttonText,
							handler);
				}
			}

		} else {
			app.showMessage(exam.getLog(loc, app.getSettings()),
					loc.getMenu("exam_log_header") + " "
							+ app.getVersionString(),
					buttonText, handler);
		}
		app.setExam(null);
		app.resetViewsEnabled();
		Layout.initializeDefaultPerspectives(app, 0.2);
		app.getLAF().addWindowClosingHandler(app);
		app.fireViewsChangedEvent();
		app.getGuiManager().updateToolbarActions();
		app.getGuiManager().setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, false, app));
		app.getGuiManager().resetMenu();

		app.setActivePerspective(0);

	}
	

	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_sign_out().getSafeUri().asString(),
					loc.getMenu("exam_menu_exit"), true), true,
					new MenuCommand(app) { // Close

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
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				.menu_icon_file_new().getSafeUri().asString(),
				loc.getMenu("New"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
						((DialogManagerW) app.getDialogManager()).getSaveDialog().showIfNeeded(newConstruction);

			}
		});

		// open menu is always visible in menu
		
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				.menu_icon_file_open().getSafeUri().asString(),
				loc.getMenu("Open"), true), true, new MenuCommand(app) {
    		
				@Override
				public void doExecute() {
			        app.openSearch(null);
				}
			});	
		
		
		if(app.getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_file_save().getSafeUri().asString(),
					loc.getMenu("Save"), true), true, new MenuCommand(app) {
		
				@Override
				public void doExecute() {
			        app.getGuiManager().save();
				}
			});			
		}

		addSeparator();

		shareItem = addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_file_share().getSafeUri().asString(),
					loc.getMenu("Share"), true), true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							if (!nativeShareSupported()) {
								showShareDialog();
							} else {
								app.getGgbApi().getBase64(true,
										getShareStringHandler(app));
							}


					}
			});



		if (app.getLAF().exportSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				        .menu_icons_file_export()
.getSafeUri().asString(),
					loc.getMenu("Export"), true),
			        true, new ExportMenuW(app));

		}


		if (app.getLAF().printSupported()) {
			Log.debug("new printItem");
			printItem = new MenuItem(MainMenu.getMenuBarHtml(
					GuiResources.INSTANCE
					.menu_icons_file_print().getSafeUri().asString(),
 loc.getMenu("PrintPreview"), true),
					true, new MenuCommand(
					app) {

				@Override
						public void doExecute() {
							if (app.getGuiManager()
									.showView(App.VIEW_EUCLIDIAN)
									|| app.getGuiManager().showView(
											App.VIEW_EUCLIDIAN2)
									|| app.getGuiManager().showView(
											App.VIEW_ALGEBRA)
									|| app.getGuiManager().showView(
											App.VIEW_CONSTRUCTION_PROTOCOL)) {
								new PrintPreviewW(app).show();
							}
						}
			});
			// updatePrintMenu();
			addItem(printItem);

		}

	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}

	/**
	 * Show exit exam dialog
	 */
	protected void showExamExitDialog() {
		// set Firefox dom.allow_scripts_to_close_windows in about:config to
		// true to make this work
		String[] optionNames = { loc.getMenu("Cancel"), loc.getMenu("Exit") };

		app.getGuiManager().getOptionPane().showOptionDialog(app,
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



	/**
	 * SHow the custom share dialog
	 */
	protected void showShareDialog() {
		Runnable shareCallback = new Runnable() {

			public void run() {
				ShareDialogW sd = new ShareDialogW(app);
				sd.setVisible(true);
				sd.center();

			}
		};
		if (app.getActiveMaterial() == null
				|| "P".equals(app.getActiveMaterial().getVisibility())) {
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				app.getLoginOperation().getView().add(new EventRenderable() {

					public void renderEvent(BaseEvent event) {
						if (event instanceof LoginEvent
								&& ((LoginEvent) event).isSuccessful()) {
							showShareDialog();
						}

					}
				});
				((SignInButton) app.getLAF().getSignInButton(app)).login();
			} else {
				// not saved, logged in
				((DialogManagerW) app.getDialogManager()).getSaveDialog()
						.setDefaultVisibility(SaveDialogW.Visibility.Shared)
					.showIfNeeded(shareCallback, true);
			}
		} else {
			// saved
			shareCallback.run();
		}

	}

	/**
	 * Go to geogebra.org or close iframe if we are running in one
	 */
	protected native void backToGeoGebra() /*-{
		if ($wnd != $wnd.parent) {
			$wnd.parent.postMessage("{\"type\":\"closesingleton\"}",
					location.protocol + "//" + location.host);
		} else {
			$wnd.location.assign("/");
		}

	}-*/;


	/**
	 * 
	 * @param app
	 *            application
	 * @return handler for native sharing
	 */
	public static StringHandler getShareStringHandler(final AppW app) {
		return new StringHandler(){
			@Override
			public void handle(String s) {
				String title = app.getKernel().getConstruction().getTitle();
				FileManagerI fm = app.getFileManager();
				fm.nativeShare(s, "".equals(title) ? "construction" : title);
			}
		};
	}

	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	public void render(boolean online) {
		shareItem.setEnabled(online);
	    if (!online) {
			shareItem.setTitle(loc.getMenu("Offline"));
		} else {
			shareItem.setTitle("");
		}
    }
}
