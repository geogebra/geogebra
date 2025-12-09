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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;

public final class Trace extends TreeMap<TraceSettings, ArrayList<TraceIndex>> {

	/** Serialization version */
	private static final long serialVersionUID = -7107935777385040694L;

	private transient TraceIndex lastTraceIndex;
	private transient ArrayList<TraceIndex> lastTraceIndices;
	private transient TraceSettings traceSettingsCurrent;

	public Trace() {
		traceSettingsCurrent = new TraceSettings();
	}

	/**
	 * Add last trace index to list.
	 */
	public void addLastTraceIndex() {
		if (lastTraceIndices != null) {
			lastTraceIndices.add(lastTraceIndex);
		}
	}

	/**
	 * @param indices
	 *            list of indices
	 * @param d
	 *            drawable for last index
	 */
	public void setNext(ArrayList<TraceIndex> indices, Drawable3D d) {
		lastTraceIndices = indices;
		lastTraceIndex = d.newTraceIndex();
	}

	@Override
	public void clear() {
		super.clear();
		lastTraceIndices = null;
	}

	/**
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 */
	public void setSettings(GColor color, int alpha) {
		traceSettingsCurrent.setColor(color, alpha);
	}

	private ArrayList<TraceIndex> getTracesIndicesCurrent() {
		ArrayList<TraceIndex> indices = get(traceSettingsCurrent);
		if (indices == null) {
			indices = new ArrayList<>();
			put(traceSettingsCurrent.copy(), indices);
		}
		return indices;
	}

	/**
	 * @param d
	 *            drawable to record
	 */
	public void record(Drawable3D d) {
		setSettings(d.color[0], d.getAlpha());

		ArrayList<TraceIndex> indices = getTracesIndicesCurrent();

		// really add trace at next current geometry record
		addLastTraceIndex();

		// prepare for next record
		setNext(indices, d);
	}

}
