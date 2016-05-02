package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.TreeMap;

import org.geogebra.common.kernel.Matrix.Coords;

public class Trace extends TreeMap<TraceSettings, ArrayList<TraceIndex>> {

	private TraceIndex lastTraceIndex;
	private ArrayList<TraceIndex> lastTraceIndices;
	private TraceSettings traceSettingsCurrent;

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

	public void setSettings(Coords color, float alpha) {
		traceSettingsCurrent.setColor(color);
		traceSettingsCurrent.setAlpha(alpha);
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
		setSettings(d.color, d.getAlpha());

		ArrayList<TraceIndex> indices = getTracesIndicesCurrent();

		// really add trace at next current geometry record
		addLastTraceIndex();

		// prepare for next record
		setNext(indices, d);
	}


}
