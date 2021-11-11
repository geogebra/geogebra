package org.geogebra.common.kernel.commands;

import java.util.List;
import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxLocalizationTest {

	@Test
	public void checkSyntaxTranslations() {
		AppCommon app = AppCommonFactory.create3D();

		for (Commands cmd : Commands.values()) {
			if (cmd == Commands.ShowSteps
					|| cmd.getTable() == CommandsConstants.TABLE_ENGLISH
					|| cmd.getTable() == CommandsConstants.TABLE_CAS
					|| cmd.name().toLowerCase(Locale.US).equals(cmd.name())
					|| NoExceptionsTest.betaCommand(cmd, app)) {
				continue;
			}
			List<Integer> signature = CommandSignatures
					.getSigneture(cmd.name(), app);
			int size = signature == null ? 0 : signature.size();
			Assert.assertNotEquals(cmd.name() + " has no syntax", 0, size);
		}
	}
}
