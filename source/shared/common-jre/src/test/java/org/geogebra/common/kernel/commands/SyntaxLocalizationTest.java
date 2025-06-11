package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Test;

public class SyntaxLocalizationTest {

	@Test
	public void checkSyntaxTranslations() {
		AppCommon app = AppCommonFactory.create3D();

		for (Commands cmd : Commands.values()) {
			List<Integer> signature = CommandSignatures
					.getSignature(cmd.name(), app);
			int size = signature == null ? 0 : signature.size();
			if (cmd.getTable() == CommandsConstants.TABLE_CAS
					|| cmd == Commands.Polyhedron || cmd == Commands.CSolutions
					|| cmd == Commands.CSolve || cmd == Commands.Stadium) {
				continue;
			}
			if (cmd.getTable() == CommandsConstants.TABLE_ENGLISH) {
				assertNull(cmd.name() + " needs no syntax", signature);
				continue;
			}

			assertNotEquals(cmd.name() + " has no syntax", 0, size);
		}
	}
}
