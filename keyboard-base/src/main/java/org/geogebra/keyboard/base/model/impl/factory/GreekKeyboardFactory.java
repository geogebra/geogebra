package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.model.KeyboardModel;

class GreekKeyboardFactory extends LetterKeyboardFactory {

    private static final String ALPHA = "\u03B1";
    private static final String BETA = "\u03B2";
    private static final String GAMMA = "\u03B3";
    private static final String DELTA = "\u03B4";
    private static final String EPSILON = "\u03B5";
    private static final String ZETA = "\u03B6";
    private static final String ETA = "\u03B7";
    private static final String THETA = "\u03B8";
    private static final String IOTA = "\u03B9";
    private static final String KAPPA = "\u03BA";
    private static final String LAMBDA = "\u03BB";
    private static final String MU = "\u03BC";
    private static final String NU = "\u03BD";
    private static final String XI = "\u03BE";
    private static final String OMICRON = "\u03BF";
    private static final String PI = "\u03C0";
    private static final String RHO = "\u03C1";
    private static final String SIGMA_SPECIAL = "\u03C2";
    private static final String SIGMA = "\u03C3";
    private static final String TAU = "\u03C4";
    private static final String UPSILON = "\u03C5";
    private static final String PHI = "\u03D5";
    private static final String PHI_VARIATION = "\u03C6";
    private static final String CHI = "\u03C7";
    private static final String PSI = "\u03C8";
    private static final String OMEGA = "\u03C9";

    KeyboardModel createGreekKeyboard(ButtonFactory buttonFactory) {
        StringBuilder topRow = new StringBuilder();
        topRow.append(PHI);
        topRow.append(SIGMA_SPECIAL);
        topRow.append(EPSILON);
        topRow.append(RHO);
        topRow.append(TAU);
        topRow.append(UPSILON);
        topRow.append(THETA);
        topRow.append(IOTA);
        topRow.append(OMICRON);
        topRow.append(PI);

        StringBuilder middleRow = new StringBuilder();
        middleRow.append(ALPHA);
        middleRow.append(SIGMA);
        middleRow.append(DELTA);
        middleRow.append(PHI_VARIATION);
        middleRow.append(GAMMA);
        middleRow.append(ETA);
        middleRow.append(XI);
        middleRow.append(KAPPA);
        middleRow.append(LAMBDA);

        StringBuilder bottomRow = new StringBuilder();
        bottomRow.append(ZETA);
        bottomRow.append(CHI);
        bottomRow.append(PSI);
        bottomRow.append(OMEGA);
        bottomRow.append(BETA);
        bottomRow.append(NU);
        bottomRow.append(MU);

        return createLetterKeyboard(buttonFactory, topRow.toString(), middleRow.toString(), bottomRow.toString());
    }
}
