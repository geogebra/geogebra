package org.geogebra.common.main.syntax;

import org.geogebra.common.main.Localization;

/**
 * Class to get the syntax of the command
 * always in english.
 *
 * @author Laszlo
 */
public class EnglishCommandSyntax extends LocalizedCommandSyntax {

	/**
	 * @param localization the localization.
	 */
	public EnglishCommandSyntax(Localization localization) {
		super(localization);
	}

	@Override
	protected String getLocalizedCommand(String key) {
		return getLocalization().getEnglishCommand(key);
	}
}