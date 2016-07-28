package org.geogebra.web.web.cas.view;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;
import org.geogebra.web.web.gui.view.algebra.RetexKeyboardListener;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.web.MathFieldW;

public class CASLaTeXEditor extends SimplePanel
		implements CASEditorW, MathKeyboardListener, AutoCompleteW,
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
		mf = new MathFieldW(canvas, canvas.getContext2d(), this);
		retexListener = new RetexKeyboardListener(canvas, mf);
		setText("");

	}

	public int getInputSelectionEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInputSelectionStart() {
		// TODO Auto-generated method stub
		return 0;
	}

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

	public String getInput() {
		return getText();
	}

	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub

	}

	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub

	}

	public void clearInputText() {
		setText("");

	}

	public void setInput(String string) {
		if (getWidget() != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		setText(string);

	}

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

	public Object getCellEditorValue(int index) {
		if (table != null) {
			return table.getGeoCasCell(index);
		}
		return null;
	}

	public void setLabels() {
		// TODO Auto-generated method stub

	}

	public void setFocus(boolean focus, boolean scheduled) {

		setWidget(focus ? mf.asWidget()
				: new Label(app.getPlain("InputLabel") + Unicode.ellipsis));
		mf.setFocus(focus);

	}

	public void onEnter(boolean b) {
		// TODO Auto-generated method stub
		if (sug.needsEnterForSuggestion()) {
			return;
		}
		this.controller.handleEnterKey(false, false, app);
	}

	public void resetInput() {
		// TODO Auto-generated method stub

	}

	public void setAutocomplete(boolean b) {
		this.autocomplete = true;

	}

	public void setLaTeX(String plain, String latex) {
		// not needed

	}

	public void ensureEditing() {
		app.getGuiManager().setOnScreenKeyboardTextField(this.retexListener);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(this, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				app.getGuiManager().setOnScreenKeyboardTextField(retexListener);
				// prevent that keyboard is closed on clicks (changing
				// cursor position)
				CancelEventTimer.keyboardSetVisible();
			}
		});
		setFocus(true, false);

	}

	public boolean getAutoComplete() {
		return autocomplete;
	}

	public List<String> resetCompletions() {
		return getInputSuggestions().resetCompletions();
	}

	public List<String> getCompletions() {
		return getInputSuggestions().getCompletions();
	}

	public void insertString(String text) {
		this.mf.deleteCurrentWord();
		GuiManagerW.makeKeyboardListener(retexListener).insertString(text);

	}

	public void toggleSymbolButton(boolean toggled) {
		// only for linear input

	}

	public ArrayList<String> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	public void requestFocus() {
		if (getWidget() != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		mf.requestViewFocus();

	}

	public Widget toWidget() {
		return this;
	}

	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener).insertString(text);

	}

	public void updatePosition(DefaultSuggestionDisplay sugPanel) {
		sugPanel.setPositionRelativeTo(this);

	}

	public boolean isForCAS() {
		return true;
	}

	public String getCommand() {
		return mf == null ? "" : mf.getCurrentWord();
	}

	private InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, this);
		}
		return sug;
	}

	public void onEnter() {
		// TODO or onEnter(false) ?
		onEnter(true);

	}

	public void onKeyTyped() {
		// TODO Auto-generated method stub
		getInputSuggestions().popupSuggestions();
	}

	public boolean needsAutofocus() {
		return true;
	}

}
