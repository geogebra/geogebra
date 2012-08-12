package geogebra.export;

import geogebra.main.AppD;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PrintScalePanel2 extends JPanel {

	private NumberFormat nf;
	private static final int maxFracDigits = 5;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	PrintScalePanel2(AppD app, ScalingPrintGridable ev) {
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(maxFracDigits);
		nf.setGroupingUsed(false);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		add(new JLabel(app.getPlain("dummy text") + ":"));
		
		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		add(slider);
	}
	
	public void addActionListener(ActionListener lst) {
		listeners.add(lst);
	}
	
	private void notifyListeners() {
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			listeners.get(i).actionPerformed(
				new ActionEvent(
					this,
					ActionEvent.ACTION_PERFORMED,
					"ViewChanged"));
		}
	}
}
