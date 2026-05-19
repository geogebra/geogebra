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

package org.geogebra.web.full.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_INTERVAL;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_LEFT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_RIGHT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.properties.PropertyView;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ProbabilityResultRow extends FlowPanel {
	private final AppW appW;
	private final PropertyView.ProbabilityResultRow resultRowProperty;
	private final List<Label> labels = new ArrayList<>();
	private boolean isEditing;

	/**
	 * Result row in probability view.
	 * @param appW {@link AppW}
	 * @param resultRowProperty {@link PropertyView.ProbabilityResultRow}
	 */
	public ProbabilityResultRow(AppW appW, PropertyView.ProbabilityResultRow resultRowProperty) {
		this.appW = appW;
		this.resultRowProperty = resultRowProperty;
		addStyleName("probabilityResultRow");
		buildGUI();
		AriaHelper.setRole(this, "group");
		resultRowProperty.setConfigurationUpdateDelegate(this::updateContent);
	}

	private void buildGUI() {
		for (PropertyView.ProbabilityResultRow.Item item : resultRowProperty.getItems()) {
			if (item instanceof PropertyView.ProbabilityResultRow.Item.Text text) {
				Label label = new Label(text.text());
				add(label);
				if (text.ariaLabel() != null) {
					AriaHelper.setLabel(label,
							appW.getLocalization().getMenu(text.ariaLabel()));
				} else {
					AriaHelper.setAriaHidden(label);
				}
				labels.add(label);
			} else if (item
					instanceof PropertyView.ProbabilityResultRow.Item.InputField inputField) {
				FlowPanel holder = new FlowPanel();
				MathTextFieldW mathTextFieldW = new MathTextFieldW(appW);
				mathTextFieldW.setText(inputField.getValue());
				AriaHelper.setLabel(mathTextFieldW.asWidget(),
						appW.getLocalization().getMenu(inputField.getAriaLabel()));
				mathTextFieldW.setPxWidth(80);
				mathTextFieldW.getMathField().setOnFocus(event -> {
					isEditing = true;
					holder.addStyleName("focusState");
				});
				mathTextFieldW.addInputHandler(() -> inputField
						.setValue(mathTextFieldW.getText()));
				mathTextFieldW.addBlurHandler(event -> {
					inputField.setValue(mathTextFieldW.getText());
					isEditing = false;
					holder.removeStyleName("focusState");
				});

				holder.setStyleName("holder");
				holder.add(mathTextFieldW);
				add(holder);
			}
		}
		updateAccessibleName();
	}

	/**
	 * Updates the aria-label of the result panel based on the selected probability mode.
	 */
	public void updateAccessibleName() {
		int probMode = resultRowProperty.getView().getProbMode();
		String key = switch (probMode) {
			case PROB_INTERVAL -> "Interval.Probability";
			case PROB_LEFT -> "Left.Sided.Probability";
			case PROB_RIGHT -> "Right.Sided.Probability";
			case PROB_TWO_TAILED -> "Two.Tailed.Probability";
			default -> "";
		};
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(key));
	}

	private void updateContent() {
		if (!isEditing) {
			clear();
			labels.clear();
			buildGUI();

		} else {
			int labelIndex = 0;
			for (PropertyView.ProbabilityResultRow.Item item: resultRowProperty.getItems()) {
				if (item instanceof PropertyView.ProbabilityResultRow.Item.Text text
						&& labelIndex < labels.size()) {
					labels.get(labelIndex++).setText(text.text());
				}
			}
		}
	}
}
