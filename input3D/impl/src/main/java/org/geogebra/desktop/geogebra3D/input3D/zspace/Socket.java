package org.geogebra.desktop.geogebra3D.input3D.zspace;
 
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;


public class Socket {
 
	
	


	/** stylus x position */
    public double stylusX;
    /** stylus y position */
    public double stylusY;
    /** stylus z position */
    public double stylusZ;
	
	/** stylus x direction */
	public double stylusDX;
	/** stylus y direction */
	public double stylusDY;
	/** stylus z direction */
	public double stylusDZ;

    public double stylusOrientationX, stylusOrientationY, stylusOrientationZ, stylusOrientationW;
     
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
    		
			stylusDX = zsggb.getStylusDX();
			stylusDY = zsggb.getStylusDY();
			stylusDZ = zsggb.getStylusDZ();
			// App.debug("\nstylus: " + stylusDX + "," + stylusDY + "," +
			// stylusDZ);

    		stylusOrientationX = zsggb.getStylusQX();
    		stylusOrientationY = zsggb.getStylusQY();
    		stylusOrientationZ = zsggb.getStylusQZ();
    		stylusOrientationW = zsggb.getStylusQW();
			// App.debug("\n" + zsggb.getStylusMatrix());
    		
    		
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
    


	public boolean setViewPort(GPoint panelPosition, GDimension panelDimension) {
		
		boolean viewPortChanged = false;
		
		if (viewPortX != panelPosition.x){
			viewPortX = panelPosition.x;
			viewPortChanged = true;
		}
		
		if (viewPortY != panelPosition.y){
			viewPortY = panelPosition.y;
			viewPortChanged = true;
		}
		
		int v = panelDimension.getWidth();
		if (viewPortW != v){
			viewPortW = v;
			viewPortChanged = true;
		}
		
		v = panelDimension.getHeight();
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
		return 2 * ZSpaceGeoGebra.EYE_SEP_HALF * zsggb.toPixelRatio();
	}
    
 
    
 
    
}

