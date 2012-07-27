package geogebra.gui.dialog.options;

import geogebra.common.gui.SetLabels;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathRegionHandling;
import geogebra.common.main.App;
import geogebra.common.main.settings.KeyboardSettings;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.Language;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.util.FullWidthLayout;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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

/**
 * Advanced options for the options dialog.
 */
public class OptionsAdvancedD extends geogebra.common.gui.dialog.options.OptionsAdvanced implements OptionPanelD, ActionListener,
		ChangeListener, FocusListener, SetLabels {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Application object.
	 */
	private AppD app;

	/**
	 * Settings for all kind of application components.
	 */
	private Settings settings;

	/** */
	private JPanel virtualKeyboardPanel, guiFontsizePanel, tooltipPanel,
			languagePanel, perspectivesPanel, miscPanel, angleUnitPanel,
			continuityPanel, usePathAndRegionParametersPanel,
			checkboxSizePanel, rightAnglePanel, coordinatesPanel;

	/**	*/
	private JLabel keyboardLanguageLabel, guiFontSizeLabel, widthLabel,
			heightLabel, opacityLabel, tooltipLanguageLabel,
			tooltipTimeoutLabel;

	/** */
	@SuppressWarnings("rawtypes")
	private JComboBox cbKeyboardLanguage, cbTooltipLanguage, cbTooltipTimeout,
			cbGUIFont;

	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits,
			cbUseLocalLabels, cbReturnAngleInverseTrig, cbIgnoreDocumentLayout,
			cbShowTitleBar, cbAllowStyleBar, cbEnableScripting, cbUseJavaFonts,
			cbReverseMouseWheel;

	/** */
	private JRadioButton angleUnitRadioDegree, angleUnitRadioRadian,
			continuityRadioOn, continuityRadioOff,
			usePathAndRegionParametersRadioOn,
			usePathAndRegionParametersRadioOff,
			checkboxSizeRadioRegular, checkboxSizeRadioLarge, rightAngleRadio1,
			rightAngleRadio2, rightAngleRadio3, rightAngleRadio4,
			coordinatesRadio1, coordinatesRadio2, coordinatesRadio3;

	/** */
	private ButtonGroup angleUnitButtonGroup, continuityButtonGroup,
			usePathAndRegionParametersButtonGroup,
			checkboxSizeButtonGroup, rightAngleButtonGroup,
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
	private String[] tooltipTimeouts = new String[] { "1", "3", "5", "10",
			"20", "30", "60", "-" };

	private JPanel wrappedPanel;

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAdvancedD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
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
		initPerspectivesPanel();
		initScriptingPanel();
		initAngleUnitPanel();
		initContinuityPanel();
		initUsePathAndRegionParametersPanel();
		initCheckboxSizePanel();
		initRightAnglePanel();
		initCoordinatesPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());
		
		panel.add(virtualKeyboardPanel);
		panel.add(angleUnitPanel);
		panel.add(continuityPanel);
		panel.add(usePathAndRegionParametersPanel);
		panel.add(checkboxSizePanel);
		panel.add(rightAnglePanel);
		panel.add(coordinatesPanel);
		panel.add(guiFontsizePanel);	
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(perspectivesPanel);
		
		
		panel.add(miscPanel);

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		wrappedPanel.add(scrollPane, BorderLayout.CENTER);
		

		setLabels();
	}

	/**
	 * Initialize the virtual keyboard panel
	 */
	@SuppressWarnings("rawtypes")
	private void initVirtualKeyboardPanel() {
		virtualKeyboardPanel = new JPanel();
		virtualKeyboardPanel.setLayout(new BoxLayout(virtualKeyboardPanel,
				BoxLayout.Y_AXIS));

		
		
		keyboardLanguageLabel = new JLabel();
		virtualKeyboardPanel.add(OptionsUtil.flowPanel(keyboardLanguageLabel));
		cbKeyboardLanguage = new JComboBox();
		// listener to this combo box is added in setLabels()
		virtualKeyboardPanel.add(OptionsUtil.flowPanel(Box.createHorizontalStrut(20),cbKeyboardLanguage));

		widthLabel = new JLabel();
		tfKeyboardWidth = new JTextField(3);
		tfKeyboardWidth.addFocusListener(this);
		heightLabel = new JLabel();
		tfKeyboardHeight = new JTextField(3);
		tfKeyboardHeight.addFocusListener(this);

		virtualKeyboardPanel.add(OptionsUtil.flowPanel(widthLabel, tfKeyboardWidth,
				new JLabel("px"), Box.createHorizontalStrut(10), heightLabel,
				tfKeyboardHeight, new JLabel("px")));
		
		cbKeyboardShowAutomatic = new JCheckBox();
		
		opacityLabel = new JLabel();
		slOpacity = new JSlider(25, 100);
		slOpacity.setPreferredSize(new Dimension(100, (int) slOpacity
				.getPreferredSize().getHeight()));
		// listener added in updateGUI()
		opacityLabel.setLabelFor(slOpacity);
		virtualKeyboardPanel.add(OptionsUtil.flowPanel(cbKeyboardShowAutomatic, opacityLabel,slOpacity));
		
	}

	/**
	 * Initialize the GUI fontsize panel
	 */
	@SuppressWarnings("rawtypes")
	private void initGUIFontSizePanel() {
		guiFontsizePanel = new JPanel();
		guiFontsizePanel.setLayout(new BoxLayout(guiFontsizePanel,
				BoxLayout.Y_AXIS));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));

		guiFontSizeLabel = new JLabel();
		panel.add(guiFontSizeLabel);

		cbGUIFont = new JComboBox();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initTooltipPanel() {
		tooltipPanel = new JPanel();
		tooltipPanel.setLayout(new BoxLayout(tooltipPanel, BoxLayout.Y_AXIS));
		
		
		tooltipLanguageLabel = new JLabel();
		tooltipPanel.add(OptionsUtil.flowPanel(tooltipLanguageLabel));
		cbTooltipLanguage = new JComboBox();
		cbTooltipLanguage.setRenderer(new LanguageRenderer(app));
		// listener to this combo box is added in setLabels()
		tooltipPanel.add(OptionsUtil.flowPanel(Box.createHorizontalStrut(20),cbTooltipLanguage));

		tooltipTimeoutLabel = new JLabel();
		
		// get tooltipTimeouts from MyXMLHandler
		tooltipTimeouts = new String[MyXMLHandler.tooltipTimeouts.length];
		for (int i = 0; i < MyXMLHandler.tooltipTimeouts.length - 1; i++)
			tooltipTimeouts[i] = MyXMLHandler.tooltipTimeouts[i];
		tooltipTimeouts[tooltipTimeouts.length - 1] = "-";

		cbTooltipTimeout = new JComboBox(tooltipTimeouts);
		
		tooltipPanel.add(OptionsUtil.flowPanel(tooltipTimeoutLabel,cbTooltipTimeout));
	}

	/**
	 * Initialize the perspectives panel.
	 */
	private void initPerspectivesPanel() {
		perspectivesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		cbShowTitleBar = new JCheckBox();
		cbShowTitleBar.addActionListener(this);
		//perspectivesPanel.add(cbShowTitleBar);

		cbAllowStyleBar = new JCheckBox();
		cbAllowStyleBar.addActionListener(this);
		//perspectivesPanel.add(cbAllowStyleBar);
		
		cbIgnoreDocumentLayout = new JCheckBox();
		cbIgnoreDocumentLayout.addActionListener(this);
		perspectivesPanel.add(OptionsUtil.flowPanel(cbIgnoreDocumentLayout));

	}

	/**
	 * Initialize the scripting panel.
	 */
	private void initScriptingPanel() {

		miscPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// two columns
		JPanel guiPanelWest = new JPanel();
		guiPanelWest.setLayout(new BoxLayout(guiPanelWest, BoxLayout.Y_AXIS));
		JPanel guiPanelEast = new JPanel();
		guiPanelEast.setLayout(new BoxLayout(guiPanelEast, BoxLayout.Y_AXIS));
		JPanel twoColumns = new JPanel();
		twoColumns.setLayout(new BorderLayout());
		twoColumns.add(guiPanelEast, BorderLayout.EAST);
		twoColumns.add(guiPanelWest, BorderLayout.WEST);
		twoColumns.setAlignmentX(wrappedPanel.LEFT_ALIGNMENT);
		miscPanel.add(twoColumns);

		cbEnableScripting = new JCheckBox();
		cbEnableScripting.addActionListener(this);
		guiPanelWest.add(cbEnableScripting);

		cbReturnAngleInverseTrig = new JCheckBox();
		cbReturnAngleInverseTrig.addActionListener(this);
		guiPanelEast.add(cbReturnAngleInverseTrig);

		cbUseJavaFonts = new JCheckBox();
		cbUseJavaFonts.addActionListener(this);
		guiPanelEast.add(cbUseJavaFonts);

		cbReverseMouseWheel = new JCheckBox();
		cbReverseMouseWheel.addActionListener(this);
		guiPanelWest.add(cbReverseMouseWheel);

	}

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
		usePathAndRegionParametersPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));

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
	 * Initialize the checkbox size panel
	 */
	private void initCheckboxSizePanel() {
		checkboxSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		checkboxSizeButtonGroup = new ButtonGroup();

		checkboxSizeRadioRegular = new JRadioButton();
		checkboxSizeRadioRegular.addActionListener(this);
		checkboxSizePanel.add(checkboxSizeRadioRegular);
		checkboxSizeButtonGroup.add(checkboxSizeRadioRegular);

		checkboxSizeRadioLarge = new JRadioButton();
		checkboxSizeRadioLarge.addActionListener(this);
		checkboxSizePanel.add(checkboxSizeRadioLarge);
		checkboxSizeButtonGroup.add(checkboxSizeRadioLarge);
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
	public void updateGUI() {

		cbEnableScripting.setSelected(!app.isScriptingDisabled());
		cbUseLocalDigits.setSelected(app.isUsingLocalizedDigits());
		cbUseLocalLabels.setSelected(app.isUsingLocalizedLabels());

		angleUnitRadioDegree
				.setSelected(app.getKernel().getAngleUnit() == Kernel.ANGLE_DEGREE);
		angleUnitRadioRadian
				.setSelected(app.getKernel().getAngleUnit() != Kernel.ANGLE_DEGREE);

		continuityRadioOn.setSelected(app.getKernel().isContinuous());
		continuityRadioOff.setSelected(!app.getKernel().isContinuous());

		usePathAndRegionParametersRadioOn.setSelected(app.getKernel()
				.usePathAndRegionParameters == PathRegionHandling.ON);
		usePathAndRegionParametersRadioOff.setSelected(app.getKernel()
				.usePathAndRegionParameters == PathRegionHandling.OFF);
		checkboxSizeRadioRegular.setSelected(app.getEuclidianView1()
				.getBooleanSize() == 13);
		checkboxSizeRadioLarge.setSelected(app.getEuclidianView1()
				.getBooleanSize() == 26);


		rightAngleRadio1.setSelected(app.getEuclidianView1()
				.getRightAngleStyle() == 0);
		rightAngleRadio2.setSelected(app.getEuclidianView1()
				.getRightAngleStyle() == 1);
		rightAngleRadio3.setSelected(app.getEuclidianView1()
				.getRightAngleStyle() == 2);
		rightAngleRadio4.setSelected(app.getEuclidianView1()
				.getRightAngleStyle() == 3);

		coordinatesRadio1.setSelected(app.getKernel().getCoordStyle() == 0);
		coordinatesRadio2.setSelected(app.getKernel().getCoordStyle() == 1);
		coordinatesRadio3.setSelected(app.getKernel().getCoordStyle() == 2);

		cbIgnoreDocumentLayout.setSelected(settings.getLayout()
				.isIgnoringDocumentLayout());
		cbShowTitleBar.setSelected(settings.getLayout().showTitleBar());
		cbAllowStyleBar.setSelected(settings.getLayout().isAllowingStyleBar());

		KeyboardSettings kbs = settings.getKeyboard();
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

		// no index found, must be "off"
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
		cbReturnAngleInverseTrig.setSelected(app.getKernel().getInverseTrigReturnsAngle());
		cbUseJavaFonts.setSelected(app.useJavaFontsForLaTeX());
		cbReverseMouseWheel.setSelected(app.isMouseWheelReversed());		

		int selectedIndex = 0;
		String loc = settings.getKeyboard().getKeyboardLocale();
		if (loc != null) {
			// look for index in locale list and add 1 to compensate default
			// entry
			selectedIndex = KeyboardSettings.supportedLocales.indexOf(loc) + 1;
		}
		// take care that this doesn't fire events by accident
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage.setSelectedIndex(selectedIndex);
		cbKeyboardLanguage.addActionListener(this);

		// avoid blanking it out
		app.getGuiManager().toggleKeyboard(false);

		updateGUIFont();
	}

	public void updateGUIFont() {
		cbGUIFont.removeActionListener(this);

		if (cbGUIFont.getItemCount() == MyXMLHandler.menuFontSizes.length + 1) {
			int gfs = app.getGUIFontSize();
			if (gfs <= -1) {
				cbGUIFont.setSelectedIndex(0);
			} else {
				for (int j = 0; j < MyXMLHandler.menuFontSizes.length; j++) {
					if (MyXMLHandler.menuFontSizes[j] >= gfs) {
						cbGUIFont.setSelectedIndex(j + 1);
						break;
					}
				}
				if (MyXMLHandler.menuFontSizes[MyXMLHandler.menuFontSizes.length - 1] < gfs) {
					cbGUIFont
							.setSelectedIndex(MyXMLHandler.menuFontSizes.length);
				}
			}
		}

		cbGUIFont.addActionListener(this);
	}

	public void updateTooltipLanguages() {
		if (cbTooltipLanguage.getItemCount() == AppD.getSupportedLocales()
				.size() + 1) {
			Locale ttl = app.getTooltipLanguage();
			if (ttl == null) {
				cbTooltipLanguage.setSelectedIndex(0);
			} else {
				boolean found = false;
				for (int i = 0; i < AppD.getSupportedLocales().size(); i++) {
					if (AppD.getSupportedLocales().get(i).toString()
							.equals(ttl.toString())) {
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
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == cbTooltipTimeout) {
			int index = cbTooltipTimeout.getSelectedIndex();
			int delay = Integer.MAX_VALUE;
			if (index < tooltipTimeouts.length - 1) {
				delay = 1000 * Integer.parseInt(tooltipTimeouts[index]);
			}
			ToolTipManager.sharedInstance().setDismissDelay(delay);
			App.debug(delay);

		} else if (source == cbTooltipLanguage) {
			int index = cbTooltipLanguage.getSelectedIndex() - 1;
			if (index == -1)
				app.setTooltipLanguage(null);
			else
				app.setTooltipLanguage(AppD.getSupportedLocales().get(index)
						.toString());
		} else if (source == cbEnableScripting) {
			app.setScriptingDisabled(!cbEnableScripting.isSelected());
		} else if (source == cbUseJavaFonts) {
			app.getDrawEquation().setUseJavaFontsForLaTeX(app,
					cbUseJavaFonts.isSelected());
		} else if (source == cbReverseMouseWheel) {
			app.reverseMouseWheel(cbReverseMouseWheel.isSelected());
		} else if (source == cbUseLocalDigits) {
			app.setUseLocalizedDigits(cbUseLocalDigits.isSelected());
		} else if (source == cbReturnAngleInverseTrig) {
			app.getKernel().setInverseTrigReturnsAngle(
					cbReturnAngleInverseTrig.isSelected());

			// make sure all calculations fully updated
			// app.getKernel().updateConstruction(); doesn't do what we want
			app.getKernel().getConstruction().getUndoManager()
					.storeUndoInfo(true);

		} else if (source == cbUseLocalLabels) {
			app.setUseLocalizedLabels(cbUseLocalLabels.isSelected());
		} else if (source == cbShowTitleBar) {
			settings.getLayout().setShowTitleBar(cbShowTitleBar.isSelected());
		} else if (source == cbIgnoreDocumentLayout) {
			settings.getLayout().setIgnoreDocumentLayout(
					cbIgnoreDocumentLayout.isSelected());
		} else if (source == cbAllowStyleBar) {
			settings.getLayout().setAllowStyleBar(cbAllowStyleBar.isSelected());
		} else if (source == angleUnitRadioDegree) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == angleUnitRadioRadian) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == continuityRadioOn) {
			app.getKernel().setContinuous(true);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == continuityRadioOff) {
			app.getKernel().setContinuous(false);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == usePathAndRegionParametersRadioOn) {
			app.getKernel().setUsePathAndRegionParameters(PathRegionHandling.ON);
			// app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == usePathAndRegionParametersRadioOff) {
			app.getKernel().setUsePathAndRegionParameters(PathRegionHandling.OFF);
			// app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (source == coordinatesRadio1) {
			app.getKernel().setCoordStyle(0);
			app.getKernel().updateConstruction();
		} else if (source == coordinatesRadio2) {
			app.getKernel().setCoordStyle(1);
			app.getKernel().updateConstruction();
		} else if (source == coordinatesRadio3) {
			app.getKernel().setCoordStyle(2);
			app.getKernel().updateConstruction();
		} else if (source == cbGUIFont) {
			int index = cbGUIFont.getSelectedIndex();
			if (index == 0)
				app.setGUIFontSize(-1); // default
			else
				app.setGUIFontSize(MyXMLHandler.menuFontSizes[index - 1]);
		} else if (source == cbKeyboardLanguage) {
			int index = cbKeyboardLanguage.getSelectedIndex();
			if (index == 0)
				settings.getKeyboard().setKeyboardLocale(
						app.getLocale().toString());
			else
				settings.getKeyboard().setKeyboardLocale(
						KeyboardSettings.supportedLocales.get(index - 1));
		} else if (source == cbKeyboardShowAutomatic) {
			settings.getKeyboard().setShowKeyboardOnStart(
					cbKeyboardShowAutomatic.isSelected());
		} else if (source == tfKeyboardWidth || source == tfKeyboardHeight) {
			changeWidthOrHeight(source);
		} else {
			handleEVOption(source,app.getEuclidianView1());
			if(app.hasEuclidianView2EitherShowingOrNot()){
				handleEVOption(source, app.getEuclidianView2());
			}
		}
	}

	private void handleEVOption(Object source, EuclidianViewD view) {
		if (source == checkboxSizeRadioRegular) {
			view.setBooleanSize(13);
		} else if (source == checkboxSizeRadioLarge) {
			view.setBooleanSize(26);
		} else if (source == rightAngleRadio1) {
			view.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE);
		} else if (source == rightAngleRadio2) {
			view.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE);
		} else if (source == rightAngleRadio3) {
			view.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT);
		} else if (source == rightAngleRadio4) {
			view.setRightAngleStyle(
					EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L);
		}
	}

	/**
	 * Slider changed.
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slOpacity) {
			settings.getKeyboard().setKeyboardOpacity(
					slOpacity.getValue() / 100.0f);
		}
	}

	/**
	 * Not implemented.
	 */
	public void focusGained(FocusEvent e) {
	}

	/**
	 * Apply textfield changes.
	 */
	public void focusLost(FocusEvent e) {

		changeWidthOrHeight(e.getSource());
	}

	private void changeWidthOrHeight(Object source) {
		KeyboardSettings kbs = settings.getKeyboard();
		if (source == tfKeyboardHeight) {
			try {
				int windowHeight = Integer.parseInt(tfKeyboardHeight.getText());
				kbs.setKeyboardHeight(windowHeight);
			} catch (NumberFormatException ex) {
				app.showError("InvalidInput", tfKeyboardHeight.getText());
				tfKeyboardHeight.setText(Integer.toString(kbs
						.getKeyboardHeight()));
			}
		} else if (source == tfKeyboardWidth) {
			try {
				int windowWidth = Integer.parseInt(tfKeyboardWidth.getText());
				kbs.setKeyboardWidth(windowWidth);
			} catch (NumberFormatException ex) {
				app.showError("InvalidInput", tfKeyboardWidth.getText());
				tfKeyboardWidth
						.setText(Integer.toString(kbs.getKeyboardWidth()));
			}
		}

	}

	
	
	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		virtualKeyboardPanel.setBorder(OptionsUtil.titleBorder(app
				.getPlain("VirtualKeyboard")));
		keyboardLanguageLabel.setText(app.getPlain("VirtualKeyboardLanguage")
				+ ":");
		widthLabel.setText(app.getPlain("Width") + ":");
		heightLabel.setText(app.getPlain("Height") + ":");
		cbKeyboardShowAutomatic.setText(app.getPlain("ShowAutomatically"));
		opacityLabel.setText(app.getMenu("Opacity") + ":");

		guiFontsizePanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("FontSize")));
		guiFontSizeLabel.setText(app.getMenu("GUIFontSize") + ":");

		tooltipPanel.setBorder(OptionsUtil.titleBorder(app
				.getPlain("Tooltips")));
		tooltipLanguageLabel.setText(app.getPlain("TooltipLanguage") + ":");
		tooltipTimeoutLabel.setText(app.getPlain("TooltipTimeout") + ":");

		languagePanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("Language")));
		cbUseLocalDigits.setText(app.getPlain("LocalizedDigits"));
		cbUseLocalLabels.setText(app.getPlain("LocalizedLabels"));

		angleUnitPanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("AngleUnit")));
		angleUnitRadioDegree.setText(app.getMenu("Degree"));
		angleUnitRadioRadian.setText(app.getMenu("Radiant"));

		continuityPanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("Continuity")));
		continuityRadioOn.setText(app.getMenu("on"));
		continuityRadioOff.setText(app.getMenu("off"));

		usePathAndRegionParametersPanel.setBorder(OptionsUtil.titleBorder(app.getMenu("UsePathAndRegionParameters")));
		usePathAndRegionParametersRadioOn.setText(app.getMenu("on"));
		usePathAndRegionParametersRadioOff.setText(app.getMenu("off"));

		checkboxSizePanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("CheckboxSize")));
		checkboxSizeRadioRegular.setText(app.getMenu("CheckboxSize.Regular"));
		checkboxSizeRadioLarge.setText(app.getMenu("CheckboxSize.Large"));

		rightAnglePanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("RightAngleStyle")));
		rightAngleRadio1.setText(app.getMenu(app.getPlain("off")));
		rightAngleRadio2.setText("\u25a1");
		rightAngleRadio3.setText("\u2219");
		rightAngleRadio4.setText("\u2335");
		rightAngleRadio4.setFont(app.getFontCanDisplayAwt("\u2335"));

		coordinatesPanel.setBorder(OptionsUtil.titleBorder(app
				.getPlain("Coordinates")));
		coordinatesRadio1.setText(app.getMenu("A = (x, y)"));
		coordinatesRadio2.setText(app.getMenu("A(x | y)"));
		coordinatesRadio3.setText(app.getMenu("A: (x, y)"));

		perspectivesPanel.setBorder(OptionsUtil.titleBorder(app
				.getMenu("Perspectives")));
		cbIgnoreDocumentLayout.setText(app.getPlain("IgnoreDocumentLayout"));
		cbShowTitleBar.setText(app.getPlain("ShowTitleBar"));
		cbAllowStyleBar.setText(app.getPlain("AllowStyleBar"));

		miscPanel.setBorder(OptionsUtil.titleBorder(app
				.getPlain("Miscellaneous")));
		cbEnableScripting.setText(app.getPlain("EnableScripting"));
		// cbEnableScripting.setSelected(b)
		cbUseJavaFonts.setText(app.getPlain("UseJavaFontsForLaTeX"));
		cbUseJavaFonts.setSelected(app.useJavaFontsForLaTeX());
		cbReverseMouseWheel.setText(app.getPlain("ReverseMouseWheel"));
		cbReverseMouseWheel.setSelected(app.isMouseWheelReversed());
		cbReturnAngleInverseTrig.setText(app.getMenu("ReturnAngleInverseTrig"));
		cbReturnAngleInverseTrig.setSelected(app.getKernel()
				.getInverseTrigReturnsAngle());

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
		String[] languages = new String[KeyboardSettings.supportedLocales
				.size() + 1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < KeyboardSettings.supportedLocales.size(); i++) {
			Locale loc = new Locale(KeyboardSettings.supportedLocales.get(i));
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			// eg want "Norwegian", not "Norwegian (Bokmal)" etc
			languages[i + 1] = loc.getDisplayLanguage(Locale.ENGLISH);
			if (languages[i + 1] == "en_gb") {
				languages[i + 1] = Language.getDisplayName("enGB");
			}
		}

		int selectedIndex = cbKeyboardLanguage.getSelectedIndex();

		if (selectedIndex == -1) {
			String loc = settings.getKeyboard().getKeyboardLocale();
			if (loc == null) {
				selectedIndex = 0;
			} else {
				// look for index in locale list and add 1 to compensate default
				// entry
				selectedIndex = KeyboardSettings.supportedLocales.indexOf(loc) + 1;
			}
		}

		// take care that this doesn't fire events by accident
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage.setModel(new DefaultComboBoxModel(languages));
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

		String[] fontSizesStr = new String[MyXMLHandler.menuFontSizes.length + 1];
		fontSizesStr[0] = app.getPlain("Default");

		for (int i = 0; i < MyXMLHandler.menuFontSizes.length; i++) {
			fontSizesStr[i + 1] = app.getPlain("Apt",
					MyXMLHandler.menuFontSizes[i] + ""); // eg "12 pt"
		}

		int selectedIndex = cbGUIFont.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbGUIFont.removeActionListener(this);
		cbGUIFont.setModel(new DefaultComboBoxModel(fontSizesStr));
		cbGUIFont.setSelectedIndex(selectedIndex);
		cbGUIFont.addActionListener(this);

		updateGUIFont();
	}

	/**
	 * @see #setLabelsKeyboardLanguage()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setLabelsTooltipLanguages() {
		String[] languages = new String[AppD.getSupportedLocales().size() + 1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < AppD.getSupportedLocales().size(); i++) {
			Locale loc = AppD.getSupportedLocales().get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			languages[i + 1] = Language.getDisplayName(ggbLangCode);
			//AppD.debug(ggbLangCode+" "+languages[i + 1]);
		}

		int selectedIndex = cbTooltipLanguage.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbTooltipLanguage.removeActionListener(this);
		cbTooltipLanguage.setModel(new DefaultComboBoxModel(languages));
		cbTooltipLanguage.setSelectedIndex(selectedIndex);
		cbTooltipLanguage.addActionListener(this);

		updateTooltipLanguages();
	}

	/**
	 * @see #setLabelsKeyboardLanguage()
	 */
	private void setLabelsTooltipTimeouts() {
		tooltipTimeouts[tooltipTimeouts.length - 1] = app.getPlain("off");

		int selectedIndex = cbTooltipTimeout.getSelectedIndex();

		// take care that this doesn't fire events by accident
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setModel(new DefaultComboBoxModel(tooltipTimeouts));
		cbTooltipTimeout.setSelectedIndex(selectedIndex);
		cbTooltipTimeout.addActionListener(this);
	}

	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	public void revalidate() {
		// TODO Auto-generated method stub
		
	}

	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}
}
