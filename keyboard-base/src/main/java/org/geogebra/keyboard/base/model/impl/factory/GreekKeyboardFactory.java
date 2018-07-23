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
        StringBuilder topRow = new StringBuilder();
		topRow.append(Characters.PHI);
		topRow.append(Characters.SIGMA_SPECIAL);
		topRow.append(Characters.EPSILON);
		topRow.append(Characters.RHO);
		topRow.append(Characters.TAU);
		topRow.append(Characters.UPSILON);
		topRow.append(Characters.THETA);
		topRow.append(Characters.IOTA);
		topRow.append(Characters.OMICRON);
		topRow.append(Characters.PI);

        StringBuilder middleRow = new StringBuilder();
		middleRow.append(Characters.ALPHA);
		middleRow.append(Characters.SIGMA);
		middleRow.append(Characters.DELTA);
		middleRow.append(Characters.PHI_VARIATION);
		middleRow.append(Characters.GAMMA);
		middleRow.append(Characters.ETA);
		middleRow.append(Characters.XI);
		middleRow.append(Characters.KAPPA);
		middleRow.append(Characters.LAMBDA);

        StringBuilder bottomRow = new StringBuilder();
		bottomRow.append(Characters.ZETA);
		bottomRow.append(Characters.CHI);
		bottomRow.append(Characters.PSI);
		bottomRow.append(Characters.OMEGA);
		bottomRow.append(Characters.BETA);
		bottomRow.append(Characters.NU);
		bottomRow.append(Characters.MU);

		setKeyboardDefinition(topRow.toString(),
				middleRow.toString(), bottomRow.toString(), false);
    }
}
