package geogebra.common.euclidian;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
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
}
