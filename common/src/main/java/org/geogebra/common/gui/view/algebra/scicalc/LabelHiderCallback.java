package org.geogebra.common.gui.view.algebra.scicalc;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.AsyncOperation;

public class LabelHiderCallback implements AsyncOperation<GeoElementND[]> {

    private LabelController mLabelController;

    public LabelHiderCallback() {
        mLabelController = new LabelController();
    }

    @Override
    public void callback(GeoElementND[] geoElements) {
        if (geoElements instanceof GeoElement[]) {
            hideLabels((GeoElement[]) geoElements);
        }
        if (geoElements != null && geoElements.length > 0) {
            geoElements[0].getKernel().storeUndoInfo();
        }
    }

    private void hideLabels(GeoElement[] geoElements) {
        for (GeoElement element : geoElements) {
            ExpressionNode definition = element.getDefinition();
            if (definition == null || definition.getLabel() == null) {
                mLabelController.hideLabel(element);
            }
        }
    }
}
