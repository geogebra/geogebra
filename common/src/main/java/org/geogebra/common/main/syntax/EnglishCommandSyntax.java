package org.geogebra.common.main.syntax;

import org.geogebra.common.main.Localization;

public class EnglishCommandSyntax extends LocalizedCommandSyntax {

	public EnglishCommandSyntax(Localization localization) {
		super(localization);
	}


	@Override
	protected String getLocalizedCommand(String key) {
		return getLocalization().getEnglishCommand(key);
	}

	@Override
	protected String getLocalizedSyntax(String key) {
		String syntaxKey = key + Localization.syntaxStr;
		return getLocalization().getEnglishCommand(syntaxKey);
	}
}