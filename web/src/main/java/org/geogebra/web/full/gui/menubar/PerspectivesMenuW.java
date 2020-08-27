package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Window.Location;

/**
 * Web implementation of PerspectivesMenu
 */
public class PerspectivesMenuW extends Submenu {

	/** Application */
	AppW app;

	/**
	 * @param app application
	 */
	public PerspectivesMenuW(AppW app) {
		super("apps", app);
		this.app = app;
		addExpandableStyleWithColor(false);
		initActions();
	}

	private void initActions() {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		addPerspective(0, pr.menu_icon_algebra_transparent());
		addPerspective(3, pr.menu_icon_cas_transparent());
		addPerspective(1, pr.menu_icon_geometry_transparent());
		addPerspective(4, pr.menu_icon_graphics3D_transparent());
		addPerspective(2, pr.menu_icon_spreadsheet_transparent());
		addPerspective(5, pr.menu_icon_probability_transparent());

		if (!app.isExam()) {
			if (app.getLAF().examSupported()) {

				addItem(MainMenu.getMenuBarHtmlClassic(
						GuiResources.INSTANCE.menu_icon_exam24().getSafeUri()
								.asString(),
						app.getLocalization().getMenu("exam_menu_entry")), // "Exam
																					// Mode"
						true, new MenuCommand(app) {

							@Override
							public void doExecute() {
								app.getSaveController().showDialogIfNeeded(getExamCallback(), true);
							}
						});
			}
		}
	}

	private void addPerspective(final int index, ResourcePrototype icon) {
		if (Layout.getDefaultPerspectives(index) == null) {
			return;
		}
		final int defID = Layout.getDefaultPerspectives(index).getDefaultID();
		addItem(MainMenu.getMenuBarHtmlClassic(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(Layout.getDefaultPerspectives(index).getId())),
				true,
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
	AsyncOperation<Boolean> getExamCallback() {
		return new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean active) {
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
		if (StringUtil.emptyOrZero(app.getTubeId())
				&& app.getAppletParameters().getDataParamApp()) {
			Browser.changeMetaTitle(app.getLocalization()
					.getMenu(Layout.getDefaultPerspectives(index).getId()));
			updateURL(Perspective.getPerspectiveSlug(index));
		}
		if (changed) {
			app.storeUndoInfo();
		}
	}

	private static void updateURL(String slug) {
		// temporary: /graphing and /geometry in stable still point to
		// classic; the URLs should be rewritten to eg /classic#3d and not
		// changed when current perspective is selected
		if (Location.getPath().replace("/", "").equals(slug)) {
			return;
		}

		Browser.changeUrl("/classic#" + slug);

	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.geogebra_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Perspectives";
	}

	@Override
	protected boolean isViewDraggingMenu() {
		return true;
	}
}
