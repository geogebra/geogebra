package org.geogebra.common.euclidian;

import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.DoubleUtil;

/**
 * Recenters the Euclidian View after Clear all menu action and Algebra View size changes.
 */
public abstract class EvPositioner {

    private EuclidianView euclidianView;
    private EuclidianSettings settings;
    private int moveToX;
    private int moveToY;
    private boolean isAnimationEnabled;

    protected EvPositioner(EuclidianView euclidianView) {
        this.euclidianView = euclidianView;
        settings = euclidianView.getSettings();
    }

    /**
     * Recenters the Euclidian View on app start and on Clear all.
     */
    public abstract void reCenter();

    protected abstract boolean isPortrait();

    protected abstract int translateToDp(int pixels);

    /**
     * Pans the EV after AV (and keyboard) animations
     * @param avWidth av width
     * @param avHeight av height
     */
    public void onAvSizeChanged(int avWidth, int avHeight) {
        int x, y;
        if (isPortrait()) {
            x = 0;
            y = euclidianView.getHeight() - translateToDp(avHeight);
        } else {
            x = translateToDp(avWidth);
            y = euclidianView.getHeight();
        }

        if (x != settings.getVisibleFromX() || y != settings.getVisibleUntilY()) {
            updateVisibleFromX(x);
            updateVisibleUntilY(y);
            updateVisibleEv();
        }
    }

    private void updateVisibleFromX(int x) {
        moveToX = (int) (settings.getXZero() + (x - settings.getVisibleFromX()) / 2);
        settings.setVisibleFromX(x);
    }

    private void updateVisibleUntilY(int y) {
        moveToY = (int) (settings.getYZero() + (y - settings.getVisibleUntilY()) / 2);
        settings.setVisibleUntilY(y);
    }

    private void updateVisibleEv() {
        if (DoubleUtil.isEqual(moveToX, euclidianView.xZero)
                && DoubleUtil.isEqual(moveToY, euclidianView.yZero)) {
            return;
        }

        if (isAnimationEnabled) {
            euclidianView.animateMove(moveToX, moveToY, false);
        }
        euclidianView.xZero = moveToX;
        euclidianView.yZero = moveToY;
        settings.setOriginNoUpdate(moveToX, moveToY);
        updateEvSizeChange();
    }

    protected EuclidianView getEuclidianView() {
        return euclidianView;
    }

    protected void updateEvSizeChange() {
        euclidianView.updateSizeChange();
    }

    protected EuclidianSettings getSettings() {
        return settings;
    }
}
