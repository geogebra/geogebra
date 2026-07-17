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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

class SelfTest {
	@Test
	void selfTest() {
		Set<String> methodNames = new TreeSet<>();
		Class<?>[] classes = new Class[]{CommandsTest.class, CommandsUsingMockedCasTest.class};
		for (Class<?> c : classes) {
			for (Method mtd : c.getDeclaredMethods()) {
				if (mtd.getAnnotations().length > 0) {
					methodNames.add(mtd.getName());
				}
			}
		}

		StringBuilder missing = new StringBuilder();
		for (Commands a : Commands.values()) {
			if (!methodNames
					.contains("cmd" + Commands.englishToInternal(a).name())
					&& Commands.englishToInternal(a)
					.getTable() != CommandsConstants.TABLE_ENGLISH
					&& Commands.englishToInternal(a)
					.getTable() != CommandsConstants.TABLE_CAS && !(a == Commands.Polyhedron
					|| a == Commands.ImplicitSurface)) {
				missing.append(a.getCommand());
				missing.append("\n");
			}
		}
		assertEquals("", missing.toString());
	}

}
