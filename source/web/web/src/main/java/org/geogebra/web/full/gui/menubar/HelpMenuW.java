package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;
import org.gwtproject.user.client.ui.Label;

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

		if (app.isWhiteboardActive()) {
			buildMenuNotes(app, loc);
		} else {
			buildMenuBase(app, loc);
		}

		if (!app.getNetworkOperation().isOnline()) {
			render(false);
		}
		app.getNetworkOperation().getView().add(this);
	}

	private void buildMenuBase(final AppW app, Localization loc) {
		addTutorialItem(app, loc);
		addManualItem(app, loc);
		addForumItem(app, loc);
		addReportBugItem(app, loc);
		addAboutItem();
		addVersionNumber(app);
	}

	private void buildMenuNotes(final AppW app, Localization loc) {
		addTutorialItem(app, loc);
		addForumItem(app, loc);
		addReportBugItem(app, loc);
		addAboutItem();
	}

	private void addTutorialItem(final AppW app, Localization loc) {
		final String tutorialURL = app.getLocalization().getTutorialURL(app.getConfig());
		if (!StringUtil.empty(tutorialURL)) {
			tutorials = addItem(
					MainMenu.getMenuBarItem(MaterialDesignResources.INSTANCE.tutorial_black(),
							loc.getMenu("Tutorials"),
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							app.getFileManager().open(tutorialURL);
						}
					}));
		}
	}

	private void addManualItem(final AppW app, Localization loc) {
		manual = addItem(
				MainMenu.getMenuBarItem(
						MaterialDesignResources.INSTANCE.manual_black(),
						loc.getMenu("Manual"),
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getGuiManager().openHelp(App.WIKI_MANUAL);

					}
				}));
	}

	private void addForumItem(final AppW app, Localization loc) {
		forum = addItem(
				MainMenu.getMenuBarItem(
						SharedResources.INSTANCE.icon_help_black(),
						loc.getMenu("Help"),
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager().open(GeoGebraConstants.FORUM_URL);
					}
				}));
	}

	private void addReportBugItem(final AppW app, Localization loc) {
		bug = addItem(
				MainMenu.getMenuBarItem(
						MaterialDesignResources.INSTANCE.bug_report_black(),
						loc.getMenu("ReportBug"),
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getFileManager().open(GeoGebraConstants.REPORT_BUG_URL);
					}
				}));
	}

	private void addAboutItem() {
		about = addItem("AboutLicense", new ShowLicenseAction(),
				new ImageIconSpec(MaterialDesignResources.INSTANCE.info_black()));
	}

	private void addVersionNumber(AppW appW) {
		String versionNr = GeoGebraConstants.getVersionString6();
		String versionStr = appW.getLocalization().getPlainDefault("VersionA",
				"Version %0", versionNr);
		Label version = BaseWidgetFactory.INSTANCE.newDisabledText(versionStr, "versionNr");
		add(version);
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
