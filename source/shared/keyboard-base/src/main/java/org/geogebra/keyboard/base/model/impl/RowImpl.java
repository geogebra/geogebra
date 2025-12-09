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

package org.geogebra.keyboard.base.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;

public class RowImpl implements Row {

    private List<WeightedButton> buttons = new ArrayList<>();

    /**
     * Add a button to the row.
     * @param button button to add
     */
    public void addButton(WeightedButton button) {
        buttons.add(button);
    }

    @Override
    public float getRowWeightSum() {
        float sumOfButtonWeights = 0;

        for (WeightedButton button : buttons) {
            sumOfButtonWeights += button.getWeight();
        }

        return sumOfButtonWeights;
    }

    @Override
    public List<WeightedButton> getButtons() {
        return buttons;
    }
}
