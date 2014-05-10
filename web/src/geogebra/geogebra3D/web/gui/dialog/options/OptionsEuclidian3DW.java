package geogebra.geogebra3D.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Properties for 3D view (web)
 * @author mathieu
 *
 */
public class OptionsEuclidian3DW extends OptionsEuclidianW {
	
	/**
	 * basic tab for 3D
	 * @author mathieu
	 *
	 */
	protected class BasicTab3D extends BasicTab {
		
		
		private CheckBox cbUseClipping, cbShowClipping;
		private FlowPanel clippingOptionsPanel;
		private Label clippingOptionsTitle;
		
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


		}
        
        public void updateClippingProperties(){
        	cbUseClipping.setValue(((EuclidianView3D) view).useClippingCube());
        	cbShowClipping.setValue(((EuclidianView3D) view).showClippingCube());
        }
        
		@Override
        public void setLabels() {
			super.setLabels();
			clippingOptionsTitle.setText(app.getPlain("Clipping"));
			cbUseClipping.setText(app.getPlain("UseClipping"));
			cbShowClipping.setText(app.getPlain("ShowClipping"));
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
	
}
