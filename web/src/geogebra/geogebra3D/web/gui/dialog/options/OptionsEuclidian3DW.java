package geogebra.geogebra3D.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import geogebra.geogebra3D.web.gui.images.StyleBar3DResources;
import geogebra.html5.event.FocusListenerW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Properties for 3D view (web)
 * @author mathieu
 *
 */
public class OptionsEuclidian3DW extends OptionsEuclidianW {
	
	private AxisTab zAxisTab;
	
	private ProjectionTab projectionTab;
	
	/**
	 * basic tab for 3D
	 * @author mathieu
	 *
	 */
	protected class BasicTab3D extends BasicTab {
		
		
		private CheckBox cbUseClipping, cbShowClipping;
		private FlowPanel clippingOptionsPanel, boxSizePanel;
		private Label clippingOptionsTitle, boxSizeTitle;
		private RadioButton radioClippingSmall, radioClippingMedium, radioClippingLarge;
		
		/**
		 * constructor
		 */
		public BasicTab3D() {
			super();
			
			addClippingOptionsPanel();
			
		}
		
		
        private void addClippingOptionsPanel() {
        	
        	// clipping options panel
        	clippingOptionsTitle = new Label();
        	clippingOptionsTitle.setStyleName("panelTitle");
        	clippingOptionsPanel = new FlowPanel();
    		cbUseClipping = new CheckBox();
    		cbUseClipping.setStyleName("checkBoxPanel");
    		clippingOptionsPanel.add(cbUseClipping);
    		//clippingOptionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    		cbShowClipping = new CheckBox();
    		cbShowClipping.setStyleName("checkBoxPanel");
    		clippingOptionsPanel.add(cbShowClipping);
    		
    		add(clippingOptionsTitle);
			indent(clippingOptionsPanel);

			cbUseClipping.addClickHandler(new ClickHandler(){

				@Override
                public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setUseClippingCube(cbUseClipping.getValue());
				}});
			
			cbShowClipping.addClickHandler(new ClickHandler(){

				@Override
                public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setShowClippingCube(cbShowClipping.getValue());
				}});


			// clipping box size
			boxSizeTitle = new Label();
			boxSizeTitle.setStyleName("panelTitle");
        	boxSizePanel = new FlowPanel();
        	radioClippingSmall = new RadioButton("radioClipping");
			radioClippingMedium = new RadioButton("radioClipping");
			radioClippingLarge = new RadioButton("radioClipping");
			boxSizePanel.add(radioClippingSmall);
			boxSizePanel.add(radioClippingMedium);
			boxSizePanel.add(radioClippingLarge);
			
			add(boxSizeTitle);
			indent(boxSizePanel);

			radioClippingSmall.addClickHandler(new ClickHandler(){

				@Override
                public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_SMALL);
				}});

			radioClippingMedium.addClickHandler(new ClickHandler(){

				@Override
                public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_MEDIUM);
				}});

			radioClippingLarge.addClickHandler(new ClickHandler(){

				@Override
                public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_LARGE);
				}});

		}
        
        /**
         * update clipping properties (use and size)
         */
        public void updateClippingProperties(){
        	cbUseClipping.setValue(((EuclidianView3D) view).useClippingCube());
        	cbShowClipping.setValue(((EuclidianView3D) view).showClippingCube());
        	
    		int flag = ((EuclidianView3D) view).getClippingReduction();
    		radioClippingSmall
    				.setValue(flag == GeoClippingCube3D.REDUCTION_SMALL);
    		radioClippingMedium
    				.setValue(flag == GeoClippingCube3D.REDUCTION_MEDIUM);
    		radioClippingLarge
    				.setValue(flag == GeoClippingCube3D.REDUCTION_LARGE);
        	
        }
        
		@Override
        public void setLabels() {
			super.setLabels();
			clippingOptionsTitle.setText(app.getPlain("Clipping"));
			cbUseClipping.setText(app.getPlain("UseClipping"));
			cbShowClipping.setText(app.getPlain("ShowClipping"));
			
			boxSizeTitle.setText(app.getPlain("BoxSize"));
			radioClippingSmall.setText(app.getPlain("BoxSize.small"));
			radioClippingMedium.setText(app.getPlain("BoxSize.medium"));
			radioClippingLarge.setText(app.getPlain("BoxSize.large"));
		}
		
		
	}
	
	
	private class ProjectionTab extends EuclidianTab {
		
		private ProjectionButtons projectionButtons;
		
		private FlowPanel orthoPanel, perspPanel, obliquePanel, glassesPanel;
		private Label orthoTitle, perspTitle, obliqueTitle, glassesTitle;

		private AutoCompleteTextFieldW tfPersp, tfGlassesEyeSep, tfObliqueAngle, tfObliqueFactor;
		private Label tfPerspLabel, tfGlassesLabel, tfObliqueAngleLabel, tfObliqueFactorLabel;
		
		private class ProjectionButtons {

			private ToggleButton[] buttons;

			private int buttonSelected;

			
			private class ClickHandleProjectionButton implements ClickHandler {
				
				private int index; 
				
				/**
				 * constructor
				 * @param index button index
				 */
				public ClickHandleProjectionButton(int index){
					this.index = index;
				}

				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setProjection(index);	                
                }
				
			}

			private ProjectionButtons() {


				buttons = new ToggleButton[4];

				buttons[EuclidianView3D.PROJECTION_ORTHOGRAPHIC] = new ToggleButton(new Image(StyleBar3DResources.INSTANCE.viewOrthographic()));
				buttons[EuclidianView3D.PROJECTION_PERSPECTIVE] = new ToggleButton(new Image(StyleBar3DResources.INSTANCE.viewPerspective()));
				buttons[EuclidianView3D.PROJECTION_GLASSES] = new ToggleButton(new Image(StyleBar3DResources.INSTANCE.viewGlasses()));
				buttons[EuclidianView3D.PROJECTION_OBLIQUE] = new ToggleButton(new Image(StyleBar3DResources.INSTANCE.viewOblique()));


				for (int i = 0; i < 4; i++){
					buttons[i].addClickHandler(new ClickHandleProjectionButton(i));
				}

				buttonSelected = ((EuclidianView3D) view).getProjection();
				buttons[buttonSelected].setValue(true);
			}

			public ToggleButton getButton(int i) {
				return buttons[i];
			}

			public void setSelected(int i) {
				buttons[buttonSelected].setValue(false);
				buttonSelected = i;
				buttons[buttonSelected].setValue(true);

			}

		}
		
		public ProjectionTab() {
			setStyleName("propertiesTab");
			
			projectionButtons = new ProjectionButtons();
			
			orthoTitle = new Label("");
			orthoTitle.setStyleName("panelTitle");
			orthoPanel = new FlowPanel();
			orthoPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC));
			add(orthoTitle);
			indent(orthoPanel);
			
			perspTitle = new Label("");
			perspTitle.setStyleName("panelTitle");
			perspPanel = new FlowPanel();		
			tfPerspLabel = new Label("");
			tfPersp = getTextField();
			tfPersp.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processPerspText();
					}
				}});

			tfPersp.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					processPerspText();
				}	
			});
			FlowPanel tfPerspPanel = new FlowPanel();
			tfPerspPanel.setStyleName("panelRowCell");
			tfPerspPanel.add(tfPerspLabel);
			tfPerspPanel.add(tfPersp);
			perspPanel.add(LayoutUtil.panelRow(
					projectionButtons.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE), 
					tfPerspPanel));
			add(perspTitle);
			indent(perspPanel);
			
			glassesTitle = new Label("");
			glassesTitle.setStyleName("panelTitle");
			glassesPanel = new FlowPanel();
			glassesPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_GLASSES));
			add(glassesTitle);
			indent(glassesPanel);

			obliqueTitle = new Label("");
			obliqueTitle.setStyleName("panelTitle");
			obliquePanel = new FlowPanel();
			obliquePanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_OBLIQUE));
			add(obliqueTitle);
			indent(obliquePanel);
			

		}
		
		protected void processPerspText(){
			try {
				double val = Double.parseDouble(tfPersp.getText());
				if (!Double.isNaN(val)) {
					double min = 1;
					if (val < min) {
						val = min;
						tfPersp.setText("" + val);
					}

					((EuclidianView3D) view)
							.setProjectionPerspectiveEyeDistance(val);
				}
			} catch (NumberFormatException e) {
				tfPersp.setText(""
						+ ((EuclidianView3D) view)
								.getProjectionPerspectiveEyeDistance());
			}
		}
		
		protected void indent(FlowPanel panel) {
			FlowPanel indent = new FlowPanel();
			indent.setStyleName("panelIndent");
			indent.add(panel);
			add(indent);
	
        }
		
		

		public void setLabels() {
			orthoTitle.setText(app.getPlain("Orthographic"));
			
			perspTitle.setText(app.getPlain("Perspective"));
			tfPerspLabel.setText(app.getPlain(app.getPlain("EyeDistance") + ":"));
			
			glassesTitle.setText(app.getPlain("Glasses"));
			
			obliqueTitle.setText(app.getPlain("Oblique"));
	        
        }
		
		/**
		 * update text values
		 */
		public void updateGUI(){
			tfPersp.setText("" + ((EuclidianView3D) view).getProjectionPerspectiveEyeDistance());
		}
	}

	/**
	 * constructor
	 * @param app application
	 * @param view 3D view
	 */
	public OptionsEuclidian3DW(AppW app, EuclidianViewInterfaceCommon view) {
	    super(app, view);

    }
	
	
	@Override
    protected BasicTab newBasicTab(){
		return new BasicTab3D();
	}

	@Override
    public void updateGUI() {
		((BasicTab3D) basicTab).updateClippingProperties();
		projectionTab.updateGUI();
		super.updateGUI();
	}
	
	@Override
    public void setLabels() {
		
		TabBar tabBar = tabPanel.getTabBar();
		
	    super.setLabels(tabBar, 4);
	    
	    tabBar.setTabText(3, app.getPlain("zAxis"));
	    zAxisTab.setLabels();
	    
	    tabBar.setTabText(5, app.getPlain("Projection"));
	    projectionTab.setLabels();
	    
    }
	
	@Override
    protected void addAxesTabs(){
		super.addAxesTabs();
		addZAxisTab();
	}
	
	private void addZAxisTab() {
		zAxisTab = new AxisTab(EuclidianOptionsModel.Z_AXIS);
		tabPanel.add(zAxisTab, "z");
	}
	
	@Override
    protected void addTabs(){
		super.addTabs();
		addProjectionTab();
	}
	
	private void addProjectionTab() {
		projectionTab = new ProjectionTab();
		tabPanel.add(projectionTab, "projection");
	}
	
	
	
}
