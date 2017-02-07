package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.LinearKeyboardImpl;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import static org.geogebra.keyboard.base.ButtonConstants.ACTION_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_CAPS_LOCK;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_LEFT;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RIGHT;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_CAPS_LOCK;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LEFT_ARROW;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RIGHT_ARROW;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.createEmptySpace;


public class GreekKeyboardFactory {

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
    private static final String OMICORN = "\u03BF";
    private static final String PI = "\u03C0";
    private static final String RHO = "\u03C1";
    private static final String SIGMA_SPECIAL = "\u03C2";
    private static final String SIGMA = "\u03C3";
    private static final String TAU = "\u03C4";
    private static final String UPSILON = "\u03C5";
    private static final String PHI = "\u03C6";
    private static final String PHI_VARIATION = "\u03D5";
    private static final String CHI = "\u03C7";
    private static final String PSI = "\u03C8";
    private static final String OMEGA = "\u03C9";

    public LinearKeyboard createGreekKeyboard() {
        LinearKeyboardImpl greekKeyboard = new LinearKeyboardImpl();

        RowImpl row = greekKeyboard.nextRow(10.0f);
        addInputButton(row, PHI_VARIATION);
        addInputButton(row, SIGMA_SPECIAL);
        addInputButton(row, EPSILON);
        addInputButton(row, RHO);
        addInputButton(row, TAU);
        addInputButton(row, UPSILON);
        addInputButton(row, THETA);
        addInputButton(row, IOTA);
        addInputButton(row, OMICORN);
        addInputButton(row, PI);

        row = greekKeyboard.nextRow(10.0f);
        addButton(row, createEmptySpace(0.5f));
        addInputButton(row, ALPHA);
        addInputButton(row, SIGMA);
        addInputButton(row, DELTA);
        addInputButton(row, PHI);
        addInputButton(row, GAMMA);
        addInputButton(row, ETA);
        addInputButton(row, XI);
        addInputButton(row, KAPPA);
        addInputButton(row, LAMBDA);
        addButton(row, createEmptySpace(0.5f));

        row = greekKeyboard.nextRow(10.0f);
        addConstantCustomButton(row, RESOURCE_CAPS_LOCK, ACTION_CAPS_LOCK);
        addButton(row, createEmptySpace(0.5f));
        addInputButton(row, ZETA);
        addInputButton(row, CHI);
        addInputButton(row, PSI);
        addInputButton(row, OMEGA);
        addInputButton(row, BETA);
        addInputButton(row, NU);
        addInputButton(row, MU);
        addButton(row, createEmptySpace(0.5f));
        addConstantCustomButton(row, RESOURCE_BACKSPACE, ACTION_BACKSPACE);

        row = greekKeyboard.nextRow(10.0f);
        addInputButton(row, ",");
        addInputButton(row, "'");
        addInputButton(row, " ", 5.0f);
        addConstantCustomButton(row, RESOURCE_LEFT_ARROW, ACTION_LEFT);
        addConstantCustomButton(row, RESOURCE_RIGHT_ARROW, ACTION_RIGHT);
        addConstantCustomButton(row, RESOURCE_RETURN, ACTION_RETURN);

        return greekKeyboard;
    }
}
