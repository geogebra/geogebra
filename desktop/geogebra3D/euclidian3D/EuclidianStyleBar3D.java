package geogebra3D.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.EuclidianStyleBarD;
import geogebra.gui.util.MyToggleButton;
import geogebra.gui.util.PopupMenuButton;
import geogebra.main.AppD;
import geogebra3D.App3D;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;



/**
 * StyleBar for 3D euclidian view
 * 
 * @author matthieu
 *
 */
public class EuclidianStyleBar3D extends EuclidianStyleBarD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	
	private PopupMenuButton btnRotateView, btnClipping;
	
	//private JTextField textRotateX;
	
	private MyToggleButton btnShowPlane, btnViewDefault, btnViewXY, btnViewXZ, btnViewYZ;
	

	private PopupMenuButton btnViewProjection;


	/**
	 * Common constructor.
	 * @param ev
	 */
	public EuclidianStyleBar3D(EuclidianView3D ev) {
		super(ev);
	}
	
	
	@Override
	protected void createDefaultMap(){
		
		super.createDefaultMap();
		
		// planes
		defaultGeoMap.put(EuclidianConstants.MODE_PLANE_THREE_POINTS,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_PARALLEL_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		
		// spheres
		defaultGeoMap.put(EuclidianConstants.MODE_SPHERE_POINT_RADIUS,
				ConstructionDefaults3D.DEFAULT_QUADRIC);
		defaultGeoMap.put(EuclidianConstants.MODE_SPHERE_TWO_POINTS,
				ConstructionDefaults3D.DEFAULT_QUADRIC);
		
		// cylinders, cones
		defaultGeoMap.put(EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS,
				ConstructionDefaults3D.DEFAULT_QUADRIC_LIMITED);
		defaultGeoMap.put(EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS,
				ConstructionDefaults3D.DEFAULT_QUADRIC_LIMITED);
		defaultGeoMap.put(EuclidianConstants.MODE_EXTRUSION,
				ConstructionDefaults3D.DEFAULT_QUADRIC_LIMITED);
		defaultGeoMap.put(EuclidianConstants.MODE_CONIFY,
				ConstructionDefaults3D.DEFAULT_QUADRIC_LIMITED);


		
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
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		
		if (source.equals(btnShowPlane)) {
			((App3D) app).togglePlane();
		}else if (source.equals(btnRotateView)) {
			if (btnRotateView.getMySlider().isShowing()){//if slider is showing, start rotation
				getView().setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
			}else{//if button has been clicked, toggle rotation
				if (getView().isRotAnimatedContinue()){
					getView().stopRotAnimation();
					btnRotateView.setSelected(false);
				}else{
					getView().setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
					btnRotateView.setSelected(true);
				}
			}
		}else if (source.equals(btnClipping)) {
			if (btnClipping.getMySlider().isShowing()){
				getView().setClippingReduction(btnClipping.getSliderValue());
			}else{
				getView().toggleShowAndUseClippingCube();
			}
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

	

	private class PopupMenuButtonForView3D extends PopupMenuButton{

		public PopupMenuButtonForView3D(){
			super(app, null, -1, -1, new Dimension(18, 18), geogebra.common.gui.util.SelectionTable.MODE_ICON,  false,  true);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
		}
		
		@Override
		public Point getToolTipLocation(MouseEvent e) {
			return new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y);
		}
		

	}
	
	@Override
	protected void createButtons() {

		super.createButtons();
		
		// ========================================
		// show grid button
		btnShowPlane = new MyToggleButtonVisibleIfNoGeo(app.getImageIcon("plane.gif"),
				iconHeight);
		btnShowPlane.addActionListener(this);

		
		//========================================
		// rotate view button
		btnRotateView = new PopupMenuButtonForView3D();	
		btnRotateView.setIcon(app.getImageIcon("stylebar_rotateview.gif"));
		btnRotateView.getMySlider().setMinimum(-10);
		btnRotateView.getMySlider().setMaximum(10);
		btnRotateView.getMySlider().setMajorTickSpacing(10);
		btnRotateView.getMySlider().setMinorTickSpacing(1);
		btnRotateView.getMySlider().setPaintTicks(true);
		//btnRotateView.getMySlider().setPaintLabels(true);
		btnRotateView.getMySlider().setPaintTrack(false);
		btnRotateView.getMySlider().setSnapToTicks(true);
		btnRotateView.setSliderValue(5);
		btnRotateView.addActionListener(this);
		
		
		//========================================
		// clipping button
		btnClipping = new PopupMenuButtonForView3D();	
		btnClipping.setIcon(app.getImageIcon("stylebar_clipping.gif"));
		btnClipping.getMySlider().setMinimum(GeoClippingCube3D.REDUCTION_MIN);
		btnClipping.getMySlider().setMaximum(GeoClippingCube3D.REDUCTION_MAX);
		btnClipping.getMySlider().setMajorTickSpacing(1);
		btnClipping.getMySlider().setMinorTickSpacing(1);
		btnClipping.getMySlider().setPaintTicks(true);
		//btnRotateView.getMySlider().setPaintLabels(true);
		btnClipping.getMySlider().setPaintTrack(true);
		btnClipping.getMySlider().setSnapToTicks(true);
		btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addActionListener(this);

		
		
		//========================================
		/* rotate x text field
		textRotateX = new JTextField(3);
		textRotateX.addActionListener(this);
		*/
		
		//========================================
		// view perspective button	
		btnViewDefault = new MyToggleButtonVisibleIfNoGeo(app.getImageIcon("standard_view_rotate.gif"), iconHeight);
		
		btnViewDefault.addActionListener(this);
		
		
		//========================================
		// view xy button	
		btnViewXY = new MyToggleButtonVisibleIfNoGeo(app.getImageIcon("view_xy.gif"), iconHeight);
		
		btnViewXY.addActionListener(this);
		
		//========================================
		// view xz button	
		btnViewXZ = new MyToggleButtonVisibleIfNoGeo(app.getImageIcon("view_xz.gif"), iconHeight);
		
		btnViewXZ.addActionListener(this);		
		
		//========================================
		// view yz button	
		btnViewYZ = new MyToggleButtonVisibleIfNoGeo(app.getImageIcon("view_yz.gif"), iconHeight);
		
		btnViewYZ.addActionListener(this);	
		
		//========================================
		// projection view button
		ImageIcon[] projectionIcons = new ImageIcon[4];
		projectionIcons[0]=app.getImageIcon("stylebar_vieworthographic.gif");
		projectionIcons[1]=app.getImageIcon("stylebar_viewperspective.gif");
		projectionIcons[2]=app.getImageIcon("stylebar_viewglasses.gif");		
		projectionIcons[3]=app.getImageIcon("stylebar_viewoblique.gif");
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
		btnViewProjection.addActionListener(this);
		
	}




	private class ProjectionPopup extends PopupMenuButton {//implements ActionListener{
		private static final long serialVersionUID = 1L;
		public ProjectionPopup(AppD app, ImageIcon[] projectionIcons){
			super(app, projectionIcons, 1, projectionIcons.length, new Dimension(16, 16), geogebra.common.gui.util.SelectionTable.MODE_ICON, true, false);
			setIcon(projectionIcons[getView().getProjection()]);
		}
		

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		}
		
		@Override
		public Point getToolTipLocation(MouseEvent e) {
			return new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y);
		}

	}
	
	@Override
	public void setLabels(){
		super.setLabels();
		btnShowPlane.setToolTipText(loc.getPlainTooltip("stylebar.xOyPlane"));
		btnRotateView.setToolTipText(loc.getPlainTooltip("stylebar.RotateView"));
		btnViewDefault.setToolTipText(loc.getPlainTooltip("stylebar.ViewDefault"));
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
		
		//btnViewProjection.setSelectedIndex(getView().getProjection());
	}
	
	@Override
	protected void updateGUI(){
		
		if (isIniting)
			return;

		super.updateGUI();
		
		btnShowPlane.removeActionListener(this);
		btnShowPlane.setSelected(getView().getShowPlane());
		btnShowPlane.addActionListener(this);

		
		btnRotateView.removeActionListener(this);
		btnRotateView.setSelected(false);
		btnRotateView.addActionListener(this);
		
		btnViewDefault.removeActionListener(this);
		btnViewDefault.setSelected(false);
		btnViewDefault.addActionListener(this);


		btnViewXY.removeActionListener(this);
		btnViewXY.setSelected(false);
		btnViewXY.addActionListener(this);

		btnViewXZ.removeActionListener(this);
		btnViewXZ.setSelected(false);
		btnViewXZ.addActionListener(this);

		btnViewYZ.removeActionListener(this);
		btnViewYZ.setSelected(false);
		btnViewYZ.addActionListener(this);
		
		btnClipping.removeActionListener(this);
		btnClipping.setSelected(getView().showClippingCube());
		btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addActionListener(this);

		btnViewProjection.removeActionListener(this);
		btnViewProjection.setSelectedIndex(getView().getProjection());
		btnViewProjection.addActionListener(this);
		
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
	protected MyToggleButton[] newToggleBtnList(){
		MyToggleButton[] superList = super.newToggleBtnList();
		MyToggleButton[] ret = new MyToggleButton[superList.length+4];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		
		int index = superList.length;
		ret[index]=btnViewDefault;index++;
		ret[index]=btnViewXY;index++;
		ret[index]=btnViewXZ;index++;
		ret[index]=btnViewYZ;
		return ret;
	}
	
	public EuclidianView3D getView(){
		return (EuclidianView3D) ev;
	}

}
