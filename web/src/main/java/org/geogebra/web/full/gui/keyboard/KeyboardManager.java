package org.geogebra.web.full.gui.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Handles creating, showing and updating the keyboard
 */
public class KeyboardManager
		implements ResizeHandler, KeyboardManagerInterface {

	private static final int SWITCHER_HEIGHT = 40;
	private AppW app;
	private RootPanel keyboardRoot;
	private VirtualKeyboardGUI keyboard;

	/**
	 * Constructor
	 *
	 * @param appWFull
	 * 			the application
	 */
	public KeyboardManager(AppW appWFull) {
		this.app = appWFull;
	}

	/**
	 *
	 * @return list of view ids which have keyboard.
	 */
	public List<Integer> getKeyboardViews() {
		ArrayList<Integer> keyboardViews = getKeyboardViewsNoEV();
		if (app.getKernel().getConstruction().hasInputBoxes()) {
			keyboardViews.add(App.VIEW_EUCLIDIAN);
			keyboardViews.add(App.VIEW_EUCLIDIAN2);
		}
		return keyboardViews;
	}

	private ArrayList<Integer> getKeyboardViewsNoEV() {
		ArrayList<Integer> keyboardViews = new ArrayList<>();
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView) {
			keyboardViews.add(App.VIEW_ALGEBRA);
		}
		keyboardViews.addAll(Arrays.asList(App.VIEW_CAS, App.VIEW_SPREADSHEET,
				App.VIEW_PROBABILITY_CALCULATOR));
		return keyboardViews;
	}

	/**
	 * Update keyboard style.
	 */
	private void updateStyle() {
		Dom.toggleClass(keyboard.asWidget(), "detached", shouldDetach());
	}

	/**
	 *
	 * @return keyboard is detachable, no view uses it
	 */
	public boolean shouldDetach() {
		for (Integer viewId : this.getKeyboardViewsNoEV()) {
			if (app.showView(viewId)) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @return the preferred keyboard width
	 */
	public double getKeyboarWidth() {
		return shouldDetach()
				? Window.getClientWidth()
				: app.getWidth();
	}

	/**
	 * @return height inside of the geogebra window
	 */
	public int estimateKeyboardHeight() {
		ensureKeyboardExists();
		int realHeight = keyboard.getOffsetHeight();
		if (realHeight > 0) {
			return realHeight;
		}
		int keyboardContentHeight = app.needsSmallKeyboard() ? TabbedKeyboard.SMALL_HEIGHT
				: TabbedKeyboard.BIG_HEIGHT;
		return keyboardContentHeight + SWITCHER_HEIGHT;
	}

	/**
	 * @param appFrame
	 *            frame of the applet
	 */
	public void addKeyboard(Panel appFrame) {
		ensureKeyboardExists();
		if (!shouldDetach()) {
			appFrame.add(keyboard);
		} else {
			if (keyboardRoot == null) {
				keyboardRoot = createKeyboardRoot();
			}
			keyboardRoot.add(keyboard);
		}
		updateStyle();
	}

	private RootPanel createKeyboardRoot() {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
		Element container = getAppletContainer();
		container.appendChild(detachedKeyboardParent);
		container.getStyle().setProperty("paddingBottom",
				estimateKeyboardHeight() + "px");
		String keyboardParentId = app.getAppletId() + "keyboard";
		detachedKeyboardParent.setId(keyboardParentId);
		Window.addResizeHandler(this);
		return RootPanel.get(keyboardParentId);
	}

	private Element getAppletContainer() {
		Element scaler = app.getArticleElement().getParentElement();
		Element container = scaler == null ? null : scaler.getParentElement();
		if (container == null) {
			return RootPanel.getBodyElement();
		}
		return container;
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (keyboard != null) {
			keyboard.onResize();
		}
	}

	/**
	 * Return a keyboard and connected to given textfield.
	 *
	 * @param textField
	 *            textfield adapter
	 * @param listener
	 *            open/close listener
	 * @return keyboard
	 */
	public VirtualKeyboardGUI getOnScreenKeyboard(
			MathKeyboardListener textField, UpdateKeyBoardListener listener) {
		ensureKeyboardExists();
		if (textField != null) {
			setOnScreenKeyboardTextField(textField);
		}

		keyboard.setListener(listener);
		return keyboard;
	}

	private void ensureKeyboardExists() {
		if (keyboard == null) {
			boolean showMoreButton = app.getConfig().showKeyboardHelpButton()
					&& !shouldDetach();
			keyboard = new OnscreenTabbedKeyboard((HasKeyboard) app,
					keyboardIsScientific(),
					showMoreButton);
		}
	}

	private boolean keyboardIsScientific() {
		ArticleElementInterface articleElement = app.getArticleElement();
		if ("evaluator".equals(articleElement.getDataParamAppName())) {
			return "scientific"
					.equals(articleElement.getParamKeyboardType("normal"));
		}
		return app.getConfig().hasScientificKeyboard();
	}

	@Override
	public boolean shouldKeyboardBeShown() {
		return keyboard != null && keyboard.shouldBeShown();
	}

	@Override
	public void updateKeyboardLanguage() {
		if (keyboard != null) {
			keyboard.checkLanguage();
		}
	}

	@Override
	public void setOnScreenKeyboardTextField(MathKeyboardListener textField) {
		if (keyboard != null) {
			if  (textField != null) {
				addExtraSpaceForKeyboard();
			} else {
				removeExtraSpaceForKeyboard();
			}

			keyboard.setProcessing(
					GuiManagerW.makeKeyboardListener(
							textField, app.getLastItemProvider()));
		}
	}

	private void addExtraSpaceForKeyboard() {
		if (noExtraSpaceNeededForKeyboard()) {
			return;
		}

		clearAppletContainerTransition();
		setAppletContainerPaddingBottom(estimateKeyboardHeight() + "px");
	}

	private boolean noExtraSpaceNeededForKeyboard() {
		return !shouldDetach() || getAppletContainer() == null;
	}

	private void clearAppletContainerTransition() {
		getAppletContainer().getStyle().clearProperty("transition");
	}

	private void removeExtraSpaceForKeyboard() {
		if (noExtraSpaceNeededForKeyboard()) {
			return;
		}

		getAppletContainer().getStyle().setProperty("transition", "padding-bottom 0.2s linear");
		setAppletContainerPaddingBottom("0");
	}

	private void setAppletContainerPaddingBottom(String value) {
		getAppletContainer().getStyle().setProperty("paddingBottom", value);
	}

	/**
	 * Notify keyboard about finished editing
	 */
	public void onScreenEditingEnded() {
		if (keyboard != null) {
			removeExtraSpaceForKeyboard();
		}
	}

	/**
	 * Update keyboard size.
	 */
	public void resizeKeyboard() {
		if (keyboard != null) {
			keyboard.onResize();
			keyboard.setStyleName();
		}
	}

	/**
	 * @return whether keyboard was closed by clicking the X button
	 */
	public boolean isKeyboardClosedByUser() {
		return this.keyboard != null && !this.keyboard.shouldBeShown();
	}

	@Override
	public void addKeyboardAutoHidePartner(GPopupPanel popup) {
		if (keyboard != null) {
			keyboard.addAutoHidePartner(popup);
		}
	}

}
