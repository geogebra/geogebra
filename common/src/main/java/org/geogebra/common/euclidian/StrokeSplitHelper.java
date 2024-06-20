package org.geogebra.common.euclidian;

import static org.geogebra.common.main.undo.ConstructionActionExecutor.DEL;

import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;

public class StrokeSplitHelper extends StrokeHelper {

	List<GeoElement> initialStrokes;

	List<GeoElement> splitParts;

	List<String> initialStateXML;

	List<String> splitStrokesXML;

	/**
	 * Creates a new helper data structure that stores the original stroke and the split
	 * up parts.
	 * @param initialStrokes parent stroke
	 * @param splitParts children stroke created after selection
	 */
	public StrokeSplitHelper(List<GeoElement> initialStrokes, List<GeoElement> splitParts) {
		this.initialStrokes = initialStrokes;
		this.splitParts = splitParts;
		initialStateXML = getStrokesXML(initialStrokes);
		splitStrokesXML = getStrokesXML(splitParts);
	}

	/**
	 * returns an array of XMLs with the initial state of the stroke before splitting.
	 * used for the undoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toSplitActionArray() {
		return Stream.concat(initialStrokes.stream().map(s -> DEL + s.getLabelSimple()),
				splitStrokesXML.stream()).toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the children stokes after splitting the initial stroke.
	 * used for the redoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toMergeActionArray() {
		return Stream.concat(splitParts.stream().map(s -> DEL + s.getLabelSimple()),
				initialStateXML.stream()).toArray(String[]::new);
	}
}