package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * @author gsturr
 *
 */
public class StatDialogStyleBar extends JToolBar implements ActionListener{

	private AppD app;
	private StatDialog statDialog;
	protected int iconHeight = 18;
	private JButton btnRounding, btnPrint;
	private JToggleButton btnShowStatistics, btnShowPlot2, btnShowData;
	private JPopupMenu roundingPopup;
	
	public StatDialogStyleBar(AppD app, StatDialog statDialog){
		
		this.statDialog = statDialog;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();
	}
	
	private void createGUI(){
		this.removeAll();	
	//	buildOptionsButton();
		
		btnPrint = new JButton(app.getImageIcon("document-print.png"));
		btnPrint.addActionListener(this);
		btnPrint.setFocusPainted(false);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false);
		btnPrint.setFocusable(false);
		btnPrint.setRolloverEnabled(true);
		btnPrint.setRolloverIcon(app.getImageIcon("document-print.png"));
				
		btnShowStatistics = new JToggleButton(app.getImageIcon("dataview-showstatistics.png"));
		btnShowStatistics.addActionListener(this);
		btnShowStatistics.setFocusPainted(false);
		btnShowStatistics.setFocusable(false);
		btnShowStatistics.setRolloverEnabled(true);
		btnShowStatistics.setRolloverIcon(app.getImageIcon("dataview-showstatistics.png"	));
		
		btnShowData = new JToggleButton(app.getImageIcon("dataview-showdata.png"));
		btnShowData.addActionListener(this);
		btnShowData.setFocusPainted(false);
		btnShowData.setFocusable(false);
		btnShowData.setRolloverEnabled(true);
		btnShowData.setRolloverIcon(app.getImageIcon("dataview-showdata.png"));
		
		btnShowPlot2 = new JToggleButton(app.getImageIcon("dataview-showplot2.png"));
		btnShowPlot2.addActionListener(this);
		btnShowPlot2.setFocusPainted(false);
		btnShowPlot2.setFocusable(false);
		btnShowPlot2.setRolloverEnabled(true);
		btnShowPlot2.setRolloverIcon(app.getImageIcon("dataview-showplot2.png"));

		buildRoundingButton();
			
		//add(btnRounding); 
		add(btnShowStatistics); 
		add(btnShowData);
		add(btnShowPlot2); 
		//addSeparator();
		//add(btnPrint);
		
	}
	
	public void updateGUI(){
		
		btnShowStatistics.setSelected(statDialog.showStatPanel());
		btnShowData.setSelected(statDialog.showDataPanel());
		btnShowPlot2.setSelected(statDialog.showComboPanel2());

	}
	
	/** 
	 * Builds popup button with options menu items 
	 */
	private void buildRoundingButton(){

		btnRounding = new JButton(app.getImageIcon("triangle-down.png"));	
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT); 
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
	
		/*
		roundingPopup = createRoundingPopup();
		
		btnRounding.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// popup appears below the button
				roundingPopup.show(getParent(), btnRounding.getLocation().x,btnRounding.getLocation().y + btnRounding.getHeight());
			}
		});
	
		updateMenuDecimalPlaces(roundingPopup);
	*/
		
	}
	
	public void setLabels(){
		btnRounding.setText(app.getMenu(".xx"));
		btnShowStatistics.setToolTipText(app.getMenu("ShowStatistics"));	
		btnShowData.setToolTipText(app.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(app.getMenu("ShowPlot2"));
		btnPrint.setToolTipText(app.getMenu("Print"));
	}

	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btnShowStatistics){
			statDialog.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		}
		if(source == btnShowData){
			statDialog.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		}
		
		if(source == btnShowPlot2){
			statDialog.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		}
		
		if(source == btnPrint){
			statDialog.doPrint();
		}

	}

}
