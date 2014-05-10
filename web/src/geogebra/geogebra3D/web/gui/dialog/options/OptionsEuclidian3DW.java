package geogebra.geogebra3D.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Properties for 3D view (web)
 * @author mathieu
 *
 */
public class OptionsEuclidian3DW extends OptionsEuclidianW {
	
	private AxisTab zAxisTab;
	
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
		super.updateGUI();
	}
	
	@Override
    public void setLabels() {
	    super.setLabels(4);
	    tabPanel.getTabBar().setTabText(3, app.getPlain("zAxis"));
	    zAxisTab.setLabels();
	    
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
	
}
