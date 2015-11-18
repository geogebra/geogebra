package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.export.PrintPreviewW;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.images.AppResources;

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
				app.showStartScreen();
			}
		};
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
	}

	private void update() { 
	    // TODO Auto-generated method stub
	    
    }
	
	native boolean nativeShareSupported()/*-{
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
			app.showMessage(app.getExam().getLog(app.getLocalization()));
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
		String noIcon = AppResources.INSTANCE.empty().getSafeUri().asString();
		if (app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_sign_out().getSafeUri().asString(),app.getMenu("Close"), true),true,new MenuCommand(app) {

				@Override
				public void doExecute() {
					// set Firefox dom.allow_scripts_to_close_windows in about:config to true to make this work
					String[] optionNames = { app.getMenu("Cancel"),
					        app.getMenu("Exit") };

					app.getGuiManager()
							.getOptionPane()
							.showOptionDialog(app,
									app.getMenu("ExitExamConfirm"),
									app.getMenu("ExitExamConfirmTitle"),
					        GOptionPane.CUSTOM_OPTION, GOptionPane.WARNING_MESSAGE, null,
					        optionNames, new AsyncOperation() {
										@Override
										public void callback(Object obj) {
											String[] dialogResult = (String[]) obj;
											if ("1".equals(dialogResult[0])) {
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

		// this is enabled always
	    uploadToGGT = addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_share().getSafeUri().asString(),app.getMenu("Share"), true),true,new MenuCommand(app) {
	    	
	    	
	    	@Override
	    	public void doExecute() {
	    		if(!nativeShareSupported()){
	    			app.uploadToGeoGebraTube();
	    		} else {
							app.getGgbApi().getBase64(true,
									getShareStringHandler());
	    		}
	    	}
	    });
		if (app.getLAF().exportSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				        .menu_icons_file_export()
			        .getSafeUri().asString(), app.getMenu("Export"), true),
			        true, new ExportMenuW(app));

		}
		if (app.has(Feature.EXAM)) {
			addItem(MainMenu.getMenuBarHtml(noIcon,
							app.getMenu("EnterExamMode"), true),
					true,
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							((DialogManagerW) app.getDialogManager())
									.getSaveDialog().showIfNeeded(
											getExamCallback());

						}
					});
		}

		if (app.has(Feature.PRINT_MENU)) {
			addItem(MainMenu.getMenuBarHtml(noIcon,
					app.getMenu("PrintPreview"), true), true, new MenuCommand(
					app) {

				@Override
				public void doExecute() {
					new PrintPreviewW(app);
					// Window.print();

				}
			});

		}

	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}

	protected StringHandler getShareStringHandler() {
		return new StringHandler(){
			@Override
			public void handle(String s) {
				String title = app.getKernel().getConstruction().getTitle();
				FileManagerI fm = app.getFileManager();
				fm.nativeShare(s, "".equals(title) ? "construction" : title);
			}
		};
	}

	Runnable getExamCallback() {

		return new Runnable() {

			public void run() {
				ExamUtil.toggleFullscreen(true);
				app.setExam(new ExamEnvironment());
				Layout.initializeDefaultPerspectives(app, 0.2);
				app.fileNew();
				app.fireViewsChangedEvent();
				app.getGuiManager().setGeneralToolBarDefinition(
						ToolBar.getAllToolsNoMacros(true, true));
				app.getGgbApi().setPerspective("1");
				app.getGuiManager().resetMenu();

				app.examWelcome();

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
