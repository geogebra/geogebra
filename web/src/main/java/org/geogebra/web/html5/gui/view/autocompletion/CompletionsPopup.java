package org.geogebra.web.html5.gui.view.autocompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompletionsPopup extends MultiWordSuggestOracle {

	private AutoCompleteW textField;
	private VerticalPanel list;

	public CompletionsPopup() {
		super();
		clear();
    }


	public void addTextField(AutoCompleteW autoCompleteTextField) {
		this.textField = autoCompleteTextField;
    }

	public void showHistoryCompletions(ArrayList<String> history) {
		if (history != null) {
			clear();
			addAll(history);
		}
    }

	@Override
	public void requestSuggestions(Request request, Callback callback) {

		if (textField == null)
			return;

		if (!textField.getAutoComplete()) {
			callback.onSuggestionsReady(request, new Response(Collections.EMPTY_LIST));
			return;
		}
		textField.resetCompletions();
		String query = request.getQuery();
		List<String> completions = textField.getCompletions();

		if (completions == null || completions.size() == 0) {
			callback.onSuggestionsReady(request, new Response(Collections.EMPTY_LIST));
			return;
		}
		int limit = request.getLimit();

		// respect limit for number of choices
		int numberTruncated = Math.max(0, completions.size() - limit);
		if (limit < completions.size()) {
			completions = completions.subList(0, limit);
		}

		// convert candidates to suggestions
		List<MultiWordSuggestion> suggestions = convertToFormattedSuggestions(query, completions);

		Response response = new Response(suggestions);
		response.setMoreSuggestionsCount(numberTruncated);

		callback.onSuggestionsReady(request, response);
	}
	
	private static List<MultiWordSuggestion> convertToFormattedSuggestions(String query, List<String> candidates) {
		List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestion>();
		for (int i = 0; i < candidates.size(); i++) {
			String candidate = candidates.get(i);
			
			SafeHtmlBuilder accum = new SafeHtmlBuilder();
			if (query.length() < candidate.length()) {
				String part1 = candidate.substring(0, query.length());
				String part2 = candidate.substring(query.length(),
				        candidate.length());
				accum.appendHtmlConstant("<strong>");
				accum.appendEscaped(part1);
				accum.appendHtmlConstant("</strong>");
				accum.appendEscaped(part2);
			} else {
				accum.appendEscaped(candidate);
			}
			suggestions.add(new MultiWordSuggestion(candidate, accum.toSafeHtml().asString()));
		}
		return suggestions;
	}
}
