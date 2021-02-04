package org.geogebra.arbase;

import org.geogebra.common.geogebra3D.euclidian3D.xr.XRMotionEvent;

abstract public class ARMotionEvent implements XRMotionEvent {

    public abstract float getX();

    public abstract float getY();

    public abstract float getX(int index);

    public abstract float getY(int index);

    public abstract int getPointerCount();

    public abstract int getAction();

    public abstract void setLocation(int x, int y);

    public Object getXREvent(){
        return null;
    }
}
