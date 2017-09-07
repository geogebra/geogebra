package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EuclidianStyleConstants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents the model for the object properties in Android and iOS
 */

abstract public class ObjectSettingsModel {

    private final int MAX_SIZE = 9;
    protected App mApp;

    protected GeoElement mGeoElement;
    protected ArrayList<GeoElement> mGeoElementsList;

    public ObjectSettingsModel(App app) {
        this.mApp = app;
        setGeoElement();
    }

    protected void setGeoElement() {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            mGeoElementsList = new ArrayList<GeoElement>();
        }
    }

    public GColor getColor() {
        if (mGeoElement == null) {
            return GColor.BLACK;
        }
        return mGeoElement.getObjectColor();
    }

    public void setColor(GColor color) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            if (!hasFurtherStyle()) {
                EuclidianStyleBarStatic.applyTextColor(mGeoElementsList, color);
            } else {
                EuclidianStyleBarStatic.applyColor(mGeoElementsList, color, mGeoElement.getAlphaValue(), mApp);
            }
        } else {
            if (!hasFurtherStyle()) {
                EuclidianStyleBarStatic.applyTextColor(Arrays.asList(mGeoElement), color);
            } else {
                EuclidianStyleBarStatic.applyColor(Arrays.asList(mGeoElement), color, mGeoElement.getAlphaValue(), mApp);
            }
        }
        mApp.setPropertiesOccured();
    }

    public boolean isLabelShown() {
        if (mGeoElement == null) {
            return false;
        }
        return mGeoElement.isLabelVisible();
    }

    public void showLabel(boolean show) {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                geo.setLabelVisible(show);
                geo.updateRepaint();
            }
        } else {
            mGeoElement.setLabelVisible(show);
            mGeoElement.updateRepaint();
        }
    }

    public int getLabelStyle() {
        if (mGeoElement == null) {
            return GeoElement.LABEL_NAME;
        }
        return mGeoElement.getLabelMode();
    }

    public void setLabelStyle(int mode) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                geo.setLabelMode(mode);
            }
        } else {
            mGeoElement.setLabelMode(mode);
        }
        showLabel(true);
        mApp.setPropertiesOccured();
    }

    public String getLabelString() {

        if (mGeoElement == null) {
            return "";
        }

        if (!mGeoElement.isLabelVisible()) {
            return App.getLabelStyleName(mApp, -1);
        }

        return App.getLabelStyleName(mApp, mGeoElement.getLabelMode());
    }

    public int getLineStyle() {
        if (mGeoElement == null) {
            return EuclidianStyleConstants.DEFAULT_LINE_TYPE;
        }
        return mGeoElement.getLineType();
    }

    public void setLineStyle(int style) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                geo.setLineType(style);
                geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
            }
        } else {
            mGeoElement.setLineType(style);
            mGeoElement.updateVisualStyleRepaint(GProperty.LINE_STYLE);
        }
        mApp.setPropertiesOccured();
    }

    public int getPointStyle() {
        if (mGeoElement == null) {
            return EuclidianStyleConstants.POINT_STYLE_DOT;
        }

        if (mGeoElement instanceof PointProperties) {
            return ((PointProperties) mGeoElement).getPointStyle();
        }

        return EuclidianStyleConstants.POINT_STYLE_DOT;
    }

    public void setPointStyle(int pointStyle) {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                ((PointProperties) geo).setPointStyle(pointStyle);
                geo.updateVisualStyleRepaint(GProperty.POINT_STYLE);
            }
        } else {
            ((PointProperties) mGeoElement).setPointStyle(pointStyle);
            mGeoElement.updateVisualStyleRepaint(GProperty.POINT_STYLE);
        }
        mApp.setPropertiesOccured();
    }

    public int getSize() {

        if (mGeoElement == null) {
            return EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
        }

        if (mGeoElement instanceof PointProperties) {
            return ((PointProperties) mGeoElement).getPointSize();
        }
        return mGeoElement.getLineThickness();
    }

    /**
     * @param value
     */
    public void setSize(int value) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (geo instanceof PointProperties) {
                    ((PointProperties) geo).setPointSize(value + 1);
                } else {
                    geo.setLineThickness(value + getMinSize());
                }
                geo.updateRepaint();
            }
        } else {
            if (mGeoElement instanceof PointProperties) {
                ((PointProperties) mGeoElement).setPointSize(value + 1);
            } else {
                mGeoElement.setLineThickness(value + getMinSize());
            }
            mGeoElement.updateRepaint();
        }
        mApp.setPropertiesOccured();
    }

    public int getMinSize() {
        if (mGeoElement == null) {
            return 1;
        }
        if (mGeoElement instanceof PointProperties) {
            return 1;
        }
        return mGeoElement.getMinimumLineThickness();
    }

    public int getMaxSize() {
        return MAX_SIZE;
    }

    public boolean getObjectFixed() {
        if (mGeoElement == null) {
            return false;
        }
        return mGeoElement.isLocked();
    }

    public void setObjectFixed(boolean objectFixed) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                geo.setFixed(objectFixed);
            }
        } else {
            mGeoElement.setFixed(objectFixed);
        }
        mApp.setPropertiesOccured();
    }

    public void rename(String name) {
        mGeoElement.rename(name);
    }

    public String getNameDescription() {
        if (mGeoElement == null) {
            return "";
        }
        return mGeoElement.getNameDescription();
    }

    public String getName() {
        if (mGeoElement == null) {
            return "";
        }
        return mGeoElement.getLabelSimple();
    }

    public double getSliderMin() {
        if (mGeoElement == null) {
            return -5;
        }
        return ((GeoNumeric) mGeoElement).getIntervalMin();
    }

    public void setSliderMin(String min) {
        if (mGeoElement == null) {
            return;
        }
        GeoNumberValue num = mApp.getKernel().getAlgebraProcessor().evaluateToNumeric(min, false);
        if (num == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                ((GeoNumeric) geo).setIntervalMin(num);
            }
        } else {
            ((GeoNumeric) mGeoElement).setIntervalMin(num);
        }
        mApp.setPropertiesOccured();
    }

    public double getSliderMax() {
        if (mGeoElement == null) {
            return 5;
        }
        return ((GeoNumeric) mGeoElement).getIntervalMax();
    }

    public void setSliderMax(String max) {
        if (mGeoElement == null) {
            return;
        }
        GeoNumberValue num = mApp.getKernel().getAlgebraProcessor().evaluateToNumeric(max, false);
        if (num == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                ((GeoNumeric) geo).setIntervalMax(num);
            }
        } else {
            ((GeoNumeric) mGeoElement).setIntervalMax(num);
        }
        mApp.setPropertiesOccured();
    }

    public double getSliderIncrement() {
        if (mGeoElement == null) {
            return 0.1;
        }
        return mGeoElement.getAnimationStep();
    }

    public void setSliderIncrement(String increment) {
        if (mGeoElement == null) {
            return;
        }
        double step = mApp.getKernel().getAlgebraProcessor().evaluateToDouble(increment);
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                geo.setAnimationStep(step);
            }
        } else {
            mGeoElement.setAnimationStep(step);
        }
        mApp.setPropertiesOccured();
    }

    public float getAlpha() {
        if (mGeoElement == null) {
            return 1;
        }
        return (float) mGeoElement.getAlphaValue();
    }

    public void setAlpha(float alpha) {
        if (mGeoElement == null) {
            return;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            EuclidianStyleBarStatic.applyColor(mGeoElementsList, mGeoElement.getObjectColor(), alpha, mApp);
        } else {
            EuclidianStyleBarStatic.applyColor(Arrays.asList(mGeoElement), mGeoElement.getObjectColor(), alpha, mApp);
        }
        mApp.setPropertiesOccured();
    }

    public boolean isFillable() {
        if (mGeoElement == null) {
            return false;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!geo.isFillable()) {
                    return false;
                }
            }
            return true;
        }
        return mGeoElement.isFillable();
    }

    public boolean isObjectShown() {
        if (mGeoElement == null) {
            return false;
        }
        return mGeoElement.isEuclidianVisible();
    }

    public void showObject(boolean checked) {
        if (mGeoElement != null) {
            if (mApp.has(Feature.MOB_SELECT_TOOL)) {
                for (GeoElement geo : mGeoElementsList) {
                    geo.setEuclidianVisible(checked);
                    geo.updateRepaint();
                }
            } else {
                mGeoElement.setEuclidianVisible(checked);
                mGeoElement.updateRepaint();
            }
        }
    }

    public String getLineStyleName(int id) {
        switch (id) {
            case EuclidianStyleConstants.LINE_TYPE_FULL:
                return "solid";
            case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
                return "dash_dot";
            case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
                return "dashed";
            case EuclidianStyleConstants.LINE_TYPE_DOTTED:
                return "dotted";
            default:
                return "";
        }
    }

    public String getPointStyleName(int id) {
        switch (id) {
            case EuclidianStyleConstants.POINT_STYLE_DOT:
                return "point";
            case EuclidianStyleConstants.POINT_STYLE_CROSS:
                return "cross";
            case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
                return "ring";
            case EuclidianStyleConstants.POINT_STYLE_PLUS:
                return "plus";
            case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
                return "diamond";
            default:
                return "";
        }
    }

    public boolean hasFurtherStyle() {
        if (mGeoElement == null) {
            return false;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!hasFurtherStyle(geo)) {
                    return false;
                }
            }
            return true;
        }
        return hasFurtherStyle(mGeoElement);
    }

    private boolean hasFurtherStyle(GeoElement geo) {
        return !(geo instanceof GeoText || geo instanceof GeoInputBox);
    }

    public boolean isTraceOn() {
        if (mGeoElement == null) {
            return false;
        }
        return mGeoElement.getTrace();
    }

    public boolean isTraceable() {
        if (mGeoElement == null) {
            return false;
        }
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!geo.isTraceable()) {
                    return false;
                }
            }
            return true;
        }
        return mGeoElement.isTraceable();
    }

    public void setTrace(boolean checked) {
        if (mGeoElement != null && mGeoElement.isTraceable()) {
            if (mApp.has(Feature.MOB_SELECT_TOOL)) {
                for (GeoElement geo : mGeoElementsList) {
                    ((Traceable) geo).setTrace(checked);
                }
            } else {
                ((Traceable) mGeoElement).setTrace(checked);
            }
        }
    }

    public boolean hasSliderProperties() {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!hasSliderProperties(geo)) {
                    return false;
                }
            }
            return true;
        }
        return hasSliderProperties(mGeoElement);
    }

    public boolean hasSliderProperties(GeoElement geo) {
        return geo instanceof GeoNumeric
                && ((GeoNumeric) geo).getIntervalMinObject() != null
                && geo.isIndependent();
    }

    public boolean hasPointProperties() {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!(geo instanceof PointProperties)) {
                    return false;
                }
            }
            return true;
        }
        return mGeoElement instanceof PointProperties;
    }


    public boolean hasLineProperties() {
        if (mApp.has(Feature.MOB_SELECT_TOOL)) {
            for (GeoElement geo : mGeoElementsList) {
                if (!LineStyleModel.match(geo)) {
                    return false;
                }
            }
            return true;
        }
        return LineStyleModel.match(mGeoElement);
    }
}
