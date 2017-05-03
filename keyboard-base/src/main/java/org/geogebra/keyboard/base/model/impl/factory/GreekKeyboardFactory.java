package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.model.KeyboardModel;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.*;

class GreekKeyboardFactory extends LetterKeyboardFactory {

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

        return createLetterKeyboard(buttonFactory, topRow.toString(), middleRow.toString(), bottomRow.toString(), false);
    }
}
