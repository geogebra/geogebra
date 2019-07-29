package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathRegionHandling;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.KeyboardSettings;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Advanced options for the options dialog.
 */
@SuppressWarnings("javadoc")
public class OptionsAdvancedD implements OptionPanelD,
		ActionListener, ChangeListener, FocusListener, SetLabels {

	/**
	 * Application object.
	 */
	private AppD app;
	private final LocalizationD loc;

	/**
	 * Settings for all kind of application components.
	 */
	private Settings settings;

	/** */
	private JPanel virtualKeyboardPanel, guiFontsizePanel, tooltipPanel,
			languagePanel, angleUnitPanel, continuityPanel,
			usePathAndRegionParametersPanel, rightAnglePanel, coordinatesPanel;

	/**	*/
	private JLabel keyboardLanguageLabel, guiFontSizeLabel, widthLabel,
			heightLabel, opacityLabel, tooltipLanguageLabel,
			tooltipTimeoutLabel;

	/** */
	private JComboBox<String> cbKeyboardLanguage, cbTooltipLanguage,
			cbTooltipTimeout,
			cbGUIFont;

	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits,
			cbUseLocalLabels;

	/** */
	private JRadioButton angleUnitRadioDegree, angleUnitRadioRadian,
			angleUnitRadioDegreesMinutesSeconds,
			continuityRadioOn, continuityRadioOff,
			usePathAndRegionParametersRadioOn,
			usePathAndRegionParametersRadioOff, rightAngleRadio1,
			rightAngleRadio2, rightAngleRadio3, rightAngleRadio4,
			coordinatesRadio1, coordinatesRadio2, coordinatesRadio3;

	/** */
	private ButtonGroup angleUnitButtonGroup, continuityButtonGroup,
			usePathAndRegionParametersButtonGroup, rightAngleButtonGroup,
			coordinatesButtonGroup;

	/** */
	private JTextField tfKeyboardWidth, tfKeyboardHeight;

	/** */
	private JSlider slOpacity;

	/**
	 * Timeout values of tooltips (last entry reserved for "Off", but that has
	 * to be translated) This is just an example, it will be overwritten by
	 * tooltipTimeouts in MyXMLHandler, plus "-" instead of "0"
	 */
	private String[] tooltipTimeouts = new String[] { "1", "3", "5", "10", "20",
			"30", "60", "-" };

	private JPanel wrappedPanel;

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAdvancedD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
		this.loc = app.getLocalization();
		this.settings = app.getSettings();

		initGUI();
		updateGUI();
	}

	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of
	 *         calling setLabels()
	 */
	private void initGUI() {
		initVirtualKeyboardPanel();
		initGUIFontSizePanel();
		initTooltipPanel();
		initLanguagePanel();
		// initPerspectivesPanel();
		initAngleUnitPanel();
		initContinuityPanel();
		initUsePathAndRegionParametersPanel();
		initRightAnglePanel();
		initCoordinatesPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());

		panel.add(angleUnitPanel);
		panel.add(rightAnglePanel);
		panel.add(coordinatesPanel);
		panel.add(continuityPanel);
		panel.add(usePathAndRegionParametersPanel);

		panel.add(virtualKeyboardPanel);
		panel.add(guiFontsizePanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		// panel.add(perspectivesPanel);

		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(4);
		// scrollPane.setBorder(BorderFactory.createEmptyBorder());

		wrappedPanel.add(scrollPane, BorderLayout.CENTER);

		setLabels();

		app.setComponentOrientation(panel);
	}

	/**
	 * Initialize the virtual keyboard panel
	 */
	private void initVirtualKeyboardPanel() {
		virtualKeyboardPanel = new JPanel();
		virtualKeyboardPanel.setLayout(
				new BoxLayout(virtualKeyboardPanel, BoxLayout.Y_AXIS));

		keyboardLanguageLabel = new JLabel();
		virtualKeyboardPanel.add(LayoutUtil.flowPanel(keyboardLanguageLabel));
		cbKeyboardLanguage = new JComboBox<>();
		// listener to this combo box is added in setLabels()
		virtualKeyboardPanel.add(LayoutUtil
				.flowPanel(Box.createHorizontalStrut(20), cbKeyboardLanguage));

		widthLabel = new JLabel();
		tfKeyboardWidth = new JTextField(3);
		tfKeyboardWidth.addFocusListener(this);
		heightLabel = new JLabel();
		tfKeyboardHeight = new JTextField(3);
		tfKeyboardHeight.addFocusListener(this);

		virtualKeyboardPanel.add(LayoutUtil.flowPanel(widthLabel,
				tfKeyboardWidth, new JLabel(loc.getMenu("Pixels.short")),
				Box.createHorizontalStrut(10), heightLabel, tfKeyboardHeight,
				new JLabel(loc.getMenu("Pixels.short"))));

		cbKeyboardShowAutomatic = new JCheckBox();

		opacityLabel = new JLabel();
		slOpacity = new JSlider(25, 100);
		slOpacity.setPreferredSize(new Dimension(100,
				(int) slOpacity.getPreferredSize().getHeight()));
		// listener added in updateGUI()
		opacityLabel.setLabelFor(slOpacity);
		virtualKeyboardPanel.add(LayoutUtil.flowPanel(cbKeyboardShowAutomatic,
				opacityLabel, slOpacity));

	}

	/**
	 * Initialize the GUI fontsize panel
	 */
	private void initGUIFontSizePanel() {
		guiFontsizePanel = new JPanel();
		guiFontsizePanel
				.setLayout(new BoxLayout(guiFontsizePanel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		guiFontSizeLabel = new JLabel();
		panel.add(guiFontSizeLabel);

		cbGUIFont = new JComboBox<>();
		// listener to this combo box is added in setLabels()
		panel.add(cbGUIFont);

		guiFontsizePanel.add(panel, BorderLayout.NORTH);

	}

	/**
	 * Initialize the language panel.
	 */
	private void initLanguagePanel() {
		languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		cbUseLocalDigits = new JCheckBox();
		cbUseLocalDigits.addActionListener(this);
		languagePanel.add(cbUseLocalDigits);

		cbUseLocalLabels = new JCheckBox();
		cbUseLocalLabels.addActionListener(this);
		languagePanel.add(cbUseLocalLabels);
	}

	/**
	 * Initialize the tooltip panel.
	 */
	private void initTooltipPanel() {
		tooltipPanel = new JPanel();
		tooltipPanel.setLayout(new BoxLayout(tooltipPanel, BoxLayout.Y_AXIS));

		tooltipLanguageLabel = new JLabel();
		tooltipPanel.add(LayoutUtil.flowPanel(tooltipLanguageLabel));
		cbTooltipLanguage = new JComboBox<>();
		cbTooltipLanguage.setRenderer(new LanguageRenderer(app));
		// listener to this combo box is added in setLabels()
		tooltipPanel.add(LayoutUtil.flowPanel(Box.createHorizontalStrut(20),
				cbTooltipLanguage));

		tooltipTimeoutLabel = new JLabel();

		// get tooltipTimeouts from MyXMLHandler
		tooltipTimeouts = new String[OptionsAdvancedD.tooltipTimeoutsLength()];
		for (int i = 0; i < OptionsAdvancedD.tooltipTimeoutsLength() - 1; i++) {
			tooltipTimeouts[i] = OptionsAdvancedD.tooltipTimeouts(i);
		}
		tooltipTimeouts[tooltipTimeouts.length - 1] = "-";

		cbTooltipTimeout = new JComboBox<>(tooltipTimeouts);

		tooltipPanel.add(
				LayoutUtil.flowPanel(tooltipTimeoutLabel, cbTooltipTimeout));
	}

	/**
	 * Initialize the perspectives panel.
	 * 
	 * private void initPerspectivesPanel() { perspectivesPanel = new JPanel(new
	 * FlowLayout(FlowLayout.LEFT));
	 * 
	 * cbIgnoreDocumentLayout = new JCheckBox();
	 * cbIgnoreDocumentLayout.addActionListener(this);
	 * perspectivesPanel.add(LayoutUtil.flowPanel(cbIgnoreDocumentLayout));
	 * 
	 * }
	 */

	/**
	 * Initialize the angle unit panel
	 */
	private void initAngleUnitPanel() {
		angleUnitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		angleUnitButtonGroup = new ButtonGroup();

		angleUnitRadioDegree = new JRadioButton();
		angleUnitRadioDegree.addActionListener(this);
		angleUnitPanel.add(angleUnitRadioDegree);
		angleUnitButtonGroup.add(angleUnitRadioDegree);

		angleUnitRadioRadian = new JRadioButton();
		angleUnitRadioRadian.addActionListener(this);
		angleUnitPanel.add(angleUnitRadioRadian);
		angleUnitButtonGroup.add(angleUnitRadioRadian);

		angleUnitRadioDegreesMinutesSeconds = new JRadioButton();
		angleUnitRadioDegreesMinutesSeconds.addActionListener(this);
		angleUnitPanel.add(angleUnitRadioDegreesMinutesSeconds);
		angleUnitButtonGroup.add(angleUnitRadioDegreesMinutesSeconds);

		// cbReturnAngleInverseTrig = new JCheckBox();
		// cbReturnAngleInverseTrig.addActionListener(this);
		// angleUnitPanel.add(cbReturnAngleInverseTrig);

	}

	/**
	 * Initialize the continuity panel
	 */
	private void initContinuityPanel() {
		continuityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		continuityButtonGroup = new ButtonGroup();

		continuityRadioOn = new JRadioButton();
		continuityRadioOn.addActionListener(this);
		continuityPanel.add(continuityRadioOn);
		continuityButtonGroup.add(continuityRadioOn);

		continuityRadioOff = new JRadioButton();
		continuityRadioOff.addActionListener(this);
		continuityPanel.add(continuityRadioOff);
		continuityButtonGroup.add(continuityRadioOff);
	}

	/**
	 * Initialize the use of path/region parameters panel
	 */
	private void initUsePathAndRegionParametersPanel() {
		usePathAndRegionParametersPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT));

		usePathAndRegionParametersButtonGroup = new ButtonGroup();

		usePathAndRegionParametersRadioOn = new JRadioButton();
		usePathAndRegionParametersRadioOn.addActionListener(this);
		usePathAndRegionParametersPanel.add(usePathAndRegionParametersRadioOn);
		usePathAndRegionParametersButtonGroup
				.add(usePathAndRegionParametersRadioOn);

		usePathAndRegionParametersRadioOff = new JRadioButton();
		usePathAndRegionParametersRadioOff.addActionListener(this);
		usePathAndRegionParametersPanel.add(usePathAndRegionParametersRadioOff);
		usePathAndRegionParametersButtonGroup
				.add(usePathAndRegionParametersRadioOff);

	}

	/**
	 * Initialize the right angle panel
	 */
	private void initRightAnglePanel() {
		rightAnglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		rightAngleButtonGroup = new ButtonGroup();

		rightAngleRadio1 = new JRadioButton();
		rightAngleRadio1.addActionListener(this);
		rightAnglePanel.add(rightAngleRadio1);
		rightAngleButtonGroup.add(rightAngleRadio1);

		rightAngleRadio2 = new JRadioButton();
		rightAngleRadio2.addActionListener(this);
		rightAnglePanel.add(rightAngleRadio2);
		rightAngleButtonGroup.add(rightAngleRadio2);

		rightAngleRadio3 = new JRadioButton();
		rightAngleRadio3.addActionListener(this);
		rightAnglePanel.add(rightAngleRadio3);
		rightAngleButtonGroup.add(rightAngleRadio3);

		rightAngleRadio4 = new JRadioButton();
		rightAngleRadio4.addActionListener(this);
		rightAnglePanel.add(rightAngleRadio4);
		rightAngleButtonGroup.add(rightAngleRadio4);
	}

	/**
	 * Initialize the coordinates panel
	 */
	private void initCoordinatesPanel() {
		coordinatesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		coordinatesButtonGroup = new ButtonGroup();

		coordinatesRadio1 = new JRadioButton();
		coordinatesRadio1.addActionListener(this);
		coordinatesPanel.add(coordinatesRadio1);
		coordinatesButtonGroup.add(coordinatesRadio1);

		coordinatesRadio2 = new JRadioButton();
		coordinatesRadio2.addActionListener(this);
		coordinatesPanel.add(coordinatesRadio2);
		coordinatesButtonGroup.add(coordinatesRadio2);

		coordinatesRadio3 = new JRadioButton();
		coordinatesRadio3.addActionListener(this);
		coordinatesPanel.add(coordinatesRadio3);
		coordinatesButtonGroup.add(coordinatesRadio3);
	}

	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	@Override
	public void updateGUI() {

		cbUseLocalDigits.setSelected(loc.isUsingLocalizedDigits());
		cbUseLocalLabels.setSelected(loc.isUsingLocalizedLabels());

		int angleUnit = app.getKernel().getAngleUnit();
		angleUnitRadioDegree.setSelected(angleUnit == Kernel.ANGLE_DEGREE);
		angleUnitRadioRadian.setSelected(angleUnit == Kernel.ANGLE_RADIANT);
		angleUnitRadioDegreesMinutesSeconds.setSelected(
				angleUnit == Kernel.ANGLE_DEGREES_MINUTES_SECONDS);

		continuityRadioOn.setSelected(app.getKernel().isContinuous());
		continuityRadioOff.setSelected(!app.getKernel().isContinuous());

		usePathAndRegionParametersRadioOn.setSelected(app
				.getKernel().usePathAndRegionParameters == PathRegionHandling.ON);
		usePathAndRegionParametersRadioOff.setSelected(app
				.getKernel().usePathAndRegionParameters == PathRegionHandling.OFF);

		rightAngleRadio1
				.setSelected(app.getEuclidianView1().getRightAngleStyle() == 0);
		rightAngleRadio2
				.setSelected(app.getEuclidianView1().getRightAngleStyle() == 1);
		rightAngleRadio3
				.setSelected(app.getEuclidianView1().getRightAngleStyle() == 2);
		rightAngleRadio4
				.setSelected(app.getEuclidianView1().getRightAngleStyle() == 3);

		coordinatesRadio1.setSelected(app.getKernel().getCoordStyle() == 0);
		coordinatesRadio2.setSelected(app.getKernel().getCoordStyle() == 1);
		coordinatesRadio3.setSelected(app.getKernel().getCoordStyle() == 2);

		// cbIgnoreDocumentLayout.setSelected(settings.getLayout()
		// .isIgnoringDocumentLayout());

		/*
		 * cbShowTitleBar.setSelected(settings.getLayout().showTitleBar());
		 * cbAllowStyleBar
		 * .setSelected(settings.getLayout().isAllowingStyleBar());
		 */

		KeyboardSettings kbs = (KeyboardSettings) settings.getKeyboard();
		cbKeyboardShowAutomatic.setSelected(kbs.isShowKeyboardOnStart());

		tfKeyboardWidth.setText(Integer.toString(kbs.getKeyboardWidth()));
		tfKeyboardHeight.setText(Integer.toString(kbs.getKeyboardHeight()));

		slOpacity.removeChangeListener(this);
		slOpacity.setValue((int) (kbs.getKeyboardOpacity() * 100));
		slOpacity.addChangeListener(this);

		// tooltip timeout
		int timeoutIndex = -1;
		int currentTimeout = ToolTipManager.sharedInstance().getDismissDelay();

		// search for combobox index
		for (int i = 0; i < tooltipTimeouts.length - 1; ++i) {
			if (Integer.parseInt(tooltipTimeouts[i]) * 1000 == currentTimeout) {
				timeoutIndex = i;
			}
		}

		// no index found, must be "Off"
		if (timeoutIndex == -1) {
			timeoutIndex = tooltipTimeouts.length - 1;
		}

		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setSelectedIndex(timeoutIndex);
		cbTooltipTimeout.addActionListener(this);

		updateTooltipLanguages();
	}

	// needed updating things on the reset defaults button
	public void updateAfterReset() {
		// cbReturnAngleInverseTrig
		// .setSelected(app.getKernel().getInverseTrigReturnsAngle());

		int selectedIndex = 0;
		String loc1 = ((KeyboardSettings) settings.getKeyboard())
				.getKeyboardLocale();
		if (loc1 != null) {
			// look for index in locale list and add 1 to compensate default
			// entry
			selectedIndex = KeyboardSettings.indexOfLocale(loc1) + 1;
		}
		// take care that this doesn't fire events by accident
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage.setSelectedIndex(selectedIndex);
		cbKeyboardLanguage.addActionListener(this);

		// avoid blanking it out
		((GuiManagerD) app.getGuiManager()).toggleKeyboard(false);

		updateGUIFont();
	}

	public void updateGUIFont() {
		cbGUIFont.removeActionListener(this);

		if (cbGUIFont.getItemCount() == Util.menuFontSizesLength() + 1) {
			int gfs = app.getGUIFontSize();
			if (gfs <= -1) {
				cbGUIFont.setSelectedIndex(0);
			} else {
				for (int j = 0; j < Util.menuFontSizesLength(); j++) {
					if (Util.menuFontSizes(j) >= gfs) {
						cbGUIFont.setSelectedIndex(j + 1);
						break;
					}
				}
				if (Util.menuFontSizes(Util.menuFontSizesLength() - 1) < gfs) {
					cbGUIFont.setSelectedIndex(Util.menuFontSizesLength());
				}
			}
		}

		cbGUIFont.addActionListener(this);
	}

	public void updateTooltipLanguages() {
		ArrayList<Locale> locales = getSupportedLocales();
		if (cbTooltipLanguage.getItemCount() == locales.size() + 1) {
			Locale ttl = app.getLocalization().getTooltipLocale();
			if (ttl == null) {
				cbTooltipLanguage.setSelectedIndex(0);
			} else {
				boolean found = false;
				for (int i = 0; i < locales.size(); i++) {
					if (locales.get(i).toString().equals(ttl.toString())) {
						cbTooltipLanguage.setSelectedIndex(i + 1);
						found = true;
						break;
					}
				}
				if (!found) {
					cbTooltipLanguage.setSelectedIndex(0);
				}
			}
		}
	}

	/**
	 * Values changed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == cbTooltipTimeout) {
			int index = cbTooltipTimeout.getSelectedIndex();
			int delay = Integer.MAX_VALUE;
			if (index < tooltipTimeouts.length - 1) {
				delay = 1000 * Integer.parseInt(tooltipTimeouts[index]);
			}
			ToolTipManager.sharedInstance().setDismissDelay(delay);
			Log.debug(delay);

		} else if (source == cbTooltipLanguage) {
			int index = cbTooltipLanguage.getSelectedIndex() - 1;
			if (index == -1) {
				app.setTooltipLanguage(null);
			} else {
				app.setTooltipLanguage(
						getSupportedLocales().get(index).toString());
			}
		} else if (source == cbUseLocalDigits) {
			loc.setUseLocalizedDigits(cbUseLocalDigits.isSelected(), app);
		} else if (source == cbUseLocalLabels) {
			loc.setUseLocalizedLabels(cbUseLocalLabels.isSelected());
			/*
			 * } else if (source == cbShowTitleBar) {
			 * settings.getLayout().setShowTitleBar
			 * (cbShowTitleBar.isSelected());
			 */
			// } else if (source == cbIgnoreDocumentLayout) {
			// settings.getLayout().setIgnoreDocumentLayout(
			// cbIgnoreDocumentLayout.isSelected());
		} else if (source == angleUnitRadioDegree) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
			app.getKernel().updateConstruction(false);
			app.setUnsaved();
		} else if (source == angleUnitRadioRadian) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
			app.getKernel().updateConstruction(false);
			app.setUnsaved();
		} else if (source == angleUnitRadioDegreesMinutesSeconds) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_DEGREES_MINUTES_SECONDS);
			app.getKernel().updateConstruction(false);
			app.setUnsaved();
		} else if (source == continuityRadioOn) {
			app.getKernel().setContinuous(true);
			app.getKernel().updateConstruction(false);
			app.setUnsaved();
		} else if (source == continuityRadioOff) {
			app.getKernel().setContinuous(false);
			app.getKernel().updateConstruction(false);
			app.setUnsaved();
		} else if (source == usePathAndRegionParametersRadioOn) {
			app.getKernel()
					.setUsePathAndRegionParameters(PathRegionHandling.ON);
			// app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == usePathAndRegionParametersRadioOff) {
			app.getKernel()
					.setUsePathAndRegionParameters(PathRegionHandling.OFF);
			// app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == coordinatesRadio1) {
			app.getKernel().setCoordStyle(0);
			app.getKernel().updateConstruction(false);
		} else if (source == coordinatesRadio2) {
			app.getKernel().setCoordStyle(1);
			app.getKernel().updateConstruction(false);
		} else if (source == coordinatesRadio3) {
			app.getKernel().setCoordStyle(2);
			app.getKernel().updateConstruction(false);
		} else if (source == cbGUIFont) {
			int index = cbGUIFont.getSelectedIndex();
			if (index == 0) {
				app.setGUIFontSize(-1); // default
			} else {
				app.setGUIFontSize(Util.menuFontSizes(index - 1));
			}
		} else if (source == cbKeyboardLanguage) {
			int index = cbKeyboardLanguage.getSelectedIndex();
			if (index == 0) {
				((KeyboardSettings) settings.getKeyboard())
						.setKeyboardLocale(app.getLocale().toString());
			} else {
				((KeyboardSettings) settings.getKeyboard()).setKeyboardLocale(
						KeyboardSettings.getLocale(index - 1));
			}
		} else if (source == cbKeyboardShowAutomatic) {
			((KeyboardSettings) settings.getKeyboard()).setShowKeyboardOnStart(
					cbKeyboardShowAutomatic.isSelected());
		} else if (source == tfKeyboardWidth || source == tfKeyboardHeight) {
			changeWidthOrHeight(source);
		} else {
			handleEVOption(source);
			app.getEuclidianView1().updateAllDrawables(true);
			if (app.hasEuclidianView2EitherShowingOrNot(1)) {
				app.getEuclidianView2(1).updateAllDrawables(true);
			}
			if (app.isEuclidianView3Dinited()) {
				app.getEuclidianView3D().updateAllDrawables();
			}
		}
	}

	private void handleEVOption(Object source) {
		if (source == rightAngleRadio1) {
			app.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE);
		} else if (source == rightAngleRadio2) {
			app.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE);
		} else if (source == rightAngleRadio3) {
			app.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT);
		} else if (source == rightAngleRadio4) {
			app.setRightAngleStyle(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L);
		}
	}

	/**
	 * Slider changed.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slOpacity) {
			((KeyboardSettings) settings.getKeyboard())
					.setKeyboardOpacity(slOpacity.getValue() / 100.0f);
		}
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void focusGained(FocusEvent e) {
		//
	}

	/**
	 * Apply textfield changes.
	 */
	@Override
	public void focusLost(FocusEvent e) {

		changeWidthOrHeight(e.getSource());
	}

	private void changeWidthOrHeight(Object source) {
		KeyboardSettings kbs = ((KeyboardSettings) settings.getKeyboard());
		if (source == tfKeyboardHeight) {
			try {
				int windowHeight = Integer.parseInt(tfKeyboardHeight.getText());
				kbs.setKeyboardHeight(windowHeight);
			} catch (NumberFormatException ex) {
				app.showError(Errors.InvalidInput, tfKeyboardHeight.getText());
				tfKeyboardHeight
						.setText(Integer.toString(kbs.getKeyboardHeight()));
			}
		} else if (source == tfKeyboardWidth) {
			try {
				int windowWidth = Integer.parseInt(tfKeyboardWidth.getText());
				kbs.setKeyboardWidth(windowWidth);
			} catch (NumberFormatException ex) {
				app.showError(Errors.InvalidInput, tfKeyboardWidth.getText());
				tfKeyboardWidth
						.setText(Integer.toString(kbs.getKeyboardWidth()));
			}
		}

	}

	/**
	 * Update the language of the user interface.
	 */
	@Override
	public void setLabels() {
		virtualKeyboardPanel.setBorder(
				LayoutUtil.titleBorder(loc.getMenu("VirtualKeyboard")));
		keyboardLanguageLabel
				.setText(loc.getMenu("VirtualKeyboardLanguage") + ":");
		widthLabel.setText(loc.getMenu("Width") + ":");
		heightLabel.setText(loc.getMenu("Height") + ":");
		cbKeyboardShowAutomatic.setText(loc.getMenu("ShowAutomatically"));
		opacityLabel.setText(loc.getMenu("Opacity") + ":");

		guiFontsizePanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("FontSize")));
		guiFontSizeLabel.setText(loc.getMenu("GUIFontSize") + ":");

		tooltipPanel.setBorder(LayoutUtil.titleBorder(loc.getMenu("Tooltips")));
		tooltipLanguageLabel.setText(loc.getMenu("TooltipLanguage") + ":");
		tooltipTimeoutLabel.setText(loc.getMenu("TooltipTimeout") + ":");

		languagePanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("Language")));
		cbUseLocalDigits.setText(loc.getMenu("LocalizedDigits"));
		cbUseLocalLabels.setText(loc.getMenu("LocalizedLabels"));

		angleUnitPanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("AngleUnit")));
		angleUnitRadioDegree.setText(loc.getMenu("Degree"));
		angleUnitRadioRadian.setText(loc.getMenu("Radiant"));
		angleUnitRadioDegreesMinutesSeconds
				.setText(loc.getMenu("DegreesMinutesSeconds"));


		continuityPanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("Continuity")));
		continuityRadioOn.setText(loc.getMenu("On"));
		continuityRadioOff.setText(loc.getMenu("Off"));

		usePathAndRegionParametersPanel.setBorder(LayoutUtil
				.titleBorder(loc.getMenu("UsePathAndRegionParameters")));
		usePathAndRegionParametersRadioOn.setText(loc.getMenu("On"));
		usePathAndRegionParametersRadioOff.setText(loc.getMenu("Off"));

		rightAnglePanel.setBorder(
				LayoutUtil.titleBorder(loc.getMenu("RightAngleStyle")));
		rightAngleRadio1.setText(loc.getMenu(loc.getMenu("Off")));
		rightAngleRadio2.setText("\u25a1");
		rightAngleRadio3.setText("\u2219");
		rightAngleRadio4.setText("\u2335");
		rightAngleRadio4.setFont(app.getFontCanDisplayAwt("\u2335"));

		coordinatesPanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("Coordinates")));
		coordinatesRadio1.setText(loc.getMenu("A = (x, y)"));
		coordinatesRadio2.setText(loc.getMenu("A(x | y)"));
		coordinatesRadio3.setText(loc.getMenu("A: (x, y)"));

		// perspectivesPanel.setBorder(LayoutUtil.titleBorder(app
		// .getMenu("Perspectives")));
		// cbIgnoreDocumentLayout.setText(loc.getMenu("IgnoreDocumentLayout"));
		/*
		 * cbShowTitleBar.setText(loc.getMenu("ShowTitleBar"));
		 * cbAllowStyleBar.setText(loc.getMenu("AllowStyleBar"));
		 */

		// cbReturnAngleInverseTrig.setText(loc.getMenu("ReturnAngleInverseTrig"));
		// cbReturnAngleInverseTrig.setSelected(app.getKernel()
		// .getInverseTrigReturnsAngle());

		setLabelsKeyboardLanguage();
		setLabelsGUIFontsize();
		setLabelsTooltipLanguages();
		setLabelsTooltipTimeouts();
	}

	/**
	 * Updates the keyboard languages, this is just necessary if the language
	 * changed (or at startup). As we use an immutable list model we have to
	 * recreate the list all the time, even if we just change the label of the
	 * first item in the list.
	 */
	private void setLabelsKeyboardLanguage() {
		String[] languages = new String[KeyboardSettings.getLocaleCount()
				+ 1];
		languages[0] = loc.getMenu("Default");

		for (int i = 0; i < KeyboardSettings.getLocaleCount(); i++) {
			Locale loc1 = new Locale(KeyboardSettings.getLocale(i));

			// eg want "Norwegian", not "Norwegian (Bokmal)" etc
			languages[i + 1] = loc1.getDisplayLanguage(Locale.ENGLISH);
			if ("engb".equals(languages[i + 1])) {
				languages[i + 1] = Language.getDisplayName("enGB");
			}
		}

		int selectedIndex = cbKeyboardLanguage.getSelectedIndex();

		if (selectedIndex == -1) {
			String loc1 = ((KeyboardSettings) settings.getKeyboard())
					.getKeyboardLocale();
			if (loc1 == null) {
				selectedIndex = 0;
			} else {
				// look for index in locale list and add 1 to compensate default
				// entry
				selectedIndex = KeyboardSettings.indexOfLocale(loc1)
						+ 1;
			}
		}

		// take care that this doesn't fire events by accident
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage
				.setModel(new DefaultComboBoxModel<>(languages));
		cbKeyboardLanguage.setSelectedIndex(selectedIndex);
		cbKeyboardLanguage.addActionListener(this);
		cbKeyboardShowAutomatic.addActionListener(this);
		tfKeyboardWidth.addActionListener(this);
		tfKeyboardHeight.addActionListener(this);
	}

	private void setLabelsGUIFontsize() {

		// String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt",
		// "24 pt",
		// "28 pt", "32 pt" };

		String[] fontSizesStr = new String[Util.menuFontSizesLength() + 1];
		fontSizesStr[0] = loc.getMenu("Default");

		for (int i = 0; i < Util.menuFontSizesLength(); i++) {
			fontSizesStr[i + 1] = loc.getPlain("Apt",
					Util.menuFontSizes(i) + ""); // eg "12 pt"
		}

		int selectedIndex = cbGUIFont.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbGUIFont.removeActionListener(this);
		cbGUIFont.setModel(new DefaultComboBoxModel<>(fontSizesStr));
		cbGUIFont.setSelectedIndex(selectedIndex);
		cbGUIFont.addActionListener(this);

		updateGUIFont();
	}

	/**
	 * @see #setLabelsKeyboardLanguage()
	 */
	private void setLabelsTooltipLanguages() {
		ArrayList<Locale> locales = getSupportedLocales();
		String[] languages = new String[locales.size() + 1];
		languages[0] = loc.getMenu("Default");
		String ggbLangCode;

		for (int i = 0; i < locales.size(); i++) {
			Locale locale = locales.get(i);
			ggbLangCode = locale.getLanguage() + locale.getCountry()
					+ locale.getVariant();

			languages[i + 1] = Language.getDisplayName(ggbLangCode);
			// AppD.debug(ggbLangCode+" "+languages[i + 1]);
		}

		int selectedIndex = cbTooltipLanguage.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbTooltipLanguage.removeActionListener(this);
		cbTooltipLanguage.setModel(new DefaultComboBoxModel<>(languages));
		cbTooltipLanguage.setSelectedIndex(selectedIndex);
		cbTooltipLanguage.addActionListener(this);

		updateTooltipLanguages();
	}

	private ArrayList<Locale> getSupportedLocales() {
		return app.getLocalization()
				.getSupportedLocales(app.has(Feature.ALL_LANGUAGES));
	}

	/**
	 * @see #setLabelsKeyboardLanguage()
	 */
	private void setLabelsTooltipTimeouts() {
		tooltipTimeouts[tooltipTimeouts.length - 1] = loc.getMenu("Off");

		int selectedIndex = cbTooltipTimeout.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setModel(new DefaultComboBoxModel<>(tooltipTimeouts));
		cbTooltipTimeout.setSelectedIndex(selectedIndex);
		cbTooltipTimeout.addActionListener(this);
	}

	@Override
	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	@Override
	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	@Override
	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}

	@Override
	public void applyModifications() {
		// override this method to make the properties view apply modifications
		// when panel changes
	}

	@Override
	public void updateFont() {

		Font font = app.getPlainFont();

		virtualKeyboardPanel.setFont(font);
		keyboardLanguageLabel.setFont(font);
		widthLabel.setFont(font);
		heightLabel.setFont(font);
		cbKeyboardShowAutomatic.setFont(font);
		opacityLabel.setFont(font);

		guiFontsizePanel.setFont(font);
		guiFontSizeLabel.setFont(font);

		tooltipPanel.setFont(font);
		tooltipLanguageLabel.setFont(font);
		tooltipTimeoutLabel.setFont(font);

		languagePanel.setFont(font);
		cbUseLocalDigits.setFont(font);
		cbUseLocalLabels.setFont(font);

		angleUnitPanel.setFont(font);
		angleUnitRadioDegree.setFont(font);
		angleUnitRadioRadian.setFont(font);
		angleUnitRadioDegreesMinutesSeconds.setFont(font);

		continuityPanel.setFont(font);
		continuityRadioOn.setFont(font);
		continuityRadioOff.setFont(font);

		usePathAndRegionParametersPanel.setFont(font);
		usePathAndRegionParametersRadioOn.setFont(font);
		usePathAndRegionParametersRadioOff.setFont(font);

		rightAnglePanel.setFont(font);
		rightAngleRadio1.setFont(font);
		rightAngleRadio2.setFont(font);
		rightAngleRadio3.setFont(font);
		rightAngleRadio4.setFont(font);
		rightAngleRadio4.setFont(font);

		coordinatesPanel.setFont(font);
		coordinatesRadio1.setFont(font);
		coordinatesRadio2.setFont(font);
		coordinatesRadio3.setFont(font);

		// perspectivesPanel.setFont(font);
		// cbIgnoreDocumentLayout.setFont(font);
		/*
		 * cbShowTitleBar.setFont(font); cbAllowStyleBar.setFont(font);
		 */

		// cbReturnAngleInverseTrig.setFont(font);

		cbKeyboardLanguage.setFont(font);
		cbTooltipLanguage.setFont(font);
		cbTooltipTimeout.setFont(font);
		cbGUIFont.setFont(font);
	}

	@Override
	public void setSelected(boolean flag) {
		// see OptionsEuclidianD for possible implementation
	}

	/** available tooltip timeouts (will be reused in OptionsAdvanced) */
	final private static String[] TOOLTIP_TIMEOUTS = new String[] { "1", "3",
			"5", "10", "20", "30", "60", "0" };

	public static String tooltipTimeouts(int i) {
		return TOOLTIP_TIMEOUTS[i];
	}

	public static int tooltipTimeoutsLength() {
		return TOOLTIP_TIMEOUTS.length;
	}

}
