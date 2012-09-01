package geogebra.gui.dialog.options;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.util.TableSymbols;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.NumberComboBox;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AxisPanel extends JPanel implements ActionListener, ItemListener, FocusListener, SetLabels {		

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public static final String PI_STR = "\u03c0";
			public static final String DEGREE_STR = "\u00b0";
			

			protected int axis;		
			protected JCheckBox cbShowAxis, cbAxisNumber, cbManualTicks, cbPositiveAxis, cbDrawAtBorder;
			protected NumberComboBox ncbTickDist;	
			protected JComboBox cbTickStyle, cbAxisLabel, cbUnitLabel;
			protected JTextField tfCross;

			private JLabel crossAt;

			private JLabel axisTicks;


			private JLabel axisLabel;

			private JLabel axisUnitLabel;

			private JLabel stickToEdge;

			private AppD app;
			protected EuclidianViewND view;
			
			final static protected int AXIS_X = 0;
			final static protected int AXIS_Y = 1;
			
			/******************************************************
			 * @param app
			 * @param view
			 * @param axis
			 */
			public AxisPanel(AppD app, EuclidianViewND view, int axis) {
				
				this.app = app;
				this.axis = axis;			
					this.view = view;
				setLayout(new FullWidthLayout());
				
				String strAxisEn = getString();		
				this.setBorder(LayoutUtil.titleBorder(app.getPlain(strAxisEn)));	
										
				// show axis
				cbShowAxis = new JCheckBox(app.getPlain("Show"+strAxisEn));	
				cbShowAxis.addActionListener(this);		
				JPanel showAxisPanel = LayoutUtil.flowPanel(cbShowAxis);
				
				// show numbers
				cbAxisNumber = new JCheckBox(app.getPlain("ShowAxisNumbers"));	
				cbAxisNumber.addActionListener(this);
				JPanel numberPanel = LayoutUtil.flowPanel(cbAxisNumber);
				
				// show positive axis only
				cbPositiveAxis = new JCheckBox(app.getPlain("PositiveDirectionOnly"));
				cbPositiveAxis.addActionListener(this);
				JPanel showPosPanel = LayoutUtil.flowPanel(cbPositiveAxis);
				
				// ticks	
				axisTicks = new JLabel(app.getPlain("AxisTicks") + ":");
				cbTickStyle = new JComboBox();			
				char big = '|';
				char small = '\'';
				cbTickStyle.addItem(" " + big + "  " + small + "  " + big + "  " + small + "  " + big); // major and minor ticks
				cbTickStyle.addItem( " " + big + "     " + big + "     " + big); // major ticks only
				cbTickStyle.addItem(""); // no ticks
				cbTickStyle.addActionListener(this);
				cbTickStyle.setEditable(false);
				JPanel showTicksPanel = LayoutUtil.flowPanel(axisTicks,cbTickStyle);
										
				// distance
				cbManualTicks = new JCheckBox(app.getPlain("TickDistance") + ":");
				cbManualTicks.addActionListener(this);
				ncbTickDist = new NumberComboBox(app);
				ncbTickDist.addItemListener(this);
				JPanel distancePanel = LayoutUtil.flowPanel(cbManualTicks,ncbTickDist);
				
				// axis and unit label
				cbAxisLabel = new JComboBox();
				cbAxisLabel.addItem(null);
				cbAxisLabel.addItem(axis == 0 ? "x" : "y");
				String [] greeks = TableSymbols.greekLowerCase;
				for (int i = 0; i < greeks.length; i++) {
					cbAxisLabel.addItem(greeks[i]);		
				}						
				cbAxisLabel.addActionListener(this);
				cbAxisLabel.setEditable(true);
				axisLabel = new JLabel(app.getPlain("AxisLabel") + ":");
					
				axisUnitLabel = new JLabel(app.getPlain("AxisUnitLabel") + ":"); 
				cbUnitLabel = new JComboBox();
				cbUnitLabel.setEditable(true);
				cbUnitLabel.addItem(null);
				cbUnitLabel.addItem(DEGREE_STR); // degrees			
				cbUnitLabel.addItem(PI_STR); // pi				
				cbUnitLabel.addItem("mm");
				cbUnitLabel.addItem("cm");
				cbUnitLabel.addItem("m");
				cbUnitLabel.addItem("km");
				cbUnitLabel.addActionListener(this);
				
				JPanel labelPanel = LayoutUtil.flowPanel(axisLabel, cbAxisLabel, axisUnitLabel,cbUnitLabel);
				
				// cross at and stick to edge
				tfCross = new MyTextField(app,6);
				tfCross.addActionListener(this);
				crossAt = new JLabel(app.getPlain("CrossAt") + ":");
				cbDrawAtBorder = new JCheckBox();
				cbDrawAtBorder.addActionListener(this);
				stickToEdge = new JLabel(app.getPlain("StickToEdge"));
				
				JPanel crossPanel  = LayoutUtil.flowPanel(crossAt, tfCross,cbDrawAtBorder,stickToEdge);
				
							
				// add all panels
				add(showAxisPanel);
				add(numberPanel);
				add(showPosPanel);
				add(distancePanel);
				add(showTicksPanel);
				add(labelPanel);
				add(crossPanel);
					
				updatePanel();
			}
			
			protected String getString(){
				return  (axis == AXIS_X) ? "xAxis" : "yAxis";
			}
			
			public void actionPerformed(ActionEvent e) {	
				doActionPerformed(e.getSource());		
			}
			
			private void doActionPerformed(Object source) {	
							
				if (source == cbShowAxis) {
					if (app.getEuclidianView1() == view)
						app.getSettings().getEuclidian(1).setShowAxis(axis, cbShowAxis.isSelected());
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setShowAxis(axis, cbShowAxis.isSelected(), true);
					else if (app.getEuclidianView2() == view)
						app.getSettings().getEuclidian(2).setShowAxis(axis, cbShowAxis.isSelected());
					else
						view.setShowAxis(axis, cbShowAxis.isSelected(), true);
				} 
				
				else if (source == cbAxisNumber) {
					boolean [] show = view.getShowAxesNumbers();
					show[axis] = cbAxisNumber.isSelected();
					view.setShowAxesNumbers(show); 
				}

				else if (source == cbManualTicks) {

					if (app.getEuclidianView1() == view)
						app.getSettings().getEuclidian(1).setAutomaticAxesNumberingDistance(!cbManualTicks.isSelected(), axis, true);
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setAutomaticAxesNumberingDistance(!cbManualTicks.isSelected(), axis);
					else if (app.getEuclidianView2() == view)
						app.getSettings().getEuclidian(2).setAutomaticAxesNumberingDistance(!cbManualTicks.isSelected(), axis, true);
					else
						view.setAutomaticAxesNumberingDistance(!cbManualTicks.isSelected(), axis);
				}

				else if (source == cbUnitLabel) {
					Object ob = cbUnitLabel.getSelectedItem();
					String text =  (ob == null) ? null : ob.toString().trim();
					String [] labels = view.getAxesUnitLabels();
					labels[axis] = text;
					view.setAxesUnitLabels(labels);
				}

				else if (source == cbAxisLabel) {
					Object ob = cbAxisLabel.getSelectedItem();
					String text =  (ob == null) ? null : ob.toString().trim();
					view.setAxisLabel(axis,text);
				}
				
				else if (source == cbTickStyle) {
					int type = cbTickStyle.getSelectedIndex();
					int [] styles = view.getAxesTickStyles();
					styles[axis] = type;

					if (app.getEuclidianView1() == view)
						app.getSettings().getEuclidian(1).setAxisTickStyle(axis, type);
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setAxesTickStyles(styles);
					else if (app.getEuclidianView2() == view)
						app.getSettings().getEuclidian(2).setAxisTickStyle(axis, type);
					else
						view.setAxesTickStyles(styles);
				}	

				else if (source == tfCross) {		
					String str = tfCross.getText();
					if ("".equals(str)) str = "0";
					double cross = parseDouble(str);	
					if (!(Double.isInfinite(cross) || Double.isNaN(cross))) {
						double[] ac = view.getAxesCross();
						ac[axis] = cross;

						if (app.getEuclidianView1() == view)
							app.getSettings().getEuclidian(1).setAxisCross(axis, cross);
						else if (!app.hasEuclidianView2EitherShowingOrNot())
							view.setAxesCross(ac);
						else if (app.getEuclidianView2() == view)
							app.getSettings().getEuclidian(2).setAxisCross(axis, cross);
						else
							view.setAxesCross(ac);
					}

					tfCross.setText(""+ view.getAxesCross()[axis]);
				}

				else if (source == cbPositiveAxis) {
					if (view == app.getEuclidianView1())
						app.getSettings().getEuclidian(1).setPositiveAxis(axis, cbPositiveAxis.isSelected());
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setPositiveAxis(axis, cbPositiveAxis.isSelected());
					else if (view == app.getEuclidianView2())
						app.getSettings().getEuclidian(2).setPositiveAxis(axis, cbPositiveAxis.isSelected());
					else
						view.setPositiveAxis(axis, cbPositiveAxis.isSelected());
				}
				else if (source == cbDrawAtBorder) {
					boolean[] border = view.getDrawBorderAxes();				
					border[axis] = cbDrawAtBorder.isSelected();		
					view.setDrawBorderAxes(border);
					if(!cbDrawAtBorder.isSelected())
						view.setAxisCross(axis, 0.0);
				}		
				
				
				view.updateBackground();			
				updatePanel();
			}
			
			
			public void itemStateChanged(ItemEvent e) {
			
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				Object source = e.getSource();
				if (source == ncbTickDist) {
					double val = ncbTickDist.getValue();
					if (val > 0) {
						if (app.getEuclidianView1() == view)
							app.getSettings().getEuclidian(1).setAxesNumberingDistance(val, axis);
						else if (!app.hasEuclidianView2EitherShowingOrNot())
							view.setAxesNumberingDistance(val, axis);
						else if (app.getEuclidianView2() == view)
							app.getSettings().getEuclidian(2).setAxesNumberingDistance(val, axis);
						else
							view.setAxesNumberingDistance(val, axis);
					}
				}			
							
				view.updateBackground();			
				updatePanel();
			}
					
			
			public void updatePanel() {		
				cbAxisNumber.removeActionListener(this);
			 	cbAxisNumber.setSelected(view.getShowAxesNumbers()[axis]);
			 	cbAxisNumber.addActionListener(this);
			 	
			 	cbManualTicks.removeActionListener(this);
			 	ncbTickDist.removeItemListener(this);
			 	
			 	cbManualTicks.setSelected(!view.isAutomaticAxesNumberingDistance()[axis]);		 			 			 
			 	ncbTickDist.setValue(view.getAxesNumberingDistances()[axis]);
			 	ncbTickDist.setEnabled(cbManualTicks.isSelected());
			 	
			 	cbManualTicks.addActionListener(this);		 	
			 	ncbTickDist.addItemListener(this);	
			 	
			 	cbAxisLabel.removeActionListener(this);
			 	cbAxisLabel.setSelectedItem(view.axisLabelForXML(axis));
			 	cbAxisLabel.addActionListener(this);
			 	
			 	cbUnitLabel.removeActionListener(this);
			 	cbUnitLabel.setSelectedItem(view.getAxesUnitLabels()[axis]);
			 	cbUnitLabel.addActionListener(this);
			 
			 	/*
			    cbShowAxis.removeActionListener(this);
		        cbShowAxis.setSelected(view.getShowXaxis());
		        cbShowAxis.addActionListener(this);	        
		        */
			 	
		        cbTickStyle.removeActionListener(this);
		        int type = view.getAxesTickStyles()[axis];
		        cbTickStyle.setSelectedIndex(type);	        
		        cbTickStyle.addActionListener(this);
		        
		        
		        cbShowAxis.removeActionListener(this);
		        //cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() : view.getShowYaxis());
		        cbShowAxis.setSelected(view.getShowAxis(axis));
		        cbShowAxis.addActionListener(this);
		        
		       
		        tfCross.removeActionListener(this);     
		        if(view.getDrawBorderAxes()[axis])
		        	tfCross.setText("");
		        else
		        	tfCross.setText(""+ view.getAxesCross()[axis]);
		        tfCross.setEnabled(!view.getDrawBorderAxes()[axis]);
		        tfCross.addActionListener(this);
		        tfCross.addFocusListener(this);


		        cbPositiveAxis.removeActionListener(this);
		        cbPositiveAxis.setSelected(view.getPositiveAxes()[axis]);
		        cbPositiveAxis.addActionListener(this);


		        cbDrawAtBorder.removeActionListener(this);
		        cbDrawAtBorder.setSelected(view.getDrawBorderAxes()[axis]);
		        cbDrawAtBorder.addActionListener(this);

		        
		        
			}

			public void focusGained(FocusEvent e) {
				//do nothing
			}

			public void focusLost(FocusEvent e) {
				// (needed for textfields)
				doActionPerformed(e.getSource());
			}

			public void setLabels() {
				String strAxisEn = getString();		
				this.setBorder(LayoutUtil.titleBorder(app.getPlain(strAxisEn)));	
				this.setBorder(LayoutUtil.titleBorder(null));
				cbShowAxis.setText(app.getPlain("Show"+strAxisEn));		
				cbAxisNumber.setText(app.getPlain("ShowAxisNumbers"));					
				cbManualTicks.setText(app.getPlain("TickDistance") + ":");
				axisTicks.setText(app.getPlain("AxisTicks") + ":");
				cbPositiveAxis.setText(app.getPlain("PositiveDirectionOnly"));
				axisLabel.setText(app.getPlain("AxisLabel") + ":");
				axisUnitLabel.setText(app.getPlain("AxisUnitLabel") + ":"); 
				crossAt.setText(app.getPlain("CrossAt") + ":");
				stickToEdge.setText(app.getPlain("StickToEdge"));
				
			}	
			
			protected double parseDouble(String text) {	
				if (text == null || text.equals("")) 
					return Double.NaN;
				return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);	
			}	
			
			
			public void updateFont() {
				Font font = app.getPlainFont();
				
				setFont(font);
				
				cbShowAxis.setFont(font);	
				cbAxisNumber.setFont(font);			
				cbManualTicks.setFont(font);
				axisTicks.setFont(font);
				cbPositiveAxis.setFont(font);
				axisLabel.setFont(font);
				axisUnitLabel.setFont(font);
				crossAt.setFont(font);
				stickToEdge.setFont(font);
				
				ncbTickDist.setFont(font);
				
				cbTickStyle.setFont(font);
				cbAxisLabel.setFont(font);
				cbUnitLabel.setFont(font);
				
				tfCross.setFont(font);
			}

}
