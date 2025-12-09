/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.view.autocompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.util.MatchedString;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.user.client.ui.MultiWordSuggestOracle;

/**
 * Completions popup.
 */
public class CompletionsPopup extends MultiWordSuggestOracle {

	private AutoCompleteTextFieldW textField;

	/**
	 * New completions popup.
	 */
	public CompletionsPopup() {
		super();
		clear();
    }

	/**
	 * @param autoCompleteTextField
	 *            textfield
	 */
	public void addTextField(AutoCompleteTextFieldW autoCompleteTextField) {
		this.textField = autoCompleteTextField;
    }

	/**
	 * @param history
	 *            input history
	 */
	public void showHistoryCompletions(ArrayList<String> history) {
		if (history != null) {
			clear();
			addAll(history);
		}
    }

	@Override
	public void requestSuggestions(Request request, Callback callback) {

		if (textField == null) {
			return;
		}

		if (!textField.getAutoComplete()) {
			callback.onSuggestionsReady(request,
					new Response(Collections.<Suggestion> emptyList()));
			return;
		}
		textField.resetCompletions();
		String query = request.getQuery();
		List<MatchedString> completions = textField.getCompletions();
		if (completions == null || completions.size() == 0) {
			callback.onSuggestionsReady(request,
					new Response(Collections.<Suggestion> emptyList()));
			return;
		}
		int limit = request.getLimit();

		// respect limit for number of choices
		int numberTruncated = Math.max(0, completions.size() - limit);
		if (limit < completions.size()) {
			completions = completions.subList(0, limit);
		}

		// convert candidates to suggestions
		List<MultiWordSuggestion> suggestions = convertToFormattedSuggestions(
				query, completions);

		Response response = new Response(suggestions);
		response.setMoreSuggestionsCount(numberTruncated);

		callback.onSuggestionsReady(request, response);
	}
	
	private static List<MultiWordSuggestion> convertToFormattedSuggestions(
			String query, List<MatchedString> candidates) {
		List<MultiWordSuggestion> suggestions = new ArrayList<>();
		for (int i = 0; i < candidates.size(); i++) {
			MatchedString candidate = candidates.get(i);
			
			SafeHtmlBuilder accum = new SafeHtmlBuilder();
			if (query.length() < candidate.content.length()) {
				String part1 = candidate.content.substring(0, candidate.from);
				String part2 = candidate.content.substring(candidate.from,
						candidate.from  + query.length());
				String part3 = candidate.content.substring(candidate.from  + query.length());
				accum.appendEscaped(part1);
				accum.appendHtmlConstant("<strong>");
				accum.appendEscaped(part2);
				accum.appendHtmlConstant("</strong>");
				accum.appendEscaped(part3);
			} else {
				accum.appendEscaped(candidate.content);
			}
			suggestions.add(new MultiWordSuggestion(candidate.content,
					accum.toSafeHtml().asString()));
		}
		return suggestions;
	}
}
