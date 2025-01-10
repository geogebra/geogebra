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
