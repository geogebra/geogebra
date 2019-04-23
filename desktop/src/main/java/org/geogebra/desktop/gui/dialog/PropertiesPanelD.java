/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimatingModel;
import org.geogebra.common.gui.dialog.options.model.AuxObjectModel;
import org.geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.CenterImageModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel.IDecoAngleListener;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.GeoComboListener;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LayerModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import org.geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import org.geogebra.common.gui.dialog.options.model.RightAngleModel;
import org.geogebra.common.gui.dialog.options.model.SelectionAllowedModel;
import org.geogebra.common.gui.dialog.options.model.ShowConditionModel;
import org.geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import org.geogebra.common.gui.dialog.options.model.ShowObjectModel;
import org.geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import org.geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import org.geogebra.common.gui.dialog.options.model.StartPointModel;
import org.geogebra.common.gui.dialog.options.model.SymbolicModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TooltipModel;
import org.geogebra.common.gui.dialog.options.model.TraceModel;
import org.geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel.IGraphicsViewLocationListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.dialog.options.OptionPanelD;
import org.geogebra.desktop.gui.dialog.options.OptionsObjectD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.GeoGebraComboBoxEditor;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.properties.AnimationSpeedPanel;
import org.geogebra.desktop.gui.properties.AnimationStepPanel;
import org.geogebra.desktop.gui.properties.SliderPanelD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * PropertiesPanel for displaying all gui elements for changing properties of
 * currently selected GeoElements.
 * 
 * @author Markus Hohenwarter
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PropertiesPanelD extends JPanel implements SetLabels, UpdateFonts {
	/** application */
	AppD app;
	/** localization */
	LocalizationD loc;
	/** kernel */
	Kernel kernel;
	/** color choser */
	GeoGebraColorChooser colChooser;

	private static final long serialVersionUID = 1L;
	private NamePanel namePanel;
	private ShowObjectPanel showObjectPanel;
	private SelectionAllowedPanel selectionAllowed;
	private ShowTrimmedIntersectionLines showTrimmedIntersectionLines;
	private ColorPanel colorPanel;
	private LabelPanel labelPanel;
	private TooltipPanel tooltipPanel;
	private LayerPanel layerPanel;
	private CoordsPanel coordPanel;
	private LineEqnPanel lineEqnPanel;
	private PlaneEqnPanel planeEqnPanel;
	private SymbolicPanel symbolicPanel;
	private ConicEqnPanel conicEqnPanel;
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel;
	private TextOptionsPanelD textOptionsPanel;
	private ArcSizePanel arcSizePanel;
	private LineStylePanel lineStylePanel;
	private LineStyleHiddenPanel lineStylePanelHidden;
	// added by Loic BEGIN
	private DecoSegmentPanel decoSegmentPanel;
	private DecoAnglePanel decoAnglePanel;
	private RightAnglePanel rightAnglePanel;
	// END

	/** filling */
	FillingPanelD fillingPanel;
	private FadingPanel fadingPanel;
	private LodPanel lodPanel;
	private CheckBoxInterpolateImage checkBoxInterpolateImage;
	private TracePanel tracePanel;
	private AnimatingPanel animatingPanel;
	private FixPanel fixPanel;
	private IneqPanel ineqStylePanel;
	private CheckBoxFixPanel checkBoxFixPanel;
	private AllowReflexAnglePanel allowReflexAnglePanel;
	private AllowOutlyingIntersectionsPanel allowOutlyingIntersectionsPanel;
	private AuxiliaryObjectPanel auxPanel;
	private AnimationStepPanel animStepPanel;
	private TextfieldSizePanel textFieldSizePanel;
	private AnimationSpeedPanel animSpeedPanel;
	private SliderPanelD sliderPanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private StartPointPanel startPointPanel;
	private CornerPointsPanel cornerPointsPanel;
	private TextEditPanel textEditPanel;
	private ScriptEditPanel scriptEditPanel;
	private BackgroundImagePanel bgImagePanel;
	private AbsoluteScreenLocationPanel absScreenLocPanel;
	private AbsoluteScreenLocationPanel centerImagePanel;
	private ListsAsComboBoxPanel comboBoxPanel;
	// private ShowView2D showView2D;
	private ShowConditionPanel showConditionPanel;
	private ColorFunctionPanel colorFunctionPanel;

	private GraphicsViewLocationPanel graphicsViewLocationPanel;
	private ButtonSizePanel buttonSizePanel;
	// private CoordinateFunctionPanel coordinateFunctionPanel;

	private TabPanel basicTab;
	private TabPanel colorTab;
	private TabPanel styleTab;
	private TabPanel sliderTab;
	private TabPanel textTab;
	private TabPanel positionTab;
	private TabPanel algebraTab;
	private TabPanel scriptTab;
	private TabPanel advancedTab;

	/**
	 * If just panels should be displayed which are used if the user modifies
	 * the default properties of an object type.
	 */
	boolean isDefaults;

	private JTabbedPane tabs;

	/**
	 * @param app
	 *            application
	 * @param colChooser
	 *            color chooser
	 * @param isDefaults
	 *            whether this is for defaults
	 */
	public PropertiesPanelD(AppD app, GeoGebraColorChooser colChooser,
			boolean isDefaults) {
		this.isDefaults = isDefaults;

		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.colChooser = colChooser;

		// load panels which are hidden for the defaults dialog
		if (!isDefaults) {
			namePanel = new NamePanel(app);
			labelPanel = new LabelPanel();
			tooltipPanel = new TooltipPanel();
			layerPanel = new LayerPanel();
			animatingPanel = new AnimatingPanel();
			scriptEditPanel = new ScriptEditPanel();
			textEditPanel = new TextEditPanel(this);
			startPointPanel = new StartPointPanel();
			cornerPointsPanel = new CornerPointsPanel();
			bgImagePanel = new BackgroundImagePanel();
			showConditionPanel = new ShowConditionPanel(app, this);
			colorFunctionPanel = new ColorFunctionPanel(app, this);

			graphicsViewLocationPanel = new GraphicsViewLocationPanel(app,
					this);
		}

		allowReflexAnglePanel = new AllowReflexAnglePanel();

		sliderPanel = new SliderPanelD(app, this, false, true);
		showObjectPanel = new ShowObjectPanel();
		selectionAllowed = new SelectionAllowedPanel();
		showTrimmedIntersectionLines = new ShowTrimmedIntersectionLines();
		colorPanel = new ColorPanel(this, colChooser);
		coordPanel = new CoordsPanel();
		lineEqnPanel = new LineEqnPanel();
		planeEqnPanel = new PlaneEqnPanel();
		conicEqnPanel = new ConicEqnPanel();
		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel();
		ineqStylePanel = new IneqPanel();
		textOptionsPanel = new TextOptionsPanelD(this);
		arcSizePanel = new ArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		lineStylePanel = new LineStylePanel();
		lineStylePanelHidden = new LineStyleHiddenPanel();
		// added by Loic BEGIN
		decoSegmentPanel = new DecoSegmentPanel();
		decoAnglePanel = new DecoAnglePanel();
		rightAnglePanel = new RightAnglePanel();
		// END
		fillingPanel = new FillingPanelD(app);
		fadingPanel = new FadingPanel();
		lodPanel = new LodPanel();
		checkBoxInterpolateImage = new CheckBoxInterpolateImage();
		tracePanel = new TracePanel();
		animatingPanel = new AnimatingPanel();
		fixPanel = new FixPanel();
		checkBoxFixPanel = new CheckBoxFixPanel();
		absScreenLocPanel = new AbsoluteScreenLocationPanel(
				"AbsoluteScreenLocation", new AbsoluteScreenLocationModel(app));
		centerImagePanel = new AbsoluteScreenLocationPanel("CenterImage",
				new CenterImageModel(app));
		comboBoxPanel = new ListsAsComboBoxPanel();
		// showView2D = new ShowView2D();
		auxPanel = new AuxiliaryObjectPanel();
		animStepPanel = new AnimationStepPanel(app);
		symbolicPanel = new SymbolicPanel();
		textFieldSizePanel = new TextfieldSizePanel(app);
		animSpeedPanel = new AnimationSpeedPanel(app);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		buttonSizePanel = new ButtonSizePanel(app, loc);
		// tabbed pane for properties
		tabs = new JTabbedPane();
		initTabs();

		tabs.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				applyModifications();

			}
		});

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	/**
	 * @return label panel
	 */
	public LabelPanel getLabelPanel() {
		return labelPanel;
	}

	/**
	 * apply tabs modifications (text edit panel, etc.)
	 */
	public void applyModifications() {
		if (textEditPanel != null) {
			textEditPanel.applyModifications();
		}
		if (scriptEditPanel != null) {
			scriptEditPanel.applyModifications();
		}
	}

	/**
	 * @param geo
	 *            GeoText to be updated
	 */
	public void updateTextEditor(GeoElement geo) {
		GeoElement[] geos = { geo };
		textEditPanel.updatePanel(geos);
	}

	/**
	 * Focus on slider tab
	 */
	public void showSliderTab() {
		Log.debug("showSliderTab()");
		tabs.setSelectedIndex(1);
	}

	/**
	 * Set min arc size from model
	 */
	public void setSliderMinValue() {
		arcSizePanel.setMinValue();
	}


	/**
	 * A list of the tab panels
	 */
	private ArrayList<TabPanel> tabPanelList;

	/**
	 * Initialize the tabs
	 */
	private void initTabs() {
		tabPanelList = new ArrayList<>();

		// basic tab
		ArrayList<JPanel> basicTabList = new ArrayList<>();

		if (!isDefaults) {
			basicTabList.add(namePanel);
		}

		basicTabList.add(showObjectPanel);

		if (!isDefaults) {
			basicTabList.add(labelPanel);
		}

		basicTabList.add(tracePanel);

		if (!isDefaults) {
			basicTabList.add(animatingPanel);
		}

		basicTabList.add(fixPanel);
		basicTabList.add(auxPanel);
		basicTabList.add(checkBoxFixPanel);

		if (!isDefaults) {
			basicTabList.add(bgImagePanel);
		}

		basicTabList.add(comboBoxPanel);
		// if (!isDefaults)
		basicTabList.add(allowReflexAnglePanel);
		basicTabList.add(rightAnglePanel);
		basicTabList.add(allowOutlyingIntersectionsPanel);
		basicTabList.add(showTrimmedIntersectionLines);

		// basicTabList.add(showView2D);
		basicTab = new TabPanel(basicTabList);
		tabPanelList.add(basicTab);

		// text tab
		ArrayList<JPanel> textTabList = new ArrayList<>();
		textTabList.add(textOptionsPanel);

		if (!isDefaults) {
			textTabList.add(textEditPanel);
			textOptionsPanel.setEditPanel(textEditPanel);
		} else {
			textOptionsPanel.setEditPanel(null);
		}

		textTab = new TabPanel(textTabList);
		tabPanelList.add(textTab);

		// slider tab
		// if(!isDefaults)
		{
			ArrayList<JPanel> sliderTabList = new ArrayList<>();
			sliderTabList.add(sliderPanel);
			sliderTab = new TabPanel(sliderTabList);
			tabPanelList.add(sliderTab);
		}

		// color tab
		ArrayList<JPanel> colorTabList = new ArrayList<>();
		colorTabList.add(colorPanel);
		colorTab = new TabPanel(colorTabList);
		tabPanelList.add(colorTab);

		// style tab
		ArrayList<JPanel> styleTabList = new ArrayList<>();
		styleTabList.add(slopeTriangleSizePanel);
		styleTabList.add(pointSizePanel);
		styleTabList.add(pointStylePanel);
		styleTabList.add(lodPanel);
		styleTabList.add(lineStylePanel);
		styleTabList.add(ineqStylePanel);
		styleTabList.add(arcSizePanel);
		styleTabList.add(buttonSizePanel);
		styleTabList.add(fillingPanel);
		styleTabList.add(fadingPanel);
		styleTabList.add(checkBoxInterpolateImage);
		styleTabList.add(textFieldSizePanel);
		styleTabList.add(decoAnglePanel);
		styleTabList.add(decoSegmentPanel);
		styleTabList.add(lineStylePanelHidden);
		styleTab = new TabPanel(styleTabList);
		tabPanelList.add(styleTab);

		// filling style
		// ArrayList fillingTabList = new ArrayList();
		// fillingTabList.add(fillingPanel);
		// TabPanel fillingTab = new TabPanel(loc.getMenu("Filling"),
		// fillingTabList);
		// fillingTab.addToTabbedPane(tabs);

		// position
		if (!isDefaults) {
			ArrayList<JPanel> positionTabList = new ArrayList<>();

			positionTabList.add(startPointPanel);
			positionTabList.add(cornerPointsPanel);

			positionTabList.add(absScreenLocPanel);
			positionTabList.add(centerImagePanel);

			positionTab = new TabPanel(positionTabList);
			tabPanelList.add(positionTab);

		}

		// algebra tab
		ArrayList<JPanel> algebraTabList = new ArrayList<>();
		algebraTabList.add(coordPanel);
		algebraTabList.add(lineEqnPanel);
		algebraTabList.add(planeEqnPanel);
		algebraTabList.add(conicEqnPanel);
		algebraTabList.add(animStepPanel);
		algebraTabList.add(animSpeedPanel);
		algebraTabList.add(symbolicPanel);
		algebraTab = new TabPanel(algebraTabList);
		tabPanelList.add(algebraTab);

		// advanced tab
		if (!isDefaults) {
			ArrayList<JPanel> advancedTabList = new ArrayList<>();

			advancedTabList.add(showConditionPanel);
			advancedTabList.add(colorFunctionPanel);

			// advancedTabList.add(coordinateFunctionPanel);
			advancedTabList.add(layerPanel);

			advancedTabList.add(tooltipPanel);

			advancedTabList.add(selectionAllowed);

			// =================================================
			// add location panel
			advancedTabList.add(graphicsViewLocationPanel);
			// ===================================================

			advancedTab = new TabPanel(advancedTabList);
			tabPanelList.add(advancedTab);
		}

		// javascript tab
		if (!isDefaults) {
			scriptTab = new TabPanel(scriptEditPanel);
			tabPanelList.add(scriptTab);
		}

		setLabels();
	}

	/**
	 * Update the labels of this panel in case the user language was changed.
	 */
	@Override
	public void setLabels() {

		// update labels of tabs
		// TODO change label for script tab
		basicTab.setTitle(loc.getMenu("Properties.Basic"));
		colorTab.setTitle(loc.getMenu("Color"));
		styleTab.setTitle(loc.getMenu("Properties.Style"));
		textTab.setTitle(loc.getMenu("Text"));
		algebraTab.setTitle(loc.getMenu("Properties.Algebra"));
		sliderTab.setTitle(loc.getMenu("Slider"));

		if (!isDefaults) {
			positionTab.setTitle(loc.getMenu("Properties.Position"));
			scriptTab.setTitle(loc.getMenu("Scripting"));
			advancedTab.setTitle(loc.getMenu("Advanced"));
		}

		// update the labels of the panels
		showObjectPanel.setLabels();
		symbolicPanel.setLabels();
		selectionAllowed.setLabels();
		showTrimmedIntersectionLines.setLabels();
		colChooser.setLabels();
		colorPanel.setLabels();
		coordPanel.setLabels();
		lineEqnPanel.setLabels();
		planeEqnPanel.setLabels();
		conicEqnPanel.setLabels();
		pointSizePanel.setLabels();
		pointStylePanel.setLabels();
		textOptionsPanel.setLabels();
		arcSizePanel.setLabels();
		lineStylePanel.setLabels();
		ineqStylePanel.setLabels();
		lineStylePanelHidden.setLabels();
		decoSegmentPanel.setLabels();
		decoAnglePanel.setLabels();
		rightAnglePanel.setLabels();
		fillingPanel.setLabels();
		fadingPanel.setLabels();
		lodPanel.setLabels();
		checkBoxInterpolateImage.setLabels();
		tracePanel.setLabels();
		fixPanel.setLabels();
		checkBoxFixPanel.setLabels();
		allowOutlyingIntersectionsPanel.setLabels();
		auxPanel.setLabels();
		animStepPanel.setLabels();
		animSpeedPanel.setLabels();
		slopeTriangleSizePanel.setLabels();
		absScreenLocPanel.setLabels();
		centerImagePanel.setLabels();
		comboBoxPanel.setLabels();
		// showView2D.setLabels();
		sliderPanel.setLabels();
		buttonSizePanel.setLabels();
		if (!isDefaults) {
			namePanel.setLabels();
			labelPanel.setLabels();
			tooltipPanel.setLabels();
			layerPanel.setLabels();
			animatingPanel.setLabels();
			scriptEditPanel.setLabels();
			textEditPanel.setLabels();
			startPointPanel.setLabels();
			cornerPointsPanel.setLabels();
			bgImagePanel.setLabels();
			showConditionPanel.setLabels();
			colorFunctionPanel.setLabels();
			graphicsViewLocationPanel.setLabels();
		}

		allowReflexAnglePanel.setLabels();

		// remember selected tab
		Component selectedTab = tabs.getSelectedComponent();

		// update tab labels
		tabs.removeAll();
		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			tp.addToTabbedPane(tabs);
		}

		// switch back to previously selected tab
		if (tabs.getTabCount() > 0) {
			int index = tabs.indexOfComponent(selectedTab);
			tabs.setSelectedIndex(Math.max(0, index));
			// tabs.setVisible(true);
		} else {
			// tabs.setVisible(false);
		}
	}

	@Override
	public void updateFonts() {

		Font font = app.getPlainFont();

		tabs.setFont(font);

		// update the labels of the panels
		showObjectPanel.updateFonts();
		selectionAllowed.updateFonts();
		showTrimmedIntersectionLines.updateFonts();
		colorPanel.updateFonts();
		colChooser.updateFonts();
		coordPanel.updateFonts();
		lineEqnPanel.updateFonts();
		planeEqnPanel.updateFonts();
		conicEqnPanel.updateFonts();
		pointSizePanel.updateFonts();
		pointStylePanel.updateFonts();
		textOptionsPanel.updateFonts();
		arcSizePanel.updateFonts();
		lineStylePanel.updateFonts();
		ineqStylePanel.updateFonts();
		lineStylePanelHidden.updateFonts();
		decoSegmentPanel.updateFonts();
		decoAnglePanel.updateFonts();
		rightAnglePanel.updateFonts();
		fillingPanel.updateFonts();
		fadingPanel.updateFonts();
		lodPanel.updateFonts();
		checkBoxInterpolateImage.updateFonts();
		tracePanel.updateFonts();
		fixPanel.updateFonts();
		checkBoxFixPanel.updateFonts();
		allowOutlyingIntersectionsPanel.updateFonts();
		auxPanel.updateFonts();
		animStepPanel.updateFonts();
		animSpeedPanel.updateFonts();
		slopeTriangleSizePanel.updateFonts();
		centerImagePanel.updateFonts();
		absScreenLocPanel.updateFonts();
		comboBoxPanel.updateFonts();
		// showView2D.updateFonts();
		sliderPanel.updateFonts();
		buttonSizePanel.updateFonts();
		if (!isDefaults) {
			namePanel.updateFonts();
			labelPanel.updateFonts();
			tooltipPanel.updateFonts();
			layerPanel.updateFonts();
			animatingPanel.updateFonts();
			scriptEditPanel.updateFonts();
			textEditPanel.updateFonts();
			startPointPanel.updateFonts();
			cornerPointsPanel.updateFonts();
			bgImagePanel.updateFonts();
			showConditionPanel.updateFonts();
			colorFunctionPanel.updateFonts();
			graphicsViewLocationPanel.updateFonts();
		}

		allowReflexAnglePanel.updateFonts();

	}

	/**
	 * Update all tabs after new GeoElements were selected.
	 * 
	 * @param geos
	 */
	private void updateTabs(Object[] geos) {
		if (geos.length == 0) {
			tabs.setVisible(false);
			return;
		}

		// remember selected tab
		Component selectedTab = tabs.getSelectedComponent();

		tabs.removeAll();
		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			tp.update(geos);
			tp.addToTabbedPane(tabs);
		}

		// switch back to previously selected tab
		if (tabs.getTabCount() > 0) {
			int index = tabs.indexOfComponent(selectedTab);
			tabs.setSelectedIndex(Math.max(0, index));
			tabs.setVisible(true);
		} else {
			tabs.setVisible(false);
		}
	}

	/**
	 * @param tabList
	 *            tabs
	 * @param geos
	 *            selected geo
	 * @return whether at least one panel on tab is visible
	 */
	static boolean updateTabPanel(ArrayList<JPanel> tabList,
			Object[] geos) {
		// update all panels and their visibility
		boolean oneVisible = false;
		int size = tabList.size();
		for (int i = 0; i < size; i++) {
			UpdateablePropertiesPanel up = (UpdateablePropertiesPanel) tabList
					.get(i);
			boolean show = (up.updatePanel(geos) != null);
			up.setVisible(show);
			if (show) {
				oneVisible = true;
			}
		}

		return oneVisible;
	}

	/**
	 * 
	 * @param geos
	 *            selected geos
	 */
	public void updateSelection(Object[] geos) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		updateTabs(geos);
	}

	/**
	 * @param geo
	 *            geo
	 */
	public void updateVisualStyle(GeoElement geo) {

		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			if (tp != null) {
				tp.updateVisualStyle(geo);
			}
		}

	}

	/**
	 * Update just definition of one geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateOneGeoDefinition(GeoElement geo) {
		namePanel.updateDef(geo);
	}

	/**
	 * Update just name of one geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateOneGeoName(GeoElement geo) {
		namePanel.updateName(geo);
	}

	private abstract class OptionPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected OptionPanel() {
			// make this protected
		}

	}

	private class CheckboxPanel extends OptionPanel
			implements IBooleanOptionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BooleanOptionModel model;
		private JCheckBox checkbox;
		private String title;

		public CheckboxPanel(final String title) {
			super();
			this.title = title;
			checkbox = new JCheckBox();
			checkbox.addItemListener(this);
			add(checkbox);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			checkbox.removeItemListener(this);

			model.updateProperties();
			// set object visible checkbox

			checkbox.addItemListener(this);
			return this;
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			checkbox.setFont(font);

		}

		@Override
		public void setLabels() {
			checkbox.setText(loc.getMenu(title));
			app.setComponentOrientation(this);

		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			if (source == checkbox) {
				apply(checkbox.isSelected());
			}
		}

		public void apply(boolean value) {
			model.applyChanges(value);
			updateSelection(model.getGeos());

		}

		@Override
		public void updateCheckbox(boolean value) {
			checkbox.setSelected(value);
		}

		public void setModel(BooleanOptionModel model) {
			this.model = model;
		}

		public JCheckBox getCheckbox() {
			return checkbox;
		}
	}

	private class ComboPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel,
			GeoComboListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel label;
		protected JComboBox comboBox;
		private MultipleOptionsModel model;
		private String title;

		public ComboPanel(final String title) {
			this.setTitle(title);
			label = new JLabel();
			comboBox = new JComboBox();

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(comboBox);
		}

		@Override
		public void setLabels() {
			label.setText(loc.getMenu(getTitle()) + ":");

			int selectedIndex = comboBox.getSelectedIndex();
			comboBox.removeActionListener(this);

			comboBox.removeAllItems();
			getModel().fillModes(loc);
			if (selectedIndex < comboBox.getItemCount()) {
				comboBox.setSelectedIndex(selectedIndex);
			}
			comboBox.addActionListener(this);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			comboBox.removeActionListener(this);

			getModel().updateProperties();

			comboBox.addActionListener(this);
			return this;
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
			comboBox.setFont(font);
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub
		}

		@Override
		public void setSelectedIndex(int index) {
			comboBox.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			comboBox.addItem(item);
		}

		/**
		 * action listener implementation for label mode combobox
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == comboBox) {
				model.applyChanges(comboBox.getSelectedIndex());
			}
		}

		public JLabel getLabel() {
			return label;
		}

		public MultipleOptionsModel getModel() {
			return model;
		}

		public void setModel(MultipleOptionsModel model) {
			this.model = model;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public void setSelectedItem(String item) {
			comboBox.setSelectedItem(item);
		}

		@Override
		public void clearItems() {
			comboBox.removeAllItems();
		}

		@Override
		public void addItem(GeoElement geo) {
			if (geo != null) {
				addItem(geo.getLabel(StringTemplate.editTemplate));
			} else {
				addItem("");
			}
		}

	}

	private static class TabPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private String title;
		private ArrayList<JPanel> panelList;
		private boolean makeVisible = true;

		public TabPanel(ArrayList<JPanel> pVec) {
			panelList = pVec;

			setLayout(new BorderLayout());

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			panel.setLayout(new FullWidthLayout());

			for (int i = 0; i < pVec.size(); i++) {
				panel.add(pVec.get(i));
			}

			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			add(scrollPane, BorderLayout.CENTER);
		}

		public TabPanel(JPanel panel) {
			panelList = new ArrayList<>();
			panelList.add(panel);

			setLayout(new BorderLayout());
			/*
			 * JPanel panel = new JPanel();
			 * panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			 * 
			 * panel.setLayout(new FullWidthLayout());
			 * 
			 * for (int i = 0; i < pVec.size(); i++) { panel.add(pVec.get(i)); }
			 * 
			 * JScrollPane scrollPane = new JScrollPane(panel);
			 * scrollPane.setBorder(BorderFactory.createEmptyBorder());
			 */
			add(panel, BorderLayout.CENTER);
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void update(Object[] geos) {
			makeVisible = updateTabPanel(panelList, geos);
		}

		public void updateVisualStyle(GeoElement geo) {

			for (int i = 0; i < panelList.size(); i++) {
				UpdateablePropertiesPanel up = (UpdateablePropertiesPanel) panelList
						.get(i);
				up.updateVisualStyle(geo);
			}

		}

		public void addToTabbedPane(JTabbedPane tabs) {
			if (makeVisible) {
				tabs.addTab(title, this);
			}
		}
	}

	/**
	 * panel with show/hide object checkbox
	 */
	private class ShowObjectPanel extends CheckboxPanel
			implements IShowObjectListener {

		private static final long serialVersionUID = 1L;

		public ShowObjectPanel() {
			super("ShowObject");
			setModel(new ShowObjectModel(this, app));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

		@Override
		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setSelected(value);
			getCheckbox().setEnabled(isEnabled);
		}

	}

	private class SelectionAllowedPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;

		public SelectionAllowedPanel() {
			super("SelectionAllowed");
			setModel(new SelectionAllowedModel(this, app));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

	}

	/**
	 * panel with show/hide trimmed intersection lines
	 */
	private class ShowTrimmedIntersectionLines extends CheckboxPanel {

		private static final long serialVersionUID = 1L;

		public ShowTrimmedIntersectionLines() {
			super("ShowTrimmed");
			setModel(new TrimmedIntersectionLinesModel(this, app));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

	} // ShowTrimmedIntersectionLines

	/**
	 * panel to fix checkbox (boolean object)
	 */
	private class CheckBoxFixPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;

		public CheckBoxFixPanel() {
			super("FixCheckbox");
			setModel(new FixCheckboxModel(this, app));
			app.setFlowLayoutOrientation(this);
		}

	} // CheckBoxFixPanel

	private class IneqPanel extends CheckboxPanel
			implements IIneqStyleListener {

		private static final long serialVersionUID = 1L;

		public IneqPanel() {
			super("ShowOnXAxis");
			IneqStyleModel model = new IneqStyleModel(app);
			model.setListener(this);
			setModel(model);
			app.setFlowLayoutOrientation(this);
		}

		@Override
		public void enableFilling(boolean value) {
			fillingPanel.setAllEnabled(value);
		}

		@Override
		public void apply(boolean value) {
			super.apply(value);
			enableFilling(value);
		}

	}

	private class SymbolicPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;

		public SymbolicPanel() {
			super("Symbolic");
			SymbolicModel model = new SymbolicModel(app);
			model.setListener(this);
			setModel(model);
			app.setFlowLayoutOrientation(this);
		}

		@Override
		public void apply(boolean value) {
			super.apply(value);
		}

	}// IneqPanel

	/**
	 * panel with label properties
	 */
	public class LabelPanel extends JPanel
			implements ItemListener, ActionListener, UpdateablePropertiesPanel,
			SetLabels, UpdateFonts, IShowLabelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox showLabelCB;
		private JComboBox labelModeCB;
		private ShowLabelModel model;
		private boolean ignoreEvents;

		/**
		 * new label panel
		 */
		protected LabelPanel() {
			super();
			model = new ShowLabelModel(app, this);
			// check boxes for show object, show label
			showLabelCB = new JCheckBox();
			showLabelCB.addItemListener(this);

			// combo box for label mode: name or algebra
			labelModeCB = new JComboBox();
			labelModeCB.addActionListener(this);

			// labelPanel with show checkbox
			app.setFlowLayoutOrientation(this);
			add(showLabelCB);
			add(labelModeCB);
		}

		@Override
		public void setLabels() {
			showLabelCB.setText(loc.getMenu("ShowLabel") + ":");

			int selectedIndex = labelModeCB.getSelectedIndex();
			labelModeCB.removeActionListener(this);

			labelModeCB.removeAllItems();
			labelModeCB.addItem(loc.getMenu("Name")); // index 0
			labelModeCB.addItem(loc.getMenu("NameAndValue")); // index 1
			labelModeCB.addItem(loc.getMenu("Value")); // index 2
			labelModeCB.addItem(loc.getMenu("Caption")); // index 3
			labelModeCB.addItem(loc.getMenu("CaptionAndValue")); // index 9

			labelModeCB.setSelectedIndex(selectedIndex);
			labelModeCB.addActionListener(this);

			// change "Show Label:" to "Show Label" if there's no menu
			updateShowLabel();

			app.setComponentOrientation(this);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			if (model.getGeos() == null) {
				return;
			}
			update();
		}

		/**
		 * Updates properties without firing listeners
		 * 
		 * @return this
		 */
		public JPanel update() {
			if (!model.checkGeos()) {
				return null;
			}

			showLabelCB.removeItemListener(this);
			labelModeCB.removeActionListener(this);

			model.updateProperties();

			showLabelCB.addItemListener(this);
			labelModeCB.addActionListener(this);

			return this;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show label value changed
			if (source == showLabelCB) {
				model.applyShowChanges(showLabelCB.isSelected());
			}
		}

		/**
		 * action listener implementation for label mode combobox
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ignoreEvents) {
				return;
			}
			Object source = e.getSource();
			if (source == labelModeCB) {
				showLabelCB.setSelected(true);
				model.applyModeChanges(
						model.fromDropdown(labelModeCB.getSelectedIndex()),
						true);
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			showLabelCB.setFont(font);
			labelModeCB.setFont(font);

		}

		private void updateShowLabel() {
			if (!model.isNameValueShown()) {
				showLabelCB.setText(loc.getMenu("ShowLabel"));
			} else {
				showLabelCB.setText(loc.getMenu("ShowLabel") + ":");
			}

		}

		@Override
		public void update(boolean isEqualVal, boolean isEqualMode, int mode) {
			// change "Show Label:" to "Show Label" if there's no menu
			updateShowLabel();

			GeoElement geo0 = model.getGeoAt(0);
			// set label visible checkbox
			if (isEqualVal) {
				showLabelCB.setSelected(geo0.isLabelVisible());
			} else {
				showLabelCB.setSelected(false);
			}
			ignoreEvents = true;
			// set label visible checkbox
			if (isEqualMode) {
				labelModeCB
						.setSelectedIndex(
								ShowLabelModel.getDropdownIndex(geo0));
			} else {
				labelModeCB.setSelectedItem(null);
			}
			ignoreEvents = false;
			// locus in selection
			labelModeCB.setVisible(model.isNameValueShown());

		}

	} // LabelPanel

	private class TooltipPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public TooltipPanel() {
			super("Tooltip");
			TooltipModel model = new TooltipModel(app);
			model.setListener(this);
			setModel(model);
		}
	} // TooltipPanel

	private class LayerPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public LayerPanel() {
			super("Layer");
			LayerModel model = new LayerModel(app);
			model.setListener(this);
			setModel(model);
		}
	} // TooltipPanel

	/**
	 * panel for trace
	 * 
	 * @author Markus Hohenwarter
	 */
	private class TracePanel extends CheckboxPanel {
		private static final long serialVersionUID = 1L;

		public TracePanel() {
			super("ShowTrace");
			setModel(new TraceModel(this, app));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

	}

	/**
	 * panel for trace
	 * 
	 * @author adapted from TracePanel
	 */
	private class AnimatingPanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AnimatingPanel() {
			super("Animating");
			setModel(new AnimatingModel(app, this));
			app.setFlowLayoutOrientation(this);
		}

	}

	/**
	 * panel to say if an image is to be interpolated
	 */
	private class CheckBoxInterpolateImage extends CheckboxPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CheckBoxInterpolateImage() {
			super("Interpolate");
			InterpolateImageModel model = new InterpolateImageModel(app);
			model.setListener(this);
			setModel(model);
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

	}

	/**
	 * panel for fixing an object
	 * 
	 * @author Markus Hohenwarter
	 */
	private class FixPanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FixPanel() {
			super("FixObject");
			setModel(new FixObjectModel(this, app));
			app.setFlowLayoutOrientation(this);
		}
	}

	/**
	 * panel to set object's absoluteScreenLocation flag
	 * 
	 * @author Markus Hohenwarter
	 */

	private class AbsoluteScreenLocationPanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AbsoluteScreenLocationPanel(String key,
				BooleanOptionModel model) {
			super(key);
			model.setListener(this);
			setModel(model);
			app.setFlowLayoutOrientation(this);
		}

	}

	/**
	 * panel to set whether GeoLists are drawn as ComboBoxes
	 * 
	 * @author Michael
	 */
	private class ListsAsComboBoxPanel extends CheckboxPanel
			implements IListAsComboListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */

		public ListsAsComboBoxPanel() {
			super("DrawAsDropDownList");
			setModel(new ListAsComboModel(app, this));
			app.setFlowLayoutOrientation(this);
		}

		@Override
		public void drawListAsComboBox(GeoList geo, boolean value) {

			if (geo.getViewSet() == null) {
				app.getEuclidianView1().drawListAsComboBox(geo, value);
				return;
			}
			Iterator<Integer> it = geo.getViewSet().iterator();

			// #3929
			while (it.hasNext()) {
				Integer view = it.next();
				if (view.intValue() == App.VIEW_EUCLIDIAN) {
					app.getEuclidianView1().drawListAsComboBox(geo, value);
				} else if (view.intValue() == App.VIEW_EUCLIDIAN2
						&& app.hasEuclidianView2(1)) {
					app.getEuclidianView2(1).drawListAsComboBox(geo, value);
				}

			}
		}
	}

	/**
	 * panel for angles to set whether reflex angles are allowed
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AllowReflexAnglePanel extends JPanel
			implements ActionListener, SetLabels, UpdateFonts,
			UpdateablePropertiesPanel, IReflexAngleListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel intervalLabel;
		private JComboBox intervalCombo;
		private ReflexAngleModel model;

		public AllowReflexAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new ReflexAngleModel(app, isDefaults);
			model.setListener(this);

			intervalLabel = new JLabel();
			intervalCombo = new JComboBox();

			add(intervalLabel);
			add(intervalCombo);

		}

		@Override
		public void setLabels() {
			intervalLabel.setText(loc.getMenu("AngleBetween"));

			intervalCombo.removeActionListener(this);
			setComboLabels();
			intervalCombo.addActionListener(this);
		}

		@Override
		public void setComboLabels() {
			int idx = intervalCombo.getSelectedIndex();
			intervalCombo.removeAllItems();

			model.fillModes(loc);
			intervalCombo.setSelectedIndex(idx);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			intervalCombo.removeActionListener(this);
			model.updateProperties();
			intervalCombo.addActionListener(this);
			return this;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == intervalCombo) {
				model.applyChanges(getIndex());
			}
		}

		private int getIndex() {
			if (model.hasOrientation()) {
				return intervalCombo.getSelectedIndex();
			}

			// first interval disabled
			return intervalCombo.getSelectedIndex() + 1;
		}

		@Override
		public void setSelectedIndex(int index) {
			if (model.hasOrientation()) {

				if (index >= intervalCombo.getItemCount()) {
					intervalCombo.setSelectedIndex(0);
				} else {
					intervalCombo.setSelectedIndex(index);
				}
			} else {
				// first interval disabled
				intervalCombo.setSelectedIndex(index - 1);
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			intervalLabel.setFont(font);
			intervalCombo.setFont(font);
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addItem(String item) {
			intervalCombo.addItem(item);
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * panel for limted paths to set whether outlying intersection points are
	 * allowed
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AllowOutlyingIntersectionsPanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AllowOutlyingIntersectionsPanel() {
			super("allowOutlyingIntersections");
			setModel(new OutlyingIntersectionsModel(this, app));
			app.setFlowLayoutOrientation(this);

			// super(new FlowLayout(FlowLayout.LEFT));

		}
	}

	/**
	 * panel to set a background image (only one checkbox)
	 * 
	 * @author Markus Hohenwarter
	 */
	private class BackgroundImagePanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BackgroundImagePanel() {
			super("BackgroundImage");
			setModel(new BackgroundImageModel(this, app));
			app.setFlowLayoutOrientation(this);
		}
	}

	/**
	 * panel for making an object auxiliary
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AuxiliaryObjectPanel extends CheckboxPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AuxiliaryObjectPanel() {
			super("AuxiliaryObject");
			setModel(new AuxObjectModel(this, app));
			app.setFlowLayoutOrientation(this);
		}

	}

	/**
	 * panel for location of vectors and text
	 */
	private class StartPointPanel extends JPanel
			implements ActionListener, FocusListener, SetLabels, UpdateFonts,
			UpdateablePropertiesPanel, IComboListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private StartPointModel model;
		private JLabel label;
		private JComboBox cbLocation;
		private DefaultComboBoxModel cbModel;

		public StartPointPanel() {
			// textfield for animation step
			model = new StartPointModel(app);
			model.setListener(this);
			label = new JLabel();
			cbLocation = new JComboBox();
			cbLocation.setEditable(true);
			cbModel = new DefaultComboBoxModel();
			cbLocation.setModel(cbModel);
			label.setLabelFor(cbLocation);
			cbLocation.addActionListener(this);
			cbLocation.addFocusListener(this);
			cbLocation.setEditor(new GeoGebraComboBoxEditor(app, 10));
			// put it all together
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(cbLocation);
		}

		@Override
		public void setLabels() {
			label.setText(loc.getMenu("StartingPoint") + ": ");
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			cbLocation.removeActionListener(this);

			// repopulate model with names of points from the geoList's model
			// take all points from construction
			// TreeSet points =
			// kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != cbModel.getSize() - 1) {
				cbModel.removeAllElements();
				// cbModel.addElement(null);
				model.fillModes(loc);
			}
			model.updateProperties();
			cbLocation.addActionListener(this);
			return this;
		}

		/**
		 * handle textfield changes
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbLocation) {
				doActionPerformed();
			}
		}

		private void doActionPerformed() {
			String strLoc = (String) cbLocation.getSelectedItem();
			model.applyChanges(strLoc, app.getDefaultErrorHandler());
			updateSelection(model.getGeos());
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// nothing to do
		}

		@Override
		public void focusLost(FocusEvent e) {
			doActionPerformed();
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSelectedIndex(int index) {
			if (index == 0) {
				GeoElement p = (GeoElement) model.getLocateableAt(0)
						.getStartPoint();
				cbLocation.setSelectedItem(
						p.getLabel(StringTemplate.editTemplate));
			} else {
				cbLocation.setSelectedItem(null);
			}
		}

		@Override
		public void addItem(String item) {
			cbModel.addElement(item);

		}

		// @Override
		// public void setSelectedItem(String item) {
		// cbLocation.setSelectedItem(item);
		// }

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub

		}
	}

	private class ImageCornerPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;
		private ImageCornerModel model;

		public ImageCornerPanel(int cornerIdx) {
			super("CornerModel");
			model = new ImageCornerModel(app);
			model.setListener(this);
			model.setCornerIdx(cornerIdx);
			setModel(model);
			comboBox.setEditable(true);
			ImageResourceD res = cornerIcon(cornerIdx);
			if (res != null) {
				getLabel().setIcon(app.getScaledIcon(res));
			}
		}

		ImageResourceD cornerIcon(int idx) {
			switch (idx) {
			case 0:
				return GuiResourcesD.CORNER1;
			case 1:
				return GuiResourcesD.CORNER2;
			case 2:
				return GuiResourcesD.CORNER4;
			}
			return null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == comboBox) {
				model.applyChanges((String) comboBox.getSelectedItem(),
						app.getDefaultErrorHandler());
			}
		}

		@Override
		public void setLabels() {
			super.setLabels();

			if (model.isCenter()) {
				getLabel().setText(loc.getCommand("Center") + ":");
			} else {
				String strLabelStart = loc.getMenu("CornerPoint");
				getLabel().setText(
						strLabelStart + " " + model.getCornerNumber() + ":");
			}
		}

	}

	private class CornerPointsPanel extends JPanel
			implements UpdateablePropertiesPanel, SetLabels, UpdateFonts /**
																			* 
																			*/
	{
		private static final long serialVersionUID = 1L;

		private ImageCornerPanel corner0;
		private ImageCornerPanel corner1;
		private ImageCornerPanel corner2;
		private ImageCornerPanel center;

		public CornerPointsPanel() {
			corner0 = new ImageCornerPanel(0);
			corner1 = new ImageCornerPanel(1);
			corner2 = new ImageCornerPanel(2);
			center = new ImageCornerPanel(GeoImage.CENTER_INDEX);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(corner0);
			add(corner1);
			add(corner2);
			if (center != null) {
				add(center);
			}
		}

		@Override
		public void updateFonts() {
			corner0.updateFonts();
			corner1.updateFonts();
			corner2.updateFonts();
			if (center != null) {
				center.updateFonts();
			}
		}

		@Override
		public void setLabels() {
			corner0.setLabels();
			corner1.setLabels();
			corner2.setLabels();
			if (center != null) {
				center.setLabels();
			}
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			if (geos == null) {
				return null;
			}
			if (center != null && center.updatePanel(geos) != null) {
				showCenter(true);
				return this;
			}
			if (corner0.updatePanel(geos) == null) {
				return null;
			}
			showCenter(false);
			corner1.updatePanel(geos);
			corner2.updatePanel(geos);
			return this;
		}

		private void showCenter(boolean b) {
			corner0.setVisible(!b);
			corner1.setVisible(!b);
			corner2.setVisible(!b);
			if (center != null) {
				center.setVisible(b);
			}
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel for script editing
	 */
	private class ScriptEditPanel extends JPanel implements ActionListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ScriptInputDialog clickDialog, updateDialog, globalDialog;
		private JTabbedPane tabbedPane;
		private JPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;

		public ScriptEditPanel() {
			super(new BorderLayout());

			int row = 35;
			int column = 15;

			tabbedPane = new JTabbedPane();

			clickDialog = new ScriptInputDialog(app, loc.getMenu("Script"),
					null, row, column, false, false);
			updateDialog = new ScriptInputDialog(app, loc.getMenu("JavaScript"),
					null, row, column, true, false);
			globalDialog = new ScriptInputDialog(app,
					loc.getMenu("GlobalJavaScript"), null, row, column, false,
					true);
			setLayout(new BorderLayout());
			// add(td.getInputPanel(), BorderLayout.NORTH);
			// add(td2.getInputPanel(), BorderLayout.CENTER);
			clickScriptPanel = new JPanel(new BorderLayout(0, 0));
			clickScriptPanel.add(clickDialog.getInputPanel(row, column, true),
					BorderLayout.CENTER);
			clickScriptPanel.add(clickDialog.getButtonPanel(),
					BorderLayout.SOUTH);

			updateScriptPanel = new JPanel(new BorderLayout(0, 0));
			updateScriptPanel.add(updateDialog.getInputPanel(row, column, true),
					BorderLayout.CENTER);
			updateScriptPanel.add(updateDialog.getButtonPanel(),
					BorderLayout.SOUTH);

			globalScriptPanel = new JPanel(new BorderLayout(0, 0));
			globalScriptPanel.add(globalDialog.getInputPanel(row, column, true),
					BorderLayout.CENTER);
			globalScriptPanel.add(globalDialog.getButtonPanel(),
					BorderLayout.SOUTH);

			add(tabbedPane, BorderLayout.CENTER);

			tabbedPane.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					applyModifications();

				}
			});

		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			clickDialog.applyModifications();
			updateDialog.applyModifications();
			globalDialog.applyModifications();
		}

		@Override
		public void setLabels() {
			// setBorder(BorderFactory.createTitledBorder(loc.getMenu("JavaScript")));
			clickDialog.setLabels(loc.getMenu("OnClick"));
			updateDialog.setLabels(loc.getMenu("OnUpdate"));
			globalDialog.setLabels(loc.getMenu("GlobalJavaScript"));
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos)) {
				return null;
			}

			// remember selected tab
			Component selectedTab = tabbedPane.getSelectedComponent();

			GeoElement button = (GeoElement) geos[0];
			clickDialog.setGeo(button);
			updateDialog.setGeo(button);
			globalDialog.setGlobal();
			tabbedPane.removeAll();
			if (button.canHaveClickScript()) {
				tabbedPane.addTab(loc.getMenu("OnClick"), clickScriptPanel);
			}
			if (button.canHaveUpdateScript()) {
				tabbedPane.addTab(loc.getMenu("OnUpdate"), updateScriptPanel);
			}
			tabbedPane.addTab(loc.getMenu("GlobalJavaScript"),
					globalScriptPanel);

			// select tab as before
			tabbedPane.setSelectedIndex(
					Math.max(0, tabbedPane.indexOfComponent(selectedTab)));

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			// return geos.length == 1 && geos[0] instanceof
			// GeoJavaScriptButton;
			return geos.length == 1;
		}

		/**
		 * handle textfield changes
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// if (e.getSource() == btEdit)
			// app.showTextDialog((GeoText) geos[0]);
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			tabbedPane.setFont(font);
			clickScriptPanel.setFont(font);
			updateScriptPanel.setFont(font);
			globalScriptPanel.setFont(font);

			clickDialog.updateFonts();
			updateDialog.updateFonts();
			globalDialog.updateFonts();

		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the kind of coordinates (cartesian or polar) for GeoPoint
	 * and GeoVector
	 * 
	 * @author Markus Hohenwarter
	 */

	private class CoordsPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public CoordsPanel() {
			super("Coordinates");
			CoordsModel model = new CoordsModel(app);
			model.setListener(this);
			setModel(model);
		}
	} // CoordsPanel

	private class LineEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public LineEqnPanel() {
			super("Equation");
			LineEqnModel model = new LineEqnModel(app);
			model.setListener(this);
			setModel(model);
		}
	}

	private class PlaneEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public PlaneEqnPanel() {
			super("Equation");
			PlaneEqnModel model = new PlaneEqnModel(app);
			model.setListener(this);
			setModel(model);
		}
	}

	private class ConicEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public ConicEqnPanel() {
			super("Equation");
			ConicEqnModel model = new ConicEqnModel(app);
			model.setListener(this);
			setModel(model);
		}

		@Override
		public void setLabels() {
			getLabel().setText(loc.getMenu(getTitle()));
			if (getModel().hasGeos() && getModel().checkGeos()) {
				int selectedIndex = comboBox.getSelectedIndex();
				comboBox.removeActionListener(this);

				comboBox.removeAllItems();
				getModel().updateProperties();
				comboBox.setSelectedIndex(selectedIndex);
				comboBox.addActionListener(this);
			}
		}

	} // ConicEqnPanel

	/**
	 * panel to select the size of a GeoPoint
	 * 
	 * @author Markus Hohenwarter
	 */
	private class PointSizePanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, ISliderListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PointSizeModel model;
		private JSlider slider;

		public PointSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// setBorder(BorderFactory.createTitledBorder(loc.getMenu("Size")));
			// JLabel sizeLabel = new JLabel(loc.getMenu("Size") + ":");

			model = new PointSizeModel(app);
			model.setListener(this);

			slider = new JSlider(1, EuclidianStyleConstants.MAX_POINT_SIZE);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			slider.addChangeListener(this);

			add(slider);
		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(
					app.getLocalization().getMenu("PointSize")));
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			if (!model.hasGeos()) {
				return;
			}
			update();
		}

		public JPanel update() {
			// check geos
			if (!model.checkGeos()) {
				return null;
			}

			slider.removeChangeListener(this);
			model.updateProperties();
			slider.addChangeListener(this);
			return this;
		}

		/**
		 * change listener implementation for slider
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		private void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());
		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);
		}

	}

	/**
	 * panel to change the point style
	 * 
	 * @author Florian Sonner
	 * @version 2008-07-17
	 */
	private class PointStylePanel extends JPanel
			implements UpdateablePropertiesPanel, SetLabels, UpdateFonts,
			ActionListener, IComboListener {
		private static final long serialVersionUID = 1L;
		private PointStyleModel model;
		private JComboBox cbStyle; // G.Sturr 2010-1-24

		public PointStylePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			model = new PointStyleModel(app);
			model.setListener(this);
			// G.STURR 2010-1-24
			// Point styles were previously displayed with fonts,
			// but not all point styles had font equivalents. This is
			// now replaced by a comboBox holding rendered point styles
			// and radio buttons to select default or custom point style.

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18, 18));
			cbStyle = new JComboBox(EuclidianView.getPointStyles());
			cbStyle.setRenderer(renderer);
			cbStyle.setMaximumRowCount(
					EuclidianStyleConstants.MAX_POINT_STYLE + 1);
			cbStyle.setBackground(getBackground());
			cbStyle.addActionListener(this);
			add(cbStyle);

			/*
			 * ----- old code ButtonGroup buttonGroup = new ButtonGroup();
			 * 
			 * String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716" };
			 * String[] strPointStyleAC = { "0", "2", "1" }; buttons = new
			 * JRadioButton[strPointStyle.length];
			 * 
			 * for(int i = 0; i < strPointStyle.length; ++i) { buttons[i] = new
			 * JRadioButton(strPointStyle[i]);
			 * buttons[i].setActionCommand(strPointStyleAC[i]);
			 * buttons[i].addActionListener(this);
			 * buttons[i].setFont(app.getSmallFont());
			 * 
			 * if(!strPointStyleAC[i].equals("-1"))
			 * buttons[i].setFont(app.getSmallFont());
			 * 
			 * buttonGroup.add(buttons[i]); add(buttons[i]); }
			 */

			// END G.STURR

		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory
					.createTitledBorder(loc.getMenu("PointStyle")));
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			if (!model.hasGeos()) {
				return;
			}
			update();
		}

		public JPanel update() {
			// check geos
			if (!model.checkGeos()) {
				return null;
			}

			// G.STURR 2010-1-24:
			// update comboBox and radio buttons
			cbStyle.removeActionListener(this);

			model.updateProperties();

			cbStyle.addActionListener(this);

			/*
			 * ----- old code to update radio button group for(int i = 0; i <
			 * buttons.length; ++i) {
			 * if(Integer.parseInt(buttons[i].getActionCommand()) ==
			 * geo0.getPointStyle()) buttons[i].setSelected(true); else
			 * buttons[i].setSelected(false); }
			 */

			// END G.STURR

			return this;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			// G.STURR 2010-1-24:
			// Handle comboBox and radio button clicks

			// int style = Integer.parseInt(e.getActionCommand());
			int style = -1;
			// comboBox click
			if (e.getSource() == cbStyle) {
				style = cbStyle.getSelectedIndex();
			}
			// END G.STURR
			model.applyChanges(style);
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);
		}

		@Override
		public void setSelectedIndex(int index) {
			cbStyle.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// TODO Auto-generated method stub
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * panel to select the size of a GeoPoint
	 * 
	 * @author Markus Hohenwarter
	 */
	private class SlopeTriangleSizePanel extends JPanel
			implements ChangeListener, UpdateablePropertiesPanel, SetLabels,
			UpdateFonts, ISliderListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private SlopeTriangleSizeModel model;
		private JSlider slider;

		public SlopeTriangleSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			model = new SlopeTriangleSizeModel(app);
			model.setListener(this);

			// JLabel sizeLabel = new JLabel(loc.getMenu("Size") + ":");
			slider = new JSlider(1, 10);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			// slider.setFont(app.getSmallFont());
			slider.addChangeListener(this);

			/*
			 * setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			 * sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //setBorder(BorderFactory
			 * .createCompoundBorder(BorderFactory.createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * add(Box.createRigidArea(new Dimension(5,0))); add(sizeLabel);
			 */
			add(slider);
		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(loc.getMenu("Size")));
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			slider.removeChangeListener(this);
			model.updateProperties();
			// set value to first point's size

			slider.addChangeListener(this);
			return this;
		}

		/**
		 * change listener implementation for slider
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();

		}

		private void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);

		}
	}

	/**
	 * panel to select the size of a GeoAngle's arc
	 * 
	 * @author Markus Hohenwarter
	 */
	private class ArcSizePanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, ISliderListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AngleArcSizeModel model;
		private JSlider slider;

		public ArcSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new AngleArcSizeModel(app);
			model.setListener(this);
			// JLabel sizeLabel = new JLabel(loc.getMenu("Size") + ":");
			slider = new JSlider(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			/*
			 * //slider.setFont(app.getSmallFont());
			 * slider.addChangeListener(this); setLayout(new BoxLayout(this,
			 * BoxLayout.X_AXIS));
			 * sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //setBorder(BorderFactory
			 * .createCompoundBorder(BorderFactory.createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * add(Box.createRigidArea(new Dimension(5,0))); add(sizeLabel);
			 */
			add(slider);
		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(loc.getMenu("Size")));
		}

		/** Set minimum arc size from settings */
		public void setMinValue() {
			slider.setValue(AngleArcSizeModel.MIN_VALUE);
		}


		@Override
		public JPanel updatePanel(Object[] geos) {
			// check geos
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			slider.removeChangeListener(this);

			model.updateProperties();

			slider.addChangeListener(this);
			return this;
		}

		/**
		 * change listener implementation for slider
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		private void updateSliderFonts() {
			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);

		}
	}

	/**
	 * panel to select thickness and style (dashing) of a GeoLine
	 * 
	 * @author Markus Hohenwarter
	 */
	private class LineStylePanel extends JPanel implements ChangeListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
			ILineStyleListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JSlider thicknessSlider;
		private JPanel thicknessPanel;
		private JPanel opacityPanel;
		private JSlider opacitySlider;
		private JLabel dashLabel;
		private JComboBox dashCB;
		private LineStyleModel model;
		private JPanel dashPanel;

		public LineStylePanel() {
			model = new LineStyleModel(app);
			model.setListener(this);
			// thickness slider
			thicknessSlider = new JSlider(1, GeoElement.MAX_LINE_WIDTH);
			thicknessSlider.setMajorTickSpacing(2);
			thicknessSlider.setMinorTickSpacing(1);
			thicknessSlider.setPaintTicks(true);
			thicknessSlider.setPaintLabels(true);
			thicknessSlider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			thicknessSlider.addChangeListener(this);

			opacitySlider = new JSlider(0, 100);
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);
			opacitySlider.setSnapToTicks(true);

			opacitySlider.addChangeListener(this);

			updateSliderFonts();
			// line style combobox (dashing)
			DashListRenderer renderer = new DashListRenderer();
			renderer.setPreferredSize(
					new Dimension(130, app.getGUIFontSize() + 6));
			dashCB = new JComboBox(EuclidianView.getLineTypes());
			dashCB.setRenderer(renderer);
			dashCB.addActionListener(this);

			// line style panel
			dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dashLabel = new JLabel();
			dashPanel.add(dashLabel);
			dashPanel.add(dashCB);

			// thickness panel
			thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			// opacity panel
			opacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			/*
			 * JLabel thicknessLabel = new JLabel(loc.getMenu("Thickness") +
			 * ":"); thicknessPanel.setLayout(new BoxLayout(thicknessPanel,
			 * BoxLayout.X_AXIS));
			 * thicknessLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //thicknessPanel.setBorder
			 * (BorderFactory.createCompoundBorder(BorderFactory
			 * .createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * thicknessPanel.add(Box.createRigidArea(new Dimension(5,0)));
			 * thicknessPanel.add(thicknessLabel);
			 */
			thicknessPanel.add(thicknessSlider);
			opacityPanel.add(opacitySlider);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			thicknessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			opacityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			dashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(thicknessPanel);
			add(opacityPanel);
			add(dashPanel);
		}

		@Override
		public void setLabels() {
			Localization loc1 = app.getLocalization();
			thicknessPanel.setBorder(BorderFactory
					.createTitledBorder(loc1.getMenu("Thickness")));
			opacityPanel.setBorder(BorderFactory
					.createTitledBorder(loc1.getMenu("LineOpacity")));

			dashLabel.setText(loc1.getMenu("LineStyle") + ":");
		}

		@Override
		public JPanel updatePanel(Object[] geos) {

			model.setGeos(geos);
			return update();
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			if (!model.hasGeos()) {
				return;
			}
			update();
		}

		public JPanel update() {
			// check geos
			if (!model.checkGeos()) {
				return null;
			}

			thicknessSlider.removeChangeListener(this);
			opacitySlider.removeChangeListener(this);
			dashCB.removeActionListener(this);

			model.updateProperties();

			thicknessSlider.addChangeListener(this);
			opacitySlider.addChangeListener(this);
			dashCB.addActionListener(this);
			return this;
		}

		/**
		 * change listener implementation for slider
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == thicknessSlider) {
				if (!thicknessSlider.getValueIsAdjusting()) {
					model.applyThickness(thicknessSlider.getValue());
				}
			} else if (e.getSource() == opacitySlider) {
				if (!opacitySlider.getValueIsAdjusting()) {
					int value = (int) ((opacitySlider.getValue() / 100.0f)
							* 255);
					model.applyOpacity(value);
				}
			}
		}

		/**
		 * action listener implementation for coord combobox
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dashCB) {
				model.applyLineType(
						((Integer) dashCB.getSelectedItem()).intValue());
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			thicknessPanel.setFont(font);
			opacityPanel.setFont(font);
			dashLabel.setFont(font);

			updateSliderFonts();
		}

		public void updateSliderFonts() {
			// set label font
			Dictionary<?, ?> labelTable = thicknessSlider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			labelTable = opacitySlider.getLabelTable();
			en = labelTable.elements();
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			// slider.setFont(app.getSmallFont());
		}

		@Override
		public void setThicknessSliderValue(int value) {
			thicknessSlider.setValue(value);
		}

		@Override
		public void setThicknessSliderMinimum(int minimum) {
			thicknessSlider.setMinimum(minimum);
		}

		@Override
		public void setOpacitySliderValue(int value) {
			opacitySlider.setValue(value);
		}

		@Override
		public void selectCommonLineStyle(boolean equalStyle, int type) {
			if (equalStyle) {
				for (int i = 0; i < dashCB.getItemCount(); i++) {
					if (type == ((Integer) dashCB.getItemAt(i)).intValue()) {
						dashCB.setSelectedIndex(i);
						break;
					}
				}
			} else {
				dashCB.setSelectedItem(null);
			}
		}

		@Override
		public void setLineTypeVisible(boolean value) {
			dashPanel.setVisible(value);
		}

		@Override
		public void setLineStyleHiddenVisible(boolean value) {
			// TODO Auto-generated method stub
		}

		@Override
		public void selectCommonLineStyleHidden(boolean equalStyle, int type) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setLineOpacityVisible(boolean value) {
			opacityPanel.setVisible(value);
		}
	}

	/**
	 * select dash style for hidden parts.
	 * 
	 * @author Mathieu
	 * 
	 */
	private class LineStyleHiddenPanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, ActionListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JRadioButton[] buttons;

		public LineStyleHiddenPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18, 18));

			buttons = new JRadioButton[3];

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE] = new JRadioButton(
					loc.getMenu("Hidden.Invisible"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE]
					.setActionCommand("none");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED] = new JRadioButton(
					loc.getMenu("Hidden.Dashed"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED]
					.setActionCommand("dashed");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN] = new JRadioButton(
					loc.getMenu("Hidden.Unchanged"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN]
					.setActionCommand("asNotHidden");

			ButtonGroup buttonGroup = new ButtonGroup();
			for (int i = 0; i < 3; i++) {
				buttons[i].addActionListener(this);
				add(buttons[i]);
				buttonGroup.add(buttons[i]);
			}

		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory
					.createTitledBorder(loc.getMenu("HiddenLineStyle")));
		}

		@Override
		public JPanel updatePanel(Object[] selGeos) {

			this.geos = selGeos;
			return update();
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			if (geos == null) {
				return;
			}
			update();
		}

		public JPanel update() {

			// check if we use 3D view
			if (!app.isEuclidianView3Dinited()) {
				return null;
			}

			// check geos
			if (!checkGeos(geos)) {
				return null;
			}

			// set value to first line's style
			GeoElement geo0 = (GeoElement) geos[0];

			// update radio buttons
			buttons[geo0.getLineTypeHidden()].setSelected(true);

			return this;
		}

		private boolean checkGeos(Object[] geos1) {
			boolean geosOK = true;
			for (int i = 0; i < geos1.length; i++) {
				GeoElement geo = (GeoElement) geos1[i];
				if (!(geo.showLineProperties())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			int type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE;

			if ("dashed".equals(e.getActionCommand())) {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED;
			} else if ("asNotHidden".equals(e.getActionCommand())) {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN;
			}

			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setLineTypeHidden(type);
				geo.updateRepaint();
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);
		}

	}

	/**
	 * panel to select the fading for endings of a surface
	 * 
	 * @author mathieu
	 */
	private class FadingPanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public FadingPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			slider = new JSlider(0, 50);
			slider.setMajorTickSpacing(25);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			updateSliderFonts();

			slider.addChangeListener(this);

			add(slider);
		}

		@Override
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(loc.getMenu("Fading")));
		}

		@Override
		public JPanel updatePanel(Object[] selGeos) {
			// check geos
			if (!checkGeos(selGeos)) {
				return null;
			}

			this.geos = selGeos;
			slider.removeChangeListener(this);

			// set value to first point's size
			GeoPlaneND geo0 = (GeoPlaneND) selGeos[0];
			slider.setValue((int) (100 * geo0.getFading()));

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] selGeos) {
			boolean geosOK = true;
			for (int i = 0; i < selGeos.length; i++) {
				GeoElement geo = (GeoElement) selGeos[i];
				if (!(geo.isGeoPlane())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoPlaneND plane;
				for (int i = 0; i < geos.length; i++) {
					plane = (GeoPlaneND) geos[i];
					plane.setFading((float) size / 100);
					((GeoElement) plane).updateRepaint();
				}
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		public void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the level of detail of surfaces
	 * 
	 * @author mathieu
	 */
	private class LodPanel extends JPanel implements ActionListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel, IComboListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JLabel label;
		private LodModel model;
		private JComboBox combo;

		public LodPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			model = new LodModel(app, isDefaults);
			model.setListener(this);

			label = new JLabel();
			combo = new JComboBox();

			add(label);
			add(combo);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}

		@Override
		public void setLabels() {

			label.setText(loc.getMenu("LevelOfDetail"));

			combo.removeActionListener(this);
			int idx = combo.getSelectedIndex();
			combo.removeAllItems();
			model.fillModes(loc);
			combo.setSelectedIndex(idx);
			combo.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			combo.removeActionListener(this);
			model.updateProperties();
			combo.addActionListener(this);
			return this;
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
			combo.setFont(font);

		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// nothing to do here

		}

		@Override
		public void setSelectedIndex(int index) {
			combo.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			combo.addItem(item);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == combo) {
				model.applyChanges(combo.getSelectedIndex());
			}
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * Panel for segment decoration
	 * 
	 * @author Loic
	 */
	private class DecoSegmentPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, IComboListener {
		private static final long serialVersionUID = 1L;
		private DecoSegmentModel model;
		private JComboBox decoCombo;
		private JLabel decoLabel;

		DecoSegmentPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new DecoSegmentModel(app);
			model.setListener(this);
			// deco combobox
			DecorationListRenderer renderer = new DecorationListRenderer();
			renderer.setPreferredSize(
					new Dimension(130, app.getGUIFontSize() + 6));
			decoCombo = new JComboBox(GeoSegment.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);

			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}

		@Override
		public void setLabels() {
			decoLabel.setText(loc.getMenu("Decoration") + ":");
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			decoCombo.removeActionListener(this);

			model.updateProperties();

			decoCombo.addActionListener(this);
			return this;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				model.applyChanges(type);
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSelectedIndex(int index) {
			decoCombo.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// TODO Auto-generated method stub
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
		}

	}

	private class DecoAnglePanel extends JPanel
			implements ActionListener, SetLabels, UpdateFonts,
			UpdateablePropertiesPanel, IDecoAngleListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private DecoAngleModel model;

		DecoAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new DecoAngleModel(app);
			model.setListener(this);
			// deco combobox
			DecorationAngleListRenderer renderer = new DecorationAngleListRenderer();
			renderer.setPreferredSize(new Dimension(80, 30));
			decoCombo = new JComboBox(GeoAngle.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);

			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}

		@Override
		public void setLabels() {
			decoLabel.setText(loc.getMenu("Decoration") + ":");
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			decoCombo.removeActionListener(this);

			model.updateProperties();
			decoCombo.addActionListener(this);
			return this;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				model.applyChanges(type);
			}
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		@Override
		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub
		}

		@Override
		public void setSelectedIndex(int index) {
			decoCombo.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// not supported
		}

		@Override
		public void setArcSizeMinValue() {
			setSliderMinValue();
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
		}

	}

	// added 3/11/06
	private class RightAnglePanel extends CheckboxPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		RightAnglePanel() {

			super("EmphasizeRightAngle");
			setModel(new RightAngleModel(this, app));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}
	}

	/**
	 * allows using a single = in condition to show object and dynamic color
	 * 
	 * @param strCond0
	 *            Condition to be processed
	 * @return processed condition
	 */
	public static String replaceEqualsSigns(String strCond0) {
		String strCond = strCond0;
		// needed to make next replace easier
		strCond = strCond.replaceAll(">=",
				ExpressionNodeConstants.strGREATER_EQUAL);
		strCond = strCond.replaceAll("<=",
				ExpressionNodeConstants.strLESS_EQUAL);
		strCond = strCond.replaceAll("==",
				ExpressionNodeConstants.strEQUAL_BOOLEAN);
		strCond = strCond.replaceAll("!=",
				ExpressionNodeConstants.strNOT_EQUAL);

		// allow A=B as well as A==B
		// also stops A=B doing an assignment of B to A :)
		return strCond.replaceAll("=",
				ExpressionNodeConstants.strEQUAL_BOOLEAN);

	}

	/**
	 * @return text panel
	 */
	public TextEditPanel getTextPanel() {
		return textEditPanel;
	}

} // PropertiesPanel

/**
 * panel for textfield size
 * 
 * @author Michael
 */
class TextfieldSizePanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, ITextFieldListener {

	private static final long serialVersionUID = 1L;

	private TextFieldSizeModel model;
	private JLabel label;
	private MyTextFieldD tfTextfieldSize;

	private LocalizationD loc;

	/**
	 * @param app
	 *            app
	 */
	public TextfieldSizePanel(AppD app) {
		this.loc = app.getLocalization();
		model = new TextFieldSizeModel(app);
		model.setListener(this);
		// text field for textfield size
		label = new JLabel();
		tfTextfieldSize = new MyTextFieldD(app, 5);
		label.setLabelFor(tfTextfieldSize);
		tfTextfieldSize.addActionListener(this);
		tfTextfieldSize.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfTextfieldSize);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);

		setLabels();
	}

	@Override
	public void setLabels() {
		label.setText(loc.getMenu("TextfieldLength") + ": ");
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		tfTextfieldSize.removeActionListener(this);

		model.updateProperties();

		tfTextfieldSize.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfTextfieldSize) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		model.applyChanges(tfTextfieldSize.getText());
		updatePanel(model.getGeos());
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// only focus lost is important
	}

	@Override
	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) {
		tfTextfieldSize.setText(text);

	}
}

/**
 * panel for condition to show object
 * 
 * @author Markus Hohenwarter
 */
class ShowConditionPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, IShowConditionListener {

	private static final long serialVersionUID = 1L;

	private ShowConditionModel model;
	private JTextField tfCondition;

	private Kernel kernel;
	private PropertiesPanelD propPanel;

	/**
	 * @param app
	 *            application
	 * @param propPanel
	 *            properties panel
	 */
	public ShowConditionPanel(AppD app, PropertiesPanelD propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		model = new ShowConditionModel(app, this);
		// non auto complete input panel
		InputPanelD inputPanel = new InputPanelD(null, app, -1, false);
		tfCondition = (AutoCompleteTextFieldD) inputPanel.getTextComponent();

		tfCondition.addActionListener(this);
		tfCondition.addFocusListener(this);

		// put it all together
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.CENTER);

		setLabels();
	}

	@Override
	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(
				kernel.getLocalization().getMenu("Condition.ShowObject")));
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		tfCondition.removeActionListener(this);

		model.updateProperties();

		tfCondition.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfCondition) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;
		model.applyChanges(tfCondition.getText(),
				kernel.getApplication().getDefaultErrorHandler());
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	/** flag to prevent double processing enter x focus lost */
	boolean processed = false;

	@Override
	public void focusLost(FocusEvent e) {
		if (!processed) {
			doActionPerformed();
		}
	}

	@Override
	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);
		tfCondition.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) {
		tfCondition.setText(text);
	}

	@Override
	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);
	}

}

/**
 * panel for condition to show object
 * 
 * @author Michael Borcherds 2008-04-01
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class ColorFunctionPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, IColorFunctionListener {

	private static final long serialVersionUID = 1L;
	/** color fun model */
	ColorFunctionModel model;
	private JTextField tfRed, tfGreen, tfBlue, tfAlpha;
	private JButton btRemove;
	private JLabel nameLabelR, nameLabelG, nameLabelB, nameLabelA;

	private JComboBox cbColorSpace;
	private int colorSpace = GeoElement.COLORSPACE_RGB;
	// flag to prevent unneeded relabeling of the colorSpace comboBox
	private boolean allowSetComboBoxLabels = true;

	private String defaultR = "0", defaultG = "0", defaultB = "0",
			defaultA = "1";

	private Kernel kernel;
	private PropertiesPanelD propPanel;

	/**
	 * @param app
	 *            app
	 * @param propPanel
	 *            properties panel
	 */
	public ColorFunctionPanel(AppD app, PropertiesPanelD propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		model = new ColorFunctionModel(app, this);
		// non auto complete input panel
		InputPanelD inputPanelR = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelG = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelB = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelA = new InputPanelD(null, app, 1, -1, true);
		tfRed = (AutoCompleteTextFieldD) inputPanelR.getTextComponent();
		tfGreen = (AutoCompleteTextFieldD) inputPanelG.getTextComponent();
		tfBlue = (AutoCompleteTextFieldD) inputPanelB.getTextComponent();
		tfAlpha = (AutoCompleteTextFieldD) inputPanelA.getTextComponent();

		tfRed.addActionListener(this);
		tfRed.addFocusListener(this);
		tfGreen.addActionListener(this);
		tfGreen.addFocusListener(this);
		tfBlue.addActionListener(this);
		tfBlue.addFocusListener(this);
		tfAlpha.addActionListener(this);
		tfAlpha.addFocusListener(this);

		nameLabelR = new JLabel("", SwingConstants.TRAILING);
		nameLabelR.setLabelFor(inputPanelR);
		nameLabelG = new JLabel("", SwingConstants.TRAILING);
		nameLabelG.setLabelFor(inputPanelG);
		nameLabelB = new JLabel("", SwingConstants.TRAILING);
		nameLabelB.setLabelFor(inputPanelB);
		nameLabelA = new JLabel("", SwingConstants.TRAILING);
		nameLabelA.setLabelFor(inputPanelA);

		btRemove = new JButton("\u2718");
		btRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.removeAll();
			}
		});

		cbColorSpace = new JComboBox();
		cbColorSpace.addActionListener(this);

		setLayout(new BorderLayout());

		JPanel colorsPanel = new JPanel(new SpringLayout());
		colorsPanel.add(nameLabelR);
		colorsPanel.add(inputPanelR);
		colorsPanel.add(nameLabelG);
		colorsPanel.add(inputPanelG);
		colorsPanel.add(nameLabelB);
		colorsPanel.add(inputPanelB);
		colorsPanel.add(nameLabelA);
		colorsPanel.add(inputPanelA);

		SpringUtilities.makeCompactGrid(colorsPanel, 4, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(colorsPanel, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new SpringLayout());

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(cbColorSpace);
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(btRemove);
		buttonsPanel.add(leftPanel);
		buttonsPanel.add(rightPanel);

		SpringUtilities.makeCompactGrid(buttonsPanel, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(buttonsPanel, BorderLayout.SOUTH);

		setLabels();
	}

	@Override
	public void setLabels() {
		Localization loc = kernel.getLocalization();

		setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("DynamicColors")));

		if (allowSetComboBoxLabels) {
			cbColorSpace.removeActionListener(this);
			cbColorSpace.removeAllItems();
			cbColorSpace.addItem(loc.getMenu("RGB"));
			cbColorSpace.addItem(loc.getMenu("HSV"));
			cbColorSpace.addItem(loc.getMenu("HSL"));
			cbColorSpace.addActionListener(this);
		}
		allowSetComboBoxLabels = true;

		switch (colorSpace) {
		default:
		case GeoElement.COLORSPACE_RGB:
			nameLabelR
					.setText(StringUtil.capitalize(loc.getColor("red")) + ":");
			nameLabelG
					.setText(
							StringUtil.capitalize(loc.getColor("green")) + ":");
			nameLabelB
					.setText(StringUtil.capitalize(loc.getColor("blue")) + ":");
			break;
		case GeoElement.COLORSPACE_HSB:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Value") + ":");
			break;
		case GeoElement.COLORSPACE_HSL:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Lightness") + ":");
			break;
		}

		nameLabelA.setText(loc.getMenu("Opacity") + ":");

		btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		// remove action listeners
		tfRed.removeActionListener(this);
		tfGreen.removeActionListener(this);
		tfBlue.removeActionListener(this);
		tfAlpha.removeActionListener(this);
		btRemove.removeActionListener(this);
		cbColorSpace.removeActionListener(this);

		model.updateProperties();

		// restore action listeners
		tfRed.addActionListener(this);
		tfGreen.addActionListener(this);
		tfBlue.addActionListener(this);
		tfAlpha.addActionListener(this);
		cbColorSpace.addActionListener(this);

		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfRed || e.getSource() == tfGreen
				|| e.getSource() == tfBlue || e.getSource() == tfAlpha) {
			doActionPerformed();
		}
		if (e.getSource() == cbColorSpace) {
			colorSpace = cbColorSpace.getSelectedIndex();
			allowSetComboBoxLabels = false;
			setLabels();
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;

		String strRed = tfRed.getText();
		String strGreen = tfGreen.getText();
		String strBlue = tfBlue.getText();
		String strAlpha = tfAlpha.getText();

		strRed = PropertiesPanelD.replaceEqualsSigns(strRed);
		strGreen = PropertiesPanelD.replaceEqualsSigns(strGreen);
		strBlue = PropertiesPanelD.replaceEqualsSigns(strBlue);
		strAlpha = PropertiesPanelD.replaceEqualsSigns(strAlpha);

		model.applyChanges(strRed, strGreen, strBlue, strAlpha, colorSpace,
				defaultR, defaultG, defaultB, defaultA);

	}

	@Override
	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	private boolean processed = false;

	@Override
	public void focusLost(FocusEvent e) {
		if (!processed) {
			doActionPerformed();
		}
	}

	@Override
	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);

		cbColorSpace.setFont(font);

		nameLabelR.setFont(font);
		nameLabelG.setFont(font);
		nameLabelB.setFont(font);
		nameLabelA.setFont(font);

		btRemove.setFont(font);

		tfRed.setFont(font);
		tfGreen.setFont(font);
		tfBlue.setFont(font);
		tfAlpha.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRedText(final String text) {
		tfRed.setText(text);

	}

	@Override
	public void setGreenText(final String text) {
		tfGreen.setText(text);
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlueText(final String text) {
		tfBlue.setText(text);

	}

	@Override
	public void setAlphaText(final String text) {
		tfAlpha.setText(text);

	}

	@Override
	public void setDefaultValues(GeoElement geo) {
		Color col = GColorD.getAwtColor(geo.getObjectColor());
		defaultR = "" + col.getRed() / 255.0;
		defaultG = "" + col.getGreen() / 255.0;
		defaultB = "" + col.getBlue() / 255.0;
		defaultA = "" + geo.getFillColor().getAlpha() / 255.0;

		// set the selected color space and labels to match the first geo's
		// color space
		colorSpace = geo.getColorSpace();
		cbColorSpace.setSelectedIndex(colorSpace);
		allowSetComboBoxLabels = false;
		setLabels();

	}

	@Override
	public void showAlpha(boolean value) {
		tfAlpha.setVisible(value);
		nameLabelA.setVisible(value);
	}

	@Override
	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);

	}

}

/**
 * panel to set graphics view location
 * 
 * @author G.Sturr
 */
class GraphicsViewLocationPanel extends JPanel
		implements ActionListener, UpdateablePropertiesPanel, SetLabels,
		UpdateFonts, IGraphicsViewLocationListener {

	private static final long serialVersionUID = 1L;

	private ViewLocationModel model;

	private JCheckBox cbGraphicsView, cbGraphicsView2, cbGraphicsView3D,
			cbGraphicsViewForPlane;

	private AppD app;

	private LocalizationD loc;

	/**
	 * @param app
	 *            app
	 * @param propPanel
	 *            props panel
	 */
	public GraphicsViewLocationPanel(AppD app, PropertiesPanelD propPanel) {
		this.app = app;
		this.loc = app.getLocalization();
		model = new ViewLocationModel(app, this);

		cbGraphicsView = new JCheckBox();
		cbGraphicsView2 = new JCheckBox();
		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);

		cbGraphicsView3D = new JCheckBox();
		cbGraphicsView3D.addActionListener(this);
		cbGraphicsViewForPlane = new JCheckBox();
		cbGraphicsViewForPlane.addActionListener(this);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(cbGraphicsView);
		add(cbGraphicsView2);
		add(cbGraphicsView3D);
		add(cbGraphicsViewForPlane);

		setLabels();
	}

	@Override
	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(
				loc.getMenu("Location")));
		cbGraphicsView.setText(loc.getMenu("DrawingPad"));
		cbGraphicsView2.setText(loc.getMenu("DrawingPad2"));
		cbGraphicsView3D.setText(loc.getMenu("GraphicsView3D"));
		cbGraphicsViewForPlane.setText(loc.getMenu("ExtraViews"));

	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		cbGraphicsView.removeActionListener(this);
		cbGraphicsView2.removeActionListener(this);
		cbGraphicsView3D.removeActionListener(this);
		cbGraphicsViewForPlane.removeActionListener(this);

		model.updateProperties();

		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);
		cbGraphicsView3D.addActionListener(this);
		cbGraphicsViewForPlane.addActionListener(this);

		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cbGraphicsView) {
			model.applyToEuclidianView1(cbGraphicsView.isSelected());
		} else if (e.getSource() == cbGraphicsView2) {
			model.applyToEuclidianView2(cbGraphicsView2.isSelected());
		} else if (e.getSource() == cbGraphicsView3D) {
			model.applyToEuclidianView3D(cbGraphicsView3D.isSelected());
		} else if (e.getSource() == cbGraphicsViewForPlane) {
			model.applyToEuclidianViewForPlane(
					cbGraphicsViewForPlane.isSelected());
		}

	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		setFont(font);
		cbGraphicsView.setFont(font);
		cbGraphicsView2.setFont(font);
		cbGraphicsView3D.setFont(font);
		cbGraphicsViewForPlane.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectView(int index, boolean isSelected) {
		switch (index) {
		default:
			Log.error("missing case");
			break;
		case 0:
			cbGraphicsView.setSelected(isSelected);
			break;
		case 1:
			cbGraphicsView2.setSelected(isSelected);
			break;
		case 2:
			cbGraphicsView3D.setSelected(isSelected);
			break;
		case 3:
			cbGraphicsViewForPlane.setSelected(isSelected);
			break;
		case 4:
			// cbAlgebraView.setValue(isSelected);
			Log.error("cbAlgebraView not implemented in desktop");
			break;

		}
	}

	@Override
	public void setCheckBox3DVisible(boolean flag) {
		cbGraphicsView3D.setVisible(flag);
	}

	@Override
	public void setCheckBoxForPlaneVisible(boolean flag) {
		cbGraphicsViewForPlane.setVisible(flag);
	}

}

/**
 * Panel for changing button size
 *
 */
class ButtonSizePanel extends JPanel implements ChangeListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts, IButtonSizeListener {

	private static final long serialVersionUID = 1L;
	private ButtonSizeModel model;

	private MyTextFieldD tfButtonWidth;
	private MyTextFieldD tfButtonHeight;
	private JLabel labelWidth;
	private JLabel labelHeight;
	private JLabel labelPixelW;
	private JLabel labelPixelH;
	private Localization loc;
	private JCheckBox cbUseFixedSize;

	/**
	 * @param app
	 *            app
	 * @param loc
	 *            localization
	 */
	public ButtonSizePanel(AppD app, Localization loc) {
		this.loc = loc;
		model = new ButtonSizeModel(app);
		model.setListener(this);

		labelWidth = new JLabel(loc.getMenu("Width"));
		labelHeight = new JLabel(loc.getMenu("Height"));
		labelPixelW = new JLabel(loc.getMenu("Pixels.short"));
		labelPixelH = new JLabel(loc.getMenu("Pixels.short"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		cbUseFixedSize = new JCheckBox(loc.getMenu("fixed"));
		tfButtonWidth = new MyTextFieldD(app, 3);
		tfButtonHeight = new MyTextFieldD(app, 3);
		tfButtonHeight.setInputVerifier(new SizeVerify());
		tfButtonWidth.setInputVerifier(new SizeVerify());
		cbUseFixedSize.addChangeListener(this);
		tfButtonHeight.setEnabled(cbUseFixedSize.isSelected());
		tfButtonWidth.setEnabled(cbUseFixedSize.isSelected());
		tfButtonHeight.addFocusListener(this);
		tfButtonWidth.addFocusListener(this);
		add(cbUseFixedSize);
		add(labelWidth);
		add(tfButtonWidth);
		add(labelPixelW);
		add(labelHeight);
		add(tfButtonHeight);
		add(labelPixelH);
	}

	@Override
	public void updateFonts() {
		// do nothing
	}

	@Override
	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(loc.getMenu("ButtonSize")));
		labelWidth.setText(loc.getMenu("Width"));
		labelHeight.setText(loc.getMenu("Height"));
		labelPixelW.setText(loc.getMenu("Pixels.short"));
		labelPixelH.setText(loc.getMenu("Pixels.short"));
		cbUseFixedSize.setText(loc.getMenu("fixed"));

	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}
		model.updateProperties();

		return this;
	}

	@Override
	public void updateSizes(int width, int height, boolean isFixed) {
		cbUseFixedSize.removeChangeListener(this);
		cbUseFixedSize.setSelected(isFixed);
		tfButtonHeight.setText("" + height);
		tfButtonWidth.setText("" + width);
		tfButtonHeight.setEnabled(isFixed);
		tfButtonWidth.setEnabled(isFixed);
		cbUseFixedSize.addChangeListener(this);

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent arg0) {
		model.setSizesFromString(tfButtonWidth.getText(),
				tfButtonHeight.getText(), cbUseFixedSize.isSelected());
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == cbUseFixedSize) {
			JCheckBox check = (JCheckBox) arg0.getSource();
			model.applyChanges(check.isSelected());
		}
	}

	/**
	 * Verifies text is 2 or 3 digits
	 *
	 */
	static class SizeVerify extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			MyTextFieldD tf = (MyTextFieldD) input;
			String s = tf.getText();
			if (!s.matches("\\d{2,3}")) {
				return false;
			}
			if (Integer.parseInt(s) < 24 || Integer.parseInt(s) > 500) {
				return false;
			}
			return true;
		}
	}
}

/**
 * panel for name of object
 * 
 * @author Markus Hohenwarter
 */
class NamePanel extends JPanel implements ActionListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts, IObjectNameListener {

	private static final long serialVersionUID = 1L;
	/** name model */
	ObjectNameModel model;
	private AutoCompleteTextFieldD tfName, tfDefinition, tfCaption;

	private Runnable doActionStopped = new Runnable() {
		@Override
		public void run() {
			model.setBusy(false);
		}
	};
	private JLabel nameLabel, defLabel, captionLabel;
	private InputPanelD inputPanelName, inputPanelDef, inputPanelCap;

	private AppD app;
	private Localization loc;

	/**
	 * @param app
	 *            application
	 */
	public NamePanel(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();
		model = new ObjectNameModel(app, this);
		// NAME PANEL

		// non auto complete input panel
		inputPanelName = new InputPanelD(null, app, 1, -1, true);
		tfName = (AutoCompleteTextFieldD) inputPanelName.getTextComponent();
		tfName.setAutoComplete(false);
		tfName.addActionListener(this);
		tfName.addFocusListener(this);

		// definition field: non auto complete input panel
		inputPanelDef = new InputPanelD(null, app, 1, -1, true);
		tfDefinition = (AutoCompleteTextFieldD) inputPanelDef
				.getTextComponent();
		tfDefinition.setAutoComplete(false);
		tfDefinition.addActionListener(this);
		tfDefinition.addFocusListener(this);

		// caption field: non auto complete input panel
		inputPanelCap = new InputPanelD(null, app, 1, -1, true);
		tfCaption = (AutoCompleteTextFieldD) inputPanelCap.getTextComponent();
		tfCaption.setAutoComplete(false);
		tfCaption.addActionListener(this);
		tfCaption.addFocusListener(this);

		// name panel
		nameLabel = new JLabel();
		nameLabel.setLabelFor(inputPanelName);

		// definition panel
		defLabel = new JLabel();
		defLabel.setLabelFor(inputPanelDef);

		// caption panel
		captionLabel = new JLabel();
		captionLabel.setLabelFor(inputPanelCap);

		setLabels();
		updateGUI(true, true);
	}

	@Override
	public void setLabels() {
		nameLabel.setText(loc.getMenu("Name") + ":");
		defLabel.setText(loc.getMenu("Definition") + ":");
		captionLabel.setText(loc.getMenu("Button.Caption") + ":");
	}

	@Override
	public void updateGUI(boolean showDefinition, boolean showCaption) {
		int newRows = 1;
		removeAll();

		if (loc.isRightToLeftReadingOrder()) {
			add(inputPanelName);
			add(nameLabel);
		} else {
			add(nameLabel);
			add(inputPanelName);
		}

		if (showDefinition) {
			newRows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelDef);
				add(defLabel);
			} else {
				add(defLabel);
				add(inputPanelDef);
			}
		}

		if (showCaption) {
			newRows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelCap);
				add(captionLabel);
			} else {
				add(captionLabel);
				add(inputPanelCap);
			}
		}

		app.setComponentOrientation(this);

		this.rows = newRows;
		setLayout();

	}

	private int rows;

	private void setLayout() {
		// Lay out the panel
		setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this, rows, 2, // rows, cols
				5, 5, // initX, initY
				5, 5); // xPad, yPad
	}

	/**
	 * current geo on which focus lost shouls apply (may be different to current
	 * geo, due to threads)
	 */
	private GeoElementND currentGeoForFocusLost = null;

	@Override
	public JPanel updatePanel(Object[] geos) {

		/*
		 * DON'T WORK : MAKE IT A TRY FOR 5.0 ? //apply textfields modification
		 * on previous geo before switching to new geo //skip this if label is
		 * not set (we re in the middle of redefinition) //skip this if action
		 * is performing if (currentGeo!=null && currentGeo.isLabelSet() &&
		 * !actionPerforming && (geos.length!=1 || geos[0]!=currentGeo)){
		 * 
		 * //App.printStacktrace("\n"+tfName.getText()+"\n"+currentGeo.getLabel(
		 * StringTemplate.defaultTemplate));
		 * 
		 * String strName = tfName.getText(); if (strName !=
		 * currentGeo.getLabel(StringTemplate.defaultTemplate))
		 * nameInputHandler.processInput(tfName.getText());
		 * 
		 * 
		 * String strDefinition = tfDefinition.getText(); if
		 * (strDefinition.length()>0 &&
		 * !strDefinition.equals(getDefText(currentGeo)))
		 * defInputHandler.processInput(strDefinition);
		 * 
		 * String strCaption = tfCaption.getText(); if
		 * (!strCaption.equals(currentGeo.getCaptionSimple())){
		 * currentGeo.setCaption(tfCaption.getText());
		 * currentGeo.updateVisualStyleRepaint(); } }
		 */

		model.setGeos(geos);
		if (!model.checkGeos()) {
			// currentGeo=null;
			return null;
		}

		model.updateProperties();

		return this;
	}

	private String redefinitionForFocusLost = "";

	/**
	 * @param geo
	 *            element
	 */
	public void updateDef(GeoElementND geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy()) {
			return;
		}

		tfDefinition.removeActionListener(this);
		model.getDefInputHandler().setGeoElement(geo);
		String text = ObjectNameModel.getDefText(geo);
		if (app.isMacOS()) {
			if (app.isMacOS() && text.length() > 300) {
				text = text.substring(0, 300);
				tfDefinition.setEditable(false);
			} else {
				tfDefinition.setEditable(true);
			}
		}

		tfDefinition.setText(text);
		tfDefinition.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * @param geo
	 *            element
	 */
	public void updateName(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy()) {
			return;
		}

		tfName.removeActionListener(this);
		model.getNameInputHandler().setGeoElement(geo);
		tfName.setText(geo.getLabel(StringTemplate.editTemplate));
		tfName.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (model.isBusy()) {
			return;
		}

		doActionPerformed(e.getSource());
	}

	private synchronized void doActionPerformed(Object source) {

		model.setBusy(true);

		if (source == tfName) {
			// rename
			model.applyNameChange(tfName.getText(),
					app.getDefaultErrorHandler());

		} else if (source == tfDefinition) {

			model.applyDefinitionChange(tfDefinition.getText(),
					app.getDefaultErrorHandler());
			tfDefinition.requestFocusInWindow();

		} else if (source == tfCaption) {
			model.applyCaptionChange(tfCaption.getText());
			if (!"".equals(tfCaption.getText())) {
				GeoElement geo0 = model.getGeoAt(0);
				geo0.setLabelVisible(true);
				geo0.setLabelMode(GeoElement.LABEL_CAPTION);

				OptionPanelD op = ((PropertiesViewD) app.getGuiManager()
						.getPropertiesView())
								.getOptionPanel(OptionType.OBJECTS);

				if (op != null && op instanceof OptionsObjectD) {
					PropertiesPanelD propPanel = ((OptionsObjectD) op)
							.getPropPanel();
					if (propPanel != null) {
						propPanel.getLabelPanel().update(true, true, 0);
					}
				}

			}
		}

		SwingUtilities.invokeLater(doActionStopped);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// started to type something : store current geo if focus lost
		currentGeoForFocusLost = model.getCurrentGeo();
	}

	@Override
	public void focusLost(FocusEvent e) {

		if (model.isBusy()) {
			return;
		}

		Object source = e.getSource();

		if (source == tfDefinition) {
			// currentGeo may has changed if focus is lost by clicking another
			// geo

			if (!tfDefinition.isEditable()) {
				return;
			}

			if (model.getCurrentGeo() == currentGeoForFocusLost) {
				model.applyDefinitionChange(tfDefinition.getText(),
						app.getDefaultErrorHandler());
			} else {
				model.redefineCurrentGeo(currentGeoForFocusLost,
						tfDefinition.getText(), redefinitionForFocusLost,
						app.getDefaultErrorHandler());
			}

			SwingUtilities.invokeLater(doActionStopped);

		} else {
			doActionPerformed(source);
		}
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		nameLabel.setFont(font);
		defLabel.setFont(font);
		captionLabel.setFont(font);

		inputPanelName.updateFonts();
		inputPanelDef.updateFonts();
		inputPanelCap.updateFonts();

		setLayout();

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// NOTHING SHOULD BE DONE HERE (ENDLESS CALL WITH UPDATE)

	}

	@Override
	public void setNameText(final String text) {
		tfName.setText(text);
		tfName.requestFocus();
	}

	@Override
	public void setDefinitionText(final String text) {
		tfDefinition.setText(text);
	}

	@Override
	public void setCaptionText(final String text) {
		tfCaption.setText(text);
		tfCaption.requestFocus();
	}

	@Override
	public void updateCaption(String text) {
		tfCaption.removeActionListener(this);
		tfCaption.setText(text);
		tfCaption.addActionListener(this);

	}

	@Override
	public void updateDefLabel() {
		updateDef(model.getCurrentGeo());

		if (model.getCurrentGeo().isIndependent()) {
			defLabel.setText(loc.getMenu("Value") + ":");
		} else {
			defLabel.setText(loc.getMenu("Definition") + ":");
		}
	}

	@Override
	public void updateName(String text) {
		tfName.removeActionListener(this);
		tfName.setText(text);

		// if a focus lost is called in between, we keep the current definition
		// text
		redefinitionForFocusLost = tfDefinition.getText();
		tfName.addActionListener(this);

	}

}