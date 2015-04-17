package org.geogebra.desktop.geogebra3D.input3D.zspace;
 
import java.awt.Dimension;
import java.awt.Point;

import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.input3D.zspace.ZSpaceGeoGebra;


public class Socket {
 
	
	


	/** stylus x position */
    public double stylusX;
    /** stylus y position */
    public double stylusY;
    /** stylus z position */
    public double stylusZ;
	
    public double birdOrientationX, birdOrientationY, birdOrientationZ, birdOrientationW;
     
    public double leftEyeX, leftEyeY, leftEyeZ;
    public double rightEyeX, rightEyeY, rightEyeZ;
    public double glassesCenterX, glassesCenterY, glassesCenterZ;
    public double glassesOrientationX, glassesOrientationY, glassesOrientationZ, glassesOrientationW;

    public boolean buttonLeft, buttonRight;
    
    /** says if it has got a message from leo */
    public boolean gotMessage = false;      
    
    
   private ZSpaceGeoGebra zsggb;

 
    public Socket() {
     
    	ZSpaceGeoGebra.RunOnce();
    	zsggb = new ZSpaceGeoGebra();
        
		
    }
    
 

    
    
    
    public boolean getData(){
    	if (zsggb.eventOccured()){
    		
    		zsggb.getData();    		
//    		App.debug("\n"+zsggb.getData());
    		
    		stylusX = zsggb.getStylusX();
    		stylusY = zsggb.getStylusY();
    		stylusZ = zsggb.getStylusZ();
//    		App.debug("\nstylus: "+stylusX+","+stylusY+","+stylusZ);
    		
    		
    		leftEyeX = zsggb.getLeftEyeX();
    		leftEyeY = zsggb.getLeftEyeY();
    		leftEyeZ = zsggb.getLeftEyeZ();
    		
    		rightEyeX = zsggb.getRightEyeX();
    		rightEyeY = zsggb.getRightEyeY();
    		rightEyeZ = zsggb.getRightEyeZ();
    		
//    		App.debug("\nleft eye: "+leftEyeX+","+leftEyeY+","+leftEyeZ);
//    		App.debug("\nright eye: "+rightEyeX+","+rightEyeY+","+rightEyeZ);
    		
    		buttonLeft = zsggb.getButton(0);
    		buttonRight = zsggb.getButton(1);
    		
    		return true;
    	}
    	
    	return false;
    }



    private int viewPortX, viewPortY, viewPortW = -1, viewPortH;
    


	public boolean setViewPort(Point panelPosition, Dimension panelDimension) {
		
		boolean viewPortChanged = false;
		
		if (viewPortX != panelPosition.x){
			viewPortX = panelPosition.x;
			viewPortChanged = true;
		}
		
		if (viewPortY != panelPosition.y){
			viewPortY = panelPosition.y;
			viewPortChanged = true;
		}
		
		int v = (int) panelDimension.getWidth();
		if (viewPortW != v){
			viewPortW = v;
			viewPortChanged = true;
		}
		
		v = (int) panelDimension.getHeight();
		if (viewPortH != v){
			viewPortH = v;
			viewPortChanged = true;
		}
		
		if (viewPortChanged){
			zsggb.setViewPort(viewPortX, viewPortY, viewPortW, viewPortH);
			return true;
		}
		
		return false;
		
		
	}






	/**
	 * 
	 * @return eye separation
	 */
	public double getEyeSeparation() {
		return ZSpaceGeoGebra.EYE_SEP * zsggb.toPixelRatio();
	}
    
 
    
 
    
}

