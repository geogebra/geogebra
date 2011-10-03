package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FrequencyTable extends JTable implements StatPanelInterface{


	protected Application app;
	private Kernel kernel; 
	protected StatDialog statDialog;
	private int mode;
	protected StatTable statTable;

	private StatPanelSettings settings;



	public FrequencyTable(Application app, StatDialog statDialog){
		this.app = app;	
		this.kernel = app.getKernel();				
		this.statDialog = statDialog;

		statTable = new StatTable(app);

		this.setLayout(new BorderLayout());
		this.add(statTable, BorderLayout.CENTER);

		statTable.setBorder(BorderFactory.createMatteBorder(1,0,0,0, SystemColor.controlShadow));
		setBorder(BorderFactory.createEmptyBorder());

	}

	public void setTable(double[] classes, double[] freq, StatPanelSettings settings){

		statTable.setStatTable( freq.length-1, null, 2, getColumnNames(settings));

		DefaultTableModel model = statTable.getModel();

		for(int row = 0; row < freq.length -1; row++){
			String interval = statDialog.format(classes[row]) + " - " + statDialog.format(classes[row+1]);
			model.setValueAt(interval, row, 0);
			model.setValueAt(statDialog.format(freq[row]), row, 1);
		}

		Dimension d = statTable.getPreferredSize();
		this.setPreferredSize(d);
		d.height = 8*statTable.getTable().getRowHeight();
		this.setMaximumSize(d);
		statTable.revalidate();
		updateFonts(app.getPlainFont());

	}

	
	private String[] getColumnNames(StatPanelSettings settings){
		String[] names = new String[2];
		names[0] = app.getMenu("Interval");
		names[1] = app.getCommand("Frequency"); 
		return names;
	}


	public void updatePanel() {}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels(){
		//statTable.setLabels(null, getColumnNames());
	}


}
