package org.geogebra.common.euclidian;

import static org.geogebra.common.main.undo.ConstructionActionExecutor.DEL;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

public class UpdateStrokeStyleStore extends StrokeHelper {

	List<GeoElement> updatedStrokes;

	List<String> initialStateXML;

	List<String> modifiedStateXML;

	private final UndoManager undoManager;

	/**
	 * Creates a new helper data structure that is used to stores the original states of the strokes
	 * before updating the style (color, line thickness) and the updated strokes.
	 * @param initialSplitStrokes reference to the children stroke created after splitting
	 */
	public UpdateStrokeStyleStore(List<GeoElement> initialSplitStrokes, UndoManager undoManager) {
		initialStateXML = getStrokesXML(initialSplitStrokes);
		this.undoManager = undoManager;
	}

	/***
	 * returns an array of XMLs of the styled/modified strokes
	 * @return array of XMLs
	 */
	public String[] toStyledStrokeArray() {
		return modifiedStateXML.stream().toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the initial unstyled/unmodified stroke xml
	 * @return array of XMLs
	 */
	public String[] toUnStyledStrokeArray() {
		return initialStateXML.stream().toArray(String[]::new);
	}

	/**
	 * adds the updated strokes (coloring/thickness etc) to the StrokeSplitHelper object
	 * @param updatedStrokes newly created stroke with the modifications
	 */

	public void addUpdatedStrokes(List<GeoElement> updatedStrokes) {
		this.updatedStrokes = updatedStrokes;
		modifiedStateXML = getStrokesXML(updatedStrokes);
	}

	/**
	 * stores an undo for the stroke style update
	 */
	public void storeStrokeStyleUpdateUndo() {
		undoManager.buildAction(ActionType.UPDATE, toStyledStrokeArray())
				.withUndo(ActionType.UPDATE, toUnStyledStrokeArray())
				.withLabels(getLabelsThatNeedRemoval())
				.storeAndNotifyUnsaved();
	}

	private String[] getLabelsThatNeedRemoval() {
		return updatedStrokes
				.stream()
				.map(s -> DEL + s.getLabelSimple())
				.toArray(String[]::new);
	}
}