package org.geogebra.ar;

public interface ARFrame {

    Object getFrame();

    boolean isCameraTracking();

    Object getCamera();

    void setHit(Object hit);

    Object getHit();

    Object getTrackable();

    boolean isHitNull();

}
