package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.FullWidthLayout;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.settings.KeyboardSettings;
import geogebra.main.settings.Settings;

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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAdvanced  extends JPanel implements ActionListener, ChangeListener, FocusListener, SetLabels {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Application object.
	 */
	private Application app;
	
	/**
	 * Settings for all kind of application components.
	 */
	private Settings settings;

	/** */
	private JPanel virtualKeyboardPanel, guiFontsizePanel, tooltipPanel, languagePanel,  perspectivesPanel, miscPanel, angleUnitPanel, continuityPanel, pointStylePanel, checkboxSizePanel, rightAnglePanel, coordinatesPanel;
	
	/**	*/
	private JLabel keyboardLanguageLabel, guiFontSizeLabel, widthLabel, heightLabel, opacityLabel, tooltipLanguageLabel, tooltipTimeoutLabel;
	
	/** */
	private JComboBox cbKeyboardLanguage, cbTooltipLanguage, cbTooltipTimeout, cbGUIFont;
	
	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits, cbUseLocalLabels, cbReturnAngleInverseTrig, cbIgnoreDocumentLayout, cbShowTitleBar, cbAllowStyleBar, cbEnableScripting, cbUseJavaFonts, cbReverseMouseWheel;
	
	/** */
	private JRadioButton angleUnitRadioDegree, angleUnitRadioRadian, continuityRadioOn, continuityRadioOff, pointStyleRadio0, pointStyleRadio1, pointStyleRadio2, pointStyleRadio3, pointStyleRadio4, pointStyleRadio6, pointStyleRadio7, checkboxSizeRadioRegular, checkboxSizeRadioLarge, rightAngleRadio1, rightAngleRadio2, rightAngleRadio3, rightAngleRadio4, coordinatesRadio1, coordinatesRadio2, coordinatesRadio3;
	
	/** */
	private ButtonGroup angleUnitButtonGroup, continuityButtonGroup, pointStyleButtonGroup, checkboxSizeButtonGroup, rightAngleButtonGroup, coordinatesButtonGroup;
	
	/** */
	private JTextField tfKeyboardWidth, tfKeyboardHeight;
	
	/** */
	private JSlider slOpacity;
	
	/**
	 * Timeout values of tooltips (last entry reserved for "Off", but that has to be translated)
	 * This is just an example,
	 * it will be overwritten by tooltipTimeouts in MyXMLHandler, plus "-" instead of "0"
	 */
	private String[] tooltipTimeouts = new String[] {
		"1",
		"3",
		"5",
		"10",
		"20",
		"30",
		"60",
		"-"
	};

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAdvanced(Application app) {
		super(new BorderLayout());
		
		this.app = app;
		this.settings = app.getSettings();
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of calling setLabels()
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
		initPointStylePanel();
		initCheckboxSizePanel();
		initRightAnglePanel();
		initCoordinatesPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());
		panel.add(virtualKeyboardPanel);
		panel.add(guiFontsizePanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(perspectivesPanel);
		panel.add(angleUnitPanel);
		panel.add(continuityPanel);
		panel.add(pointStylePanel);
		panel.add(checkboxSizePanel);
		panel.add(rightAnglePanel);
		panel.add(coordinatesPanel);
		panel.add(miscPanel);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the virtual keyboard panel
	 */
	private void initVirtualKeyboardPanel() {
		virtualKeyboardPanel = new JPanel();
		virtualKeyboardPanel.setLayout(new BoxLayout(virtualKeyboardPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		keyboardLanguageLabel = new JLabel();
		panel.add(keyboardLanguageLabel);
		
		cbKeyboardLanguage = new JComboBox();
		// listener to this combo box is added in setLabels()
		panel.add(cbKeyboardLanguage);
		
		virtualKeyboardPanel.add(panel, BorderLayout.NORTH);
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		widthLabel = new JLabel();
		panel.add(widthLabel);
		
		tfKeyboardWidth = new JTextField(3);
		tfKeyboardWidth.addFocusListener(this);
		panel.add(tfKeyboardWidth);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		heightLabel = new JLabel();
		panel.add(heightLabel);
		
		tfKeyboardHeight = new JTextField(3);
		tfKeyboardHeight.addFocusListener(this);
		panel.add(tfKeyboardHeight);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		cbKeyboardShowAutomatic = new JCheckBox();
		panel.add(cbKeyboardShowAutomatic);
		
		opacityLabel = new JLabel();
		panel.add(opacityLabel);
		
		slOpacity = new JSlider(25, 100);
		slOpacity.setPreferredSize(new Dimension(100, (int)slOpacity.getPreferredSize().getHeight()));
		// listener added in updateGUI()
		panel.add(slOpacity);
		
		opacityLabel.setLabelFor(slOpacity);
		
		virtualKeyboardPanel.add(panel, BorderLayout.CENTER);
	}	
	
	/**
	 * Initialize the GUI fontsize panel
	 */
	private void initGUIFontSizePanel() {
		guiFontsizePanel = new JPanel();
		guiFontsizePanel.setLayout(new BoxLayout(guiFontsizePanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
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
	private void initTooltipPanel() {
		tooltipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	
		
		tooltipLanguageLabel = new JLabel();
		tooltipPanel.add(tooltipLanguageLabel);
		
		cbTooltipLanguage = new JComboBox();
		// listener to this combo box is added in setLabels()
		tooltipPanel.add(cbTooltipLanguage);

		tooltipTimeoutLabel = new JLabel();
		tooltipPanel.add(tooltipTimeoutLabel);

		// get tooltipTimeouts from MyXMLHandler
		tooltipTimeouts = new String[MyXMLHandler.tooltipTimeouts.length];
		for (int i = 0; i < MyXMLHandler.tooltipTimeouts.length - 1; i++)
			tooltipTimeouts[i] = MyXMLHandler.tooltipTimeouts[i];
		tooltipTimeouts[tooltipTimeouts.length - 1] = "-";

		cbTooltipTimeout = new JComboBox(tooltipTimeouts);
		tooltipPanel.add(cbTooltipTimeout);
	}
	
	
	/**
	 * Initialize the perspectives panel.
	 */
	private void initPerspectivesPanel() {
		perspectivesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbShowTitleBar = new JCheckBox();
		cbShowTitleBar.addActionListener(this);
		perspectivesPanel.add(cbShowTitleBar);

		cbIgnoreDocumentLayout = new JCheckBox();
		cbIgnoreDocumentLayout.addActionListener(this);
		perspectivesPanel.add(cbIgnoreDocumentLayout);

		cbAllowStyleBar = new JCheckBox();
		cbAllowStyleBar.addActionListener(this);
		perspectivesPanel.add(cbAllowStyleBar);
		
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
		twoColumns.setAlignmentX(LEFT_ALIGNMENT);
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
	 * Initialize the point style panel
	 */
	private void initPointStylePanel() {
		pointStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pointStyleButtonGroup = new ButtonGroup();

		pointStyleRadio0 = new JRadioButton();
		pointStyleRadio0.addActionListener(this);
		pointStylePanel.add(pointStyleRadio0);
		pointStyleButtonGroup.add(pointStyleRadio0);

		pointStyleRadio2 = new JRadioButton();
		pointStyleRadio2.addActionListener(this);
		pointStylePanel.add(pointStyleRadio2);
		pointStyleButtonGroup.add(pointStyleRadio2);

		pointStyleRadio1 = new JRadioButton();
		pointStyleRadio1.addActionListener(this);
		pointStylePanel.add(pointStyleRadio1);
		pointStyleButtonGroup.add(pointStyleRadio1);

		pointStyleRadio3 = new JRadioButton();
		pointStyleRadio3.addActionListener(this);
		pointStylePanel.add(pointStyleRadio3);
		pointStyleButtonGroup.add(pointStyleRadio3);

		pointStyleRadio4 = new JRadioButton();
		pointStyleRadio4.addActionListener(this);
		pointStylePanel.add(pointStyleRadio4);
		pointStyleButtonGroup.add(pointStyleRadio4);

		pointStyleRadio6 = new JRadioButton();
		pointStyleRadio6.addActionListener(this);
		pointStylePanel.add(pointStyleRadio6);
		pointStyleButtonGroup.add(pointStyleRadio6);

		pointStyleRadio7 = new JRadioButton();
		pointStyleRadio7.addActionListener(this);
		pointStylePanel.add(pointStyleRadio7);
		pointStyleButtonGroup.add(pointStyleRadio7);
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

		angleUnitRadioDegree.setSelected(app.getKernel().getAngleUnit() == Kernel.ANGLE_DEGREE);
		angleUnitRadioRadian.setSelected(app.getKernel().getAngleUnit() != Kernel.ANGLE_DEGREE);

		continuityRadioOn.setSelected(app.getKernel().isContinuous());
		continuityRadioOff.setSelected(!app.getKernel().isContinuous());

		checkboxSizeRadioRegular.setSelected(app.getEuclidianView().getBooleanSize() == 13);
		checkboxSizeRadioLarge.setSelected(app.getEuclidianView().getBooleanSize() == 26);

		switch (app.getEuclidianView().getPointStyle()) {
		case 1:
			pointStyleRadio1.setSelected(true);
			break;
		case 2:
			pointStyleRadio2.setSelected(true);
			break;
		case 3:
			pointStyleRadio3.setSelected(true);
			break;
		case 4:
			pointStyleRadio4.setSelected(true);
			break;
		case 6:
			pointStyleRadio6.setSelected(true);
			break;
		case 7:
			pointStyleRadio7.setSelected(true);
			break;
		case 0:
		default:
			pointStyleRadio0.setSelected(true);
			break;
		}

		rightAngleRadio1.setSelected(app.getEuclidianView().getRightAngleStyle() == 0);
		rightAngleRadio2.setSelected(app.getEuclidianView().getRightAngleStyle() == 1);
		rightAngleRadio3.setSelected(app.getEuclidianView().getRightAngleStyle() == 2);
		rightAngleRadio4.setSelected(app.getEuclidianView().getRightAngleStyle() == 3);

		coordinatesRadio1.setSelected(app.getKernel().getCoordStyle() == 0);
		coordinatesRadio2.setSelected(app.getKernel().getCoordStyle() == 1);
		coordinatesRadio3.setSelected(app.getKernel().getCoordStyle() == 2);

		cbIgnoreDocumentLayout.setSelected(settings.getLayout().isIgnoringDocumentLayout());
		cbShowTitleBar.setSelected(settings.getLayout().showTitleBar());
		cbAllowStyleBar.setSelected(settings.getLayout().isAllowingStyleBar());
		
		KeyboardSettings kbs = settings.getKeyboard();
		cbKeyboardShowAutomatic.setSelected(kbs.isShowKeyboardOnStart());
		
		tfKeyboardWidth.setText(Integer.toString(kbs.getKeyboardWidth()));
		tfKeyboardHeight.setText(Integer.toString(kbs.getKeyboardHeight()));
		
		slOpacity.removeChangeListener(this);
		slOpacity.setValue((int)(kbs.getKeyboardOpacity() * 100));
		slOpacity.addChangeListener(this);
		
		// tooltip timeout
		int timeoutIndex = -1;
		int currentTimeout = ToolTipManager.sharedInstance().getDismissDelay();

		// search for combobox index
		for(int i = 0; i < tooltipTimeouts.length-1; ++i) {
			if(Integer.parseInt(tooltipTimeouts[i]) * 1000 == currentTimeout) {
				timeoutIndex = i;
			}
		}
		
		// no index found, must be "off"
		if(timeoutIndex == -1) {
			timeoutIndex = tooltipTimeouts.length-1;
		}
		
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setSelectedIndex(timeoutIndex);
		cbTooltipTimeout.addActionListener(this);

		updateTooltipLanguages();
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
						cbGUIFont.setSelectedIndex(j+1);
						break;
					}
				}
				if (MyXMLHandler.menuFontSizes[MyXMLHandler.menuFontSizes.length-1] < gfs) {
					cbGUIFont.setSelectedIndex(MyXMLHandler.menuFontSizes.length);
				}
			}
		}
		
		cbGUIFont.addActionListener(this);
	}

	public void updateTooltipLanguages() {
		if (cbTooltipLanguage.getItemCount() == Application.supportedLocales.size() + 1) {
			Locale ttl = app.getTooltipLanguage();
			if (ttl == null) {
				cbTooltipLanguage.setSelectedIndex(0);
			} else {
				boolean found = false;
				for (int i = 0; i < Application.supportedLocales.size(); i++) {
					if (Application.supportedLocales.get(i).toString().equals(ttl.toString()))
					{
						cbTooltipLanguage.setSelectedIndex(i+1);
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
			Application.debug(delay);

		} else if (source == cbTooltipLanguage) {
			int index = cbTooltipLanguage.getSelectedIndex() - 1;
			if (index == -1) app.setTooltipLanguage(null);
			else app.setTooltipLanguage(Application.supportedLocales.get(index));
		} else if(source == cbEnableScripting) {
			app.setScriptingDisabled(!cbEnableScripting.isSelected());
		} else if(source == cbUseJavaFonts) {
			app.getDrawEquation().setUseJavaFontsForLaTeX(app, cbUseJavaFonts.isSelected());
		} else if(source == cbReverseMouseWheel) {
			app.reverseMouseWheel(cbReverseMouseWheel.isSelected());
		} else if(source == cbUseLocalDigits) {
			app.setUseLocalizedDigits(cbUseLocalDigits.isSelected());
		} else if(source == cbReturnAngleInverseTrig) {
			app.getKernel().setInverseTrigReturnsAngle(cbReturnAngleInverseTrig.isSelected());
			
			// make sure all calculations fully updated
			//app.getKernel().updateConstruction(); doesn't do what we want
			app.getKernel().getConstruction().getUndoManager().storeUndoInfo(true);

		} else if(source == cbUseLocalLabels) {
			app.setUseLocalizedLabels(cbUseLocalLabels.isSelected());
		} else if(source == cbShowTitleBar) {
			settings.getLayout().setShowTitleBar(cbShowTitleBar.isSelected());
		} else if(source == cbIgnoreDocumentLayout) {
			settings.getLayout().setIgnoreDocumentLayout(cbIgnoreDocumentLayout.isSelected());
		} else if(source == cbAllowStyleBar) {
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
		} else if (source == pointStyleRadio0) {
			app.getEuclidianView().setPointStyle(0);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(0);
		} else if (source == pointStyleRadio1) {
			app.getEuclidianView().setPointStyle(1);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(1);
		} else if (source == pointStyleRadio2) {
			app.getEuclidianView().setPointStyle(2);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(2);
		} else if (source == pointStyleRadio3) {
			app.getEuclidianView().setPointStyle(3);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(3);
		} else if (source == pointStyleRadio4) {
			app.getEuclidianView().setPointStyle(4);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(4);
		} else if (source == pointStyleRadio6) {
			app.getEuclidianView().setPointStyle(6);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(6);
		} else if (source == pointStyleRadio7) {
			app.getEuclidianView().setPointStyle(7);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setPointStyle(7);
		} else if (source == checkboxSizeRadioRegular) {
			app.getEuclidianView().setBooleanSize(13);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setBooleanSize(13);
		} else if (source == checkboxSizeRadioLarge) {
			app.getEuclidianView().setBooleanSize(26);
			if (app.hasEuclidianView2())
				app.getEuclidianView2().setBooleanSize(26);
		} else if (source == rightAngleRadio1) {
			app.getEuclidianView().setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_NONE);
		} else if (source == rightAngleRadio2) {
			app.getEuclidianView().setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE);
		} else if (source == rightAngleRadio3) {
			app.getEuclidianView().setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
		} else if (source == rightAngleRadio4) {
			app.getEuclidianView().setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_L);
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
			if (index == 0) app.setGUIFontSize(-1); // default
			else app.setGUIFontSize(MyXMLHandler.menuFontSizes[index - 1]);
		} else if (source == cbKeyboardLanguage){			
			int index = cbKeyboardLanguage.getSelectedIndex();
			if(index==0)
				settings.getKeyboard().setKeyboardLocale(app.getLocale());
			else
				settings.getKeyboard().setKeyboardLocale(
						KeyboardSettings.supportedLocales.get(index-1));
		}else if (source == cbKeyboardShowAutomatic){						
			settings.getKeyboard().setShowKeyboardOnStart(cbKeyboardShowAutomatic.isSelected());			
		}else if(source == tfKeyboardWidth || source == tfKeyboardHeight){
			changeWidthOrHeight(source);
		}
	}

	/**
	 * Slider changed.
	 */
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == slOpacity) {			
			settings.getKeyboard().setKeyboardOpacity(slOpacity.getValue() / 100.0f);
		}
	}

	/**
	 * Not implemented.
	 */
	public void focusGained(FocusEvent e) {}

	/**
	 * Apply textfield changes.
	 */
	public void focusLost(FocusEvent e) {		
		
		changeWidthOrHeight(e.getSource());
	}

	private void changeWidthOrHeight(Object source) {
		KeyboardSettings kbs = settings.getKeyboard();
		if(source == tfKeyboardHeight) {
			try {
				int windowHeight = Integer.parseInt(tfKeyboardHeight.getText());
				kbs.setKeyboardHeight(windowHeight);
			} catch(NumberFormatException ex) {
				app.showError("InvalidInput", tfKeyboardHeight.getText());
				tfKeyboardHeight.setText(Integer.toString(kbs.getKeyboardHeight()));
			}
		} else if(source == tfKeyboardWidth) {
			try {
				int windowWidth = Integer.parseInt(tfKeyboardWidth.getText());
				kbs.setKeyboardWidth(windowWidth);
			} catch(NumberFormatException ex) {
				app.showError("InvalidInput", tfKeyboardWidth.getText());
				tfKeyboardWidth.setText(Integer.toString(kbs.getKeyboardWidth()));
			}
		}
		
	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		virtualKeyboardPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("VirtualKeyboard")));		
		keyboardLanguageLabel.setText(app.getPlain("VirtualKeyboardLanguage")+":");
		widthLabel.setText(app.getPlain("Width")+":");
		heightLabel.setText(app.getPlain("Height")+":");
		cbKeyboardShowAutomatic.setText(app.getPlain("ShowAutomatically"));
		opacityLabel.setText(app.getMenu("Opacity")+":");

		guiFontsizePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("FontSize")));	
		guiFontSizeLabel.setText(app.getMenu("GUIFontSize")+":");

		tooltipPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Tooltips")));
		tooltipLanguageLabel.setText(app.getPlain("TooltipLanguage")+":");
		tooltipTimeoutLabel.setText(app.getPlain("TooltipTimeout")+":");
		
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Language")));
		cbUseLocalDigits.setText(app.getPlain("LocalizedDigits"));
		cbUseLocalLabels.setText(app.getPlain("LocalizedLabels"));

		angleUnitPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("AngleUnit")));
		angleUnitRadioDegree.setText(app.getMenu("Degree"));
		angleUnitRadioRadian.setText(app.getMenu("Radiant"));

		continuityPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Continuity")));
		continuityRadioOn.setText(app.getMenu("on"));
		continuityRadioOff.setText(app.getMenu("off"));

		checkboxSizePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("CheckboxSize")));
		checkboxSizeRadioRegular.setText(app.getMenu("CheckboxSize.Regular"));
		checkboxSizeRadioLarge.setText(app.getMenu("CheckboxSize.Large"));

		rightAnglePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("RightAngleStyle")));
		rightAngleRadio1.setText(app.getMenu(app.getPlain("off")));
		rightAngleRadio2.setText("\u25a1");
		rightAngleRadio3.setText("\u2219");
		rightAngleRadio4.setText("\u2335");
		rightAngleRadio4.setFont(app.getFontCanDisplay("\u2335"));

		coordinatesPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Coordinates")));
		coordinatesRadio1.setText(app.getMenu("A = (x, y)"));
		coordinatesRadio2.setText(app.getMenu("A(x | y)"));
		coordinatesRadio3.setText(app.getMenu("A: (x, y)"));

		pointStylePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("DefaultPointStyle")));
		pointStyleRadio0.setText(app.getMenu("\u25cf"));
		pointStyleRadio1.setText(app.getMenu("\u2716"));
		pointStyleRadio2.setText(app.getMenu("\u25cb"));
		pointStyleRadio3.setText(app.getMenu("\u271a"));
		pointStyleRadio4.setText(app.getMenu("\u25c6"));
		pointStyleRadio6.setText(app.getMenu("\u25b2"));
		pointStyleRadio7.setText(app.getMenu("\u25bc"));

		perspectivesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Perspectives")));
		cbIgnoreDocumentLayout.setText(app.getPlain("IgnoreDocumentLayout"));
		cbShowTitleBar.setText(app.getPlain("ShowTitleBar"));
		cbAllowStyleBar.setText(app.getPlain("AllowStyleBar"));
		
		miscPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Miscellaneous")));
		cbEnableScripting.setText(app.getPlain("EnableScripting"));
		//cbEnableScripting.setSelected(b)
		cbUseJavaFonts.setText(app.getPlain("UseJavaFontsForLaTeX"));	
		cbUseJavaFonts.setSelected(app.useJavaFontsForLaTeX());
		cbReverseMouseWheel.setText(app.getPlain("ReverseMouseWheel"));	
		cbReverseMouseWheel.setSelected(app.isMouseWheelReversed());
		cbReturnAngleInverseTrig.setText(app.getMenu("ReturnAngleInverseTrig"));
		cbReturnAngleInverseTrig.setSelected(app.getKernel().getInverseTrigReturnsAngle());
		
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
		String[] languages = new String[KeyboardSettings.supportedLocales.size()+1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < KeyboardSettings.supportedLocales.size(); i++) {
			Locale loc = (Locale) KeyboardSettings.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			languages[i+1] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i+1] == null)
				languages[i+1] = loc.getDisplayLanguage(Locale.ENGLISH);
		}
		
		int selectedIndex = cbKeyboardLanguage.getSelectedIndex();
		
		if(selectedIndex == -1) {
			Locale loc = settings.getKeyboard().getKeyboardLocale();
			if(loc == null) {
				selectedIndex = 0;
			} else{
				// look for index in locale list and add 1 to compensate default entry
				selectedIndex = KeyboardSettings.supportedLocales.indexOf(settings.getKeyboard().getKeyboardLocale())+1;
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
		
		//String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
		//		"28 pt", "32 pt" };
		
		String[] fontSizesStr = new String[MyXMLHandler.menuFontSizes.length + 1];
		fontSizesStr[0] = app.getPlain("Default");
		
		for (int i = 0 ; i < MyXMLHandler.menuFontSizes.length ; i++) {
			fontSizesStr[i + 1] = app.getPlain("Apt",MyXMLHandler.menuFontSizes[i]+""); // eg "12 pt"
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
	private void setLabelsTooltipLanguages() {	
		String[] languages = new String[Application.supportedLocales.size()+1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			Locale loc = (Locale) Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();
			
			languages[i+1] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i+1] == null)
				languages[i+1] = loc.getDisplayLanguage(Locale.ENGLISH);
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
		tooltipTimeouts[tooltipTimeouts.length-1] = app.getPlain("off");
		
		int selectedIndex = cbTooltipTimeout.getSelectedIndex();

		// take care that this doesn't fire events by accident 
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setModel(new DefaultComboBoxModel(tooltipTimeouts));
		cbTooltipTimeout.setSelectedIndex(selectedIndex);
		cbTooltipTimeout.addActionListener(this);
	}
}
