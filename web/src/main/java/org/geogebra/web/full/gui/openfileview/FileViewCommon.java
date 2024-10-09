package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.gui.layout.scientific.SettingsAnimator;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.ProfileAvatar;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.shared.components.ComponentSearchBar;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class FileViewCommon extends AnimatingPanel implements Persistable {

	private final AppW app;
	private final String title;
	// header
	private HeaderView headerView;
	private ComponentSearchBar searchBar;

	// content panel
	private FlowPanel contentPanel;
	// material panel
	private FlowPanel materialPanel;
	private final LocalizationW loc;
	private Button signInTextButton;
	private StandardButton signInIconButton;
	private ProfileAvatar profilePanel;
	private StandardButton examInfoBtn;
	private Label timer;
	private FlowPanel emptyListNotificationPanel;
	private LoadSpinner spinner;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * @param app the application
	 * @param title the header title key.
	 * @param withSearch true if searchbar should be added to header
	 */
	public FileViewCommon(AppW app, String title, boolean withSearch) {
		loc = app.getLocalization();
		this.app = app;
		this.title = title;
		setAnimator(new SettingsAnimator(app.getAppletFrame(), this));
		initGUI(withSearch);
	}

	private void initGUI(boolean withSearch) {
		this.setStyleName("openFileView");
		addStyleName("panelFadeIn");
		initHeader(withSearch);
		initContentPanel();
		initSpinner();
		initMaterialPanel();
		setLabels();
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("materialPanel");
	}

	private void initHeader(boolean withSearch) {
		headerView = new HeaderView();
		headerView.setCaption(title);
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(source -> {
			updateAnimateOutStyle();
			CSSEvents.runOnAnimation(this::close, getElement(), getAnimateOutStyle());
		});

		if (withSearch) {
			addSearchBar();
			buildSingInPanel();
		}
		if (!examController.isIdle()) {
			addExamPanel();
		}
		this.setHeaderWidget(headerView);
	}

	private void addExamPanel() {
		addExamTimeLabel();
		addExamInfoButton();
	}

	private void addExamTimeLabel() {
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		// run timer
		AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (!examController.isIdle()) {
					timer.setText(examController.getDurationFormatted(loc));
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
		headerView.add(timer);
	}

	private void addExamInfoButton() {
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		examInfoBtn.addFastClickHandler(source -> showExamDialog(examInfoBtn));
		headerView.add(examInfoBtn);
	}

	private void showExamDialog(StandardButton examInfoBtn) {
		new ExamLogAndExitDialog(app, true, examInfoBtn).show();
	}

	private void buildSingInPanel() {
		SignInControllerI signInController = app.getLAF()
				.getSignInController(app);
		signInTextButton = getLoginTextButton(signInController);
		signInTextButton.setStyleName("signIn");
		getHeader().add(signInTextButton);

		signInIconButton = getLoginIconButton(signInController);
		getHeader().add(signInIconButton);

		profilePanel = new ProfileAvatar(app);
		getHeader().add(profilePanel);

		final GeoGebraTubeUser user = app.getLoginOperation().getModel()
				.getLoggedInUser();
		if (user == null) {
			profilePanel.setVisible(false);
		} else {
			profilePanel.update(user);
			signInTextButton.setVisible(false);
			signInIconButton.setVisible(false);
		}
	}

	private Button getLoginTextButton(SignInControllerI signInController) {
		Button button = new Button(app.getLocalization().getMenu("SignIn"));
		button.getElement().setAttribute("type", "button");
		button.addStyleName("signInButton");
		button.addClickHandler(event -> {
			signInController.login();
			signInController.initLoginTimer();
		});
		return button;
	}

	private StandardButton getLoginIconButton(SignInControllerI signInController) {
		StandardButton button = new StandardButton(MaterialDesignResources.INSTANCE.login(), 24);
		button.setStyleName("signInIcon flatButtonHeader");
		button.addFastClickHandler(event -> {
			signInController.login();
			signInController.initLoginTimer();
		});
		return button;
	}

	private void updateSignInButtonsVisibility(boolean smallScreen) {
		final GeoGebraTubeUser user = app.getLoginOperation().getModel()
				.getLoggedInUser();
		if (user == null && signInIconButton != null && signInTextButton != null) {
			signInIconButton.setVisible(smallScreen);
			signInTextButton.setVisible(!smallScreen);
		}
	}

	private void addSearchBar() {
		searchBar = new ComponentSearchBar(app);
		getHeader().add(searchBar);
	}

	private void initContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	/**
	 * adds content if available
	 */
	public void addContent() {
		if (emptyListNotificationPanel != null) {
			emptyListNotificationPanel.removeFromParent();
		}
		contentPanel.add(materialPanel);
	}

	/**
	 * Clear contents
	 */
	public void clearPanels() {
		if (contentPanel != null) {
			contentPanel.clear();
		}
	}

	/**
	 * remove empty notification panel
	 */
	public void removeEmptyInfoPanel() {
		if (emptyListNotificationPanel != null) {
			emptyListNotificationPanel.removeFromParent();
		}
	}

	@Override
	public void setLabels() {
		headerView.setCaption(localize(title));
		for (int i = 0; i < materialCount(); i++) {
			Widget widget = materialPanel.getWidget(i);
			if (widget instanceof MaterialCard) {
				((MaterialCard) widget).setLabels();
			}
		}
		if (signInTextButton != null) {
			signInTextButton.setText(loc.getMenu("SignIn"));
		}
		if (profilePanel != null) {
			profilePanel.setLabels();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		onResize();
	}

	@Override
	public void onResize() {
		super.onResize();
		resizeHeader();
	}

	/**
	 * update header style on resize
	 */
	public void resizeHeader() {
		boolean smallScreen = app.getAppletFrame()
				.hasSmallWindowOrCompactHeader();
		headerView.resizeTo(smallScreen);
		if (searchBar != null) {
			Dom.toggleClass(searchBar, "compact", smallScreen);
		}
		updateSignInButtonsVisibility(smallScreen);
		Dom.toggleClass(contentPanel, "compact", smallScreen);
	}

	public void clearMaterials() {
		materialPanel.clear();
	}

	void showEmptyListNotification(InfoErrorData data) {
		if (materialPanel != null) {
			materialPanel.removeFromParent();
		}
		if (emptyListNotificationPanel != null) {
			emptyListNotificationPanel.removeFromParent();
		}
		emptyListNotificationPanel = getEmptyListNotificationPanel(data);
		contentPanel.add(emptyListNotificationPanel);
	}

	private FlowPanel getEmptyListNotificationPanel(InfoErrorData data) {
		return new ComponentInfoErrorPanel(loc, data, null);
	}

	String localize(String key) {
		return loc.getMenu(key);
	}

	/**
	 *
	 * @param card to add.
	 */
	public void addMaterialCard(Widget card) {
		materialPanel.add(card);
	}

	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	public void clearContents() {
		contentPanel.clear();
	}

	public boolean hasNoMaterials() {
		return materialCount() == 0;
	}

	public int materialCount() {
		return materialPanel.getWidgetCount();
	}

	public Widget materialAt(int index) {
		return materialPanel.getWidget(index);
	}

	public void addMaterialOrLoadMoreFilesPanel(Widget widget) {
		materialPanel.add(widget);
	}

	public void insertMaterial(Widget widget, int idx) {
		materialPanel.insert(widget, idx);
	}

	public HeaderView getHeader() {
		return headerView;
	}

	/**
	 * hide sign in button, show profile avatar
	 * @param event - login event
	 */
	public void onLogin(LoginEvent event) {
		signInTextButton.setVisible(false);
		signInIconButton.setVisible(false);
		profilePanel.setVisible(true);
		profilePanel.update(event.getUser());
		headerView.add(profilePanel);
	}

	/**
	 * hide avatar, show sign in button
	 */
	public void onLogout() {
		profilePanel.setVisible(false);
		boolean isSmallScreen = app.getAppletFrame()
				.hasSmallWindowOrCompactHeader();
		updateSignInButtonsVisibility(isSmallScreen);
	}

	private void initSpinner() {
		spinner = new LoadSpinner();
		addToContent(spinner);
	}

	/**
	 * show loading wheel
	 */
	public void showSpinner() {
		spinner.show();
	}

	/**
	 * hide loading wheel
	 */
	public void hideSpinner() {
		spinner.hide();
	}
}
