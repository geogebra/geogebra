package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * JPanel to display settings options for a ComboStatPanel
 * @author G. Sturr
 *
 */
public class OptionsPanel extends JPanel implements PropertyChangeListener, ActionListener, FocusListener, StatPanelInterface{
	private static final long serialVersionUID = 1L;

	private AppD app;
	private StatDialog statDialog;
	private StatPanelSettings settings;

	// histogram  panel components
	private JCheckBox ckCumulative, ckManual, ckOverlayNormal,ckOverlayPolygon, ckShowFrequencyTable, ckShowHistogram;
	private JRadioButton rbRelative, rbNormalized,  rbFreq ;
	private JLabel lblFreqType;

	// graph  panel components
	private JCheckBox ckAutoWindow, ckShowGrid;
	private JLabel lblXMin, lblXMax, lblYMin, lblYMax, lblXInterval, lblYInterval;
	private MyTextField fldXMin, fldXMax, fldYMin, fldYMax, fldXInterval, fldYInterval;

	private static final int tab1 = 1;
	private static final int tab2 = 15;

	// option panels
	private JPanel histogramPanel, graphPanel, classesPanel, scatterplotPanel;

	private JPanel mainPanel;
	private boolean showYSettings = true;
	private JTabbedPane tabbedPane;
	private JCheckBox ckShowLines;
	private JLabel lblOverlay;

	private boolean isUpdating = false;
	private int plotType;
	private JPanel freqPanel;
	private JPanel showPanel;
	private JRadioButton rbLeftRule;
	private JRadioButton rbRightRule;
	private JPanel dimPanel;
	private JLabel lblClassRule;


	public OptionsPanel(AppD app, StatDialog statDialog, StatPanelSettings settings){

		this.app = app;
		this.statDialog = statDialog;
		this.settings = settings;

		// create option panels
		createHistogramPanel();
		createGraphPanel();
		createScatterplotPanel();
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(histogramPanel);
		mainPanel.add(scatterplotPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addFocusListener(this);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,SystemColor.controlShadow));
		
		// update
		setLabels();
		updateGUI();
		//this.setPreferredSize(tabbedPane.getPreferredSize());
		
		this.requestFocusInWindow();

	}


	public void setPanel(int plotType){

		this.plotType = plotType;
		tabbedPane.removeAll();
		String tabTitle = StatComboPanel.plotMap.get(plotType);
		tabbedPane.insertTab(tabTitle, null, new JScrollPane(mainPanel),null, 0);
		tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
		showYSettings = true;
		
		switch(plotType){
		
		case StatComboPanel.PLOT_HISTOGRAM:
			classesPanel.setVisible(true);
			histogramPanel.setVisible(true);
			graphPanel.setVisible(true);
			scatterplotPanel.setVisible(false);		
			break;
			
		case StatComboPanel.PLOT_DOTPLOT:
		case StatComboPanel.PLOT_BOXPLOT:
		case StatComboPanel.PLOT_NORMALQUANTILE:
			tabbedPane.removeTabAt(0);
			showYSettings = true;
			classesPanel.setVisible(false);
			histogramPanel.setVisible(false);
			graphPanel.setVisible(true);
			scatterplotPanel.setVisible(false);
			break;
			
		case StatComboPanel.PLOT_SCATTERPLOT:	
			showYSettings = true;
			classesPanel.setVisible(false);
			histogramPanel.setVisible(false);
			graphPanel.setVisible(true);
			scatterplotPanel.setVisible(true);
			break;
			
		case StatComboPanel.PLOT_RESIDUAL:
			tabbedPane.removeTabAt(0);
			showYSettings = true;
			classesPanel.setVisible(false);
			histogramPanel.setVisible(false);
			graphPanel.setVisible(true);
			scatterplotPanel.setVisible(false);
			break;
		}

		setLabels();
		updateGUI();
	}

	private void createHistogramPanel(){

		// create components
		ckCumulative = new JCheckBox();
		ckCumulative.addActionListener(this);

		lblFreqType = new JLabel();
		rbFreq = new JRadioButton();
		rbFreq.addActionListener(this);

		rbNormalized = new JRadioButton();
		rbNormalized.addActionListener(this);

		rbRelative = new JRadioButton();	
		rbRelative.addActionListener(this);
		
		ButtonGroup g = new ButtonGroup();
		g.add(rbFreq);
		g.add(rbNormalized);
		g.add(rbRelative);

		lblOverlay = new JLabel();
		ckOverlayNormal = new JCheckBox();
		ckOverlayNormal.addActionListener(this);

		ckOverlayPolygon = new JCheckBox();
		ckOverlayPolygon.addActionListener(this);

		ckShowFrequencyTable = new JCheckBox();
		ckShowFrequencyTable.addActionListener(this);
		
		ckShowHistogram = new JCheckBox();
		ckShowHistogram.addActionListener(this);

		ckManual = new JCheckBox();		
		ckManual.addActionListener(this);
		
		
		lblClassRule = new JLabel();
		rbLeftRule = new JRadioButton();
		rbLeftRule.addActionListener(this);
		rbRightRule = new JRadioButton();
		rbRightRule.addActionListener(this);
		
		ButtonGroup g2 = new ButtonGroup();
		g2.add(rbLeftRule);
		g2.add(rbRightRule);
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.weightx=1;
		c.anchor=GridBagConstraints.LINE_START;
		
		// tab = tab-like constraint
		GridBagConstraints tab = (GridBagConstraints) c.clone();
		tab.insets = new Insets(0,20,0,0);	
		
		
		// create frequency type panel
		freqPanel = new JPanel(new GridBagLayout()); 
		freqPanel.add(ckCumulative,c);
		freqPanel.add(rbFreq,tab);
		freqPanel.add(rbRelative,tab);
		freqPanel.add(rbNormalized,tab);
		
		
		// create show panel
		showPanel = new JPanel(new GridBagLayout()); 
		showPanel.add(ckShowHistogram,c);
		showPanel.add(ckShowFrequencyTable,c);
		showPanel.add(ckOverlayPolygon,c);
		showPanel.add(ckOverlayNormal,c);
		
		
		// create classes panel
		classesPanel = new JPanel(new GridBagLayout());
		classesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("FrequencyType")));
		classesPanel.add(ckManual, c);
		c.insets.top += 8;   // vertical gap
		classesPanel.add(lblClassRule, c);
		c.insets.top -= 8;   // undo vertical gap
		classesPanel.add(rbLeftRule, tab);	
		classesPanel.add(rbRightRule, tab);	
		
		// put the sub-panels together
		Box vBox = Box.createVerticalBox();
		vBox.add(classesPanel);
		vBox.add(freqPanel);
		vBox.add(showPanel);
		
		histogramPanel = new JPanel(new BorderLayout());
		histogramPanel.add(vBox, BorderLayout.NORTH);
		histogramPanel.setBorder(BorderFactory.createEmptyBorder());
		
	}

	


	private void createScatterplotPanel(){

		// create components
		ckShowLines = new JCheckBox();		
		ckShowLines.addActionListener(this);

		// layout
		Box p = Box.createVerticalBox();
		p.add(insetPanel(tab1,ckShowLines));	

		scatterplotPanel = new JPanel(new BorderLayout());
		scatterplotPanel.add(p, BorderLayout.NORTH);	
	}




	private void createGraphPanel(){

		int fieldWidth = 8;

		// create components
		ckAutoWindow = new JCheckBox();		
		ckAutoWindow.addActionListener(this);

		ckShowGrid = new JCheckBox();		
		ckShowGrid.addActionListener(this);

		lblXMin = new JLabel();
		fldXMin = new MyTextField(app,fieldWidth);
		fldXMin.setEditable(true);
		fldXMin.addActionListener(this);
		fldXMin.addFocusListener(this);
		
		lblXMax = new JLabel();
		fldXMax = new MyTextField(app,fieldWidth);
		fldXMax.addActionListener(this);
		fldXMax.addFocusListener(this);
		
		lblYMin = new JLabel();
		fldYMin = new MyTextField(app,fieldWidth);
		fldYMin.addActionListener(this);
		fldYMin.addFocusListener(this);
		
		lblYMax = new JLabel();
		fldYMax = new MyTextField(app,fieldWidth);
		fldYMax.addActionListener(this);
		fldYMax.addFocusListener(this);
		
		lblXInterval = new JLabel();
		fldXInterval = new MyTextField(app,fieldWidth);
		fldXInterval.addActionListener(this);
		fldXInterval.addFocusListener(this);
		
		lblYInterval = new JLabel();
		fldYInterval = new MyTextField(app,fieldWidth);
		fldYInterval.addActionListener(this);
		fldYInterval.addFocusListener(this);
		
		// create graph options panel
		JPanel graphOptionsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.weightx=1;
		c.anchor=GridBagConstraints.LINE_START;
		graphOptionsPanel.add(ckShowGrid,c);
		c.insets = new Insets(0,0,4,0);
		graphOptionsPanel.add(ckAutoWindow,c);
		
		
		// create window dimensions panel
		dimPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx=0;
		c1.gridy=0;
		c1.weightx=0;
		c1.insets = new Insets(2,10,0,0);
		c1.anchor=GridBagConstraints.EAST;
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx=1;
		c2.gridy=0;
		c2.weightx=1;
		c2.insets = c1.insets;
		c2.anchor=GridBagConstraints.WEST;
		
		// x dimensions
		dimPanel.add(lblXMin,c1);
		dimPanel.add(fldXMin,c2);
		
		c1.gridy ++; c2.gridy ++;
		dimPanel.add(lblXMax,c1);
		dimPanel.add(fldXMax,c2);
		
		c1.gridy ++; c2.gridy ++;
		dimPanel.add(lblXInterval,c1);
		dimPanel.add(fldXInterval,c2);
		
		// y dimensions
		c1.insets.top += 8; // add vertical gap
		c1.gridy ++; c2.gridy ++;
		dimPanel.add(lblYMin,c1);
		dimPanel.add(fldYMin,c2);
		c1.insets.top -= 8; // remove vertical gap
		
		c1.gridy ++; c2.gridy ++;
		dimPanel.add(lblYMax,c1);
		dimPanel.add(fldYMax,c2);
		
		c1.gridy ++; c2.gridy ++;
		dimPanel.add(lblYInterval,c1);
		dimPanel.add(fldYInterval,c2);
		
		
		// put the sub-panels together
		Box vBox = Box.createVerticalBox();
		vBox.add(graphOptionsPanel);
		vBox.add(dimPanel);
		
		graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(vBox, BorderLayout.NORTH);
		graphPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	private static JComponent insetPanelRight(int inset, JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.add(Box.createRigidArea(new Dimension(10,0)));
		p.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return p;
	}

	private static JComponent insetPanel(int inset, JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return p;
	}

	public void setLabels(){
		
		// titled borders
		classesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Classes")));				
		showPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Show")));
		freqPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("FrequencyType")));
		dimPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Dimensions")));
		
		// histogram options
		ckManual.setText(app.getMenu("SetClasssesManually"));		
		lblFreqType.setText(app.getMenu("FrequencyType") + ":");

		rbFreq.setText(app.getMenu("Count"));
		rbNormalized.setText(app.getMenu("Normalized"));
		rbRelative.setText(app.getMenu("Relative"));

		ckCumulative.setText(app.getMenu("Cumulative"));
		lblOverlay.setText(app.getMenu("Overlay"));
		ckOverlayNormal.setText(app.getMenu("NormalCurve"));
		ckOverlayPolygon.setText(app.getMenu("FrequencyPolygon"));
		ckShowFrequencyTable.setText(app.getMenu("FrequencyTable"));
		ckShowHistogram.setText(app.getMenu("Histogram"));

		lblClassRule.setText(app.getMenu("ClassRule") + ":");
		rbRightRule.setText(app.getMenu("RightClassRule"));
		rbLeftRule.setText(app.getMenu("LeftClassRule"));
		
		// graph options
		ckAutoWindow.setText(app.getMenu("AutoDimension"));
		ckShowGrid.setText(app.getPlain("ShowGrid"));
		lblXMin.setText(app.getPlain("xmin") + ":");
		lblXMax.setText(app.getPlain("xmax") + ":");
		lblYMin.setText(app.getPlain("ymin") + ":");
		lblYMax.setText(app.getPlain("ymax") + ":");

		lblXInterval.setText(app.getPlain("xstep") + ":");
		lblYInterval.setText(app.getPlain("ystep") + ":");

		ckShowLines.setText(app.getMenu("LineGraph"));
		
		repaint();
	}


	private void updateGUI(){

		isUpdating  = true;

		ckManual.setSelected(settings.useManualClasses);	
		
		rbFreq.setSelected(settings.frequencyType == StatPanelSettings.TYPE_COUNT);
		rbRelative.setSelected(settings.frequencyType == StatPanelSettings.TYPE_RELATIVE);
		rbNormalized.setSelected(settings.frequencyType == StatPanelSettings.TYPE_NORMALIZED);
		
		rbLeftRule.setSelected(settings.isLeftRule);
		
		ckCumulative.setSelected(settings.isCumulative);	
		ckOverlayNormal.setSelected(settings.hasOverlayNormal);	
		ckOverlayPolygon.setSelected(settings.hasOverlayPolygon);	
		ckShowGrid.setSelected(settings.showGrid);	
		ckAutoWindow.setSelected(settings.isAutomaticWindow);
		ckShowFrequencyTable.setSelected(settings.showFrequencyTable);
		ckShowHistogram.setSelected(settings.showHistogram);


		lblYMin.setVisible(showYSettings);
		fldYMin.setVisible(showYSettings);
		lblYMax.setVisible(showYSettings);
		fldYMax.setVisible(showYSettings);
		lblYInterval.setVisible(showYSettings);
		fldYInterval.setVisible(showYSettings);

		// enable/disable window dimension components
		dimPanel.setEnabled(!ckAutoWindow.isSelected());
		fldXMin.setEnabled(!ckAutoWindow.isSelected());
		fldXMax.setEnabled(!ckAutoWindow.isSelected());
		fldXInterval.setEnabled(!ckAutoWindow.isSelected());
		fldYMin.setEnabled(!ckAutoWindow.isSelected());
		fldYMax.setEnabled(!ckAutoWindow.isSelected());
		fldYInterval.setEnabled(!ckAutoWindow.isSelected());

		lblXMin.setEnabled(!ckAutoWindow.isSelected());
		lblXMax.setEnabled(!ckAutoWindow.isSelected());
		lblXInterval.setEnabled(!ckAutoWindow.isSelected());
		lblYMin.setEnabled(!ckAutoWindow.isSelected());
		lblYMax.setEnabled(!ckAutoWindow.isSelected());
		lblYInterval.setEnabled(!ckAutoWindow.isSelected());

		
		// enable/disable normal overlay option
		ckOverlayNormal.setEnabled(settings.frequencyType == StatPanelSettings.TYPE_NORMALIZED);
		
		if(ckAutoWindow.isSelected()){
			fldXMin.setText("" + statDialog.format(settings.xMin));
			fldXMax.setText("" + statDialog.format(settings.xMax));
			fldXInterval.setText("" + statDialog.format(settings.xAxesInterval));
			
			fldYMin.setText("" + statDialog.format(settings.yMin));
			fldYMax.setText("" + statDialog.format(settings.yMax));
			fldYInterval.setText("" + statDialog.format(settings.yAxesInterval));
			
		}

		isUpdating  = false;
		repaint();
	}


	private void doTextFieldActionPerformed(JTextField source) {
		if(isUpdating) return;
		try {
			String inputText = source.getText().trim();
			NumberValue nv;
			nv = app.getKernel().getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();

			if(source == fldXMin){
				settings.xMin = value;  
				firePropertyChange("settings", true, false);
			}
			else if(source == fldXMax){
				settings.xMax = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYMax){
				settings.yMax = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYMin){
				settings.yMin = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldXInterval){
				settings.xAxesInterval = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYInterval){
				settings.yAxesInterval = value;
				firePropertyChange("settings", true, false);
			}
						
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {

		if(isUpdating) return;

		Object source  = e.getSource();
		if(source instanceof JTextField){
			doTextFieldActionPerformed((JTextField) source);
		}

		else if(source == ckManual){
			settings.useManualClasses = ckManual.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckCumulative){
			settings.isCumulative = ckCumulative.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == rbFreq){
			settings.frequencyType = StatPanelSettings.TYPE_COUNT;
			firePropertyChange("settings", true, false);
		}
		else if(source == rbRelative){
			settings.frequencyType = StatPanelSettings.TYPE_RELATIVE;
			firePropertyChange("settings", true, false);
		}
		else if(source == rbNormalized){
			settings.frequencyType = StatPanelSettings.TYPE_NORMALIZED;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckOverlayNormal){
			settings.hasOverlayNormal = ckOverlayNormal.isSelected();
			firePropertyChange("settings", true, false);
		}	
		else if(source == ckOverlayPolygon){
			settings.hasOverlayPolygon = ckOverlayPolygon.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckShowGrid){
			settings.showGrid = ckShowGrid.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckAutoWindow){
			settings.isAutomaticWindow = ckAutoWindow.isSelected();
			
			settings.xAxesIntervalAuto = ckAutoWindow.isSelected();
			settings.yAxesIntervalAuto = ckAutoWindow.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckShowFrequencyTable){
			settings.showFrequencyTable = ckShowFrequencyTable.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckShowHistogram){
			settings.showHistogram = ckShowHistogram.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == rbLeftRule || source == rbRightRule){
			settings.isLeftRule = rbLeftRule.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == this.ckShowLines){
			settings.showScatterplotLine = ckShowLines.isSelected();
			firePropertyChange("settings", true, false);
		}
		
		else{
			firePropertyChange("settings", true, false);
		}

		updateGUI();
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void focusLost(FocusEvent e) {
		if(e.getSource() instanceof JTextField){
			doTextFieldActionPerformed((JTextField)(e.getSource()));
		}
	}


	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}


	public void updatePanel() {
		// TODO Auto-generated method stub
		
	}


}
