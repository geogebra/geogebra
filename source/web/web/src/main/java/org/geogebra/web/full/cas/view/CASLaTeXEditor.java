package org.geogebra.web.full.cas.view;

import java.util.Objects;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.popup.autocompletion.InputSuggestions;
import org.geogebra.common.main.App;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.gui.inputfield.AutoCompletePopup;
import org.geogebra.web.full.gui.util.SyntaxAdapterImplWithPaste;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.inputfield.AbstractSuggestionDisplay;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * ReTeX editor for CAS
 *
 */
public class CASLaTeXEditor extends FlowPanel implements CASEditorW,
		MathKeyboardListener, MathFieldListener, BlurHandler {
	/** suggestions */
	AutoCompletePopup sug;
	private final InputSuggestions inputSuggestions;
	private final MathFieldW mf;
	/** keyboard connector */
	RetexKeyboardListener retexListener;
	private AppWFull app;
	private CASTableControllerW controller;
	private boolean autocomplete = true;
	private Widget dummy;
	private Canvas canvas;
	private boolean editAsText;

	/**
	 * @param app
	 *            application
	 * @param controller
	 *            controller
	 */
	public CASLaTeXEditor(final AppW app,
			final CASTableControllerW controller) {
		this.app = (AppWFull) app;
		this.controller = controller;
		inputSuggestions = new InputSuggestions(null);
		canvas = Canvas.createIfSupported();
		mf = new MathFieldW(new SyntaxAdapterImplWithPaste(app.getKernel()), this,
				canvas, this);
		retexListener = new RetexKeyboardListener(canvas, mf);
		mf.setOnBlur(this);
		add(mf);
		dummy = new Label(
				app.getLocalization().getMenu("InputLabel") + Unicode.ELLIPSIS);
		dummy.addStyleName("CAS_dummyLabel");
		this.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		updateWidth();
	}

	private void updateWidth() {
		int width = app.getGuiManager().getLayout().getDockManager()
				.getPanel(App.VIEW_CAS).getOffsetWidth() - 35;
		if (width > 0) {
			this.getElement().getStyle().setWidth(width, Unit.PX);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		// autocommitting empty text produces $1
		if (!isSuggesting()) {
			onEnter(false);
			controller.stopEditing();
		}
	}

	@Override
	public int getInputSelectionEnd() {
		return 0;
	}

	@Override
	public int getInputSelectionStart() {
		return 0;
	}

	@Override
	public String getInputSelectedText() {
		return null;
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		return mf.getText();
	}

	@Override
	public String getLaTeX() {
		if (mf == null) {
			return "";
		}
		TeXSerializer s = new TeXSerializer();
		return s.serialize(mf.getFormula());
	}

	@Override
	public String getInput() {
		return getText();
	}

	@Override
	public void setInputSelectionStart(int selStart) {
		// not needed
	}

	@Override
	public void setInputSelectionEnd(int selEnd) {
		// not needed
	}

	@Override
	public void clearInputText() {
		setText("");
	}

	@Override
	public void setInput(String string) {
		if (getWidget(0) != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		setText(string);
	}

	private void setWidget(Widget asWidget) {
		insert(asWidget, 0);
	}

	@Override
	public void setText(String text0) {
		if (mf != null) {
			if (editAsText) {
				mf.getInternal().setPlainText(text0);
			} else {
				mf.parse(text0);
			}
		}
	}

	@Override
	public void setLabels() {
		// not needed
	}

	@Override
	public void setFocus(boolean focus) {
		remove(focus ? dummy : mf);
		if (focus) {
			updateWidth();
		}
		setWidget(focus ? mf.asWidget()
				: dummy);
		mf.setFocus(focus);
	}

	@Override
	public void onEnter(boolean keepFocus) {
		if (sug != null && sug.needsEnterForSuggestion()) {
			return;
		}
		// got here by blur: do not use previous cell ref
		if (!keepFocus && (StringUtil.empty(getText())
				|| Objects.equals(controller.getTextBeforeEdit(), getText()))) {
			this.setFocus(false);
			return;
		}
		this.controller.handleEnterKey(false, false, app, keepFocus);
	}

	@Override
	public void resetInput() {
		// not needed
	}

	@Override
	public void setAutocomplete(boolean b) {
		this.autocomplete = b;
	}

	@Override
	public void ensureEditing() {
		final GuiManagerInterfaceW gui = app.getGuiManager();
		app.showKeyboard(retexListener, true);
		app.getKeyboardManager().setOnScreenKeyboardTextField(retexListener);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(this, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				doClickStart();
				gui.setActivePanelAndToolbar(App.VIEW_CAS);
			}
		});
		setFocus(true);
	}

	/**
	 * Click start callback
	 */
	protected void doClickStart() {
		setFocus(true);
		app.showKeyboard(retexListener);
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}

	@Override
	public boolean getAutoComplete() {
		return autocomplete;
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(
				app.getParserFunctions().toEditorAutocomplete(text, app.getLocalization()));
	}

	@Override
	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	@Override
	public void requestFocus() {
		if (getWidget(0) != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		mf.requestViewFocus();
	}

	@Override
	public Widget toWidget() {
		return this;
	}

	@Override
	public void autocomplete(String text) {
		KeyboardInputAdapter.onKeyboardInput(mf.getInternal(), text);
	}

	@Override
	public void updatePosition(AbstractSuggestionDisplay sugPanel) {
		sugPanel.setPositionRelativeTo(this);
	}

	@Override
	public String getCommand() {
		return inputSuggestions.getCommand(mf);
	}

	private AutoCompletePopup getInputSuggestions() {
		if (sug == null) {
			sug = new AutoCompletePopup(app, new AutocompleteProvider(app, true), this);
		}
		return sug;
	}

	@Override
	public void onEnter() {
		// TODO or onEnter(false) ?
		onEnter(true);
	}

	@Override
	public void onKeyTyped(String key) {
		double scaleX = app.getGeoGebraElement().getScaleX();
		int left = (int) ((getAbsoluteLeft() - (int) app.getAbsLeft()) / scaleX);
		int top = getAbsoluteTop() - (int) app.getAbsTop();
		getInputSuggestions().popupSuggestions(left,
				top - 3, // extra padding of editor
				getOffsetHeight() + 5); // extra padding of editor
		mf.scrollParentHorizontally(this);
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		if (isSuggesting()) {
			sug.onArrowKeyPressed(keyCode);
			return true;
		}
		mf.scrollParentHorizontally(this);
		return false;
	}

	@Override
	public boolean onEscape() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFocus() {
		return mf.hasFocus();
	}

	@Override
	public boolean acceptsCommandInserts() {
		return false;
	}

	@Override
	public AppW getApplication() {
		return app;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		// not needed
		return true;
	}

	@Override
	public void insertInput(String input) {
		mf.insertString(input);
	}

	@Override
	public void adjustCaret(HumanInputEvent<?> event) {
		mf.adjustCaret(EventUtil.getTouchOrClickClientX(event),
				EventUtil.getTouchOrClickClientY(event), app.getGeoGebraElement().getScaleX());
	}

	@Override
	public void setEditAsText(boolean asText) {
		this.editAsText = asText;
	}

	@Override
	public void setPixelRatio(double ratio) {
		mf.setPixelRatio(ratio);
	}

	/**
	 * Updates the font size.
	 */
	public void updateFontSize() {
		int targetFontSize = app.getSettings().getFontSettings()
				.getAppFontSize();

		mf.setFontSize(targetFontSize);
		setDummyFontSize(targetFontSize);
	}

	private void setDummyFontSize(int size) {
		Element element = dummy.getElement();
		Style style = element.getStyle();
		style.setFontSize(size, Unit.PX);
	}
}
