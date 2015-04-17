package org.geogebra.desktop.geogebra3D.input3D.zspace;



public class ZSpaceGeoGebra {
	
	
	
	
    public static final double EYE_SEP = 1;





	public ZSpaceGeoGebra() {


    }
    
    
    
    
    
    public void setEventOccured(){
    }
    
    public boolean eventOccured(){
    	return false;
    }
    
    public void getData(){
    }
    
    /**
     * 
     * @param i i-th button
     * @return state of the i-th button
     */
    public boolean getButton(int i){
    	return false;
    }
    

    
    public void setViewPort(int x, int y, int w, int h){
    }
    
    
    
	/**
	 * @return last calculated x coord
	 */
	public double getStylusX(){
		return 0;
	}
	
	/**
	 * @return last calculated y coord
	 */
	public double getStylusY(){
		return 0;
	}
	
	/**
	 * @return last calculated z coord
	 */
	public double getStylusZ(){
		return 0;
	}
	
	/**
	 * @return last calculated left eye x coord
	 */
	public double getLeftEyeX(){
		return 0;
	}
	
	/**
	 * @return last calculated left eye y coord
	 */
	public double getLeftEyeY(){
		return 0;
	}
	
	/**
	 * @return last calculated left eye z coord
	 */
	public double getLeftEyeZ(){
		return 0;
	}

	/**
	 * @return last calculated right eye x coord
	 */
	public double getRightEyeX(){
		return 0;
	}
	
	/**
	 * @return last calculated right eye y coord
	 */
	public double getRightEyeY(){
		return 0;
	}
	
	/**
	 * @return last calculated right eye z coord
	 */
	public double getRightEyeZ(){
		return 0;
	}

	/**
	 * 
	 * @return real world dim to pixel dim ratio
	 */
	public double toPixelRatio(){
		return 1;
	}





	public static void RunOnce() {
	}


}
