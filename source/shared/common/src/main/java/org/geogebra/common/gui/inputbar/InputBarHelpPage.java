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

package org.geogebra.common.gui.inputbar;

import java.util.List;

import org.geogebra.common.util.StringUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Immutable page of the input bar help, see {@link InputBarHelpViewModel}.
 */
public sealed interface InputBarHelpPage {

	/** @return the title to show in the header */
	@NonNull String title();

	/**
	 * Page with a search field.
	 */
	sealed interface Searchable extends InputBarHelpPage {

		/** @return the text in the search field */
		@NonNull String searchText();

		/** @return the category searched by this page */
		@NonNull CommandCategory searchCategory();

		/**
		 * @param searchText search text
		 * @param searchResults commands matching the search text
		 * @return a page with the updated search
		 */
		@NonNull Searchable withSearchText(@NonNull String searchText, @NonNull List<String> searchResults);
	}

	/**
	 * Root page with the recent syntaxes and the command categories when the search text is
	 * empty, the matching commands of all categories otherwise.
	 * @param title the title to show in the header
	 * @param searchText the text in the search field
	 * @param searchResults matching commands; empty when the search text is empty
	 * @param recentSyntaxes recently selected syntaxes, newest first
	 * @param categories the non-empty categories
	 */
	record CommandCategories(
			@NonNull String title,
			@NonNull String searchText,
			@NonNull List<String> searchResults,
			@NonNull List<String> recentSyntaxes,
			@NonNull List<CommandCategory> categories)
			implements Searchable {

		@Override
		public @NonNull CommandCategory searchCategory() {
			return CommandCategory.ALL_COMMANDS;
		}

		@Override
		public @NonNull CommandCategories withSearchText(
				@NonNull String searchText, @NonNull List<String> searchResults) {
			List<String> results = StringUtil.emptyTrim(searchText) ? List.of() : searchResults;
			return new CommandCategories(title, searchText, results, recentSyntaxes, categories);
		}
	}

	/**
	 * Page with the commands of a category, filtered by the search text.
	 * @param title the title to show in the header
	 * @param searchText the text in the search field
	 * @param commands commands of the category matching the search text
	 * @param category the category
	 */
	record Commands(
			@NonNull String title,
			@NonNull String searchText,
			@NonNull List<String> commands,
			@NonNull CommandCategory category)
			implements Searchable {

		@Override
		public @NonNull CommandCategory searchCategory() {
			return category;
		}

		@Override
		public @NonNull Commands withSearchText(
				@NonNull String searchText, @NonNull List<String> searchResults) {
			return new Commands(title, searchText, searchResults, category);
		}
	}

	/**
	 * Page with the syntaxes of a command.
	 * @param title localized command name or math function
	 * @param syntaxes the syntaxes of the command
	 * @param manualUrl URL of the manual page of the command (the Operators page for math
	 * functions); null in exam mode
	 */
	record Syntaxes(
			@NonNull String title,
			@NonNull List<String> syntaxes,
			@Nullable String manualUrl) implements InputBarHelpPage {}
}
