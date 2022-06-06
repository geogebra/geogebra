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
			if (geo.getParentAlgorithm() != null && geo.isEqual(point)) {
                contentRows.add(getContentRow(app, geo));
            }
        }
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
			return localization.getCommand("Intersect");
        } else if (cmd == Commands.Roots) {
            return localization.getCommand("Root");
        } else if (cmd == Commands.RemovableDiscontinuity) {
            return localization.getMenu("RemovableDiscontinuity");
        }
        return localization.getCommand(cmd.getCommand());
    }
}
