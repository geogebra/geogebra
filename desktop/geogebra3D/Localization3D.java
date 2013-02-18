package geogebra3D;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.main.LocalizationD;

public class Localization3D extends LocalizationD{
	
	public Localization3D(App app) {
		super(app);
	}

	@Override
	public String getCommandSyntax(String key) {
		String command = getCommand(key);
		String key3D = key + Localization.syntax3D;
		String syntax = getCommand(key3D);
		if (!syntax.equals(key3D)) {
			syntax = syntax.replace("[", command + '[');
			return syntax;
		}

		return super.getCommandSyntax(key);
	}
}
