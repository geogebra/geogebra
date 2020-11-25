package org.geogebra.common.gui.view.algebra.scicalc;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.AsyncOperation;

public class LabelHiderCallback implements AsyncOperation<GeoElementND[]> {

    private LabelController mLabelController;
    private boolean storeUndo = true;

    public LabelHiderCallback() {
        mLabelController = new LabelController();
    }

    @Override
    public void callback(GeoElementND[] geoElements) {
        if (geoElements instanceof GeoElement[]) {
            hideLabels((GeoElement[]) geoElements);
        }
        if (geoElements != null && geoElements.length > 0 && storeUndo) {
            geoElements[0].getKernel().storeUndoInfo();
        }
    }

    private void hideLabels(GeoElement[] geoElements) {
        for (GeoElement element : geoElements) {
            if (shouldHideLabel(element)) {
                mLabelController.hideLabel(element);
            }
        }
    }

    private boolean shouldHideLabel(GeoElement element) {
        boolean isShowingExtendedAv = element instanceof GeoNumeric
                && ((GeoNumeric) element).isShowingExtendedAV();
        boolean isIndependent = element.isIndependent();
        ExpressionNode definition = element.getDefinition();
        return !(isShowingExtendedAv && isIndependent)
                && (definition == null || definition.getLabel() == null);
    }

    public void setStoreUndo(boolean storeUndo) {
        this.storeUndo = storeUndo;
    }
}
