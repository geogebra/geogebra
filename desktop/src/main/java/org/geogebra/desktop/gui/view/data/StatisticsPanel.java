package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.gui.view.data.StatisticsModel.IStatisticsModelListener;
import org.geogebra.desktop.main.AppD;

/**
 * 
 * Extended JPanel that displays: (1) summary statistics for the current data
 * set (2) interactive panels for performing statistical inference with the
 * current data set
 * 
 * @author G. Sturr
 * 
 */
public class StatisticsPanel extends JPanel implements StatPanelInterface,
		ActionListener, IStatisticsModelListener {
	private static final long serialVersionUID = 1L;

	private StatisticsModel model;
	// inference mode selection
	private JComboBox cbInferenceMode;

	// panels
	private BasicStatTable statTable;
	private OneVarInferencePanelD oneVarInferencePanel;
	private TwoVarInferencePanel twoVarInferencePanel;
	private ANOVATable anovaTable;
	private MultiVarStatPanel minMVStatPanel;
	private JPanel selectionPanel;
	private JPanel inferencePanel;

	// ggb fields
	private DataAnalysisViewD statDialog;
	private AppD app;
	private DataAnalysisModel daModel;

	/*************************************
	 * Constructor
	 * 
	 * @param app
	 * @param statDialog
	 */
	public StatisticsPanel(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		model = new StatisticsModel(app, daModel, this);
		// create the sub-panels
		createSelectionPanel();
		createStatTable();
		if (statTable != null) {
			statTable.setBorder(BorderFactory.createEmptyBorder());
			inferencePanel = new JPanel(new BorderLayout());
			inferencePanel.add(statTable, BorderLayout.CENTER);

			// add sub-panels to layout
			setLayout(new BorderLayout());
			add(selectionPanel, BorderLayout.NORTH);
			add(inferencePanel, BorderLayout.CENTER);

			setLabels();
		}
	}

	/**
	 * Creates a table to display summary statistics for the current data set(s)
	 */
	private void createStatTable() {
		// create a statTable according to dialog type
		if (daModel.getMode() == DataAnalysisModel.MODE_ONEVAR) {
			statTable = new BasicStatTable(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_REGRESSION) {
			statTable = new BasicStatTable(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_MULTIVAR) {
			statTable = new MultiVarStatPanel(app, statDialog);
		}
	}

	/**
	 * Reconfigures the panel layout according to the current selected inference
	 * mode
	 */
	private void setInferencePanel() {

		if (inferencePanel == null) {
			return;
		}

		inferencePanel.removeAll();
		switch (model.getSelectedMode()) {

		case StatisticsModel.INFER_ZTEST:
		case StatisticsModel.INFER_TTEST:
		case StatisticsModel.INFER_ZINT:
		case StatisticsModel.INFER_TINT:
			inferencePanel.add(getOneVarInferencePanel(), BorderLayout.NORTH);
			break;

		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TINT_2MEANS:
			inferencePanel.add(getTwoVarInferencePanel(true),
					BorderLayout.NORTH);
			break;

		case StatisticsModel.INFER_TTEST_PAIRED:
		case StatisticsModel.INFER_TINT_PAIRED:
			inferencePanel.add(getTwoVarInferencePanel(false),
					BorderLayout.NORTH);
			break;

		case StatisticsModel.INFER_ANOVA:

			GridBagConstraints tab = new GridBagConstraints();
			tab.gridx = 0;
			tab.gridy = GridBagConstraints.RELATIVE;
			tab.weightx = 1;
			tab.insets = new Insets(4, 20, 0, 20);
			tab.fill = GridBagConstraints.HORIZONTAL;
			tab.anchor = GridBagConstraints.NORTHWEST;

			JPanel p = new JPanel(new GridBagLayout());
			p.add(getAnovaTable(), tab);
			p.add(getMinMVStatPanel(), tab);
			inferencePanel.add(p, BorderLayout.CENTER);

			break;

		default:
			inferencePanel.add(statTable, BorderLayout.CENTER);
		}

		revalidate();
		repaint();
		this.setMinimumSize(this.getPreferredSize());
		statDialog.updateStatDataPanelVisibility();
	}

	private void createSelectionPanel() {
		createInferenceTypeComboBox();

		selectionPanel = new JPanel(new BorderLayout());
		selectionPanel.add(cbInferenceMode, app.getLocalization().borderWest());
	}

	private ANOVATable getAnovaTable() {
		if (anovaTable == null) {
			anovaTable = new ANOVATable(app, statDialog);
		}
		return anovaTable;
	}

	private OneVarInferencePanelD getOneVarInferencePanel() {
		if (oneVarInferencePanel == null) {
			oneVarInferencePanel = new OneVarInferencePanelD(app, statDialog);
		}
		return oneVarInferencePanel;
	}

	private TwoVarInferencePanel getTwoVarInferencePanel() {
		if (twoVarInferencePanel == null) {
			twoVarInferencePanel = new TwoVarInferencePanel(app, statDialog);
		}
		return twoVarInferencePanel;
	}

	private TwoVarInferencePanel getTwoVarInferencePanel(boolean enablePooled) {
		TwoVarInferencePanel p = getTwoVarInferencePanel();
		p.setEnablePooled(enablePooled);
		return p;
	}

	private MultiVarStatPanel getMinMVStatPanel() {
		if (minMVStatPanel == null) {
			minMVStatPanel = new MultiVarStatPanel(app, statDialog);
		}
		minMVStatPanel.setMinimalTable(true);
		return minMVStatPanel;
	}

	/**
	 * Creates the JComboBox that selects inference mode
	 */
	private void createInferenceTypeComboBox() {

		if (cbInferenceMode == null) {
			cbInferenceMode = new JComboBox();
			cbInferenceMode.setFocusable(false);
			cbInferenceMode.setRenderer(new MyRenderer());

		} else {
			cbInferenceMode.removeActionListener(this);
			cbInferenceMode.removeAllItems();
		}

		model.fillInferenceModes();

		cbInferenceMode.addActionListener(this);
		cbInferenceMode.setMaximumRowCount(cbInferenceMode.getItemCount());
		cbInferenceMode.addActionListener(this);

	}

	@Override
	public void updateFonts(Font font) {
		if (statTable != null) {
			statTable.updateFonts(font);
		}
	}

	@Override
	public void setLabels() {
		statTable.setLabels();
	}

	@Override
	public void updatePanel() {
		// System.out.println("============= update stat panel");
		if (statTable == null) {
			return;
		}

		statTable.updatePanel();
		model.update();
		revalidate();
		repaint();
		this.setMinimumSize(this.getPreferredSize());
		// statDialog.updateStatDataPanelVisibility();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == cbInferenceMode
				&& cbInferenceMode.getSelectedItem() != null) {

			model.selectInferenceMode(
					cbInferenceMode.getSelectedItem().toString());
			setInferencePanel();
			updatePanel();
		}

	}

	// ============================================================
	// ComboBox Renderer with SEPARATOR
	// ============================================================

	private static class MyRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		public static final String SEPARATOR = "SEPARATOR";
		JSeparator separator;

		public MyRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}

	@Override
	public void addInferenceMode(String item) {
		cbInferenceMode.addItem(item);
	}

	@Override
	public void selectInferenceMode(String item) {
		cbInferenceMode.setSelectedItem(item);
	}

	@Override
	public String getSeparator() {
		return MyRenderer.SEPARATOR;
	}

	@Override
	public void updateOneVarInference(int mode) {
		getOneVarInferencePanel().setSelectedPlot(mode);
		getOneVarInferencePanel().updatePanel();
	}

	@Override
	public void updateTwoVarInference(int mode) {
		getTwoVarInferencePanel().setSelectedInference(mode);
		getTwoVarInferencePanel().updatePanel();
	}

	@Override
	public void updateAnovaTable() {
		getAnovaTable().updatePanel();
		getMinMVStatPanel().updatePanel();

	}

}
