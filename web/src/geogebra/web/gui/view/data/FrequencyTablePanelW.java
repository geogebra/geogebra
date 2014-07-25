package geogebra.web.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.statistics.AlgoFrequencyTable;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class FrequencyTablePanelW extends FlowPanel implements StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;

	protected AppW app;
	private Kernel kernel;
	// protected DataAnalysisViewD statDialog;
	// private int mode;
	protected StatTableW statTable;

	// private StatPanelSettings settings;

	public FrequencyTablePanelW(AppW app) {
		this.app = app;
		this.kernel = app.getKernel();

		statTable = new StatTableW(app);

	//	this.setLayout(new BorderLayout());
	//	this.add(statTable, BorderLayout.CENTER);

		add(statTable);

	}

	public void setTableFromGeoFrequencyTable(AlgoFrequencyTable algo,
			boolean useClasses) {

		String[] strValue = algo.getValueString();
		String[] strFrequency = algo.getFrequencyString();
		String[] strHeader = algo.getHeaderString();

		statTable.setStatTable(strValue.length, null, 2, strHeader);
//		DefaultTableModel model = statTable.getModel();
//
//		if (useClasses) {
//			for (int row = 0; row < strValue.length - 1; row++) {
//				model.setValueAt(strValue[row] + " - " + strValue[row + 1],
//						row, 0);
//				model.setValueAt(strFrequency[row], row, 1);
//			}
//		} else {
//			for (int row = 0; row < strValue.length; row++) {
//				model.setValueAt(strValue[row], row, 0);
//				model.setValueAt(strFrequency[row], row, 1);
//			}
//		}
//
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
		// statTable.setLabels(null, getColumnNames());
	}

}
