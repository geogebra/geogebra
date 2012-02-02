package geogebra.common.euclidian;

import geogebra.common.kernel.Kernel;

public abstract class AbstractZoomer {
	protected enum ZoomerMode{ZOOM,ZOOM_RW,AXES,MOVE}
	/**
	 * 
	 */
	protected final AbstractEuclidianView view;

	static final int MAX_STEPS = 15; // frames

	protected static final int DELAY = 10;

	static final int MAX_TIME = 400; // millis

	
	
	protected ZoomerMode mode;
	
	protected double px, py; // zoom point

	protected double factor;

	protected int counter, steps;

	protected double oldScale, newScale, add, dx, dy;
	
	protected double x0,x1,y0,y1,xminOld,yminOld,ymaxOld,xmaxOld;

	protected long startTime;

	protected boolean storeUndo;

	public AbstractZoomer(AbstractEuclidianView view){
		this.view = view;
	}
	
	public void init(double ratio, boolean storeUndo) {
		// this.ratio = ratio;
		this.storeUndo = storeUndo;

		// zoomFactor = ratio / scaleRatio;
		oldScale = view.getYscale();
		newScale = view.getXscale() * ratio; // new yscale
		this.steps = MAX_STEPS;
		mode = ZoomerMode.AXES;
	}

	public void init(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		this.px = px;
		this.py = py;
		// this.zoomFactor = zoomFactor;
		this.storeUndo = storeUndo;

		oldScale = view.getXscale();
		newScale = view.getXscale() * zoomFactor;
		
		this.steps = Math.min(MAX_STEPS, steps);
		mode = ZoomerMode.ZOOM;
	}
	
	public void initRW(double x0, double x1, double y0, double y1, int steps,
			boolean storeUndo) {
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;

		xminOld = view.getXmin();
		xmaxOld = view.getXmax();
		yminOld = view.getYmin();
		ymaxOld = view.getYmax();
		// this.zoomFactor = zoomFactor;
		this.storeUndo = storeUndo;

		this.steps = Math.min(MAX_STEPS, steps);
		mode = ZoomerMode.ZOOM_RW;
	}
	
	public void init(double ox, double oy, boolean storeUndo) {
		this.px = ox;
		this.py = oy;
		this.storeUndo = storeUndo;
		mode = ZoomerMode.MOVE;
		this.steps = MAX_STEPS;
	}
	
	protected void step(){
		counter++;
		long time = System.currentTimeMillis() - startTime;
		if ((counter == steps) || (time > MAX_TIME)) { // end of animation
			stopAnimation();
		} else {
			switch(mode){
			case AXES:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(view.getxZero(), 
						view.getyZero(), view.getXscale(), oldScale * factor);
				break;
			case ZOOM:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor), oldScale
					* factor, oldScale * factor * view.getScaleRatio());
				break;
			case ZOOM_RW:
				double i = counter;
				double j = steps - counter;
				view.setRealWorldCoordSystem(((x0 * i) + (xminOld * j)) / steps,
					((x1 * i) + (xmaxOld * j)) / steps,
					((y0 * i) + (yminOld * j)) / steps,
					((y1 * i) + (ymaxOld * j)) / steps);
				break;
			case MOVE:	
				factor = 1.0 - (counter * add);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor), view.getXscale(),
					view.getYscale());
			}
		}
	}
	
	protected synchronized void stopAnimation() {
		stopTimer();
		// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
		switch(mode){
		case AXES:
			view.setCoordSystem(view.getxZero(), view.getyZero(), 
					view.getXscale(), newScale);
			break;
		case ZOOM:factor = newScale / oldScale;
		view.setCoordSystem(px + (dx * factor), py + (dy * factor), newScale,
				newScale * view.getScaleRatio());
			break;
		case ZOOM_RW:view.setRealWorldCoordSystem(x0,x1,y0,y1);
			break;
		case MOVE:view.setCoordSystem(px, py, view.getXscale(), view.getYscale());
			break;	
		}
		if(setStandard){
			setStandard = false;
			view.setAnimatedCoordSystem(standardX, standardY, 0, 
					AbstractEuclidianView.SCALE_STANDARD, MAX_STEPS, storeUndo);
		}
		if (storeUndo) {
			view.getApplication().storeUndoInfo();
		}
	}

	private boolean setStandard = false;
	private double standardX, standardY;
	public void setStandardViewAfter(double xzero, double yzero) {
		setStandard = true;
		standardX = xzero;
		standardY = yzero;
		
	}
	
	public synchronized void startAnimation() {
		if (!hasTimer()) {
			return;
		}
		switch(mode){
		case AXES:
			add = (newScale - oldScale) / steps;
			break;
		case ZOOM:add = (newScale - oldScale) / steps;
			dx = view.getxZero() - px;
			dy = view.getyZero() - py;
			break;
		case MOVE:
			dx = view.getxZero() - px;
			dy = view.getyZero() - py;
			if (Kernel.isZero(dx) && Kernel.isZero(dy)) {
				return;
			}
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MAX_STEPS;
			break;
		}
		counter = 0;

		startTime = System.currentTimeMillis();
		startTimer();
	}

	protected abstract void stopTimer();
	protected abstract void startTimer();
	protected abstract boolean hasTimer();

}
