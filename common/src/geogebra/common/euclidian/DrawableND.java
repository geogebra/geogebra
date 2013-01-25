package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Class for drawables in any dimension
 * 
 * @author matthieu
 *
 */
public abstract class DrawableND {
	
	
	private boolean createdByDrawList = false;	
	/** says if this drawable is visible when draw list is visible */
	private boolean createdByDrawListVisible = false;
	/** drawList that has created this */
	private DrawableND drawList;
	

	
	/**
	 * sets that this has been created by the DrawList
	 * @param drawList true if created by DrawList
	 */
	public void setCreatedByDrawList(DrawableND drawList) {
		createdByDrawList = true;
		setCreatedByDrawListVisible(true);
		
		if (drawList.isCreatedByDrawListVisible()){
			this.drawList = drawList.getDrawListCreator();
		}else{
			this.drawList = drawList;
		}
		
	}
	
	/**
	 * sets if this is visible when the DrawList is visible
	 * @param flag true to make this visible when the DrawList is visible
	 */
	public void setCreatedByDrawListVisible(boolean flag) {
		createdByDrawListVisible = flag;
		
	}
	
	/**
	 * @return if this has been created by a DrawList
	 */
	public boolean createdByDrawList() {
		return createdByDrawList;
		
	}
	
	/**
	 * @return if this is visible when the DrawList is visible
	 */
	public boolean isCreatedByDrawListVisible() {
		return createdByDrawListVisible;
		
	}
	/**
	 * @return the drawList that has created this (if one)
	 */
	public DrawableND getDrawListCreator(){
		return drawList;
	}
	
	
	
	/**
	 * @return the geo linked to this
	 */
	public abstract GeoElement getGeoElement();
	
	
	/**
	 * says that the drawable has to be updated
	 * in 2D : update it immediately
	 * in 3D : update at next frame
	 */
	public void setWaitForUpdate(){
		
		update();
	}
	
	/**
	 * update it immediately
	 */
	public abstract void update();

	/**
	 * @return x-coord of the label
	 */
	public double getxLabel() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return y-coord of the label
	 */
	public double getyLabel() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
