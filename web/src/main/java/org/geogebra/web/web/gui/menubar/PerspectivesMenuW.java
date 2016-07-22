package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
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

	    PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		if (app.has(Feature.NEW_START_SCREEN)) {
			addPerspective(0, pr.menu_icon_algebra());
			addPerspective(3, pr.menu_icon_cas());
			addPerspective(1, pr.menu_icon_geometry());
			addPerspective(4, pr.menu_icon_graphics3D());
			addPerspective(2, pr.menu_icon_spreadsheet());
			addPerspective(5, pr.menu_icon_probability());
		} else {
			addPerspective(0, pr.menu_icon_algebra());
			addPerspective(1, pr.menu_icon_geometry());
			addPerspective(2, pr.menu_icon_spreadsheet());
			addPerspective(3, pr.menu_icon_cas());
			addPerspective(4, pr.menu_icon_graphics3D());
			addPerspective(5, pr.menu_icon_probability());
		}


		if (app.has(Feature.NEW_START_SCREEN) && !app.isExam()) {

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


	private void addPerspective(int i, ResourcePrototype icon) {
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
		if (defaultPerspectives[i] == null) {
			return;
		}
		final int index = i;
		final int defID = defaultPerspectives[i].getDefaultID();
		addItem(MainMenu.getMenuBarHtml(ImgResourceHelper.safeURI(icon),
				app.getMenu(defaultPerspectives[i].getId()), true), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						setPerspective(app, index);
						if (!(app.isExam() && app.getExam().getStart() >= 0)) {
							((AppWFull) app).showStartTooltip(defID);
						}
					}
				});

	}

	/**
	 * @return callback that shows the exam welcome message and prepares Exam
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
		app.setActivePerspective(index);
		// app.getToolbar().closeAllSubmenu();
		if (app.getTubeId() < 1 && app.getArticleElement().getDataParamApp()) {

			Browser.changeUrl(Perspective.perspectiveSlugs[index]);

		}
		if (changed) {
			app.storeUndoInfo();
		}
	}
	


}
