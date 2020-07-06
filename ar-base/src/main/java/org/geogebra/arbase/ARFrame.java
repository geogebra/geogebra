package org.geogebra.arbase;

public interface ARFrame {

    Object getFrame();

    boolean isCameraTracking();

    Object getCamera();

    void setHit(Object hit);

    Object getHit();

    Object getTrackable();

    boolean isHitNull();

}
