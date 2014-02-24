package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.main.App;

public abstract class EuclidianControllerWeb extends EuclidianController {

	protected boolean zoomY;

	protected boolean zoomX;

	protected double scale;

	public EuclidianControllerWeb(App app) {
		super(app);
	}

	@Override
	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		Hits hits1 = view.getHits();
		view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = view.getHits();

		this.zoomY = hits1.hasYAxis() && hits2.hasYAxis();
		this.zoomX = hits1.hasXAxis() && hits2.hasXAxis();

		if (this.zoomY) {
			this.oldDistance = y1 - y2;
			this.scale = this.view.getYscale();
		} else if (this.zoomX) {
			this.oldDistance = x1 - x2;
			this.scale = this.view.getXscale();
		} else {
			super.twoTouchStart(x1, y1, x2, y2);
		}
	}

	@Override
	public void twoTouchMove(double x1, double y1, double x2, double y2) {
		if (this.zoomY) {
			double newRatio = this.scale * (y1 - y2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), this.view.getXscale(), newRatio);
		} else if (this.zoomX) {
			double newRatio = this.scale * (x1 - x2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), newRatio, this.view.getYscale());
		} else {
			super.twoTouchMove(x1, y1, x2, y2);
		}
	}
}
