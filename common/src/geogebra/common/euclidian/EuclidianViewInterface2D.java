package geogebra.common.euclidian;

import java.awt.geom.AffineTransform;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.AbstractApplication;

public interface EuclidianViewInterface2D extends EuclidianViewInterfaceSlim{
	public void updateForPlane();

	public int getMaxLayerUsed();

	public AbstractKernel getKernel();
	public AbstractApplication getApplication();

	public void updateBackgroundImage();

	public double getInvXscale();
	public double getInvYscale();

	public void updateBackground();

	public int getFontSize();

	public double toScreenCoordXd(double aRW);
	public double toScreenCoordYd(double aRW);
	public int toScreenCoordY(double aRW);
	public int toScreenCoordX(double aRW);

	public boolean toScreenCoords(double[] coords);

	public double getyZero();
	public double getxZero();
	public double getScaleRatio();
	public int getMode();
	public Coords getCoordsForView(Coords coords);

	public int getRightAngleStyle();

	public CoordMatrix getMatrix();

	public Graphics2D getBackgroundGraphics();

	public Graphics2D getTempGraphics2D(geogebra.common.awt.Font plainFontCommon);

	public int getBooleanSize();

	public geogebra.common.awt.AffineTransform getCoordTransform();

	public geogebra.common.awt.Font getFontAngle();
	public geogebra.common.awt.Font getFontLine();
	public geogebra.common.awt.Font getFontPoint();
	public geogebra.common.awt.Font getFontConic();

	public int getCapturingThreshold();

	public geogebra.common.awt.GeneralPath getBoundingPath();

	public int getPointStyle();

	public geogebra.common.awt.AffineTransform getTransform(GeoConicND conic, Coords m, Coords[] ev);

	public boolean isOnScreen(double[] a);

	public int toClippedScreenCoordX(double x);
	public int toClippedScreenCoordY(double y);

	public geogebra.common.awt.Font getFont();

	public  geogebra.common.awt.Font  getFontVector();

	public Color getBackgroundCommon();
}
