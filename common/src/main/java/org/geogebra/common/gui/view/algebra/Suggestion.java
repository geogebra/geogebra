package org.geogebra.common.gui.view.algebra;

public class Suggestion {
	private String[] labels;

	public Suggestion(String... labels) {
		this.labels = labels;
	}

	public String getLabels() {
		return labels.length == 1 ? labels[0]
				: "{" + String.join(", ", labels) + "}";
	}
}
