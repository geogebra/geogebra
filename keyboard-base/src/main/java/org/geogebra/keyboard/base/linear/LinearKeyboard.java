package org.geogebra.keyboard.base.linear;

import java.util.List;

/**
 * Describes a keyboard that consists of rows of buttons.<p>
 * The rows can contain any number of buttons, and the number of buttons
 * can also differ for each row.
 */
public interface LinearKeyboard {

    /**
     * Get the rows that this keyboard consists of.
     *
     * @return list of rows
     */
    List<Row> getRows();
}
