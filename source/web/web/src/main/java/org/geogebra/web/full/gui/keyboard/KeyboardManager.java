package org.geogebra.web.full.gui.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.gui.AlgebraMathFieldProcessing;
import org.geogebra.web.full.gui.dialog.text.GeoTextEditor;
import org.geogebra.web.full.gui.dialog.text.TextEditPanelProcessing;
import org.geogebra.web.full.gui.openfileview.HeaderFileView;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.util.keyboard.AutocompleteProcessing;
import org.geogebra.web.full.util.keyboard.ScriptAreaProcessing;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;

/**
 * Handles creating, showing and updating the keyboard
 */
public final class KeyboardManager
		implements RequiresResize, KeyboardManagerInterface {

	private final AppW app;
	private VirtualKeyboardGUI keyboard;

	private String originalBodyPadding;
	private final Style bodyStyle;
	private KeyboardListener processing;
	private final KeyboardDetachController detachController;

	/**
	 * Constructor
	 *
	 * @param appWFull the application
	 */
	public KeyboardManager(AppW appWFull) {
		this.app = appWFull;
		this.bodyStyle = RootPanel.getBodyElement().getStyle();
		detachController = new KeyboardDetachController(app.getAppletId(),
				app.getAppletParameters().getDetachKeyboardParent(),
				app.getGeoGebraElement().getParentElement(),
				shouldDetach());
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
	 * OpenFileView width if open, app width (dockpanel width) otherwise
	 * @return the preferred keyboard width
	 */
	public double getKeyboardWidth() {
		double appWidth = app.getWidth();
		if (app.getGuiManager().isOpenFileViewLoaded()) {
			HeaderFileView headerFileView = (HeaderFileView) app.getGuiManager().getBrowseView();
			if (headerFileView != null && headerFileView.getPanel().getOffsetWidth() > 0) {
				appWidth = headerFileView.getPanel().getOffsetWidth();
			}
		}

		return detachController.isEnabled()
				? detachController.getParentWidth()
				: appWidth;
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
		return TabbedKeyboard.TOTAL_HEIGHT;
	}

	/**
	 * @param appFrame
	 *            frame of the applet
	 */
	public void addKeyboard(Panel appFrame) {
		ensureKeyboardsExist();
		if (detachController.isEnabled()) {
			detachController.addAsDetached(keyboard);
			app.addWindowResizeListener(this);
		} else {
			appFrame.add(keyboard);

		}
		updateStyle();
	}

	/**
	 *
	 * @return true if the keyboard is not attached to the frame.
	 */
	public boolean isKeyboardOutsideFrame() {
		return detachController.isEnabled();
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
			KeyboardCloseListener listener) {
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
			if (processing != null) {
				keyboard.setProcessing(processing);
			}
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
		if (detachController.removeKeyboardRootFromDom()) {
			keyboard = null;
		}
	}

	@Override
	public void setOnScreenKeyboardTextField(MathKeyboardListener textField) {
		processing = makeKeyboardListener(textField, app.getLastItemProvider());
		if (keyboard != null) {
			if (textField != null) {
				addExtraSpaceForKeyboard();
			} else {
				removeExtraSpaceForKeyboard();
			}
			keyboard.setProcessing(processing);
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
		if (shouldDetach() && !detachController.hasCustomParent()) {
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

	/**
	 * Create keyboard adapter for text editing object.
	 * Implemented here so that the components from web-common can have
	 * processing implementation in web.
	 */
	private static KeyboardListener makeKeyboardListener(
			MathKeyboardListener textField, HasLastItem lastItemProvider) {
		if (textField instanceof RetexKeyboardListener) {
			return new MathFieldProcessing(
					((RetexKeyboardListener) textField).getMathField());
		}
		if (textField instanceof RadioTreeItem) {
			return new AlgebraMathFieldProcessing(
					(RadioTreeItem) textField,
					lastItemProvider);
		}
		if (textField instanceof KeyboardListener) {
			return (KeyboardListener) textField;
		}
		if (textField instanceof GeoTextEditor) {
			return new TextEditPanelProcessing((GeoTextEditor) textField);
		}
		if (textField instanceof AutoCompleteTextFieldW) {
			return new AutocompleteProcessing(
					(AutoCompleteTextFieldW) textField);
		}

		if (textField instanceof ScriptArea) {
			return new ScriptAreaProcessing((ScriptArea) textField);
		}

		return null;
	}
}
