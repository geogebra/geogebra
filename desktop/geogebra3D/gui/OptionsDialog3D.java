package geogebra3D.gui;

import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.gui.util.IconTabbedPane;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;
import geogebra3D.Application3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class OptionsDialog3D extends OptionsDialog {
	private static final long serialVersionUID = 1L;
	private OptionsEuclidian3D euclidianPanel3D;

	public OptionsDialog3D(AppD app) {
		super(app);
	}
	
	@Override
	protected void createTabs(){
		//super.createTabs();
		euclidianPanel3D = new OptionsEuclidian3D((Application3D) app);
		
	}
	
	@Override
	protected void initGUI() {
		setLayout(new BorderLayout());

		// init tabs
		createTabs();





		addTabs();



		add(euclidianPanel3D, BorderLayout.CENTER);



		setLabels(); // update all labels

		setPreferredSize(new Dimension(640, 480));
		pack();

		setLocationRelativeTo(null);
	}
	
	@Override
	protected void addTabs(){


	}
	
	@Override
	public void setLabels() {
		//super.setLabels();
		
		setTitle(app.getPlain("GraphicsView3D"));


				
		//tabbedPane.setTitleAt(TAB_EUCLIDIAN3D, app.getPlain("GraphicsView3D"));
		
		euclidianPanel3D.setLabels();
	}
	
	public static class Factory extends OptionsDialog.Factory {
		public OptionsDialog create(AppD app) {
			return new OptionsDialog3D(app);
		}
	}
	
	public void updateGUI() {
		euclidianPanel3D.updateGUI();
	}
	
	public void showTab(int index) {
		
	}
	
	public void windowClosing(WindowEvent e) {
		setVisible(false);
	}
}
