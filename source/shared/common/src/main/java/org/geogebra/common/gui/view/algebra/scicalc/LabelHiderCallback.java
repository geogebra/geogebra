/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
                && ((GeoNumeric) element).isAVSliderOrCheckboxVisible();
        boolean isIndependent = element.isIndependent();
        ExpressionNode definition = element.getDefinition();
        return !(isShowingExtendedAv && isIndependent)
                && (definition == null || definition.getLabel() == null);
    }

    public void setStoreUndo(boolean storeUndo) {
        this.storeUndo = storeUndo;
    }
}
