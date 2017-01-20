package org.geogebra.web.web.cas.view;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;
import org.geogebra.web.web.gui.view.algebra.RetexKeyboardListener;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;

public class CASLaTeXEditor extends FlowPanel
 implements CASEditorW,
		MathKeyboardListener,
		MathFieldListener {
	InputSuggestions sug;
	private MathFieldW mf;
	RetexKeyboardListener retexListener;
	private AppW app;
	private CASTableW table;
	private CASTableControllerW controller;
	private boolean autocomplete = true;

	public CASLaTeXEditor(CASTableW table, final AppW app,
			final CASTableControllerW controller) {
		this.app = app;
		this.table = table;
		this.controller = controller;
		Canvas canvas = Canvas.createIfSupported();
		mf = new MathFieldW(this, canvas, this);
		retexListener = new RetexKeyboardListener(canvas, mf);
		add(mf);

	}

	@Override
	public int getInputSelectionEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInputSelectionStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getInputSelectedText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		GeoGebraSerializer s = new GeoGebraSerializer();
		return s.serialize(mf.getFormula());
	}

	@Override
	public String getInput() {
		return getText();
	}

	@Override
	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub

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
		// removeDummy();
		if (mf != null) {
			Parser parser = new Parser(mf.getMetaModel());
			MathFormula formula;
			try {
				formula = parser.parse(text0);
				mf.setFormula(formula);
			} catch (ParseException e) {
				Log.warn("Problem parsing: " + text0);
				e.printStackTrace();
			}
		}
		// updateLineHeight();
	}

	@Override
	public Object getCellEditorValue(int index) {
		if (table != null) {
			return table.getGeoCasCell(index);
		}
		return null;
	}

	@Override
	public void setLabels() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus(boolean focus, boolean scheduled) {
		setWidget(focus ? mf.asWidget()
				: new Label(app.getLocalization().getMenu("InputLabel")
						+ Unicode.ellipsis));
		mf.setFocus(focus);

	}

	@Override
	public void onEnter(boolean b) {
		// TODO Auto-generated method stub
		if (sug.needsEnterForSuggestion()) {
			return;
		}
		this.controller.handleEnterKey(false, false, app);
	}

	@Override
	public void resetInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAutocomplete(boolean b) {
		this.autocomplete = true;

	}

	@Override
	public void setLaTeX(String plain, String latex) {
		// not needed

	}

	@Override
	public void ensureEditing() {
		app.getGuiManager().setOnScreenKeyboardTextField(this.retexListener);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(this, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				doClickStart();
			}
		});
		setFocus(true, false);

	}

	/**
	 * Click start callback
	 */
	protected void doClickStart() {
		app.getGuiManager().setOnScreenKeyboardTextField(retexListener);
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}
	@Override
	public boolean getAutoComplete() {
		return autocomplete;
	}

	@Override
	public List<String> resetCompletions() {
		return getInputSuggestions().resetCompletions();
	}

	@Override
	public List<String> getCompletions() {
		return getInputSuggestions().getCompletions();
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(text);
	}

	@Override
	public void toggleSymbolButton(boolean toggled) {
		// only for linear input

	}

	@Override
	public ArrayList<String> getHistory() {
		// TODO Auto-generated method stub
		return null;
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
		GuiManagerW.makeKeyboardListener(retexListener).insertString(text);

	}

	@Override
	public void updatePosition(DefaultSuggestionDisplay sugPanel) {
		sugPanel.setPositionRelativeTo(this);

	}

	@Override
	public boolean isForCAS() {
		return true;
	}

	@Override
	public String getCommand() {
		return mf == null ? "" : mf.getCurrentWord();
	}

	private InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, this);
		}
		return sug;
	}

	@Override
	public void onEnter() {
		// TODO or onEnter(false) ?
		onEnter(true);

	}

	@Override
	public void onKeyTyped() {
		// TODO Auto-generated method stub
		getInputSuggestions().popupSuggestions();
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public void onCursorMove() {
		// TODO Auto-generated method stub

	}

	@Override
	public String alt(int unicodeKeyChar, boolean shift) {
		return retexListener.alt(unicodeKeyChar, shift);
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
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		mf.setFormula(GeoGebraSerializer.reparse(this.mf.getFormula()));

	}

}
