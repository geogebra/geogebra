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

package org.geogebra.keyboard.base.model.impl.factory;

public class GreekKeyboardFactory extends LetterKeyboardFactory {

	/**
	 * Creates a GreekKeyboardFactory.
	 */
	public GreekKeyboardFactory() {
		super();
		initializeDefinition();
	}

	private void initializeDefinition() {
		String bottomRow = ""
				+ Characters.ZETA
				+ Characters.CHI
				+ Characters.PSI
				+ Characters.OMEGA
				+ Characters.BETA
				+ Characters.NU
				+ Characters.MU;
		String middleRow = ""
				+ Characters.ALPHA
				+ Characters.SIGMA
				+ Characters.DELTA
				+ Characters.PHI_VARIATION
				+ Characters.GAMMA
				+ Characters.ETA
				+ Characters.XI
				+ Characters.KAPPA
				+ Characters.LAMBDA;
		String topRow = ""
				+ Characters.PHI
				+ Characters.SIGMA_SPECIAL
				+ Characters.EPSILON
				+ Characters.RHO
				+ Characters.TAU
				+ Characters.UPSILON
				+ Characters.THETA
				+ Characters.IOTA
				+ Characters.OMICRON
				+ Characters.PI_CHAR;
		setKeyboardDefinition(topRow, middleRow, bottomRow, DEFAULT_CONTROL,
				ACTION_SHIFT, ACTION_ABC_LETTERS);
	}
}
