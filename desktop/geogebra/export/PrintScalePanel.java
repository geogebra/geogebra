package geogebra.export;

import geogebra.common.euclidian.EuclidianView;
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panel for print scale of EuclidianView. Notifies attached ActionListeners about scale changes.
 * 
 * @author Markus Hohenwarter
 */
public class PrintScalePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static final int maxFracDigits = 5; 
	
	private JTextField tfScale1, tfScale2;
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	private EuclidianView ev;	
	private NumberFormat nf;
	private ActionListener al; 

	public PrintScalePanel(AppD app, EuclidianView ev) {		
		this.ev = ev;		
		
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(maxFracDigits);
		nf.setGroupingUsed(false);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));			
		
		tfScale1 = new MyTextField(app);
		tfScale2 = new MyTextField(app);
		tfScale1.setColumns(maxFracDigits);	
		tfScale2.setColumns(maxFracDigits);
		tfScale1.setHorizontalAlignment(SwingConstants.RIGHT);
		tfScale2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		add(new JLabel(app.getPlain("ScaleInCentimeter") + ":"));
		add(tfScale1);
		add(new JLabel(" : "));
		add(tfScale2);				

		al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireTextFieldUpdate();
			}
		};
		tfScale1.addActionListener(al);
		tfScale2.addActionListener(al);
		
		FocusListener flst = new FocusListener() {
			public void focusLost(FocusEvent e) {
				fireTextFieldUpdate();
			}
			public void focusGained(FocusEvent e) {
				//
			}
		};
		tfScale1.addFocusListener(flst);
		tfScale2.addFocusListener(flst);
		
		updateTextFields();
	}
	
	
	
	private void updateTextFields() {
		tfScale1.removeActionListener(al);
		tfScale2.removeActionListener(al);
		
		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			tfScale1.setText("1");
			tfScale2.setText(nf.format(1/scale));
		} else {			
			tfScale1.setText(nf.format(scale));
			tfScale2.setText("1");
		}
		
		tfScale1.addActionListener(al);
		tfScale2.addActionListener(al);
	}

	private void fireTextFieldUpdate() {
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
		
		updateTextFields();

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
