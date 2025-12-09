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

package org.geogebra.keyboard.base.model;

import static org.junit.Assert.*;

import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.WeightedButtonImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.junit.Test;

public class KeyboardBaseTest {
    private final double precision = 1.0E-6;

    @Test
    public void testRowWeights() {
        ButtonFactory unmodifiedButtonFactory = new ButtonFactory(null);
        ButtonFactory accentButtonFactory = new ButtonFactory(
                new KeyModifier[]{new AccentModifier()}
        );
        ButtonFactory capsLockButtonFactory = new ButtonFactory(
                new KeyModifier[]{new CapsLockModifier()}
        );

        WeightedButton[] buttons = new WeightedButtonImpl[3];
        buttons[0] = unmodifiedButtonFactory.createInputButton("test", "t", "t");
        buttons[1] = accentButtonFactory.createInputButton("a", "a", "a");
        buttons[2] = capsLockButtonFactory.createInputButton("b", "b", "b");

        RowImpl row = new RowImpl();
        row.addButton(buttons[0]);
        row.addButton(buttons[1]);
        row.addButton(buttons[2]);

        assertEquals(
                buttons[0].getWeight() + buttons[1].getWeight() + buttons[2].getWeight(),
                row.getRowWeightSum(),
                precision
        );
    }
}
