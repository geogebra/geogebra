package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;

/**
 * Help menu
 */
public class HelpMenuW extends Submenu implements BooleanRenderable {

	private AriaMenuItem tutorials;
	private AriaMenuItem forum;
	private AriaMenuItem manual;
	private AriaMenuItem about;
	private AriaMenuItem bug;

	/**
	 * @param app
	 *            application
	 */
	public HelpMenuW(final AppW app) {
		super("help", app);
		addExpandableStyleWithColor(false);
		Localization loc = app.getLocalization();
		final String tutorialURL = app.getLocalization().getTutorialURL(app.getConfig());
		// Tutorials
		if (!StringUtil.empty(tutorialURL)) {
			tutorials = addItem(
					MainMenu.getMenuBarHtml(MaterialDesignResources.INSTANCE.tutorial_black(),
							loc.getMenu("Tutorials")),
					true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							app.getFileManager().open(tutorialURL);
						}
					});
		}
		// Help
		manual = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.manual_black(),
						loc.getMenu("Manual")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getGuiManager().openHelp(App.WIKI_MANUAL);

					}
				});
		forum = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.forum_black(),
						loc.getMenu("GeoGebraForum")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager().open(GeoGebraConstants.FORUM_URL);
					}
				});
		addSeparator();
		// Report Bug
		bug = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.bug_report_black(),
						loc.getMenu("ReportBug")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager()
								.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB
										+ "&lang="
										+ app.getLocalization().getLanguage());
					}
				});
		addSeparator();
		about = addItem(new LicenseAction(app));
		if (!app.getNetworkOperation().isOnline()) {
			render(false);
		}
		app.getNetworkOperation().getView().add(this);
	}

	@Override
	public void render(boolean online) {
		about.setEnabled(online);
		manual.setEnabled(online);
		tutorials.setEnabled(online);
		bug.setEnabled(online);
		forum.setEnabled(online);
	}

	@Override
	public SVGResource getImage() {
		return SharedResources.INSTANCE.icon_help_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "HelpAndFeedback";
	}
}
