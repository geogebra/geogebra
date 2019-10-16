package org.geogebra.test.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.Localization;

public class CommandSignatures {

	private static LocalizationJre loc = new LocalizationCommon(3);
	static {
		loc.setApp(new AppCommon(loc, new AwtFactoryCommon()));
	}

	/**
	 * @param cmdName
	 *            command name
	 * @return numbers of arguments per syntax
	 */
	public static List<Integer> getSigneture(String cmdName) {
		if ("ExportImage".equals(cmdName)) {
			return Arrays.asList(0, 2, 4, 6);
		}
		String syntax = loc.getCommand(cmdName + Localization.syntaxStr);
		if (!syntax.contains(Localization.syntaxStr)) {
			ArrayList<Integer> signature = new ArrayList<>();
			for (String line : syntax.split("\n")) {
				if ("[]".equals(line) || "[ ]".equals(line)) {
					signature.add(0);
				} else {
					signature.add(line.replace("x, y", "xy").split(",").length);
				}
			}
			if ("Function".equals(cmdName) || "Random".equals(cmdName)
					|| "DataFunction".equals(cmdName)
					|| "ZoomIn".equals(cmdName)
					|| "StartLogging".equals(cmdName)) {
				signature.add(0);
			}
			return signature;
		}
		return null;
	}
}
