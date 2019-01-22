package org.geogebra.arbase;


public interface ARSnackBarManagerInterface {

    enum SnackBarMessage {NONE, DETECT_SURFACE, TAP_SCREEN}

    void updateSnackBarAR(SnackBarMessage message);
}
