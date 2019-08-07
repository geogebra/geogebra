package org.geogebra.arbase;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.DoubleUtil;

public class RatioManager {
    private double mARRatioAtStart;
    private double mARRatio;
    private String units = "cm";        // current units used for Ratio snack bar and ratio settings
    private String arRatioText = "1";   // current ratio used for Ratio snack bar and ratio settings
    private int ratioMetricSystem = EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS;


    public String getUnits() {
        return units;
    }

    public String getARRatioInString() {
        return arRatioText;
    }

    public void setARRatioAtStart(double arRatioAtStart) {
        mARRatioAtStart = arRatioAtStart;
        mARRatio = arRatioAtStart;
    }

    public float getARRatioAtStart() {
        return (float) mARRatioAtStart;
    }

    public int getARRatioMetricSystem() {
        return ratioMetricSystem;
    }

    public String getSnackBarText(ARGestureManager arGestureManager, App app) {
        double ratio;
        if (arGestureManager != null) {
            ratio =
                    mARRatio * arGestureManager.getScaleFactor() * getUnitConversion(app);
        } else {
            ratio = mARRatio;
        }
        String text;
        if (app.has(Feature.G3D_AR_RATIO_SETTINGS) &&
                ratioMetricSystem == EuclidianView3D.RATIO_UNIT_INCHES) {
            ratio = (double) Math.round(ratio * 100d) / 100d;
            units = "inch";
        } else {
            if (ratio >= 100) {
                // round double for precision 3 in m
                ratio = (double) Math.round(ratio) / 100d;
                units = "m";
            } else if (ratio < 0.5 ) {
                // round double for precision 3 in mm
                ratio = (double) Math.round(ratio * 1000d) / 100d;
                units = "mm";
            } else {
                // round double for precision 3 in cm
                ratio = (double) Math.round(ratio * 100d) / 100d;
                units = "cm";
            }
        }
        text = getRatioMessage(ratio);
        return text;
    }

    private String getRatioMessage(double ratio) {
        if(DoubleUtil.isInteger(ratio)) {
            arRatioText = String.format("%d", (long) ratio);
        } else {
            arRatioText = String.format("%.4s", ratio);
        }
        return String.format("1 : %s %s", arRatioText, units);
    }

    public void setARRatioMetricSystem(int metricSystem, ARGestureManager arGestureManager,
                                       App app) {
        ratioMetricSystem = metricSystem;
        getSnackBarText(arGestureManager, app);
    }

    private float getUnitConversion(App app) {
        if (app.has(Feature.G3D_AR_RATIO_SETTINGS) &&
                ratioMetricSystem == EuclidianView3D.RATIO_UNIT_INCHES) {
            return EuclidianView3D.FROM_CM_TO_INCH;
        } else {
            return 1;
        }
    }

    public void setARRatio(double ratio) {
        if (ratioMetricSystem == EuclidianView3D.RATIO_UNIT_INCHES) {
            mARRatio = ((ratio * EuclidianView3D.FROM_INCH_TO_CM));
        } else {
            mARRatio = ratio;
        }
    }

    public double getARRatio() {
        return mARRatio;
    }

}
