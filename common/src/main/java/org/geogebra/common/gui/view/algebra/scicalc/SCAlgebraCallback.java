package org.geogebra.common.gui.view.algebra.scicalc;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.AsyncOperation;

public class SCAlgebraCallback implements AsyncOperation<GeoElementND[]> {

    private LabelController mLabelController;
    private boolean mShouldCenterText;

    public SCAlgebraCallback() {
        mLabelController = new LabelController();
    }

    public void setShouldCenterText(boolean shouldCenterText) {
        mShouldCenterText = shouldCenterText;
    }

    @Override
    public void callback(GeoElementND[] geoElements) {
        Kernel kernel = geoElements[0].getKernel();
        if (mShouldCenterText) {
            kernel.checkGeoTexts(geoElements);
        }
        if (geoElements instanceof GeoElement[]) {
            hideLabels((GeoElement[]) geoElements);
        }
        kernel.storeUndoInfo();
    }

    private void hideLabels(GeoElement[] geoElements) {
        for (GeoElement element : geoElements) {
            ExpressionNode definition = element.getDefinition();
            if (definition != null && definition.getLabel() == null) {
                mLabelController.hideLabel(element);
            }
        }
    }
}
