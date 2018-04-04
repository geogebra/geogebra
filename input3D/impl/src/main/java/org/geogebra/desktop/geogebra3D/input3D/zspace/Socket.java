package org.geogebra.desktop.geogebra3D.input3D.zspace;
 
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;


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
     
	// start values
	public double leftEyeX = 0,
			leftEyeY = -EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT
					* Math.sin(Math.PI / 6),
			leftEyeZ = EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT
					* Math.cos(Math.PI / 6);
	public double rightEyeX = EuclidianSettings3D.EYE_SEP_DEFAULT,
			rightEyeY, rightEyeZ;
    public double glassesCenterX, glassesCenterY, glassesCenterZ;
    public double glassesOrientationX, glassesOrientationY, glassesOrientationZ, glassesOrientationW;

	public boolean wantsStereo, stylusDetected = false;

	public boolean buttonLeft, buttonRight, button3;
    
    /** says if it has got a message from leo */
    public boolean gotMessage = false;      
    
    
   private ZSpaceGeoGebra zsggb;

	private static boolean LIBRARY_LOADED = false;
	private static boolean ZSPACE_INITED = false;

	public static void initZSpace() throws Input3DException {

		if (!LIBRARY_LOADED) {
			ZSpaceGeoGebra.RunOnce();
			LIBRARY_LOADED = true;
		}

		
		if (!ZSPACE_INITED) {
			ZSpaceGeoGebra.Initialize();
			ZSPACE_INITED = true;
		}

	}

 
	public Socket() throws Input3DException {
     
		initZSpace();
    	zsggb = new ZSpaceGeoGebra();
        
		
    }
    
 

    
    
    
    public boolean getData(){

		wantsStereo = zsggb.wantsStereo();

    	if (zsggb.eventOccured()){
    		
    		zsggb.getData();    		
//    		App.debug("\n"+zsggb.getData());
    		
    		stylusX = zsggb.getStylusX();
    		stylusY = zsggb.getStylusY();
    		stylusZ = zsggb.getStylusZ();
			// App.debug("\nstylus: " + stylusX + "," + stylusY + "," +
			// stylusZ);
    		
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
    		
			stylusDetected = zsggb.stylusDetected();
			// App.debug("" + stylusDetected);
    		
    		leftEyeX = zsggb.getLeftEyeX();
    		leftEyeY = zsggb.getLeftEyeY();
    		leftEyeZ = zsggb.getLeftEyeZ();
    		
    		rightEyeX = zsggb.getRightEyeX();
    		rightEyeY = zsggb.getRightEyeY();
    		rightEyeZ = zsggb.getRightEyeZ();
    		
			// App.debug("\nleft eye: " + leftEyeX + "," + leftEyeY + ","
			// + leftEyeZ);
//    		App.debug("\nright eye: "+rightEyeX+","+rightEyeY+","+rightEyeZ);
    		

    		buttonLeft = zsggb.getButton(0);
    		buttonRight = zsggb.getButton(1);
			button3 = zsggb.getButton(2);
    		
    		return true;
    	}
    	

    	return false;
    }



    private int viewPortX, viewPortY, viewPortW = -1, viewPortH;
    


	public boolean setViewPort(int width, int height, int x, int y) {
		
		boolean viewPortChanged = false;
		
		if (viewPortX != x) {
			viewPortX = x;
			viewPortChanged = true;
		}
		
		if (viewPortY != y) {
			viewPortY = y;
			viewPortChanged = true;
		}
		
		int v = width;
		if (viewPortW != v){
			viewPortW = v;
			viewPortChanged = true;
		}
		
		v = height;
		if (viewPortH != v){
			viewPortH = v;
			viewPortChanged = true;
		}
		
		if (viewPortChanged){
			zsggb.setViewPort(viewPortX, viewPortY, viewPortW, viewPortH);
			return true;
		}
		
		zsggb.update();
		return false;

	}






	/**
	 * 
	 * @return eye separation
	 */
	public double getEyeSeparation() {
		return 2 * ZSpaceGeoGebra.EYE_SEP_HALF * zsggb.toPixelRatio();
	}
    
 
	/**
	 * 
	 * @return display angle with ground
	 */
	public double getDisplayAngle() {
		return zsggb.getDisplayAngle();
	}
 
    
}

