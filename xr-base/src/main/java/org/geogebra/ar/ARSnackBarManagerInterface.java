package org.geogebra.ar;


public interface ARSnackBarManagerInterface {

    enum SnackBarMessage {NONE, DETECT_SURFACE, TAP_SCREEN}

    void updateSnackBarAR(SnackBarMessage message);

    void showRatio(String text);

    void hideRatio();

}
