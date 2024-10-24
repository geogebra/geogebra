package org.geogebra.web.shared;

import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class ProfileAvatar extends FlowPanel {
	protected AppW app;
	protected LocalizationW loc;
	protected GPopupMenuW profilePanel;
	private Image avatar;
	private Image profileImage;
	private Label userName;
	protected String profileLink;
	private String editProfileHref = "https://accounts.geogebra.org/";
	private AriaMenuItem profileItem;
	private AriaMenuItem settingsItem;
	private AriaMenuItem signOutItem;

	/**
	 * profile avatar constructor
	 * @param app - application
	 */
	public ProfileAvatar(final AppW app) {
		setStyleName("profilePanel");
		this.app = app;
		loc = app.getLocalization();
		buildGui();

		buildPopup(app);
		addDomHandler(event -> {
			togglePopup();
			event.stopPropagation();
		}, ClickEvent.getType());
	}

	private void buildGui() {
		FlowPanel imageHolder = new FlowPanel();
		imageHolder.addStyleName("imageHolder");

		avatar = new Image();
		avatar.setStyleName("profileImage");

		imageHolder.add(avatar);
		add(imageHolder);
	}

	private void buildPopup(final AppW app) {
		profilePanel = new GPopupMenuW(app);
		profilePanel.getPopupPanel().addStyleName("profilePopup");
		profilePanel.getPopupPanel().setAutoHideEnabled(true);
		profilePanel.getPopupPanel().addAutoHidePartner(getElement());

		addAvatarPanel();
		addProfileItem();
		addSettingsItem();
		profilePanel.addVerticalSeparator();
		addSignOutItem();

		profilePanel.getPopupPanel().addCloseHandler((event) -> removeStyleName("selected"));
	}

	private void addAvatarPanel() {
		FlowPanel imageHolder = new FlowPanel();
		profileImage = new Image();
		profileImage.setStyleName("profileImage");

		userName = BaseWidgetFactory.INSTANCE.newPrimaryText("");
		imageHolder.add(profileImage);
		imageHolder.add(userName);

		AriaMenuItem profileItem = new AriaMenuItem(imageHolder, (Scheduler.ScheduledCommand) null);
		profileItem.addStyleName("profileItem");
		profilePanel.addItem(profileItem);
	}

	private void addProfileItem() {
		profileItem =
				MainMenu.getMenuBarItem(MaterialDesignResources.INSTANCE.person_black(),
						loc.getMenu("ProfilePanel.Profile"),
				(Command) () -> Browser.openWindow(profileLink));
		profilePanel.addItem(profileItem);
	}

	private void addSettingsItem() {
		settingsItem =
				MainMenu.getMenuBarItem(MaterialDesignResources.INSTANCE.settings_border(),
						loc.getMenu("ProfilePanel.Settings"),
				(Command) () -> Browser.openWindow(editProfileHref));
		profilePanel.addItem(settingsItem);
	}

	private void addSignOutItem() {
		signOutItem =
				MainMenu.getMenuBarItem(MaterialDesignResources.INSTANCE.signout_black(),
						loc.getMenu("SignOut"),
				(Command) () -> {
			app.getLoginOperation().showLogoutUI();
			app.getLoginOperation().performLogOut();
			togglePopup();
		});
		profilePanel.addItem(signOutItem);
	}

	/**
	 * Update profile pic
	 * @param user signed in user
	 */
	public void update(GeoGebraTubeUser user) {
		profileLink = user.getProfileURL();
		userName.setText(user.getUserName());
		if (user.getImageURL() != null) {
			avatar.setUrl(user.getImageURL());
			profileImage.setUrl(user.getImageURL());
		} else {
			avatar.setUrl(
					SharedResources.INSTANCE.icon_help_black().getSafeUri().asString());
			profileImage.setUrl(
					SharedResources.INSTANCE.icon_help_black().getSafeUri().asString());
		}
	}

	/**
	 * show / hide popupPanel.
	 */
	void togglePopup() {
		if (profilePanel.getPopupPanel().isShowing()) {
			profilePanel.hide();
		} else {
			profilePanel.getPopupPanel().showRelativeTo(this);
		}
		Dom.toggleClass(this, "selected", profilePanel.getPopupPanel().isShowing());
	}

	/**
	 * update popup language
	 */
	public void setLabels() {
		profileItem.setTextContent(
						loc.getMenu("ProfilePanel.Profile"));
		settingsItem.setTextContent(
				loc.getMenu("ProfilePanel.Settings"));
		signOutItem.setTextContent(
				loc.getMenu("SignOut"));
	}
}
