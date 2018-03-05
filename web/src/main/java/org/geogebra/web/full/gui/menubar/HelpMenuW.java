package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

/**
 * Help menu
 */
public class HelpMenuW extends GMenuBar implements BooleanRenderable {
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
				MainMenu.getMenuBarHtml(app.isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.tutorial_black()
								.getSafeUri().asString()
						: AppResources.INSTANCE.empty().getSafeUri().asString(),
						loc.getMenu("Tutorials"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getGuiManager().openHelp(App.WIKI_TUTORIAL);
					}
				});
		// Help
		manual = addItem(
				MainMenu.getMenuBarHtml(app.isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.manual_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE.menu_icon_help().getSafeUri()
								.asString(),
						loc.getMenu("Manual"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getGuiManager().openHelp(App.WIKI_MANUAL);

					}
				});
		forum = addItem(
				MainMenu.getMenuBarHtml(app.isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.forum_black()
								.getSafeUri().asString()
						: AppResources.INSTANCE.empty().getSafeUri().asString(),
						loc.getMenu("GeoGebraForum"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager().open(GeoGebraConstants.FORUM_URL,
								"_blank", "");
					}
				});
		addSeparator();
		// Report Bug
		bug = addItem(
				MainMenu.getMenuBarHtml(app.isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.bug_report_black()
								.getSafeUri().asString()
						: AppResources.INSTANCE.empty().getSafeUri().asString(),
						loc.getMenu("ReportBug"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager()
								.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB
										+ "&lang="
										+ app.getLocalization().getLanguage(),
										"_blank", "");
					}
				});
		addSeparator();
		about = addItem(
				MainMenu.getMenuBarHtml(
						app.isUnbundledOrWhiteboard()
								? MaterialDesignResources.INSTANCE.info_black()
										.getSafeUri().asString()
								: GuiResources.INSTANCE.menu_icon_help_about()
										.getSafeUri().asString(),
						loc.getMenu("AboutLicense"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager()
								.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL
										+ "&version=" + app.getVersionString()
										+ "&date="
										+ GeoGebraConstants.BUILD_DATE,
										"_blank",
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
}
