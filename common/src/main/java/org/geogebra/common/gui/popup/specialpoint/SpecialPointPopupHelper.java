package org.geogebra.common.gui.popup.specialpoint;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

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
        for (GeoElement geo : previewPoints) {
            if (geo.getParentAlgorithm() != null) {
                contentRows.add(getContentRow(app, geo));
            }
        }
        GeoElement point = previewPoints.get(0);
        contentRows.add(point.getAlgebraDescriptionRHS());
        return contentRows;
    }

    private static String getContentRow(App app, GeoElement geo) {
        AlgoElement parentAlgo = geo.getParentAlgorithm();
        GetCommand cmd = parentAlgo.getClassName();
        Localization localization = app.getLocalization();
        if (cmd == Commands.Intersect) {
            if (parentAlgo instanceof AlgoIntersectAbstract) {
                AlgoIntersectAbstract intersectAbstract = (AlgoIntersectAbstract) parentAlgo;
                for (GeoElement input: intersectAbstract.getInput()) {
                    if (input == app.getKernel().getYAxis()) {
                        return localization.getMenu("yIntercept");
                    }
                }
            }
            return localization.getMenu("Root");
        } else if (cmd == Commands.Roots) {
            return localization.getCommand("Root");
        }
        return localization.getCommand(cmd.getCommand());
    }
}
