package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.geogebra3D.web.gui.images.StyleBar3DResources;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.euclidian.EuclidianStyleBarW;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.resources.client.ImageResource;

/**
 * style bar for 3D view
 * @author mathieu
 *
 */
public class EuclidianStyleBar3DW extends EuclidianStyleBarW {
	
private PopupMenuButton btnRotateView, btnClipping;
	
	private MyToggleButton2 btnShowPlane, btnViewDefault, btnViewXY, btnViewXZ, btnViewYZ;
	

	private PopupMenuButton btnViewProjection;

	/**
	 * constructor
	 * @param ev euclidian view
	 */
	public EuclidianStyleBar3DW(EuclidianView ev) {
	    super(ev);
    }
	
	
	@Override
    public EuclidianView3D getView(){
		return (EuclidianView3D) ev;
	}
	
	
	@Override
	protected void createDefaultMap(){
		
		super.createDefaultMap();
		
		EuclidianStyleBarStatic3D.addToDefaultMap(defaultGeoMap);
		
	}
	
	
	
	
	
	
	
	private void setIcon(PopupMenuButton button, ImageResource icon){
		AppResourcesConverter.setIcon(icon, button); 
	}
	
	@Override
	protected void createButtons() {

		super.createButtons();
		
		// ========================================
		// show grid button
		btnShowPlane = new MyToggleButtonForEV(StyleBar3DResources.INSTANCE.plane(),
				iconHeight);
		btnShowPlane.addValueChangeHandler(this);

		
		//========================================
		// rotate view button
		btnRotateView = new PopupMenuButtonForView3D(){
			@Override
		    protected void fireActionPerformed() {
				getView().setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
		    }
			
			@Override
            protected void onClickAction(){
				if (getView().isRotAnimatedContinue()){
					getView().stopRotAnimation();
					//btnRotateView.setSelected(false);
				}else{
					getView().setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
					//btnRotateView.setSelected(true);
				}
			}
		};
		setIcon(btnRotateView, StyleBar3DResources.INSTANCE.rotateView());
		btnRotateView.getMySlider().setMinimum(-10);
		btnRotateView.getMySlider().setMaximum(10);
		btnRotateView.getMySlider().setMajorTickSpacing(10);
		btnRotateView.getMySlider().setMinorTickSpacing(1);
		btnRotateView.getMySlider().setPaintTicks(true);
		//btnRotateView.getMySlider().setPaintTrack(false);
		//btnRotateView.getMySlider().setSnapToTicks(true);
		btnRotateView.setSliderValue(5);
		btnRotateView.addPopupHandler(this);
		btnRotateView.addActionListener(this);
		
		
		//========================================
		// clipping button
		btnClipping = new PopupMenuButtonForView3D(){
			@Override
		    protected void fireActionPerformed() {
				getView().setClippingReduction(btnClipping.getSliderValue());
		    }
			
			@Override
            protected void onClickAction(){			
				getView().toggleShowAndUseClippingCube();
			}
		};	
		setIcon(btnClipping, StyleBar3DResources.INSTANCE.clipping());
		btnClipping.getMySlider().setMinimum(GeoClippingCube3D.REDUCTION_MIN);
		btnClipping.getMySlider().setMaximum(GeoClippingCube3D.REDUCTION_MAX);
		btnClipping.getMySlider().setMajorTickSpacing(1);
		btnClipping.getMySlider().setMinorTickSpacing(1);
		btnClipping.getMySlider().setPaintTicks(true);
		//btnClipping.getMySlider().setPaintTrack(true);
		//btnClipping.getMySlider().setSnapToTicks(true);
		btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addPopupHandler(this);

		
		
		//========================================
		/* rotate x text field
		textRotateX = new JTextField(3);
		textRotateX.addActionListener(this);
		*/
		
		//========================================
		// view perspective button	
		btnViewDefault = new MyToggleButtonForEV(StyleBar3DResources.INSTANCE.standardViewRotate(), iconHeight);
		
		btnViewDefault.addValueChangeHandler(this);
		
		
		//========================================
		// view xy button	
		btnViewXY = new MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewXY(), iconHeight);
		
		btnViewXY.addValueChangeHandler(this);
		
		//========================================
		// view xz button	
		btnViewXZ = new MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewXZ(), iconHeight);
		
		btnViewXZ.addValueChangeHandler(this);		
		
		//========================================
		// view yz button	
		btnViewYZ = new MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewYZ(), iconHeight);
		
		btnViewYZ.addValueChangeHandler(this);	
		
		//========================================
		// projection view button
		ImageOrText[] projectionIcons = new ImageOrText[4];
		for (int i = 0 ; i < 4; i++){
			projectionIcons[i] = new ImageOrText();
		}
		projectionIcons[0].url = StyleBar3DResources.INSTANCE.viewOrthographic().getSafeUri().asString();
		projectionIcons[1].url = StyleBar3DResources.INSTANCE.viewPerspective().getSafeUri().asString();
		projectionIcons[2].url = StyleBar3DResources.INSTANCE.viewGlasses().getSafeUri().asString();
		projectionIcons[3].url = StyleBar3DResources.INSTANCE.viewOblique().getSafeUri().asString();
		btnViewProjection = new ProjectionPopup((AppW) app, projectionIcons);
		btnViewProjection.addPopupHandler(this);
		
	}




	private class ProjectionPopup extends PopupMenuButton {
		private static final long serialVersionUID = 1L;
		public ProjectionPopup(AppW app, ImageOrText[] projectionIcons){
			super(app, projectionIcons, 1, projectionIcons.length, new GDimensionW(20, iconHeight),
					geogebra.common.gui.util.SelectionTable.MODE_ICON, true, false);
			setIcon(projectionIcons[getView().getProjection()]);
		}
		

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		}
		
		/*
		@Override
		public Point getToolTipLocation(MouseEvent e) {
			return new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y);
		}
		*/

	}
	
	private class PopupMenuButtonForView3D extends PopupMenuButton /*implements ClickHandler*/{

		public PopupMenuButtonForView3D(){
			super((AppW)EuclidianStyleBar3DW.this.app, null, -1, -1, new GDimensionW(20, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_ICON,  false,  true);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
		}
		
		/*
		public void onClick(ClickEvent event) {
			App.debug("onClick : "+this);
	    }
	    */
		
		/*
		@Override
		public Point getToolTipLocation(MouseEvent e) {
			return new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y);
		}
		*/
		

	}

	
	

	@Override
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		
		App.debug("source = "+source);
		
		if (source.equals(btnShowPlane)) {
			getView().togglePlane();
		//}else if (source.equals(btnRotateView)) { // done in button creation
			
		//}else if (source.equals(btnClipping)) { // done in button creation
			
		}else if (source.equals(btnViewDefault)) {
			getView().setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,EuclidianView3D.ANGLE_ROT_XOY,false);
		}else if (source.equals(btnViewXY)) {
			getView().setRotAnimation(-90,90,true);
		}else if (source.equals(btnViewXZ)) {
			getView().setRotAnimation(-90,0,true);
		}else if (source.equals(btnViewYZ)) {
			getView().setRotAnimation(0,0,true);
		}else if (source.equals(btnViewProjection)) {
			int si = btnViewProjection.getSelectedIndex();
			App.debug("si = "+si);
			switch(si){
			case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
				getView().setProjectionOrthographic();
				break;
			case EuclidianView3D.PROJECTION_PERSPECTIVE:
				getView().setProjectionPerspective();
				break;
			case EuclidianView3D.PROJECTION_GLASSES:
				getView().setProjectionGlasses();
				break;
			case EuclidianView3D.PROJECTION_OBLIQUE:
				getView().setProjectionOblique();
				break;
			}
		}else
			super.processSource(source, targetGeos);
	}
	
	
	
	
	

	@Override
	protected void addBtnShowPlane(){
		add(btnShowPlane);
	}

	@Override
	protected void addBtnPointCapture(){}
	

	@Override
	protected void addBtnRotateView(){
		
		addSeparator();
		
		add(btnRotateView);
		//add(textRotateX);
		add(btnViewDefault);
		add(btnViewXY);
		add(btnViewXZ);
		add(btnViewYZ);
		
		addSeparator();
		add(btnClipping);
		add(btnViewProjection);
	}

	@Override
	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D() ;
	}
	
	
	@Override
    protected void setToolTips() {

		super.setToolTips();
		
	    Localization loc = app.getLocalization();
	    btnShowPlane.setToolTipText(loc.getPlainTooltip("stylebar.xOyPlane"));
		btnRotateView.setToolTipText(loc.getPlainTooltip("stylebar.RotateView"));
		btnViewDefault.setToolTipText(loc.getPlainTooltip("stylebar.ViewDefaultRotate"));
		btnViewXY.setToolTipText(loc.getPlainTooltip("stylebar.ViewXY"));
		btnViewXZ.setToolTipText(loc.getPlainTooltip("stylebar.ViewXZ"));
		btnViewYZ.setToolTipText(loc.getPlainTooltip("stylebar.ViewYZ"));
		btnClipping.setToolTipText(loc.getPlainTooltip("stylebar.Clipping"));
		btnViewProjection.setToolTipText(loc.getPlainTooltip("stylebar.ViewProjection"));
		btnViewProjection.setToolTipArray(
				new String[] {
						loc.getPlainTooltip("stylebar.ParallelProjection"),
						loc.getPlainTooltip("stylebar.PerspectiveProjection"),
						loc.getPlainTooltip("stylebar.GlassesProjection"),
						loc.getPlainTooltip("stylebar.ObliqueProjection")
				});
	    
	    
	}
	
	
	@Override
	protected void updateGUI(){
		
		super.updateGUI();
		
		btnShowPlane.removeValueChangeHandler();
		btnShowPlane.setSelected(getView().getShowPlane());
		btnShowPlane.addValueChangeHandler(this);

		
		
		btnViewDefault.removeValueChangeHandler();
		btnViewDefault.setSelected(false);
		btnViewDefault.addValueChangeHandler(this);


		btnViewXY.removeValueChangeHandler();
		btnViewXY.setSelected(false);
		btnViewXY.addValueChangeHandler(this);

		btnViewXZ.removeValueChangeHandler();
		btnViewXZ.setSelected(false);
		btnViewXZ.addValueChangeHandler(this);

		btnViewYZ.removeValueChangeHandler();
		btnViewYZ.setSelected(false);
		btnViewYZ.addValueChangeHandler(this);
		
		btnClipping.setSliderValue(getView().getClippingReduction());
		
	}
	
	
	

	@Override
	protected PopupMenuButton[] newPopupBtnList(){
		PopupMenuButton[] superList = super.newPopupBtnList();
		PopupMenuButton[] ret = new PopupMenuButton[superList.length+3];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		
		int index = superList.length;
		ret[index]=btnRotateView;index++;
		ret[index]=btnClipping;index++;
		ret[index]=btnViewProjection;
		return ret;
	}
	
	@Override
	protected MyToggleButton2[] newToggleBtnList(){
		MyToggleButton2[] superList = super.newToggleBtnList();
		MyToggleButton2[] ret = new MyToggleButton2[superList.length+4];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		
		int index = superList.length;
		ret[index]=btnViewDefault;index++;
		ret[index]=btnViewXY;index++;
		ret[index]=btnViewXZ;index++;
		ret[index]=btnViewYZ;
		return ret;
	}
	
	
}
