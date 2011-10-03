package geogebra3D.gui;


import geogebra.gui.OptionsEuclidian;
import geogebra.gui.inputfield.MyTextField;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class OptionsEuclidian3D extends OptionsEuclidian {
	
	private AxisPanel3D zAxisPanel;

	public OptionsEuclidian3D(Application3D app) {
		super(app, app.getEuclidianView3D());
		

		enableStuff(false);
		
		updateGUI();
		
	}

	private void enableStuff(boolean flag){
		//	TODO remove when implemented
		

		//btBackgroundColor.setEnabled(flag);

		tfMinX.setEnabled(flag);
		tfMaxX.setEnabled(flag);
		tfMinY.setEnabled(flag);
		tfMaxY.setEnabled(flag);

		btAxesColor.setEnabled(flag);
		cbAxesStyle.setEnabled(flag);
		cbShowMouseCoords.setEnabled(flag);
		cbTooltips.setEnabled(flag);
		
		((AxisPanel3D) xAxisPanel).enableStuff(flag);
		((AxisPanel3D) yAxisPanel).enableStuff(flag);
		zAxisPanel.enableStuff(flag);
		
		

		cbGridManualTick.setEnabled(flag);  
        ncbGridTickX.setEnabled(flag); 
		ncbGridTickY.setEnabled(flag);
		cbGridTickAngle.setEnabled(flag);  
		cbGridStyle.setEnabled(flag);  
		cbGridType.setEnabled(flag);  
		cbBoldGrid.setEnabled(flag); 
		btGridColor.setEnabled(flag); 
		
	}

	
	
	protected void setViewFromIndex(int index){
		
		/*
		switch(index){
		case 0:
		default:
			setView(app.getEuclidianView());
			break;
		case 1:
			setView(app.getGuiManager().getEuclidianView2());
			break;
		case 2:
			setView(((Application3D) app).getEuclidianView3D());
			break;
		}
		*/
		
	}
	
	public void updateGUI() {
		super.updateGUI();
		

		zAxisPanel.updatePanel();
		
		//projection
		//tfPersp.removeActionListener(this);
		tfPersp.setText(""+((EuclidianView3D) view).getProjectionPerspectiveValue());
		//tfPersp.addActionListener(this);
		
		tfAnaglyph.setText(""+((EuclidianView3D) view).getEyeSepFactor()*1000);
		cbAnaglyphGray.setSelected(((EuclidianView3D) view).isAnaglyphGrayScaled());
		cbAnaglyphShutDownGreen.setSelected(((EuclidianView3D) view).isAnaglyphShutDownGreen());
		
		
		tfCavAngle.setText(""+((EuclidianView3D) view).getCavAngle());
		tfCavFactor.setText(""+((EuclidianView3D) view).getCavFactor());
		
		
		
		
	}
	
	protected void setLabelsForCbView(){
		/*
		super.addItemsToCbView();
		cbView.addItem(app.getPlain("GraphicsView3D"));
		*/
	}
	
	protected void createCbView(){
	}
	
	protected void addCbView(){
	}
	
	protected void updateGUIforCbView(){
		
	}
	
	protected void updateMinMax(){
		super.updateMinMax();
	}
	
	protected void setCbViewSelectedIndex(){
		/*
		if(view == app.getEuclidianView())
    		cbView.setSelectedIndex(0);
		else if (view == app.getGuiManager().getEuclidianView2() )
			cbView.setSelectedIndex(1);
		else
			cbView.setSelectedIndex(2);
		 */
	}
	
	

	protected void initAxisPanels(){

        xAxisPanel = new AxisPanel3D(0);
        yAxisPanel = new AxisPanel3D(1);
        zAxisPanel = new AxisPanel3D(2);
        
	}
	

	protected void addTabs(){
		super.addTabs();
		tabbedPane.addTab("" , buildProjectionPanel() );
	}
	

	protected void addAxisTabs(){
		super.addAxisTabs();
		tabbedPane.addTab("" , zAxisPanel );
	}
	
	
	private JLabel[] projectionLabel;
	private JTextField tfPersp, tfAnaglyph, tfCavAngle, tfCavFactor;
	private JLabel tfPerspLabel, tfAnaglyphLabel, tfCavAngleLabel, tfCavFactorLabel;
	private ProjectionButtons projectionButtons;
	private JCheckBox cbAnaglyphGray;
	private JLabel cbAnaglyphGrayLabel;
	private JCheckBox cbAnaglyphShutDownGreen;
	private JLabel cbAnaglyphShutDownGreenLabel;
	
	
	private class ProjectionButtons {
		
		private JButton[] buttons;
		
		private int buttonSelected;
		
		private EuclidianView3D view;
		
		private ProjectionButtons(OptionsEuclidian3D options){
			
			view = (EuclidianView3D) options.view;
			
			buttons = new JButton[4];
			
			buttons[EuclidianView3D.PROJECTION_ORTHOGRAPHIC] =  new JButton(app.getImageIcon("stylebar_vieworthographic.gif"));
			buttons[EuclidianView3D.PROJECTION_PERSPECTIVE] =  new JButton(app.getImageIcon("stylebar_viewperspective.gif"));
			buttons[EuclidianView3D.PROJECTION_ANAGLYPH] =  new JButton(app.getImageIcon("stylebar_viewanaglyph.gif"));
			buttons[EuclidianView3D.PROJECTION_CAV] =  new JButton(app.getImageIcon("stylebar_viewcav.gif"));
			
			for (int i=0; i<4; i++)
				buttons[i].addActionListener(options);			
			
			buttonSelected = view.getProjection();
			buttons[buttonSelected].setSelected(true);
		}
		
		private JButton getButton(int i){
			return buttons[i];
		}
		
		private void setSelected(int i){
			buttons[buttonSelected].setSelected(false);
			buttonSelected = i;
			buttons[buttonSelected].setSelected(true);
			
		}
		
		
	}
	
	private JPanel buildProjectionPanel() {
		
		//JLabel label;		
			
		
		projectionLabel = new JLabel[4]; // "orthographic", "perspective", "anaglyph" etc.
		for(int i=0;i<4;i++)
			projectionLabel[i] = new JLabel("");
		
		projectionButtons = new ProjectionButtons(this);
		
		
        JPanel orthoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        orthoPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC));
        orthoPanel.add(projectionLabel[0]);
            
 		
        JPanel perspPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        perspPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE));
        perspPanel.add(projectionLabel[1]);
        tfPerspLabel = new JLabel("");
        perspPanel.add(tfPerspLabel);
        tfPersp = new MyTextField(app,5);
        tfPersp.addActionListener(this);
        tfPersp.addFocusListener(this);
        perspPanel.add(tfPersp);
        
        
        JPanel anaglyphPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        anaglyphPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_ANAGLYPH));
        anaglyphPanel.add(projectionLabel[2]);
        tfAnaglyphLabel = new JLabel("");
        anaglyphPanel.add(tfAnaglyphLabel);
        tfAnaglyph = new MyTextField(app,3);
        tfAnaglyph.addActionListener(this);
        tfAnaglyph.addFocusListener(this);
        anaglyphPanel.add(tfAnaglyph);
        cbAnaglyphGray = new JCheckBox();
        cbAnaglyphGray.addActionListener(this);
        cbAnaglyphGrayLabel = new JLabel("");
        anaglyphPanel.add(cbAnaglyphGray);
        anaglyphPanel.add(cbAnaglyphGrayLabel);
        cbAnaglyphShutDownGreen = new JCheckBox();
        cbAnaglyphShutDownGreen.addActionListener(this);
        cbAnaglyphShutDownGreenLabel = new JLabel("");
        anaglyphPanel.add(cbAnaglyphShutDownGreen);
        anaglyphPanel.add(cbAnaglyphShutDownGreenLabel);
           
        

		
        JPanel cavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        cavPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_CAV));
        cavPanel.add(projectionLabel[EuclidianView3D.PROJECTION_CAV]);
        tfCavAngleLabel = new JLabel("");
        cavPanel.add(tfCavAngleLabel);
        tfCavAngle = new MyTextField(app,4);
        tfCavAngle.addActionListener(this);
        tfCavAngle.addFocusListener(this);
        cavPanel.add(tfCavAngle);
        tfCavFactorLabel = new JLabel("");
        cavPanel.add(tfCavFactorLabel);
        tfCavFactor = new MyTextField(app,4);
        tfCavFactor.addActionListener(this);
        tfCavFactor.addFocusListener(this);
        cavPanel.add(tfCavFactor);
 		
        

		//==========================================
		// create basic panel and add all sub panels
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,2,5));
        
		/*
        northPanel.add(dimPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(axesOptionsPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(bgPanel);
        */
		northPanel.add(orthoPanel);
		northPanel.add(perspPanel);
		northPanel.add(anaglyphPanel);
		northPanel.add(cavPanel);

        // use a BorderLayout to keep sub panels together
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(northPanel, BorderLayout.NORTH);
        
        return ret;
		
	}
	
	

	protected void setTabLabels(){
		tabbedPane.setTitleAt(0,app.getMenu("Properties.Basic"));
        tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
        tabbedPane.setTitleAt(2, app.getPlain("yAxis"));   
        tabbedPane.setTitleAt(3, app.getPlain("zAxis"));   
        tabbedPane.setTitleAt(4, app.getMenu("Grid"));	
        tabbedPane.setTitleAt(5, app.getMenu("Projection"));	
	}
	
	
	public void setLabels(){
		super.setLabels();
		
		//perspective tab
		projectionLabel[0].setText(app.getPlain("orthographic"));
		
		projectionLabel[1].setText(app.getPlain("perspective")+":");
		tfPerspLabel.setText(app.getPlain("eyeDistance")+":");
		
		projectionLabel[2].setText(app.getPlain("anaglyph")+":");
		tfAnaglyphLabel.setText(app.getPlain("eyesSeparation")+":");
		cbAnaglyphGrayLabel.setText(app.getPlain("grayScale"));
		cbAnaglyphShutDownGreenLabel.setText(app.getPlain("shutDownGreen"));
		
		projectionLabel[3].setText(app.getPlain("oblique")+":");
		tfCavAngleLabel.setText(app.getPlain("angle")+":");
		tfCavFactorLabel.setText(app.getPlain("factor")+":");
		
		projectionButtons.setSelected(((EuclidianView3D) view).getProjection());
	}
	
	
	
	
	protected void doActionPerformed(Object source) {	
		
		if (source == tfPersp) {
			try{
				double val = Double.parseDouble(tfPersp.getText());
				if (! Double.isNaN(val)) {
					double min =1;
					if (val<min){
						val=min;
						tfPersp.setText(""+val);
					}

					((EuclidianView3D) view).setProjectionPerspectiveValue(val);	
				}
			}catch(NumberFormatException e){
				tfPersp.setText(""+((EuclidianView3D) view).getProjectionPerspectiveValue());
			}
		}else if (source == tfAnaglyph) {
			try{
				double val = Double.parseDouble(tfAnaglyph.getText());
				if (! Double.isNaN(val)) {
					if (val<0){
						val=0;
						tfAnaglyph.setText(""+val);
					}

					((EuclidianView3D) view).setEyeSepFactor(val/1000);	
				}
			}catch(NumberFormatException e){
				tfAnaglyph.setText(""+((EuclidianView3D) view).getEyeSepFactor()*1000);
			}
		}else if (source == tfCavAngle) {
			try{
				double val = Double.parseDouble(tfCavAngle.getText());
				if (! Double.isNaN(val)) {

					((EuclidianView3D) view).setCavAngle(val);
				}
			}catch(NumberFormatException e){
				tfCavAngle.setText(""+((EuclidianView3D) view).getCavAngle());
			}
		}else if (source == tfCavFactor) {
			try{
				double val = Double.parseDouble(tfCavFactor.getText());
				if (! Double.isNaN(val)) {
					if (val<0){
						val=0;
						tfCavFactor.setText(""+val);
					}
					((EuclidianView3D) view).setCavFactor(val);
				}
			}catch(NumberFormatException e){
				tfCavFactor.setText(""+((EuclidianView3D) view).getCavFactor());
			}
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC)) {
			((EuclidianView3D) view).setProjectionOrthographic();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_ORTHOGRAPHIC);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE)) {
			((EuclidianView3D) view).setProjectionPerspective();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_PERSPECTIVE);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_ANAGLYPH)) {
			((EuclidianView3D) view).setAnaglyph();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_ANAGLYPH);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_CAV)) {
			((EuclidianView3D) view).setCav();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_CAV);
		}else if (source == cbAnaglyphGray) {
			((EuclidianView3D) view).setAnaglyphGrayScaled(cbAnaglyphGray.isSelected());
		/*
		}else if (source == cbAnaglyphGrayLabel) {
			boolean flag = !cbAnaglyphGray.isSelected();
			cbAnaglyphGray.setSelected(flag);
			((EuclidianView3D) view).setAnaglyphGrayScaled(flag);
			*/
		}else if (source == cbAnaglyphShutDownGreen) {
			((EuclidianView3D) view).setAnaglyphShutDownGreen(cbAnaglyphShutDownGreen.isSelected());
		
		
			
		}else super.doActionPerformed(source);
		
	}
	
	
	
	
	
	protected class AxisPanel3D extends AxisPanel{
		
		final static protected int AXIS_Z = 2;

		public AxisPanel3D(int axis) {
			super(axis);
		}
		
		protected void enableStuff(boolean flag){
//			TODO remove when implemented
			
			cbAxisNumber.setEnabled(flag);
			cbManualTicks.setEnabled(flag);
			cbPositiveAxis.setEnabled(flag);
			cbDrawAtBorder.setEnabled(flag);
			ncbTickDist.setEnabled(flag);	
		    cbTickStyle.setEnabled(flag); 
		    cbAxisLabel.setEnabled(flag);
		    cbUnitLabel.setEnabled(flag);
			tfCross.setEnabled(flag);
			
			
		}
		
		protected String getString(){
			if (axis==AXIS_Z)
				return "zAxis";
			else
				return super.getString();
		}
		
		
	}
	

	
	
}
