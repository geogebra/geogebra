package org.geogebra.common.main.syntax;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Class to get the syntax of the command with the
 * current locale
 *
 * @author Laszlo
 */
public class LocalizedCommandSyntax implements CommandSyntax {

	private final Localization loc;

	private List<SyntaxFilter> syntaxFilters = new ArrayList<>();

	/**
	 *
	 * @param localization the localization.
	 */
	public LocalizedCommandSyntax(Localization localization) {
		this.loc = localization;
	}

	/**
	 * @param internalCommandName internal command name
	 * @param dim dimension override
	 * @return command syntax TODO check whether getSyntaxString works here
	 */
	@Override
	public String getCommandSyntax(String internalCommandName, int dim) {
		String localizedCommandName = getLocalizedCommand(internalCommandName);
		if (dim == 3) {
			String keySyntax3D = internalCommandName + Localization.syntax3D;
			String syntax3D = loc.getCommand(keySyntax3D);
			if (!syntax3D.equals(keySyntax3D)) {
				syntax3D = filterSyntax(internalCommandName, syntax3D);
				return buildSyntax(syntax3D, localizedCommandName);
			}
		}
		String syntax = getLocalizedSyntax(internalCommandName);
		syntax = filterSyntax(internalCommandName, syntax);
		syntax = buildSyntax(syntax, localizedCommandName);
		return syntax;
	}

	/**
	 *
	 * @param internalCommandName internal command name
	 * @return the localized command
	 */
	protected String getLocalizedCommand(String internalCommandName) {
		return loc.getCommand(internalCommandName);
	}

	private String getLocalizedSyntax(String internalCommandName) {
		return getLocalizedCommand(internalCommandName + Localization.syntaxStr);
	}

	private String getLocalizedSyntaxCAS(String internalCommandName) {
		return getLocalizedCommand(internalCommandName + Localization.syntaxCAS);
	}

	private String filterSyntax(String internalCommandName, String syntax) {
		String filteredSyntax = syntax;
		for (SyntaxFilter syntaxFilter : syntaxFilters) {
			filteredSyntax = syntaxFilter.getFilteredSyntax(internalCommandName, filteredSyntax);
		}
		return filteredSyntax;
	}

	private String buildSyntax(String syntax, String command) {
		return syntax.replace("[", command + '(').replace(']', ')');
	}

	@Override
	public String getCommandSyntaxCAS(String internalCommandName) {
		String command = getLocalizedCommand(internalCommandName);
		String syntax = getLocalizedSyntaxCAS(internalCommandName);

		String keyCAS = internalCommandName + Localization.syntaxCAS;
		// make sure "PointList.SyntaxCAS" not displayed in dialog
		if (syntax.equals(keyCAS)) {
			syntax = getLocalizedSyntax(internalCommandName);
		}

		syntax = filterSyntax(internalCommandName, syntax);
		syntax = buildSyntax(syntax, command);
		return syntax;
	}

	/**
	 *
	 * @return the localization.
	 */
	protected Localization getLocalization() {
		return loc;
	}

	/**
	 * Add a syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void addSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			syntaxFilters.add(syntaxFilter);
		}
	}

	/**
	 * Remove a previously added syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void removeSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			syntaxFilters.remove(syntaxFilter);
		}
	}
}