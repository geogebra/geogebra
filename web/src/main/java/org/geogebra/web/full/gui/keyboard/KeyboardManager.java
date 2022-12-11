package org.geogebra.web.full.gui.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;

/**
 * Handles creating, showing and updating the keyboard
 */
public class KeyboardManager
		implements RequiresResize, KeyboardManagerInterface {

	private static final int SWITCHER_HEIGHT = 42;
	private AppW app;
	private RootPanel keyboardRoot;
	private VirtualKeyboardGUI keyboard;

	private String originalBodyPadding;
	private final Style bodyStyle;

	/**
	 * Constructor
	 *
	 * @param appWFull the application
	 */
	public KeyboardManager(AppW appWFull) {
		this.app = appWFull;
		this.bodyStyle = RootPanel.getBodyElement().getStyle();
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
		if (!"auto".equals(app.getAppletParameters().getParamDetachKeyboard())) {
			return Boolean.parseBoolean(app.getAppletParameters().getParamDetachKeyboard());
		}
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
				? NavigatorUtil.getWindowWidth()
				: app.getWidth();
	}

	/**
	 * @return height inside of the geogebra window
	 */
	public int estimateKeyboardHeight() {
		ensureKeyboardsExist();
		int realHeight = keyboard.getOffsetHeight();
		if (realHeight > 0) {
			return realHeight;
		}
		return estimateHiddenKeyboardHeight();
	}

	@Override
	public int estimateHiddenKeyboardHeight() {
		int keyboardContentHeight = app.needsSmallKeyboard() ? TabbedKeyboard.SMALL_HEIGHT
				: TabbedKeyboard.BIG_HEIGHT;
		return keyboardContentHeight + SWITCHER_HEIGHT;
	}

	/**
	 * @param appFrame
	 *            frame of the applet
	 */
	public void addKeyboard(Panel appFrame) {
		ensureKeyboardsExist();
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
		String keyboardParentId = app.getAppletId() + "keyboard";
		detachedKeyboardParent.setId(keyboardParentId);
		app.addWindowResizeListener(this);
		return RootPanel.get(keyboardParentId);
	}

	private Element getAppletContainer() {
		Element scaler = app.getGeoGebraElement().getParentElement();
		Element container = scaler == null ? null : scaler.getParentElement();
		if (container == null) {
			return RootPanel.getBodyElement();
		}
		return container;
	}

	@Override
	public void onResize() {
		if (keyboard != null) {
			keyboard.onResize();
		}
	}

	/**
	 * Update keyboard processor and close listener.
	 *
	 * @param textField
	 *            textfield adapter
	 * @param listener
	 *            open/close listener
	 */
	public void setListeners(MathKeyboardListener textField,
			UpdateKeyBoardListener listener) {
		ensureKeyboardsExist();
		((OnscreenTabbedKeyboard) keyboard).clearAndUpdate();
		if (textField != null) {
			setOnScreenKeyboardTextField(textField);
		}
		keyboard.setListener(listener);
	}

	/**
	 * Lazy loading getter
	 * @return the keyboard
	 */
	@Nonnull
	public VirtualKeyboardGUI getOnScreenKeyboard() {
		ensureKeyboardsExist();
		return keyboard;
	}

	private void ensureKeyboardsExist() {
		if (keyboard == null) {
			boolean showMoreButton = app.getConfig().showKeyboardHelpButton()
					&& !shouldDetach();
			keyboard = new OnscreenTabbedKeyboard((HasKeyboard) app, showMoreButton);
		}
	}

	@Override
	public void updateKeyboardLanguage() {
		if (keyboard != null) {
			keyboard.checkLanguage();
		}
	}

	@Override
	public void clearAndUpdateKeyboard() {
		if (keyboard != null) {
			keyboard.clearAndUpdate();
		}
	}

	@Override
	public void removeFromDom() {
		if (keyboardRoot != null) {
			// both clear and remove to save memory
			keyboardRoot.removeFromParent();
			keyboardRoot.clear();
			keyboard = null;
		}
	}

	@Override
	public void setOnScreenKeyboardTextField(MathKeyboardListener textField) {
		if (keyboard != null) {
			if (textField != null) {
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
		if (extraSpaceNeededForKeyboard()) {
			originalBodyPadding = bodyStyle.getPaddingBottom();
			bodyStyle.setProperty("paddingBottom", estimateKeyboardHeight() + "px");
		}
	}

	private void removeExtraSpaceForKeyboard() {
		if (!Objects.equals(originalBodyPadding, bodyStyle.getPaddingBottom())) {
			bodyStyle.setProperty("paddingBottom", originalBodyPadding);
		}
	}

	private boolean extraSpaceNeededForKeyboard() {
		if (shouldDetach()) {
			double appletBottom = app.getFrameElement().getAbsoluteBottom();
			return NavigatorUtil.getWindowHeight() - appletBottom < estimateKeyboardHeight();
		}

		return false;
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

	@Override
	public boolean isKeyboardClosedByUser() {
		return this.keyboard != null && !this.keyboard.shouldBeShown();
	}

	@Override
	public void addKeyboardAutoHidePartner(GPopupPanel popup) {
		if (keyboard != null) {
			keyboard.addAutoHidePartner(popup);
		}
	}

	/**
	 * @param tab tab to be activated
	 */
	public void selectTab(KeyboardType tab) {
		if (keyboard != null) {
			keyboard.selectTab(tab);
		}
	}
}
