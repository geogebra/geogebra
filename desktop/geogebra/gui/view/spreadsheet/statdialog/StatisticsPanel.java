package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
/**
 * 
 * Extended JPanel that displays:
 * (1) summary statistics for the current data set 
 * (2) interactive panels for performing statistical inference with the current data set
 * 
 * @author G. Sturr
 *
 */
public class StatisticsPanel extends JPanel implements StatPanelInterface, ActionListener {
	private static final long serialVersionUID = 1L;
	// inference mode constants 
	public static final int SUMMARY_STATISTICS = 0;
	// one var
	public static final int INFER_ZTEST = 1;
	public static final int INFER_ZINT = 2;
	public static final int INFER_TTEST = 3;
	public static final int INFER_TINT = 4;
	// two var
	public static final int INFER_TTEST_2MEANS = 20;
	public static final int INFER_TTEST_PAIRED = 21;
	public static final int INFER_TINT_2MEANS = 22;
	public static final int INFER_TINT_PAIRED = 23;
	// multi var
	public static final int INFER_ANOVA = 40;


	// inference mode selection
	private JComboBox cbInferenceMode;
	private HashMap<Integer, String> labelMap;
	private HashMap<String, Integer> labelMapReverse;
	private int selectedMode = SUMMARY_STATISTICS;

	// panels
	private BasicStatTable statTable;
	private OneVarInferencePanel oneVarInferencePanel;
	private LinearRegressionPanel regressionPanel;
	private TwoVarInferencePanel twoVarInferencePanel;
	private ANOVATable anovaTable;
	private MinimalMultiVarStatPanel minMVStatPanel;
	private JPanel selectionPanel;
	private JPanel inferencePanel;

	// ggb fields
	private DataAnalysisViewD statDialog;
	private AppD app;



	/*************************************
	 * Constructor
	 * @param app
	 * @param statDialog
	 */
	public StatisticsPanel(AppD app, DataAnalysisViewD statDialog)  {

		this.app = app;
		this.statDialog = statDialog;

		// create the sub-panels	
		createSelectionPanel();
		createStatTable();
		if(statTable != null){
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
	private void createStatTable(){
		// create a statTable according to dialog type
		if(statDialog.getMode() == DataAnalysisViewD.MODE_ONEVAR){
			statTable = new BasicStatTable(app, statDialog, statDialog.getMode());
		}
		else if(statDialog.getMode() == DataAnalysisViewD.MODE_REGRESSION){
			statTable = new BasicStatTable(app, statDialog, statDialog.getMode());
		}
		else if(statDialog.getMode() == DataAnalysisViewD.MODE_MULTIVAR){
			statTable = new MultiVarStatPanel(app, statDialog);
		}
	}


	/**
	 * Reconfigures the panel layout according to the current selected inference mode
	 */
	private void setInferencePanel(){

		if(inferencePanel == null) return;

		inferencePanel.removeAll();
		switch(selectedMode){

		case INFER_ZTEST:
		case INFER_TTEST:
		case INFER_ZINT:
		case INFER_TINT:
			inferencePanel.add(getOneVarInferencePanel(), BorderLayout.NORTH);
			break;

		case INFER_TTEST_2MEANS:
		case INFER_TTEST_PAIRED:
		case INFER_TINT_2MEANS:
		case INFER_TINT_PAIRED:		
			inferencePanel.add(getTwoVarInferencePanel(), BorderLayout.NORTH);
			//inferencePanel.add(statTable, BorderLayout.CENTER);
			break;

		case INFER_ANOVA:
			
			GridBagConstraints tab = new GridBagConstraints();
			tab.gridx=0;
			tab.gridy = GridBagConstraints.RELATIVE;
			tab.weightx=1;
			tab.insets = new Insets(4,20,0,20);
			tab.fill = GridBagConstraints.HORIZONTAL;
			tab.anchor=GridBagConstraints.NORTHWEST;
			
			JPanel p = new JPanel(new GridBagLayout());
			p.add(getAnovaTable(),tab);
			p.add(getMinMVStatPanel(),tab);
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


	private void createSelectionPanel(){
		createLabelMap();
		createInferenceTypeComboBox();

		selectionPanel = new JPanel(new BorderLayout());
		selectionPanel.add(cbInferenceMode, BorderLayout.WEST);
	}


	private ANOVATable getAnovaTable(){
		if(anovaTable == null)
			anovaTable = new ANOVATable(app, statDialog);
		return anovaTable;
	}

	private OneVarInferencePanel getOneVarInferencePanel(){
		if(oneVarInferencePanel == null)
			oneVarInferencePanel = new OneVarInferencePanel(app, statDialog);
		return oneVarInferencePanel;
	}

	private TwoVarInferencePanel getTwoVarInferencePanel(){
		if(twoVarInferencePanel == null)
			twoVarInferencePanel = new TwoVarInferencePanel(app, statDialog);
		return twoVarInferencePanel;
	}

	private MinimalMultiVarStatPanel getMinMVStatPanel(){
		if(minMVStatPanel == null)
			minMVStatPanel = new MinimalMultiVarStatPanel(app, statDialog);
		return minMVStatPanel;
	}
	

	/**
	 * Creates the JComboBox that selects inference mode
	 */
	private void createInferenceTypeComboBox(){

		if(cbInferenceMode == null){
			cbInferenceMode = new JComboBox();
			cbInferenceMode.setFocusable(false);
			cbInferenceMode.setRenderer(new MyRenderer());

		}else{
			cbInferenceMode.removeActionListener(this);
			cbInferenceMode.removeAllItems();
		}

		switch(statDialog.getMode()){

		case DataAnalysisViewD.MODE_ONEVAR:
			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
			cbInferenceMode.addItem(labelMap.get(INFER_ZTEST));
			cbInferenceMode.addItem(labelMap.get(INFER_TTEST));
			cbInferenceMode.addItem(MyRenderer.SEPARATOR);
			cbInferenceMode.addItem(labelMap.get(INFER_ZINT));
			cbInferenceMode.addItem(labelMap.get(INFER_TINT));
			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
			cbInferenceMode.addItem(labelMap.get(INFER_ANOVA));
			cbInferenceMode.addItem(labelMap.get(INFER_TTEST_2MEANS));
			cbInferenceMode.addItem(labelMap.get(INFER_TTEST_PAIRED));
			cbInferenceMode.addItem(MyRenderer.SEPARATOR);
			cbInferenceMode.addItem(labelMap.get(INFER_TINT_2MEANS));
			cbInferenceMode.addItem(labelMap.get(INFER_TINT_PAIRED));
			break;
		}

		cbInferenceMode.setSelectedItem(labelMap.get(selectedMode));
		cbInferenceMode.addActionListener(this);
		cbInferenceMode.setMaximumRowCount(cbInferenceMode.getItemCount());
		cbInferenceMode.addActionListener(this);


	}


	/**
	 * Creates two hash maps for JComboBox selections, 
	 * 1) plotMap:  Key = integer mode, Value = JComboBox menu string  
	 * 2) plotMapReverse: Key = JComboBox menu string, Value = integer mode    
	 */
	private void createLabelMap(){
		if(labelMap == null)
			labelMap = new HashMap<Integer,String>();

		labelMap.clear();
		labelMap.put(INFER_TTEST, app.getMenu("TMeanTest"));
		labelMap.put(INFER_TINT, app.getMenu("TMeanInterval"));
		labelMap.put(INFER_ZTEST, app.getMenu("ZMeanTest"));
		labelMap.put(INFER_ZINT, app.getMenu("ZMeanInterval"));

		labelMap.put(INFER_ANOVA, app.getMenu("ANOVA"));
		labelMap.put(SUMMARY_STATISTICS, app.getMenu("Statistics"));

		labelMap.put(INFER_TTEST_2MEANS, app.getMenu("TTestDifferenceOfMeans"));
		labelMap.put(INFER_TTEST_PAIRED, app.getMenu("TTestPairedDifferences"));
		labelMap.put(INFER_TINT_2MEANS, app.getMenu("TEstimateDifferenceOfMeans"));
		labelMap.put(INFER_TINT_PAIRED, app.getMenu("TEstimatePairedDifferences"));

		// REVERSE LABEL MAP
		labelMapReverse = new HashMap<String, Integer>();
		for(Integer key: labelMap.keySet()){
			labelMapReverse.put(labelMap.get(key), key);
		}

	}



	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		statTable.setLabels();
	}

	public void updatePanel() {
		if(statTable == null){
			return;
		}
		
		statTable.updatePanel();

		switch(selectedMode){

		case INFER_ZTEST:
		case INFER_TTEST:
		case INFER_ZINT:
		case INFER_TINT:
			getOneVarInferencePanel().setSelectedPlot(selectedMode);
			getOneVarInferencePanel().updatePanel();
			break;

		case INFER_TTEST_2MEANS:
		case INFER_TTEST_PAIRED:
		case INFER_TINT_2MEANS:
		case INFER_TINT_PAIRED:		
			getTwoVarInferencePanel().setSelectedInference(selectedMode);
			getTwoVarInferencePanel().updatePanel();
			break;

		case INFER_ANOVA:
			getAnovaTable().updatePanel();
			getMinMVStatPanel().updatePanel();
			break;
		}

		revalidate();
		repaint();
		this.setMinimumSize(this.getPreferredSize());
		//statDialog.updateStatDataPanelVisibility();
		
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if(source == cbInferenceMode && cbInferenceMode.getSelectedItem() != null){			

			if(cbInferenceMode.getSelectedItem().equals(MyRenderer.SEPARATOR)){
				cbInferenceMode.setSelectedItem(labelMap.get(selectedMode));
			}
			else 
			{
				selectedMode = labelMapReverse.get(cbInferenceMode.getSelectedItem());
			}
			setInferencePanel();
			updatePanel();
		}

	}


	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================

	private static class MyRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		public static final String SEPARATOR = "SEPARATOR";
		JSeparator separator;

		public MyRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

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






}
