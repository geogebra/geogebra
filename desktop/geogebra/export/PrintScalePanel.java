package geogebra.export;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.Localization;
import geogebra.common.util.Unicode;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Panel for print scale of EuclidianView. Notifies attached ActionListeners about scale changes.
 * 
 * @author Markus Hohenwarter
 */
public class PrintScalePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static final int maxFracDigits = 5; 
	
	private JTextField tfScale1, tfScale2, tfSize1, tfSize2;
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	private EuclidianView ev;	
	private NumberFormat nf;
	private JComboBox exportMode;
	private JPanel pxModePanel, cmModePanel;
	private boolean pxMode = false;
	
	public PrintScalePanel(AppD app, EuclidianView ev) {		
		this.ev = ev;		
		Localization loc = app.getLocalization();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(maxFracDigits);
		nf.setGroupingUsed(false);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));			
		
		Runnable updateCm = new Runnable(){
			public void run(){
				fireTextFieldUpdate();
			}
		};
		
		Runnable updatePx = new Runnable(){
			public void run(){
				fireTextFieldUpdate();
			}
		};
		
		tfScale1 = getNumberField(app, updateCm);
		tfScale2 = getNumberField(app, updateCm);
		tfSize1 = getNumberField(app, updatePx);
		tfSize2 = getNumberField(app, updatePx);
		
		exportMode = new JComboBox();
		exportMode.addItem(loc.getPlain("ScaleInCentimeter") + ":");
		exportMode.addItem(loc.getPlain("SizeInPixels") + ":");
		add(exportMode);
		
		exportMode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(pxMode){
					PrintScalePanel.this.remove(pxModePanel);
					PrintScalePanel.this.add(cmModePanel);
					updateScaleTextFields();
				}else{
					PrintScalePanel.this.remove(cmModePanel);
					PrintScalePanel.this.add(pxModePanel);
					updateSizeTextFields();
				}
				SwingUtilities.updateComponentTreeUI(PrintScalePanel.this);
				notifyListeners();
				pxMode = !pxMode;
				
			}});
		
		cmModePanel=new JPanel();
		cmModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));	
		cmModePanel.add(tfScale1);
		cmModePanel.add(new JLabel(" unit = "));
		cmModePanel.add(tfScale2);
		cmModePanel.add(new JLabel(" cm"));
		
		
		pxModePanel=new JPanel();
		pxModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));	
		pxModePanel.add(tfSize1);
		pxModePanel.add(new JLabel(loc.getMenu("Pixels.short")+" "+Unicode.multiply+ " "));
		pxModePanel.add(tfSize2);
		pxModePanel.add(new JLabel(loc.getMenu("Pixels.short")));

		add(cmModePanel);
		
		updateScaleTextFields();
	}
	
	
	
	private JTextField getNumberField(AppD app, final Runnable run) {
		JTextField ret = new MyTextField(app);
		ret.setColumns(maxFracDigits);
		ret.setHorizontalAlignment(SwingConstants.RIGHT);
		FocusListener flst = new FocusListener() {
			public void focusLost(FocusEvent e) {
				run.run();
			}
			public void focusGained(FocusEvent e) {
				//
			}
		};
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run.run();
			}
		};
		ret.addActionListener(al);
		ret.addFocusListener(flst);
		return ret;
	}

	void updateSizeTextFields() {
		ActionListener a1 = removeListener(tfSize1);
		ActionListener a2 = removeListener(tfSize2);
		
		tfSize1.setText(nf.format(ev.getWidth()));
		tfSize2.setText(nf.format(ev.getHeight()));
		
		
		tfSize1.addActionListener(a1);
		tfSize2.addActionListener(a2);
	}

	private void updateScaleTextFields() {
		ActionListener a1 = removeListener(tfScale1);
		ActionListener a2 = removeListener(tfScale2);
		
		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			tfScale1.setText("1");
			tfScale2.setText(nf.format(1/scale));
		} else {			
			tfScale1.setText(nf.format(scale));
			tfScale2.setText("1");
		}
		
		tfScale1.addActionListener(a1);
		tfScale2.addActionListener(a2);
	}

	private ActionListener removeListener(JTextField field) {
		ActionListener ret = field.getActionListeners()[0];
		field.removeActionListener(ret);
		return ret;
	}



	void fireTextFieldUpdate() {
		boolean viewChanged = false;
		
		try {
			double numerator = Double.parseDouble(tfScale1.getText());
			double denominator = Double.parseDouble(tfScale2.getText());
			double scale = numerator / denominator;
			if (!(Double.isInfinite(scale) || Double.isNaN(scale))) {
				ev.setPrintingScale(scale);
				viewChanged = true;
			}			
		} catch (Exception e) {}
		
		updateScaleTextFields();

		if (viewChanged) {
			notifyListeners();
		}
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
