package org.geogebra.web.shared;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.undo.UndoRedoButtonsController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.gwtutil.SafeExamBrowser;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.view.button.ActionButton;
import org.geogebra.web.shared.view.button.DisappearingActionButton;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.animation.client.AnimationScheduler.AnimationCallback;
import org.gwtproject.dom.client.DivElement;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.core.Function;

/**
 * Singleton representing external header bar of unbundled apps.
 */
public class GlobalHeader implements EventRenderable {
	/**
	 * Singleton instance.
	 */
	public static final GlobalHeader INSTANCE = new GlobalHeader();

	private ProfileAvatar profilePanel;
	private RootPanel signIn;
	private MenuToggleButton menuBtn;
	private AppW app;
	private Label timer;
	private StandardButton examInfoBtn;

	private boolean shareButtonInitialized;
	private @CheckForNull FlowPanel examTypeHolder;

	/**
	 * Activate sign in button in external header
	 *
	 * @param appW
	 *            application
	 */
	public void addSignIn(final AppW appW) {
		this.app = appW;
		signIn = RootPanel.get("signInButton");
		if (signIn == null) {
			return;
		}
		Dom.addEventListener(signIn.getElement(), "click", (e) -> {
			appW.getSignInController().login();
			e.stopPropagation();
			e.preventDefault();
		});
		app.getLoginOperation().getView().add(this);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			if (profilePanel == null) {
				profilePanel = new ProfileAvatar(app);
			}
			signIn.setVisible(false);
			profilePanel.setVisible(true);
			profilePanel.update(((LoginEvent) event).getUser());
			DivElement profile = DOM.createDiv().cast();
			profile.setId("profileId");
			signIn.getElement().getParentElement().appendChild(profile);

			RootPanel.get("profileId").add(profilePanel);
		}
		if (event instanceof LogOutEvent) {
			profilePanel.setVisible(false);
			signIn.setVisible(true);
		}
	}

	/**
	 * @param callback
	 *            click callback
	 */
	public void initShareButton(final AsyncOperation<Widget> callback) {
		final RootPanel rp = getShareButton();
		if (rp != null && !shareButtonInitialized) {
			shareButtonInitialized = true;
			Dom.addEventListener(rp.getElement(), "click", (e) -> {
				callback.callback(rp);
				e.stopPropagation();
				e.preventDefault();
			});
		}
	}

	private static RootPanel getShareButton() {
		return RootPanel.get("shareButton");
	}

	/**
	 * Get element, NOT panel to make sure root panels are not nested
	 *
	 * @return element containing the buttons in header
	 */
	public static Element getButtonElement() {
		return Document.get().getElementById("buttonsID");
	}

	/**
	 * @return panel of exam timer
	 */
	public RootPanel getExamPanel() {
		return RootPanel.get("examId");
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return exam timer
	 */
	public Label getTimer() {
		return timer;
	}

	/**
	 * @return exam info button
	 */
	public StandardButton getExamInfoBtn() {
		return examInfoBtn;
	}

	/**
	 * Initialize the settings, undo and redo buttons if they are on the header
	 */
	public void initButtonsIfOnHeader() {
		if (app != null) {
			initSettingButtonIfOnHeader();
			initUndoRedoButtonsIfOnHeader();
		}
	}

	private void initSettingButtonIfOnHeader() {
		ActionButton settingsButton = getActionButton("settingsButton");
		if (settingsButton != null) {
			setTitle(settingsButton, "Settings");
			settingsButton.setAction(() -> app.getGuiManager().showSciSettingsView());
		}
	}

	private void setTitle(ActionButton settingsButton, String string) {
		settingsButton.setTitle(string);
		app.getLocalization().registerLocalizedUI(settingsButton);
	}

	private void initUndoRedoButtonsIfOnHeader() {
		ActionButton undoButton = getUndoButton();
		ActionButton redoButton = getRedoButton();
		if (undoButton != null && redoButton != null) {
			UndoRedoButtonsController.addUndoRedoFunctionality(undoButton,
					redoButton, app.getKernel());
		}
	}

	private ActionButton getUndoButton() {
		ActionButton undoButton = getActionButton("undoButton");
		if (undoButton != null) {
			setTitle(undoButton, "Undo");
		}
		return undoButton;
	}

	private ActionButton getRedoButton() {
		ActionButton undoButton = getDisappearingActionButton("redoButton");
		if (undoButton != null) {
			setTitle(undoButton, "Redo");
		}
		return undoButton;
	}

	private ActionButton getActionButton(String viewId) {
		RootPanel view = getViewById(viewId);
		if (view != null) {
			return new ActionButton(app, view);
		}
		return null;
	}

	private DisappearingActionButton getDisappearingActionButton(String viewId) {
		RootPanel view = getViewById(viewId);
		if (view != null) {
			return new DisappearingActionButton(app, view);
		}
		return null;
	}

	private static RootPanel getViewById(String viewId) {
		return RootPanel.get(viewId);
	}

	/**
	 * remove exam timer and put back button panel
	 */
	public void resetAfterExam() {
		if (getButtonElement() == null) {
			return;
		}
		getExamPanel().getElement().removeFromParent();
		getButtonElement().getStyle().clearDisplay();
		onResize();
	}

	/**
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		if (getButtonElement() == null) {
			return;
		}
		// remove other buttons
		getButtonElement().getStyle().setDisplay(Display.NONE);

		// exam panel with timer and info btn
		Image timerImg = new Image(MaterialDesignResources.INSTANCE.timer()
				.getSafeUri().asString());
		timerImg.addStyleName("timerImg");
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		// add exam panel to
		Element exam = DOM.createDiv();
		exam.setId("examId");
		getButtonElement().getParentElement().appendChild(exam);
		// The link should be disabled in all exam-capable apps since APPS-3289, but make sure
		Dom.querySelector("#headerID a").setAttribute("href", "#");
		RootPanel examId = RootPanel.get("examId");
		examId.addStyleName("examPanel");

		if (SafeExamBrowser.get() != null && SafeExamBrowser.get().security != null) {
			SafeExamBrowser.SebSecurity security = SafeExamBrowser.get().security;
			String hash = security.configKey.substring(0, 8);
			security.updateKeys((ignore) ->
					addExamType("Safe Exam Browser (" + hash + ")"));
		} else if (!app.getExam().isRestrictedGraphExam()) {
			addExamType(app.getExam().getCalculatorNameForHeader());
		}

		examId.add(timerImg);
		examId.add(timer);
		examId.add(examInfoBtn);
		// run timer
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (getApp().getExam() != null) {
					if (getApp().getExam().isCheating()) {
						getApp().getGuiManager()
								.setUnbundledHeaderStyle("examCheat");
						if (examTypeHolder != null) {
							examTypeHolder.addStyleName("cheat");
						}
					}
					getTimer().setText(
							getApp().getExam().getElapsedTimeLocalized());
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
		onResize();
	}

	private void addExamType(String examTypeName) {
		HTML examImg = new HTML(DefaultMenuIconProvider.INSTANCE.assignment().getSVG());
		examImg.setStyleName("examTypeIcon");
		Label examType = new Label(examTypeName);
		examType.setStyleName("examType");
		FlowPanel examTypePanel = new FlowPanel();
		examTypePanel.getElement().setId("examTypeId");
		examTypePanel.addStyleName("examTypePanel");
		if (app != null && app.isLockedExam()) {
			examTypePanel.addStyleName("locked");
		}
		examTypePanel.add(examImg);
		examTypePanel.add(examType);
		this.examTypeHolder = examTypePanel;
		RootPanel.get("examId").add(examTypePanel);
	}

	/**
	 * Show/hide apps picker as needed
	 */
	public static void onResize() {
		Function resize = GeoGebraGlobal.getGgbHeaderResize();
		if (resize != null) {
			resize.call();
		}
	}

	/**
	 * Update style of share button, NPE safe.
	 *
	 * @param selected
	 *            whether to mark share button as selected
	 */
	public void selectShareButton(boolean selected) {
		final RootPanel rp = getShareButton();
		if (rp != null) {
			Dom.toggleClass(rp, "selected", selected);
		}
	}

	/**
	 * Initialize without creating any buttons.
	 *
	 * @param app
	 *            application
	 */
	public void setApp(AppW app) {
		this.app = app;
	}

	/**
	 * @return whether there is a header in DOM
	 */
	public static boolean isInDOM() {
		return RootPanel.get("headerID") != null;
	}

	/**
	 * update ui on language change
	 */
	public void setLabels() {
		if (profilePanel != null) {
			profilePanel.setLabels();
		}
		if (menuBtn != null) {
			menuBtn.setLabel();
		}
	}

	public void setMenuBtn(MenuToggleButton menuBtn) {
		this.menuBtn = menuBtn;
	}
}
