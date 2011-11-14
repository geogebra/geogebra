package geogebra3D.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianStyleBar;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;



/**
 * StyleBar for 3D euclidian view
 * 
 * @author matthieu
 *
 */
public class EuclidianStyleBar3D extends EuclidianStyleBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private PopupMenuButton btnRotateView;
	
	//private JTextField textRotateX;
	
	private MyToggleButton btnViewDefault, btnViewXY, btnViewXZ, btnViewYZ;
	

	private PopupMenuButton btnViewProjection;


	/**
	 * Common constructor.
	 * @param ev
	 */
	public EuclidianStyleBar3D(EuclidianView3D ev) {
		super(ev);
	}
	
	
	protected void addBtnPointCapture(){}
	
	protected void addBtnRotateView(){
		
		//addSeparator();
		
		add(btnRotateView);
		//add(textRotateX);
		add(btnViewDefault);
		add(btnViewXY);
		add(btnViewXZ);
		add(btnViewYZ);
		
		//addSeparator();
		
		add(btnViewProjection);
	}

	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D() ;
	}
	
	
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		if (source.equals(btnRotateView)) {

			if (btnRotateView.getMySlider().isShowing()){//if slider is showing, start rotation
				((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
			}else{//if button has been clicked, toggle rotation
				if (((EuclidianView3D) ev).isRotAnimatedContinue()){
					((EuclidianView3D) ev).stopRotAnimation();
					btnRotateView.setSelected(false);
				}else{
					((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
					btnRotateView.setSelected(true);
				}
			}
			
			/*
		}else if (source.equals(textRotateX)) {
			EuclidianView3D ev3D = ((EuclidianView3D) ev);
			try{
				int angle = Integer.parseInt(textRotateX.getText());
				Application.debug(angle);
				ev3D.setRotAnimation(angle,ev3D.getZRot(),false);
			} catch (Exception e) {
				Application.debug("erreur: "+textRotateX.getText());
				textRotateX.setText(""+((int) ev3D.getXRot()));
			}
			*/
		}else if (source.equals(btnViewDefault)) {
			((EuclidianView3D) ev).setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,EuclidianView3D.ANGLE_ROT_XOY,false);
		}else if (source.equals(btnViewXY)) {
			((EuclidianView3D) ev).setRotAnimation(-90,90,true);
		}else if (source.equals(btnViewXZ)) {
			((EuclidianView3D) ev).setRotAnimation(-90,0,true);
		}else if (source.equals(btnViewYZ)) {
			((EuclidianView3D) ev).setRotAnimation(0,0,true);
		}else
			super.processSource(source, targetGeos);
	}

	protected void createButtons() {

		super.createButtons();
		
		//========================================
		// rotate view button
		btnRotateView = new PopupMenuButton(app, null, -1, -1, null, -1,  false,  true){
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
			}
		};		
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
		/* rotate x text field
		textRotateX = new JTextField(3);
		textRotateX.addActionListener(this);
		*/
		
		//========================================
		// view perspective button	
		btnViewDefault = new MyToggleButton(app.getImageIcon("view_default.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewDefault.addActionListener(this);
		
		
		//========================================
		// view xy button	
		btnViewXY = new MyToggleButton(app.getImageIcon("view_xy.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewXY.addActionListener(this);
		
		//========================================
		// view xz button	
		btnViewXZ = new MyToggleButton(app.getImageIcon("view_xz.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewXZ.addActionListener(this);		
		
		//========================================
		// view yz button	
		btnViewYZ = new MyToggleButton(app.getImageIcon("view_yz.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewYZ.addActionListener(this);	
		
		//========================================
		// projection view button
		ImageIcon[] projectionIcons = new ImageIcon[4];
		projectionIcons[0]=app.getImageIcon("stylebar_vieworthographic.gif");
		projectionIcons[1]=app.getImageIcon("stylebar_viewperspective.gif");
		projectionIcons[2]=app.getImageIcon("stylebar_viewanaglyph.gif");		
		projectionIcons[3]=app.getImageIcon("stylebar_viewcav.gif");
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
	}	
	
	private class ProjectionPopup extends PopupMenuButton implements ActionListener{
		
		public ProjectionPopup(Application app, ImageIcon[] projectionIcons){
			super(app, projectionIcons, 1, projectionIcons.length, new Dimension(16, 16), SelectionTable.MODE_ICON, true, false);
			addActionListener(this);
			setIcon(projectionIcons[((EuclidianView3D) ev).getProjection()]);
		}
		
		public void actionPerformed(ActionEvent e){
			Integer si = getSelectedIndex();
			//Application.debug(si);
			switch(si){
			case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
				((EuclidianView3D) ev).setProjectionOrthographic();
				break;
			case EuclidianView3D.PROJECTION_PERSPECTIVE:
				((EuclidianView3D) ev).setProjectionPerspective();
				break;
			case EuclidianView3D.PROJECTION_ANAGLYPH:
				((EuclidianView3D) ev).setAnaglyph();
				break;
			case EuclidianView3D.PROJECTION_CAV:
				((EuclidianView3D) ev).setCav();
				break;
			}
		}

		public void update(Object[] geos) {
			this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		}

	}
	
	public void setLabels(){
		super.setLabels();
		btnRotateView.setToolTipText(app.getPlainTooltip("stylebar.RotateView"));
		btnViewDefault.setToolTipText(app.getPlainTooltip("stylebar.ViewDefault"));
		btnViewXY.setToolTipText(app.getPlainTooltip("stylebar.ViewXY"));
		btnViewXZ.setToolTipText(app.getPlainTooltip("stylebar.ViewXZ"));
		btnViewYZ.setToolTipText(app.getPlainTooltip("stylebar.ViewYZ"));
		btnViewProjection.setToolTipText(app.getPlainTooltip("stylebar.ViewProjection"));
		//btnViewProjection.setSelectedIndex(((EuclidianView3D) ev).getProjection());
	}
	
	protected void updateGUI(){
		super.updateGUI();
		
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
		

		/*
		btnViewProjection.removeActionListener(this);
		btnViewProjection.setSelected(false);
		btnViewProjection.addActionListener(this);
		 */

		
		
	}
	
	
	protected PopupMenuButton[] newPopupBtnList(){
		PopupMenuButton[] superList = super.newPopupBtnList();
		PopupMenuButton[] ret = new PopupMenuButton[superList.length+2];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		
		int index = superList.length;
		ret[index]=btnRotateView;index++;
		ret[index]=btnViewProjection;
		return ret;
	}
	
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

}
