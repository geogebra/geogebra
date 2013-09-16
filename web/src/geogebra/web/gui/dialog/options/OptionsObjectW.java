package geogebra.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.dialog.options.model.AuxObjectModel;
import geogebra.common.gui.dialog.options.model.AuxObjectModel.IAuxObjectListener;
import geogebra.common.gui.dialog.options.model.ColorObjectModel;
import geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import geogebra.common.gui.dialog.options.model.FixObjectModel;
import geogebra.common.gui.dialog.options.model.FixObjectModel.IFixObjectListener;
import geogebra.common.gui.dialog.options.model.OptionsModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import geogebra.common.gui.dialog.options.model.ShowLabelModel;
import geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import geogebra.common.gui.dialog.options.model.ShowObjectModel;
import geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import geogebra.common.gui.dialog.options.model.TraceModel;
import geogebra.common.gui.dialog.options.model.TraceModel.ITraceListener;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Traceable;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.openjdk.awt.geom.Dimension;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.util.SelectionTable;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OptionsObjectW extends
geogebra.common.gui.dialog.options.OptionsObject implements OptionPanelW {
	private FlowPanel wrappedPanel;
	private VerticalPanel basicTab;
	private FlowPanel colorTab;
	private FlowPanel styleTab;
	private TabPanel tabPanel;
	private VerticalPanel advancedTab;

	private ShowObjectPanel showObjectPanel;
	private TracePanel tracePanel;
	private LabelPanel labelPanel;
	private FixPanel fixPanel;
	private AuxPanel auxPanel;
	private ShowConditionPanel showConditionPanel;
	private ColorPanel colorPanel;
	
	class OptionPanel {
		private OptionsModel model;
		private Widget widget;

		public void update(Object[] geos) {
			getModel().setGeos(geos);
			if (!getModel().checkGeos())
				return;
			getModel().updateProperties();
		}

		public Widget getWidget() {
			return widget;
		}

		public void setWidget(Widget widget) {
			this.widget = widget;
		}

		public OptionsModel getModel() {
			return model;
		}

		public void setModel(OptionsModel model) {
			this.model = model;
		}

	}
	class ShowObjectPanel extends OptionPanel implements IShowObjectListener {
		private final CheckBox showObjectCB;
		private ShowObjectModel model;
		public ShowObjectPanel() {
			showObjectCB = new CheckBox(app.getPlain("ShowObject")); 
			setWidget(showObjectCB);

			model = new ShowObjectModel(this);
			setModel(model);
			showObjectCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyChanges(showObjectCB.getValue());
				};

			});

		}

		public void updateCheckbox(boolean equalObjectVal,  boolean showObjectCondition) {
			if (equalObjectVal) {
				GeoElement geo0 = (GeoElement)model.getGeos()[0];
				showObjectCB.setValue(geo0.isEuclidianVisible());
			}
			else {
				showObjectCB.setValue(false);
			}

			showObjectCB.setEnabled(!showObjectCondition);
		}
	}

	class TracePanel  extends OptionPanel implements ITraceListener {
		private final CheckBox showTraceCB;
		private TraceModel model;
		public TracePanel() {
			showTraceCB = new CheckBox(app.getPlain("ShowTrace")); 
			setWidget(showTraceCB);

			model = new TraceModel(this);
			setModel(model);

			showTraceCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyChanges(showTraceCB.getValue());
				}
			});

		}

		public void updateCheckbox(boolean isEqual) {
			Traceable geo0 = (Traceable)model.getGeoAt(0);
			if (isEqual) {
				showTraceCB.setValue(geo0.getTrace());
			}
			else {
				showTraceCB.setValue(false);
			}
		}

	}		

	class LabelPanel extends OptionPanel implements IShowLabelListener {
		private final CheckBox showLabelCB;
		private final FlowPanel mainWidget;
		private final ListBox labelMode;
		private ShowLabelModel model;
		public LabelPanel() {
			mainWidget = new FlowPanel();
			showLabelCB = new CheckBox(app.getPlain("ShowLabel") + ":"); 
			mainWidget.add(showLabelCB);
			setWidget(mainWidget);

			model = new ShowLabelModel(app, this);
			setModel(model);

			updateShowLabel();

			labelMode = new ListBox(false);

			labelMode.addItem(app.getPlain("Name")); // index 0
			labelMode.addItem(app.getPlain("NameAndValue")); // index 1
			labelMode.addItem(app.getPlain("Value")); // index 2
			labelMode.addItem(app.getPlain("Caption")); // index 3 Michael
			// Borcherd

			labelMode.setEnabled(showLabelCB.getValue());

			showLabelCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyShowChanges(showLabelCB.getValue());
				}
			});

			labelMode.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyModeChanges(labelMode.getSelectedIndex());
				}

			});
			mainWidget.add(labelMode);

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
				showLabelCB.setValue(geo0.isLabelVisible());
				labelMode.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setValue(false);
				labelMode.setEnabled(false);
			}

			// set label visible checkbox
			if (isEqualMode) {
				labelMode.setSelectedIndex(geo0.getLabelMode());
			}
			else {
				labelMode.setSelectedIndex(-1);
			}

			// locus in selection
			labelMode.setVisible(model.isNameValueShown());

		}
	}

	class FixPanel extends OptionPanel implements IFixObjectListener {
		private final CheckBox showFixCB;
		private FixObjectModel model;
		public FixPanel() {
			showFixCB = new CheckBox(app.getPlain("FixObject"));
			setWidget(showFixCB);

			model = new FixObjectModel(this);
			setModel(model);

			showFixCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyChanges(showFixCB.getValue());
				}
			});

		}

		public void updateCheckbox(boolean equalObjectVal) {
			// set object visible checkbox
			if (equalObjectVal) {
				showFixCB.setValue(model.getGeoAt(0).isFixed());
			}
			else {
				showFixCB.setValue(false);
			}

		}

	}		

	class AuxPanel extends OptionPanel implements IAuxObjectListener {
		private final CheckBox auxCB;
		private AuxObjectModel model;
		public AuxPanel() {
			auxCB = new CheckBox(app.getPlain("AuxiliaryObject"));
			setWidget(auxCB);

			model = new AuxObjectModel(this);
			setModel(model);

			auxCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyChanges(auxCB.getValue());
				}
			});

		}

		public void updateCheckbox(boolean equalObjectVal) {
			// set object visible checkbox
			if (equalObjectVal) {
				auxCB.setValue(model.getGeoAt(0).isAuxiliaryObject());
			}
			else {
				auxCB.setValue(false);
			}

		}

	}		

	class ShowConditionPanel extends OptionPanel implements	IShowConditionListener {

		private static final long serialVersionUID = 1L;

		private ShowConditionModel model;
		private AutoCompleteTextFieldW tfCondition;

		private Kernel kernel;
		private boolean processed;
		private final FlowPanel mainWidget;
		//private PropertiesPanelD propPanel;

		public ShowConditionPanel(AppW app/*, PropertiesPanelD propPanel*/) {
			kernel = app.getKernel();
			//this.propPanel = propPanel;
			model = new ShowConditionModel(app, this);
			setModel(model);

			// non auto complete input panel
			InputPanelW inputPanel = new InputPanelW(null, app, -1, false);
			tfCondition = inputPanel.getTextComponent();

			tfCondition.addKeyHandler(new KeyHandler(){

				public void keyReleased(KeyEvent e) {
					if (e.getKeyChar() == '\n') {
						doActionPerformed();	    
					}
				}

			});

			//		tfCondition.addFocusListener(new FocusListener(this) {
			//			
			//		});
			// put it all together
			mainWidget = new FlowPanel();
			mainWidget.add(inputPanel);
			setWidget(mainWidget);
		}



		private void doActionPerformed() {
			processed = true;
			model.applyChanges(tfCondition.getText());
		}

		//	public void focusGained(FocusEvent arg0) {
		//		processed = false;
		//	}
		//
		//	boolean processed = false;
		//
		//	public void focusLost(FocusEvent e) {
		//		if (!processed)
		//			doActionPerformed();
		//	}


		public void setText(String text) {
			tfCondition.setText(text);	
		}

		public void updateSelection(Object[] geos) {
			//propPanel.updateSelection(geos);
		}

	}

	class ColorPanel extends OptionPanel implements IColorObjectListener {
		private ColorObjectModel model;
		private VerticalPanel mainPanel;
		private ColorPopupMenuButton colorChooser;
		private GColor selectedColor;
		private SelectionTable colorTable;
		
		public ColorPanel() {
			model = new ColorObjectModel(app, this);
			setModel(model);
			
			final GDimensionW colorIconSize = new GDimensionW(20, 20);
			colorChooser = new ColorPopupMenuButton((AppW) app, colorIconSize,
					ColorPopupMenuButton.COLORSET_DEFAULT, true) {

				@Override
				public void update(Object[] geos) {

					updateColorTable();
					GeoElement geo0 = model.getGeoAt(0);
					int index = this.getColorIndex(geo0.getObjectColor());
					setSelectedIndex(index);
					setDefaultColor(geo0.getAlphaValue(), geo0.getObjectColor());
				};
				
				@Override
				public void handlePopupActionEvent(){
					super.handlePopupActionEvent();
					applyChanges();
				}
				
				@Override
				public void setSliderValue(int value) {
					super.setSliderValue(value);
					if (!model.hasGeos()) {
						return;
					}
					float alpha = value / 100.0f;
					GColor color = model.getGeoAt(0).getObjectColor();
					model.applyChanges(color, alpha, true);
				}
			};
			
			colorChooser.setKeepVisible(false);
			mainPanel = new VerticalPanel();
			FlowPanel colorPanel = new FlowPanel();
			Label label = new Label("Choose color:");
			colorPanel.add(label);
			colorPanel.add(colorChooser);
			mainPanel.add(colorPanel);
			setWidget(mainPanel);
			
		}
		
		
		public void applyChanges() {
			float alpha = colorChooser.getSliderValue() / 100.0f;
			GColor color = colorChooser.getSelectedColor();
			model.applyChanges(color, alpha, false);
		
		}
		
		public void updateChooser(boolean equalObjColor,
                boolean equalObjColorBackground, boolean allFillable,
                boolean hasBackground) {
	        // TODO Auto-generated method stub
			selectedColor = null;
			GColor selectedBGColor = null;
			float alpha = 1;
			GeoElement geo0 = model.getGeoAt(0);
			if (equalObjColorBackground) {
				selectedBGColor = geo0.getBackgroundColor();
			}

			if (isBackgroundColorSelected()) {
				selectedColor = selectedBGColor;
			}				
			else {
				// set selectedColor if all selected geos have the same color
				if (equalObjColor) {
					if (allFillable) {
						selectedColor = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						selectedColor = geo0.getObjectColor();
					}
				}
			}
			colorChooser.update(model.getGeos());
		}
		

		public void updatePreview(GColor col, float alpha) {
	        // TODO Auto-generated method stub
	        
        }

		public boolean isBackgroundColorSelected() {
	        // TODO Auto-generated method stub
	        return false;
        }


		public void updateNoBackground(GeoElement geo, GColor col, float alpha,
                boolean updateAlphaOnly, boolean allFillable) {
			if (!updateAlphaOnly){
				geo.setObjColor(col);
			}
			if (allFillable){
				geo.setAlphaValue(alpha);
			}

		}
		
	}
	
	public OptionsObjectW(AppW app) {
		this.app = app;

		kernel = app.getKernel();

		// build GUI
		initGUI();
	}

	private void initGUI() {
		wrappedPanel = new FlowPanel();
		wrappedPanel.setSize("300", "100%");
		tabPanel = new TabPanel();
		tabPanel.setSize("300", "100%");
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() 
        {			
			public void onSelection(SelectionEvent<Integer> event) {
				updateGUI();
	            
            }
        });;
		addBasicTab();
		addColorTab();
		addStyleTab();
		addAdvancedTab();
		tabPanel.selectTab(0);
		wrappedPanel.add(tabPanel);
		
		updateGUI();
	}



	private void addBasicTab() {
		basicTab = new VerticalPanel();
		basicTab.setStyleName("optionsPanel");

		VerticalPanel checkboxPanel = new VerticalPanel();
		checkboxPanel.setSize("100%", "100%");
		basicTab.add(checkboxPanel);

		showObjectPanel = new ShowObjectPanel();   
		checkboxPanel.add(showObjectPanel.getWidget());


		tracePanel = new TracePanel(); 
		checkboxPanel.add(tracePanel.getWidget());
		basicTab.add(checkboxPanel);

		labelPanel = new LabelPanel();
		checkboxPanel.add(labelPanel.getWidget());

		fixPanel = new FixPanel();
		checkboxPanel.add(fixPanel.getWidget());

		auxPanel = new AuxPanel();
		checkboxPanel.add(auxPanel.getWidget());

		basicTab.add(checkboxPanel);

		tabPanel.add(basicTab, "Basic");

	}

	private void addColorTab() {
		colorTab = new FlowPanel();
		colorPanel = new ColorPanel();
		colorTab.add(colorPanel.getWidget());
		tabPanel.add(colorTab, "Color");
	}

	private void addStyleTab() {
		styleTab = new FlowPanel();
		tabPanel.add(styleTab, "Style");
	}

	private void addAdvancedTab() {
		advancedTab = new VerticalPanel();

		advancedTab.setSize("100%", "100%");

		showConditionPanel = new ShowConditionPanel((AppW) app);
		advancedTab.add(showConditionPanel.getWidget());
		tabPanel.add(advancedTab, "Advanced");
	}




	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(250, 400);
	}

	public void setMinimumSize(Dimension preferredSize) {
	
	}

	public void updateGUI() {

		Object[] geos = app.getSelectionManager().getSelectedGeos().toArray();
		if (geos.length != 0) {
			showObjectPanel.update(geos);
			tracePanel.update(geos);
			labelPanel.update(geos);
			fixPanel.update(geos);
			auxPanel.update(geos);
			showConditionPanel.update(geos);
			colorPanel.update(geos);
		}


	}

	public Widget getWrappedPanel() {
		// TODO Auto-generated method stub
		return wrappedPanel;
	}
}
