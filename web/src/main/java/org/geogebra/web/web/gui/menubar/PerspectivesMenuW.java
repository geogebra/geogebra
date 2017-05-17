package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
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
	

	/**
	 * @param app application
	 */
	public PerspectivesMenuW(AppW app) {
		super(true, "apps", app);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
		initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {

	    PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();

		addPerspective(0, pr.menu_icon_algebra());
		addPerspective(3, pr.menu_icon_cas());
		addPerspective(1, pr.menu_icon_geometry());
		addPerspective(4, pr.menu_icon_graphics3D());
		addPerspective(2, pr.menu_icon_spreadsheet());
		addPerspective(5, pr.menu_icon_probability());
		if (app.has(Feature.WHITEBOARD_APP)) {
			addPerspective(6, pr.menu_icon_whiteboard());
		}


		if (!app.isExam()) {

			if (app.getLAF().examSupported(app.has(Feature.EXAM_TABLET))) {
				addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_exam().getSafeUri().asString(),
						app.getLocalization().getMenu("exam_menu_entry"), true), // "Exam
																					// Mode"
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
		if (Layout.getDefaultPerspectives(i) == null) {
			return;
		}
		final int index = i;
		final int defID = Layout.getDefaultPerspectives(i).getDefaultID();
		addItem(MainMenu.getMenuBarHtml(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(Layout.getDefaultPerspectives(i).getId()),
				true), true,
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

			@Override
			public void run() {
				app.getLAF().toggleFullscreen(true);

				app.setNewExam();
				((AppWFull) app).examWelcome();

			}
		};
	}

	/**
	 * @param app
	 *            application
	 * @param index
	 *            perspective index
	 */
	static void setPerspective(AppW app, int index) {
		app.persistWidthAndHeight();
		boolean changed = app.getGuiManager().getLayout()
				.applyPerspective(Layout.getDefaultPerspectives(index));
		app.updateViewSizes();
		app.getGuiManager().updateMenubar();
		// set active perspective for highlighting
		app.setActivePerspective(index);
		// app.getToolbar().closeAllSubmenu();
		if (app.getTubeId() < 1
				&& app.getArticleElement().getDataParamApp()) {

			Browser.changeUrl(Perspective.getPerspectiveSlug(index));

		}
		if (changed) {
			app.storeUndoInfo();
		}
	}
	


}
