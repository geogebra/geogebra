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
	@SuppressWarnings("rawtypes")
	private JComboBox exportMode;
	private JPanel pxModePanel, cmModePanel;
	private boolean pxMode = false;
	
	/**
	 * @param app application
	 * @param ev selected view
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		Runnable updateWidth = new Runnable(){
			public void run(){
				fireWidthTextFieldUpdate();
			}
		};
		
		Runnable updateHeight = new Runnable(){
			public void run(){
				fireHeightTextFieldUpdate();
			}
		};
		
		tfScale1 = getNumberField(app, updateCm);
		tfScale2 = getNumberField(app, updateCm);
		tfSize1 = getNumberField(app, updateWidth);
		tfSize2 = getNumberField(app, updateHeight);
		
		exportMode = new JComboBox();
		exportMode.addItem(loc.getPlain("ScaleInCentimeter") + ":");
		exportMode.addItem(loc.getPlain("SizeInPixels") + ":");
		add(exportMode);
		
		exportMode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				switchMode();
				
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
	
	/**
	 * Switch to the correct mode (pixel vs cm)
	 */
	void switchMode(){
		pxMode = exportMode.getSelectedIndex() > 0;
		if(pxMode){
			PrintScalePanel.this.remove(cmModePanel);
			PrintScalePanel.this.add(pxModePanel);
			updateSizeTextFields(ev.getExportWidth(),ev.getExportHeight());
			
		}else{
			PrintScalePanel.this.remove(pxModePanel);
			PrintScalePanel.this.add(cmModePanel);
			updateScaleTextFields();
		}
		SwingUtilities.updateComponentTreeUI(PrintScalePanel.this);
		notifyListeners();
	}
	
	
	
	private static JTextField getNumberField(AppD app, final Runnable run) {
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

	/**
	 * Update pixel fields to default values
	 * @param width width
	 * @param height height
	 */
	void updateSizeTextFields(int width, int height) {
		setTextNoListener(tfSize1, nf.format(width));
		setTextNoListener(tfSize2, nf.format(height));
	}

	private void updateScaleTextFields() {
		
		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			setTextNoListener(tfScale1,"1");
			setTextNoListener(tfScale2,nf.format(1/scale));
		} else {			
			setTextNoListener(tfScale1,nf.format(scale));
			setTextNoListener(tfScale2,"1");
		}
	}

	private static void setTextNoListener(JTextField field, String s) {
		ActionListener ret = field.getActionListeners()[0];
		field.removeActionListener(ret);
		field.setText(s);
		field.addActionListener(ret);
		
	}



	/**
	 * Validate the texts in scale input, if OK,
	 * change export scale of EV and notify listeners
	 */
	void fireTextFieldUpdate() {
		boolean viewChanged = false;
		
		try {
			double numerator = Double.parseDouble(tfScale2.getText());
			double denominator = Double.parseDouble(tfScale1.getText());
			double scale = numerator / denominator;
			if (!(Double.isInfinite(scale) || Double.isNaN(scale))) {
				ev.setPrintingScale(scale);
				viewChanged = true;
			}			
		} catch (Exception e) {
			//invalid numbers, continue editing
		}
		
		updateScaleTextFields();

		if (viewChanged) {
			notifyListeners();
		}
	}	

	void fireWidthTextFieldUpdate() {
		try {
			int width = Integer.parseInt(tfSize1.getText());
			int height = (width * ev.getExportHeight())/ev.getExportWidth();
			updateSizeTextFields(width, height);
			notifyListeners();			
		} catch (Exception e) {
			//invalid numbers, continue editing
		}
		
	}
	
	void fireHeightTextFieldUpdate() {
		try {
			int height = Integer.parseInt(tfSize2.getText());
			int width = (height * ev.getExportWidth())/ev.getExportHeight();
			updateSizeTextFields(width, height);
			notifyListeners();			
		} catch (Exception e) {
			//invalid numbers, continue editing
		}
	}

	
	/**
	 * @param lst listens to changes of scale / size
	 */
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

	/**
	 * @return width in pixels
	 */
	public int getPixelWidth() {
		return Integer.parseInt(tfSize1.getText());
	}
	
	/**
	 * Height in pixels
	 */
	public int getPixelHeight() {
		return Integer.parseInt(tfSize2.getText());
	}
	/**
	 * @return whether we export using pixels rather than cm
	 */
	public boolean isPxMode() {
		return this.pxMode;
	}

}
