package org.geogebra.common.main.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.syntax.EnglishCommandSyntax;
import org.geogebra.common.main.syntax.LocalizedCommandSyntax;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.MatchedString;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public class AutocompleteProvider {
	@NonOwning
	@Weak
	@Nonnull
	private final App app;
	private final boolean forCAS;
	private LocalizedCommandSyntax englishCommandSyntax;

	/**
	 * @param app application
	 * @param forCAS whether this is for the classic CAS
	 */
	public AutocompleteProvider(@Nonnull App app, boolean forCAS) {
		this.app = app;
		this.forCAS = forCAS;
	}

	/**
	 * Adds a syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void addSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			getEnglishCommandSyntax().addSyntaxFilter(syntaxFilter);
		}
	}

	/**
	 * Removes a previously added syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void removeSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			getEnglishCommandSyntax().removeSyntaxFilter(syntaxFilter);
		}
	}

	/**
	 * @param localizedCommandName localized command
	 * @return syntaxes of a single command
	 */
	public List<String> getSyntaxes(String localizedCommandName) {
		ArrayList<String> syntaxes = new ArrayList<>();
		addSyntaxes(localizedCommandName, syntaxes);
		return syntaxes;
	}

	/**
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 *
	 * @param commands
	 *            commands
	 * @return syntaxes
	 */
	public List<MatchedString> getSyntaxes(List<MatchedString> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<MatchedString> syntaxes = new ArrayList<>();
		for (MatchedString command : commands) {
			addSyntaxes(command, syntaxes);
		}
		return syntaxes;
	}

	private void addSyntaxes(MatchedString match, ArrayList<MatchedString> syntaxes) {
		String syntaxString = getSyntaxString(match.content);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(new MatchedString(syntax, match.from, match.to));
		}
	}

	private void addSyntaxes(String localizedCommandName, ArrayList<String> syntaxes) {
		String syntaxString = getSyntaxString(localizedCommandName);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(syntax);
		}
	}

	private String getSyntaxString(String localizedCommandName) {
		String internalCommandName = app.getInternalCommand(localizedCommandName);
		boolean englishOnly = internalCommandName == null
				&& isFallbackCompletionAllowed();
		if (englishOnly) {
			internalCommandName = app.englishToInternal(localizedCommandName);
		}

		String syntaxString;
		if (isCas()) {
			LocalizedCommandSyntax commandSyntax = app.getLocalization().getCommandSyntax();
			syntaxString = commandSyntax.getCommandSyntaxCAS(internalCommandName);
		} else {
			LocalizedCommandSyntax commandSyntax = englishOnly
					? getEnglishCommandSyntax() : app.getLocalization().getCommandSyntax();
			AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
			syntaxString = algebraProcessor.getSyntax(commandSyntax, internalCommandName,
					app.getSettings());
		}

		if (syntaxString == null || syntaxString.isEmpty()) {
			return "";
		}

		if (syntaxString.endsWith(Localization.syntaxCAS)
				|| syntaxString.endsWith(Localization.syntaxStr)) {
			// command not found, check for macros
			Macro macro = forCAS ? null
					: app.getKernel().getMacro(internalCommandName);
			if (macro != null) {
				return macro.toString();
			} else {
				// syntaxes.add(cmdInt + "[]");
				Log.debug("Can't find syntax for: " + internalCommandName);
			}
			return "";
		}
		return syntaxString;
	}

	private LocalizedCommandSyntax getEnglishCommandSyntax() {
		if (englishCommandSyntax == null) {
			englishCommandSyntax = new EnglishCommandSyntax(app.getLocalization());
		}
		return englishCommandSyntax;
	}

	/**
	 * @return whether to allow English commands as well
	 */
	public boolean isFallbackCompletionAllowed() {
		return app.getLocalization().languageIs("zh");
	}

	/**
	 * @param curWord word to be completed
	 * @return stream of suggestions
	 */
	public Stream<Completion> getCompletions(String curWord) {
		List<String> functionResults = app.getParserFunctions().getCompletions(curWord);
		Stream<Completion> completions = functionResults.stream()
				.map(function -> new Completion(getMatch(function, curWord),
						Collections.singletonList(function),
						App.WIKI_OPERATORS,
						GuiManagerInterface.Help.GENERIC));

		List<MatchedString> commandResults = getCommandDictionary()
				.getCompletions(curWord.toLowerCase());
		if (commandResults != null) {
			Stream<Completion> commandCompletions = commandResults.stream()
					.map(command -> new Completion(command,
							getSyntaxes(command.content),
							app.getInternalCommand(command.content),
							GuiManagerInterface.Help.COMMAND));
			completions = Stream.concat(completions, commandCompletions);
		}

		return completions.filter(completion -> !completion.syntaxes.isEmpty());
	}

	private MatchedString getMatch(String function, String curWord) {
		return new MatchedString(function.split("\\(")[0], 0, curWord.length());
	}

	private boolean isCas() {
		return forCAS || app.getConfig().getVersion() == GeoGebraConstants.Version.CAS;
	}

	private LowerCaseDictionary getCommandDictionary() {
		return isCas() ? app.getCommandDictionaryCAS() : app.getCommandDictionary();
	}

	public static class Completion {
		public final MatchedString match;
		public final List<String> syntaxes;
		public final String helpPage;
		public final GuiManagerInterface.Help helpType;

		private Completion(MatchedString match, List<String> syntaxes, String helpPage,
				GuiManagerInterface.Help helpType) {
			this.match = match;
			this.syntaxes = syntaxes;
			this.helpPage = helpPage;
			this.helpType = helpType;
		}

		public MatchedString getMatch() {
			return match;
		}

		public String getCommand() {
			return match.content;
		}

		public List<String> getSyntaxes() {
			return syntaxes;
		}

		public String getHelpPage() {
			return helpPage;
		}

		public GuiManagerInterface.Help getHelpType() {
			return helpType;
		}

		public int getOffset() {
			return match.from;
		}

		@Override
		public String toString() {
			return match.content;
		}
	}
}
