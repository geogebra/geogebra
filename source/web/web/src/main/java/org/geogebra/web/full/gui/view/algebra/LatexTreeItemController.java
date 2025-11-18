package org.geogebra.web.full.gui.view.algebra;

import java.util.HashMap;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.full.gui.inputfield.AutoCompletePopup;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;

/**
 * @author Laszlo
 *
 */
public class LatexTreeItemController extends RadioTreeItemController
		implements MathFieldListener, BlurHandler {

	private AutoCompletePopup autocomplete;
	private RetexKeyboardListener retexListener;
	private final EvaluateInput evalInput;
	private String lastInput = "";

	/**
	 * @param item
	 *            AV item
	 */
	public LatexTreeItemController(RadioTreeItem item) {
		super(item);
		evalInput = new EvaluateInput(item, this, item.getAV().getSelectionCallback());
		evalInput.setUsingValidInput(app.getActivity().useValidInput());
	}

	@Override
	protected void startEdit(boolean ctrl) {
		if (item.isInputTreeItem() && item.onEditStart()) {
			setOnScreenKeyboardTextField();
		} else {
			super.startEdit(ctrl);
		}
		storeInitialInput();
	}

	/**
	 * Stores the initial input that should be restored upon pressing Escape.
	 * Make sure to call this method upon edit start.
	 */
	void storeInitialInput() {
		lastInput = item.getText();
	}

	@Override
	public void onBlur(BlurEvent event) {
		Scheduler.get().scheduleDeferred(() -> {
			item.resetInputBarOnBlur();
			if (preventBlur || isSuggesting()) {
				return;
			}

			onEnter(false);
			if (item.isEmpty() && item.isInputTreeItem()) {
				item.addDummyLabel();
				item.setItemWidth(item.getAV().getFullWidth());
			}

			if (item.getAV().isNodeTableEmpty()) {
				// #5245#comment:8, cases B and C excluded
				item.updateGUIfocus(true);
			}
		});
	}

	@Override
	public void onEnter(final boolean keepFocus) {
		if (isEditing()) {
			dispatchEditEvent(EventType.EDITOR_STOP);
		}
		if (item.isInputTreeItem() && item.isEmpty()) {
			item.styleEditor();
			item.addDummyLabel();
			setEditing(false);
			return;
		}
		item.setShowInputHelpPanel(false);
		if (item.geo == null && isEditing()) {
			if (StringUtil.empty(item.getText())) {
				return;
			}
			item.getAV().setLaTeXLoaded();
			evalInput.createGeoFromInput(keepFocus);
			setEditing(false);
			return;
		}
		if (!isEditing()) {
			return;
		}

		item.stopEditing(item.getText(), obj -> {
			if (obj != null && !keepFocus) {
				app.setScrollToShow(true);
				obj.update();
			}
		}, keepFocus);
	}

	@Override
	public void onEnter() {
		if (isSuggesting()) {
			autocomplete.needsEnterForSuggestion();
			return;
		}
		// make sure editing flag is up to date e.g. after failed redefine
		setEditing(true);
		onEnter(true);
		item.getAV().clearActiveItem();
		dispatchKeyTypeEvent("\n");
	}

	@Override
	public void onKeyTyped(String key) {
		if (!app.getSelectionManager().getSelectedGeos().isEmpty()) {
			// to clear preview points
			app.getSelectionManager().clearSelectedGeos();
		}
		item.onKeyTyped();
		dispatchKeyTypeEvent(key);
	}

	private void dispatchKeyTypeEvent(String key) {
		Event event = new Event(EventType.EDITOR_KEY_TYPED);
		if (key != null) {
			HashMap<String, Object> jsonArgument = new HashMap<>();
			jsonArgument.put("key", key);
			jsonArgument.put("label", "");
			event.setJsonArgument(jsonArgument);
		}
		app.dispatchEvent(event);
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		if (isSuggesting()) {
			autocomplete.onArrowKeyPressed(keyCode);
			return true;
		}
		item.onCursorMove();
		return false;
	}

	/**
	 * @return whether suggestions are open
	 */
	public boolean isSuggesting() {
		return autocomplete != null && autocomplete.isSuggesting();
	}

	/**
	 * Just evaluate input.
	 * @return the evaluated geo.
	 */
	public GeoElementND evaluateToGeo() {
		return evalInput.evaluateToGeo();
	}
	
	/**
	 * @param afterCb additional callback that runs after creation.
	 */
	public void createGeoFromInput(final AsyncOperation<GeoElementND[]> afterCb) {
		evalInput.createGeoFromInput(afterCb);
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		KeyboardInputAdapter.onKeyboardInput(getMathField().getInternal(), text);
	}

	/**
	 * @return keyboard listener
	 */
	public RetexKeyboardListener getRetexListener() {
		return retexListener;
	}

	/**
	 * @param retexListener
	 *            keyboard listener
	 */
	public void setRetexListener(RetexKeyboardListener retexListener) {
		this.retexListener = retexListener;
	}

	/**
	 * Connect keyboard listener to keyboard
	 */
	public void setOnScreenKeyboardTextField() {
		app.getKeyboardManager()
				.setOnScreenKeyboardTextField(item);
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}

	/**
	 * @param show
	 *            whether to show keyboard
	 */
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(item.canvas, getMathField());
		retexListener.setAcceptsCommandInserts(true);
		if (show) {
			app.getAppletFrame().showKeyboard(true, item, false);
		}
	}

	private MathFieldW getMathField() {
		return item.getMathField();
	}

	/**
	 * @return autocomplete popup (lazy load)
	 */
	AutoCompletePopup getAutocompletePopup() {
		if (autocomplete == null) {
			autocomplete = new AutoCompletePopup(app, app.getAutocompleteProvider(), item);
		}
		return autocomplete;
	}

	@Override
	public boolean onEscape() {
		if (autocomplete != null && autocomplete.isSuggesting()) {
			autocomplete.hide();
		} else {
			item.setText(lastInput);
		}
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		onEnter(false);
		if (item.isInputTreeItem()) {
			item.setItemWidth(item.getAV().getFullWidth());
		}
		boolean handled;
		if (shiftDown) {
			handled = app.getAccessibilityManager().focusPrevious();
		} else {
			handled = app.getAccessibilityManager().focusNext();
		}
		app.hideKeyboard();
		return handled;
	}
}
