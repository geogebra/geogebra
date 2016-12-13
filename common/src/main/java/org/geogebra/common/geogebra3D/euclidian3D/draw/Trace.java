package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;

public class Trace extends TreeMap<TraceSettings, ArrayList<TraceIndex>> {

	private transient TraceIndex lastTraceIndex;
	private transient ArrayList<TraceIndex> lastTraceIndices;
	private transient TraceSettings traceSettingsCurrent;

	public Trace() {
		traceSettingsCurrent = new TraceSettings();
	}

	public void addLastTraceIndex() {
		if (lastTraceIndices != null) {
			lastTraceIndices.add(lastTraceIndex);
		}

	}

	public void setNext(ArrayList<TraceIndex> indices, Drawable3D d) {
		lastTraceIndices = indices;
		lastTraceIndex = d.newTraceIndex();
	}

	@Override
	public void clear() {
		super.clear();
		lastTraceIndices = null;
	}

	public void setSettings(GColor color, int alpha) {
		traceSettingsCurrent.setColor(color, alpha);
	}

	public ArrayList<TraceIndex> getTracesIndicesCurrent() {
		ArrayList<TraceIndex> indices = get(traceSettingsCurrent);
		if (indices == null) {
			indices = new ArrayList<TraceIndex>();
			put(traceSettingsCurrent.clone(), indices);
		}
		return indices;
	}

	public void record(Drawable3D d) {
		setSettings(d.color[0], d.getAlpha());

		ArrayList<TraceIndex> indices = getTracesIndicesCurrent();

		// really add trace at next current geometry record
		addLastTraceIndex();

		// prepare for next record
		setNext(indices, d);
	}

}
