package org.geogebra.web.full.gui.view.algebra;

import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.inputfield.MathFieldInputSuggestions;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * @author Laszlo
 *
 */
public class LatexTreeItemController extends RadioTreeItemController
		implements MathFieldListener, BlurHandler {

	private MathFieldInputSuggestions sug;
	private RetexKeyboardListener retexListener;
	private EvaluateInput evalInput;

	/**
	 * @param item
	 *            AV item
	 */
	public LatexTreeItemController(RadioTreeItem item) {
		super(item);
		evalInput = new EvaluateInput(item, this);
		evalInput.setUsingValidInput(app.getActivity().useValidInput());
	}

	@Override
	protected void startEdit(boolean ctrl) {
		if (item.isInputTreeItem() && item.onEditStart()) {
			setOnScreenKeyboardTextField();
		} else {
			super.startEdit(ctrl);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		Scheduler.get().scheduleDeferred(() -> {
			item.resetInputBarOnBlur();
			if (preventBlur || isSuggesting()) {
				return;
			}

			onEnter(false, false);
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
	public void onEnter(final boolean keepFocus, boolean createSliders) {
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
		if (item.geo == null) {
			if (StringUtil.empty(item.getText())) {
				return;
			}
			item.getAV().setLaTeXLoaded();
			createGeoFromInput(keepFocus, createSliders);
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
			sug.needsEnterForSuggestion();
			return;
		}
		// make sure editing flag is up to date e.g. after failed redefine
		setEditing(true);
		onEnter(true, false);
		item.getAV().clearActiveItem();
		dispatchKeyTypeEvent("\n");
	}

	@Override
	public void onKeyTyped(String key) {
		if (app.getSelectionManager().getSelectedGeos().size() > 0) {
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
			event.setJsonArgument(jsonArgument);
		}
		app.dispatchEvent(event);
	}

	@Override
	public void onCursorMove() {
		item.onCursorMove();
	}

	@Override
	public void onUpKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyUp();
		}
	}

	@Override
	public void onDownKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyDown();
		}
	}

	@Override
	public void onInsertString() {
		// nothing to do
	}

	/**
	 * @return whether suggestions are open
	 */
	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	/**
	 * @param keepFocus
	 *            whether the focus should stay afterwards
	 * @param withSliders
	 *            whether to create sliders
	 */
	public void createGeoFromInput(final boolean keepFocus,
			boolean withSliders) {
		evalInput.createGeoFromInput(keepFocus, withSliders);
	}

	/**
	 * Just evaulate input.
	 * @return the evaulated geo.
	 */
	public GeoElementND evaluateToGeo() {
		return evalInput.evaluateToGeo();
	}
	
	/**
	 * @param afterCb 
	 * 				additional callback that runs after creation.
	 */
	public void createGeoFromInput(final AsyncOperation<GeoElementND[]> afterCb) {
		evalInput.createGeoFromInput(afterCb);
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener, null)
				.insertString(text);
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

	@Override
	public void showKeyboard() {
		app.showKeyboard(item);
	}

	/**
	 * @param show
	 *            whether to show keyboard
	 */
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(item.canvas, getMathField());
		retexListener.setAcceptsCommandInserts(true);
		if (show) {
			app.getAppletFrame().showKeyBoard(true, item, false);
		}
	}

	private MathFieldW getMathField() {
		return item.getMathField();
	}

	/**
	 * @return input suggestion model (lazy load)
	 */
	MathFieldInputSuggestions getInputSuggestions() {
		if (sug == null) {
			boolean forCas = getApp().getConfig().getVersion() == GeoGebraConstants.Version.CAS;
			sug = new MathFieldInputSuggestions(app, item, forCas);
		}
		return sug;
	}

	@Override
	public boolean onEscape() {
		if (item.geo != null || StringUtil.empty(item.getText())) {
			onBlur(null);
			app.getAccessibilityManager().focusGeo(item.geo);
			return true;
		}
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		onEnter(false, false);
		if (item.isInputTreeItem()) {
			item.addDummyLabel();
			item.setItemWidth(item.getAV().getFullWidth());
		}
		app.hideKeyboard();
		if (shiftDown) {
			app.getAccessibilityManager()
					.focusPrevious();
		} else {
			app.getAccessibilityManager()
					.focusNext();
		}
	}
}