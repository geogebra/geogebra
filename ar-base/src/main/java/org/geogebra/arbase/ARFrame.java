package org.geogebra.arbase;

abstract public class ARFrame {

    public abstract Object getFrame();

    public abstract boolean isCameraTracking();

    public abstract Object getCamera();

    public abstract void setHit(Object hit);

    public abstract Object getHit();

    public abstract Object getTrackable();

    public abstract boolean isHitNull();

}
