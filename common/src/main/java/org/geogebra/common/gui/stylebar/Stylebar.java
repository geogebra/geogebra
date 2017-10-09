package org.geogebra.common.gui.stylebar;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;

import java.util.Collections;
import java.util.List;

public class Stylebar {

    private EuclidianView euclidianView;
    private SelectionManager selectionManager;

    public Stylebar(EuclidianView euclidianView, SelectionManager selectionManager) {
        this.euclidianView = euclidianView;
        this.selectionManager = selectionManager;
    }

    public List<GeoElement> createActiveGeoList() {
        boolean hasGeosInThisView = false;
        for (GeoElement geo : selectionManager.getSelectedGeos()) {
            if (geo.isVisibleInView(euclidianView.getViewID()) && geo.isEuclidianVisible()
                    && !geo.isAxis()) {
                hasGeosInThisView = true;
                break;
            }
        }

        EuclidianController euclidianController = euclidianView.getEuclidianController();
        for (GeoElement geo : euclidianController.getJustCreatedGeos()) {
            if (geo.isVisibleInView(euclidianView.getViewID()) && geo.isEuclidianVisible()) {
                hasGeosInThisView = true;
                break;
            }
        }

        List<GeoElement> activeGeoList;
        if (hasGeosInThisView) {
            activeGeoList = selectionManager.getSelectedGeos();

            // we also update stylebars according to just created geos
            activeGeoList.addAll(euclidianController.getJustCreatedGeos());
        } else {
            activeGeoList = Collections.emptyList();
        }

        return activeGeoList;
    }
}
