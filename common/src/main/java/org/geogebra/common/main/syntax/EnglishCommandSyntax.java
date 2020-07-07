package org.geogebra.common.main.syntax;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Class to get the syntax of the command
 * always in english.
 *
 * @author Laszlo
 */
public class EnglishCommandSyntax extends LocalizedCommandSyntax {

	/**
	 *
	 * @param localization the localization.
	 */
	public EnglishCommandSyntax(Localization localization, SyntaxFilter syntaxFilter) {
		super(localization, syntaxFilter);
	}

	@Override
	protected String getLocalizedCommand(String key) {
		return getLocalization().getEnglishCommand(key);
	}
}