package org.geogebra.test.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class CommandSignatures {

	/**
	 * @param cmdName
	 *            command name
	 * @return numbers of arguments per syntax
	 */
	public static List<Integer> getSignature(String cmdName, App app) {
		if ("ExportImage".equals(cmdName)) {
			return Arrays.asList(0, 2, 4, 6);
		}
		String syntax = app.getLocalization().getCommand(cmdName + Localization.syntaxStr);
		if (!syntax.contains(Localization.syntaxStr)) {
			ArrayList<Integer> signature = new ArrayList<>();
			for (String line : syntax.split("\n")) {
				if ("[]".equals(line) || "[ ]".equals(line)) {
					signature.add(0);
				} else {
					signature.add(line.replace("x, y", "xy").split(",").length);
				}
			}
			if (Arrays.asList("Function", "Random", "PenStroke", "DataFunction", "Textfield")
					.contains(cmdName)) {
				signature.add(0);
			}
			return signature;
		}
		return null;
	}
}
