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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.GeoGebraConstants.Version;
import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.App;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.geogebra.common.util.ManualPage;
import org.geogebra.common.util.MatchedString;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.controller.InputController;
import org.geogebra.editor.share.editor.MathField;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.jspecify.annotations.NonNull;

import com.google.j2objc.annotations.Property;
import com.google.j2objc.annotations.Weak;

/**
 * View model of the input bar help. Holds a navigation stack of {@link InputBarHelpPage}s, the
 * last page being the visible one; the help is hidden when the stack is empty.
 * <p>
 * Navigation: categories (with recent syntaxes) → commands of a category → syntaxes of a
 * command. Selecting a syntax notifies {@link #syntaxSelected} and closes the help.
 * <p>
 * Keeps the recently selected syntaxes in memory, newest first, without duplicates; they are
 * cleared when an exam starts or ends.
 */
public final class InputBarHelpViewModel implements ExamListener {

	private static final int RECENT_SYNTAXES_CAPACITY = 5;

	/** Notified when a syntax is selected, before the help closes. */
	@Property("readonly")
	public final @NonNull MulticastEvent<Insertion> syntaxSelected = new MulticastEvent<>();

	@Weak
	private final App app;

	private final MutableState<@NonNull List<InputBarHelpPage>> navigationStack =
			new MutableState<>(List.of());

	private final Map<CommandCategory, List<String>> commandsByCategory =
			new EnumMap<>(CommandCategory.class);
	private final Map<String, List<String>> mathFunctionSyntaxes = new HashMap<>();
	private final List<String> recentSyntaxes = new ArrayList<>();
	private boolean replacesWord;
	private int charsBeforeCursor;
	private int charsAfterCursor;

	/**
	 * @param app app
	 */
	public InputBarHelpViewModel(@NonNull App app) {
		this.app = app;
	}

	/** @return the navigation stack, bottom first; empty when the help is hidden */
	public @NonNull State<@NonNull List<InputBarHelpPage>> getNavigationStack() {
		return navigationStack;
	}

	/** @return whether the help is shown */
	public boolean isOpen() {
		return !navigationStack.get().isEmpty();
	}

	/**
	 * Shows the categories page as the only page. If the word around the cursor is a math
	 * function, or neither a reserved name nor an existing label, the search starts with it
	 * and the selected syntax replaces it.
	 * @param editorState state of the input
	 */
	public void open(@NonNull EditorState editorState) {
		updateCommands();

		StringBuilder wordBuilder = new StringBuilder();
		int lengthBeforeCursor = InputController.getWordAroundCursor(editorState, wordBuilder);
		String word = wordBuilder.toString();
		replacesWord = word.isEmpty()
				|| mathFunctionSyntaxes.containsKey(word)
				|| (!app.getParserFunctions().isReserved(word)
						&& app.getKernel().lookupLabel(word) == null);
		charsBeforeCursor = replacesWord ? lengthBeforeCursor : 0;
		charsAfterCursor = replacesWord ? word.length() - lengthBeforeCursor : 0;
		String searchText = replacesWord ? word : "";

		String title = app.getLocalization().getMenu("Commands");
		InputBarHelpPage.CommandCategories categoriesPage = new InputBarHelpPage.CommandCategories(
				title, "", List.of(), List.copyOf(recentSyntaxes), getCategoriesToShow());
		List<MatchedString> searchResults = search(CommandCategory.ALL_COMMANDS, searchText);
		navigationStack.set(List.of(categoriesPage.withSearchText(searchText, searchResults)));
	}

	/**
	 * Opens the commands of a category on top of the current page. Does nothing if the help is
	 * hidden.
	 * @param category category
	 */
	public void openCategory(@NonNull CommandCategory category) {
		if (!isOpen()) {
			return;
		}
		String title = app.getLocalization().getMenu(category.getTranslationKey());
		push(new InputBarHelpPage.Commands(title, "", search(category, ""), category));
	}

	/**
	 * Opens the syntaxes of a command on top of the current page. Does nothing if the help is
	 * hidden.
	 * @param command localized command name or math function
	 */
	public void openCommand(@NonNull String command) {
		if (!isOpen()) {
			return;
		}
		boolean isMathFunction = mathFunctionSyntaxes.containsKey(command);
		List<String> syntaxes = isMathFunction
				? mathFunctionSyntaxes.get(command)
				: app.getAutocompleteProvider().getSyntaxes(command);
		String manualUrl = null;
		if (!GlobalScope.isExamActive(app)) {
			String url = isMathFunction
					? app.getGuiManager().getHelpURL(ManualPage.OPERATORS, null)
					: app.getGuiManager().getHelpURL(ManualPage.COMMAND, app.getReverseCommand(command));
			manualUrl = url.replace(" ", "%20");
		}
		push(new InputBarHelpPage.Syntaxes(command, syntaxes, manualUrl));
	}

	/**
	 * Updates the search text of the current page.
	 * @param searchText search text
	 */
	public void setSearchText(@NonNull String searchText) {
		List<InputBarHelpPage> stack = new ArrayList<>(navigationStack.get());
		if (!stack.isEmpty()
				&& stack.get(stack.size() - 1) instanceof InputBarHelpPage.Searchable page) {
			List<MatchedString> searchResults = search(page.searchCategory(), searchText);
			stack.set(stack.size() - 1, page.withSearchText(searchText, searchResults));
			navigationStack.set(stack);
		}
	}

	/**
	 * Clears the search text of the current page. If the search started with the word around
	 * the cursor, from now on the selected syntax is inserted after a space instead of replacing
	 * that word.
	 */
	public void clearSearch() {
		if (charsBeforeCursor + charsAfterCursor > 0) {
			replacesWord = false;
			charsBeforeCursor = 0;
			charsAfterCursor = 0;
		}
		setSearchText("");
	}

	/**
	 * Adds the syntax to the recent syntaxes, notifies {@link #syntaxSelected} and closes the
	 * help.
	 * @param syntax selected syntax
	 */
	public void selectSyntax(@NonNull String syntax) {
		recentSyntaxes.remove(syntax);
		recentSyntaxes.add(0, syntax);
		if (recentSyntaxes.size() > RECENT_SYNTAXES_CAPACITY) {
			recentSyntaxes.remove(RECENT_SYNTAXES_CAPACITY);
		}
		syntaxSelected.notifyListeners(
				new Insertion(syntax, replacesWord, charsBeforeCursor, charsAfterCursor));
		close();
	}

	/** Leaves the top page; hides the help if it was the only page. */
	public void navigateBack() {
		List<InputBarHelpPage> stack = navigationStack.get();
		if (!stack.isEmpty()) {
			navigationStack.set(stack.subList(0, stack.size() - 1));
		}
	}

	/** Hides the help. */
	public void close() {
		navigationStack.set(List.of());
	}

	@Override
	public void examStateChanged(ExamState newState) {
		if (newState == ExamState.ACTIVE || newState == ExamState.FINISHED) {
			recentSyntaxes.clear();
		}
	}

	private void push(@NonNull InputBarHelpPage page) {
		List<InputBarHelpPage> newStack = new ArrayList<>(navigationStack.get());
		newStack.add(page);
		navigationStack.set(newStack);
	}

	private void updateCommands() {
		AutocompleteProvider provider = app.getAutocompleteProvider();
		mathFunctionSyntaxes.clear();
		for (String syntax : provider.getFunctionSyntaxes()) {
			String name = syntax.split("\\(")[0];
			mathFunctionSyntaxes.computeIfAbsent(name, key -> new ArrayList<>()).add(syntax);
		}

		// commands without syntaxes (e.g. CAS commands when Solve is not allowed) are skipped,
		// like in the input suggestion popup
		List<String> allCommands = new ArrayList<>();
		for (String command : provider.getCommandDictionary().values()) {
			if (!provider.getSyntaxes(command).isEmpty()) {
				allCommands.add(command);
			}
		}
		// math functions are not added to the case-insensitive command dictionary, because
		// the function gamma would replace the command Gamma there
		allCommands.addAll(mathFunctionSyntaxes.keySet());
		allCommands.sort(
				Comparator.comparing(StringUtil::removeAccents).thenComparing(Comparator.naturalOrder()));

		for (CommandCategory category : CommandCategory.values()) {
			if (category == CommandCategory.ALL_COMMANDS) {
				commandsByCategory.put(category, allCommands);
			} else {
				Set<String> namesInCategory = getCommandNames(category);
				commandsByCategory.put(
						category,
						allCommands.stream().filter(namesInCategory::contains).collect(Collectors.toList()));
			}
		}
	}

	/** @return names of all commands in the category, including the ones the app does not allow */
	private @NonNull Set<String> getCommandNames(@NonNull CommandCategory category) {
		if (category == CommandCategory.MATH_FUNCTIONS) {
			return mathFunctionSyntaxes.keySet();
		}
		Set<String> names = new HashSet<>();
		for (Commands command : Commands.values()) {
			if (category.contains(command)) {
				String internalName = Commands.englishToInternal(command).name();
				names.add(app.getLocalization().getCommand(internalName));
			}
		}
		return names;
	}

	private @NonNull List<CommandCategory> getCategoriesToShow() {
		Version version = app.getConfig().getVersion();
		List<CommandCategory> pinnedCategories = new ArrayList<>();
		List<CommandCategory> otherCategories = new ArrayList<>();
		for (CommandCategory category : CommandCategory.values()) {
			boolean isHiddenInThisApp =
					(category == CommandCategory.THREE_D && version != Version.GRAPHING_3D)
							|| (category == CommandCategory.CAS && version != Version.CAS);
			if (isHiddenInThisApp || commandsByCategory.get(category).isEmpty()) {
				continue;
			}
			if (category == CommandCategory.MATH_FUNCTIONS || category == CommandCategory.ALL_COMMANDS) {
				pinnedCategories.add(category);
			} else {
				otherCategories.add(category);
			}
		}
		otherCategories.sort(Comparator.comparing((CommandCategory category) ->
				StringUtil.removeAccents(app.getLocalization().getMenu(category.getTranslationKey()))));

		List<CommandCategory> categories = new ArrayList<>(pinnedCategories);
		categories.addAll(otherCategories);
		return categories;
	}

	/**
	 * @return the commands of the category matching the search text, with the matched part; all
	 * commands of the category with nothing matched if the search text is blank
	 */
	private @NonNull List<MatchedString> search(
			@NonNull CommandCategory category, @NonNull String searchText) {
		List<String> commandsInCategory = commandsByCategory.get(category);
		String query = searchText.trim();
		if (query.isEmpty()) {
			return commandsInCategory.stream()
					.map(command -> new MatchedString(command, 0, 0))
					.collect(Collectors.toList());
		}
		Set<String> namesInCategory = new HashSet<>(commandsInCategory);
		Set<String> addedNames = new HashSet<>();
		return app.getAutocompleteProvider()
				.getCompletions(query)
				.map(AutocompleteProvider.Completion::getMatch)
				.filter(match -> namesInCategory.contains(match.content()))
				// the suggestion popup lists functions like log once per syntax
				.filter(match -> addedNames.add(match.content()))
				.collect(Collectors.toList());
	}

	/**
	 * A selected syntax and how to insert it into the input.
	 * @param syntax the syntax to insert
	 * @param replacesWord if true, the word around the cursor is replaced by the syntax,
	 * otherwise the syntax is inserted after a space
	 * @param charsBeforeCursor number of characters to remove before the cursor
	 * @param charsAfterCursor number of characters to remove after the cursor
	 */
	public record Insertion(
			@NonNull String syntax, boolean replacesWord, int charsBeforeCursor, int charsAfterCursor) {

		/**
		 * Inserts the syntax into the input at its cursor, either in place of the word around
		 * the cursor or after a space, and notifies the input change listeners.
		 * @param mathField input
		 */
		public void insertInto(@NonNull MathField mathField) {
			MathFieldInternal editor = mathField.getInternal();
			EditorState editorState = editor.getEditorState();
			InputController inputController = editor.getInputController();
			if (replacesWord) {
				inputController.removeCharacters(editorState, charsBeforeCursor, charsAfterCursor);
			} else {
				inputController.newCharacter(editorState, ' ');
			}
			KeyboardInputAdapter.onKeyboardInput(editor, syntax);
			mathField.fireInputChangedEvent();
		}
	}
}
