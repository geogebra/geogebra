package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

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
		ClickStartHandler.init(signIn, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				((SignInButton) appW.getLAF().getSignInButton(appW)).login();
			}
		});
		app.getLoginOperation().getView().add(this);
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
			ClickStartHandler.init(rp, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					callback.callback(rp);
				}
			});
		}
	}

	private static RootPanel getShareButton() {
		return RootPanel.get("shareButton");
	}

	private static RootPanel getSettingsButton() {
		return RootPanel.get("settingsButton");
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
		setTitleIfOnHeaderFor("settingsButton", "Settings");
		final RootPanel rp = getSettingsButton();
		if (rp != null ) {
			ClickStartHandler.init(rp, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					if (getButtonElement().getParentElement() != null) {
						getButtonElement().getParentElement().getStyle()
							.setDisplay(Display.NONE);
					}
					((AppWFull) getApp()).getAppletFrame().showBrowser(
							getApp().getGuiManager().getSciSettingsView());
				}
			});
		}
	}


	/**
	 * Initialize the undo and redo buttons if these are on the header
	 */
	public void initUndoRedoButtonsIfOnHeader() {
		initUndoButtonIfOnHeader();
		initRedoButtonIfOnHeader();
	}

	private void initUndoButtonIfOnHeader() {
		setTitleIfOnHeaderFor("undoButton", "Undo");
	}

	private void initRedoButtonIfOnHeader() {
		setTitleIfOnHeaderFor("redoButton", "Redo");
	}

	private void setTitleIfOnHeaderFor(String viewId, String titleLocalizationKey) {
		RootPanel viewElement = getViewById(viewId);
		if (viewElement != null) {
			setTitleFor(viewElement, titleLocalizationKey);
		}
	}

	private RootPanel getViewById(String viewId) {
		return RootPanel.get(viewId);
	}

	private void setTitleFor(RootPanel viewElement, String titleLocalizationKey) {
		AriaHelper.setTitle(viewElement, app.getLocalization().getMenu(titleLocalizationKey), app);
	}

	/**
	 * remove exam timer and put back button panel
	 */
	public void resetAfterExam() {
		forceVisible(false);
		getExamPanel().getElement().removeFromParent();
		getButtonElement().getStyle()
				.setDisplay(Display.FLEX);
		getHomeLink().setHref(oldHref);
	}

	private void forceVisible(boolean visible) {
		app.getArticleElement().attr("marginTop", visible ? "+64" : "0");
		// takes care of both header visibility and menu button placement
		app.fitSizeToScreen();
	}

	/**
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		forceVisible(true);
		// remove other buttons
		getButtonElement().getStyle()
				.setDisplay(Display.NONE);
		// exam panel with timer and info btn
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24, app);
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
					String os = Browser.getMobileOperatingSystem();
					getApp().getExam().checkCheating(os);
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

}
