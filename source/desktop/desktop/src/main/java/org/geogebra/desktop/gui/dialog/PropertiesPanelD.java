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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenPositionModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimatingModel;
import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.AuxObjectModel;
import org.geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.CenterImageModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.DrawArrowsModel;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.IconOptionsModel;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LayerModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import org.geogebra.common.gui.dialog.options.model.RightAngleModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.SegmentStyleModel;
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
import org.geogebra.common.gui.dialog.options.model.TextFieldAlignmentModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TooltipModel;
import org.geogebra.common.gui.dialog.options.model.TraceModel;
import org.geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import org.geogebra.common.gui.dialog.options.model.VectorHeadStyleModel;
import org.geogebra.common.gui.dialog.options.model.VerticalIncrementModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel.IGraphicsViewLocationListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.GeoGebraComboBoxEditor;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.properties.AnimationSpeedPanel;
import org.geogebra.desktop.gui.properties.AnimationStepPanel;
import org.geogebra.desktop.gui.properties.SliderPropertiesPanelD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.util.SliderUtil;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
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
public class PropertiesPanelD extends JPanel implements SetLabels, UpdateFonts,
 UpdateTabs {
	/** application */
	AppD app;
	/** localization */
	LocalizationD loc;
	/** kernel */
	Kernel kernel;
	/** color chooser */
	GeoGebraColorChooser colChooser;

	private static final long serialVersionUID = 1L;
	private NamePanelD namePanel;
	private ShowObjectPanel showObjectPanel;
	private CheckboxPanel selectionAllowed;
	private CheckboxPanel showTrimmedIntersectionLines;
	private ColorPanel colorPanel;
	private LabelPanel labelPanel;
	private ComboPanel tooltipPanel;
	private ComboPanel layerPanel;
	private ComboPanel coordPanel;
	private ComboPanel lineEqnPanel;
	private ComboPanel planeEqnPanel;
	private CheckboxPanel symbolicPanel;
	private ConicEqnPanel conicEqnPanel;
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel;
	private TextOptionsPanelD textOptionsPanel;
	private ArcSizePanel arcSizePanel;
	private LineStylePanel lineStylePanel;
	private LineStyleHiddenPanel lineStylePanelHidden;
	private CheckboxPanel drawArrowsPanel;
	private IconDropdownPanelD segmentStartStylePanel;
	private IconDropdownPanelD segmentEndStylePanel;
	private IconDropdownPanelD vectorStylePanel;
	// added by Loic BEGIN
	private DecoSegmentPanel decoSegmentPanel;
	private DecoAnglePanel decoAnglePanel;
	private RightAnglePanel rightAnglePanel;
	// END

	/** filling */
	FillingPanelD fillingPanel;
	private FadingPanel fadingPanel;
	private LodPanel lodPanel;
	private CheckboxPanel checkBoxInterpolateImage;
	private CheckboxPanel tracePanel;
	private CheckboxPanel animatingPanel;
	private CheckboxPanel fixPanel;
	private CheckboxPanel ineqStylePanel;
	private CheckboxPanel checkBoxFixPanel;
	private AllowReflexAnglePanel allowReflexAnglePanel;
	private CheckboxPanel allowOutlyingIntersectionsPanel;
	private CheckboxPanel auxPanel;
	private TextPropertyPanel animStepPanel;
	private TextPropertyPanel verticalIncrementPanel;
	private TextPropertyPanel textFieldSizePanel;
	private TextFieldAlignmentPanel textFieldAlignmentPanel;
	private AnimationSpeedPanel animSpeedPanel;
	private SliderPropertiesPanelD sliderPanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private StartPointPanel startPointPanel;
	private CornerPointsPanel cornerPointsPanel;
	private TextEditPanel textEditPanel;
	private ScriptEditPanel scriptEditPanel;
	private CheckboxPanel bgImagePanel;
	private CheckboxPanel absScreenLocPanel;
	private CheckboxPanel centerImagePanel;
	private CheckboxPanel comboBoxPanel;
	private ShowConditionPanel showConditionPanel;
	private ColorFunctionPanel colorFunctionPanel;
	private TextPropertyPanel absPositionXPanel;
	private TextPropertyPanel absPositionYPanel;

	private GraphicsViewLocationPanel graphicsViewLocationPanel;
	private ButtonSizePanel buttonSizePanel;
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
			namePanel = new NamePanelD(app, this);
			labelPanel = new LabelPanel();
			tooltipPanel = new ComboPanel(new TooltipModel(app), app);
			layerPanel = new ComboPanel(new LayerModel(app), app);
			scriptEditPanel = new ScriptEditPanel();
			textEditPanel = new TextEditPanel(this);
			startPointPanel = new StartPointPanel();
			cornerPointsPanel = new CornerPointsPanel();
			bgImagePanel = getCheckboxPanel(new BackgroundImageModel(null, app));
			showConditionPanel = new ShowConditionPanel(app, this);
			colorFunctionPanel = new ColorFunctionPanel(app, this);

			graphicsViewLocationPanel = new GraphicsViewLocationPanel(app,
					this);
		}

		allowReflexAnglePanel = new AllowReflexAnglePanel();

		sliderPanel = new SliderPropertiesPanelD(app, this, false, true);
		showObjectPanel = new ShowObjectPanel();
		selectionAllowed = getCheckboxPanel(new SelectionAllowedModel(null, app));
		showTrimmedIntersectionLines = getCheckboxPanel(
				new TrimmedIntersectionLinesModel(null, app));
		colorPanel = new ColorPanel(this, colChooser);
		coordPanel = new ComboPanel(new CoordsModel(app), app);
		lineEqnPanel = new ComboPanel(new LineEqnModel(app), app);
		planeEqnPanel = new ComboPanel(new PlaneEqnModel(app), app);
		conicEqnPanel = new ConicEqnPanel();
		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel();
		ineqStylePanel = getCheckboxPanel(new IneqStyleModel(app));
		textOptionsPanel = new TextOptionsPanelD(this);
		arcSizePanel = new ArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		lineStylePanel = new LineStylePanel();
		lineStylePanelHidden = new LineStyleHiddenPanel();
		drawArrowsPanel = getCheckboxPanel(new DrawArrowsModel(null, app));
		segmentStartStylePanel = new IconDropdownPanelD(getStartImages(),
				new SegmentStyleModel(app, true), 2);
		segmentEndStylePanel = new IconDropdownPanelD(getEndImages(),
				new SegmentStyleModel(app, false), 2);
		vectorStylePanel = new IconDropdownPanelD(getVectorImages(),
				new VectorHeadStyleModel(app), 1);
		// added by Loic BEGIN
		decoSegmentPanel = new DecoSegmentPanel();
		decoAnglePanel = new DecoAnglePanel();
		rightAnglePanel = new RightAnglePanel();
		// END
		fillingPanel = new FillingPanelD(app);
		fadingPanel = new FadingPanel();
		lodPanel = new LodPanel();
		checkBoxInterpolateImage = getCheckboxPanel(new InterpolateImageModel(app));
		tracePanel = getCheckboxPanel(new TraceModel(null, app));
		animatingPanel = getCheckboxPanel(new AnimatingModel(app, null));
		fixPanel = getCheckboxPanel(new FixObjectModel(null, app));
		checkBoxFixPanel = getCheckboxPanel(new FixCheckboxModel(null, app));
		absScreenLocPanel = getCheckboxPanel(new AbsoluteScreenLocationModel(app));
		absPositionXPanel = new TextPropertyPanel(app, new AbsoluteScreenPositionModel.ForX(app));
		absPositionYPanel = new TextPropertyPanel(app, new AbsoluteScreenPositionModel.ForY(app));
		centerImagePanel = getCheckboxPanel(new CenterImageModel(app));
		comboBoxPanel = getCheckboxPanel(new ListAsComboModel(app, null));
		// showView2D = new ShowView2D();
		auxPanel = getCheckboxPanel(new AuxObjectModel(null, app));
		animStepPanel = new AnimationStepPanel(new AnimationStepModel(app), app);
		verticalIncrementPanel = new TextPropertyPanel(app, new VerticalIncrementModel(app));
		symbolicPanel = getCheckboxPanel(new SymbolicModel(app));
		textFieldSizePanel = new TextPropertyPanel(app, new TextFieldSizeModel(app));
		textFieldAlignmentPanel = new TextFieldAlignmentPanel(app);
		animSpeedPanel = new AnimationSpeedPanel(app);
		allowOutlyingIntersectionsPanel = getCheckboxPanel(
				new OutlyingIntersectionsModel(null, app));
		buttonSizePanel = new ButtonSizePanel(app, loc);
		// tabbed pane for properties
		tabs = new JTabbedPane();
		initTabs();

		tabs.addChangeListener(e -> applyModifications());

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	private List<ImageResourceD> getEndImages() {
		return Arrays.asList(GuiResourcesD.STYLEBAR_END_DEFAULT,
		GuiResourcesD.STYLEBAR_END_LINE,
		GuiResourcesD.STYLEBAR_END_ARROW,
		GuiResourcesD.STYLEBAR_END_CROWS_FOOT,
		GuiResourcesD.STYLEBAR_END_ARROW_OUTLINED,
		GuiResourcesD.STYLEBAR_END_ARROW_FILLED,
		GuiResourcesD.STYLEBAR_END_CIRCLE_OUTLINED,
		GuiResourcesD.STYLEBAR_END_CIRCLE,
		GuiResourcesD.STYLEBAR_END_SQUARE_OUTLINED,
		GuiResourcesD.STYLEBAR_END_SQUARE,
		GuiResourcesD.STYLEBAR_END_DIAMOND_OUTLINED,
		GuiResourcesD.STYLEBAR_END_DIAMOND_FILLED);
	}

	private List<ImageResourceD> getStartImages() {
		return Arrays.asList(GuiResourcesD.STYLEBAR_START_DEFAULT,
		GuiResourcesD.STYLEBAR_START_LINE,
		GuiResourcesD.STYLEBAR_START_ARROW,
		GuiResourcesD.STYLEBAR_START_CROWS_FOOT,
		GuiResourcesD.STYLEBAR_START_ARROW_OUTLINED,
		GuiResourcesD.STYLEBAR_START_ARROW_FILLED,
		GuiResourcesD.STYLEBAR_START_CIRCLE_OUTLINED,
		GuiResourcesD.STYLEBAR_START_CIRCLE,
		GuiResourcesD.STYLEBAR_START_SQUARE_OUTLINED,
		GuiResourcesD.STYLEBAR_START_SQUARE,
		GuiResourcesD.STYLEBAR_START_DIAMOND_OUTLINED,
		GuiResourcesD.STYLEBAR_START_DIAMOND_FILLED);
	}

	private List<ImageResourceD> getVectorImages() {
		return Arrays.asList(GuiResourcesD.STYLEBAR_END_ARROW_FILLED,
				GuiResourcesD.STYLEBAR_END_ARROW);
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
			basicTabList.add(namePanel.getDynamicCaptionPanel());
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

		textTab = new TabPanel(textTabList, true);
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
		styleTabList.add(drawArrowsPanel);
		styleTabList.add(ineqStylePanel);
		styleTabList.add(arcSizePanel);
		styleTabList.add(buttonSizePanel);
		styleTabList.add(fillingPanel);
		styleTabList.add(fadingPanel);
		styleTabList.add(checkBoxInterpolateImage);
		styleTabList.add(textFieldSizePanel);
		styleTabList.add(textFieldAlignmentPanel);
		styleTabList.add(decoAnglePanel);
		styleTabList.add(segmentStartStylePanel);
		styleTabList.add(segmentEndStylePanel);
		styleTabList.add(vectorStylePanel);
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
			positionTabList.add(absScreenLocPanel);
			positionTabList.add(startPointPanel);
			positionTabList.add(cornerPointsPanel);
			positionTabList.add(absPositionXPanel);
			positionTabList.add(absPositionYPanel);
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
		algebraTabList.add(verticalIncrementPanel);
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
		drawArrowsPanel.setLabels();
		ineqStylePanel.setLabels();
		lineStylePanelHidden.setLabels();
		segmentStartStylePanel.setLabels();
		segmentEndStylePanel.setLabels();
		vectorStylePanel.setLabels();
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
		verticalIncrementPanel.setLabels();
		slopeTriangleSizePanel.setLabels();
		absScreenLocPanel.setLabels();
		absPositionXPanel.setLabels();
		absPositionYPanel.setLabels();
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
		for (int i = 0; i < tabs.getTabCount(); i++) {
			TabPanel tp = (TabPanel) tabs.getComponentAt(i);
			if (tp != null) {
				tabs.setTitleAt(i, tp.title);
			}
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
		drawArrowsPanel.updateFonts();
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
		verticalIncrementPanel.updateFonts();
		animSpeedPanel.updateFonts();
		slopeTriangleSizePanel.updateFonts();
		centerImagePanel.updateFonts();
		absScreenLocPanel.updateFonts();
		absPositionXPanel.updateFonts();
		absPositionYPanel.updateFonts();
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

	@Override
	public void updateTabs(Object[] geos) {
		if (geos.length == 0) {
			tabs.setVisible(false);
			return;
		}

		// remember selected tab
		Component selectedTab = tabs.getSelectedComponent();

		tabs.removeAll();
		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			tp.update(geos, tabs);
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
			boolean show = up.updatePanel(geos) != null;
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
	 * Update just definition of one geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateOneGeoDefinition(GeoElement geo) {
		namePanel.updateDefinition(geo);
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

	private static class TabPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private String title;
		private ArrayList<JPanel> panelList;

		public TabPanel(ArrayList<JPanel> pVec) {
			this(pVec, false);
		}

		public TabPanel(ArrayList<JPanel> pVec, boolean fullHeight) {
			panelList = pVec;

			setLayout(new BorderLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			panel.setLayout(new FullWidthLayout(5, fullHeight));

			for (JPanel jPanel : pVec) {
				panel.add(jPanel);
			}

			JScrollPane scrollPane = new JScrollPane(panel);
			if (fullHeight) {
				scrollPane.addComponentListener(getInvalidator(pVec.get(pVec.size() - 1)));
			}
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			add(scrollPane, BorderLayout.CENTER);
		}

		private ComponentListener getInvalidator(JPanel jPanel) {
			return new ComponentListener() {
				@Override
				public void componentResized(ComponentEvent e) {
					jPanel.invalidate();
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					// not needed
				}

				@Override
				public void componentShown(ComponentEvent e) {
					// not needed
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					// not needed
				}
			};
		}

		public TabPanel(JPanel panel) {
			panelList = new ArrayList<>();
			panelList.add(panel);

			setLayout(new BorderLayout());
			add(panel, BorderLayout.CENTER);
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void update(Object[] geos, JTabbedPane tabs) {
			if (updateTabPanel(panelList, geos)) {
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
			super(app, PropertiesPanelD.this, new ShowObjectModel(null, app));
		}

		@Override
		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setSelected(value);
			getCheckbox().setEnabled(isEnabled);
		}

	}

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

	private CheckboxPanel getCheckboxPanel(BooleanOptionModel model) {
		return new CheckboxPanel(app,  PropertiesPanelD.this, model);
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
		public void addItem(String item) {
			intervalCombo.addItem(item);
		}

		@Override
		public void clearItems() {
			// TODO Auto-generated method stub
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
		List<String> currentContent;

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
			List<String> points = new ArrayList<>(model.getChoices(loc));
			if (!points.equals(currentContent)) {
				cbModel.removeAllElements();
				model.fillModes(loc);
				currentContent = points;
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
			super(app, "CornerModel");
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
			implements UpdateablePropertiesPanel, SetLabels, UpdateFonts {

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
	}

	/**
	 * panel for script editing
	 */
	private class ScriptEditPanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTabbedPane tabbedPane;
		ScriptInputModel[] models;
		private List<ScriptInputDialog> panels = new ArrayList<>();

		public ScriptEditPanel() {
			super(new BorderLayout());

			int row = 35;
			int column = 15;

			tabbedPane = new JTabbedPane();
			models = ScriptInputModel.getModels(app);
			for (ScriptInputModel model : models) {
				ScriptInputDialog panel = new ScriptInputDialog(app, column, row, model);
				panels.add(panel);
			}
			setLayout(new BorderLayout());

			add(tabbedPane, BorderLayout.CENTER);

			tabbedPane.addChangeListener(e -> applyModifications());

		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			panels.forEach(ScriptInputDialog::applyModifications);
		}

		@Override
		public void setLabels() {
			panels.forEach(ScriptInputDialog::setLabels);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos)) {
				return null;
			}

			// remember selected tab
			final Component selectedTab = tabbedPane.getSelectedComponent();
			for (ScriptInputModel model: models) {
				model.setGeos(geos);
				model.updatePanel();
			}
			tabbedPane.removeAll();
			for (int i = 0; i < models.length; i++) {
				if (models[i].checkGeos()) {
					tabbedPane.addTab(loc.getMenu(models[i].getTitle()), panels.get(i));
				}
			}
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

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();

			tabbedPane.setFont(font);
			for (ScriptInputDialog dialog: panels) {
				dialog.updateFonts();
			}
		}
	}

	/**
	 * Panel for conic equation type
	 */
	private class ConicEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public ConicEqnPanel() {
			super(new ConicEqnModel(app), app);
		}

		@Override
		public void setLabels() {
			getLabel().setText(getTitle() + ":");
			if (getMultipleModel().hasGeos() && getMultipleModel().checkGeos()) {
				int selectedIndex = comboBox.getSelectedIndex();
				comboBox.removeActionListener(this);

				comboBox.removeAllItems();
				getMultipleModel().updateProperties();
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
			SliderUtil.addValueChangeListener(slider, d -> model.storeUndoInfo());

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
			model.applyChangesNoUndo(slider.getValue());
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
		private JComboBox cbStyle;

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
			// Handle comboBox and radio button clicks
			int style = -1;
			// comboBox click
			if (e.getSource() == cbStyle) {
				style = cbStyle.getSelectedIndex();
			}
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
			SliderUtil.addValueChangeListener(slider, val -> model.storeUndoInfo());

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
			model.applyChangesNoUndo(slider.getValue());
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
			SliderUtil.addValueChangeListener(slider, d -> model.storeUndoInfo());
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
			model.applyChanges(slider.getValue());
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
			SliderUtil.addValueChangeListener(thicknessSlider, val -> model.storeUndoInfo());

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
			SliderUtil.addValueChangeListener(opacitySlider, val -> model.storeUndoInfo());
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
				model.applyThickness(thicknessSlider.getValue());
			} else if (e.getSource() == opacitySlider) {
				model.applyOpacityPercentage(opacitySlider.getValue());
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
				geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
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

	private class IconDropdownPanelD extends JPanel implements ActionListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel, IComboListener {
		private IconOptionsModel model;
		private PopupMenuButtonD dropdown;
		private JLabel label;

		IconDropdownPanelD(List<ImageResourceD> imgFileNameList, IconOptionsModel model,
				int nrOfColumns) {
			super(new FlowLayout(FlowLayout.LEFT));
			this.model = model;
			model.setListener(this);

			ImageIcon[] iconArray = new ImageIcon[imgFileNameList.size()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIconD.createFileImageIcon(
						imgFileNameList.get(i));
			}
			dropdown = new PopupMenuButtonD(app, iconArray, -1, nrOfColumns,
					new Dimension(36, 36), SelectionTable.MODE_ICON);
			dropdown.setSelectedIndex(0);
			dropdown.setStandardButton(true);
			dropdown.setKeepVisible(false);
			dropdown.addActionListener(this);

			label = new JLabel();
			add(label);
			add(dropdown);
			setLabels();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dropdown) {
				model.applyChanges(dropdown
						.getSelectedIndex());
			}
		}

		@Override
		public void setLabels() {
			label.setText(app.getLocalization()
					.getMenu(model.getTitle()) + ":");
		}

		@Override
		public void updateFonts() {
			Font font = app.getPlainFont();
			label.setFont(font);
		}

		@Override
		public void setSelectedIndex(int index) {
			dropdown.setSelectedIndex(index);
		}

		@Override
		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			dropdown.removeActionListener(this);
			model.updateProperties();
			dropdown.addActionListener(this);
			return this;
		}

		@Override
		public void addItem(String item) {
			// nothing to do here
		}

		@Override
		public void clearItems() {
			// nothing to do here
		}
	}

	private class DecoAnglePanel extends JPanel
			implements ActionListener, SetLabels, UpdateFonts,
			UpdateablePropertiesPanel, IComboListener {
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
		public void setSelectedIndex(int index) {
			decoCombo.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// not supported
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

			super(app, PropertiesPanelD.this, new RightAngleModel(null, app));
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
 * Panel for setting text alignment.
 */
class TextFieldAlignmentPanel extends JPanel
		implements ActionListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, IComboListener {

	private TextFieldAlignmentModel model;

	private JLabel label;
	private JComboBox<String> comboBox;

	private AppD app;
	private LocalizationD loc;

	/**
	 * Creates a new TextFieldAlignmentPanel instance.
	 *
	 * @param app app
	 */
	TextFieldAlignmentPanel(AppD app) {
		this.app = app;
		loc = app.getLocalization();

		createModel();
		createPanel();
		setLabels();
	}

	private void createModel() {
		model = new TextFieldAlignmentModel(app);
		model.setListener(this);
	}

	private void createPanel() {
		label = new JLabel();
		comboBox = new JComboBox<>();
		comboBox.addActionListener(this);

		add(label);
		add(comboBox);
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	@Override
	public void setLabels() {
		label.setText(loc.getMenu("stylebar.Align") + ":");

		comboBox.removeActionListener(this);
		model.fillModes(loc);
		comboBox.addActionListener(this);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		comboBox.removeActionListener(this);

		model.updateProperties();

		comboBox.addActionListener(this);
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.applyChanges(comboBox.getSelectedIndex());
		updatePanel(model.getGeos());
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		setFont(font);
		comboBox.setFont(font);
		label.setFont(font);
	}

	@Override
	public void setSelectedIndex(int index) {
		comboBox.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		comboBox.addItem(item);
	}

	@Override
	public void clearItems() {
		comboBox.removeAllItems();
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
	private JTextField tfRed;
	private JTextField tfGreen;
	private JTextField tfBlue;
	private JTextField tfAlpha;
	private JButton btRemove;
	private JLabel nameLabelR;
	private JLabel nameLabelG;
	private JLabel nameLabelB;
	private JLabel nameLabelA;

	private JComboBox cbColorSpace;
	private int colorSpace = GeoElement.COLORSPACE_RGB;
	// flag to prevent unneeded relabeling of the colorSpace comboBox
	private boolean allowSetComboBoxLabels = true;

	private String defaultR = "0";
	private String defaultG = "0";
	private String defaultB = "0";
	private String defaultA = "1";

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
		InputPanelD inputPanelR = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelG = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelB = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelA = new InputPanelD(null, app, -1, true, false);
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
		btRemove.addActionListener(e -> model.removeAll());

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

	private JCheckBox cbGraphicsView;
	private JCheckBox cbGraphicsView2;
	private JCheckBox cbGraphicsView3D;
	private JCheckBox cbGraphicsViewForPlane;

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