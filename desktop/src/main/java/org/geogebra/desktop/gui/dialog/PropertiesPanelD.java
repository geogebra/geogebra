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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

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
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel.IDecoAngleListener;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel.IGraphicsViewLocationListener;
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
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.dialog.options.model.TooltipModel;
import org.geogebra.common.gui.dialog.options.model.TraceModel;
import org.geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.GeoGebraComboBoxEditor;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.gui.properties.AnimationSpeedPanel;
import org.geogebra.desktop.gui.properties.AnimationStepPanel;
import org.geogebra.desktop.gui.properties.SliderPanel;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.GeoGebraIcon;
import org.geogebra.desktop.gui.util.PopupMenuButton;
import org.geogebra.desktop.gui.util.SelectionTable;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.view.spreadsheet.MyTableD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * PropertiesPanel for displaying all gui elements for changing properties of
 * currently selected GeoElements.
 * 
 * @see #update(Graphics) PropertiesPanel
 * @author Markus Hohenwarter
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class PropertiesPanelD extends JPanel implements SetLabels, UpdateFonts {

	AppD app;
	LocalizationD loc;
	private Kernel kernel;
	GeoGebraColorChooser colChooser;

	private static final long serialVersionUID = 1L;
	private NamePanel namePanel;
	private ShowObjectPanel showObjectPanel;
	private SelectionAllowedPanel selectionAllowed;
	private ShowTrimmedIntersectionLines showTrimmedIntersectionLines;
	private ColorPanel colorPanel;
	private LabelPanel labelPanel;
	private TooltipPanel tooltipPanel;
	private LayerPanel layerPanel; // Michael Borcherds 2008-02-26
	private CoordsPanel coordPanel;
	private LineEqnPanel lineEqnPanel;
	private ConicEqnPanel conicEqnPanel;
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel; // Florian Sonner 2008-07-17
	private TextOptionsPanel textOptionsPanel;
	private ArcSizePanel arcSizePanel;
	private LineStylePanel lineStylePanel;
	private LineStyleHiddenPanel lineStylePanelHidden;
	// added by Loic BEGIN
	private DecoSegmentPanel decoSegmentPanel;
	private DecoAnglePanel decoAnglePanel;
	private RightAnglePanel rightAnglePanel;
	// END

	FillingPanel fillingPanel;
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
	private SliderPanel sliderPanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private StartPointPanel startPointPanel;
	private CornerPointsPanel cornerPointsPanel;
	private TextEditPanel textEditPanel;
	private ScriptEditPanel scriptEditPanel;
	private BackgroundImagePanel bgImagePanel;
	private AbsoluteScreenLocationPanel absScreenLocPanel;
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

	private String localize(final String id) {
		String txt = loc.getPlain(id);
		if (txt.equals(id)) {
			txt = loc.getMenu(id);
		}
		return txt;
	}

	/**
	 * @param app
	 * @param colChooser
	 * @param isDefaults
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
			layerPanel = new LayerPanel(); // Michael Borcherds 2008-02-26
			animatingPanel = new AnimatingPanel();
			scriptEditPanel = new ScriptEditPanel();
			textEditPanel = new TextEditPanel();
			startPointPanel = new StartPointPanel();
			cornerPointsPanel = new CornerPointsPanel();
			bgImagePanel = new BackgroundImagePanel();
			showConditionPanel = new ShowConditionPanel(app, this);
			colorFunctionPanel = new ColorFunctionPanel(app, this);

			graphicsViewLocationPanel = new GraphicsViewLocationPanel(app, this);
		}

		allowReflexAnglePanel = new AllowReflexAnglePanel();

		sliderPanel = new SliderPanel(app, this, false, true);
		showObjectPanel = new ShowObjectPanel();
		selectionAllowed = new SelectionAllowedPanel();
		showTrimmedIntersectionLines = new ShowTrimmedIntersectionLines();
		colorPanel = new ColorPanel(this, colChooser);
		coordPanel = new CoordsPanel();
		lineEqnPanel = new LineEqnPanel();
		conicEqnPanel = new ConicEqnPanel();
		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel(); // Florian Sonner 2008-07-12
		ineqStylePanel = new IneqPanel();
		textOptionsPanel = new TextOptionsPanel();
		arcSizePanel = new ArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		lineStylePanel = new LineStylePanel();
		lineStylePanelHidden = new LineStyleHiddenPanel();
		// added by Loic BEGIN
		decoSegmentPanel = new DecoSegmentPanel();
		decoAnglePanel = new DecoAnglePanel();
		rightAnglePanel = new RightAnglePanel();
		// END
		fillingPanel = new FillingPanel();
		fadingPanel = new FadingPanel();
		lodPanel = new LodPanel();
		checkBoxInterpolateImage = new CheckBoxInterpolateImage();
		tracePanel = new TracePanel();
		animatingPanel = new AnimatingPanel();
		fixPanel = new FixPanel();
		checkBoxFixPanel = new CheckBoxFixPanel();
		absScreenLocPanel = new AbsoluteScreenLocationPanel();
		comboBoxPanel = new ListsAsComboBoxPanel();
		// showView2D = new ShowView2D();
		auxPanel = new AuxiliaryObjectPanel();
		animStepPanel = new AnimationStepPanel(app);
		textFieldSizePanel = new TextfieldSizePanel(app);
		animSpeedPanel = new AnimationSpeedPanel(app);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		buttonSizePanel = new ButtonSizePanel(app, loc);
		// tabbed pane for properties
		tabs = new JTabbedPane();
		initTabs();

		tabs.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				applyModifications();

			}
		});

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	/**
	 * apply tabs modifications (text edit panel, etc.)
	 */
	public void applyModifications() {
		if (textEditPanel != null)
			textEditPanel.applyModifications();
		if (scriptEditPanel != null)
			scriptEditPanel.applyModifications();
	}

	/**
	 * @param geo
	 *            GeoText to be updated
	 */
	public void updateTextEditor(GeoElement geo) {
		GeoElement[] geos = { geo };
		textEditPanel.updatePanel(geos);
	}

	public void showSliderTab() {
		App.debug("showSliderTab()");
		tabs.setSelectedIndex(1);
	}

	// added by Loic BEGIN
	public void setSliderMinValue() {
		arcSizePanel.setMinValue();
	}

	// END

	/**
	 * A list of the tab panels
	 */
	private ArrayList<TabPanel> tabPanelList;

	/**
	 * Initialize the tabs
	 */
	private void initTabs() {
		tabPanelList = new ArrayList<TabPanel>();

		// basic tab
		ArrayList<JPanel> basicTabList = new ArrayList<JPanel>();

		if (!isDefaults)
			basicTabList.add(namePanel);

		basicTabList.add(showObjectPanel);

		if (!isDefaults)
			basicTabList.add(labelPanel);

		basicTabList.add(tracePanel);

		if (!isDefaults)
			basicTabList.add(animatingPanel);

		basicTabList.add(fixPanel);
		basicTabList.add(auxPanel);
		basicTabList.add(checkBoxFixPanel);

		if (!isDefaults)
			basicTabList.add(bgImagePanel);

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
		ArrayList<JPanel> textTabList = new ArrayList<JPanel>();
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
			ArrayList<JPanel> sliderTabList = new ArrayList<JPanel>();
			sliderTabList.add(sliderPanel);
			sliderTab = new TabPanel(sliderTabList);
			tabPanelList.add(sliderTab);
		}

		// color tab
		ArrayList<JPanel> colorTabList = new ArrayList<JPanel>();
		colorTabList.add(colorPanel);
		colorTab = new TabPanel(colorTabList);
		tabPanelList.add(colorTab);

		// style tab
		ArrayList<JPanel> styleTabList = new ArrayList<JPanel>();
		styleTabList.add(slopeTriangleSizePanel);
		styleTabList.add(pointSizePanel);
		styleTabList.add(pointStylePanel);
		styleTabList.add(lineStylePanel);
		styleTabList.add(ineqStylePanel);
		styleTabList.add(arcSizePanel);
		styleTabList.add(buttonSizePanel);
		styleTabList.add(fillingPanel);
		styleTabList.add(fadingPanel);
		styleTabList.add(lodPanel);
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
		// TabPanel fillingTab = new TabPanel(app.getPlain("Filling"),
		// fillingTabList);
		// fillingTab.addToTabbedPane(tabs);

		// position
		if (!isDefaults) {
			ArrayList<JPanel> positionTabList = new ArrayList<JPanel>();

			positionTabList.add(startPointPanel);
			positionTabList.add(cornerPointsPanel);

			positionTabList.add(absScreenLocPanel);

			positionTab = new TabPanel(positionTabList);
			tabPanelList.add(positionTab);

		}

		// algebra tab
		ArrayList<JPanel> algebraTabList = new ArrayList<JPanel>();
		algebraTabList.add(coordPanel);
		algebraTabList.add(lineEqnPanel);
		algebraTabList.add(conicEqnPanel);
		algebraTabList.add(animStepPanel);
		algebraTabList.add(animSpeedPanel);
		algebraTab = new TabPanel(algebraTabList);
		tabPanelList.add(algebraTab);

		// advanced tab
		if (!isDefaults) {
			ArrayList<JPanel> advancedTabList = new ArrayList<JPanel>();

			advancedTabList.add(showConditionPanel);
			advancedTabList.add(colorFunctionPanel);

			// advancedTabList.add(coordinateFunctionPanel);
			advancedTabList.add(layerPanel); // Michael Borcherds 2008-02-26

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
			ArrayList<JPanel> scriptTabList = new ArrayList<JPanel>();
			// scriptTabList.add(scriptOptionsPanel);

			scriptTabList.add(scriptEditPanel);

			scriptTab = new TabPanel(scriptTabList);
			tabPanelList.add(scriptTab);
		}

		setLabels();
	}

	/**
	 * Update the labels of this panel in case the user language was changed.
	 */
	public void setLabels() {

		// update labels of tabs
		// TODO change label for script tab
		basicTab.setTitle(app.getMenu("Properties.Basic"));
		colorTab.setTitle(app.getPlain("Color"));
		styleTab.setTitle(app.getMenu("Properties.Style"));
		textTab.setTitle(app.getPlain("Text"));
		algebraTab.setTitle(app.getMenu("Properties.Algebra"));
		sliderTab.setTitle(app.getPlain("Slider"));

		if (!isDefaults) {
			positionTab.setTitle(app.getMenu("Properties.Position"));
			scriptTab.setTitle(app.getPlain("Scripting"));
			advancedTab.setTitle(app.getMenu("Advanced"));
		}

		// update the labels of the panels
		showObjectPanel.setLabels();
		selectionAllowed.setLabels();
		showTrimmedIntersectionLines.setLabels();
		colChooser.setLabels();
		colorPanel.setLabels();
		coordPanel.setLabels();
		lineEqnPanel.setLabels();
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
		} else
			tabs.setVisible(false);
	}

	private static boolean updateTabPanel(TabPanel tabPanel,
			ArrayList<JPanel> tabList, Object[] geos) {
		// update all panels and their visibility
		boolean oneVisible = false;
		int size = tabList.size();
		for (int i = 0; i < size; i++) {
			UpdateablePropertiesPanel up = (UpdateablePropertiesPanel) tabList
					.get(i);
			boolean show = (up.updatePanel(geos) != null);
			up.setVisible(show);
			if (show)
				oneVisible = true;
		}

		return oneVisible;
	}

	public void updateSelection(Object[] geos) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		updateTabs(geos);
	}

	public void updateVisualStyle(GeoElement geo) {

		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			if (tp != null)
				tp.updateVisualStyle(geo);
		}

	}

	public void updateSelection(GeoElement geo) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		updateSelection(new GeoElement[] { geo });
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

	};

	private class CheckboxPanel extends OptionPanel implements
			IBooleanOptionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BooleanOptionModel model;
		private JCheckBox checkbox;
		private String title;

		public CheckboxPanel(final String title) {
			this.title = title;
			checkbox = new JCheckBox();
			checkbox.addItemListener(this);
			add(checkbox);
		}

		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			checkbox.removeItemListener(this);

			model.updateProperties();
			// set object visible checkbox

			checkbox.addItemListener(this);
			return this;
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			checkbox.setFont(font);

		}

		public void setLabels() {
			checkbox.setText(localize(title));
			app.setComponentOrientation(this);

		}

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
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, IComboListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel label;
		protected JComboBox comboBox;
		private MultipleOptionsModel model;
		private String title;

		public ComboPanel(final String title) {
			this.setModel(model);
			this.setTitle(title);
			label = new JLabel();
			comboBox = new JComboBox();

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(comboBox);
		}

		public void setLabels() {
			label.setText(localize(getTitle()) + ":");

			int selectedIndex = comboBox.getSelectedIndex();
			comboBox.removeActionListener(this);

			comboBox.removeAllItems();
			getModel().fillModes(loc);
			comboBox.setSelectedIndex(selectedIndex);
			comboBox.addActionListener(this);
		}

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

		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
			comboBox.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void setSelectedIndex(int index) {
			comboBox.setSelectedIndex(index);

		}

		public void addItem(String item) {
			comboBox.addItem(item);

		}

		/**
		 * action listener implementation for label mode combobox
		 */
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

		public void setSelectedItem(String item) {
			comboBox.setSelectedItem(item);
		}

	}

	private class TabPanel extends JPanel {

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

		public void setTitle(String title) {
			this.title = title;
		}

		public void update(Object[] geos) {
			makeVisible = updateTabPanel(this, panelList, geos);
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
	private class ShowObjectPanel extends CheckboxPanel implements
			IShowObjectListener {

		private static final long serialVersionUID = 1L;

		public ShowObjectPanel() {
			super("ShowObject");
			setModel(new ShowObjectModel(this));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}

		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setSelected(value);
			getCheckbox().setEnabled(isEnabled);
		}

	}

	private class SelectionAllowedPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;

		public SelectionAllowedPanel() {
			super("SelectionAllowed");
			setModel(new SelectionAllowedModel(this));
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
			setModel(new TrimmedIntersectionLinesModel(this));
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
			setModel(new FixCheckboxModel(this));
			app.setFlowLayoutOrientation(this);
		}

	} // CheckBoxFixPanel

	private class IneqPanel extends CheckboxPanel implements IIneqStyleListener {

		private static final long serialVersionUID = 1L;

		public IneqPanel() {
			super("ShowOnXAxis");
			IneqStyleModel model = new IneqStyleModel();
			model.setListener(this);
			setModel(model);
			app.setFlowLayoutOrientation(this);
		}

		public void enableFilling(boolean value) {
			fillingPanel.setAllEnabled(value);
		}

		@Override
		public void apply(boolean value) {
			super.apply(value);
			enableFilling(value);
		}

	} // IneqPanel

	/**
	 * panel with label properties
	 */
	private class LabelPanel extends JPanel implements ItemListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
			IShowLabelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox showLabelCB;
		private JComboBox labelModeCB;
		private ShowLabelModel model;

		public LabelPanel() {
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

		public void setLabels() {
			showLabelCB.setText(app.getPlain("ShowLabel") + ":");

			int selectedIndex = labelModeCB.getSelectedIndex();
			labelModeCB.removeActionListener(this);

			labelModeCB.removeAllItems();
			labelModeCB.addItem(app.getPlain("Name")); // index 0
			labelModeCB.addItem(app.getPlain("NameAndValue")); // index 1
			labelModeCB.addItem(app.getPlain("Value")); // index 2
			labelModeCB.addItem(app.getPlain("Caption")); // index 3 Michael
			// Borcherds

			labelModeCB.setSelectedIndex(selectedIndex);
			labelModeCB.addActionListener(this);

			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			updateShowLabel();

			app.setComponentOrientation(this);
		}

		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (model.getGeos() == null)
				return;
			update();
		}

		public JPanel update() {
			if (!model.checkGeos())
				return null;

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
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == labelModeCB) {
				model.applyModeChanges(labelModeCB.getSelectedIndex());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showLabelCB.setFont(font);
			labelModeCB.setFont(font);

		}

		private void updateShowLabel() {
			if (!model.isNameValueShown()) {
				showLabelCB.setText(app.getPlain("ShowLabel"));
			} else {
				showLabelCB.setText(app.getPlain("ShowLabel") + ":");
			}

		}

		public void update(boolean isEqualVal, boolean isEqualMode) {
			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			updateShowLabel();

			GeoElement geo0 = model.getGeoAt(0);
			// set label visible checkbox
			if (isEqualVal) {
				showLabelCB.setSelected(geo0.isLabelVisible());
				labelModeCB.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setSelected(false);
				labelModeCB.setEnabled(false);
			}

			// set label visible checkbox
			if (isEqualMode)
				labelModeCB.setSelectedIndex(Math.min(geo0.getLabelMode(), 3));
			else
				labelModeCB.setSelectedItem(null);

			// locus in selection
			labelModeCB.setVisible(model.isNameValueShown());

		}

	} // LabelPanel

	private class TooltipPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public TooltipPanel() {
			super("Tooltip");
			TooltipModel model = new TooltipModel();
			model.setListener(this);
			setModel(model);
		}
	} // TooltipPanel

	private class LayerPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public LayerPanel() {
			super("Layer");
			LayerModel model = new LayerModel();
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
			setModel(new TraceModel(this));
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
			InterpolateImageModel model = new InterpolateImageModel();
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
			setModel(new FixObjectModel(this));
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

		public AbsoluteScreenLocationPanel() {
			super("AbsoluteScreenLocation");
			setModel(new AbsoluteScreenLocationModel(app, this));
			app.setFlowLayoutOrientation(this);
		}

	}

	/**
	 * panel to set whether GeoLists are drawn as ComboBoxes
	 * 
	 * @author Michael
	 */
	private class ListsAsComboBoxPanel extends CheckboxPanel implements
			IListAsComboListener {
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
	private class AllowReflexAnglePanel extends JPanel implements
			ActionListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel,
			IReflexAngleListener {
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

		public void setLabels() {
			intervalLabel.setText(app.getPlain("AngleBetween"));

			intervalCombo.removeActionListener(this);
			setComboLabels();
			intervalCombo.addActionListener(this);
		}

		public void setComboLabels() {
			int idx = intervalCombo.getSelectedIndex();
			intervalCombo.removeAllItems();

			model.fillModes(loc);
			intervalCombo.setSelectedIndex(idx);
		}

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

		public void updateFonts() {
			Font font = app.getPlainFont();

			intervalLabel.setFont(font);
			intervalCombo.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void addItem(String item) {
			intervalCombo.addItem(item);
		}

		public void setSelectedItem(String item) {
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
		private static final long serialVersionUID = 1L;;

		public AllowOutlyingIntersectionsPanel() {
			super("allowOutlyingIntersections");
			setModel(new OutlyingIntersectionsModel(this));
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
			setModel(new BackgroundImageModel(this));
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
			setModel(new AuxObjectModel(this));
			app.setFlowLayoutOrientation(this);
		}

	}

	/**
	 * panel for location of vectors and text
	 */
	private class StartPointPanel extends JPanel implements ActionListener,
			FocusListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel,
			IComboListener {
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

		public void setLabels() {
			label.setText(app.getPlain("StartingPoint") + ": ");
		}

		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}
		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

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
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbLocation)
				doActionPerformed();
		}

		private void doActionPerformed() {
			String strLoc = (String) cbLocation.getSelectedItem();
			model.applyChanges(strLoc);
			updateSelection(model.getGeos());
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed();
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void setSelectedIndex(int index) {
			if (index == 0) {
				GeoElement p = (GeoElement) model.getLocateableAt(0)
						.getStartPoint();
				cbLocation.setSelectedItem(p
						.getLabel(StringTemplate.editTemplate));
			} else {
				cbLocation.setSelectedItem(null);
			}
		}

		public void addItem(String item) {
			cbModel.addElement(item);

		}

		public void setSelectedItem(String item) {
			cbLocation.setSelectedItem(item);

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
			getLabel().setIcon(
					app.getScaledIcon("corner" + model.getCornerNumber()
							+ ".png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == comboBox) {
				model.applyChanges((String) comboBox.getSelectedItem());
			}
		}

		@Override
		public void setLabels() {
			super.setLabels();
			String strLabelStart = app.getPlain("CornerPoint");
			getLabel().setText(strLabelStart + model.getCornerNumber() + ":");

		}

	}

	private class CornerPointsPanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts /**
	 * 
	 */
	{
		private static final long serialVersionUID = 1L;

		private ImageCornerPanel corner0;
		private ImageCornerPanel corner1;
		private ImageCornerPanel corner2;

		public CornerPointsPanel() {
			corner0 = new ImageCornerPanel(0);
			corner1 = new ImageCornerPanel(1);
			corner2 = new ImageCornerPanel(2);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(corner0);
			add(corner1);
			add(corner2);

		}

		public void updateFonts() {
			corner0.updateFonts();
			corner1.updateFonts();
			corner2.updateFonts();
		}

		public void setLabels() {
			corner0.setLabels();
			corner1.setLabels();
			corner2.setLabels();
		}

		public JPanel updatePanel(Object[] geos) {
			if (geos == null || corner0.updatePanel(geos) == null) {
				return null;
			}

			corner1.updatePanel(geos);
			corner2.updatePanel(geos);
			return this;
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel for text editingA
	 */
	public class TextEditPanel extends JPanel implements ActionListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private TextInputDialog td;
		private JPanel editPanel;

		public TextEditPanel() {
			td = new TextInputDialog(app, app.getPlain("Text"), null, null,
					true, 30,
					5, false);
			setLayout(new BorderLayout());

			editPanel = new JPanel(new BorderLayout(0, 0));
			editPanel.add(td.getInputPanel(), BorderLayout.CENTER);
			editPanel.add(td.getToolBar(), BorderLayout.SOUTH);
			editPanel.setBorder(BorderFactory.createEtchedBorder());

			JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					editPanel, td.getPreviewPanel());
			sp.setResizeWeight(0.5);
			sp.setBorder(BorderFactory.createEmptyBorder());

			add(sp, BorderLayout.CENTER);
			// add(td.getPreviewPanel(), BorderLayout.NORTH);
			add(td.getButtonPanel(), BorderLayout.SOUTH);

		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			td.applyModifications();
		}

		public void setLabels() {
			// editPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Edit")));
			// td.getPreviewPanel().setBorder(BorderFactory.createTitledBorder(app.getMenu("Preview")));
			td.setLabels(app.getPlain("Text"));
		}

		public JPanel updatePanel(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos)) {
				td.reset();
				return null;
			}

			GeoText text = (GeoText) geos[0];
			td.setGeoText(text);
			td.updateRecentSymbolTable();

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			return geos.length == 1 && geos[0] instanceof GeoText
					&& !((GeoText) geos[0]).isTextCommand()
					&& !((GeoText) geos[0]).isFixed();
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			// if (e.getSource() == btEdit)
			// app.showTextDialog((GeoText) geos[0]);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			editPanel.setFont(font);
			td.updateFonts();
		}

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

			clickDialog = new ScriptInputDialog(app, app.getPlain("Script"),
					null, row, column, false, false);
			updateDialog = new ScriptInputDialog(app,
					app.getPlain("JavaScript"), null, row, column, true, false);
			globalDialog = new ScriptInputDialog(app,
					app.getPlain("GlobalJavaScript"), null, row, column, false,
					true);
			setLayout(new BorderLayout());
			// add(td.getInputPanel(), BorderLayout.NORTH);
			// add(td2.getInputPanel(), BorderLayout.CENTER);
			clickScriptPanel = new JPanel(new BorderLayout(0, 0));
			clickScriptPanel.add(clickDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			clickScriptPanel
					.add(clickDialog.getButtonPanel(), loc.borderEast());

			updateScriptPanel = new JPanel(new BorderLayout(0, 0));
			updateScriptPanel.add(
					updateDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			updateScriptPanel.add(updateDialog.getButtonPanel(),
					loc.borderEast());

			globalScriptPanel = new JPanel(new BorderLayout(0, 0));
			globalScriptPanel.add(
					globalDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			globalScriptPanel.add(globalDialog.getButtonPanel(),
					loc.borderEast());

			add(tabbedPane, BorderLayout.CENTER);

			tabbedPane.addChangeListener(new ChangeListener() {

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

		public void setLabels() {
			// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
			clickDialog.setLabels(app.getPlain("OnClick"));
			updateDialog.setLabels(app.getPlain("OnUpdate"));
			globalDialog.setLabels(app.getPlain("GlobalJavaScript"));
		}

		public JPanel updatePanel(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos))
				return null;

			// remember selected tab
			Component selectedTab = tabbedPane.getSelectedComponent();

			GeoElement button = (GeoElement) geos[0];
			clickDialog.setGeo(button);
			updateDialog.setGeo(button);
			globalDialog.setGlobal();
			tabbedPane.removeAll();
			if (button.canHaveClickScript())
				tabbedPane.addTab(app.getPlain("OnClick"), clickScriptPanel);
			if (button.canHaveUpdateScript())
				tabbedPane.addTab(app.getPlain("OnUpdate"), updateScriptPanel);
			tabbedPane.addTab(app.getPlain("GlobalJavaScript"),
					globalScriptPanel);

			// select tab as before
			tabbedPane.setSelectedIndex(Math.max(0,
					tabbedPane.indexOfComponent(selectedTab)));

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
		public void actionPerformed(ActionEvent e) {
			// if (e.getSource() == btEdit)
			// app.showTextDialog((GeoText) geos[0]);
		}

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
			CoordsModel model = new CoordsModel();
			model.setListener(this);
			setModel(model);
		}
	} // CoordsPanel

	private class LineEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public LineEqnPanel() {
			super("Equation");
			LineEqnModel model = new LineEqnModel();
			model.setListener(this);
			setModel(model);
		}
	} // LineEqnPanel

	private class ConicEqnPanel extends ComboPanel {
		private static final long serialVersionUID = 1L;

		public ConicEqnPanel() {
			super("Equation");
			ConicEqnModel model = new ConicEqnModel(loc);
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

			// setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");

			model = new PointSizeModel();
			model.setListener(this);

			slider = new JSlider(1, 9);
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

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getPlain("PointSize")));
		}

		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

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
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

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
	private class PointStylePanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, ActionListener,
			IComboListener {
		private static final long serialVersionUID = 1L;
		private PointStyleModel model;
		private JComboBox cbStyle; // G.Sturr 2010-1-24

		public PointStylePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			model = new PointStyleModel();
			model.setListener(this);
			// G.STURR 2010-1-24
			// Point styles were previously displayed with fonts,
			// but not all point styles had font equivalents. This is
			// now replaced by a comboBox holding rendered point styles
			// and radio buttons to select default or custom point style.

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18, 18));
			cbStyle = new JComboBox(EuclidianViewD.getPointStyles());
			cbStyle.setRenderer(renderer);
			cbStyle.setMaximumRowCount(EuclidianStyleConstants.MAX_POINT_STYLE + 1);
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

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getMenu("PointStyle")));
		}

		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (!model.hasGeos())
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!model.checkGeos())
				return null;

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

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);
		}

		public void setSelectedIndex(int index) {
			cbStyle.setSelectedIndex(index);
		}

		public void addItem(String item) {
			// TODO Auto-generated method stub

		}

		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * panel to select the size of a GeoText
	 * 
	 * @author Markus Hohenwarter
	 */
	private class TextOptionsPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, FocusListener,
			ITextOptionsListener {
		private static final long serialVersionUID = 1L;
		private TextOptionsModel model;

		private JLabel decimalLabel;
		private JComboBox cbFont, cbSize, cbDecimalPlaces;
		private JToggleButton btBold, btItalic;

		private JPanel secondLine;
		private boolean secondLineVisible = false;
		private TextEditPanel editPanel;

		public TextOptionsPanel() {

			model = new TextOptionsModel(app);
			model.setListener(this);

			cbFont = new JComboBox(model.getFonts());
			cbFont.addActionListener(this);

			// font size
			// TODO require font phrases F.S.
			cbSize = new JComboBox(model.getFontSizes());
			cbSize.addActionListener(this);
			cbFont.addFocusListener(this);
			// toggle buttons for bold and italic
			btBold = new JToggleButton();
			btBold.setFont(app.getBoldFont());
			btBold.addActionListener(this);
			btItalic = new JToggleButton();
			btItalic.setFont(app.getPlainFont().deriveFont(Font.ITALIC));
			btItalic.addActionListener(this);

			// decimal places
			ComboBoxRenderer renderer = new ComboBoxRenderer();
			cbDecimalPlaces = new JComboBox(loc.getRoundingMenu());
			cbDecimalPlaces.setRenderer(renderer);
			cbDecimalPlaces.addActionListener(this);

			// font, size
			JPanel firstLine = new JPanel();
			firstLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

			firstLine.add(cbFont);
			firstLine.add(cbSize);
			firstLine.add(btBold);
			firstLine.add(btItalic);

			// bold, italic
			secondLine = new JPanel();
			secondLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
			decimalLabel = new JLabel();
			secondLine.add(decimalLabel);
			secondLine.add(cbDecimalPlaces);

			setLayout(new BorderLayout(5, 5));
			add(firstLine, BorderLayout.NORTH);
			add(secondLine, BorderLayout.SOUTH);
			secondLineVisible = true;
		}

		public void setEditPanel(TextEditPanel tep) {
			this.editPanel = tep;
		}

		public void setLabels() {
			String[] fontSizes = app.getLocalization().getFontSizeStrings();

			int selectedIndex = cbSize.getSelectedIndex();
			cbSize.removeActionListener(this);
			cbSize.removeAllItems();

			for (int i = 0; i < fontSizes.length; ++i) {
				cbSize.addItem(fontSizes[i]);
			}

			cbSize.addItem(app.getMenu("Custom") + "...");

			cbSize.setSelectedIndex(selectedIndex);
			cbSize.addActionListener(this);

			btItalic.setText(app.getPlain("Italic").substring(0, 1));
			btBold.setText(app.getPlain("Bold").substring(0, 1));

			decimalLabel.setText(app.getMenu("Rounding") + ":");
		}

		public void setFontSizeVisibleOnly() {
			cbSize.setVisible(true);
			cbFont.setVisible(false);
			btBold.setVisible(false);
			btItalic.setVisible(false);
			secondLine.setVisible(false);
		}

		class ComboBoxRenderer extends JLabel implements ListCellRenderer {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			JSeparator separator;

			public ComboBoxRenderer() {
				setOpaque(true);
				setBorder(new EmptyBorder(1, 1, 1, 1));
				separator = new JSeparator(SwingConstants.HORIZONTAL);
			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String str = (value == null) ? "" : value.toString();
				if ("---".equals(str)) {
					return separator;
				}
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				setFont(list.getFont());
				setText(str);
				return this;
			}
		}

		public JPanel updatePanel(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (!model.hasGeos()) {
				return;
			}
			update();
		}

		public JPanel update() {
			// check geos
			if (!model.checkGeos()) {
				model.cancelEditGeo();
				return null;
			}

			cbSize.removeActionListener(this);
			cbFont.removeActionListener(this);
			cbDecimalPlaces.removeActionListener(this);

			model.updateProperties();

			cbSize.addActionListener(this);
			cbFont.addActionListener(this);
			cbDecimalPlaces.addActionListener(this);
			return this;
		}

		/**
		 * change listener implementation for slider
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == cbSize) {
				boolean isCustom = (cbSize.getSelectedIndex() == 7);
				if (isCustom) {
					final String percentStr = JOptionPane.showInputDialog(
							app.getFrame(),
							app.getPlain("EnterPercentage"),
							Math.round(model.getTextPropertiesAt(0)
									.getFontSizeMultiplier() * 100) + "%");

					model.applyFontSizeFromString(percentStr);
				} else {
					model.applyFontSizeFromIndex(cbSize.getSelectedIndex());
				}
			} else if (source == cbFont) {
				model.applyFont(cbFont.getSelectedIndex() == 1);
			} else if (source == cbDecimalPlaces) {
				model.applyDecimalPlaces(cbDecimalPlaces.getSelectedIndex());
			} else if (source == btBold || source == btItalic) {
				model.applyFontStyle(btBold.isSelected(), btItalic.isSelected());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			cbFont.setFont(font);
			cbSize.setFont(font);
			cbDecimalPlaces.setFont(font);

			btItalic.setFont(font);
			btBold.setFont(font);

			decimalLabel.setFont(font);

			editPanel.setFont(font);
		}

		public void focusGained(FocusEvent arg0) {
			cbSize.setSelectedIndex(GeoText.getFontSizeIndex(model
					.getTextPropertiesAt(0).getFontSizeMultiplier()));
		}

		public void focusLost(FocusEvent arg0) {
			model.cancelEditGeo();
		}

		public void setWidgetsVisible(boolean showFontDetails, boolean isButton) {
			// hide most options for Textfields
			cbFont.setVisible(showFontDetails);
			btBold.setVisible(showFontDetails);
			btItalic.setVisible(showFontDetails);
			secondLine.setVisible(showFontDetails);
			secondLineVisible = showFontDetails;

			if (isButton) {
				secondLine.setVisible(!showFontDetails);
				secondLineVisible = !showFontDetails;
			}

			cbFont.setVisible(model.isTextEditable());

		}

		public void selectSize(int index) {
			cbSize.setSelectedIndex(index);

		}

		public void selectFont(int index) {
			cbFont.setSelectedIndex(index);

		}

		public void selectDecimalPlaces(int index) {
			cbDecimalPlaces.setSelectedIndex(index);
		}

		public void setSecondLineVisible(boolean noDecimals) {
			if (noDecimals) {

				if (secondLineVisible) {
					secondLineVisible = false;
				}
			} else {
				if (!secondLineVisible) {
					secondLineVisible = true;
				}

				secondLine.setVisible(secondLineVisible);
			}

		}

		public void updatePreview() {
			if (textEditPanel != null) {
				textEditPanel.td.handleDocumentEvent();
			}
		}

		public void selectFontStyle(int style) {
			btBold.setSelected(style == Font.BOLD
					|| style == (Font.BOLD + Font.ITALIC));
			btItalic.setSelected(style == Font.ITALIC
					|| style == (Font.BOLD + Font.ITALIC));

		}

		public void setEditorText(ArrayList<DynamicTextElement> list) {
			// TODO Auto-generated method stub

		}

		public void setEditorText(String text) {
			// TODO Auto-generated method stub

		}

		public void reinitEditor() {
			// only called in Web

		}
	}

	/**
	 * panel to select the size of a GeoPoint
	 * 
	 * @author Markus Hohenwarter
	 */
	private class SlopeTriangleSizePanel extends JPanel implements
			ChangeListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
			ISliderListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private SlopeTriangleSizeModel model;
		private JSlider slider;

		public SlopeTriangleSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			model = new SlopeTriangleSizeModel();
			model.setListener(this);

			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");
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

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}

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
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

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

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

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
			model = new AngleArcSizeModel();
			model.setListener(this);
			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");
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

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}

		// added by Loic BEGIN
		public void setMinValue() {
			slider.setValue(AngleArcSizeModel.MIN_VALUE);
		}

		// END

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
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				model.applyChanges(slider.getValue());
			}
		}

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

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void setValue(int value) {
			slider.setValue(value);

		}
	}

	/**
	 * panel to select the filling of a polygon or conic section
	 * 
	 * @author Markus Hohenwarter
	 */
	class FillingPanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, ActionListener,
			IFillingListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private FillingModel model;

		JSlider opacitySlider;
		private JSlider angleSlider;
		private JSlider distanceSlider;
		private JComboBox cbFillType;
		private JCheckBox cbFillInverse;

		private JPanel transparencyPanel, hatchFillPanel, imagePanel,
				anglePanel, distancePanel;
		private JLabel lblFillType;
		private JLabel lblSelectedSymbol;
		private JLabel lblMsgSelected;
		private JButton btnOpenFile;

		private PopupMenuButton btnImage;
		// button for removing turtle's image
		private JButton btnClearImage;
		private JLabel lblFillInverse;
		private JLabel lblSymbols;
		private ArrayList<String> imgFileNameList;
		private PopupMenuButton btInsertUnicode;

		// For handle single bar
		private JPanel barsPanel;
		private JToggleButton[] selectionBarButtons;
		private int selectedBarButton;

		public FillingPanel() {

			// For filling whit unicode char
			model = new FillingModel(app);
			model.setListener(this);
			btInsertUnicode = new PopupMenuButton(app);
			buildInsertUnicodeButton();
			btInsertUnicode.addActionListener(this);
			btInsertUnicode.setVisible(false);
			lblMsgSelected = new JLabel(loc.getMenu("Filling.CurrentSymbol")
					+ ":");
			lblMsgSelected.setVisible(false);
			fillingPanel = this;
			lblSymbols = new JLabel(app.getMenu("Filling.Symbol") + ":");
			lblSymbols.setVisible(false);
			lblSelectedSymbol = new JLabel();
			lblSelectedSymbol.setFont(new Font("SansSerif", Font.PLAIN, 24));

			// JLabel sizeLabel = new JLabel(app.getPlain("Filling") + ":");
			opacitySlider = new JSlider(0, 100);
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);
			opacitySlider.setSnapToTicks(true);

			angleSlider = new JSlider(0, 180);
			// angleSlider.setPreferredSize(new Dimension(150,50));
			angleSlider.setMajorTickSpacing(45);
			angleSlider.setMinorTickSpacing(5);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setSnapToTicks(true);

			// Create the label table
			Hashtable<Integer, JLabel> labelHash = new Hashtable<Integer, JLabel>();
			labelHash.put(new Integer(0), new JLabel("0" + Unicode.DEGREE));
			labelHash.put(new Integer(45), new JLabel(
					Unicode.FORTY_FIVE_DEGREES));
			labelHash.put(new Integer(90), new JLabel("90" + Unicode.DEGREE));
			labelHash.put(new Integer(135), new JLabel("135" + Unicode.DEGREE));
			labelHash.put(new Integer(180), new JLabel("180" + Unicode.DEGREE));
			angleSlider.setLabelTable(labelHash);

			distanceSlider = new JSlider(5, 50);
			// distanceSlider.setPreferredSize(new Dimension(150,50));
			distanceSlider.setMajorTickSpacing(10);
			distanceSlider.setMinorTickSpacing(5);
			distanceSlider.setPaintTicks(true);
			distanceSlider.setPaintLabels(true);
			distanceSlider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			// set label font
			Dictionary<?, ?> labelTable = opacitySlider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			labelTable = angleSlider.getLabelTable();
			en = labelTable.elements();
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			labelTable = distanceSlider.getLabelTable();
			en = labelTable.elements();
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			// ========================================
			// create sub panels

			// panel for the fill type combobox
			cbFillType = new JComboBox();
			JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel syPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			lblFillType = new JLabel(app.getPlain("Filling") + ":");
			cbFillInverse = new JCheckBox();
			lblFillInverse = new JLabel(app.getPlain("InverseFilling"));
			cbPanel.add(lblFillType);
			cbPanel.add(cbFillType);
			cbPanel.add(cbFillInverse);
			cbPanel.add(lblFillInverse);
			syPanel.add(lblSymbols);
			syPanel.add(btInsertUnicode);
			syPanel.add(lblMsgSelected);
			lblSelectedSymbol.setAlignmentX(CENTER_ALIGNMENT);
			lblSelectedSymbol.setAlignmentY(CENTER_ALIGNMENT);
			lblSelectedSymbol.setVisible(false);
			syPanel.add(lblSelectedSymbol);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(cbPanel);
			panel.add(syPanel);
			// panels to hold sliders
			transparencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			transparencyPanel.add(opacitySlider);

			anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			anglePanel.add(angleSlider);

			distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			distancePanel.add(distanceSlider);

			// hatchfill panel: only shown when hatch fill option is selected
			hatchFillPanel = new JPanel();
			hatchFillPanel.setLayout(new BoxLayout(hatchFillPanel,
					BoxLayout.X_AXIS));
			hatchFillPanel.add(anglePanel);
			hatchFillPanel.add(distancePanel);
			hatchFillPanel.setVisible(false);

			// image panel: only shown when image fill option is selected
			createImagePanel();
			imagePanel.setVisible(false);

			// ===========================================================
			// put all the sub panels together

			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panel);
			this.add(transparencyPanel);
			this.add(hatchFillPanel);
			this.add(imagePanel);
		}

		public void setAllEnabled(boolean b) {
			Component[] c = this.getComponents();
			for (int i = 0; i < c.length; i++) {
				Component[] subc = ((JPanel) c[i]).getComponents();
				for (int j = 0; j < subc.length; j++) {
					subc[j].setEnabled(b);
				}
			}
		}

		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}
		public void setLabels() {

			// setBorder(BorderFactory.createTitledBorder(app.getPlain("Filling")));

			transparencyPanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Opacity")));
			anglePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Angle")));
			distancePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Spacing")));
			imagePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Images")));

			btnOpenFile.setText(app.getMenu("ChooseFromFile") + "...");

			// fill type combobox
			lblFillType.setText(app.getMenu("Filling") + ":");

			int selectedIndex = cbFillType.getSelectedIndex();
			cbFillType.removeActionListener(this);
			cbFillType.removeAllItems();

			model.fillModes(loc);

			cbFillType.setSelectedIndex(selectedIndex);
			cbFillType.addActionListener(this);

		}

		private JPanel createImagePanel() {

			// =============================================
			// create array of image files from toolbar icons
			// for testing only ...

			imgFileNameList = new ArrayList<String>();
			String imagePath = "/geogebra/gui/images/";

			imgFileNameList.add(""); // for delete
			imgFileNameList.add(imagePath + "go-down.png");
			imgFileNameList.add(imagePath + "go-up.png");
			imgFileNameList.add(imagePath + "go-previous.png");
			imgFileNameList.add(imagePath + "go-next.png");
			imgFileNameList.add(imagePath + "nav_fastforward.png");
			imgFileNameList.add(imagePath + "nav_rewind.png");
			imgFileNameList.add(imagePath + "nav_skipback.png");
			imgFileNameList.add(imagePath + "nav_skipforward.png");
			imgFileNameList.add("/geogebra/main/nav_play.png");
			imgFileNameList.add("/geogebra/main/nav_pause.png");

			imgFileNameList.add(imagePath + "exit.png");

			ImageIcon[] iconArray = new ImageIcon[imgFileNameList.size()];
			iconArray[0] = GeoGebraIcon.createNullSymbolIcon(24, 24);
			for (int i = 1; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createFileImageIcon(app,
						imgFileNameList.get(i), 1.0f, new Dimension(32, 32));
			}
			// ============================================

			// panel for button to open external file

			btnImage = new PopupMenuButton(app, iconArray, -1, 4,
					new Dimension(32, 32),
					org.geogebra.common.gui.util.SelectionTable.MODE_ICON);
			btnImage.setSelectedIndex(1);
			btnImage.setStandardButton(true);
			btnImage.setKeepVisible(false);
			btnImage.addActionListener(this);

			btnClearImage = new JButton(app.getScaledIcon("delete_small.gif"));
			btnClearImage.addActionListener(this);

			btnOpenFile = new JButton();
			btnOpenFile.addActionListener(this);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.add(btnImage);
			btnPanel.add(btnClearImage);
			btnPanel.add(btnOpenFile);

			// =====================================
			// put all sub panels together

			imagePanel = new JPanel(new BorderLayout());
			imagePanel.add(btnPanel, BorderLayout.CENTER);
			return imagePanel;
		}

		public void setStandardFillType() {
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(false);
			imagePanel.setVisible(false);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);
		}

		public void setHatchFillType() {

			distanceSlider.removeChangeListener(this);
			distanceSlider.setMinimum(5);
			distanceSlider.addChangeListener(this);
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			angleSlider.removeChangeListener(this);
			angleSlider.setMaximum(180);
			angleSlider.setMinorTickSpacing(5);
			angleSlider.addChangeListener(this);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);
		}

		public void setCrossHatchedFillType() {
			distanceSlider.removeChangeListener(this);
			distanceSlider.setMinimum(5);
			distanceSlider.addChangeListener(this);
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			// Only at 0, 45 and 90 degrees texturepaint not have mismatches
			angleSlider.removeChangeListener(this);
			angleSlider.setMaximum(45);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.addChangeListener(this);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);

		}

		public void setBrickFillType() {
			distanceSlider.removeChangeListener(this);
			distanceSlider.setMinimum(5);
			distanceSlider.addChangeListener(this);
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			angleSlider.removeChangeListener(this);
			angleSlider.setMaximum(180);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.addChangeListener(this);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);
		}

		public void setSymbolFillType() {
			distanceSlider.removeChangeListener(this);
			distanceSlider.setMinimum(10);
			distanceSlider.addChangeListener(this);
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			// for dotted angle is useless
			anglePanel.setVisible(false);
			lblSymbols.setVisible(true);
			lblSelectedSymbol.setVisible(true);
			btInsertUnicode.setVisible(true);
		}

		public void setDottedFillType() {
			distanceSlider.removeChangeListener(this);
			distanceSlider.setMinimum(5);
			distanceSlider.addChangeListener(this);
			transparencyPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			// for dotted angle is useless
			anglePanel.setVisible(false);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);
		}

		public void setImageFillType() {
			transparencyPanel.setVisible(true);
			hatchFillPanel.setVisible(false);
			imagePanel.setVisible(true);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			btInsertUnicode.setVisible(false);
			this.btnImage.setVisible(true);
			this.btnClearImage.setVisible(false);

			// for GeoButtons only show the image file button
			if (model.hasGeoButton() || model.hasGeoTurtle()) {
				transparencyPanel.setVisible(false);
				lblFillType.setVisible(false);
				cbFillType.setVisible(false);
				if (model.hasGeoTurtle()) {
					this.btnImage.setVisible(false);
					this.btnClearImage.setVisible(true);
				}
			}
		}

		public JPanel update(Object[] geos) {
			// check geos
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			cbFillType.removeActionListener(this);
			cbFillInverse.removeActionListener(this);
			opacitySlider.removeChangeListener(this);
			angleSlider.removeChangeListener(this);
			distanceSlider.removeChangeListener(this);

			model.updateProperties();

			cbFillType.addActionListener(this);
			cbFillInverse.addActionListener(this);
			opacitySlider.addChangeListener(this);
			angleSlider.addChangeListener(this);
			distanceSlider.addChangeListener(this);

			if (model.hasGeoButton()) {
				int index = imgFileNameList.lastIndexOf(model.getGeoAt(0)
						.getImageFileName());

				btnImage.setSelectedIndex(index > 0 ? index : 0);
			} else {
				btnImage.setSelectedIndex(0);
			}
			addSelectionBar();
			return this;
		}

		public void setFillInverseSelected(boolean value) {
			cbFillInverse.setSelected(value);
		}

		public void setFillInverseVisible(boolean isVisible) {
			cbFillInverse.setVisible(isVisible);
			lblFillInverse.setVisible(isVisible);
		}

		public void setFillTypeVisible(boolean isVisible) {
			lblFillType.setVisible(isVisible);
			cbFillType.setVisible(isVisible);
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			// For barchart opacity color and
			// opacity image have same value if there is a tag
			if (e.getSource() == opacitySlider) {
				model.applyOpacity(opacitySlider.getValue());
				kernel.notifyRepaint();
				return;
			}
			if (!angleSlider.getValueIsAdjusting()
					&& !distanceSlider.getValueIsAdjusting()) {
				model.applyAngleAndDistance(angleSlider.getValue(),
						distanceSlider.getValue());
			}
		}

		/**
		 * action listener for fill type combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			// handle change in fill type
			if (source == cbFillType) {
				model.applyFillType(cbFillType.getSelectedIndex());

			} else if (source == cbFillInverse) {

				model.applyFillingInverse(cbFillInverse.isSelected());

			}
			// handle image button selection
			else if (source == this.btnImage) {
				String fileName = null;
				if (btnImage.getSelectedIndex() == 0) {
					fileName = "";
				} else {
					fileName = imgFileNameList.get(btnImage.getSelectedIndex());
				}
				model.applyImage(fileName);
			} else if (source == this.btnClearImage) {
				model.applyImage("");
			}

			// handle load image file
			else if (source == btnOpenFile) {
				String fileName = ((GuiManagerD) app.getGuiManager())
						.getImageFromFile();
				model.applyImage(fileName);

			} else if (source == btInsertUnicode) {
				model.applyUnicode(lblSelectedSymbol.getText());

			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			transparencyPanel.setFont(font);
			anglePanel.setFont(font);
			distancePanel.setFont(font);
			imagePanel.setFont(font);

			btnOpenFile.setFont(font);
			lblFillType.setFont(font);
			cbFillType.setFont(font);

			lblFillInverse.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		private void addSelectionBar() {
			if (barsPanel != null) {
				remove(barsPanel);
			}
			GeoElement geo0 = model.getGeoAt(0);
			AlgoElement algo = geo0.getParentAlgorithm();
			if (algo instanceof AlgoBarChart) {
				int numBar = ((AlgoBarChart) algo).getIntervals();
				model.setBarChart(true);
				selectionBarButtons = new JToggleButton[numBar + 1];
				ButtonGroup group = new ButtonGroup();
				barsPanel = new JPanel(new GridLayout(0, 5, 5, 5));
				barsPanel.setBorder(new TitledBorder(app
						.getPlain("SelectedBar")));
				for (int i = 0; i < numBar + 1; i++) {
					selectionBarButtons[i] = new JToggleButton(loc.getPlain(
							"BarA", i + ""));
					selectionBarButtons[i].setSelected(false);
					selectionBarButtons[i].setActionCommand("" + i);
					selectionBarButtons[i]
							.addActionListener(new ActionListener() {

								public void actionPerformed(ActionEvent arg0) {
									selectedBarButton = Integer
											.parseInt(((JToggleButton) arg0
													.getSource())
													.getActionCommand());
									FillingPanel.this.update(model.getGeos());
								}

							});
					group.add(selectionBarButtons[i]);
					barsPanel.add(selectionBarButtons[i]);
				}
				selectionBarButtons[0].setText(loc.getPlain("AllBars"));
				selectionBarButtons[selectedBarButton].setSelected(true);
				add(barsPanel);
			}
		}

		private void buildInsertUnicodeButton() {
			if (btInsertUnicode != null)
				btInsertUnicode.removeAllMenuItems();

			btInsertUnicode.setKeepVisible(false);
			btInsertUnicode.setStandardButton(true);
			btInsertUnicode.setFixedIcon(GeoGebraIcon
					.createDownTriangleIcon(10));

			JMenu menu = new JMenu(app.getMenu("Properties.Basic"));

			// Suits and music
			String[] fancy = Unicode.getSetOfSymbols(0x2660, 16);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));

			// Chess
			fancy = Unicode.getSetOfSymbols(0x2654, 12);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));

			// Stars
			fancy = Unicode.getSetOfSymbols(0x2725, 3);
			String[] fancy2 = Unicode.getSetOfSymbols(0x2729, 23);
			String[] union = new String[26];
			System.arraycopy(fancy, 0, union, 0, 3);
			System.arraycopy(fancy2, 0, union, 3, 23);
			btInsertUnicode.addPopupMenuItem(createMenuItem(union, -1, 4));

			// Squares
			fancy = Unicode.getSetOfSymbols(0x2b12, 8);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));
			app.setComponentOrientation(menu);

		}

		private JMenu createMenuItem(String[] table, int rows, int columns) {

			StringBuilder sb = new StringBuilder(7);
			sb.append(table[0]);
			sb.append(' ');
			sb.append(table[1]);
			sb.append(' ');
			sb.append(table[2]);
			sb.append("  ");

			JMenu menu = new JMenu(sb.toString());
			menu.add(new LatexTableFill(app, this, btInsertUnicode, table,
					rows, columns,
					org.geogebra.common.gui.util.SelectionTable.MODE_TEXT));

			menu.setFont(GFontD.getAwtFont(app.getFontCanDisplay(sb.toString())));

			return menu;
		}

		class LatexTableFill extends SelectionTable implements MenuElement {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Object[] latexArray;
			private PopupMenuButton popupButton;

			public LatexTableFill(AppD app, FillingPanel panel,
					PopupMenuButton popupButton, Object[] data, int rows,
					int columns, org.geogebra.common.gui.util.SelectionTable mode) {
				super(app, data, rows, columns, new Dimension(24, 24), mode);
				this.latexArray = data;
				this.popupButton = popupButton;
				setHorizontalAlignment(SwingConstants.CENTER);
				setSelectedIndex(0);
				this.setShowGrid(true);
				this.setGridColor(org.geogebra.desktop.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
				this.setBorder(BorderFactory
						.createLineBorder(MyTableD.TABLE_GRID_COLOR));
				this.setShowSelection(false);
			}

			public Component getComponent() {
				return this;
			}

			public MenuElement[] getSubElements() {
				return new MenuElement[0];
			}

			public void menuSelectionChanged(boolean arg0) {
			}

			public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1,
					MenuSelectionManager arg2) {
			}

			public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1,
					MenuSelectionManager arg2) {

				if (this.getSelectedIndex() >= latexArray.length)
					return;

				if (arg0.getID() == MouseEvent.MOUSE_RELEASED) {

					// get the selected string
					App.debug("processMouseEvent, index: "
							+ this.getSelectedIndex());
					String s = (String) latexArray[this.getSelectedIndex()];
					// if LaTeX string, adjust the string to include selected
					// text within braces

					if (s != null) {

						App.debug("processMouseEvent, S: " + s);
						lblSelectedSymbol.setText(s);
						lblSelectedSymbol.setFont(GFontD.getAwtFont(app
								.getFontCanDisplay(s)));
					}
					App.debug("handlePopupActionEvent begin");
					popupButton.handlePopupActionEvent();
					App.debug("handlePopupActionEvent end");
				}
			}
		}

		public void setSelectedIndex(int index) {
			cbFillType.setSelectedIndex(index);
		}

		public void addItem(String item) {
			cbFillType.addItem(item);

		}

		public void setSelectedItem(String item) {
			cbFillType.setSelectedItem(item);
		}

		public void setSymbolsVisible(boolean isVisible) {

			if (isVisible) {
				btInsertUnicode.setVisible(true);
				lblSymbols.setVisible(true);
				lblSelectedSymbol.setVisible(true);
				lblMsgSelected.setVisible(true);
			} else {
				lblSymbols.setVisible(false);
				btInsertUnicode.setVisible(false);
				lblMsgSelected.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				lblSelectedSymbol.setText("");
			}
		}

		public void setFillingImage(String imageFileName) {
			if (imageFileName != null) {
				int idx = imgFileNameList.lastIndexOf(imageFileName);
				btnImage.setSelectedIndex(idx > 0 ? idx : 0);
			} else {
				btnImage.setSelectedIndex(-1);
			}
		}

		public void setFillValue(int value) {
			opacitySlider.setValue(value);
		}

		public void setAngleValue(int value) {
			angleSlider.setValue(value);
		}

		public void setDistanceValue(int value) {
			distanceSlider.setValue(value);
		}

		public int getSelectedBarIndex() {
			return selectedBarButton;
		}

		public void selectSymbol(String symbol) {
			lblSelectedSymbol.setText(symbol);
		}

		public String getSelectedSymbolText() {
			return lblSelectedSymbol.getText();
		}

		public float getFillingValue() {
			return opacitySlider.getValue();
		}

		public FillType getSelectedFillType() {
			return model.getFillTypeAt(cbFillType.getSelectedIndex());
		}

		public int getDistanceValue() {
			return distanceSlider.getValue();
		}

		public int getAngleValue() {
			return angleSlider.getValue();
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
			model = new LineStyleModel();
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
			renderer.setPreferredSize(new Dimension(130,
					app.getGUIFontSize() + 6));
			dashCB = new JComboBox(EuclidianViewD.getLineTypes());
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
			 * JLabel thicknessLabel = new JLabel(app.getPlain("Thickness") +
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

		public void setLabels() {
			thicknessPanel.setBorder(BorderFactory.createTitledBorder(app
					.getPlain("Thickness")));
			opacityPanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("LineOpacity")));

			dashLabel.setText(app.getPlain("LineStyle") + ":");
		}

		public JPanel update(Object[] geos) {

			model.setGeos(geos);
			return update();
		}

		public JPanel updatePanel(Object[] geos) {

			model.setGeos(geos);
			return update();
		}

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
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == thicknessSlider) {
				if (!thicknessSlider.getValueIsAdjusting()) {
					model.applyThickness(thicknessSlider.getValue());
				}
			} else if (e.getSource() == opacitySlider) {
				if (!opacitySlider.getValueIsAdjusting()) {
					int value = (int) ((opacitySlider.getValue() / 100.0f) * 255);
					model.applyOpacity(value);
				}
			}
		}

		/**
		 * action listener implementation for coord combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dashCB) {
				model.applyLineType(((Integer) dashCB.getSelectedItem())
						.intValue());
			}
		}

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

		public void setThicknessSliderValue(int value) {
			thicknessSlider.setValue(value);
		}

		public void setThicknessSliderMinimum(int minimum) {
			thicknessSlider.setMinimum(minimum);
		}

		public void setOpacitySliderValue(int value) {
			opacitySlider.setValue(value);
		}

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

		public void setLineTypeVisible(boolean value) {
			dashPanel.setVisible(value);
		}

		public void setLineOpacityVisible(boolean value) {
			opacityPanel.setVisible(value);
		}
	}

	/**
	 * select dash style for hidden parts.
	 * 
	 * @author matthieu
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
					app.getMenu("Hidden.Invisible"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE]
					.setActionCommand("none");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED] = new JRadioButton(
					app.getMenu("Hidden.Dashed"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED]
					.setActionCommand("dashed");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN] = new JRadioButton(
					app.getMenu("Hidden.Unchanged"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN]
					.setActionCommand("asNotHidden");

			ButtonGroup buttonGroup = new ButtonGroup();
			for (int i = 0; i < 3; i++) {
				buttons[i].addActionListener(this);
				add(buttons[i]);
				buttonGroup.add(buttons[i]);
			}

		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getMenu("HiddenLineStyle")));
		}

		public JPanel updatePanel(Object[] geos) {

			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			
			// check if we use 3D view
			if (!app.isEuclidianView3Dinited())
				return null;
			
			// check geos
			if (!checkGeos(geos))
				return null;

			// set value to first line's style
			GeoElement geo0 = (GeoElement) geos[0];

			// update radio buttons
			buttons[geo0.getLineTypeHidden()].setSelected(true);

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.isPath())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {

			int type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE;

			if (e.getActionCommand() == "dashed") {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED;
			} else if (e.getActionCommand() == "asNotHidden") {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN;
			}

			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setLineTypeHidden(type);
				geo.updateRepaint();
			}
		}

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

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Fading")));
		}

		public JPanel updatePanel(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			// set value to first point's size
			GeoPlaneND geo0 = (GeoPlaneND) geos[0];
			slider.setValue((int) (100 * geo0.getFading()));

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
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

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the level of detail of surfaces
	 * 
	 * @author mathieu
	 */
	private class LodPanel extends JPanel 
	implements ActionListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel,
	IComboListener {

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

		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}
		public void setLabels() {
			
			label.setText(app.getPlain("LevelOfDetail"));

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

		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
			combo.setFont(font);

		}


		public void updateVisualStyle(GeoElement geo) {
			// nothing to do here

		}

		public void setSelectedIndex(int index) {
			combo.setSelectedIndex(index);
		}

		public void addItem(String item) {
			combo.addItem(item);
			
		}

		public void setSelectedItem(String item) {
			// nothing to do here
			
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == combo) {
				model.applyChanges(combo.getSelectedIndex());
			}
			
		}
	}

	/**
	 * panel to show a 2D view from a plane, polygon, etc.
	 * 
	 * @author mathieu
	 */
	@SuppressWarnings("unused")
	private class ShowView2D extends JPanel implements ItemListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox cb2DView;

		public ShowView2D() {
			super(new FlowLayout(FlowLayout.LEFT));

			// check boxes for show trace
			cb2DView = new JCheckBox();
			cb2DView.addItemListener(this);

			// put it all together
			add(cb2DView);
		}

		public void setLabels() {
			cb2DView.setText(app.getPlain("ViewFrom"));
		}

		public JPanel updatePanel(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cb2DView.removeItemListener(this);

			// check if properties have same values
			ViewCreator temp, geo0 = (ViewCreator) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (ViewCreator) geos[i];
				// same object visible value
				if (geo0.hasView2DVisible() != temp.hasView2DVisible())
					equalVal = false;
			}

			// set checkbox
			if (equalVal)
				cb2DView.setSelected(geo0.hasView2DVisible());
			else
				cb2DView.setSelected(false);

			cb2DView.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof ViewCreator))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			ViewCreator geo;
			Object source = e.getItemSelectable();

			// absolute screen location flag changed
			if (source == cb2DView) {
				boolean flag = cb2DView.isSelected();
				for (int i = 0; i < geos.length; i++) {
					geo = (ViewCreator) geos[i];
					geo.setView2DVisible(flag);
				}

				updateSelection(geos);
			}
		}

		public void updateFonts() {
			// TODO Auto-generated method stub

		}

		public void updateVisualStyle(GeoElement geo) {
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
			model = new DecoSegmentModel();
			model.setListener(this);
			// deco combobox
			DecorationListRenderer renderer = new DecorationListRenderer();
			renderer.setPreferredSize(new Dimension(130,
					app.getGUIFontSize() + 6));
			decoCombo = new JComboBox(GeoSegment.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);

			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}

		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
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

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				model.applyChanges(type);
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void setSelectedIndex(int index) {
			decoCombo.setSelectedIndex(index);
		}

		public void addItem(String item) {
			// TODO Auto-generated method stub

		}

		public void setSelectedItem(String item) {

		}
	}

	private class DecoAnglePanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel,
			IDecoAngleListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private DecoAngleModel model;

		DecoAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new DecoAngleModel();
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

		public JPanel updatePanel(Object[] geos) {
			return update(geos);
		}
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			decoCombo.removeActionListener(this);

			model.updateProperties();
			decoCombo.addActionListener(this);
			return this;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				model.applyChanges(type);
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void setSelectedIndex(int index) {
			decoCombo.setSelectedIndex(index);

		}

		public void addItem(String item) {
		}

		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

		}

		public void setArcSizeMinValue() {
			setSliderMinValue();

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
			setModel(new RightAngleModel(this));
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}
	}

	/**
	 * allows using a single = in condition to show object and dynamic color
	 * 
	 * @param strCond
	 *            Condition to be processed
	 * @return processed condition
	 */
	public static String replaceEqualsSigns(String strCond) {
		// needed to make next replace easier
		strCond = strCond.replaceAll(">=",
				ExpressionNodeConstants.strGREATER_EQUAL);
		strCond = strCond.replaceAll("<=",
				ExpressionNodeConstants.strLESS_EQUAL);
		strCond = strCond.replaceAll("==",
				ExpressionNodeConstants.strEQUAL_BOOLEAN);
		strCond = strCond
				.replaceAll("!=", ExpressionNodeConstants.strNOT_EQUAL);

		// allow A=B as well as A==B
		// also stops A=B doing an assignment of B to A :)
		return strCond
				.replaceAll("=", ExpressionNodeConstants.strEQUAL_BOOLEAN);

	}

} // PropertiesPanel

/**
 * panel for textfield size
 * 
 * @author Michael
 */
class TextfieldSizePanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
		ITextFieldListener {

	private static final long serialVersionUID = 1L;

	private TextFieldSizeModel model;
	private JLabel label;
	private MyTextField tfTextfieldSize;
	private AppD app;

	public TextfieldSizePanel(AppD app) {
		this.app = app;
		model = new TextFieldSizeModel(app);
		model.setListener(this);
		// text field for textfield size
		label = new JLabel();
		tfTextfieldSize = new MyTextField(app, 5);
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

	public void setLabels() {
		label.setText(app.getPlain("TextfieldLength") + ": ");
	}

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
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfTextfieldSize)
			doActionPerformed();
	}

	private void doActionPerformed() {
		model.applyChanges(tfTextfieldSize.getText());
		updatePanel(model.getGeos());
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	public void updateFonts() {
		// TODO Auto-generated method stub

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setText(String text) {
		tfTextfieldSize.setText(text);

	}
}

/**
 * panel for condition to show object
 * 
 * @author Markus Hohenwarter
 */
class ShowConditionPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
		IShowConditionListener {

	private static final long serialVersionUID = 1L;

	private ShowConditionModel model;
	private JTextField tfCondition;

	private Kernel kernel;
	private PropertiesPanelD propPanel;

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

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(kernel.getApplication()
				.getMenu("Condition.ShowObject")));
	}

	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos())
			return null;

		tfCondition.removeActionListener(this);

		model.updateProperties();

		tfCondition.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfCondition) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;
		model.applyChanges(tfCondition.getText());
	}

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
			doActionPerformed();
	}

	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);
		tfCondition.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setText(String text) {
		tfCondition.setText(text);
	}

	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);
	}

	public String replaceEqualsSigns(String strCond) {
		return PropertiesPanelD.replaceEqualsSigns(strCond);
	}
}

/**
 * panel for condition to show object
 * 
 * @author Michael Borcherds 2008-04-01
 */
@SuppressWarnings({"unchecked","rawtypes"})
class ColorFunctionPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
		IColorFunctionListener {

	private static final long serialVersionUID = 1L;

	private ColorFunctionModel model;
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

	public void setLabels() {
		Localization loc = kernel.getLocalization();

		setBorder(BorderFactory
				.createTitledBorder(loc.getMenu("DynamicColors")));

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
		case GeoElement.COLORSPACE_RGB:
			nameLabelR.setText(loc.getMenu("Red") + ":");
			nameLabelG.setText(loc.getMenu("Green") + ":");
			nameLabelB.setText(loc.getMenu("Blue") + ":");
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
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfRed || e.getSource() == tfGreen
				|| e.getSource() == tfBlue || e.getSource() == tfAlpha)
			doActionPerformed();
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

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	private boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
			doActionPerformed();
	}

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

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setRedText(final String text) {
		tfRed.setText(text);

	}

	public void setGreenText(final String text) {
		tfGreen.setText(text);
		// TODO Auto-generated method stub

	}

	public void setBlueText(final String text) {
		tfBlue.setText(text);

	}

	public void setAlphaText(final String text) {
		tfAlpha.setText(text);

	}

	public void setDefaultValues(GeoElement geo) {
		Color col = org.geogebra.desktop.awt.GColorD.getAwtColor(geo.getObjectColor());
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

	public void showAlpha(boolean value) {
		tfAlpha.setVisible(value);
		nameLabelA.setVisible(value);
	}

	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);

	}

}

/**
 * panel to set graphics view location
 * 
 * @author G.Sturr
 */
class GraphicsViewLocationPanel extends JPanel implements ActionListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts,
		IGraphicsViewLocationListener {

	private static final long serialVersionUID = 1L;

	private GraphicsViewLocationModel model;

	private JCheckBox cbGraphicsView, cbGraphicsView2, cbGraphicsView3D,
			cbGraphicsViewForPlane;

	private Kernel kernel;
	private AppD app;

	public GraphicsViewLocationPanel(AppD app, PropertiesPanelD propPanel) {
		this.app = app;
		kernel = app.getKernel();
		model = new GraphicsViewLocationModel(app, this);

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

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(kernel.getApplication()
				.getMenu("Location")));
		cbGraphicsView.setText(app.getPlain("DrawingPad"));
		cbGraphicsView2.setText(app.getPlain("DrawingPad2"));
		cbGraphicsView3D.setText(app.getPlain("GraphicsView3D"));
		cbGraphicsViewForPlane.setText(app.getPlain("ExtraViews"));

	}

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

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cbGraphicsView) {
			model.applyToEuclidianView1(cbGraphicsView.isSelected());
		}else if (e.getSource() == cbGraphicsView2) {
			model.applyToEuclidianView2(cbGraphicsView2.isSelected());
		}else if (e.getSource() == cbGraphicsView3D) {
			model.applyToEuclidianView3D(cbGraphicsView3D.isSelected());
		} else if (e.getSource() == cbGraphicsViewForPlane) {
			model.applyToEuclidianViewForPlane(cbGraphicsViewForPlane
					.isSelected());
		}

	}

	public void updateFonts() {
		Font font = app.getPlainFont();

		setFont(font);
		cbGraphicsView.setFont(font);
		cbGraphicsView2.setFont(font);
		cbGraphicsView3D.setFont(font);
		cbGraphicsViewForPlane.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void selectView(int index, boolean isSelected) {
		switch(index){
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
			
		}
	}

	public void setCheckBox3DVisible(boolean flag) {
		cbGraphicsView3D.setVisible(flag);		
	}

	public void setCheckBoxForPlaneVisible(boolean flag) {
		cbGraphicsViewForPlane.setVisible(flag);
	}

}

class ButtonSizePanel extends JPanel implements ChangeListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts, IButtonSizeListener {

	private static final long serialVersionUID = 1L;
	private ButtonSizeModel model;

	private MyTextField tfButtonWidth;
	private MyTextField tfButtonHeight;
	private JLabel labelWidth;
	private JLabel labelHeight;
	private JLabel labelPixelW;
	private JLabel labelPixelH;
	private Localization loc;
	private JCheckBox cbUseFixedSize;

	public ButtonSizePanel(AppD app, Localization loc) {
		this.loc = loc;
		model = new ButtonSizeModel();
		model.setListener(this);

		labelWidth = new JLabel(loc.getPlain("Width"));
		labelHeight = new JLabel(loc.getPlain("Height"));
		labelPixelW = new JLabel(loc.getMenu("Pixels.short"));
		labelPixelH = new JLabel(loc.getMenu("Pixels.short"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		cbUseFixedSize = new JCheckBox(loc.getPlain("fixed"));
		tfButtonWidth = new MyTextField(app, 3);
		tfButtonHeight = new MyTextField(app, 3);
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

	public void updateFonts() {

	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(loc.getPlain("ButtonSize")));
		labelWidth.setText(loc.getPlain("Width"));
		labelHeight.setText(loc.getPlain("Height"));
		labelPixelW.setText(loc.getMenu("Pixels.short"));
		labelPixelH.setText(loc.getMenu("Pixels.short"));
		cbUseFixedSize.setText(loc.getPlain("fixed"));

	}

	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}
		model.updateProperties();

		return this;
	}

	public void updateSizes(int width, int height, boolean isFixed) {
		cbUseFixedSize.removeChangeListener(this);
		cbUseFixedSize.setSelected(isFixed);
		tfButtonHeight.setText("" + height);
		tfButtonWidth.setText("" + width);
		tfButtonHeight.setEnabled(isFixed);
		tfButtonWidth.setEnabled(isFixed);
		cbUseFixedSize.addChangeListener(this);

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent arg0) {
		model.setSizesFromString(tfButtonWidth.getText(),
				tfButtonHeight.getText(), cbUseFixedSize.isSelected());
	}

	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == cbUseFixedSize) {
			JCheckBox check = (JCheckBox) arg0.getSource();
			model.applyChanges(check.isSelected());
		}
	}

	class SizeVerify extends InputVerifier {
		public boolean verify(JComponent input) {
			MyTextField tf = (MyTextField) input;
			String s = tf.getText();
			if (!s.matches("\\d{2,3}"))
				return false;
			if (Integer.parseInt(s) < 24 || Integer.parseInt(s) > 500)
				return false;
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

	private ObjectNameModel model;
	private AutoCompleteTextFieldD tfName, tfDefinition, tfCaption;

	private Runnable doActionStopped = new Runnable() {
		public void run() {
			model.setBusy(false);
		}
	};
	private JLabel nameLabel, defLabel, captionLabel;
	private InputPanelD inputPanelName, inputPanelDef, inputPanelCap;

	private AppD app;
	private Localization loc;

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

	public void setLabels() {
		nameLabel.setText(loc.getPlain("Name") + ":");
		defLabel.setText(loc.getPlain("Definition") + ":");
		captionLabel.setText(loc.getMenu("Button.Caption") + ":");
	}

	public void updateGUI(boolean showDefinition, boolean showCaption) {
		int rows = 1;
		removeAll();

		if (loc.isRightToLeftReadingOrder()) {
			add(inputPanelName);
			add(nameLabel);
		} else {
			add(nameLabel);
			add(inputPanelName);
		}

		if (showDefinition) {
			rows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelDef);
				add(defLabel);
			} else {
				add(defLabel);
				add(inputPanelDef);
			}
		}

		if (showCaption) {
			rows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelCap);
				add(captionLabel);
			} else {
				add(captionLabel);
				add(inputPanelCap);
			}
		}

		app.setComponentOrientation(this);

		this.rows = rows;
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
	private GeoElement currentGeoForFocusLost = null;

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

	public void updateDef(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy())
			return;

		tfDefinition.removeActionListener(this);
		model.getDefInputHandler().setGeoElement(geo);
		tfDefinition.setText(ObjectNameModel.getDefText(geo));
		tfDefinition.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	public void updateName(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy())
			return;

		tfName.removeActionListener(this);
		model.getNameInputHandler().setGeoElement(geo);
		tfName.setText(geo.getLabel(StringTemplate.editTemplate));
		tfName.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * handle textfield changes
	 */
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
			model.applyNameChange(tfName.getText());

		} else if (source == tfDefinition) {

			model.applyDefinitionChange(tfDefinition.getText());
			tfDefinition.requestFocusInWindow();

		} else if (source == tfCaption) {
			model.applyCaptionChange(tfCaption.getText());
		}

		SwingUtilities.invokeLater(doActionStopped);
	}

	public void focusGained(FocusEvent arg0) {
		// started to type something : store current geo if focus lost
		currentGeoForFocusLost = model.getCurrentGeo();
	}

	public void focusLost(FocusEvent e) {

		if (model.isBusy()) {
			return;
		}

		Object source = e.getSource();

		if (source == tfDefinition) {
			model.redefineCurrentGeo(currentGeoForFocusLost,
					tfDefinition.getText(), redefinitionForFocusLost);

			SwingUtilities.invokeLater(doActionStopped);

		} else {
			doActionPerformed(source);
		}
	}

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

	public void updateVisualStyle(GeoElement geo) {
		// NOTHING SHOULD BE DONE HERE (ENDLESS CALL WITH UPDATE)

	}

	public void setNameText(final String text) {
		tfName.setText(text);
		tfName.requestFocus();
	}

	public void setDefinitionText(final String text) {
		tfDefinition.setText(text);
	}

	public void setCaptionText(final String text) {
		tfCaption.setText(text);
		tfCaption.requestFocus();
	}

	public void updateCaption() {
		tfCaption.removeActionListener(this);
		tfCaption.setText(model.getCurrentGeo().getRawCaption());
		tfCaption.addActionListener(this);

	}

	public void updateDefLabel() {
		updateDef(model.getCurrentGeo());

		if (model.getCurrentGeo().isIndependent()) {
			defLabel.setText(app.getPlain("Value") + ":");
		} else {
			defLabel.setText(app.getPlain("Definition") + ":");
		}
	}

	public void updateName(String text) {
		tfName.removeActionListener(this);
		tfName.setText(text);

		// if a focus lost is called in between, we keep the current definition
		// text
		redefinitionForFocusLost = tfDefinition.getText();
		tfName.addActionListener(this);

	}

}