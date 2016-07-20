package org.geogebra.web.web.gui.inputfield;

import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class InputSuggestions {
	protected AutoCompleteDictionary dict;
	private ScrollableSuggestionDisplay sug;
	public static int querylimit = 5000;
	private List<String> completions;
	StringBuilder curWord;
	private int curWordStart;
	protected CompletionsPopup popup;
	private App app;

	private EquationEditorListener component;

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		public void onSuggestionsReady(SuggestOracle.Request req,
				SuggestOracle.Response res) {
			updateSuggestions(res);
		}
	};

	protected SuggestBox.SuggestionCallback sugCallback = new SuggestBox.SuggestionCallback() {
		public void onSuggestionSelected(Suggestion s) {

			String sugg = s.getReplacementString();
			autocomplete(sugg, true);
		}
	};

	public void updateCurrentWord(boolean searchRight) {
		int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
				component.getText(), getCaretPosition(), false);
		if (next > -1) {
			this.curWordStart = next;
		}
	}

	public int getCaretPosition() {
		return 0;
	}

	public void autocomplete(String sugg, boolean replace) {

	}

	public boolean popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false);// compatibility should be preserved
		if (curWord != null && curWord.length() > 0
				&& !"sqrt".equals(curWord.toString())) {
			// for length check we also need flattenKorean
			if (!InputHelper.needsAutocomplete(this.curWord, app.getKernel())) {
				// if there is only one letter typed,
				// for any reason, this method should
				// hide the suggestions instead!
				hideSuggestions();
			} else {
				popup.requestSuggestions(
						new SuggestOracle.Request(this.curWord.toString(),
								querylimit), popupCallback);
			}
		} else {
			hideSuggestions();
		}
		return true;
	}

	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	protected void updateSuggestions(Response res) {
		sug.updateHeight();
		component.updatePosition(sug);
		sug.accessShowSuggestions(res, popup, sugCallback);

	}
}
