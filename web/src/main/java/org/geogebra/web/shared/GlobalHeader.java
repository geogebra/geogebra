package org.geogebra.web.shared;

import org.geogebra.common.main.undo.UndoRedoButtonsController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.Visibility;
import org.geogebra.web.shared.view.button.ActionButton;
import org.geogebra.web.shared.view.button.DisappearingActionButton;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Singleton representing external header bar of unbundled apps.
 */
public class GlobalHeader implements EventRenderable {
	/**
	 * Singleton instance.
	 */
	public static final GlobalHeader INSTANCE = new GlobalHeader();

	private ProfilePanel profilePanel;
	private RootPanel signIn;
	private AppW app;
	private Label timer;
	private StandardButton examInfoBtn;
	private GeoGebraFrameW frame;

	private String oldHref;

	private boolean shareButtonInitialized;

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

	/**
	 * Add app picker button in external header for suite
	 *
	 * @param appW
	 *            application
	 * @return app picker button
	 */
	public SuiteHeaderAppPicker addSuiteAppPicker(final AppW appW) {
		this.app = appW;
		RootPanel appPickerPanel = RootPanel.get("suiteAppPicker");
		if (appPickerPanel != null) {
			SuiteHeaderAppPicker suiteHeaderAppPicker = new SuiteHeaderAppPicker(app);
			appPickerPanel.add(suiteHeaderAppPicker);
			suiteHeaderAppPicker.checkButtonVisibility();
			return suiteHeaderAppPicker;
		}
		return null;
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			if (profilePanel == null) {
				profilePanel = new ProfilePanel(app);
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
	 * Initialize the settings button if it's on the header
	 */
	public void initSettingButtonIfOnHeader() {
		ActionButton settingsButton = getActionButton("settingsButton");
		if (settingsButton != null) {
			setTitle(settingsButton, "Settings");
			settingsButton.setAction(new Runnable() {
				@Override
				public void run() {
					app.getGuiManager().showSciSettingsView();
				}
			});
		}
	}

	private void setTitle(ActionButton settingsButton, String string) {
		settingsButton.setTitle(string);
		app.getLocalization().registerLocalizedUI(settingsButton);
	}

	/**
	 * Initialize the undo and redo buttons if these are on the header
	 */
	public void initUndoRedoButtonsIfOnHeader() {
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
		forceVisible(Visibility.HIDDEN);
		getExamPanel().getElement().removeFromParent();
		getButtonElement().getStyle().setDisplay(Display.FLEX);
		getHomeLink().setHref(oldHref);
	}

	private void forceVisible(Visibility visible) {
		app.getAppletParameters().setAttribute("marginTop",
				visible == Visibility.VISIBLE ? "64" : "0");
		// takes care of both header visibility and menu button placement
		frame.forceHeaderVisibility(visible);
	}

	/**
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		forceVisible(Visibility.VISIBLE);
		// remove other buttons
		getButtonElement().getStyle()
				.setDisplay(Display.NONE);
		// exam panel with timer and info btn
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		// add exam panel to
		DivElement exam = DOM.createDiv().cast();
		exam.setId("examId");
		getButtonElement().getParentElement().appendChild(exam);
		oldHref = getHomeLink().getHref();
		getHomeLink().setHref("#");
		RootPanel.get("examId").addStyleName("examPanel");
		RootPanel.get("examId").add(timer);
		RootPanel.get("examId").add(examInfoBtn);
		// run timer
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (getApp().getExam() != null) {
					if (getApp().getExam().isCheating()) {
						getApp().getGuiManager()
								.setUnbundledHeaderStyle("examCheat");
					}
					getTimer().setText(
							getApp().getExam().getElapsedTimeLocalized());
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
	}

	private static AnchorElement getHomeLink() {
		return RootPanel.get("headerID").getElement().getElementsByTagName("a")
				.getItem(0).cast();
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

	public void setFrame(GeoGebraFrameW frame) {
		this.frame = frame;
	}

	/**
	 * @return whether there is a header in DOM
	 */
	public static boolean isInDOM() {
		return RootPanel.get("headerID") != null;
	}

}
