package org.geogebra.desktop.export;

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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Panel for print scale of EuclidianView. Notifies attached ActionListeners
 * about scale changes.
 * 
 * @author Markus Hohenwarter
 */

public class PrintScalePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int maxFracDigits = 5;

	private final JTextField tfScale1;
	private final JTextField tfScale2;
	private final JTextField tfSize1;
	private final JTextField tfSize2;
	private final JTextField tfScaleFixed;

	private final Vector<ActionListener> listeners = new Vector<>();
	private final EuclidianView ev;
	private final NumberFormat nf;

	@SuppressWarnings("rawtypes")
	private final JComboBox exportMode;
	private final JPanel pxModePanel;
	private final JPanel cmModePanel;
	private final JPanel fixedSizeModePanel;

	public enum PrintScaleModes {
		SIZEINCM, SIZEINPX, FIXED_SIZE
	}

	private PrintScaleModes mode = PrintScaleModes.SIZEINCM;

	private boolean pixelSizeEnabled = true;

	private String jcbItemSizeInPixels = "";
	private String jcbItemFixedSize = "";
	private String jcbItemScaleInCentimeter = "";

	/**
	 * @param app
	 *            application
	 * @param ev
	 *            selected view
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PrintScalePanel(AppD app, EuclidianView ev) {
		this.ev = ev;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(maxFracDigits);
		nf.setGroupingUsed(false);

		setLayout(new FlowLayout(FlowLayout.LEFT));

		Runnable updateCm = this::fireTextFieldUpdate;

		Runnable updateFixedSize = this::fireFixedSizeTextFieldUpdate;

		Runnable updateWidth = this::fireWidthTextFieldUpdate;

		Runnable updateHeight = this::fireHeightTextFieldUpdate;

		tfScale1 = getNumberField(app, updateCm);
		tfScale2 = getNumberField(app, updateCm);
		tfSize1 = getNumberField(app, updateWidth);
		tfSize2 = getNumberField(app, updateHeight);
		tfScaleFixed = getNumberField(app, updateFixedSize);

		// this label is not used (replaced with combo box exportMode)
		// scaleLabel = new JLabel(loc.getPlain("ScaleInCentimeter") + ":");

		// new variables added (3 rows) - are used as items in the combo box
		// exportMode
		Localization loc = app.getLocalization();
		jcbItemScaleInCentimeter = loc.getMenu("ScaleInCentimeter") + ":";
		jcbItemFixedSize = loc.getMenu("FixedSize") + ":";
		jcbItemSizeInPixels = loc.getMenu("SizeInPixels") + ":";

		exportMode = new JComboBox();

		exportMode.addItem(jcbItemScaleInCentimeter);
		exportMode.addItem(jcbItemFixedSize);
		exportMode.addItem(jcbItemSizeInPixels);
		// end of block update

		add(exportMode);

		exportMode.addActionListener(arg0 -> switchMode());

		fixedSizeModePanel = new JPanel();
		fixedSizeModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fixedSizeModePanel.add(new JLabel(
				" " + loc.getPlain("APixelsOnScreen", "100") + " = "));
		fixedSizeModePanel.add(tfScaleFixed);
		fixedSizeModePanel.add(new JLabel(" cm"));

		cmModePanel = new JPanel();
		cmModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		cmModePanel.add(tfScale1);
		cmModePanel.add(new JLabel(" " + loc.getMenu("units") + " = "));
		cmModePanel.add(tfScale2);
		cmModePanel.add(new JLabel(" cm"));

		pxModePanel = new JPanel();
		pxModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		pxModePanel.add(tfSize1);
		pxModePanel.add(new JLabel(
				loc.getMenu("Pixels.short") + " " + Unicode.MULTIPLY + " "));
		pxModePanel.add(tfSize2);
		pxModePanel.add(new JLabel(loc.getMenu("Pixels.short")));

		add(cmModePanel);

		updateScaleTextFields();
	}

	/**
	 * @param b to allow pixel size
	 */
	@SuppressWarnings("unchecked")
	public void enableAbsoluteSize(boolean b) {
		if (b == pixelSizeEnabled) {
			return;
		}
		pixelSizeEnabled = b;
		exportMode.removeItem(jcbItemSizeInPixels);
		if (b) {
			exportMode.addItem(jcbItemSizeInPixels);
		}
	}

	/**
	 * Switch to the correct mode (pixel vs cm vs fixed size)
	 */
	void switchMode() {

		if (exportMode.getSelectedItem().toString()
				.equals(jcbItemSizeInPixels)) {
			mode = PrintScaleModes.SIZEINPX;
		} else if (exportMode.getSelectedItem().toString()
				.equals(jcbItemFixedSize)) {
			mode = PrintScaleModes.FIXED_SIZE;
		} else {
			mode = PrintScaleModes.SIZEINCM;
		}

		switch (mode) {
		case SIZEINCM:
			this.remove(pxModePanel);
			this.remove(fixedSizeModePanel);
			this.add(cmModePanel);
			updateScaleTextFields();
			break;
		case SIZEINPX:
			this.remove(cmModePanel);
			this.remove(fixedSizeModePanel);
			this.add(pxModePanel);
			updateSizeTextFields(ev.getExportWidth(), ev.getExportHeight());
			break;
		case FIXED_SIZE:
			this.remove(cmModePanel);
			this.remove(pxModePanel);
			this.add(fixedSizeModePanel);
			updateFixedSizeTextFields();
			revalidate();
			repaint();
			break;
		}

		SwingUtilities.updateComponentTreeUI(this);
		notifyListeners();
	}

	private static JTextField getNumberField(AppD app, final Runnable run) {
		JTextField ret = new MyTextFieldD(app);
		ret.setColumns(maxFracDigits);
		ret.setHorizontalAlignment(SwingConstants.RIGHT);
		FocusListener flst = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				run.run();
			}

			@Override
			public void focusGained(FocusEvent e) {
				//
			}
		};
		ActionListener al = e -> run.run();
		ret.addActionListener(al);
		ret.addFocusListener(flst);
		return ret;
	}

	/**
	 * Update pixel fields to default values
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */

	private void updateSizeTextFields(int width, int height) {
		setTextNoListener(tfSize1, nf.format(width));
		setTextNoListener(tfSize2, nf.format(height));
	}

	private void updateFixedSizeTextFields() {
		double relScale = 100 * ev.getPrintingScale() / ev.getXscale();
		setTextNoListener(tfScaleFixed, nf.format(relScale));
	}

	private void updateScaleTextFields() {

		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			setTextNoListener(tfScale2, "1");
			setTextNoListener(tfScale1, nf.format(1 / scale));
		} else {
			setTextNoListener(tfScale2, nf.format(scale));
			setTextNoListener(tfScale1, "1");
		}
	}

	private static void setTextNoListener(JTextField field, String s) {
		ActionListener ret = field.getActionListeners()[0];
		field.removeActionListener(ret);
		field.setText(s);
		field.addActionListener(ret);

	}

	/**
	 * Validate the texts in scale input, if OK, change export scale of EV and
	 * notify listeners
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
			// invalid numbers, continue editing
		}

		updateScaleTextFields();

		if (viewChanged) {
			notifyListeners();
		}
	}

	void fireWidthTextFieldUpdate() {
		try {
			int width = Integer.parseInt(tfSize1.getText());
			int height = (width * ev.getExportHeight()) / ev.getExportWidth();
			updateSizeTextFields(width, height);
			notifyListeners();
		} catch (Exception e) {
			Log.debug(tfSize1.getText() + " is not a valid number");
		}

	}

	void fireHeightTextFieldUpdate() {
		try {
			int height = Integer.parseInt(tfSize2.getText());
			int width = (height * ev.getExportWidth()) / ev.getExportHeight();
			updateSizeTextFields(width, height);
			notifyListeners();
		} catch (Exception e) {
			Log.debug(tfSize2.getText() + " is not a valid number");
		}
	}

	void fireFixedSizeTextFieldUpdate() {
		boolean viewChanged = false;

		try {
			double userScale = Double.parseDouble(tfScaleFixed.getText());
			if (!(Double.isInfinite(userScale) || Double.isNaN(userScale))) {
				double scale = userScale * ev.getXscale() / 100;
				ev.setPrintingScale(scale);
				viewChanged = true;
			}
		} catch (Exception e) {
			Log.debug(e);
		}

		updateFixedSizeTextFields();

		if (viewChanged) {
			notifyListeners();
		}
	}

	/**
	 * @param lst
	 *            listens to changes of scale / size
	 */
	public void addActionListener(ActionListener lst) {
		listeners.add(lst);
	}

	private void notifyListeners() {
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			listeners.get(i).actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "ViewChanged"));
		}
	}

	/**
	 * @return width in pixels
	 */
	public int getPixelWidth() {
		try {
			return Integer.parseInt(tfSize1.getText());
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @return Height in pixels
	 */
	public int getPixelHeight() {
		try {
			return Integer.parseInt(tfSize2.getText());
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @return whether we export using pixels rather than cm
	 */
	public PrintScaleModes getMode() {
		return this.mode;
	}

}
