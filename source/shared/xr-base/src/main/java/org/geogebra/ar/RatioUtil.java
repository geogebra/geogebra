package org.geogebra.ar;

import static org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface.DESK_DISTANCE_AVERAGE;
import static org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface.DESK_DISTANCE_MAX;
import static org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface.MAX_FACTOR_TO_EMPHASIZE;
import static org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface.PROJECT_FACTOR_RELATIVE_PRECISION;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawClippingCube3D;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.util.DoubleUtil;

class RatioUtil {

    private double ratioAtStart;
    private float ratioChange = 1;      // change of ratio when ratio is set from menu
    private String ratioText = "1";   // current ratio used for Ratio snack bar and ratio settings

    float getRatioChange() {
        return ratioChange;
    }

    String getRatioText() {
        return ratioText;
    }

    double setARScaleAtStart(EuclidianView3D view3D, CoordMatrix4x4 viewModelMatrix,
                           CoordMatrix4x4 projectMatrix, double ggbToRw) {
        float mDistance = (float) viewModelMatrix.getOrigin().calcNorm3();
        // don't expect distance less than desk distance at start
        if (mDistance < DESK_DISTANCE_MAX) {
            mDistance = (float) DESK_DISTANCE_AVERAGE;
        }
        // 1 ggb unit ==  1 meter
        // ratio
        double ratio;
        double projectFactor = projectMatrix.get(1, 1);
        double precisionPoT = DoubleUtil.getPowerOfTen(projectFactor);
        double precision = Math.round(projectFactor / precisionPoT) * precisionPoT
                * PROJECT_FACTOR_RELATIVE_PRECISION;
        projectFactor = Math.round(projectFactor / precision) * precision;
        float fittingScreenScale = (float) (DrawClippingCube3D.REDUCTION_ENLARGE
                * (mDistance / projectFactor)
                / view3D.getRenderer().getWidth());
        ratio = fittingScreenScale / ggbToRw; // fittingScreenScale = ggbToRw * ratio
        double pot = DoubleUtil.getPowerOfTen(ratio);
        ratio = ratio / pot;
        if (ratio < 2f / MAX_FACTOR_TO_EMPHASIZE) {
            ratio = 1f;
        } else if (ratio < 5f / MAX_FACTOR_TO_EMPHASIZE) {
            ratio = 2f;
        } else if (ratio < 10f / MAX_FACTOR_TO_EMPHASIZE) {
            ratio = 5f;
        } else {
            ratio = 10f;
        }
        ratio = ratio * pot;

        int mToCm = 100;
        ratioAtStart = ratio * mToCm;
        return ratio;
    }

    private static float getUnitConversion(EuclidianView3D view3D) {
        if (view3D.getARRatioMetricSystem() == EuclidianView3D.RATIO_UNIT_INCHES) {
            return EuclidianView3D.FROM_CM_TO_INCH;
        } else {
            return 1;
        }
    }

    void setARRatio(double ratio, EuclidianView3D view3D) {
        if (view3D.getARRatioMetricSystem() == EuclidianView3D.RATIO_UNIT_INCHES) {
            ratioChange = (float) ((ratio * EuclidianView3D.FROM_INCH_TO_CM) / ratioAtStart);
        } else {
            ratioChange = (float) ((ratio) / ratioAtStart);
        }
    }

    double calculateRatio(EuclidianView3D view3D, ARGestureManager arGestureManager) {
        double ratio;
        if (arGestureManager != null) {
            ratio = ratioAtStart * arGestureManager.getScaleFactor() * ratioChange
                    * getUnitConversion(view3D);
        } else {
            ratio = ratioAtStart;
        }

        if (ratio >= 100) {
            // round to 0 decimal places.
            ratio = (double) Math.round(ratio);
        } else if (ratio < 10 ) {
            // round to 2 decimal places.
            ratio = (double) Math.round(ratio * 100) / 100d;
        } else {
            // round to 1 decimal places.
            ratio = (double) Math.round(ratio * 10) / 10d;
        }

        if (view3D.getARRatioMetricSystem() == EuclidianView3D.RATIO_UNIT_INCHES) {
            view3D.setARRatioUnit("inch");
        } else {
            view3D.setARRatioUnit("cm");
        }
        return ratio;
    }

    String getRatioMessage(double ratio, EuclidianView3D view3D) {
        if(DoubleUtil.isInteger(ratio)) {
            ratioText = String.format("%d", (long) ratio);
        } else {
            ratioText = String.format("%.4s", ratio);
        }
        return String.format("1 : %s %s", ratioText, view3D.getARRatioUnit());
    }
}
