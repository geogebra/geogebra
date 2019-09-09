package org.geogebra.common.kernel.geos.properties;

public enum TextAlignment {
    LEFT, RIGHT, CENTER;

    @Override
    public String toString() {
        switch (this) {
            case LEFT:
                return "left";
            case CENTER:
                return "center";
            case RIGHT:
                return "right";
            default:
            	return null;
        }
    }
}
