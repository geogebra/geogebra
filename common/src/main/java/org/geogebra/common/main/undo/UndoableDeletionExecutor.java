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
