package org.geogebra.web.full.gui.inputfield;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.inputfield.InputSuggestions;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.gui.view.autocompletion.GSuggestBox;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.himamis.retex.editor.share.model.Korean;

public class MathFieldInputSuggestions extends InputSuggestions
		implements HasSuggestions {

	private static final int MINIMUM_HEIGHT = 29;

	private ScrollableSuggestionDisplay sug;
	public static final int QUERY_LIMIT = 5000;
	StringBuilder curWord;
	protected CompletionsPopup popup;
	private @Nonnull AutoCompleteW component;

	/**
	 * @param app
	 *            application
	 * @param component
	 *            text input
	 * @param forCAS
	 *            whether to use this for CAS
	 */
	public MathFieldInputSuggestions(AppW app, AutoCompleteW component,
			boolean forCAS) {
		super(app, forCAS);
		this.component = component;
		curWord = new StringBuilder();
		popup = new CompletionsPopup();
		popup.addTextField(component);
		sug = new ScrollableSuggestionDisplay(this, app.getPanel(), app);
	}

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		@Override
		public void onSuggestionsReady(SuggestOracle.Request req,
				SuggestOracle.Response res) {
			updateSuggestions(res);

		}
	};

	protected GSuggestBox.SuggestionCallback sugCallback = new GSuggestBox.SuggestionCallback() {
		@Override
		public void onSuggestionSelected(Suggestion s) {
			String sugg = s.getReplacementString();
			autocompleteAndHide(sugg);
		}
	};

	/**
	 * @param searchRight
	 *            TODO whether to check chars to the right?
	 */
	public void updateCurrentWord(boolean searchRight) {
		curWord = new StringBuilder(component.getCommand());
	}

	public int getCaretPosition() {
		return 0;
	}

	/**
	 * @param sugg
	 *            suggestion
	 */
	public void autocompleteAndHide(String sugg) {
		component.insertString(sugg);
		sug.hideSuggestions();
	}

	/**
	 * Show suggestions.
	 * 
	 * @return true
	 */
	public boolean popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false); // compatibility should be preserved
		if (curWord != null && curWord.length() > 0
				&& !"sqrt".equals(curWord.toString())) {
			// for length check we also need flattenKorean
			if (!needsAutocomplete(this.curWord)) {
				// if there is only one letter typed,
				// for any reason, this method should
				// hide the suggestions instead!
				hideSuggestions();
			} else {
				Log.debug("requestingSug" + curWord);
				Log.debug(
						"Korean:" + Korean.unflattenKorean(curWord.toString()));
				popup.requestSuggestions(
						new SuggestOracle.Request(this.curWord.toString(),
								QUERY_LIMIT), popupCallback);
			}
		} else {
			hideSuggestions();
		}
		return true;
	}

	/**
	 * Hide the suggestions.
	 * 
	 * @return true
	 */
	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	/**
	 * Update the suggestions.
	 * 
	 * @param res
	 *            oracle response
	 */
	protected void updateSuggestions(Response res) {
		sug.updateHeight();
		component.updatePosition(sug);
		sug.accessShowSuggestions(res, popup, sugCallback);
	}

	public void setFocus() {
		sug.focus();
	}

	public boolean isSuggesting() {
		return sug.isSuggestionListShowing();
	}

	/**
	 * @return whether enter should be consumend by suggestions
	 */
	public boolean needsEnterForSuggestion() {
		if (sug.isSuggestionListShowing()) {
			sugCallback.onSuggestionSelected(sug.accessCurrentSelection());
			return true;
		}
		return false;
	}

	public void onKeyDown() {
		sug.moveSelectionDown();
	}

	public void onKeyUp() {
		sug.moveSelectionUp();
	}

	/**
	 * @return completions for current word
	 */
	public List<String> resetCompletions() {
		updateCurrentWord(false);
		return resetCompletions(curWord);
	}

	@Override
	public double getMaxSuggestionsHeight() {
		AppW app = component.getApplication();

		double spaceBelow = app.getHeight() + app.getAbsTop()
				- component.getAbsoluteTop()
				- component.toWidget().getOffsetHeight()
				- app.getAppletFrame().getKeyboardHeight();

		return Math.max(MINIMUM_HEIGHT, Math.min(app.getHeight() / 2, spaceBelow));
	}
}
