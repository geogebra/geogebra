package geogebra.gui.properties;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.gui.AngleTextField;
import geogebra.gui.dialog.PropertiesDialog;
import geogebra.gui.dialog.PropertiesPanel;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * panel for numeric slider
 * 
 * @author Markus Hohenwarter
 */
public class SliderPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] geos; // currently selected geos
	private AngleTextField tfMin, tfMax;
	private JTextField tfWidth;
	private JTextField[] tfields;
	private JLabel[] tLabels;
	private JCheckBox cbSliderFixed, cbRandom;
	private JComboBox coSliderHorizontal;

	private AppD app;
	private AnimationStepPanel stepPanel;
	private AnimationSpeedPanel speedPanel;
	private Kernel kernel;
	private PropertiesPanel propPanel;
	private JPanel intervalPanel, sliderPanel, animationPanel;
	private boolean useTabbedPane, includeRandom;
	private boolean actionPerforming;

	public SliderPanel(AppD app, PropertiesPanel propPanel,
			boolean useTabbedPane, boolean includeRandom) {
		this.app = app;
		kernel = app.getKernel();
		this.propPanel = propPanel;
		this.useTabbedPane = useTabbedPane;
		this.includeRandom = includeRandom;

		intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		animationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		cbSliderFixed = new JCheckBox("", true);
		cbSliderFixed.addActionListener(this);
		sliderPanel.add(cbSliderFixed);

		cbRandom = new JCheckBox();
		cbRandom.addActionListener(this);
		sliderPanel.add(cbRandom);

		coSliderHorizontal = new JComboBox();
		coSliderHorizontal.addActionListener(this);
		sliderPanel.add(coSliderHorizontal);

		tfMin = new AngleTextField(6, app);
		tfMax = new AngleTextField(6, app);
		tfWidth = new MyTextField(app, 4);
		tfields = new MyTextField[3];
		tLabels = new JLabel[3];
		tfields[0] = tfMin;
		tfields[1] = tfMax;
		tfields[2] = tfWidth;
		int numPairs = tLabels.length;

		// add textfields
		for (int i = 0; i < numPairs; i++) {
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tLabels[i] = new JLabel("", SwingConstants.LEADING);
			p.add(tLabels[i]);
			JTextField textField = tfields[i];
			tLabels[i].setLabelFor(textField);
			textField.addActionListener(this);
			textField.addFocusListener(this);
			p.add(textField);
			p.setAlignmentX(Component.LEFT_ALIGNMENT);

			if (i < 2) {
				intervalPanel.add(p);
			} else {
				sliderPanel.add(p);
			}
		}

		// add increment to intervalPanel
		stepPanel = new AnimationStepPanel(app);
		stepPanel.setPartOfSliderPanel();
		intervalPanel.add(stepPanel);

		speedPanel = new AnimationSpeedPanel(app);
		speedPanel.setPartOfSliderPanel();
		animationPanel.add(speedPanel);

		setLabels();
	}

	private void initPanels() {
		removeAll();

		// put together interval, slider options, animation panels
		if (useTabbedPane) {
			setLayout(new FlowLayout());
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			tabbedPane.addTab(app.getPlain("Interval"), intervalPanel);
			tabbedPane.addTab(app.getMenu("Slider"), sliderPanel);
			tabbedPane.addTab(app.getPlain("Animation"), animationPanel);
			add(tabbedPane);
		} else { // no tabs
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			intervalPanel.setBorder(BorderFactory.createTitledBorder(app
					.getPlain("Interval")));
			sliderPanel.setBorder(BorderFactory.createTitledBorder(app
					.getPlain("Slider")));
			animationPanel.setBorder(BorderFactory.createTitledBorder(app
					.getPlain("Animation")));
			add(intervalPanel);
			add(Box.createVerticalStrut(5));
			add(sliderPanel);
			add(Box.createVerticalStrut(5));
			add(animationPanel);
		}
	}

	public void setLabels() {
		initPanels();

		cbSliderFixed.setText(app.getPlain("fixed"));
		cbRandom.setText(app.getPlain("Random"));

		String[] comboStr = { app.getPlain("horizontal"),
				app.getPlain("vertical") };

		int selectedIndex = coSliderHorizontal.getSelectedIndex();
		coSliderHorizontal.removeActionListener(this);
		coSliderHorizontal.removeAllItems();

		for (int i = 0; i < comboStr.length; ++i) {
			coSliderHorizontal.addItem(comboStr[i]);
		}

		coSliderHorizontal.setSelectedIndex(selectedIndex);
		coSliderHorizontal.addActionListener(this);

		String[] labels = { app.getPlain("min") + ":",
				app.getPlain("max") + ":", app.getPlain("Width") + ":" };

		for (int i = 0; i < tLabels.length; ++i) {
			tLabels[i].setText(labels[i]);
		}
	}

	public JPanel update(Object[] geos) {
		stepPanel.update(geos);
		speedPanel.update(geos);

		this.geos = geos;
		if (!checkGeos(geos)) {
			return null;
		}

		for (int i = 0; i < tfields.length; i++)
			tfields[i].removeActionListener(this);
		coSliderHorizontal.removeActionListener(this);
		cbSliderFixed.removeActionListener(this);
		cbRandom.removeActionListener(this);

		// check if properties have same values
		GeoNumeric temp, num0 = (GeoNumeric) geos[0];
		boolean equalMax = true;
		boolean equalMin = true;
		boolean equalWidth = true;
		boolean equalSliderFixed = true;
		boolean random = true;
		boolean equalSliderHorizontal = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoNumeric) geos[i];

			// we don't check isIntervalMinActive, because we want to display
			// the interval even if it's empty
			if (num0.getIntervalMinObject() == null
					|| temp.getIntervalMinObject() == null
					|| !Kernel.isEqual(num0.getIntervalMin(),
							temp.getIntervalMin()))
				equalMin = false;
			if (num0.getIntervalMaxObject() == null
					|| temp.getIntervalMaxObject() == null
					|| !Kernel.isEqual(num0.getIntervalMax(),
							temp.getIntervalMax()))
				equalMax = false;
			if (!Kernel.isEqual(num0.getSliderWidth(), temp.getSliderWidth()))
				equalWidth = false;
			if (num0.isSliderFixed() != temp.isSliderFixed())
				equalSliderFixed = false;
			if (num0.isRandom() != temp.isRandom())
				random = false;
			if (num0.isSliderHorizontal() != temp.isSliderHorizontal())
				equalSliderHorizontal = false;

			if (!(temp instanceof GeoAngle))
				onlyAngles = false;
		}

		StringTemplate highPrecision = StringTemplate.printDecimals(
				StringType.GEOGEBRA,
				PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS, false);
		if (equalMin) {
			GeoElement min0 = num0.getIntervalMinObject();
			if (onlyAngles
					&& (min0 == null || (!min0.isLabelSet() && min0
							.isIndependent()))) {
				tfMin.setText(kernel.formatAngle(num0.getIntervalMin(),
						highPrecision).toString());
			} else
				tfMin.setText(min0.getLabel(highPrecision));
		} else {
			tfMin.setText("");
		}

		if (equalMax) {
			GeoElement max0 = num0.getIntervalMaxObject();
			if (onlyAngles
					&& (max0 == null || (!max0.isLabelSet() && max0
							.isIndependent())))
				tfMax.setText(kernel.formatAngle(num0.getIntervalMax(),
						highPrecision).toString());
			else
				tfMax.setText(max0.getLabel(highPrecision));
		} else {
			tfMax.setText("");
		}

		if (equalWidth) {
			tfWidth.setText(kernel.format(num0.getSliderWidth(), highPrecision));
		} else {
			tfMax.setText("");
		}

		if (equalSliderFixed)
			cbSliderFixed.setSelected(num0.isSliderFixed());

		if (random)
			cbRandom.setSelected(num0.isRandom());

		cbRandom.setVisible(includeRandom);

		if (equalSliderHorizontal) {
			// TODO why doesn't this work when you create a slider
			coSliderHorizontal.setSelectedIndex(num0.isSliderHorizontal() ? 0
					: 1);
		}

		for (int i = 0; i < tfields.length; i++)
			tfields[i].addActionListener(this);
		coSliderHorizontal.addActionListener(this);
		cbSliderFixed.addActionListener(this);
		cbRandom.addActionListener(this);

		return this;
	}

	private static boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!(geo.isIndependent() && geo.isGeoNumeric())) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cbSliderFixed)
			doCheckBoxActionPerformed((JCheckBox) source);
		else if (source == cbRandom)
			doRandomActionPerformed((JCheckBox) source);
		else if (source == coSliderHorizontal)
			doComboBoxActionPerformed((JComboBox) source);
		else
			doTextFieldActionPerformed((JTextField) e.getSource());
	}

	private void doCheckBoxActionPerformed(JCheckBox source) {
		boolean fixed = source.isSelected();
		for (int i = 0; i < geos.length; i++) {
			GeoNumeric num = (GeoNumeric) geos[i];
			num.setSliderFixed(fixed);
			num.updateRepaint();
		}
		update(geos);
	}

	private void doRandomActionPerformed(JCheckBox source) {
		boolean random = source.isSelected();
		for (int i = 0; i < geos.length; i++) {
			GeoNumeric num = (GeoNumeric) geos[i];
			num.setRandom(random);
			num.updateRepaint();
		}
		update(geos);
	}

	private void doComboBoxActionPerformed(JComboBox source) {
		boolean horizontal = source.getSelectedIndex() == 0;
		for (int i = 0; i < geos.length; i++) {
			GeoNumeric num = (GeoNumeric) geos[i];
			num.setSliderHorizontal(horizontal);
			num.updateRepaint();
		}
		update(geos);
	}

	private void doTextFieldActionPerformed(JTextField source) {
		actionPerforming = true;
		String inputText = source.getText().trim();
		boolean emptyString = inputText.equals("");
		NumberValue value = new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
		}

		if (source == tfMin || source == tfMax) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				boolean dependsOnListener = false;
				GeoElement geoValue = value.toGeoElement();
				if (num.getMinMaxListeners() != null)
					for (GeoNumeric listener : num.getMinMaxListeners()) {
						if (geoValue.isChildOrEqual(listener)) {
							dependsOnListener = true;
						}
					}
				if (dependsOnListener || geoValue.isChildOrEqual(num)) {
					app.showErrorDialog(app.getError("CircularDefinition"));
				} else {
					if (source == tfMin) {
						num.setIntervalMin(value);
					} else {
						num.setIntervalMax(value);
					}
				}
				num.updateRepaint();

			}
		} else if (source == tfWidth) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				num.setSliderWidth(value.getDouble());
				num.updateRepaint();
			}
		}

		if (propPanel != null) {
			propPanel.updateSelection(geos);
		} else {
			update(geos);
		}
		actionPerforming = false;
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		if (!actionPerforming) {
			doTextFieldActionPerformed((JTextField) e.getSource());
		}
	}

	public void updateFonts() {
		Font font = app.getPlainFont();
		
		cbSliderFixed.setFont(font);
		cbRandom.setFont(font);
		coSliderHorizontal.setFont(font);


		for (int i = 0; i < tLabels.length; ++i) {
			tLabels[i].setFont(font);
		}
		
		tfMin.setFont(font);
		tfMax.setFont(font);
		tfWidth.setFont(font);
		
		for (int i = 0; i < tfields.length; ++i) 
			tfields[i].setFont(font);
		
		
	}
}