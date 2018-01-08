package org.geogebra.common.gui.popup.specialpoint;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

import java.util.ArrayList;
import java.util.List;

public class SpecialPointPopupHelper {

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static List<String> getContentRows(App app, List<GeoElement> previewPoints) {
        List<String> contentRows = new ArrayList<>();
        for (GeoElement geo : previewPoints) {
            if (geo.getParentAlgorithm() != null) {
                GetCommand cmd = geo.getParentAlgorithm().getClassName();
                String text;
                if (cmd == Commands.Intersect) {
                    text = app.getLocalization().getMenu("yIntercept");
                } else if (cmd == Commands.Roots) {
                    text = app.getLocalization().getCommand("Root");
                } else {
                    text = app.getLocalization().getCommand(cmd.getCommand());
                }
                contentRows.add(text);
            }
        }
        GeoElement point = previewPoints.get(0);
        contentRows.add(point.getAlgebraDescriptionRHS());
        return contentRows;
    }
}
