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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Test;

public class SelfTest {
	@Test
	public void selfTest() {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), false);
		Set<String> methodNames = new TreeSet<>();
		Class<?>[] classes = new Class[]{CommandsTestCommon.class, CommandsUsingCASTest.class,
				ProveCommandTest.class};
		for (Class<?> c : classes) {
			Method[] mtds = c.getMethods();
			for (int i = 0; i < mtds.length; i++) {
				methodNames.add(mtds[i].getName());
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
