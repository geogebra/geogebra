package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class FrequencyTablePanelW extends FlowPanel implements StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;

	protected AppW app;
	private Kernel kernel;
	private String[] strHeader;
	// protected DataAnalysisViewD statDialog;
	// private int mode;
	protected StatTableW statTable;
	
	// private StatPanelSettings settings;

	public FrequencyTablePanelW(AppW app) {
		this.app = app;
		this.kernel = app.getKernel();

		statTable = new StatTableW(app);
		statTable.setStyleName("frequencyTable");
		add(statTable);

	}

	public void setTableFromGeoFrequencyTable(AlgoFrequencyTable algo,
			boolean useClasses) {

		String[] strValue = algo.getValueString();
		String[] strFrequency = algo.getFrequencyString();
		strHeader = algo.getHeaderString();

		statTable.setStatTable(strValue.length, null, 2, strHeader);
		if (useClasses) {
			for (int row = 0; row < strValue.length - 1; row++) {
				statTable.getTable().setWidget(row, 0, new Label(strValue[row] + " - " + strValue[row + 1]));
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
//
//		 d = statTable.getPreferredSize();
//		this.setPreferredSize(d);
//		int numRows = Math.min(8, statTable.getTable().getRowCount());
//		d.height = numRows * statTable.getTable().getRowHeight();
//		this.setMaximumSize(d);
//		statTable.revalidate();
//		updateFonts(app.getPlainFont());
	}

	public void updatePanel() {
		// do nothing
	}

	public void setLabels() {
		statTable.setLabels(null, strHeader);
	}

}
