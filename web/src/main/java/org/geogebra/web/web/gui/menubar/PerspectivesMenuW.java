package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Web implementation of FileMenu
 */
public class PerspectivesMenuW extends GMenuBar {
	
	/** Application */
	AppW app;
	private Layout layout;
	

	/**
	 * @param app application
	 */
	public PerspectivesMenuW(AppW app) {
	    super(true);
	    this.app = app;
	    this.layout = app.getGuiManager().getLayout();
	    addStyleName("GeoGebraMenuBar");
		initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {

		
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
	    ArrayList<ResourcePrototype> icons = new ArrayList<ResourcePrototype>();
	    PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		if (app.has(Feature.NEW_START_SCREEN)) {
			icons.add(pr.menu_icon_algebra());
			icons.add(pr.menu_icon_cas());
			icons.add(pr.menu_icon_geometry());
			icons.add(pr.menu_icon_graphics3D());
			icons.add(pr.menu_icon_spreadsheet());
			icons.add(pr.menu_icon_probability());
			icons.add(pr.menu_icon_exam());
		} else {
			icons.add(pr.menu_icon_algebra());
			icons.add(pr.menu_icon_geometry());
			icons.add(pr.menu_icon_spreadsheet());
			icons.add(pr.menu_icon_cas());
			icons.add(pr.menu_icon_graphics3D());
			icons.add(pr.menu_icon_probability());
		}
		for (int i = 0; i < defaultPerspectives.length; ++i) {
			if(defaultPerspectives[i] == null){
				continue;
			}
			final int index = i;
			final int defID = defaultPerspectives[i].getDefaultID();
			addItem(MainMenu.getMenuBarHtml(
					ImgResourceHelper.safeURI(icons.get(i)),
					app.getMenu(defaultPerspectives[i].getId()), true),true,new MenuCommand(app) {
						
						@Override
						public void doExecute() {
							setPerspective(app, index);
							if (!(app.isExam() && app.getExam().getStart() >= 0)) {
								((AppWFull) app).showStartTooltip(defID);
							}
						}
			});			
		}

		if (app.has(Feature.NEW_START_SCREEN) && !app.isExam()) {
			/*
			 * if (app.isExam()) {
			 * addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.
			 * menu_icon_sign_out().getSafeUri().asString(),
			 * app.getMenu("exam_menu_exit"), true), true, new MenuCommand(app)
			 * { // Close
			 * 
			 * @Override public void doExecute() { // set Firefox //
			 * dom.allow_scripts_to_close_windows in // about:config to true to
			 * make this work String[] optionNames = { app.getMenu("Cancel"),
			 * app.getMenu("Exit") };
			 * 
			 * app.getGuiManager().getOptionPane().showOptionDialog(app,
			 * app.getMenu("exam_exit_confirmation"), // ExitExamConfirm
			 * app.getMenu("exam_exit_header"), // ExitExamConfirmTitle 1,
			 * GOptionPane.WARNING_MESSAGE, null, optionNames, new
			 * AsyncOperation<String[]>() {
			 * 
			 * @Override public void callback(String[] obj) { if
			 * ("1".equals(obj[0])) { exitAndResetExam(); } } }); } });
			 * 
			 * return; }
			 */
			if (app.getLAF().examSupported(app.has(Feature.EXAM_TABLET))) {
				addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_exam().getSafeUri().asString(),
						app.getMenu("exam_menu_entry"), true), // "Exam Mode"
						true, new MenuCommand(app) {

							@Override
							public void doExecute() {
								((DialogManagerW) app.getDialogManager()).getSaveDialog()
										.showIfNeeded(getExamCallback());

							}
						});
			}
		}
	}

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
			app.getGuiManager().setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, false));
			app.getGuiManager().resetMenu();
		}
	}

	/**
	 * @return callback that shows the exam welcom message and prepares Exam
	 *         (goes fullscreen)
	 */
	Runnable getExamCallback() {

		return new Runnable() {

			public void run() {
				if (app.getLAF().supportsFullscreen()) {
					ExamUtil.toggleFullscreen(true);
				}
				app.setExam(new ExamEnvironment());
				((AppWFull) app).examWelcome();

			}
		};
	}

	/**
	 * @param index
	 *            perspective index
	 */
	static void setPerspective(AppW app, int index) {
		app.persistWidthAndHeight();
		boolean changed = app.getGuiManager().getLayout()
				.applyPerspective(Layout.defaultPerspectives[index]);
		app.updateViewSizes();
		app.getGuiManager().updateMenubar();
		// set active perspective for highlighting
		PerspectivesPopup.setActivePerspective(index);
		// app.getToolbar().closeAllSubmenu();
		if (app.getTubeId() < 1 && app.getArticleElement().getDataParamApp()) {
			Browser.changeUrl(Perspective.perspectiveSlugs[index]);
		}
		if (changed) {
			app.storeUndoInfo();
		}
	}
	


}
