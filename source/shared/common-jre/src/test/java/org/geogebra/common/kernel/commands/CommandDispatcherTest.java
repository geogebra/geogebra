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

import static org.geogebra.common.kernel.commands.Commands.Evaluate;
import static org.geogebra.common.kernel.commands.Commands.ImplicitSurface;
import static org.geogebra.common.kernel.commands.Commands.Polyhedron;
import static org.geogebra.common.kernel.commands.Commands.SolveQuartic;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandDispatcherTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupClassicApp();
	}

	@Test
	void testAllCommandsInSwitch() {
		CommandDispatcher dispatcher = new CommandDispatcher3DJre(getKernel());
		List<Commands> undef = Arrays.stream(Commands.values()).filter(cmd ->
			dispatcher.commandTableSwitch(
					new Command(getKernel(), cmd.name(), false)) == null).toList();
		// Evaluate is an internal CAS command, the others are unreleased
		assertEquals(List.of(ImplicitSurface, Polyhedron, Evaluate, SolveQuartic), undef);
	}

}
