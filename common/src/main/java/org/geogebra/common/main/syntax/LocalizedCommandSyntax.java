package org.geogebra.common.main.syntax;

import org.geogebra.common.main.Localization;

public class LocalizedCommandSyntax implements CommandSyntax {
	private final Localization loc;

	public LocalizedCommandSyntax(Localization localization) {
		this.loc = localization;
	}

	/**
	 * @param key command name
	 * @param dim dimension override
	 * @return command syntax TODO check whether getSyntaxString works here
	 */
	@Override
	public String getCommandSyntax(String key, int dim) {
		String command = getLocalizedCommand(key);
		if (dim == 3) {
			String key3D = key + Localization.syntax3D;
			String cmdSyntax3D = loc.getCommand(key3D);
			if (!cmdSyntax3D.equals(key3D)) {
				cmdSyntax3D = buildSyntax(cmdSyntax3D, command);
				return cmdSyntax3D;
			}
		}

		String syntax = getLocalizedSyntax(key);
		syntax = buildSyntax(syntax, command);

		return syntax;
	}

	protected String getLocalizedCommand(String key) {
		return loc.getCommand(key);
	}

	private String getLocalizedSyntax(String key) {
		String syntaxKey = key + Localization.syntaxStr;
		return getLocalizedCommand(syntaxKey);
	}

	private String getLocalizedSyntaxCAS(String key) {
		String syntaxKey = key + Localization.syntaxCAS;
		return getLocalizedCommand(syntaxKey);
	}

	private String buildSyntax(String syntax, String command) {
		return syntax.replace("[", command + '(').replace(']', ')');
	}

	@Override
	public String getCommandSyntaxCAS(String key) {

		String command = getLocalizedCommand(key);
		String syntax = getLocalizedSyntaxCAS(key);

		String keyCAS = key + Localization.syntaxCAS;
		// make sure "PointList.SyntaxCAS" not displayed in dialog
		if (syntax.equals(keyCAS)) {
			syntax = getLocalizedSyntax(key);
		}

		syntax = buildSyntax(syntax, command);

		return syntax;
	}

	protected Localization getLocalization() {
		return loc;
	}
}