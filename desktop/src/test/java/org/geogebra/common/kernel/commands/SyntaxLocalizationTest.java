package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxLocalizationTest {

	@Test
	public void checkSyntaxTranslations() {
		LocalizationD loc = new LocalizationD(3);
		loc.setApp(new AppCommon(loc, new AwtFactoryD()));

		for (Commands cmd : Commands.values()) {
			String syntax = loc.getCommand(cmd + Localization.syntaxStr);
			if (!syntax.contains(Localization.syntaxStr)
					&& !cmd.name().equals("ExportImage")) {
				ArrayList<Integer> signature = new ArrayList<>();
				for (String line : syntax.split("\n")) {
					if ("[]".equals(line) || "[ ]".equals(line)) {
						signature.add(0);
					} else {
						signature.add(
								line.replace("x, y", "xy").split(",").length);
					}
				}
				Assert.assertArrayEquals(cmd.name(), signature.toArray(),
						CommandSignatures.getSigneture(cmd.name()).toArray());

			}

		}
	}
}
