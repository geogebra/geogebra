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

package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UndoableDeletionExecutor implements DeletionExecutor {

	List<String> labels = new ArrayList<>();
	List<String> xmls = new ArrayList<>();

	@Override
	public void delete(GeoElement geo) {
		if (geo.getKernel().isUndoActive()) {
			doStoreDeletion(geo);
			for (GeoElement child: geo.getAllChildren()) {
				doStoreDeletion(child);
			}
		}
		geo.removeOrSetUndefinedIfHasFixedDescendent();
	}

	@Override
	public boolean storeUndoAction(Kernel kernel) {
		if (kernel.isUndoActive() && !labels.isEmpty()) {
			kernel.storeStateForModeStarting();
			String[] labelsArray = labels.toArray(new String[0]);
			kernel.getConstruction().getUndoManager()
					.buildAction(ActionType.REMOVE, labelsArray)
					.withUndo(ActionType.ADD, xmls.toArray(new String[0]))
					.withLabels(labelsArray).storeAndNotifyUnsaved();
			return true;
		}
		return false;
	}

	private void doStoreDeletion(GeoElement geo) {
		if (labels.contains(geo.getLabelSimple())) {
			return;
		}
		labels.add(geo.getLabelSimple());
		AlgoElement parentAlgorithm = geo.getParentAlgorithm();
		if (parentAlgorithm != null) {
			xmls.add(parentAlgorithm.getXML());
		} else {
			xmls.add(geo.getXML());
		}
	}

}
