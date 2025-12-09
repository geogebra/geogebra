/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
