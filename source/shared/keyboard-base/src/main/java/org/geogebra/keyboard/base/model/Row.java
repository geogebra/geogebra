package org.geogebra.keyboard.base.model;

import java.util.List;

/**
 * Describes a row in a liner keyboard. It is specified by a list of weighted buttons.
 */
public interface Row {

    /**
     * The sum weight of the row. Used to calculate the width of a weighted button,
     * relative to the total width.<p>
     * <p>
     * The rowWeightSum should match with the sum of the weight of the buttons.
     * In case they don't match, the functionality is unspecified.
     *
     * @return the weight of the row.
     */
    float getRowWeightSum();

    /**
     * List of the weighted buttons.
     *
     * @return list of buttons.
     */
    List<WeightedButton> getButtons();
}
