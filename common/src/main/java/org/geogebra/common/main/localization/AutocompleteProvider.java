package org.geogebra.common.main.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.MatchedString;
import org.geogebra.common.util.debug.Log;

public class AutocompleteProvider {
	private final App app;
	private final boolean forCAS;

	/**
	 * @param app application
	 * @param forCAS whether this is for the classic CAS
	 */
	public AutocompleteProvider(App app, boolean forCAS) {
		this.app = app;
		this.forCAS = forCAS;
	}

	/**
	 * @param command localized command
	 * @return syntaxes of a single command
	 */
	public List<String> getSyntaxes(String command) {
		ArrayList<String> syntaxes = new ArrayList<>();
		addSyntaxes(command, syntaxes);
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
		for (MatchedString cmd : commands) {
			addSyntaxes(cmd, syntaxes);
		}
		return syntaxes;
	}

	private void addSyntaxes(MatchedString match, ArrayList<MatchedString> syntaxes) {
		String syntaxString = getSyntaxString(match.content);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(new MatchedString(syntax, match.from, match.to));
		}
	}

	private void addSyntaxes(String cmd, ArrayList<String> syntaxes) {
		String syntaxString = getSyntaxString(cmd);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(syntax);
		}
	}

	private String getSyntaxString(String cmd) {
		String cmdInt = app.getInternalCommand(cmd);
		boolean englishOnly = cmdInt == null
				&& isFallbackCompletionAllowed();

		if (englishOnly) {
			cmdInt = app.englishToInternal(cmd);
		}
		String syntaxString;
		if (isCas()) {
			app.getLocalization().getCommandSyntax()
					.setSyntaxFilter(app.getConfig().newCommandSyntaxFilter());
			syntaxString = app.getLocalization()
					.getCommandSyntaxCAS(cmdInt);
		} else {
			AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
			syntaxString = englishOnly
					? ap.getEnglishSyntax(cmdInt, app.getSettings())
					: ap.getSyntax(cmdInt, app.getSettings());
		}

		if (syntaxString == null || syntaxString.isEmpty()) {
			return "";
		}

		if (syntaxString.endsWith(Localization.syntaxCAS)
				|| syntaxString.endsWith(Localization.syntaxStr)) {
			// command not found, check for macros
			Macro macro = forCAS ? null
					: app.getKernel().getMacro(cmd);
			if (macro != null) {
				return macro.toString();
			} else {
				// syntaxes.add(cmdInt + "[]");
				Log.debug("Can't find syntax for: " + cmd);
			}

			return "";
		}
		return syntaxString;
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
		Stream<Completion> completions = app.getParserFunctions().getCompletions(curWord).stream()
				.map(function -> new Completion(getMatch(function, curWord),
						Collections.singletonList(function), App.WIKI_OPERATORS,
						GuiManagerInterface.Help.GENERIC));
		List<MatchedString> cmdDict = getDictionary()
				.getCompletions(curWord.toLowerCase());

		if (cmdDict != null) {
			Stream<Completion> commands = cmdDict.stream()
					.map(command -> new Completion(command, getSyntaxes(command.content),
							app.getInternalCommand(command.content),
							GuiManagerInterface.Help.COMMAND));
			completions = Stream.concat(completions, commands);
		}

		return completions.filter(completion -> !completion.syntaxes.isEmpty());
	}

	private MatchedString getMatch(String function, String curWord) {
		return new MatchedString(function.split("\\(")[0], 0, curWord.length());
	}

	private boolean isCas() {
		return forCAS || app.getConfig().getVersion() == GeoGebraConstants.Version.CAS;
	}

	private LowerCaseDictionary getDictionary() {
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
