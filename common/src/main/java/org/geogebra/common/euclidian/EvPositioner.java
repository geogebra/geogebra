package org.geogebra.common.euclidian;

import org.geogebra.common.gui.EdgeInsets;
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
    private int oldWidth = -1;
    private boolean isAnimationEnabled;

    protected EvPositioner(EuclidianView euclidianView) {
        this.euclidianView = euclidianView;
        settings = euclidianView.getSettings();
    }

    protected abstract int getAvWidth();

    protected abstract int getAvHeight();

    protected abstract boolean isPortrait();

    protected abstract int translateToDp(int pixels);

    protected abstract int getTopInset();

    /**
     * Initializes the xZero and yZero.
     *
     * @param width width
     * @param height height
     */
    public void initIfEvSizeNotSet(int width, int height) {
        oldWidth = settings.getRawWidth();
        if (oldWidth < 0) {
            initialize(width, height);
        }
    }

    protected void initialize(int width, int height) {
        oldWidth = width;
        settings.setVisibleFromX(0);
        settings.setVisibleUntilY(height);
        double xZero = width / 2.0;
        double yZero = height / 2.0;
        settings.setOriginNoUpdate(xZero, yZero);
        euclidianView.xZero = xZero;
        euclidianView.yZero = yZero;
    }

    /**
     * Centers the EV on app start or after orientation change
     */
    public void reCenter() {
        try {
            centerWithAvSize(getAvWidth(), getAvHeight());
        } catch (ViewDestroyedException ignored) {
            // no AV to measure
        }
    }

    /**
     * @param overlappedWidth width hidden by another view
     * @param overlappedHeight height hidden by another view
     */
    public void centerWithAvSize(int overlappedWidth, int overlappedHeight) {
        boolean isPortrait;
        try {
            isPortrait = isPortrait();
        } catch (ViewDestroyedException ignored) {
            return;
        }

        int newVisibleFromX = isPortrait ? 0 : translateToDp(overlappedWidth);
        int newVisibleUntilY =
                isPortrait
                        ? settings.getHeight() - translateToDp(overlappedHeight)
                        : settings.getHeight();

        euclidianView.xZero = getNewXZero(newVisibleFromX);
        euclidianView.yZero = getNewYZero(newVisibleUntilY);
        oldWidth = settings.getRawWidth();

        settings.setVisibleFromX(newVisibleFromX);
        settings.setVisibleUntilY(newVisibleUntilY);

        settings.setOriginNoUpdate(euclidianView.xZero, euclidianView.yZero);

        euclidianView.updateSizeChange();
    }

    private double getNewXZero(int newVisibleFromX) {
        double newSize = newVisibleFromX + settings.getWidth();
        double oldSize = settings.getVisibleFromX() + oldWidth;
        double dx = (newSize - oldSize) / 2.0;
        return settings.getXZero() + dx;
    }

    private double getNewYZero(int newVisibleUntilY) {
        double dy = (newVisibleUntilY - settings.getVisibleUntilY()) / 2.0;
        return settings.getYZero() + dy;
    }

    /**
     * Pans the EV after AV (and keyboard) animations
     *
     * @param avWidth av width
     * @param avHeight av height
     */
    public void onAvSizeChanged(int avWidth, int avHeight) {
        boolean isPortrait;
        try {
            isPortrait = isPortrait();
        } catch (ViewDestroyedException ignored) {
            return;
        }

        int x, y;
        int avWidthDp = translateToDp(avWidth);
        int avHeightDp = translateToDp(avHeight);
        if (isPortrait) {
            x = 0;
            y = euclidianView.getHeight() - avHeightDp;
        } else {
            x = avWidthDp;
            y = euclidianView.getHeight();
        }

        if (x != settings.getVisibleFromX() || y != settings.getVisibleUntilY()) {
            updateVisibleFromX(x);
            updateVisibleUntilY(y);
            updateVisibleEv();
        }
        updateEuclidianViewSafeAreaInsets(avWidthDp, avHeightDp);
    }

    private void updateEuclidianViewSafeAreaInsets(int avWidthDp, int avHeightDp) {
        boolean isPortrait;
        try {
            isPortrait = isPortrait();
        } catch (ViewDestroyedException ignored) {
            return;
        }

        int margin = EuclidianView.MINIMUM_SAFE_AREA;
        int leftInset = isPortrait ? 0 : avWidthDp;
        int topInset = translateToDp(getTopInset());
        int rightInset = 0;
        int bottomInset = isPortrait ? avHeightDp : 0;
        EdgeInsets insets = new EdgeInsets(leftInset + margin, topInset + margin,
                rightInset + margin, bottomInset + margin);
        euclidianView.setSafeAreaInsets(insets);
    }

    private void updateVisibleFromX(int x) {
        moveToX = (int) (settings.getXZero() + (x - settings.getVisibleFromX()) / 2.0);
        settings.setVisibleFromX(x);
    }

    private void updateVisibleUntilY(int y) {
        moveToY = (int) (settings.getYZero() + (y - settings.getVisibleUntilY()) / 2.0);
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
        euclidianView.updateSizeChange();
    }

    protected void setOldWidth(int oldWidth) {
        this.oldWidth = oldWidth;
    }

    protected EuclidianView getEuclidianView() {
        return euclidianView;
    }

    protected class ViewDestroyedException extends IllegalStateException {

        public ViewDestroyedException() {
            super("The view is destroyed.");
        }
    }
}
