package org.geogebra.common.euclidian;

import org.geogebra.common.awt.*;
import org.geogebra.common.javax.swing.GBox;

/**
 * Class used for testing.
 */
public class EuclidianViewCommon extends EuclidianView {

    private GGraphics2D tempGrapics = new GGraphicsCommon();

    @Override
    public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
        return tempGrapics;
    }

    @Override
    public GFont getFont() {
        return null;
    }

    @Override
    protected void initCursor() {
        // ignore empty method
}

    @Override
    protected void setStyleBarMode(int mode) {
        // ignore empty method
    }

    @Override
    protected void updateSizeKeepDrawables() {
        // ignore empty method
    }

    @Override
    public boolean requestFocusInWindow() {
        return false;
    }

    @Override
    public void paintBackground(GGraphics2D g2) {
        // ignore empty method
}

    @Override
    protected void drawResetIcon(GGraphics2D g) {
        // ignore empty method
}

    @Override
    public void setBackground(GColor bgColor) {
        // ignore empty method
}

    @Override
    public void setPreferredSize(GDimension preferredSize) {
        // ignore empty method
}

    @Override
    protected CoordSystemAnimation newZoomer() {
        return null;
    }

    @Override
    public void add(GBox box) {
        // ignore empty method
}

    @Override
    public void remove(GBox box) {
        // ignore empty method
}

    @Override
    public GGraphics2D getGraphicsForPen() {
        return null;
    }

    @Override
    protected EuclidianStyleBar newEuclidianStyleBar() {
        return null;
    }

    @Override
    protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
        // ignore empty method
}

    @Override
    protected EuclidianStyleBar newDynamicStyleBar() {
        return null;
    }

    @Override
    public void readText(String text) {
        // ignore empty method
}

    @Override
    public void repaint() {
        // ignore empty method
}

    @Override
    public GColor getBackgroundCommon() {
        return null;
    }

    @Override
    public boolean hitAnimationButton(int x, int y) {
        return false;
    }

    @Override
    public void setCursor(EuclidianCursor cursor) {
        // ignore empty method
}

    @Override
    public void setToolTipText(String plainTooltip) {
        // ignore empty method
}

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public void requestFocus() {
        // ignore empty method
}

    @Override
    public void closeDropdowns() {
        // ignore empty method
}

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public EuclidianController getEuclidianController() {
        return null;
    }

    @Override
    public boolean suggestRepaint() {
        return false;
    }

    @Override
    public void clearView() {
        // ignore empty method
    }
}
