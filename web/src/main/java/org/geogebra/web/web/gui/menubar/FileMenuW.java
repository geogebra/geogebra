package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.export.PrintPreviewW;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.util.ShareDialogW;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable {
	
	/** Application */
	AppW app;
	private MenuItem uploadToGGT;
	/** clear construction and reset GUI */
	Runnable newConstruction;
	private MenuItem printItem;
	
	/**
	 * @param app application
	 */
	public FileMenuW(final AppW app) {
	    super(true);
	    this.app = app;
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
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
		if (!ExamUtil.toggleFullscreen(false)) {
			app.getExam().exit();
			app.showMessage(true, app.getExam().getLog(app.getLocalization()), app.getMenu("exam_log_header"));
			app.setExam(null);
			Layout.initializeDefaultPerspectives(app, 0.2);
			app.getLAF().addWindowClosingHandler(app);
			app.fireViewsChangedEvent();
			app.getGuiManager().updateToolbarActions();
			app.getGuiManager().setGeneralToolBarDefinition(
					ToolBar.getAllToolsNoMacros(true, false));
			app.getGuiManager().resetMenu();
		}
	}
	

	private void initActions() {
		if (app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_sign_out().getSafeUri().asString(),app.getMenu("exam_menu_exit"), true),true,new MenuCommand(app) { //Close

				@Override
				public void doExecute() {
					// set Firefox dom.allow_scripts_to_close_windows in about:config to true to make this work
					String[] optionNames = { app.getMenu("Cancel"),
					        app.getMenu("Exit") };

					app.getGuiManager()
							.getOptionPane()
							.showOptionDialog(app,
									app.getMenu("exam_exit_confirmation"), //ExitExamConfirm
									app.getMenu("exam_exit_header"), //ExitExamConfirmTitle
											1, GOptionPane.WARNING_MESSAGE,
											null,
											optionNames,
											new AsyncOperation<String[]>() {
										@Override
												public void callback(
														String[] obj) {
													if ("1".equals(obj[0])) {
												exitAndResetExam();
											}
										}
					        });
				}
			});

			return;
		}
		
		
		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_new().getSafeUri().asString(),app.getMenu("New"), true),true,new MenuCommand(app) {

			@Override
			public void doExecute() {
						((DialogManagerW) app.getDialogManager()).getSaveDialog().showIfNeeded(newConstruction);
						if (app.has(Feature.NEW_START_SCREEN)) {
							app.showPerspectivesPopup();
						}
			}
		});

		// open menu is always visible in menu
		
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getPlain("Open"), true),true,new MenuCommand(app) {
    		
				@Override
				public void doExecute() {
			        app.openSearch(null);
				}
			});	
		
		
		if(app.getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_save().getSafeUri().asString(), app.getMenu("Save"), true),true,new MenuCommand(app) {
		
				@Override
				public void doExecute() {
			        app.getGuiManager().save();
				}
			});			
		}

		addSeparator();

		if (app.has(Feature.WEB_SHARE_DIALOG)) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_file_share().getSafeUri().asString(),
					app.getMenu("Share"), true), true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							if (app.getActiveMaterial() == null) {
								app.getGuiManager().save();
							} else if (!app.getLoginOperation().isLoggedIn()) {
								((SignInButton) app.getLAF()
										.getSignInButton(app)).login();
							} else {
								ShareDialogW sd = new ShareDialogW(app);
								sd.setVisible(true);
								sd.center();
							}

					}
			});

		} else {
		// this is enabled always

			uploadToGGT = addItem(
					MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_share().getSafeUri().asString(),
							app.getMenu("Share"), true),
					true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							if (!nativeShareSupported()) {
								app.uploadToGeoGebraTube();
							} else {
						app.getGgbApi().getBase64(true,
								getShareStringHandler(app));
							}
						}
					});
		}

		if (app.getLAF().exportSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				        .menu_icons_file_export()
			        .getSafeUri().asString(), app.getMenu("Export"), true),
			        true, new ExportMenuW(app));

		}


		if (app.getLAF().printSupported()) {
			Log.debug("new printItem");
			printItem = new MenuItem(MainMenu.getMenuBarHtml(
					GuiResources.INSTANCE
					.menu_icons_file_print().getSafeUri().asString(),
					app.getMenu("PrintPreview"), true), true, new MenuCommand(
					app) {

				@Override
						public void doExecute() {
							if (app.getGuiManager()
									.showView(App.VIEW_EUCLIDIAN)
									|| app.getGuiManager().showView(
											App.VIEW_EUCLIDIAN2)
									|| app.has(Feature.WEB_PRINT_CP_VIEW)
									&& app.getGuiManager().showView(
											App.VIEW_CONSTRUCTION_PROTOCOL)) {
								new PrintPreviewW(app).show();
							}
						}
			});
			// updatePrintMenu();
			addItem(printItem);

		}

		addSeparator();

		if (app.getLAF().examSupported(app.has(Feature.EXAM_TABLET))) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icons_exam_mode().getSafeUri().asString(),
					app.getMenu("exam_menu_enter"), true),// EnterExamMode
					true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							((DialogManagerW) app.getDialogManager())
									.getSaveDialog().showIfNeeded(
											getExamCallback());

						}
					});
		}

	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}

	private boolean printItemAdded = false;

	/**
	 * Show or hide print TODO not implemented
	 */
	public void updatePrintMenu() {
		// Log.debug("print item added: " + printItemAdded);
		// if (printItem == null)
		// return;
		// if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN)
		// || app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
		// Log.debug("show print item");
		// // printItem.setVisible(true);
		// // printItem.setEnabled(true);
		// // if (!printItemAdded) { // if (printItem.getParentMenu() == null)
		// // {
		// Log.debug("add print menu");
		// addItem(printItem);
		// printItemAdded = true;
		// // }
		// } else {
		// Log.debug("don't show print item");
		// // printItem.setVisible(false);
		// // printItem.setEnabled(false);
		// // if (printItemAdded) { // if (printItem.getParentMenu() != null) {
		// Log.debug("remove print menu");
		// removeItem(printItem);
		// printItemAdded = false;
		// // }
		//
		// }
	}

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
	 * @return callback that shows the exam welcom message and prepares Exam
	 *         (goes fullscreen)
	 */
	Runnable getExamCallback() {

		return new Runnable() {

			public void run() {
				ExamUtil.toggleFullscreen(true);
				app.setExam(new ExamEnvironment());
				((AppWFull) app).examWelcome();

			}
		};
	}
	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	public void render(boolean online) {
	    uploadToGGT.setEnabled(online);
	    if (!online) {
	    	uploadToGGT.setTitle(app.getMenu("YouAreOffline"));
		} else {
			uploadToGGT.setTitle("");
		}
    }
}
