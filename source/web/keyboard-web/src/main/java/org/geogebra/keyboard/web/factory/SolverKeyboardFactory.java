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

package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.CharacterProvider;
import org.geogebra.keyboard.base.model.impl.factory.DefaultCharProvider;
import org.geogebra.keyboard.web.factory.model.solver.SolverDefaultKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverSpecialSymbolsKeyboardFactory;

public final class SolverKeyboardFactory extends DefaultKeyboardFactory {

	/**
	 * Keyboard layout for solver
	 */
	public SolverKeyboardFactory() {
		this(new DefaultCharProvider());
	}

	/**
	 * Keyboard layout for solver
	 * @param charProvider - character provider
	 */
	public SolverKeyboardFactory(CharacterProvider charProvider) {
		super(new DefaultCharProvider(), null);
		defaultKeyboardModelFactory = new SolverDefaultKeyboardFactory(charProvider);
		mathKeyboardFactory = new SolverDefaultKeyboardFactory(charProvider);
		functionKeyboardFactory = new SolverFunctionKeyboardFactory();
		specialSymbolsKeyboardFactory = new SolverSpecialSymbolsKeyboardFactory();
	}
}
