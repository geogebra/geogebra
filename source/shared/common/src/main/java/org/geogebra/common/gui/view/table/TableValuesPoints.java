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

package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.algos.AlgoDependentListExpression;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Creates points according to the table view model.
 */
public interface TableValuesPoints {

    /**
     * Returns true if points are visible for this evaluatable.
     *
     * @param column column
     * @return true iff points are visible
     */
    boolean arePointsVisible(int column);

    /**
     * Sets if points should be visible for this evaluatable
     *
     * @param column column
     * @param visible visibility
     */
    void setPointsVisible(int column, boolean visible);

    /**
     * Remove all point lists.
     */
    void clear();

    /**
     * Add point list if relevant
     * @param evaluatable point list
     */
    void notifyPointsAdded(AlgoDependentListExpression evaluatable);

    /**
     * Remove a point list.
     * @param element list of points
     */
    void removeList(GeoElement element);

    /**
     * Create a list of points for a column.
     * @param evaluatable evaluatable
     */
    void createPointsIfNeeded(GeoEvaluatable evaluatable);
}
