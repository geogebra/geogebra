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

package org.geogebra.common.gui.popup.specialpoint;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class SpecialPointPopupHelper {

    /**
     * Returns the list of Strings that represent the rows of a popup for preview points.
     *
     * @param app the application
     * @param previewPoints the preview points for which the popup is created
     *
     * @return a list of Strings that represent the content for a popup
     */
    public static List<String> getContentRows(App app, List<GeoElement> previewPoints) {
        List<String> contentRows = new ArrayList<>();
        GeoElement point = previewPoints.get(0);
        for (GeoElement geo : previewPoints) {
            AlgoElement parentAlgorithm = geo.getParentAlgorithm();
            if (parentAlgorithm != null && geo.isEqual(point)) {
                contentRows.add(getContentRow(app, parentAlgorithm));
            }
        }
        contentRows.add(point.getAlgebraDescriptionRHS());
        return contentRows;
    }

    private static String getContentRow(App app, AlgoElement parentAlgo) {
        GetCommand cmd = parentAlgo.getClassName();
        Localization localization = app.getLocalization();
        if (cmd == Commands.Intersect) {
            if (parentAlgo instanceof AlgoIntersectAbstract) {
                AlgoIntersectAbstract intersectAbstract = (AlgoIntersectAbstract) parentAlgo;
                Kernel kernel = app.getKernel();
                GeoElement xAxis = kernel.getXAxis();
                GeoElement yAxis = kernel.getYAxis();
                for (GeoElement input : intersectAbstract.getInput()) {
                    if (input == yAxis) {
                        return localization.getMenu("yIntercept");
                    } else if (input == xAxis) {
                        return localization.getMenu("Root");
                    }
                }
            }
            return localization.getMenu("Intersect.tooltip");
        } else if (cmd == Commands.Roots) {
            return localization.getMenu("Root");
        } else if (cmd == Commands.Extremum) {
            return localization.getMenu("Extremum.tooltip");
        } else if (cmd == Commands.RemovableDiscontinuity) {
            return localization.getMenu("RemovableDiscontinuity");
        }
        return localization.getCommand(cmd.getCommand());
    }
}
