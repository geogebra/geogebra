package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Web implementation of PerspectivesMenu
 */
public class PerspectivesMenuW extends Submenu {

	/** Application */
	AppW app;
	private final ExamController examController = GlobalScope.examController;

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

		if (examController.isIdle()) {
			if (app.getLAF().isOfflineExamSupported()) {
				// "Exam
				// Mode"
				addItem(MainMenu.getMenuBarItem(
						GuiResources.INSTANCE.menu_icon_exam24(),
						app.getLocalization().getMenu("exam_menu_entry"), new MenuCommand(app) {
							@Override
							public void doExecute() {
								app.getSaveController().showDialogIfNeeded(getExamCallback(), true);
							}
						}) // "Exam
																					// Mode"
				);
			}
		}
	}

	private void addPerspective(final int index, ResourcePrototype icon) {
		Perspective perspective = app.getLayout().getDefaultPerspectives(index);
		if (perspective == null) {
			return;
		}
		AriaMenuItem item = addItem(MainMenu.getMenuBarItem(icon,
				app.getLocalization()
						.getMenu(perspective.getId()),
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						setPerspective(app, perspective);
						if (!examController.isExamActive()) {
							app.showStartTooltip(perspective);
						}
					}
				}));
		item.addStyleName("noTransparency");
	}

	/**
	 * @return callback that shows the exam welcome message and prepares Exam
	 *         (goes fullscreen)
	 */
	AsyncOperation<Boolean> getExamCallback() {
		return active -> app.showExamWelcomeMessage();
	}

	/**
	 * @param app
	 *            application
	 * @param perspective
	 *            perspective
	 */
	static void setPerspective(AppW app, Perspective perspective) {
		app.persistWidthAndHeight();
		boolean changed = app.getGuiManager().getLayout()
				.applyPerspective(perspective);
		app.updateViewSizes();
		app.getGuiManager().updateMenubar();
		// set active perspective for highlighting
		app.setActivePerspective(perspective);
		// app.getToolbar().closeAllSubmenu();
		if (StringUtil.emptyOrZero(app.getTubeId())
				&& app.getAppletParameters().getDataParamApp()) {
			Browser.changeMetaTitle(app.getLocalization()
					.getMenu(perspective.getId()));
			Browser.changeUrl("/classic#" + perspective.getSlug());
		}
		if (changed) {
			app.storeUndoInfo();
		}
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
