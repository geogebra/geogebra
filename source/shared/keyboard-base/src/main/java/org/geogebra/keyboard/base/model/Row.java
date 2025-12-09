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
