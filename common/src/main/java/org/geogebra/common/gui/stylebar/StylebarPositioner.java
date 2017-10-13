package org.geogebra.common.gui.stylebar;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.SelectionManager;

import java.util.Collections;
import java.util.List;

public class StylebarPositioner {

    private App app;
    private EuclidianView euclidianView;
    private SelectionManager selectionManager;

    public StylebarPositioner(App app, EuclidianView euclidianView) {
        this.app = app;
        this.euclidianView = euclidianView;
        selectionManager = app.getSelectionManager();
    }

    private boolean hasVisibleGeos(List<GeoElement> geoList) {
        for (GeoElement geo : geoList) {
            if (geo.isVisibleInView(euclidianView.getViewID())
                    && geo.isEuclidianVisible()
                    && !geo.isAxis()) {
                return true;
            }
        }
        return false;
    }

    private List<GeoElement> createActiveGeoList() {
        List<GeoElement> selectedGeos = selectionManager.getSelectedGeos();
        List<GeoElement> justCreatedGeos = euclidianView.getEuclidianController().getJustCreatedGeos();
        if (hasVisibleGeos(selectedGeos) || hasVisibleGeos(justCreatedGeos)) {
            selectedGeos.addAll(justCreatedGeos);
            return selectionManager.getSelectedGeos();
        }
        return Collections.emptyList();
    }

    private GPoint getStylebarPositionForDrawable(
            GRectangle2D gRectangle2D,
            boolean hasBoundingBox,
            boolean isPoint,
            boolean isFunction,
            int stylebarHeight,
            int minTopPosition,
            int maxTopPosition) {

        final int MARGIN = 4;
        final int BOTTOM_MARGIN = 10 * MARGIN;
        double left, top;
        int maxTopWithMargin = maxTopPosition - 2 * MARGIN;

        if (gRectangle2D == null) {
            if (!isFunction || isPoint) {
                return null;
            }
        }

        if (isFunction) {
            GPoint mouseLoc = euclidianView.getEuclidianController()
                    .getMouseLoc();
            if (mouseLoc == null) {
                return null;
            }
            top = mouseLoc.y + MARGIN;
        } else if (isPoint) {
            top = gRectangle2D.getMaxY() + MARGIN;
        } else {
            if (hasBoundingBox) {
                top = gRectangle2D.getMinY() - stylebarHeight - BOTTOM_MARGIN;
            } else {
                top = gRectangle2D.getMinY();
            }
        }

        if (top < minTopPosition) {
            top = (gRectangle2D != null ? gRectangle2D.getMaxY() : 0) + MARGIN;
        }

        if (top > maxTopWithMargin) {
            if (isPoint) {
                top = gRectangle2D.getMinY() - stylebarHeight - BOTTOM_MARGIN;
            } else {
                top = maxTopWithMargin;
            }
        }

        if (isFunction) {
            left = euclidianView.getEuclidianController().getMouseLoc().x + MARGIN;
        } else {
            left = gRectangle2D.getMaxX();
        }

        return new GPoint((int) left, (int) top);
    }

    public GPoint getPositionOnCanvas(int stylebarHeight, int minTopPosition, int maxTopPosition) {
        List<GeoElement> activeGeoList = createActiveGeoList();

        if (activeGeoList.size() == 0) {
            return null;
        }

        if (app.has(Feature.DYNAMIC_STYLEBAR_SELECTION_TOOL)
                && app.getMode() == EuclidianConstants.MODE_SELECT) {

            return getStylebarPositionForDrawable(
                    app.getActiveEuclidianView().getSelectionRectangle(),
                    true,
                    false,
                    false,
                    stylebarHeight,
                    minTopPosition,
                    maxTopPosition);
        }

        GeoElement geo = activeGeoList.get(0);

        if (geo.isEuclidianVisible()) {
            if (app.has(Feature.FUNCTIONS_DYNAMIC_STYLEBAR_POSITION)
                    && geo instanceof GeoFunction) {
                if (euclidianView.getHits().contains(geo)) {
                    GPoint position = getStylebarPositionForDrawable(
                            null,
                            true,
                            false,
                            true,
                            stylebarHeight,
                            minTopPosition,
                            maxTopPosition);
                    if (position != null) {
                        return position;
                    }
                }
            } else {
                Drawable dr = (Drawable) euclidianView.getDrawableND(geo);
                if (dr != null) {
                    GPoint position = getStylebarPositionForDrawable(
                            dr.getBoundsForStylebarPosition(),
                            !(dr instanceof DrawLine),
                            dr instanceof DrawPoint && activeGeoList.size() < 2,
                            false,
                            stylebarHeight,
                            minTopPosition,
                            maxTopPosition);
                    if (position != null) {
                        return position;
                    }
                }
            }
        }
        return null;
    }
}
