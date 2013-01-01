package geogebra3D.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.EuclidianStyleBarD;
import geogebra.gui.util.MyToggleButton;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

import java.util.ArrayList;



/**
 * StyleBar for view for plane
 * 
 * @author matthieu
 *
 */
public class EuclidianStyleBarForPlane extends EuclidianStyleBarD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	private MyToggleButton btnOrientation;


	/**
	 * Common constructor.
	 * @param ev
	 */
	public EuclidianStyleBarForPlane(EuclidianViewForPlane ev) {
		super(ev);
	}
	

	@Override
	protected void addGraphicsDecorationsButtons(){
		//add(btnShowAxes);
		add(btnShowGrid);
	}
	
	@Override
	protected void addBtnRotateView(){

		add(btnOrientation);

	}
	

	@Override
	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D() ;
	}
	
	
	@Override
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		
		if (source.equals(btnOrientation)) {
			((EuclidianViewForPlane) ev).updateOrientationRegardingView();
		}else
			super.processSource(source, targetGeos);
	}

	@Override
	protected void createButtons() {

		super.createButtons();
		
		// ========================================
		// show grid button
		btnOrientation = new MyToggleButton(app.getImageIcon("stylebar_orientation.gif"),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);
			}
		};
		btnOrientation.addActionListener(this);

		
	}	
	
	
	@Override
	public void setLabels(){
		super.setLabels();
		btnOrientation.setToolTipText(app.getPlainTooltip("stylebar.Orientation"));
		
	}
	
	@Override
	protected void updateGUI(){
		super.updateGUI();
		
		btnOrientation.removeActionListener(this);
		btnOrientation.setSelected(false);
		btnOrientation.addActionListener(this);
		
		

		
	}
	
	

}
