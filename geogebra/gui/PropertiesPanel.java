/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.SpringUtilities;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.AlgoIntersectAbstract;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.AlgoTransformation;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoTextField;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.LevelOfDetail;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
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
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

	/**
	 * PropertiesPanel for displaying all gui elements for changing properties
	 * of currently selected GeoElements. 
	 * @see update()
	 * PropertiesPanel
	 * @author Markus Hohenwarter
	 */
public	class PropertiesPanel extends JPanel implements SetLabels {
		private static final int MAX_COMBOBOX_ENTRIES = 200;
		
		private Application app;
		private Kernel kernel;
		private JColorChooser colChooser;
	
		private static final long serialVersionUID = 1L;
		private NamePanel namePanel;
		private ShowObjectPanel showObjectPanel;		
		private SelectionAllowedPanel selectionAllowed;		
		private ShowTrimmedIntersectionLines showTrimmedIntersectionLines;		
		private ColorPanel colorPanel;
		private LabelPanel labelPanel;
		private TooltipPanel tooltipPanel;
		private LayerPanel layerPanel; // Michael Borcherds 2008-02-26
		private CoordPanel coordPanel;
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
		//END
		
		private FillingPanel fillingPanel;
		private FadingPanel fadingPanel;
		private LodPanel lodPanel;
		private CheckBoxInterpolateImage checkBoxInterpolateImage;
		private TracePanel tracePanel;
		private AnimatingPanel animatingPanel;
		private FixPanel fixPanel;
		private IneqStylePanel ineqStylePanel;
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
		private ShowConditionPanel showConditionPanel;
		private ColorFunctionPanel colorFunctionPanel;
		
		private GraphicsViewLocationPanel graphicsViewLocationPanel;
		
		//private CoordinateFunctionPanel coordinateFunctionPanel;
		
		private TabPanel basicTab;
		private TabPanel colorTab;
		private TabPanel styleTab;
		private TabPanel lineStyleTab;
		private TabPanel sliderTab;
		private TabPanel textTab;
		private TabPanel positionTab;
		private TabPanel algebraTab;
		private TabPanel scriptTab;
		private TabPanel advancedTab;
		
		/**
		 * If just panels should be displayed which are used if the user
		 * modifies the default properties of an object type. 
		 */
		private boolean isDefaults;
				
		private JTabbedPane tabs;

		/**
		 * @param app
		 * @param colChooser
		 * @param isDefaults
		 */
		public PropertiesPanel(Application app, JColorChooser colChooser, boolean isDefaults) {					
			this.isDefaults = isDefaults;
			
			this.app = app;
			this.kernel = app.getKernel();
			this.colChooser = colChooser;
			
			// load panels which are hidden for the defaults dialog
			if(!isDefaults) {
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
				allowReflexAnglePanel = new AllowReflexAnglePanel();
							
				//coordinateFunctionPanel = new CoordinateFunctionPanel(app, this);
			}
			
			sliderPanel = new SliderPanel(app, this, false, true);
			showObjectPanel = new ShowObjectPanel();
			selectionAllowed = new SelectionAllowedPanel();
			showTrimmedIntersectionLines = new ShowTrimmedIntersectionLines();
			colorPanel = new ColorPanel(colChooser);
			coordPanel = new CoordPanel();
			lineEqnPanel = new LineEqnPanel();
			conicEqnPanel = new ConicEqnPanel();
			pointSizePanel = new PointSizePanel();
			pointStylePanel = new PointStylePanel(); // Florian Sonner 2008-07-12
			ineqStylePanel = new IneqStylePanel();
			textOptionsPanel = new TextOptionsPanel();
			arcSizePanel = new ArcSizePanel();
			slopeTriangleSizePanel = new SlopeTriangleSizePanel();
			lineStylePanel = new LineStylePanel();
			lineStylePanelHidden = new LineStyleHiddenPanel();
			// added by Loic BEGIN
			decoSegmentPanel=new DecoSegmentPanel();
			decoAnglePanel=new DecoAnglePanel();
			rightAnglePanel=new RightAnglePanel();
			//END
			fillingPanel = new FillingPanel();
			fadingPanel = new FadingPanel();
			lodPanel = new LodPanel();
			checkBoxInterpolateImage = new CheckBoxInterpolateImage();
			tracePanel = new TracePanel();
			animatingPanel = new AnimatingPanel();
			fixPanel = new FixPanel();
			checkBoxFixPanel = new CheckBoxFixPanel();
			absScreenLocPanel = new AbsoluteScreenLocationPanel();
			auxPanel = new AuxiliaryObjectPanel();
			animStepPanel = new AnimationStepPanel(app);
			textFieldSizePanel = new TextfieldSizePanel(app);
			animSpeedPanel = new AnimationSpeedPanel(app);
			allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
			
 			//tabbed pane for properties
			tabs = new JTabbedPane();				
 			initTabs();
 			
 			setLayout(new BorderLayout());
 			add(tabs, BorderLayout.CENTER); 			
		}		
		
		public void showSliderTab() {
			tabs.setSelectedIndex(1);
		}
		
		// added by Loic BEGIN
		public void setSliderMinValue(){
			arcSizePanel.setMinValue();
		}
		//END		
		
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
						
			if(!isDefaults)
				basicTabList.add(namePanel);
			
			basicTabList.add(showObjectPanel);	
			
			basicTabList.add(selectionAllowed);	
						
			if(!isDefaults)
				basicTabList.add(labelPanel);
			
			basicTabList.add(tracePanel);
			
			if(!isDefaults)
				basicTabList.add(animatingPanel);
				
			basicTabList.add(fixPanel);	
			basicTabList.add(auxPanel);
			basicTabList.add(checkBoxFixPanel);
			
			if(!isDefaults)
				basicTabList.add(bgImagePanel);
			
			basicTabList.add(absScreenLocPanel);
			if(!isDefaults)
				basicTabList.add(allowReflexAnglePanel);	
			basicTabList.add(rightAnglePanel);
			basicTabList.add(allowOutlyingIntersectionsPanel);
			basicTabList.add(showTrimmedIntersectionLines);
			basicTab = new TabPanel(basicTabList);
			tabPanelList.add(basicTab);
			
			// text tab
			ArrayList<JPanel> textTabList = new ArrayList<JPanel>();			
			textTabList.add(textOptionsPanel);	
			
			if(!isDefaults)
				textTabList.add(textEditPanel);
			
			textTab = new TabPanel(textTabList);
			tabPanelList.add(textTab);
			
			// slider tab
			//if(!isDefaults)
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
			styleTabList.add(lineStylePanelHidden);	
			styleTabList.add(arcSizePanel);		
			styleTabList.add(fillingPanel);
			styleTabList.add(fadingPanel);
			styleTabList.add(lodPanel);
			styleTabList.add(checkBoxInterpolateImage);
			styleTabList.add(textFieldSizePanel);
			styleTab = new TabPanel(styleTabList);
			tabPanelList.add(styleTab);
				
			// decoration
			ArrayList<JPanel> decorationTabList = new ArrayList<JPanel>();	
			decorationTabList.add(decoAnglePanel);
			decorationTabList.add(decoSegmentPanel);
			lineStyleTab = new TabPanel(decorationTabList);
			tabPanelList.add(lineStyleTab);
			
			// filling style
//			ArrayList fillingTabList = new ArrayList();	
//			fillingTabList.add(fillingPanel);			
//			TabPanel fillingTab = new TabPanel(app.getPlain("Filling"), fillingTabList);
//			fillingTab.addToTabbedPane(tabs);										
			
			// position			
			if(!isDefaults) {
				ArrayList<JPanel> positionTabList = new ArrayList<JPanel>();
				
				positionTabList.add(startPointPanel);	
				positionTabList.add(cornerPointsPanel);
				
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
			if(!isDefaults) {
				ArrayList<JPanel> advancedTabList = new ArrayList<JPanel>();
				
				advancedTabList.add(showConditionPanel);	
				advancedTabList.add(colorFunctionPanel);	
				
				//advancedTabList.add(coordinateFunctionPanel);	
				advancedTabList.add(layerPanel); // Michael Borcherds 2008-02-26
				
				advancedTabList.add(tooltipPanel);
				
				
				//=================================================
				// add location panel 
				 advancedTabList.add(graphicsViewLocationPanel);
				//===================================================
				
				
				advancedTab = new TabPanel(advancedTabList);
				tabPanelList.add(advancedTab);
			}
			
			// javascript tab
			if(!isDefaults) {
				ArrayList<JPanel> scriptTabList = new ArrayList<JPanel>();			
				//scriptTabList.add(scriptOptionsPanel);
				
				scriptTabList.add(scriptEditPanel);
				
				scriptTab = new TabPanel(scriptTabList);
				tabPanelList.add(scriptTab);
			}
			
			
			setLabels();
		}
		
		/**
		 * Update the labels of this panel in case the user language
		 * was changed.
		 */
		public void setLabels() {
			// update labels of tabs
			// TODO change label for script tab
			basicTab.setTitle(app.getMenu("Properties.Basic"));
			colorTab.setTitle(app.getPlain("Color"));
			styleTab.setTitle(app.getMenu("Properties.Style"));
			lineStyleTab.setTitle(app.getPlain("Decoration"));
			textTab.setTitle(app.getPlain("Text"));
			algebraTab.setTitle(app.getMenu("Properties.Algebra"));
			sliderTab.setTitle(app.getPlain("Slider")); 
			
			if(!isDefaults) {
				positionTab.setTitle(app.getMenu("Properties.Position"));
				scriptTab.setTitle(app.getPlain("Scripting")); 
				advancedTab.setTitle(app.getMenu("Advanced"));
			}
			
			// update the labels of the panels
			showObjectPanel.setLabels();
			selectionAllowed.setLabels();
			showTrimmedIntersectionLines.setLabels();
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
			sliderPanel.setLabels();

			if(!isDefaults) {
		 		allowReflexAnglePanel.setLabels();
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
			}
									
			// remember selected tab
			Component selectedTab = tabs.getSelectedComponent();
			
			// update tab labels
			tabs.removeAll();				
			for (int i=0; i < tabPanelList.size(); i++) {
				TabPanel tp = (TabPanel) tabPanelList.get(i);
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
		 * Update all tabs after new GeoElements were selected.
		 * @param geos
		 */
		private void updateTabs(Object [] geos) {			
			if (geos.length == 0) {
				tabs.setVisible(false);
				return;
			}
			
			
			// remember selected tab
			Component selectedTab = tabs.getSelectedComponent();
			
			tabs.removeAll();				
			for (int i=0; i < tabPanelList.size(); i++) {
				TabPanel tp = (TabPanel) tabPanelList.get(i);
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
		
		private boolean updateTabPanel(TabPanel tabPanel, ArrayList tabList, Object [] geos) {
			// update all panels and their visibility			
			boolean oneVisible = false;
			int size = tabList.size();
			for (int i=0; i < size; i++) {
				UpdateablePanel up = (UpdateablePanel) tabList.get(i);
				boolean show = (up.update(geos) != null);						
				up.setVisible(show);
				if (show) oneVisible = true;
			}
			
			return oneVisible;				
		}				
		
		public void updateSelection(Object[] geos) {
			//if (geos == oldSelGeos) return;
			//oldSelGeos = geos;										
						
			updateTabs(geos);
		}				
						
		
		private class TabPanel extends JPanel {
		
			private String title;
			private ArrayList panelList;
			private boolean makeVisible = true;			
			
			public TabPanel(ArrayList pVec) {
				panelList = pVec;
					
				setLayout(new BorderLayout());
				
				JPanel panel = new JPanel();
				panel.setBorder(BorderFactory.createEmptyBorder(5, 5,5,5));
				
				panel.setLayout(new FullWidthLayout());
				
				for (int i = 0; i < pVec.size(); i++) {		
					panel.add((JPanel)pVec.get(i));
				}
				
				JScrollPane scrollPane = new JScrollPane(panel);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				add(scrollPane, BorderLayout.CENTER);
			}
		
			public void setTitle(String title) {
				this.title = title;
			}
			
			public void update(Object [] geos) {
				makeVisible = updateTabPanel(this, panelList, geos);
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
		private class ShowObjectPanel extends JPanel implements ItemListener, UpdateablePanel, SetLabels {
		
			private static final long serialVersionUID = 1L;
			private Object[] geos; // currently selected geos
			private JCheckBox showObjectCB;

			public ShowObjectPanel() {
				setLayout(new FlowLayout(FlowLayout.LEFT));
				
				// check box for show object
				showObjectCB = new JCheckBox();
				showObjectCB.addItemListener(this);			
				add(showObjectCB);			
			}
			
			public void setLabels() {
				showObjectCB.setText(app.getPlain("ShowObject"));
			}			

			public JPanel update(Object[] geos) {
				this.geos = geos;
				if (!checkGeos(geos))
					return null;

				showObjectCB.removeItemListener(this);

				// check if properties have same values
				GeoElement temp, geo0 = (GeoElement) geos[0];
				boolean equalObjectVal = true;
				boolean showObjectCondition = geo0.getShowObjectCondition() != null;

				for (int i = 1; i < geos.length; i++) {
					temp = (GeoElement) geos[i];
					// same object visible value
					if (geo0.isSetEuclidianVisible()
						!= temp.isSetEuclidianVisible()) {
						equalObjectVal = false;
						break;
					}
					
					if (temp.getShowObjectCondition() != null) {
						showObjectCondition = true;
					}
				}

				// set object visible checkbox
				if (equalObjectVal)
					showObjectCB.setSelected(geo0.isSetEuclidianVisible());
				else
					showObjectCB.setSelected(false);

				showObjectCB.setEnabled(!showObjectCondition);
				
				showObjectCB.addItemListener(this);
				return this;
			}

			// show everything but numbers (note: drawable angles are shown)
			private boolean checkGeos(Object[] geos) {
				boolean geosOK = true;
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					if (!geo.isDrawable()
							// can't allow a free fixed number to become visible (as a slider)
							|| (geo.isGeoNumeric() && geo.isFixed())) {
						geosOK = false;
						break;
					}
				}
				return geosOK;
			}

			/**
			 * listens to checkboxes and sets object and label visible state
			 */
			public void itemStateChanged(ItemEvent e) {
				GeoElement geo;
				Object source = e.getItemSelectable();

				// show object value changed
				if (source == showObjectCB) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						geo.setEuclidianVisible(showObjectCB.isSelected());
						geo.updateRepaint();
					}
				}
				updateSelection(geos);
			}

		} // ShowObjectPanel

		/**
		 * panel with show/hide object checkbox
		 */
		private class SelectionAllowedPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		
			private static final long serialVersionUID = 1L;
			private Object[] geos; // currently selected geos
			private JCheckBox selectionAllowedCB;

			public SelectionAllowedPanel() {
				setLayout(new FlowLayout(FlowLayout.LEFT));
				
				// check box for show object
				selectionAllowedCB = new JCheckBox();
				selectionAllowedCB.addItemListener(this);			
				add(selectionAllowedCB);			
			}
			
			public void setLabels() {
				selectionAllowedCB.setText(app.getPlain("SelectionAllowed"));
			}			

			public JPanel update(Object[] geos) {
				this.geos = geos;
				if (!checkGeos(geos))
					return null;

				selectionAllowedCB.removeItemListener(this);

				// check if properties have same values
				GeoElement temp, geo0 = (GeoElement) geos[0];
				boolean equalObjectVal = true;

				for (int i = 1; i < geos.length; i++) {
					temp = (GeoElement) geos[i];
					// same object visible value
					if (geo0.isSelectionAllowed()
						!= temp.isSelectionAllowed()) {
						equalObjectVal = false;
						break;
					}
					
				}

				// set object visible checkbox
				if (equalObjectVal)
					selectionAllowedCB.setSelected(geo0.isSelectionAllowed());
				else
					selectionAllowedCB.setSelected(false);

				selectionAllowedCB.setEnabled(true);
				
				selectionAllowedCB.addItemListener(this);
				return this;
			}

			// show everything 
			private boolean checkGeos(Object[] geos) {
				return true;
			}

			/**
			 * listens to checkboxes and sets object and label visible state
			 */
			public void itemStateChanged(ItemEvent e) {
				GeoElement geo;
				Object source = e.getItemSelectable();

				// show object value changed
				if (source == selectionAllowedCB) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						geo.setSelectionAllowed(selectionAllowedCB.isSelected());
						geo.updateRepaint();
					}
				}
				updateSelection(geos);
			}

		} // SelectionAllowedPanel

		/**
		 * panel with show/hide trimmed intersection lines
		 */
		private class ShowTrimmedIntersectionLines extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		
			private static final long serialVersionUID = 1L;
			private Object[] geos; // currently selected geos
			private JCheckBox showTrimmedLinesCB;

			public ShowTrimmedIntersectionLines() {
				setLayout(new FlowLayout(FlowLayout.LEFT));
				
				// check box for show object
				showTrimmedLinesCB = new JCheckBox();
				showTrimmedLinesCB.addItemListener(this);			
				add(showTrimmedLinesCB);			
			}
			
			public void setLabels() {
				showTrimmedLinesCB.setText(app.getPlain("ShowTrimmed"));
			}			

			public JPanel update(Object[] geos) {
				this.geos = geos;
				if (!checkGeos(geos))
					return null;

				showTrimmedLinesCB.removeItemListener(this);

				// check if properties have same values
				GeoElement temp, geo0 = (GeoElement) geos[0];
				boolean equalObjectVal = true;

				for (int i = 1; i < geos.length; i++) {
					temp = (GeoElement) geos[i];
					// same object visible value
					if (geo0.getShowTrimmedIntersectionLines() != temp.getShowTrimmedIntersectionLines()) {
						equalObjectVal = false;
						break;
					}
					
				}

				// set object visible checkbox
				if (equalObjectVal)
					showTrimmedLinesCB.setSelected(geo0.getShowTrimmedIntersectionLines());
				else
					showTrimmedLinesCB.setSelected(false);

				showTrimmedLinesCB.setEnabled(true);
				
				showTrimmedLinesCB.addItemListener(this);
				return this;
			}

			// show everything but numbers (note: drawable angles are shown)
			private boolean checkGeos(Object[] geos) {
				boolean geosOK = true;
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					if (!(geo.getParentAlgorithm() instanceof AlgoIntersectAbstract)) {
						geosOK = false;
						break;
					}
				}
				return geosOK;
			}

			/**
			 * listens to checkboxes and sets object and label visible state
			 */
			public void itemStateChanged(ItemEvent e) {
				GeoElement geo;
				Object source = e.getItemSelectable();

				// show object value changed
				if (source == showTrimmedLinesCB) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						geo.setShowTrimmedIntersectionLines(showTrimmedLinesCB.isSelected());
						geo.updateRepaint();
					}
				}
				updateSelection(geos);
			}

		} // ShowObjectPanel

	/**
	 * panel to fix checkbox (boolean object)
	 */
	private class CheckBoxFixPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox checkboxFixCB;

		public CheckBoxFixPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			checkboxFixCB = new JCheckBox();
			checkboxFixCB.addItemListener(this);			
			add(checkboxFixCB);			
		}
		
		public void setLabels() {
			checkboxFixCB.setText(app.getPlain("FixCheckbox"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			checkboxFixCB.removeItemListener(this);

			// check if properties have same values
			GeoBoolean temp, geo0 = (GeoBoolean) geos[0];
			boolean equalObjectVal = true;
			
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoBoolean) geos[i];
				// same object visible value
				if (geo0.isCheckboxFixed() != temp.isCheckboxFixed()) {
					equalObjectVal = false;
					break;
				}								
			}

			// set object visible checkbox
			if (equalObjectVal)
				checkboxFixCB.setSelected(geo0.isCheckboxFixed());
			else
				checkboxFixCB.setSelected(false);
			
			checkboxFixCB.addItemListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoBoolean) {
					GeoBoolean bool = (GeoBoolean) geos[i];
					if (!bool.isIndependent()) {
						return false;
					}
 				} else
 					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == checkboxFixCB) {
				for (int i = 0; i < geos.length; i++) {
					GeoBoolean bool = (GeoBoolean) geos[i];
					bool.setCheckboxFixed(checkboxFixCB.isSelected());
					bool.updateRepaint();
				}
			}
			updateSelection(geos);
		}

	} // CheckBoxFixPanel
	
	/**
	 * panel color chooser and preview panel
	 */
	private class ColorPanel extends JPanel implements UpdateablePanel, ChangeListener, SetLabels {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos		
		private JLabel previewLabel, currentColorLabel;
		private JPanel previewPanel;

		public ColorPanel(JColorChooser colChooser) {
			colChooser.setLocale(app.getLocale());
			previewPanel = new PreviewPanel();			
			previewLabel = new JLabel();
			currentColorLabel = new JLabel();
			AbstractColorChooserPanel [] tabs = colChooser.getChooserPanels();
			
			setLayout(new BorderLayout());		
			
			/*
			// Michael Borcherds 2008-03-14
			// added RGB in a new tab
			// and moved preview underneath
			JTabbedPane colorTabbedPane = new JTabbedPane();
			colorTabbedPane.addTab( app.getMenu("Swatches"), tabs[0] );
			//colorTabbedPane.addTab( app.getMenu("HSB"), tabs[1] );
			colorTabbedPane.addTab( app.getMenu("RGB"), tabs[2] );
			colorTabbedPane.setSelectedIndex(0);
			JPanel p = new JPanel();
			
			// create grid with one column
			p.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.weightx = 0.0;
			c.weighty = 0.0;
			
			c.gridx = 0;
			c.gridy = 0;			
			c.gridwidth = 4;
			p.add(colorTabbedPane, c);
			
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.insets = new Insets(10,0,0,0);  //top padding
			p.add(new JLabel(app.getMenu("Preview") + ": "), c);
								
			c.gridx = 1;
			c.gridy = 1;
			c.gridwidth = 1;
			p.add(previewPanel, c);
										
			c.weighty = 1.0;
			p.add(Box.createVerticalGlue(), c);
		*/
	
		
			setLayout(new BorderLayout());
			
			JPanel colorPalette = new JPanel(new FlowLayout(FlowLayout.LEFT));
			colorPalette.add(tabs[0]);
			add(colorPalette, BorderLayout.NORTH);		
			
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(previewLabel);
			p.add(previewPanel);
			p.add(currentColorLabel);
			add(p, BorderLayout.CENTER);
			
			// in order to get state changes we need to set color chooser to
			// a color that is different to the 	
			
			/*
			// remove possible old change listeners from color chooser						
			ChangeListener [] listeners = (ChangeListener[]) colChooser.getListeners(ChangeListener.class);
			if (listeners != null) {
				for (int i = 0; i< listeners.length; i++) {
					colChooser.getSelectionModel().removeChangeListener( listeners[i]);
				}
			}*/
			
						
			//colChooser.setColor(new Color(1, 1,1, 100));
			colChooser.getSelectionModel().addChangeListener(this);	
		}
		

		
		private class PreviewPanel extends JPanel {
		    public PreviewPanel() {
		        setPreferredSize(new Dimension(100,app.getGUIFontSize() + 8));
		        setBorder(BorderFactory.createRaisedBevelBorder());
		      }
		      public void paintComponent(Graphics g) {
		        Dimension size = getSize();
	
		        g.setColor(getForeground());
		        g.fillRect(0,0,size.width,size.height);
		      }
	    }
	    
	    public void setLabels() {
			previewLabel.setText(app.getMenu("Preview") + ": ");
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalObjColor = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object color
				if (!geo0.getObjectColor().equals(temp.getObjectColor())) {
					equalObjColor = false;
					break;
				}
			}

			// set colorButton's color to object color
			Color col;
			if (equalObjColor) {
				col = geo0.getObjectColor();
				previewPanel.setToolTipText(col.getRed() + ", " + col.getGreen() + ", " + col.getBlue());
				currentColorLabel.setText("("+previewPanel.getToolTipText()+")");		
			} else {
				col = null;
				previewPanel.setToolTipText("");
				currentColorLabel.setText("");
			}
			
			previewPanel.setForeground(col);
			return this;
		}

		/**
		 * sets color of selected GeoElements
		 */
		private void updateColor(Color col) {						
			if (col == null || geos == null)
				return;
			
			// update preview panel
			previewPanel.setForeground(col);
			previewPanel.setToolTipText(col.getRed() + ", " + col.getGreen() + ", " + col.getBlue());
			currentColorLabel.setText("("+previewPanel.getToolTipText()+")");

			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setObjColor(col);
				//geo.updateRepaint();
				geo.updateVisualStyle();
			}					
			
			Application.debug("Setting color RGB = "+col.getRed()+" "+col.getGreen()+" "+col.getBlue());
			
			// in order to get state changes we need to set color chooser to
			// a color that is not an available color
			colChooser.getSelectionModel().removeChangeListener(this);		
			colChooser.setColor(new Color(0, 0, 1));
			
			colChooser.getSelectionModel().addChangeListener(this);	
		}

	
		// show everything but images
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				/* removed - we want to be able to change the color of everything in the spreadsheet
				if (geos[i] instanceof GeoNumeric) {
					GeoNumeric num = (GeoNumeric) geos[i];
					if (!num.isDrawable())
						return false;
				} else */
				if (geos[i] instanceof GeoImage)
					return false;
			}
			return true;
		}
		
		/**
		 * Listens for color chooser state changes
		 */
		public void stateChanged(ChangeEvent arg0) {
			updateColor(colChooser.getColor());	
		}	

	} // ColorPanel

	/**
	 * panel with label properties
	 */
	private class LabelPanel
		extends JPanel
		implements ItemListener, ActionListener , UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showLabelCB;
		private JComboBox labelModeCB;
		private boolean showNameValueComboBox;

		public LabelPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show object, show label
			showLabelCB = new JCheckBox();
			showLabelCB.addItemListener(this);

			// combo box for label mode: name or algebra
			labelModeCB = new JComboBox();
			labelModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
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
			labelModeCB.addItem(app.getPlain("Caption")); // index 3 Michael Borcherds
			
			labelModeCB.setSelectedIndex(selectedIndex);
			labelModeCB.addActionListener(this);
			
			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			if (!showNameValueComboBox) {
				showLabelCB.setText(app.getPlain("ShowLabel"));
			} else {
				showLabelCB.setText(app.getPlain("ShowLabel") + ":");
			}
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showLabelCB.removeItemListener(this);
			labelModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLabelVal = true;
			boolean equalLabelMode = true;
			showNameValueComboBox =  geo0.isLabelValueShowable();

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				//	same label visible value
				if (geo0.isLabelVisible() != temp.isLabelVisible())
					equalLabelVal = false;
				//	same label mode
				if (geo0.getLabelMode() != temp.getLabelMode())
					equalLabelMode = false;
				
				showNameValueComboBox =
					showNameValueComboBox && temp.isLabelValueShowable();
			}
			
			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			if (!showNameValueComboBox)
				showLabelCB.setText(app.getPlain("ShowLabel"));
			else
				showLabelCB.setText(app.getPlain("ShowLabel") + ":");

			//	set label visible checkbox
			if (equalLabelVal) {
				showLabelCB.setSelected(geo0.isLabelVisible());
				labelModeCB.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setSelected(false);
				labelModeCB.setEnabled(false);
			}

			//	set label visible checkbox
			if (equalLabelMode)
				labelModeCB.setSelectedIndex(geo0.getLabelMode());
			else
				labelModeCB.setSelectedItem(null);

			// locus in selection
			labelModeCB.setVisible(showNameValueComboBox);
			showLabelCB.addItemListener(this);
			labelModeCB.addActionListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isLabelShowable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show label value changed
			if (source == showLabelCB) {
				boolean flag = showLabelCB.isSelected();				
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLabelVisible(flag);
					geo.updateRepaint();
				}	
				update(geos);
			}
		}

		/**
		* action listener implementation for label mode combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == labelModeCB) {
				GeoElement geo;
				int mode = labelModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLabelMode(mode);
					geo.updateRepaint();
				}
			}
		}

	} // LabelPanel
	
	/**
	 * panel with label properties
	 */
	private class TooltipPanel
		extends JPanel
		implements ItemListener, ActionListener , UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox tooltipModeCB;
		JLabel label;

		public TooltipPanel() {
			
			label = new JLabel();
			label.setLabelFor(tooltipModeCB);

			// combo box for label mode: name or algebra
			tooltipModeCB = new JComboBox();
			tooltipModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);			
			add(tooltipModeCB);			
		}
		
		public void setLabels() {
			
			label.setText(app.getMenu("Tooltip")+":");

			int selectedIndex = tooltipModeCB.getSelectedIndex();
			tooltipModeCB.removeActionListener(this);
			
			tooltipModeCB.removeAllItems();
			tooltipModeCB.addItem(app.getMenu("Labeling.automatic")); // index 0
			tooltipModeCB.addItem(app.getMenu("on")); // index 1
			tooltipModeCB.addItem(app.getMenu("off")); // index 2
			tooltipModeCB.addItem(app.getPlain("Caption")); // index 3
			tooltipModeCB.addItem(app.getPlain("NextCell")); // index 4 Michael Borcherds
			
			tooltipModeCB.setSelectedIndex(selectedIndex);
			tooltipModeCB.addActionListener(this);
			

		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			tooltipModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLabelMode = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];

				//	same tooltip mode
				if (geo0.getLabelMode() != temp.getTooltipMode())
					equalLabelMode = false;

			}
			
			//	set label visible checkbox
			if (equalLabelMode)
				tooltipModeCB.setSelectedIndex(geo0.getTooltipMode());
			else
				tooltipModeCB.setSelectedItem(null);

			// locus in selection
			tooltipModeCB.addActionListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isDrawable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		/**
		* action listener implementation for label mode combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == tooltipModeCB) {
				GeoElement geo;
				int mode = tooltipModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setTooltipMode(mode);
				}
			}
		}

	} // TooltipPanel

	/*
	 * panel with layers properties
	 Michael Borcherds
	 */
	private class LayerPanel
		extends JPanel
		implements ItemListener, ActionListener , UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox layerModeCB;
		private JLabel layerLabel;

		public LayerPanel() {
			layerLabel = new JLabel();	
			layerLabel.setLabelFor(layerModeCB);

			// combo box for label mode: name or algebra
			layerModeCB = new JComboBox();
			
			for (int layer = 0; layer <= EuclidianView.MAX_LAYERS; ++layer) {
				layerModeCB.addItem(" "+layer); 
			}
			
			layerModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(layerLabel);
			add(layerModeCB);			
		}
		
		public void setLabels() {
			layerLabel.setText(app.getPlain("Layer") + ":");
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			layerModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLayer = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				//	same label visible value
				if (geo0.getLayer() != temp.getLayer())
					equalLayer = false;
			}
			

			if (equalLayer)
				layerModeCB.setSelectedIndex(geo0.getLayer());
			else
				layerModeCB.setSelectedItem(null);

			// locus in selection
			layerModeCB.addActionListener(this);
			return this;
		}

		// show everything that's drawable
		// don't want layers for dependent numbers as we want to 
		// minimise the XML for such objects to keep the spreadsheet fast
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement)geos[i]).isDrawable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		/**
		* action listener implementation for label mode combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == layerModeCB) {
				GeoElement geo;
				int layer = layerModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLayer(layer);
					geo.updateRepaint();
				}
			}
		}

	} // LayersPanel

	/**
	 * panel for trace
	 * @author Markus Hohenwarter
	 */
	private class TracePanel extends JPanel implements ItemListener,  UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showTraceCB;

		public TracePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			showTraceCB = new JCheckBox();
			showTraceCB.addItemListener(this);
			add(showTraceCB);
		}
		
		public void setLabels() {
			showTraceCB.setText(app.getPlain("ShowTrace"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showTraceCB.removeItemListener(this);

			// check if properties have same values
			Traceable temp, geo0 = (Traceable) geos[0];
			boolean equalTrace = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (Traceable) geos[i];
				// same object visible value
				if (geo0.getTrace() != temp.getTrace())
					equalTrace = false;
			}

			// set trace visible checkbox
			if (equalTrace)
				showTraceCB.setSelected(geo0.getTrace());
			else
				showTraceCB.setSelected(false);

			showTraceCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof Traceable)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Traceable geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showTraceCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (Traceable) geos[i];
					geo.setTrace(showTraceCB.isSelected());
					geo.updateRepaint();
				}
			}
		}
	}
	/**
	 * panel for trace
	 * @author adapted from TracePanel
	 */
	private class AnimatingPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showAnimatingCB;

		public AnimatingPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for animating
			showAnimatingCB = new JCheckBox();
			showAnimatingCB.addItemListener(this);
			add(showAnimatingCB);
		}
		
		public void setLabels() {
			showAnimatingCB.setText(app.getPlain("Animating"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showAnimatingCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalAnimating = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isAnimating() != temp.isAnimating())
					equalAnimating = false;
			}

			// set animating checkbox
			if (equalAnimating)
				showAnimatingCB.setSelected(geo0.isAnimating());
			else
				showAnimatingCB.setSelected(false);

			showAnimatingCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement)geos[i]).isAnimatable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// animating value changed
			if (source == showAnimatingCB) {
				boolean animate = showAnimatingCB.isSelected();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAnimating(animate);
					geo.updateRepaint();
				}
				
				// make sure that we are animating 
				if (animate)
					kernel.getAnimatonManager().startAnimation();				
			}
		}
	}
	
	
	
	
	
	
	/**
	 * panel to say if an image is to be interpolated
	 */
	private class CheckBoxInterpolateImage extends JPanel implements ItemListener, SetLabels, UpdateablePanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox checkbox;

		public CheckBoxInterpolateImage() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			checkbox = new JCheckBox();
			checkbox.addItemListener(this);			
			add(checkbox);			
		}
		
		public void setLabels() {
			checkbox.setText(app.getPlain("Interpolate"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			checkbox.removeItemListener(this);

			// check if properties have same values
			GeoImage temp, geo0 = (GeoImage) geos[0];
			boolean equalObjectVal = true;
			
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoImage) geos[i];
				// same object visible value
				if (geo0.isInterpolate() != temp.isInterpolate()) {
					equalObjectVal = false;
					break;
				}								
			}

			// set object visible checkbox
			if (equalObjectVal)
				checkbox.setSelected(geo0.isInterpolate());
			else
				checkbox.setSelected(false);
			
			checkbox.addItemListener(this);
			return this;
		}

		// only images
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoImage)) 
 					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == checkbox) {
				for (int i = 0; i < geos.length; i++) {
					GeoImage image = (GeoImage) geos[i];
					image.setInterpolate(checkbox.isSelected());
					image.updateRepaint();
				}
			}
			updateSelection(geos);
		}

	} // CheckBoxInterpolateImage


	/**
	 * panel for fixing an object
	 * @author Markus Hohenwarter
	 */
	private class FixPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showFixCB;

		public FixPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			showFixCB = new JCheckBox();
			showFixCB.addItemListener(this);
			add(showFixCB);
		}
		
		public void setLabels() {
			showFixCB.setText(app.getPlain("FixObject"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showFixCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalFix = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isFixed() != temp.isFixed())
					equalFix = false;
			}

			// set trace visible checkbox
			if (equalFix)
				showFixCB.setSelected(geo0.isFixed());
			else
				showFixCB.setSelected(false);

			showFixCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isFixable())
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showFixCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setFixed(showFixCB.isSelected());
					geo.updateRepaint();
				}
			}		
			
			updateSelection(geos);
		}
	}

	private class IneqStylePanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showOnAxis;

		public IneqStylePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			showOnAxis = new JCheckBox();
			showOnAxis.addItemListener(this);
			add(showOnAxis);
		}
		
		public void setLabels() {
			showOnAxis.setText(app.getPlain("ShowOnXAxis"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showOnAxis.removeItemListener(this);

			// check if properties have same values
			if(!(geos[0] instanceof GeoFunction))
				return null;
			GeoFunction temp, geo0 = (GeoFunction) geos[0];
			boolean equalFix = true;

			for (int i = 0; i < geos.length; i++) {
				if(!(geos[i] instanceof GeoFunction))
					return null;
				temp = (GeoFunction) geos[i];
				
				if (geo0.showOnAxis() != temp.showOnAxis())
					equalFix = false;
			}

			// set trace visible checkbox
			if (equalFix){
				showOnAxis.setSelected(geo0.showOnAxis());
				if(geo0.showOnAxis())
					fillingPanel.setAllEnabled(false);
			}
			else
				showOnAxis.setSelected(false);

			showOnAxis.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if(!(geos[i] instanceof GeoFunction))
					return false;
				GeoFunction gfun = (GeoFunction) geos[i];
				if (!gfun.isBooleanFunction() || gfun.getVarString().equals("y"))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoFunction geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showOnAxis) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoFunction) geos[i];
					geo.setShowOnAxis(showOnAxis.isSelected());
					geo.updateRepaint();
					
				}
				fillingPanel.setAllEnabled(!showOnAxis.isSelected());
			}		
			
			updateSelection(geos);
		}
	}

	
	/**
	 * panel to set object's absoluteScreenLocation flag
	 * @author Markus Hohenwarter
	 */
	private class AbsoluteScreenLocationPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox cbAbsScreenLoc;

		public AbsoluteScreenLocationPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			cbAbsScreenLoc = new JCheckBox();
			cbAbsScreenLoc.addItemListener(this);

			// put it all together		
			add(cbAbsScreenLoc);						
		}
		
		public void setLabels() {
			cbAbsScreenLoc.setText(app.getPlain("AbsoluteScreenLocation"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbAbsScreenLoc.removeItemListener(this);

			// check if properties have same values
			AbsoluteScreenLocateable temp, geo0 = (AbsoluteScreenLocateable) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (AbsoluteScreenLocateable) geos[i];
				// same object visible value
				if (geo0.isAbsoluteScreenLocActive() != temp.isAbsoluteScreenLocActive())
					equalVal = false;
			}

			// set checkbox
			if (equalVal)
				cbAbsScreenLoc.setSelected(geo0.isAbsoluteScreenLocActive());
			else
				cbAbsScreenLoc.setSelected(false);

			cbAbsScreenLoc.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo instanceof AbsoluteScreenLocateable) {
					AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
					if (!absLoc.isAbsoluteScreenLocateable() || geo.isGeoBoolean() || geo.isGeoButton())
						return false;
				}					
				else
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			AbsoluteScreenLocateable geo;
			Object source = e.getItemSelectable();

			// absolute screen location flag changed
			if (source == cbAbsScreenLoc) {
				boolean flag = cbAbsScreenLoc.isSelected();
				EuclidianViewInterface ev = app.getActiveEuclidianView();
				for (int i = 0; i < geos.length; i++) {
					geo = (AbsoluteScreenLocateable) geos[i];
					if (flag) {
						// convert real world to screen coords
						int x = ev.toScreenCoordX(geo.getRealWorldLocX());
						int y = ev.toScreenCoordY(geo.getRealWorldLocY());
						if (!geo.isAbsoluteScreenLocActive())
							geo.setAbsoluteScreenLoc(x, y);							
					} else {
						// convert screen coords to real world 
						double x = ev.toRealWorldCoordX(geo.getAbsoluteScreenLocX());
						double y = ev.toRealWorldCoordY(geo.getAbsoluteScreenLocY());
						if (geo.isAbsoluteScreenLocActive())
							geo.setRealWorldLoc(x, y);
					}
					geo.setAbsoluteScreenLocActive(flag);															
					geo.toGeoElement().updateRepaint();
				}
				
				updateSelection(geos);
			}
		}
	}	
	
	/**
	 * panel for angles to set whether reflex angles are allowed 
	 * @author Markus Hohenwarter
	 */
	private class AllowReflexAnglePanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox reflexAngleCB;
		private JCheckBox forceReflexAngleCB;

		public AllowReflexAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			reflexAngleCB = new JCheckBox();
			reflexAngleCB.addItemListener(this);
			forceReflexAngleCB = new JCheckBox();
			forceReflexAngleCB.addItemListener(this);
			add(reflexAngleCB);	

			add(forceReflexAngleCB);
		}
		
		public void setLabels() {
			reflexAngleCB.setText(app.getPlain("allowReflexAngle"));
			forceReflexAngleCB.setText(app.getPlain("forceReflexAngle"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

//			 Michael Borcherds 2007-11-19
			reflexAngleCB.removeItemListener(this);
			forceReflexAngleCB.removeItemListener(this);

			// check if properties have same values
			GeoAngle temp, geo0 = (GeoAngle) geos[0];
			boolean equalangleStyle=true;
			boolean allreflex=true;
			
			for (int i = 0; i < geos.length; i++) {
				temp = (GeoAngle) geos[i];
				// same object visible value
				if (temp.getAngleStyle()!=3) allreflex=false;
				if (geo0.getAngleStyle() != temp.getAngleStyle())
					equalangleStyle = false;
			}
			
			if (allreflex==true) reflexAngleCB.setEnabled(false); else reflexAngleCB.setEnabled(true);

			
			if (equalangleStyle)
			{
				switch (geo0.getAngleStyle()) {
				case 2: // acute/obtuse
					reflexAngleCB.setSelected(false);
					forceReflexAngleCB.setSelected(false);
					break;
				case 3: // force reflex
					reflexAngleCB.setSelected(true);
					forceReflexAngleCB.setSelected(true);
					break;
				default: // should be 0: anticlockwise
					reflexAngleCB.setSelected(true);
					forceReflexAngleCB.setSelected(false);
					break;
					
				}
			}
			else
			{
				reflexAngleCB.setSelected(false);
				forceReflexAngleCB.setSelected(false);
			}

			reflexAngleCB.addItemListener(this);
			forceReflexAngleCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if ((geo.isIndependent() && !isDefaults) || !(geo instanceof GeoAngle))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoAngle geo;
			Object source = e.getItemSelectable();

//Michael Borcherds 2007-11-19
			if (source == reflexAngleCB || source==forceReflexAngleCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoAngle) geos[i];
					if (forceReflexAngleCB.isSelected()) {
						geo.setAngleStyle(3);
						reflexAngleCB.setEnabled(false);
					}
					else 
					{
						reflexAngleCB.setEnabled(true);
						if (reflexAngleCB.isSelected())
							geo.setAngleStyle(0);
						else
							geo.setAngleStyle(2);							
					}
//					Michael Borcherds 2007-11-19
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel for limted paths to set whether outlying intersection points are allowed 
	 * @author Markus Hohenwarter
	 */
	private class AllowOutlyingIntersectionsPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox outlyingIntersectionsCB;

		public AllowOutlyingIntersectionsPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace			
			outlyingIntersectionsCB = new JCheckBox();
			outlyingIntersectionsCB.addItemListener(this);
			
			add(outlyingIntersectionsCB);			
		}
		
		public void setLabels() {
			outlyingIntersectionsCB.setText(app.getPlain("allowOutlyingIntersections"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			outlyingIntersectionsCB.removeItemListener(this);

			// check if properties have same values
			LimitedPath temp, geo0 = (LimitedPath) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (LimitedPath) geos[i];
				// same value?
				if (geo0.allowOutlyingIntersections() != temp.allowOutlyingIntersections())
					equalVal = false;
			}

			// set trace visible checkbox
			if (equalVal)
				outlyingIntersectionsCB.setSelected(geo0.allowOutlyingIntersections());
			else
				outlyingIntersectionsCB.setSelected(false);

			outlyingIntersectionsCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof LimitedPath))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			LimitedPath geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == outlyingIntersectionsCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (LimitedPath) geos[i];
					geo.setAllowOutlyingIntersections(outlyingIntersectionsCB.isSelected());
					geo.toGeoElement().updateRepaint();
				}
			}
		}
	}

	
 	/**
	 * panel to set a background image (only one checkbox)
	 * @author Markus Hohenwarter
	 */
	private class BackgroundImagePanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox isBGimage;

		public BackgroundImagePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			isBGimage = new JCheckBox();
			isBGimage.addItemListener(this);
			add(isBGimage);
		}
		
		public void setLabels() {
			isBGimage.setText(app.getPlain("BackgroundImage"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			isBGimage.removeItemListener(this);

			// check if properties have same values
			GeoImage temp, geo0 = (GeoImage) geos[0];
			boolean equalIsBGimage = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoImage) geos[i];
				// same object visible value
				if (geo0.isInBackground() != temp.isInBackground())
					equalIsBGimage = false;
			}

			// set trace visible checkbox
			if (equalIsBGimage)
				isBGimage.setSelected(geo0.isInBackground());
			else
				isBGimage.setSelected(false);

			isBGimage.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoImage))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoImage geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == isBGimage) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoImage) geos[i];
					geo.setInBackground(isBGimage.isSelected());
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel for making an object auxiliary 
	 * @author Markus Hohenwarter
	 */
	private class AuxiliaryObjectPanel extends JPanel implements ItemListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox auxCB;

		public AuxiliaryObjectPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			// check boxes for show trace
			auxCB = new JCheckBox();
			auxCB.addItemListener(this);
			add(auxCB);			
		}
		
		public void setLabels() {
			auxCB.setText(app.getPlain("AuxiliaryObject"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			auxCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalAux = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isAuxiliaryObject() != temp.isAuxiliaryObject())
					equalAux = false;
			}

			// set trace visible checkbox
			if (equalAux)
				auxCB.setSelected(geo0.isAuxiliaryObject());
			else
				auxCB.setSelected(false);

			auxCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			// geo should be visible in algebra view
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isAlgebraVisible()) return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == auxCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAuxiliaryObject(auxCB.isSelected());
				}
			}
		}
	}


	/**
	 * panel for location of vectors and text 
	 */
	private class StartPointPanel
		extends JPanel
		implements ActionListener, FocusListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JLabel label;
		private JComboBox cbLocation;
		private DefaultComboBoxModel cbModel;

		public StartPointPanel() {
			// textfield for animation step
			label = new JLabel();
			cbLocation = new JComboBox();
			cbLocation.setEditable(true);
			cbModel = new DefaultComboBoxModel();
			cbLocation.setModel(cbModel);
			label.setLabelFor(cbLocation);
			cbLocation.addActionListener(this);
			cbLocation.addFocusListener(this);

			// put it all together
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(cbLocation);			
		}
		
		public void setLabels() {
			label.setText(app.getPlain("StartingPoint") + ": ");
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbLocation.removeActionListener(this);

			// repopulate model with names of points from the geoList's model
			// take all points from construction
			//TreeSet points = kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != cbModel.getSize() - 1) {				
				cbModel.removeAllElements();
				cbModel.addElement(null);			
				Iterator<GeoElement> it = points.iterator();
				int count = 0;
				while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
					GeoElement p = it.next();
					cbModel.addElement(p.getLabel());				
				}
			}

			// check if properties have same values
			Locateable temp, geo0 = (Locateable) geos[0];
			boolean equalLocation = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (Locateable) geos[i];
				// same object visible value
				if (geo0.getStartPoint() != temp.getStartPoint()) {
					equalLocation = false;
					break;
				}

			}

			// set location textfield
			GeoElement p = (GeoElement) geo0.getStartPoint();
			if (equalLocation && p != null) {
				cbLocation.setSelectedItem(p.getLabel());
			} else
				cbLocation.setSelectedItem(null);

			cbLocation.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true; 
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof Locateable && !((Locateable)geo).isAlwaysFixed()) 
						||	geo.isGeoImage())					  
				{
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
			if (e.getSource() == cbLocation)
				doActionPerformed();
		}

		private void doActionPerformed() {
			String strLoc = (String) cbLocation.getSelectedItem();
			GeoPointND newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				newLoc = null;
			} else {
				newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc);
			}

			for (int i = 0; i < geos.length; i++) {
				Locateable l = (Locateable) geos[i];
				try {
					l.setStartPoint(newLoc);
					l.toGeoElement().updateRepaint();
				} catch (CircularDefinitionException e) {
					app.showError("CircularDefinition");
				}
			}

			updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed();
		}
	}

	/**
	 * panel for three corner points of an image (A, B and D)
	 */
	private class CornerPointsPanel
		extends JPanel
		implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JLabel [] labelLocation;
		private JComboBox [] cbLocation;
		private DefaultComboBoxModel [] cbModel;

		public CornerPointsPanel() {
			labelLocation = new JLabel[3];
			cbLocation = new JComboBox[3];
			cbModel = new DefaultComboBoxModel[3];
			
			// put it all together
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			// textfield for animation step
			for (int i = 0; i < 3; i++) {
				labelLocation[i] = new JLabel();
				cbLocation[i] = new JComboBox();
				cbLocation[i].setEditable(true);
				cbModel[i] = new DefaultComboBoxModel();
				cbLocation[i].setModel(cbModel[i]);
				labelLocation[i].setLabelFor(cbLocation[i]);
				cbLocation[i].addActionListener(this);
				cbLocation[i].addFocusListener(this);
				
				JPanel locPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				locPanel.add(labelLocation[i]);
				locPanel.add(cbLocation[i]);
				add(locPanel);
			}
		}
		
		public void setLabels() {
			String strLabelStart = app.getPlain("CornerPoint");
			
			for (int i = 0; i < 3; i++) {
				int pointNumber = i < 2 ? (i+1) : (i+2);
				labelLocation[i].setText(strLabelStart + " " + pointNumber + ":");
			}
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;
			
			for (int k=0; k<3; k++) {
				cbLocation[k].removeActionListener(this);					
			}
			
			// repopulate model with names of points from the geoList's model
			// take all points from construction
			TreeSet<GeoElement> points = kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
			if (points.size() != cbModel[0].getSize() - 1) {			
				// clear models
				for (int k=0; k<3; k++) {					
					cbModel[k].removeAllElements();
					cbModel[k].addElement(null);
				}
								
				// insert points
				Iterator<GeoElement> it = points.iterator();
				int count=0;
				while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
					GeoPointND p = (GeoPointND)it.next();	
					
					for (int k=0; k<3; k++) {
						cbModel[k].addElement(p.getLabel());
					}
				}
			}

			for (int k=0; k<3; k++) {				
				// check if properties have same values
				GeoImage temp, geo0 = (GeoImage) geos[0];
				boolean equalLocation = true;
	
				for (int i = 0; i < geos.length; i++) {
					temp = (GeoImage) geos[i];
					// same object visible value
					if (geo0.getCorner(k) != temp.getCorner(k)) {
						equalLocation = false;
						break;
					}	
				}
	
				// set location textfield
				GeoPoint p = geo0.getCorner(k);
				if (equalLocation && p != null) {
					cbLocation[k].setSelectedItem(p.getLabel());
				} else
					cbLocation[k].setSelectedItem(null);
	
				cbLocation[k].addActionListener(this);
			}
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo instanceof GeoImage) {
					GeoImage img = (GeoImage) geo;
					if (img.isAbsoluteScreenLocActive() ||
							!img.isIndependent())
						return false;					
				} else
					return false;
			}
			return true;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {	
			doActionPerformed(e.getSource());
		}

		private void doActionPerformed(Object source) {
			int number = 0;
			if (source == cbLocation[1])
				number = 1;
			else if (source == cbLocation[2])
				number = 2;
			
			String strLoc = (String) cbLocation[number].getSelectedItem();
			GeoPointND newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				newLoc = null;
			} else {
				newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc);
			}

			for (int i = 0; i < geos.length; i++) {
				GeoImage im = (GeoImage) geos[i];		
				im.setCorner((GeoPoint) newLoc, number);
				im.updateRepaint();				
			}

			updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed(e.getSource());
		}
	}

	/**
	 * panel for text editing
	 */
	private class TextEditPanel
		extends JPanel
		implements ActionListener, UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;	
		private TextInputDialog td;
		private JPanel editPanel;
		
		public TextEditPanel() {
			td = new TextInputDialog(app, app.getPlain("Text"), null, null,
										30, 5, false);
			setLayout(new BorderLayout());
			
			
			editPanel = new JPanel(new BorderLayout(0,0));
			editPanel.add(td.getInputPanel(), BorderLayout.CENTER);
			editPanel.add(td.getToolBar(), BorderLayout.SOUTH);
			editPanel.setBorder(BorderFactory.createEtchedBorder());
			
			
			JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPanel, td.getPreviewPanel());
			sp.setResizeWeight(0.5);
			sp.setBorder(BorderFactory.createEmptyBorder());
				
			add(sp, BorderLayout.CENTER);
			//add(td.getPreviewPanel(), BorderLayout.NORTH);
			add(td.getButtonPanel(), BorderLayout.SOUTH);
			
			
		}
		
		public void setLabels() {
			//editPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Edit")));
			//td.getPreviewPanel().setBorder(BorderFactory.createTitledBorder(app.getMenu("Preview")));
			td.setLabels(app.getPlain("Text"));
		}

		public JPanel update(Object[] geos) {			
			if (geos.length != 1 || !checkGeos(geos))
				return null;			
			
			GeoText text = (GeoText) geos[0];			
			td.setGeoText(text);
			td.updateRecentSymbolTable();
			
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			return geos.length == 1 && geos[0] instanceof GeoText
			&& !((GeoText)geos[0]).isTextCommand() && !((GeoText)geos[0]).isFixed();	
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			//if (e.getSource() == btEdit)
			//	app.showTextDialog((GeoText) geos[0]);
		}
	}

	/**
	 * panel for script editing
	 */
	private class ScriptEditPanel
		extends JPanel
		implements ActionListener, UpdateablePanel, SetLabels {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;	
		private ScriptInputDialog clickDialog, updateDialog, globalDialog;
		private JTabbedPane tabbedPane;
		private JPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;
		public ScriptEditPanel() {	
			super(new BorderLayout());
			
			tabbedPane = new JTabbedPane();
			
			
			clickDialog = new ScriptInputDialog(app, app.getPlain("Script"), null,
					35, 15, false, false);
			updateDialog = new ScriptInputDialog(app, app.getPlain("JavaScript"), null,
					35, 15, true, false);
			globalDialog = new ScriptInputDialog(app, app.getPlain("GlobalJavaScript"), null,
					35, 15, false, true);
			setLayout(new BorderLayout());
			//add(td.getInputPanel(), BorderLayout.NORTH);
			//add(td2.getInputPanel(), BorderLayout.CENTER);
			clickScriptPanel = new JPanel(new BorderLayout(0,0));
			clickScriptPanel.add(clickDialog.getInputPanel(), BorderLayout.NORTH);
			clickScriptPanel.add(clickDialog.getButtonPanel(), BorderLayout.EAST);
			
			updateScriptPanel = new JPanel(new BorderLayout(0,0));
			updateScriptPanel.add(updateDialog.getInputPanel(), BorderLayout.NORTH);
			updateScriptPanel.add(updateDialog.getButtonPanel(), BorderLayout.EAST);
			
			globalScriptPanel = new JPanel(new BorderLayout(0,0));
			globalScriptPanel.add(globalDialog.getInputPanel(), BorderLayout.NORTH);
			globalScriptPanel.add(globalDialog.getButtonPanel(), BorderLayout.EAST);
			
			add(tabbedPane, BorderLayout.CENTER);
			
		}
		
		public void setLabels() {
			//setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
			clickDialog.setLabels(app.getPlain("OnClick"));
			updateDialog.setLabels(app.getPlain("OnUpdate"));
			globalDialog.setLabels(app.getPlain("GlobalJavaScript"));
		}

		public JPanel update(Object[] geos) {			
			if (geos.length != 1 || !checkGeos(geos))
				return null;			
			
			GeoElement button = (GeoElement) geos[0];	
			clickDialog.setGeo(button);
			updateDialog.setGeo(button);	
			globalDialog.setGlobal();
			tabbedPane.removeAll();
			if(button.canHaveClickScript())tabbedPane.addTab(app.getPlain("OnClick"), clickScriptPanel);
			if(button.canHaveUpdateScript())tabbedPane.addTab(app.getPlain("OnUpdate"), updateScriptPanel);
			tabbedPane.addTab(app.getPlain("GlobalJavaScript"), globalScriptPanel);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			//return geos.length == 1 && geos[0] instanceof GeoJavaScriptButton;			
			return geos.length == 1;			
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			//if (e.getSource() == btEdit)
			//	app.showTextDialog((GeoText) geos[0]);
		}
	}
	
	/**
	 * panel to select the kind of coordinates (cartesian or polar)
	 *  for GeoPoint and GeoVector
	 * @author Markus Hohenwarter
	 */
	private class CoordPanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JLabel coordLabel;
		private JComboBox coordCB;		

		public CoordPanel() {
			coordLabel = new JLabel();
			coordCB = new JComboBox();
			
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(coordLabel);
			add(coordCB);
		}
		
		public void setLabels() {
			coordLabel.setText(app.getPlain("Coordinates")+":");
			
			int selectedIndex = coordCB.getSelectedIndex();
			coordCB.removeActionListener(this);
			
			coordCB.removeAllItems();
			coordCB.addItem(app.getPlain("CartesianCoords")); // index 0
			coordCB.addItem(app.getPlain("PolarCoords")); // index 1
			coordCB.addItem(app.getPlain("ComplexNumber")); // index 2
			
			coordCB.setSelectedIndex(selectedIndex);
			coordCB.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			coordCB.removeActionListener(this);

			//	check if properties have same values
			GeoVec3D geo0 = (GeoVec3D) geos[0];
			boolean equalMode = true;

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
				case Kernel.COORD_CARTESIAN :
					coordCB.setSelectedIndex(0);
					break;
				case Kernel.COORD_POLAR :
					coordCB.setSelectedIndex(1);
					break;
				case Kernel.COORD_COMPLEX :
					coordCB.setSelectedIndex(2);
					break;
				default :
					coordCB.setSelectedItem(null);
			}

			coordCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			//boolean allPoints = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoPoint
					|| geos[i] instanceof GeoVector)) {
					geosOK = false;
				}
				
				// check if fixed
				if (((GeoElement)geos[i]).isFixed()) geosOK = false;
				
				//if (!(geos[i] instanceof GeoPoint)) allPoints = false;
			}
			
			// remove ComplexNumber option if any vectors are in list
			//if (!allPoints && coordCB.getItemCount() == 3) coordCB.removeItemAt(2);
			
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == coordCB) {
				GeoVec3D geo;
				switch (coordCB.getSelectedIndex()) {
					case 0 : // Kernel.CARTESIAN					
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoVec3D) geos[i];
							geo.setMode(Kernel.COORD_CARTESIAN);
							geo.updateRepaint();
						}
						break;

					case 1 : // Kernel.POLAR					
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoVec3D) geos[i];
							geo.setMode(Kernel.COORD_POLAR);
							geo.updateRepaint();
						}
						break;
					case 2 : // Kernel.COMPLEX					
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoVec3D) geos[i];
								geo.setMode(Kernel.COORD_COMPLEX);
								geo.updateRepaint();
						}
						break;
				}
			}
		}
	}

	/**
	 * panel to select the kind of line equation 
	 *  for GeoLine 
	 * @author Markus Hohenwarter
	 */
	private class LineEqnPanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JComboBox eqnCB;
		private JLabel eqnLabel;

		public LineEqnPanel() {
			eqnLabel = new JLabel();
			eqnCB = new JComboBox();
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}
		
		public void setLabels() {
			eqnLabel.setText(app.getPlain("Equation") + ":");
			
			int selectedIndex = eqnCB.getSelectedIndex();
			eqnCB.removeActionListener(this);
			
			eqnCB.removeAllItems();
			eqnCB.addItem(app.getPlain("ImplicitLineEquation"));
			// index 0
			eqnCB.addItem(app.getPlain("ExplicitLineEquation"));
			// index 1		
			eqnCB.addItem(app.getPlain("ParametricForm")); // index 2
			
			eqnCB.setSelectedIndex(selectedIndex);
			eqnCB.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			//	check if properties have same values
			GeoLine temp, geo0 = (GeoLine) geos[0];
			boolean equalMode = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoLine) geos[i];
				// same mode?
				if (geo0.getMode() != temp.getMode())
					equalMode = false;
			}

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
				case GeoLine.EQUATION_IMPLICIT :
					eqnCB.setSelectedIndex(0);
					break;
				case GeoLine.EQUATION_EXPLICIT :
					eqnCB.setSelectedIndex(1);
					break;
				case GeoLine.PARAMETRIC :
					eqnCB.setSelectedIndex(2);
					break;
				default :
					eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoLine)
					|| geos[i] instanceof GeoSegment) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoLine geo;
				switch (eqnCB.getSelectedIndex()) {
					case 0 : // GeoLine.EQUATION_IMPLICIT				
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.EQUATION_IMPLICIT);
							geo.updateRepaint();
						}
						break;

					case 1 : // GeoLine.EQUATION_EXPLICIT				
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.EQUATION_EXPLICIT);
							geo.updateRepaint();
						}
						break;

					case 2 : // GeoLine.PARAMETRIC	
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.PARAMETRIC);
							geo.updateRepaint();
						}
						break;
				}
			}
		}
	}

	/**
	 * panel to select the kind of conic equation 
	 *  for GeoConic 
	 * @author Markus Hohenwarter
	 */
	private class ConicEqnPanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private DefaultComboBoxModel eqnCBmodel;
		private JComboBox eqnCB;
		private JLabel eqnLabel;
		int implicitIndex, explicitIndex, specificIndex;

		public ConicEqnPanel() {
			eqnLabel = new JLabel();
			eqnCB = new JComboBox();
			eqnCBmodel = new DefaultComboBoxModel();
			eqnCB.setModel(eqnCBmodel);
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}
		
		public void setLabels() {
			eqnLabel.setText(app.getPlain("Equation") + ":");
			
			if(geos != null)
				update(geos);
			
			// TODO: Anything else required? (F.S.)
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			// check if all conics have same type and mode
			// and if specific, explicit is possible		 
			GeoConic temp, geo0 = (GeoConic) geos[0];
			boolean equalType = true;
			boolean equalMode = true;
			boolean specificPossible = geo0.isSpecificPossible();
			boolean explicitPossible = geo0.isExplicitPossible();
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoConic) geos[i];
				// same type?
				if (geo0.getType() != temp.getType())
					equalType = false;
				// same mode?
				if (geo0.getToStringMode() != temp.getToStringMode())
					equalMode = false;
				// specific equation possible?
				if (!temp.isSpecificPossible())
					specificPossible = false;
				// explicit equation possible?
				if (!temp.isExplicitPossible())
					explicitPossible = false;
			}

			// specific can't be shown because there are different types
			if (!equalType)
				specificPossible = false;

			specificIndex = -1;
			explicitIndex = -1;
			implicitIndex = -1;
			int counter = -1;
			eqnCBmodel.removeAllElements();
			if (specificPossible) {
				eqnCBmodel.addElement(geo0.getSpecificEquation());
				specificIndex = ++counter;
			}
			if (explicitPossible) {
				eqnCBmodel.addElement(app.getPlain("ExplicitConicEquation"));
				explicitIndex = ++counter;
			}
			implicitIndex = ++counter;
			eqnCBmodel.addElement(app.getPlain("ImplicitConicEquation"));

			int mode;
			if (equalMode)
				mode = geo0.getToStringMode();
			else
				mode = -1;
			switch (mode) {
				case GeoConic.EQUATION_SPECIFIC :
					if (specificIndex > -1)
						eqnCB.setSelectedIndex(specificIndex);
					break;

				case GeoConic.EQUATION_EXPLICIT :
					if (explicitIndex > -1)
						eqnCB.setSelectedIndex(explicitIndex);
					break;

				case GeoConic.EQUATION_IMPLICIT :
					eqnCB.setSelectedIndex(implicitIndex);
					break;

				default :
					eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i].getClass() != GeoConic.class) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoConic geo;
				int selIndex = eqnCB.getSelectedIndex();
				if (selIndex == specificIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToSpecific();
						geo.updateRepaint();
					}
				} else if (selIndex == explicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToExplicit();
						geo.updateRepaint();
					}
				} else if (selIndex == implicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToImplicit();
						geo.updateRepaint();
					}
				}
			}
		}
	}

	/**
	 * panel to select the size of a GeoPoint
	 * @author Markus Hohenwarter
	 */
	private class PointSizePanel extends JPanel implements ChangeListener, SetLabels, UpdateablePanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public PointSizePanel() {	
			super(new FlowLayout(FlowLayout.LEFT));
					
			//setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
		
			slider = new JSlider(1, 9);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);	
			
			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);		
			slider.setPreferredSize(dim);	
			*/

			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);			
			
			add(slider);			
		}
		
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("PointSize")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			PointProperties geo0 = (PointProperties) geos[0];
			slider.setValue(geo0.getPointSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement)geos[i];
				if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
						&& (!(geo.isGeoList() && ((GeoList)geo).showPointProperties()))) {
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
				PointProperties point;
				for (int i = 0; i < geos.length; i++) {
					point = (PointProperties) geos[i];
					point.setPointSize(size);
					point.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to change the point style
	 * @author Florian Sonner
	 * @version 2008-07-17
	 */
	private class PointStylePanel extends JPanel implements UpdateablePanel, SetLabels, ActionListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JRadioButton[] buttons;
		private JComboBox cbStyle;  //G.Sturr 2010-1-24
		
		public PointStylePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			

			//G.STURR 2010-1-24 
			// Point styles were previously displayed with fonts,
			// but not all point styles had font equivalents. This is
			// now replaced by a comboBox holding rendered point styles 
			// and radio buttons to select default or custom point style.
			
			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18,18));		
			cbStyle = new JComboBox(EuclidianView.getPointStyles());
			cbStyle.setRenderer(renderer);
			cbStyle.setMaximumRowCount(EuclidianView.MAX_POINT_STYLE+1);
			cbStyle.setBackground(getBackground());
			cbStyle.addActionListener(this);
			
			buttons = new JRadioButton[2];
			buttons[0] = new JRadioButton(app.getPlain("Default"));			
			buttons[0].setActionCommand("default");
			buttons[0].addActionListener(this);
			buttons[1] = new JRadioButton();
			add(buttons[0]);
			add(buttons[1]);
			
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(buttons[0]);
			buttonGroup.add(buttons[1]);
			
			add(cbStyle);
		
			/* ----- old code
			ButtonGroup buttonGroup = new ButtonGroup();
			
			String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716" };
			String[] strPointStyleAC = { "0", "2", "1" };
			buttons = new JRadioButton[strPointStyle.length];
			
			for(int i = 0; i < strPointStyle.length; ++i) {
				buttons[i] = new JRadioButton(strPointStyle[i]);
				buttons[i].setActionCommand(strPointStyleAC[i]);
				buttons[i].addActionListener(this);
				buttons[i].setFont(app.getSmallFont());
				
				if(!strPointStyleAC[i].equals("-1"))
					buttons[i].setFont(app.getSmallFont());
				
				buttonGroup.add(buttons[i]);
				add(buttons[i]);
			}		
			*/
			
			//END G.STURR
			
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getMenu("PointStyle") ));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;

			//	set value to first point's style 
			PointProperties geo0 = (PointProperties) geos[0];


			//G.STURR 2010-1-24: 
			// update comboBox and radio buttons
			cbStyle.removeActionListener(this);
			if(geo0.getPointStyle() == -1){   
				// select default button
				buttons[0].setSelected(true);
				cbStyle.setSelectedIndex(app.getEuclidianView().getPointStyle());
				
			} else {   
				// select custom button and set combo box selection
				buttons[1].setSelected(true);
				cbStyle.setSelectedIndex(geo0.getPointStyle());
			}
			cbStyle.addActionListener(this);
		
			
			/* ----- old code to update radio button group			  
			for(int i = 0; i < buttons.length; ++i) {
				if(Integer.parseInt(buttons[i].getActionCommand()) == geo0.getPointStyle())
					buttons[i].setSelected(true);
				else
					buttons[i].setSelected(false);
			}
			*/
			
			//END G.STURR
			
			
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement)geos[i];
				if (geo.isGeoElement3D() || //TODO add point style to 3D points
						(!geo.getGeoElementForPropertiesDialog().isGeoPoint()
						&& (!(geo.isGeoList() && ((GeoList)geo).showPointProperties())))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			//G.STURR 2010-1-24: 
			//Handle comboBox and radio button clicks
			
			//int style = Integer.parseInt(e.getActionCommand());
			int style = -1;
			   // comboBox click
			if (e.getSource() == cbStyle){
				style = cbStyle.getSelectedIndex();
				buttons[1].removeActionListener(this);
				buttons[1].setSelected(true);
				buttons[1].addActionListener(this);
			}	
			   // default button click	
			if (e.getActionCommand()=="default"){	
				cbStyle.removeActionListener(this);
				cbStyle.setSelectedIndex(app.getEuclidianView().getPointStyle());
				cbStyle.addActionListener(this);
			}	
			// END G.STURR	
			
			
			PointProperties point;
			for (int i = 0; i < geos.length; i++) {
				point = (PointProperties) geos[i];
				point.setPointStyle(style);
				point.updateRepaint();
			}
		}
	}
	
	/**
	 * panel to select the size of a GeoText
	 * @author Markus Hohenwarter
	 */
	private class TextOptionsPanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		
		private JLabel decimalLabel;
		private JComboBox cbFont, cbSize, cbDecimalPlaces;		
		private JToggleButton btBold, btItalic;
		
		private JPanel secondLine;
		private boolean secondLineVisible = false;
		private boolean justDisplayFontSize = true;

		
		public TextOptionsPanel() {	
			// font: serif, sans serif
			String [] fonts = { "Sans Serif", "Serif" };
			cbFont = new JComboBox(fonts);
			cbFont.addActionListener(this);	
			
			// font size	
			// TODO require font phrases F.S.
			cbSize = new JComboBox(new String[] { app.getPlain("ExtraSmall"), app.getPlain("Small"), app.getPlain("Medium"), app.getPlain("Large"), app.getPlain("ExtraLarge") });
			cbSize.addActionListener(this);						
			
			// toggle buttons for bold and italic
			btBold = new JToggleButton();
			btBold.setFont(app.getBoldFont());
			btBold.addActionListener(this);			
			btItalic = new JToggleButton();
			btItalic.setFont(app.getPlainFont().deriveFont(Font.ITALIC));
			btItalic.addActionListener(this);		
			
			// decimal places			
			ComboBoxRenderer renderer = new ComboBoxRenderer();
		    cbDecimalPlaces = new JComboBox(app.getRoundingMenu());
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
			decimalLabel  = new JLabel();					
			secondLine.add(decimalLabel);
			secondLine.add(cbDecimalPlaces);									
			
			setLayout(new BorderLayout(5,5));
			add(firstLine, BorderLayout.NORTH);
			add(secondLine, BorderLayout.SOUTH);	
			secondLineVisible = true;
		}
		
		public void setLabels() {
			String[] fontSizes = app.getFontSizeStrings();
			
			int selectedIndex = cbSize.getSelectedIndex();
			cbSize.removeActionListener(this);
			cbSize.removeAllItems();
			
			for(int i = 0; i < fontSizes.length; ++i) {
				cbSize.addItem(fontSizes[i]);
			}
			
			cbSize.setSelectedIndex(selectedIndex);
			cbSize.addActionListener(this);
			
			btItalic.setText(app.getPlain("Italic").substring(0,1));
			btBold.setText(app.getPlain("Bold").substring(0,1));
			
			decimalLabel.setText(app.getMenu("Rounding") + ":");
		}

		class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		    JSeparator separator;

		    public ComboBoxRenderer() {
		      setOpaque(true);
		      setBorder(new EmptyBorder(1, 1, 1, 1));
		      separator = new JSeparator(JSeparator.HORIZONTAL);
		    }

		    public Component getListCellRendererComponent(JList list, Object value,
		        int index, boolean isSelected, boolean cellHasFocus) {
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
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			
			// hide most options for Buttons / Textfields
			cbFont.setVisible(!justDisplayFontSize);
			btBold.setVisible(!justDisplayFontSize);
			btItalic.setVisible(!justDisplayFontSize);
			secondLine.setVisible(!justDisplayFontSize);
			secondLineVisible = !justDisplayFontSize;
			
			this.geos = geos;			
			
			cbSize.removeActionListener(this);
			cbFont.removeActionListener(this);
			cbDecimalPlaces.removeActionListener(this);

			//	set value to first text's size and style
			TextProperties geo0 = (TextProperties) geos[0];		
			
			cbSize.setSelectedIndex(GeoText.getFontSizeIndex(geo0.getFontSize())); // font size ranges from -6 to 6, transform this to 0,1,..,6
			cbFont.setSelectedIndex(geo0.isSerifFont() ? 1 : 0);
			int selItem = -1;
			
			int decimals = geo0.getPrintDecimals();
			if (decimals > 0 && decimals < Application.decimalsLookup.length && !geo0.useSignificantFigures())
				selItem = Application.decimalsLookup[decimals];

			int figures = geo0.getPrintFigures();
			if (figures > 0 && figures < Application.figuresLookup.length && geo0.useSignificantFigures())
				selItem = Application.figuresLookup[figures];
			
			cbDecimalPlaces.setSelectedIndex(selItem);
			
			if (((GeoElement)geo0).isIndependent()
					|| (geo0 instanceof GeoList)) { // don't want rounding option for lists of texts?
				if (secondLineVisible) {
					remove(secondLine);	
					secondLineVisible = false;
				}				
			} else {
				if (!secondLineVisible) {
					add(secondLine, BorderLayout.SOUTH);
					secondLineVisible = true;
				}	
			}
		
			int style = geo0.getFontStyle();
			btBold.setSelected(style == Font.BOLD || style == (Font.BOLD + Font.ITALIC));
			btItalic.setSelected(style == Font.ITALIC || style == (Font.BOLD + Font.ITALIC));				
			
			
			
			cbSize.addActionListener(this);
			cbFont.addActionListener(this);			
			cbDecimalPlaces.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			justDisplayFontSize = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement)geos[i];
				
				if (geo instanceof TextProperties && !((TextProperties)geo).justFontSize()) {
					justDisplayFontSize  = false;
				}
				
				if (!(geo.getGeoElementForPropertiesDialog().isGeoText())) {
					if (!((GeoElement)geos[i]).isGeoButton()) {
						geosOK = false;
						break;
					}
				} 
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			
			if (source == cbSize) {			
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setFontSize(GeoText.getRelativeFontSize(cbSize.getSelectedIndex())); // transform indices to the range -6, .. , 6
					((GeoElement)text).updateRepaint();
				}
			} 
			else if (source == cbFont) {
				boolean serif = cbFont.getSelectedIndex() == 1;		
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setSerifFont(serif);
					((GeoElement)text).updateRepaint();
				}
			}
			else if (source == cbDecimalPlaces) {
				int decimals = cbDecimalPlaces.getSelectedIndex();
				//Application.debug(decimals+"");
				//Application.debug(roundingMenuLookup[decimals]+"");
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					if (decimals < 8) // decimal places
					{
						//Application.debug("decimals"+roundingMenuLookup[decimals]+"");
						text.setPrintDecimals(Application.roundingMenuLookup[decimals], true);
					}
					else // significant figures
					{
						//Application.debug("figures"+roundingMenuLookup[decimals]+"");
						text.setPrintFigures(Application.roundingMenuLookup[decimals], true);
					}
					((GeoElement)text).updateRepaint();
				}
			}
			else if (source == btBold || source == btItalic) {
				int style = 0;
				if (btBold.isSelected()) style += 1;
				if (btItalic.isSelected()) style += 2;
					
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setFontStyle(style);
					((GeoElement)text).updateRepaint();
				}
			}								
		}
	}
	
	/**
	 * panel to select the size of a GeoPoint
	 * @author Markus Hohenwarter
	 */
	private class SlopeTriangleSizePanel
		extends JPanel
		implements ChangeListener, UpdateablePanel, SetLabels {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public SlopeTriangleSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
			slider = new JSlider(1, 10);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;			
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);

			/*
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
			sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));	
			add(Box.createRigidArea(new Dimension(5,0)));
			add(sizeLabel);
			*/		
			add(slider);			
		}
		
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoNumeric geo0 = (GeoNumeric) geos[0];
			slider.setValue(geo0.getSlopeTriangleSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof GeoNumeric
					&& geo.getParentAlgorithm() instanceof AlgoSlope)) {
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
				GeoNumeric num;
				for (int i = 0; i < geos.length; i++) {
					num = (GeoNumeric) geos[i];
					num.setSlopeTriangleSize(size);
					num.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select the size of a GeoAngle's arc
	 * @author Markus Hohenwarter
	 */
	private class ArcSizePanel extends JPanel implements ChangeListener, SetLabels, UpdateablePanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public ArcSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
			slider = new JSlider(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			/*
			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
			sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));
			add(Box.createRigidArea(new Dimension(5,0)));
			add(sizeLabel);
			*/		
			add(slider);			
		}
						
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}
		
		//added by Loic BEGIN
		public void setMinValue(){
			slider.setValue(20);
		}
		// END
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoAngle geo0 = (GeoAngle) geos[0];
			slider.setValue(geo0.getArcSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoAngle) {
					GeoAngle angle = (GeoAngle) geos[i];
					if (angle.isIndependent() || !angle.isDrawable()) {
						geosOK = false;
						break;
					}
				} else {
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
				GeoAngle angle;
				for (int i = 0; i < geos.length; i++) {
					angle = (GeoAngle) geos[i];
					// addded by Loic BEGIN
					// check if decoration could be drawn
					if (size<20&&(angle.decorationType==GeoElement.DECORATION_ANGLE_THREE_ARCS
							|| angle.decorationType==GeoElement.DECORATION_ANGLE_TWO_ARCS)){
						angle.setArcSize(20);
						int selected=((GeoAngle)geos[0]).decorationType;
						if (selected==GeoElement.DECORATION_ANGLE_THREE_ARCS
							|| selected==GeoElement.DECORATION_ANGLE_TWO_ARCS){
							slider.setValue(20);							
							}
						}
					//END
					else angle.setArcSize(size);
					angle.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select the filling of a polygon or conic section
	 * @author Markus Hohenwarter
	 */
	private class FillingPanel extends JPanel implements ChangeListener, SetLabels, UpdateablePanel, ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		
		private FillingPanel fillingPanel;
		
		private JSlider fillingSlider;
		private JSlider angleSlider;
		private JSlider distanceSlider;
		private JComboBox cbFillType;
		private JCheckBox cbFillInverse;
		
		private JPanel transparencyPanel, hatchFillPanel, imagePanel, anglePanel, distancePanel;
		private JLabel lblFillType;
		private JButton btnOpenFile;
		
		private PopupMenuButton btnImage;
		private String[] fileNameArray;
		private JLabel lblFillInverse;

		public FillingPanel() {
			
			fillingPanel = this; 
				
			//JLabel sizeLabel = new JLabel(app.getPlain("Filling") + ":");		
			fillingSlider = new JSlider(0, 100);
			fillingSlider.setMajorTickSpacing(25);
			fillingSlider.setMinorTickSpacing(5);
			fillingSlider.setPaintTicks(true);
			fillingSlider.setPaintLabels(true);
			fillingSlider.setSnapToTicks(true);

			angleSlider = new JSlider(0, 180);
			angleSlider.setPreferredSize(new Dimension(250,64));
			angleSlider.setMajorTickSpacing(45);
			angleSlider.setMinorTickSpacing(5);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setSnapToTicks(true);

			//Create the label table
			Hashtable<Integer,JLabel> labelHash = new Hashtable<Integer,JLabel>();
			labelHash.put( new Integer( 0 ), new JLabel("0\u00b0") );
			labelHash.put( new Integer( 45 ), new JLabel("45\u00b0") );
			labelHash.put( new Integer( 90 ), new JLabel("90\u00b0") );
			labelHash.put( new Integer( 135 ), new JLabel("135\u00b0") );
			labelHash.put( new Integer( 180 ), new JLabel("180\u00b0") );
			angleSlider.setLabelTable( labelHash );

			distanceSlider = new JSlider(5, 50);
			distanceSlider.setPreferredSize(new Dimension(150,64));
			distanceSlider.setMajorTickSpacing(10);
			distanceSlider.setMinorTickSpacing(5);
			distanceSlider.setPaintTicks(true);
			distanceSlider.setPaintLabels(true);
			distanceSlider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary<?, ?> labelTable = fillingSlider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel)en.nextElement();
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

			
			
			//========================================
			// create sub panels
			
			// panel for the fill type combobox
			cbFillType = new JComboBox();
			JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			lblFillType = new JLabel(app.getPlain("Filling") + ":");
			cbFillInverse = new JCheckBox();
			lblFillInverse = new JLabel(app.getPlain("InverseFilling"));
			cbPanel.add(lblFillType);
			cbPanel.add(cbFillType);
			cbPanel.add(cbFillInverse);
			cbPanel.add(lblFillInverse);
			
			// panels to hold sliders
			transparencyPanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
			transparencyPanel.add(fillingSlider);
				
			anglePanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
			anglePanel.add(angleSlider);
			
			distancePanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
			distancePanel.add(distanceSlider);
			
			// hatchfill panel: only shown when hatch fill option is selected
			hatchFillPanel = new JPanel();
			hatchFillPanel.setLayout(new BoxLayout(hatchFillPanel,BoxLayout.X_AXIS));
			hatchFillPanel.add(anglePanel);
			hatchFillPanel.add(distancePanel);
			hatchFillPanel.setVisible(false);
			
			// image panel: only shown when image fill option is selected
			createImagePanel();
			imagePanel.setVisible(false);
			
			
			
			//===========================================================
			// put all the sub panels together
			
			this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			this.add(cbPanel);
			this.add(transparencyPanel);
			this.add(hatchFillPanel);
			this.add(imagePanel);
			
			
			
		}
		
		public void setAllEnabled(boolean b) {
			Component[] c = this.getComponents();
			for(int i=0;i<c.length;i++){
				Component[] subc = ((JPanel)c[i]).getComponents();
				for(int j=0;j<subc.length;j++){
					subc[j].setEnabled(b);
				}
			}			
		}

		public void setLabels() {
			
			//setBorder(BorderFactory.createTitledBorder(app.getPlain("Filling")));
			
			transparencyPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Opacity")));
			anglePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Angle")));
			distancePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Spacing")));
			imagePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Images")));
			
			
			btnOpenFile.setText(app.getMenu("ChooseFromFile")+"...");
			
			
			// fill type combobox
			lblFillType = new JLabel(app.getMenu("Filling") + ":");
			
			int selectedIndex = cbFillType.getSelectedIndex();
			cbFillType.removeActionListener(this);		
			cbFillType.removeAllItems();
					
			cbFillType.addItem(app.getMenu("Filling.Standard")); // index 0
			cbFillType.addItem(app.getMenu("Filling.Hatch")); // index 1
			cbFillType.addItem(app.getMenu("Filling.Image")); // index 2
			
			cbFillType.setSelectedIndex(selectedIndex);
			cbFillType.addActionListener(this);
				
			
		}
		
		private JPanel createImagePanel(){
			
			
			//=============================================	
			// create array of image files from toolbar icons	
			// for testing only ... 
			ImageIcon[] iconArray = new ImageIcon[20];
			fileNameArray = new String[20];
			String modeStr;
			for( int i = 0; i < 20; i++) {		
				modeStr = kernel.getModeText(i).toLowerCase(Locale.US);
				fileNameArray[i]="/geogebra/gui/toolbar/images/mode_"+modeStr+"_32.gif";
				iconArray[i] = GeoGebraIcon.createFileImageIcon( app, fileNameArray[i], 1.0f, new Dimension(32,32));
			}
			//============================================
	
			
			// panel for button to open external file		
					
			btnImage = new PopupMenuButton(app, iconArray, -1,-1,new Dimension(32,32), SelectionTable.MODE_ICON);
			btnImage.addActionListener(this);			
			
			btnOpenFile = new JButton();
			btnOpenFile.addActionListener(this);
			
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.add(btnImage);
			btnPanel.add(btnOpenFile);
			
				
			
			//=====================================
			// put all sub panels together
			
			imagePanel = new JPanel(new BorderLayout());
			imagePanel.add(btnPanel, BorderLayout.CENTER);
			
			return imagePanel;
		}

		
		
		private void updateFillTypePanel(int fillType){
			
			switch(fillType){
				
			case GeoElement.FILL_STANDARD:
				hatchFillPanel.setVisible(false);
				imagePanel.setVisible(false);
				break;

			case GeoElement.FILL_HATCH:
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				break;
				
			case GeoElement.FILL_IMAGE:
				hatchFillPanel.setVisible(false);
				imagePanel.setVisible(true);
				break;

			}
		}
		
		
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			cbFillType.removeActionListener(this);
			//	set selected fill type to first geo's fill type
			cbFillType.setSelectedIndex(((GeoElement) geos[0]).getFillType());
			cbFillType.addActionListener(this);		
			
			cbFillInverse.removeActionListener(this);
			//	set selected fill type to first geo's fill type
			cbFillInverse.setSelected(((GeoElement) geos[0]).isInverseFill());
			cbFillInverse.addActionListener(this);
			updateFillTypePanel(((GeoElement) geos[0]).getFillType());
			
			
			this.geos = geos;
			fillingSlider.removeChangeListener(this);
			angleSlider.removeChangeListener(this);
			distanceSlider.removeChangeListener(this);

			//	set value to first geo's alpha value
			double alpha = ((GeoElement) geos[0]).getAlphaValue();
			fillingSlider.setValue((int) Math.round(alpha * 100));

			double angle = ((GeoElement) geos[0]).getHatchingAngle();
			angleSlider.setValue((int)angle);

			int distance = ((GeoElement) geos[0]).getHatchingDistance();
			distanceSlider.setValue(distance);

			fillingSlider.addChangeListener(this);
			angleSlider.addChangeListener(this);
			distanceSlider.addChangeListener(this);
			
		//	imageList.removeListSelectionListener(this);
		//	imageList.setSelectedValue(((GeoElement) geos[0]).getImageFileName(), true);	
		//	imageList.addListSelectionListener(this);
			
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			cbFillInverse.setVisible(true);
			lblFillInverse.setVisible(true);
			cbFillType.setVisible(true); //TODO remove this (see below)
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoFunctionNVar || geos[i] instanceof GeoFunction
						|| geos[i] instanceof GeoCurveCartesian || geos[i] instanceof GeoConic
						|| geos[i] instanceof GeoPolygon || geos[i] instanceof GeoLocus)
						|| ( ((GeoElement)geos[i]).getParentAlgorithm() instanceof AlgoTransformation)){
					cbFillInverse.setVisible(false);
					lblFillInverse.setVisible(false);
				}
				if (!((GeoElement) geos[i]).isFillable()) {
					geosOK = false;
					break;
				}
				
				//TODO add fill type for 3D elements
				if (((GeoElement) geos[i]).isGeoElement3D())
					cbFillType.setVisible(false);
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!fillingSlider.getValueIsAdjusting() && !angleSlider.getValueIsAdjusting() && !distanceSlider.getValueIsAdjusting()) {
				float alpha = fillingSlider.getValue() / 100.0f;
				int angle = angleSlider.getValue();
				int distance = distanceSlider.getValue();
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAlphaValue(alpha);
					geo.setHatchingAngle(angle);
					geo.setHatchingDistance(distance);
					geo.updateVisualStyle();
				}
			}
		}

			
		/**
		* action listener for fill type combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			GeoElement geo;
			
			// handle change in fill type
			if (source == cbFillType) {
				
				int fillType = cbFillType.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setFillType(fillType);
					
					// set default image to first imageList element
					if(fillType == GeoElement.FILL_IMAGE && geo.getFillImage() == null){
					//	String fileName = (String)(imageList.getModel()).getElementAt(0);
					//	geo.setFillImage(geo.getImageFileName());
						//imageList.setSelectedIndex(0);
						//imageTable.repaint();
					}
					
					geo.updateRepaint();
				}
				fillingPanel.updateFillTypePanel(fillType);
			}
			else if (source == cbFillInverse) {
				
				
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setInverseFill(cbFillInverse.isSelected());
					geo.updateRepaint();
				}
				
			}
			// handle image button selection 
			else if(source == this.btnImage){		
				String fileName = fileNameArray[btnImage.getSelectedIndex()];
				if(fileName != null)
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						geo.setImageFileName(fileName);
						geo.updateRepaint();
					}
			}	
			
			// handle load image file 
			else if(source == btnOpenFile){		
				String fileName = app.getGuiManager().getImageFromFile();
				if(fileName != null)
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						geo.setImageFileName(fileName);
						geo.updateRepaint();
					}
			}	
		}

	}
	

	/**
	 * panel to select thickness and style (dashing) of a GeoLine
	 * @author Markus Hohenwarter
	 */
	private class LineStylePanel
		extends JPanel
		implements ChangeListener, ActionListener, UpdateablePanel, SetLabels {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;
		private JPanel thicknessPanel;
		private JLabel dashLabel;
		private JComboBox dashCB;

		public LineStylePanel() {
			// thickness slider		
			slider = new JSlider(1, GeoElement.MAX_LINE_WIDTH);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);

			// line style combobox (dashing)		
			DashListRenderer renderer = new DashListRenderer();
			renderer.setPreferredSize(
				new Dimension(130, app.getGUIFontSize() + 6));
			dashCB = new JComboBox(EuclidianView.getLineTypes());
			dashCB.setRenderer(renderer);
			dashCB.addActionListener(this);

			// line style panel
			JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dashLabel = new JLabel();
			dashPanel.add(dashLabel);
			dashPanel.add(dashCB);

			// thickness panel
			thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			/*
			JLabel thicknessLabel = new JLabel(app.getPlain("Thickness") + ":");
			thicknessPanel.setLayout(new BoxLayout(thicknessPanel, BoxLayout.X_AXIS));	
			thicknessLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//thicknessPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));	
			thicknessPanel.add(Box.createRigidArea(new Dimension(5,0)));
			thicknessPanel.add(thicknessLabel);
			*/				
			thicknessPanel.add(slider);			

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			thicknessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			dashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);	
			add(thicknessPanel);
			add(dashPanel);			
		}
		
		public void setLabels() {
			thicknessPanel.setBorder(
					BorderFactory.createTitledBorder(app.getPlain("Thickness")));
			
			dashLabel.setText(app.getPlain("LineStyle") + ":");
		}
		
		private int maxMinimumThickness(Object[] geos) {
			
			if (geos == null || geos.length == 0) return 1;
			
			for (int i = 0  ; i < geos.length ; i++) {
				GeoElement testGeo = ((GeoElement)geos[i]).getGeoElementForPropertiesDialog();
				if (testGeo.getMinimumLineThickness() == 1) return 1;
			}
			
			return 0;
			
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);
			dashCB.removeActionListener(this);

			//	set slider value to first geo's thickness 
			GeoElement temp, geo0 = (GeoElement) geos[0];
			slider.setValue(geo0.getLineThickness());
			
			// allow polygons to have thickness 0
			slider.setMinimum(maxMinimumThickness(geos));

			//	check if geos have same line style
			boolean equalStyle = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same style?
				if (geo0.getLineType() != temp.getLineType())
					equalStyle = false;
			}

			// select common line style
			if (equalStyle) {
				int type = geo0.getLineType();				
				for (int i = 0; i < dashCB.getItemCount(); i++) {
					if (type == ((Integer) dashCB.getItemAt(i)).intValue()) {
						dashCB.setSelectedIndex(i);
						break;
					}
				}
			} else
				dashCB.setSelectedItem(null);

			slider.addChangeListener(this);
			dashCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = ((GeoElement) geos[i]).getGeoElementForPropertiesDialog();
				if (!(geo.isPath()
					|| (geo.isGeoList() && ((GeoList)geo).showLineProperties() )
					|| (geo.isGeoNumeric()
						&& (((GeoNumeric) geo).isDrawable() || isDefaults))
					|| ((geo instanceof GeoFunctionNVar)
						&& ((GeoFunctionNVar) geo).isInequality()))) {
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
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineThickness(size);
					geo.updateRepaint();
				}
			}
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dashCB) {
				GeoElement geo;
				int type = ((Integer) dashCB.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineType(type);
					geo.updateRepaint();
				}
			}
		}
	} 

	



	/**
	 * select 
	 * dash style for hidden parts.
	 * @author matthieu
	 *
	 */
	private class LineStyleHiddenPanel extends JPanel implements UpdateablePanel, SetLabels, ActionListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JRadioButton[] buttons;

		public LineStyleHiddenPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18,18));		


			buttons = new JRadioButton[3];

			buttons[EuclidianView.LINE_TYPE_HIDDEN_NONE] 
			        = new JRadioButton(app.getMenu("Hidden.Invisible"));			
			buttons[EuclidianView.LINE_TYPE_HIDDEN_NONE]
			        .setActionCommand("none");

			buttons[EuclidianView.LINE_TYPE_HIDDEN_DASHED] 
			        = new JRadioButton(app.getMenu("Hidden.Dashed"));			
			buttons[EuclidianView.LINE_TYPE_HIDDEN_DASHED]
			        .setActionCommand("dashed");

			buttons[EuclidianView.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN] 
			        = new JRadioButton(app.getMenu("Hidden.Unchanged"));	
			buttons[EuclidianView.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN]
			        .setActionCommand("asNotHidden");

			ButtonGroup buttonGroup = new ButtonGroup();
			for(int i=0; i<3; i++){
				buttons[i].addActionListener(this);
				add(buttons[i]);
				buttonGroup.add(buttons[i]);
			}



		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getMenu("HiddenLineStyle") ));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;

			//	set value to first line's style 
			GeoElement geo0 = (GeoElement) geos[0];

			// update radio buttons 
			buttons[geo0.getLineTypeHidden()].setSelected(true);

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.isPath())
						|| !(geo.isGeoElement3D())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {


			int type = EuclidianView.LINE_TYPE_HIDDEN_NONE;

			if (e.getActionCommand()=="dashed"){	
				type = EuclidianView.LINE_TYPE_HIDDEN_DASHED;
			}else if (e.getActionCommand()=="asNotHidden"){
				type = EuclidianView.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN;
			}


			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setLineTypeHidden(type);
				geo.updateRepaint();
			}
		}
	}
	
	
	
	/**
	 * panel to select the fading for endings of a surface
	 * @author mathieu
	 */
	private class FadingPanel extends JPanel implements ChangeListener, SetLabels, UpdateablePanel {

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

			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);			
			
			add(slider);			
		}
		
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Fading")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoPlaneND geo0 = (GeoPlaneND) geos[0];
			slider.setValue((int) (100*geo0.getFading()));

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement)geos[i];
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
					plane.setFading((float) size/100);
					((GeoElement) plane).updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select the level of detail of surfaces
	 * @author mathieu
	 */
	private class LodPanel extends JPanel implements ChangeListener, SetLabels, UpdateablePanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public LodPanel() {		
			super(new FlowLayout(FlowLayout.LEFT));
				
		
			slider = new JSlider(0, 4);
			slider.setMajorTickSpacing(1);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);			
			
			add(slider);			
		}
		
		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("LevelOfDetail")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			LevelOfDetail geo0 = (LevelOfDetail) geos[0];
			slider.setValue(geo0.getLevelOfDetail());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof LevelOfDetail)) {
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
				int lod = slider.getValue();
				LevelOfDetail geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (LevelOfDetail) geos[i];
					geo.setLevelOfDetail(lod);
					((GeoElement) geo).updateRepaint();
				}
			}
		}
	}

	
	
	/**
	 * Panel for segment decoration
	 * @author Loic
	 */
	private class DecoSegmentPanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		private static final long serialVersionUID = 1L;
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private Object[] geos;
		
		DecoSegmentPanel(){
			super(new FlowLayout(FlowLayout.LEFT));
			// deco combobox 		
			DecorationListRenderer renderer = new DecorationListRenderer();
			renderer.setPreferredSize(new Dimension(130, app.getGUIFontSize() + 6));
			decoCombo = new JComboBox(GeoSegment.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);

			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}
		
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
		}
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			decoCombo.removeActionListener(this);

			//	set slider value to first geo's thickness 
			GeoSegment geo0 = (GeoSegment) geos[0];
			decoCombo.setSelectedIndex(geo0.decorationType);

			decoCombo.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoSegment)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}
		
		public void actionPerformed(ActionEvent e){
			Object source = e.getSource();
			if (source == decoCombo) {
				GeoSegment geo;
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoSegment) geos[i];
// Michael Borcherds 2007-11-20 BEGIN
//					geo.decorationType = type;
					geo.setDecorationType(type);
// Michael Borcherds 2007-11-20 END
					geo.updateRepaint();
				}
			}
		}
	}
	
	private class DecoAnglePanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel{
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private Object[] geos;
		
		DecoAnglePanel(){
			super(new FlowLayout(FlowLayout.LEFT));
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
		
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
		}
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			decoCombo.removeActionListener(this);

			//	set slider value to first geo's decoration 
			GeoAngle geo0 = (GeoAngle) geos[0];
			decoCombo.setSelectedIndex(geo0.decorationType);
			decoCombo.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoAngle)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}
		
		public void actionPerformed(ActionEvent e){
			Object source = e.getSource();
			if (source == decoCombo) {
				GeoAngle geo;
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoAngle) geos[i];
					geo.setDecorationType(type);
					// addded by Loic BEGIN
					// check if decoration could be drawn
					if (geo.getArcSize()<20&&(geo.decorationType==GeoElement.DECORATION_ANGLE_THREE_ARCS
							|| geo.decorationType==GeoElement.DECORATION_ANGLE_TWO_ARCS)){
						geo.setArcSize(20);
						setSliderMinValue();
						}
					//END
					geo.updateRepaint();
				}
			}
		}
	}
	
	// added 3/11/06
	private class RightAnglePanel extends JPanel implements ActionListener, SetLabels, UpdateablePanel {
		private JCheckBox emphasizeRightAngle;
		private Object[] geos;
		RightAnglePanel(){
			super(new FlowLayout(FlowLayout.LEFT));
			emphasizeRightAngle=new JCheckBox();
			emphasizeRightAngle.addActionListener(this);
			add(emphasizeRightAngle);
		}
		
		public void setLabels() {
			emphasizeRightAngle.setText(app.getPlain("EmphasizeRightAngle"));
		}
		
		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			emphasizeRightAngle.removeActionListener(this);

			//	set JcheckBox value to first geo's decoration 
			GeoAngle geo0 = (GeoAngle) geos[0];
			emphasizeRightAngle.setSelected(geo0.isEmphasizeRightAngle());
			emphasizeRightAngle.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoAngle)) {
					geosOK = false;
					break;
				}
				/*
				// If it isn't a right angle
				else if (!Kernel.isEqual(((GeoAngle)geos[i]).getValue(), Kernel.PI_HALF)){
					geosOK=false;
					break;
				}*/
			}
			return geosOK;
		}
		
		public void actionPerformed(ActionEvent e){
			Object source = e.getSource();
			if (source == emphasizeRightAngle) {
				GeoAngle geo;
				boolean b=emphasizeRightAngle.isSelected();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoAngle) geos[i];
					geo.setEmphasizeRightAngle(b);
					geo.updateRepaint();
				}
			}
		}
		
	}

	/**
	 * allows using a single = in condition to show object and dynamic color
	 * @param strCond Condition to be processed
	 * @return processed condition
	 */
	public static String replaceEqualsSigns(String strCond) {
		// needed to make next replace easier
		strCond = strCond.replaceAll(">=", ExpressionNode.strGREATER_EQUAL);
		strCond = strCond.replaceAll("<=", ExpressionNode.strLESS_EQUAL);
		strCond = strCond.replaceAll("==", ExpressionNode.strEQUAL_BOOLEAN);
		strCond = strCond.replaceAll("!=", ExpressionNode.strNOT_EQUAL);
		
		// allow A=B as well as A==B
		// also stops A=B doing an assignment of B to A :)
		return strCond.replaceAll("=", ExpressionNode.strEQUAL_BOOLEAN);

	}

} // PropertiesPanel
	
	
/**
 * panel for numeric slider
 * @author Markus Hohenwarter
 */
class SliderPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] geos; // currently selected geos
	private AngleTextField tfMin, tfMax;
	private JTextField tfWidth;
	private JTextField [] tfields;
	private JLabel [] tLabels;
	private JCheckBox cbSliderFixed, cbRandom;
	private JComboBox coSliderHorizontal;
	
	private Application app;
	private AnimationStepPanel stepPanel;
	private TextfieldSizePanel textFieldSizePanel;
	private AnimationSpeedPanel speedPanel;
	private Kernel kernel;
	private PropertiesPanel propPanel;
	private JPanel intervalPanel, sliderPanel, animationPanel;
	private boolean useTabbedPane, includeRandom;
	private boolean actionPerforming;

	public SliderPanel(Application app, PropertiesPanel propPanel, boolean useTabbedPane, boolean includeRandom) {
		this.app = app;
		kernel = app.getKernel();
		this.propPanel = propPanel;
		this.useTabbedPane = useTabbedPane;
		this.includeRandom = includeRandom;
					
		intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,5));	
		sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5, 5));		
		animationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5, 5));
		
		cbSliderFixed = new JCheckBox("",true);
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
		tfWidth = new MyTextField(app,4);
		tfields = new MyTextField[3];
		tLabels = new JLabel[3];
		tfields[0] = tfMin;
		tfields[1] = tfMax;
		tfields[2] = tfWidth;
		int numPairs = tLabels.length;

		//	add textfields
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
		    
		    if (i < 2)
		    	intervalPanel.add(p);
		    else 
		    	sliderPanel.add(p);
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
		}
		else { // no tabs 
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
			intervalPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Interval")));					
			sliderPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Slider")));		
			animationPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Animation")));
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
		
		String [] comboStr = {app.getPlain("horizontal"), app.getPlain("vertical")};
		
		int selectedIndex = coSliderHorizontal.getSelectedIndex();
		coSliderHorizontal.removeActionListener(this);
		coSliderHorizontal.removeAllItems();
		
		for(int i = 0; i < comboStr.length; ++i) {
			coSliderHorizontal.addItem(comboStr[i]);
		}
		
		coSliderHorizontal.setSelectedIndex(selectedIndex);
		coSliderHorizontal.addActionListener(this);

		String[] labels = { app.getPlain("min")+":",
							app.getPlain("max")+":", app.getPlain("Width")+":"};
		
		for(int i = 0; i < tLabels.length; ++i) {
			tLabels[i].setText(labels[i]);
		}
	}

	public JPanel update(Object[] geos) {
		stepPanel.update(geos);
		speedPanel.update(geos);
		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;
		
		for (int i=0; i<tfields.length; i++) 
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

			// we don't check isIntervalMinActive, because we want to display the interval even if it's empty
			if (num0.getIntervalMinObject() == null || temp.getIntervalMinObject() == null || !Kernel.isEqual(num0.getIntervalMin(), temp.getIntervalMin()))
				equalMin = false;
			if (num0.getIntervalMaxObject() == null || temp.getIntervalMaxObject() == null || !Kernel.isEqual(num0.getIntervalMax(), temp.getIntervalMax()))
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

		
        kernel.setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		if (equalMin){
			GeoElement min0 = num0.getIntervalMinObject();
			if (onlyAngles && (min0 == null ||(!min0.isLabelSet() && min0.isIndependent()))){				
				tfMin.setText(kernel.formatAngle(num0.getIntervalMin()).toString());			
			}else
				tfMin.setText(min0.getLabel());
		} else {
			tfMin.setText("");
		}
		
		if (equalMax){
			GeoElement max0 = num0.getIntervalMaxObject();
			if (onlyAngles &&  (max0 == null ||(!max0.isLabelSet() && max0.isIndependent()) ))
				tfMax.setText(kernel.formatAngle(num0.getIntervalMax()).toString());
			else
				tfMax.setText(max0.getLabel());
		} else {
			tfMax.setText("");
		}
		
		if (equalWidth){
			tfWidth.setText(kernel.format(num0.getSliderWidth()));
		} else {
			tfMax.setText("");
		}
		
		//kernel.setMaximumFractionDigits(oldDigits);
		kernel.restorePrintAccuracy();

		if (equalSliderFixed)
			cbSliderFixed.setSelected(num0.isSliderFixed());
		
		if (random)
			cbRandom.setSelected(num0.isRandom());
		
		cbRandom.setVisible(includeRandom);
		
		if (equalSliderHorizontal) {
			// TODO why doesn't this work when you create a slider
			coSliderHorizontal.setSelectedIndex(num0.isSliderHorizontal() ? 0 : 1);
		}
			

		for (int i=0; i<tfields.length; i++) 
			tfields[i].addActionListener(this);
		coSliderHorizontal.addActionListener(this);
		cbSliderFixed.addActionListener(this);
		cbRandom.addActionListener(this);
	
		return this;
	}

	private boolean checkGeos(Object[] geos) {				
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
		NumberValue value = new MyDouble(kernel,Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,false);					
		}			
		
		if (source == tfMin || source == tfMax) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				boolean dependsOnListener = false;
				GeoElement geoValue = value.toGeoElement();
				if(num.getMinMaxListeners()!=null)
					for(GeoNumeric listener : num.getMinMaxListeners()){
						if(geoValue.isChildOrEqual(listener)) 
							dependsOnListener = true;
					}
				if(dependsOnListener || geoValue.isChildOrEqual(num)){
					app.showErrorDialog(app.getError("CircularDefinition"));
				}
				else{ 
					if(source == tfMin)
						num.setIntervalMin(value);
					else
						num.setIntervalMax(value);
				}
				num.updateRepaint();
			
			}
		}
		else if (source == tfWidth) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				num.setSliderWidth(value.getDouble());
				num.updateRepaint();
			}
		} 
		
		if (propPanel != null)		
			propPanel.updateSelection(geos);
		else
			update(geos);
		actionPerforming = false;
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		if (!actionPerforming)
			doTextFieldActionPerformed((JTextField) e.getSource());
	}
}	

/**
 * panel for animation step
 * @author Markus Hohenwarter
 */
class AnimationStepPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JLabel label;	
	private AngleTextField tfAnimStep;
	private boolean partOfSliderPanel = false;
	
	private Kernel kernel;

	public AnimationStepPanel(Application app) {
		kernel = app.getKernel();
		
		// text field for animation step
		label = new JLabel();
		tfAnimStep = new AngleTextField(6, app);
		label.setLabelFor(tfAnimStep);
		tfAnimStep.addActionListener(this);
		tfAnimStep.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfAnimStep);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);
				
		setLabels();
	}
	
	public void setLabels() {
		label.setText(kernel.getApplication().getPlain("AnimationStep") + ": ");
	}	
	
	public void setPartOfSliderPanel() {
		partOfSliderPanel = true;
	}

	public JPanel update(Object[] geos) {		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfAnimStep.removeActionListener(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (geo0.getAnimationStep() != temp.getAnimationStep())
				equalStep = false;
			if (!(temp.isGeoAngle()))
				onlyAngles = false;
		}

		// set trace visible checkbox
		//int oldDigits = kernel.getMaximumFractionDigits();
		//kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		kernel.setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);

        if (equalStep){
        	GeoElement stepGeo = geo0.getAnimationStepObject();
			if (onlyAngles && (stepGeo == null ||(!stepGeo.isLabelSet() && stepGeo.isIndependent())))
				tfAnimStep.setText(
					kernel.formatAngle(geo0.getAnimationStep()).toString());
			else
				tfAnimStep.setText(stepGeo.getLabel());
        }
		else
			tfAnimStep.setText("");
        
		//kernel.setMaximumFractionDigits(oldDigits);
        kernel.restorePrintAccuracy();

		tfAnimStep.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() 
					|| geo.isGeoText() 
					|| geo.isGeoImage()
					|| geo.isGeoList()
					|| geo.isGeoBoolean()
					|| geo.isGeoButton()
					|| !partOfSliderPanel && geo.isGeoNumeric() && geo.isIndependent() // slider						
			)  
			{				
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
		if (e.getSource() == tfAnimStep)
			doActionPerformed();
	}

	private void doActionPerformed() {
		NumberValue newVal =
			kernel.getAlgebraProcessor().evaluateToNumeric(
				tfAnimStep.getText(),true);
		if (newVal != null && !Double.isNaN(newVal.getDouble())) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationStep(newVal);
				geo.updateRepaint();
			}
		}
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}
}

/**
 * panel for textfield size
 * @author Michael
 */
class TextfieldSizePanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JLabel label;	
	private MyTextField tfTextfieldSize;
	
	private Kernel kernel;

	public TextfieldSizePanel(Application app) {
		kernel = app.getKernel();
		
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
		label.setText(kernel.getApplication().getPlain("TextfieldLength") + ": ");
	}	
	public JPanel update(Object[] geos) {		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfTextfieldSize.removeActionListener(this);

		// check if properties have same values
		GeoTextField temp, geo0 = (GeoTextField) geos[0];
		boolean equalSize = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoTextField) geos[i];
			// same object visible value
			if (geo0.getLength() != temp.getLength())
				equalSize = false;
		}


        if (equalSize){
			tfTextfieldSize.setText(geo0.getLength()+"");
        }
		else
			tfTextfieldSize.setText("");
        
		tfTextfieldSize.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!(geo instanceof GeoTextField)						
			)  
			{				
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
		if (e.getSource() == tfTextfieldSize)
			doActionPerformed();
	}

	private void doActionPerformed() {
		NumberValue newVal =
			kernel.getAlgebraProcessor().evaluateToNumeric(
				tfTextfieldSize.getText(),true);
		if (newVal != null && !Double.isNaN(newVal.getDouble())) {
			for (int i = 0; i < geos.length; i++) {
				GeoTextField geo = (GeoTextField) geos[i];
				geo.setLength((int)newVal.getDouble());
				geo.updateRepaint();
			}
		}
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}
}

/**
 * panel for animation speed
 * @author adapted from AnimationStepPanel
 */
class AnimationSpeedPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JTextField tfAnimSpeed;
	private boolean partOfSliderPanel = false;
	private JComboBox animationModeCB;	
	private JLabel modeLabel, speedLabel;
	private Application app;	
	private Kernel kernel;

	public AnimationSpeedPanel(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		
			// combo box for 
		animationModeCB = new JComboBox();
		modeLabel = new JLabel();
		
		// text field for animation step
		speedLabel = new JLabel();
		tfAnimSpeed = new JTextField(5);
		speedLabel.setLabelFor(tfAnimSpeed);
		tfAnimSpeed.addActionListener(this);
		tfAnimSpeed.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(speedLabel);
		animPanel.add(tfAnimSpeed);
		animPanel.add(modeLabel);
		animPanel.add(animationModeCB);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);
		
		setLabels();
	}
	
	public void setLabels() {
		modeLabel.setText(app.getPlain("Repeat") + ": ");
		speedLabel.setText(app.getPlain("AnimationSpeed") + ": ");
		
		int selectedIndex = animationModeCB.getSelectedIndex();
		animationModeCB.removeActionListener(this);
		
		animationModeCB.removeAllItems();
		animationModeCB.addItem("\u21d4 "+app.getPlain("Oscillating")); // index 0
		animationModeCB.addItem("\u21d2 "+app.getPlain("Increasing")); // index 1
		animationModeCB.addItem("\u21d0 "+app.getPlain("Decreasing")); // index 2
		animationModeCB.addItem("\u21d2 "+app.getPlain("IncreasingOnce")); // index 3
		
		animationModeCB.setSelectedIndex(selectedIndex);
		animationModeCB.addActionListener(this);
	}
	
	public void setPartOfSliderPanel() {
		partOfSliderPanel = true;
	}

	public JPanel update(Object[] geos) {		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfAnimSpeed.removeActionListener(this);
		animationModeCB.removeActionListener(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalSpeed = true;
		boolean equalAnimationType = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (geo0.getAnimationSpeedObject() != temp.getAnimationSpeedObject())
				equalSpeed = false;
			if (geo0.getAnimationType() != temp.getAnimationType())
				equalAnimationType = false;
		}

		if (equalAnimationType)
			animationModeCB.setSelectedIndex(geo0.getAnimationType());
		else
			animationModeCB.setSelectedItem(null);

		// set trace visible checkbox
		//int oldDigits = kernel.getMaximumFractionDigits();
		//kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
        kernel.setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
        
        if (equalSpeed) {
        	GeoElement speedObj = geo0.getAnimationSpeedObject();
        	GeoNumeric num = kernel.getDefaultNumber(geo0.isAngle());
			tfAnimSpeed.setText(speedObj == null ? num.getAnimationSpeedObject().getLabel() : speedObj.getLabel());
        } else
			tfAnimSpeed.setText("");
        
		//kernel.setMaximumFractionDigits(oldDigits);
        kernel.restorePrintAccuracy();

		tfAnimSpeed.addActionListener(this);
		animationModeCB.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() 
					|| geo.isGeoText() 
					|| geo.isGeoImage()
					|| (geo instanceof GeoTextField)
					|| (geo instanceof GeoButton)
					|| geo.isGeoList()
					|| geo.isGeoBoolean()
					|| (geo.isGeoPoint() && !geo.isPointOnPath())
					|| !partOfSliderPanel && geo.isGeoNumeric() && geo.isIndependent() // slider						
			)  
			{				
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
		if (e.getSource() == tfAnimSpeed)
			doActionPerformed();
		else if (e.getSource() == animationModeCB) 
			setType(animationModeCB.getSelectedIndex());
	}

	private void doActionPerformed() {
		NumberValue animSpeed = 
			kernel.getAlgebraProcessor().evaluateToNumeric(tfAnimSpeed.getText(), false);
		if (animSpeed != null) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationSpeedObject(animSpeed);
				geo.updateCascade();
			}
			kernel.udpateNeedToShowAnimationButton();
			kernel.notifyRepaint();
			
			
		}
		update(geos);
	}

	private void setType(int type) {
		
		if (geos == null) return;
		
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationType(type);
				geo.updateRepaint();
			}
		
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}
}


/**
 * panel for condition to show object
 * @author Markus Hohenwarter
 */
class ShowConditionPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JTextField tfCondition;
	
	private Kernel kernel;
	private PropertiesPanel propPanel;

	public ShowConditionPanel(Application app, PropertiesPanel propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		
		// non auto complete input panel
		InputPanel inputPanel = new InputPanel(null, app, 20, false);
		tfCondition = (AutoCompleteTextField) inputPanel.getTextComponent();				
		
		tfCondition.addActionListener(this);
		tfCondition.addFocusListener(this);

		// put it all together
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.CENTER);
		
		setLabels();
	}
	
	public void setLabels() {
		setBorder(
			BorderFactory.createTitledBorder(
				kernel.getApplication().getMenu("Condition.ShowObject")
			)
		);
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfCondition.removeActionListener(this);

		// take condition of first geo
		String strCond = "";
		GeoElement geo0 = (GeoElement) geos[0];	
		GeoBoolean cond = geo0.getShowObjectCondition();
		if (cond != null) {
			strCond = cond.getLabel();
		}	
		
		for (int i=0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];	
			cond = geo.getShowObjectCondition();
			if (cond != null) {
				String strCondGeo = cond.getLabel();
				if (!strCond.equals(strCondGeo))
					strCond = "";
			}	
		}		
		
		tfCondition.setText(strCond);
		tfCondition.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		for (int i=0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];	
			if (!geo.isEuclidianShowable())
				return false;
		}
		
		return true;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfCondition)
			doActionPerformed();
	}

	private void doActionPerformed() {
		processed = true;
		GeoBoolean cond;
		String strCond = tfCondition.getText();
		if (strCond == null || strCond.trim().length() == 0) {
			cond = null;
		} else {
			
			strCond = PropertiesPanel.replaceEqualsSigns(strCond);
			
			cond = kernel.getAlgebraProcessor().evaluateToBoolean(strCond);
		}
				
		if (cond != null || strCond.trim().length() == 0) {
			// set condition
			try {
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					geo.setShowObjectCondition(cond);				

					// make sure object shown when condition removed
					if (cond == null) geo.updateRepaint(); 
				}	
				
			} catch (CircularDefinitionException e) {
				tfCondition.setText("");
				kernel.getApplication().showError("CircularDefinition");
			}	
			
			if (cond != null)
				cond.updateRepaint();		
			
			// to update "showObject" as well
			propPanel.updateSelection(geos);
		} else {
			// put back faulty condition (for editing)
			tfCondition.setText(strCond);
		}
		
	}

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}
	
	boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
		doActionPerformed();
	}
}

/**
 * panel for condition to show object
 * @author Michael Borcherds 2008-04-01
 */
class ColorFunctionPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JTextField tfRed, tfGreen, tfBlue, tfAlpha;
	private JButton btRemove;
	private JLabel nameLabelR, nameLabelG, nameLabelB, nameLabelA;
	
	
	private JComboBox cbColorSpace;
	private int colorSpace = GeoElement.COLORSPACE_RGB;
	// flag to prevent unneeded relabeling of the colorSpace comboBox
	private boolean allowSetComboBoxLabels = true;
	
	private String defaultR = "0", defaultG = "0", defaultB = "0", defaultA = "1";
	
	private Kernel kernel;
	private PropertiesPanel propPanel;

	public ColorFunctionPanel(Application app, PropertiesPanel propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		
		// non auto complete input panel
		InputPanel inputPanelR = new InputPanel(null, app, 1, 20, true);
		InputPanel inputPanelG = new InputPanel(null, app, 1, 20, true);
		InputPanel inputPanelB = new InputPanel(null, app, 1, 20, true);
		InputPanel inputPanelA = new InputPanel(null, app, 1, 20, true);
		tfRed = (AutoCompleteTextField) inputPanelR.getTextComponent();				
		tfGreen = (AutoCompleteTextField) inputPanelG.getTextComponent();				
		tfBlue = (AutoCompleteTextField) inputPanelB.getTextComponent();				
		tfAlpha = (AutoCompleteTextField) inputPanelA.getTextComponent();				
		
		tfRed.addActionListener(this);
		tfRed.addFocusListener(this);
		tfGreen.addActionListener(this);
		tfGreen.addFocusListener(this);
		tfBlue.addActionListener(this);
		tfBlue.addFocusListener(this);
		tfAlpha.addActionListener(this);
		tfAlpha.addFocusListener(this);
		
		nameLabelR = new JLabel("", JLabel.TRAILING);	
		nameLabelR.setLabelFor(inputPanelR);
		nameLabelG = new JLabel("", JLabel.TRAILING);	
		nameLabelG.setLabelFor(inputPanelG);
		nameLabelB = new JLabel("", JLabel.TRAILING);	
		nameLabelB.setLabelFor(inputPanelB);
		nameLabelA = new JLabel("", JLabel.TRAILING);	
		nameLabelA.setLabelFor(inputPanelA);
		
		btRemove = new JButton("\u2718");
		btRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];	
					geo.removeColorFunction();
					geo.setObjColor(geo.getObjectColor());
					geo.updateRepaint();
				}
				tfRed.setText("");
				tfGreen.setText("");
				tfBlue.setText("");
				tfAlpha.setText("");
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
		
		SpringUtilities.makeCompactGrid(colorsPanel,
                4, 2, 		//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		add(colorsPanel, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel(new SpringLayout());
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(cbColorSpace);
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(btRemove);
		buttonsPanel.add(leftPanel);
		buttonsPanel.add(rightPanel);
		
		SpringUtilities.makeCompactGrid(buttonsPanel,
                1, 2, 		//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		add(buttonsPanel, BorderLayout.SOUTH);
		
		setLabels();
	}
	
	public void setLabels() {
		Application app = kernel.getApplication();
		
		setBorder(
			BorderFactory.createTitledBorder(
				app.getMenu("DynamicColors")
			)
		);

		if(allowSetComboBoxLabels){  
			cbColorSpace.removeActionListener(this);
			cbColorSpace.removeAllItems();
			cbColorSpace.addItem(app.getMenu("RGB"));
			cbColorSpace.addItem(app.getMenu("HSV"));
			cbColorSpace.addItem(app.getMenu("HSL"));
			cbColorSpace.addActionListener(this);
		}
		allowSetComboBoxLabels = true;

		switch(colorSpace){
		case GeoElement.COLORSPACE_RGB:
			nameLabelR.setText(app.getMenu("Red") + ":");
			nameLabelG.setText(app.getMenu("Green") + ":");
			nameLabelB.setText(app.getMenu("Blue") + ":");
			break;
		case GeoElement.COLORSPACE_HSB:
			nameLabelR.setText(app.getMenu("Hue") + ":");
			nameLabelG.setText(app.getMenu("Saturation") + ":");
			nameLabelB.setText(app.getMenu("Value") + ":");
			break;
		case GeoElement.COLORSPACE_HSL:
			nameLabelR.setText(app.getMenu("Hue") + ":");
			nameLabelG.setText(app.getMenu("Saturation") + ":");
			nameLabelB.setText(app.getMenu("Lightness") + ":");
			break;
		}
		
		nameLabelA.setText(app.getMenu("Opacity") + ":");
		
		btRemove.setToolTipText(app.getPlainTooltip("Remove"));
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;
		
		boolean someFillable = false;
		for (int i = 0 ; i < geos.length ; i++) {
			if (((GeoElement)geos[i]).isFillable()) {
				someFillable = true;
				continue;
			}
		}
		
		tfAlpha.setVisible(someFillable);
		nameLabelA.setVisible(someFillable);
		
		GeoElement geo = (GeoElement)geos[0];
		Color col = geo.getObjectColor();
		defaultR = "" + col.getRed() / 255.0;
		defaultG = "" + col.getGreen() / 255.0;
		defaultB = "" + col.getBlue() / 255.0;
		defaultA = "" + geo.getFillColor().getAlpha() / 255.0;

		tfRed.removeActionListener(this);
		tfGreen.removeActionListener(this);
		tfBlue.removeActionListener(this);
		tfAlpha.removeActionListener(this);
		btRemove.removeActionListener(this);
		cbColorSpace.removeActionListener(this);

		// take condition of first geo
		String strRed = "";
		String strGreen = "";
		String strBlue = "";
		String strAlpha = "";
		GeoElement geo0 = (GeoElement) geos[0];	
		GeoList colorList = geo0.getColorFunction();
		if (colorList != null) {
			strRed = colorList.get(0).getLabel();
			strGreen = colorList.get(1).getLabel();
			strBlue = colorList.get(2).getLabel();
			if (colorList.size() == 4)
				strAlpha = colorList.get(3).getLabel();
		}
		colorSpace = geo0.getColorSpace();
		cbColorSpace.setSelectedIndex(colorSpace);
		
		for (int i=0; i < geos.length; i++) {
			geo = (GeoElement) geos[i];	
			GeoList colorListTemp = geo.getColorFunction();
			if (colorListTemp != null) {
				String strRedTemp = colorListTemp.get(0).getLabel();
				String strGreenTemp = colorListTemp.get(1).getLabel();
				String strBlueTemp = colorListTemp.get(2).getLabel();
				String strAlphaTemp = "";
				if (colorListTemp.size() == 4)
					strAlphaTemp = colorListTemp.get(3).getLabel();
				if (!strRed.equals(strRedTemp)) strRed = "";
				if (!strGreen.equals(strGreenTemp)) strGreen = "";
				if (!strBlue.equals(strBlueTemp)) strBlue = "";
				if (!strAlpha.equals(strAlphaTemp)) strAlpha = "";
			}	
		}		

		tfRed.setText(strRed);
		tfRed.addActionListener(this);
		tfGreen.setText(strGreen);
		tfGreen.addActionListener(this);
		tfBlue.setText(strBlue);
		tfBlue.addActionListener(this);
		tfAlpha.setText(strAlpha);
		tfAlpha.addActionListener(this);
		cbColorSpace.addActionListener(this);
		return this;
	}

	// return true: want to be able to color all spreadsheet objects
	private boolean checkGeos(Object[] geos) {
		return true;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfRed || e.getSource() == tfGreen || e.getSource() == tfBlue|| e.getSource() == tfAlpha)
			doActionPerformed();
		if ( e.getSource() == cbColorSpace){
			colorSpace = cbColorSpace.getSelectedIndex();
			allowSetComboBoxLabels = false;
			setLabels();
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;
		
		GeoList list = null;
		GeoList listAlpha = null;
		String strRed = tfRed.getText();
		String strGreen = tfGreen.getText();
		String strBlue = tfBlue.getText();
		String strAlpha = tfAlpha.getText();
		
		strRed = PropertiesPanel.replaceEqualsSigns(strRed);
		strGreen = PropertiesPanel.replaceEqualsSigns(strGreen);
		strBlue = PropertiesPanel.replaceEqualsSigns(strBlue);
		strAlpha = PropertiesPanel.replaceEqualsSigns(strAlpha);
		
		if ((strRed == null || strRed.trim().length() == 0) &&
				(strGreen == null || strGreen.trim().length() == 0) &&
				(strAlpha == null || strAlpha.trim().length() == 0) &&
			(strBlue == null || strBlue.trim().length() == 0)) {
			//num = null;
			list=null;
			listAlpha=null;
		} else {
			if (strRed == null || strRed.trim().length() == 0) strRed = defaultR;
			if (strGreen == null || strGreen.trim().length() == 0) strGreen = defaultG;
			if (strBlue == null || strBlue.trim().length() == 0) strBlue = defaultB;
			if (strAlpha == null || strAlpha.trim().length() == 0) strAlpha = defaultA;
	
			list = kernel.getAlgebraProcessor().evaluateToList("{"+strRed + ","+strGreen+","+strBlue+"}");
			
			if (!"1".equals(strAlpha))
				listAlpha = kernel.getAlgebraProcessor().evaluateToList("{"+strRed + ","+strGreen+","+strBlue+","+strAlpha+"}");
		
		}
		
				
		// set condition
		//try {
		if (list != null) {							//
		if (((list.get(0) instanceof NumberValue)) && 	// bugfix, enter "x" for a color 
				((list.get(1) instanceof NumberValue)) &&	//
				((list.get(2) instanceof NumberValue)) &&	//
			((list.size() == 3  || list.get(3) instanceof NumberValue)) )		//
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo.isFillable() && listAlpha != null){
					geo.setColorFunction(listAlpha);
					list=listAlpha; //to have correct update
				}else
					geo.setColorFunction(list);	
				geo.setColorSpace(colorSpace);
			}	
			
	
		list.updateRepaint();
		
		// to update "showObject" as well
		propPanel.updateSelection(geos);
		} else {
			// put back faulty text (for editing)
			tfRed.setText(strRed);
			tfGreen.setText(strGreen);
			tfBlue.setText(strBlue);
			tfAlpha.setText(strAlpha);
		}

	}

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}
	
	private boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
			doActionPerformed();
	}
}



/**
 * panel to set graphics view location
 * @author G.Sturr
 */
class GraphicsViewLocationPanel
	extends JPanel
	implements ActionListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	
	private JCheckBox cbGraphicsView, cbGraphicsView2; 
	
	private Kernel kernel;
	private Application app;
	private EuclidianView ev;
	private PropertiesPanel propPanel;

	public GraphicsViewLocationPanel(Application app, PropertiesPanel propPanel) {
		this.app = app;
		kernel = app.getKernel();
		this.propPanel = propPanel;

		ev = app.getEuclidianView();
				
		cbGraphicsView = new JCheckBox();
		cbGraphicsView2 = new JCheckBox();		
		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(cbGraphicsView);
		add(cbGraphicsView2);
		
		setLabels();
	}
	
	public void setLabels() {
		setBorder(
			BorderFactory.createTitledBorder(
				kernel.getApplication().getMenu("Location")
			)
		);
		cbGraphicsView.setText(app.getPlain("DrawingPad"));
		cbGraphicsView2.setText(app.getPlain("DrawingPad2"));
		
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		cbGraphicsView.removeActionListener(this);
		cbGraphicsView2.removeActionListener(this);

		boolean isInEV = false;
		boolean isInEV2 = false;

		for (int i=0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];	
			if (geo.isVisibleInView(Application.VIEW_EUCLIDIAN)) 
				isInEV = true;
			if (geo.isVisibleInView(Application.VIEW_EUCLIDIAN2)) 
				isInEV2 = true;
		}		

		cbGraphicsView.setSelected(isInEV);		
		cbGraphicsView2.setSelected(isInEV2);		
		
		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);

		return this;
	}

	private boolean checkGeos(Object[] geos) {
		
		if (!app.hasEuclidianView2()) return false;
		
		/*
		  for (int i=0; i < geos.length; i++) {
		 
			
			GeoElement geo = (GeoElement) geos[i];	
			if (!geo.isEuclidianShowable())
				return false;
				
		}*/
		
		return true;
	}

	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == cbGraphicsView){
			for (int i=0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if(cbGraphicsView.isSelected()){
					//geo.addView(ev);
					//ev.add(geo);
					app.addToEuclidianView(geo);
				}else{
					//geo.removeView(ev);
					//ev.remove(geo);
					app.removeFromEuclidianView(geo);
				}
			}
		}
		
		if (e.getSource() == cbGraphicsView2){
			for (int i=0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				
				EuclidianView ev2 = (EuclidianView)app.getEuclidianView2();
				
				if(cbGraphicsView2.isSelected()){
					geo.addView(Application.VIEW_EUCLIDIAN2);
					ev2.add(geo);
				}else{
					geo.removeView(Application.VIEW_EUCLIDIAN2);
					ev2.remove(geo);
				}
			}
		}
				
	}

	
}









/**
 * panel for name of object
 * @author Markus Hohenwarter
 */
class NamePanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
		
	private AutoCompleteTextField tfName, tfDefinition, tfCaption;
	private JLabel nameLabel, defLabel, captionLabel;
	private InputPanel inputPanelName, inputPanelDef, inputPanelCap;
	private RenameInputHandler nameInputHandler;
	private RedefineInputHandler defInputHandler;
	private GeoElement currentGeo;	
	private Application app;

	public NamePanel(Application app) {	
		this.app = app;
		// NAME PANEL
		nameInputHandler = new RenameInputHandler(app, null, false);
		
		// non auto complete input panel
		inputPanelName = new InputPanel(null, app, 1, 10, true);
		tfName = (AutoCompleteTextField) inputPanelName.getTextComponent();				
		tfName.setAutoComplete(false);		
		tfName.addActionListener(this);
		tfName.addFocusListener(this);	
		
		// DEFINITON PANEL		
// Michael Borcherds 2007-12-31 BEGIN added third argument
		defInputHandler = new RedefineInputHandler(app, null, null);
//		 Michael Borcherds 2007-12-31 END
	
		// definition field: non auto complete input panel
		inputPanelDef = new InputPanel(null, app, 1, 20, true);
		tfDefinition = (AutoCompleteTextField) inputPanelDef.getTextComponent();
		tfDefinition.setAutoComplete(false);		
		tfDefinition.addActionListener(this);
		tfDefinition.addFocusListener(this);

		// caption field: non auto complete input panel
		inputPanelCap = new InputPanel(null, app, 1, 20, true);
		tfCaption = (AutoCompleteTextField) inputPanelCap.getTextComponent();
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
		nameLabel.setText(app.getPlain("Name") + ":");
		defLabel.setText(app.getPlain("Definition") + ":");
		captionLabel.setText(app.getMenu("Button.Caption") + ":");
	}	
	
	private void updateGUI(boolean showDefinition, boolean showCaption) {
		int rows = 1;
		removeAll();
		
		add(nameLabel);		
		add(inputPanelName);
		
		if (showDefinition) {	
			rows++;
			add(defLabel);
			add(inputPanelDef);
		}
		
		if (showCaption) {
			rows++;
			add(captionLabel);
			add(inputPanelCap);
		}
		
		 //Lay out the panel
		setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this,
                                        rows, 2, 	// rows, cols
                                        5, 5,   //initX, initY
                                        5, 5);  //xPad, yPad	
	}

	public JPanel update(Object[] geos) {		
		if (!checkGeos(geos))
			return null;

		// NAME
		tfName.removeActionListener(this);

		// take name of first geo		
		GeoElement geo0 = (GeoElement) geos[0];	
		tfName.setText(geo0.getLabel());
	
		currentGeo = geo0;
		nameInputHandler.setGeoElement(geo0);
		
		tfName.addActionListener(this);		
		
		// DEFINITION
		//boolean showDefinition = !(currentGeo.isGeoText() || currentGeo.isGeoImage());
		boolean showDefinition = currentGeo.isGeoText() ? ((GeoText)currentGeo).isTextCommand() :
			! (((currentGeo.isGeoImage()|| currentGeo.isGeoButton()) && currentGeo.isIndependent()));
		if (showDefinition) {			
			tfDefinition.removeActionListener(this);
			defInputHandler.setGeoElement(currentGeo);
			tfDefinition.setText(getDefText(currentGeo));
			tfDefinition.addActionListener(this);
			
			if (currentGeo.isIndependent()) {
				defLabel.setText(app.getPlain("Value")+ ":");
			} else {
				defLabel.setText(app.getPlain("Definition")+ ":");
			}
		}
//		defLabel.setVisible(showDefinition);
//		inputPanelDef.setVisible(showDefinition);
		
		// CAPTION
		boolean showCaption = !currentGeo.isTextValue(); // borcherds was currentGeo.isGeoBoolean();
		if (showCaption) {			
			tfCaption.removeActionListener(this);
			tfCaption.setText(getCaptionText(currentGeo));
			tfCaption.addActionListener(this);			
		} 
//		captionLabel.setVisible(showCaption);
//		inputPanelCap.setVisible(showCaption);
		
		updateGUI(showDefinition, showCaption);
		
		return this;
	}

	private boolean checkGeos(Object[] geos) {				
		return geos.length == 1;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {		
			doActionPerformed(e.getSource());
	}

	private synchronized void doActionPerformed(Object source) {
		actionPerforming = true;
		
		if (source == tfName) {
			// rename
			String strName = tfName.getText();				
			nameInputHandler.processInput(strName);
			
			// reset label if not successful
			strName = currentGeo.getLabel();
			if (!strName.equals(tfName.getText())) {
				tfName.setText(strName);
				tfName.requestFocus();
			}
		} 
		else if (source == tfDefinition) {		
			String strDefinition = tfDefinition.getText();	
			if (!strDefinition.equals(getDefText(currentGeo))) {		
				defInputHandler.processInput(strDefinition);
	
				// reset definition string if not successful
				strDefinition = getDefText(currentGeo);
				if (!strDefinition.equals(tfDefinition.getText())) {
					tfDefinition.setText(strDefinition);
					tfDefinition.requestFocus();
				}
			}
		}		
		else if (source == tfCaption) {		
			String strCaption = tfCaption.getText();	
			currentGeo.setCaption(strCaption);			
			
			strCaption = getCaptionText(currentGeo);
			if (!strCaption.equals(tfCaption.getText().trim())) {
				tfCaption.setText(strCaption);	
				tfCaption.requestFocus();
			}
		}	
		currentGeo.updateRepaint();
		
		actionPerforming = false;
	}
	private boolean actionPerforming = false;

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {	
		if (!actionPerforming) 
			doActionPerformed(e.getSource());
	}
	
	private String getDefText(GeoElement geo) {
		/*
		return geo.isIndependent() ?
				geo.toOutputValueString() :
				geo.getCommandDescription();	*/
		return geo.getRedefineString(false, true);
	}
	
	private String getCaptionText(GeoElement geo) {
		String strCap = currentGeo.getRawCaption();
		if (strCap.equals(currentGeo.getLabel()))
			return "";
		else
			return strCap;
	}
	
	
}

interface UpdateablePanel {
	public JPanel update(Object[] geos);
	public void setVisible(boolean flag);
}