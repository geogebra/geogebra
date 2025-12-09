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

package org.geogebra.keyboard.scientific.factory;

import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificDefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;

public final class ScientificKeyboardFactory extends DefaultKeyboardFactory {

	/**
	 * Creates a ScientificKeyboardFactory with default implementations
	 * for keyboard model factories.
	 */
	public ScientificKeyboardFactory() {
		mathKeyboardFactory = new ScientificDefaultKeyboardFactory(true);
		defaultKeyboardModelFactory = new ScientificDefaultKeyboardFactory(false);
		functionKeyboardFactory = new ScientificFunctionKeyboardFactory();
		letterKeyboardFactory = new ScientificLettersKeyboardFactory();
	}
}
