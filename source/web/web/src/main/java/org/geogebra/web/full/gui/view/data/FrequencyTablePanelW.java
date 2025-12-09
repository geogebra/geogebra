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

package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Frequency table for Web
 */
public class FrequencyTablePanelW extends FlowPanel implements StatPanelInterfaceW {

	private String[] strHeader;
	protected StatTableW statTable;

	/**
	 * Create new frequency table
	 */
	public FrequencyTablePanelW() {
		statTable = new StatTableW();
		statTable.setStyleName("frequencyTable");
		add(statTable);
	}

	/**
	 * @param algo
	 *            frequency table algorithm
	 * @param useClasses
	 *            whether to use classes
	 */
	public void setTableFromGeoFrequencyTable(AlgoFrequencyTable algo,
			boolean useClasses) {
		String[] strValue = algo.getValueString();
		String[] strFrequency = algo.getFrequencyString();
		strHeader = algo.getHeaderString();

		statTable.setStatTable(strValue.length, null, 2, strHeader);
		if (useClasses) {
			for (int row = 0; row < strValue.length - 1; row++) {
				statTable.getTable().setWidget(row, 0,
						new Label(strValue[row] + " - " + strValue[row + 1]));
				statTable.getTable().setWidget(row, 1, new Label(strFrequency[row]));
			}
		} else {
			for (int row = 0; row < strValue.length; row++) {
				statTable.getTable().setWidget(row, 0, new Label(strValue[row]));
				statTable.getTable().setWidget(row, 1, new Label(strFrequency[row]));
			}
		}

		setTableSize();
	}

	private void setTableSize() {
		// do nothing
	}

	@Override
	public void updatePanel() {
		// do nothing
	}

	@Override
	public void setLabels() {
		statTable.setLabels(null, strHeader);
	}

}
