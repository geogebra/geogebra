package geogebra.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.statistics.AlgoFrequencyTable;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class FrequencyTablePanel extends JPanel implements StatPanelInterface {
	private static final long serialVersionUID = 1L;

	protected AppD app;
	private Kernel kernel;
	protected DataAnalysisViewD statDialog;
	private int mode;
	protected StatTable statTable;

	private StatPanelSettings settings;

	public FrequencyTablePanel(AppD app, DataAnalysisViewD statDialog) {
		this.app = app;
		this.kernel = app.getKernel();
		this.statDialog = statDialog;

		statTable = new StatTable(app);

		this.setLayout(new BorderLayout());
		this.add(statTable, BorderLayout.CENTER);

		statTable.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlShadow));
		setBorder(BorderFactory.createEmptyBorder());

	}

	public void setTableFromGeoFrequencyTable(AlgoFrequencyTable algo,
			boolean useClasses) {

		String[] strValue = algo.getValueString();
		String[] strFrequency = algo.getFrequencyString();
		String[] strHeader = algo.getHeaderString();

		statTable.setStatTable(strValue.length, null, 2, strHeader);
		DefaultTableModel model = statTable.getModel();

		if (useClasses) {
			for (int row = 0; row < strValue.length - 1; row++) {
				model.setValueAt(strValue[row] + " - " + strValue[row + 1],
						row, 0);
				model.setValueAt(strFrequency[row], row, 1);
			}
		} else {
			for (int row = 0; row < strValue.length; row++) {
				model.setValueAt(strValue[row], row, 0);
				model.setValueAt(strFrequency[row], row, 1);
			}
		}

		setTableSize();
	}

	private void setTableSize() {

		Dimension d = statTable.getPreferredSize();
		this.setPreferredSize(d);
		int numRows = Math.min(8, statTable.getTable().getRowCount());
		d.height = numRows * statTable.getTable().getRowHeight();
		this.setMaximumSize(d);
		statTable.revalidate();
		updateFonts(app.getPlainFont());
	}

	public void updatePanel() {
		// do nothing
	}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		// statTable.setLabels(null, getColumnNames());
	}

}
