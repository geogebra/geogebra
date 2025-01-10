package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.Button;

/**
 * Describes a weighted button.
 */
public interface WeightedButton extends Button {

    /**
     * Returns the weight. Used to calculate the width of the button relative to the
     * total width of the row. See {@link Row#getRowWeightSum()}.
     *
     * @return weight
     */
    float getWeight();

	String getAltText();
}
