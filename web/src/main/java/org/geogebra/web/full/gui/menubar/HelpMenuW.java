package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;

/**
 * Help menu
 */
public class HelpMenuW extends Submenu implements BooleanRenderable {
	/**
	 * Settings for version/about window
	 */
	protected static final String ABOUT_WINDOW_PARAMS = "width=720,height=600,"
			+ "scrollbars=yes,toolbar=no,location=no,directories=no,"
			+ "menubar=no,status=no,copyhistory=no";
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
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		Localization loc = app.getLocalization();

		// Tutorials
		tutorials = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.tutorial_black(),
						loc.getMenu("Tutorials")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager().open(app.getLocalization()
								.getTutorialURL(app.getConfig()));
					}
				});
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
		about = addItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.info_black(),
						loc.getMenu("AboutLicense")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager()
								.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL
										+ "&version=" + app.getVersionString()
										+ "&date="
										+ GeoGebraConstants.BUILD_DATE,
										ABOUT_WINDOW_PARAMS);
					}
				});
		if (!app.getNetworkOperation().isOnline()) {
			render(false);
		}
		app.getNetworkOperation().getView().add(this);
		// TODO: This item has no localization entry yet.
		// addItem("About / Team", new Command() {
		// public void execute() {
		// Window.open(GeoGebraConstants.GGW_ABOUT_TEAM_URL, "_blank", "");
		// }
		// });
	}

	@Override
	public void render(boolean b) {
		about.setEnabled(b);
		manual.setEnabled(b);
		tutorials.setEnabled(b);
		bug.setEnabled(b);
		forum.setEnabled(b);
	}

	@Override
	public SVGResource getImage() {
		return SharedResources.INSTANCE.icon_help_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Help";
	}
}
