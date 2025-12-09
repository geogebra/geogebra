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