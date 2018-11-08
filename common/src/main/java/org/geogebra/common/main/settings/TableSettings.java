package org.geogebra.common.main.settings;

import java.util.HashMap;
import java.util.Map.Entry;

public class TableSettings {

	private double valuesMin = -2.0;
	private double valuesMax = 2.0;
	private double valuesStep = 1.0;
	private HashMap<String, Boolean> showPoints = new HashMap<>();

	public double getValuesMin() {
		return valuesMin;
	}

	public void setValuesMin(double valuesMin) {
		this.valuesMin = valuesMin;
	}

	public double getValuesMax() {
		return valuesMax;
	}

	public void setValuesMax(double valuesMax) {
		this.valuesMax = valuesMax;
	}

	public double getValuesStep() {
		return valuesStep;
	}

	public void setValuesStep(double valuesStep) {
		this.valuesStep = valuesStep;
	}
	
	public void setShowPoints(String label, boolean show) {
		this.showPoints.put(label, show);
	}

	public boolean isShowPoints(String label) {
		return this.showPoints.containsKey(label) && this.showPoints.get(label);
	}

	public void getXML(StringBuilder sb){
		if (showPoints.isEmpty()) {
			return;
		}
		sb.append("<tableview min=\"");
		sb.append(valuesMin);
		sb.append("\" max=\"");
		sb.append(valuesMax);
		sb.append("\" step=\"");
		sb.append(valuesStep);
		sb.append("\">\n");
		for (Entry<String, Boolean> column : showPoints.entrySet()) {
			sb.append("\t<column label=\"");
			sb.append(column.getKey());
			sb.append("\" points=\"");
			sb.append(column.getValue());
			sb.append("\"/>\n");
		}
		sb.append("</tableview>\n");
	}
}
