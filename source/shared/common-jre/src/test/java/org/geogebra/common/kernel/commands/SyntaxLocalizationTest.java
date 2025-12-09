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
					|| cmd == Commands.CSolve) {
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
