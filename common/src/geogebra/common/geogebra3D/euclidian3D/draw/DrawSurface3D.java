package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordsFloat3;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra.common.main.App;
import geogebra.common.util.debug.Log;

/**
 * Class for drawing a 2-var function
 * 
 * @author mathieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {

	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;

	private double uDelta, vDelta;

	// number of intervals in root mesh (for each parameters, if parameters
	// delta are equals)
	private static final short ROOT_MESH_INTERVALS = 10;


	// number of split for boundary
	private static final short BOUNDARY_SPLIT = 10;

	// max split array size ( size +=4 for one last split)
	private static final int MAX_SPLIT = 4096;

	// draw array size ( size +=1 for one last draw)
	private static final int MAX_DRAW = MAX_SPLIT;

	private static final int CORNER_LIST_SIZE = MAX_DRAW * 3;
	
	/**
	 * max splits in one update loop
	 */
	private static final int MAX_SPLITS_IN_ONE_UPDATE = 512;

	private DrawSurface3D.Corner[] currentSplit, nextSplit, cornerList;

	/**
	 * list of things to draw
	 */
	protected CornerAndCenter[] drawList;

	private int currentSplitIndex, nextSplitIndex, cornerListIndex;
	private int currentSplitStoppedIndex;
	protected int loopSplitIndex;
	protected int drawListIndex;

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;
		this.surfaceGeo.setDerivatives();

		currentSplit = new DrawSurface3D.Corner[MAX_SPLIT + 4];
		nextSplit = new DrawSurface3D.Corner[MAX_SPLIT + 4];
		drawList = new CornerAndCenter[MAX_DRAW + 100];
		cornerList = new DrawSurface3D.Corner[CORNER_LIST_SIZE];
		
		ccForStillToSplit = new CornerAndCenter();
		cornerForStillToSplit = new Corner[6];
		vertexForStillToSplit = new CoordsFloat3[12];
		normalForStillToSplit = new CoordsFloat3[12];

	}
	
	final static private boolean DEBUG = true;
	
	/**
	 * console debug
	 * @param s message
	 */
	final static protected void debug(String s){
		if (DEBUG){
			Log.debug(s);
		}
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getSurfaceIndex());
	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		drawGeometry(renderer);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}
	
	private boolean drawFromScratch = true;

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		PlotterSurface surface = renderer.getGeometryManager().getSurface();

		if (drawFromScratch){
			
			// maybe set to null after redefine
			surfaceGeo.setDerivatives();

			// calc min/max values
			double uMin = surfaceGeo.getMinParameter(0);
			double uMax = surfaceGeo.getMaxParameter(0);
			double vMin = surfaceGeo.getMinParameter(1);
			double vMax = surfaceGeo.getMaxParameter(1);

			if (((GeoElement) surfaceGeo).isGeoFunctionNVar()) {
				if (Double.isNaN(uMin)) {
					uMin = getView3D().getXmin();
				}
				if (Double.isNaN(uMax)) {
					uMax = getView3D().getXmax();
				}
				if (Double.isNaN(vMin)) {
					vMin = getView3D().getYmin();
				}
				if (Double.isNaN(vMax)) {
					vMax = getView3D().getYmax();
				}
			}

			uDelta = uMax - uMin;
			if (Kernel.isZero(uDelta)) {
				setSurfaceIndex(-1);
				return true;
			}
			vDelta = vMax - vMin;
			if (Kernel.isZero(vDelta)) {
				setSurfaceIndex(-1);
				return true;
			}


			// max values
			maxRWPixelDistance = getView3D().getMaxPixelDistance() / getView3D().getScale();

			maxRWDistanceNoAngleCheck = 1 * maxRWPixelDistance;
			maxRWDistance = 5 * maxRWPixelDistance;
			maxBend = Math.tan(20 * Kernel.PI_180);

			// maxRWDistanceNoAngleCheck = 1 * maxRWPixelDistance;
			// maxRWDistance = 2 * maxRWPixelDistance;
			// maxBend = getView3D().getMaxBend();

			updateCullingBox();

			debug("\nmax distances = " + maxRWDistance + ", " + maxRWDistanceNoAngleCheck);

			// create root mesh
			int uN = 1 + (int) (ROOT_MESH_INTERVALS * Math.sqrt(uDelta / vDelta));
			int vN = 1 + ROOT_MESH_INTERVALS * ROOT_MESH_INTERVALS / uN;
			debug("grids: " + uN + ", " + vN);
			cornerListIndex = 0;
			Corner corner = createRootMesh(uMin, uMax, uN, vMin, vMax, vN);

			// split root mesh as start
			currentSplitIndex = 0;
			currentSplitStoppedIndex = 0;
			nextSplitIndex = 0;
			drawListIndex = 0;
			notDrawn = 0;
			splitRootMesh(corner);
			debug("\nnot drawn after split root mesh: " + notDrawn);
				
			// now splitted root mesh is ready
			drawFromScratch = false;
		}
		
		
		// start recursive split
		loopSplitIndex = 0;
		long time = System.currentTimeMillis();
		boolean stillRoomLeft = split(false);
		
		time = System.currentTimeMillis() - time;
		if (time > 0){
			App.debug("split : "+time);
		}

		debug("\ndraw size : " + drawListIndex + "\nnot drawn : " + notDrawn + 
				"\nstill to split : "  + (currentSplitIndex - currentSplitStoppedIndex) + 
				"\nnext to split : "  + nextSplitIndex + 
				"\ncorner list size : " + cornerListIndex +
				"\nstill room left : "+stillRoomLeft);

		boolean waitingSplits = (currentSplitIndex - currentSplitStoppedIndex) + nextSplitIndex > 0;

		
		
		
		time = System.currentTimeMillis();
		
		// draw splitted, still to split, and next to split
		surface.start(getReusableSurfaceIndex());

		if (!stillRoomLeft){
			for (int i = currentSplitStoppedIndex; i < currentSplitIndex; i++) {
				currentSplit[i].split(true);
			}
			for (int i = 0; i < nextSplitIndex; i++) {
				nextSplit[i].split(true);
			}
			debug("\n--- draw size : " + drawListIndex);
			if (drawListIndex > 0) {
				surface.startTriangles(CORNER_LIST_SIZE * 10);
				for (int i = 0; i < drawListIndex; i++) {
					drawList[i].draw(surface);
				}
				surface.endGeometryDirect();
			}
		}else{
			if (drawListIndex > 0 || waitingSplits) {
				surface.startTriangles(CORNER_LIST_SIZE * 10);
				for (int i = 0; i < drawListIndex; i++) {
					drawList[i].draw(surface);
				}
				for (int i = currentSplitStoppedIndex; i < currentSplitIndex; i++) {
					currentSplit[i].drawAsStillToSplit(surface);
				}
				for (int i = 0; i < nextSplitIndex; i++) {
					nextSplit[i].drawAsStillToSplit(surface);
					//nextSplit[i].drawAsNextToSplit(surface);
				}

				surface.endGeometryDirect();
			}
		}

		setSurfaceIndex(surface.end());
		
		time = System.currentTimeMillis() - time;
		if (time > 0){
			App.debug("draw : "+time);
		}
		
		// update is finished if no waiting splits, or if no room left for draw or split
		return !waitingSplits || !stillRoomLeft;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			setWaitForUpdate();
		}
	}
	
	@Override
	public void setWaitForUpdate() {
		drawFromScratch = true;
		super.setWaitForUpdate();
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	private boolean updateCullingBox() {
		EuclidianView3D view = getView3D();
		double off = maxRWPixelDistance * 2;
		cullingBox[0] = view.getXmin() - off;
		cullingBox[1] = view.getXmax() + off;
		cullingBox[2] = view.getYmin() - off;
		cullingBox[3] = view.getYmax() + off;
		cullingBox[4] = view.getZmin() - off;
		cullingBox[5] = view.getZmax() + off;
		return true;
	}

	private boolean inCullingBox(CoordsFloat3 p) {

		if ((p.x > cullingBox[0]) && (p.x < cullingBox[1])
				&& (p.y > cullingBox[2]) && (p.y < cullingBox[3])
				&& (p.z > cullingBox[4]) && (p.z < cullingBox[5])) {
			return true;
		}

		return false;
	}


	private Corner createRootMesh(double uMin, double uMax, int uN, double vMin, double vMax, int vN) {


		Corner bottomRight = newCorner(uMax, vMax);
		Corner first = bottomRight;

		// first row
		Corner right = bottomRight;
		for (int i = 1; i <= uN; i++) {
			Corner left = newCorner(uMax - (uDelta * i) / uN, vMax);
			right.l = left;
			right = left;
		}

		// all rows
		for (int j = 1; j <= vN; j++) {
			double v = vMax - (vDelta * j) / vN;
			Corner below = bottomRight;
			right = newCorner(uMax, v);
			below.a = right;
			for (int i = 1; i <= uN; i++) {
				Corner left = newCorner(uMax - (uDelta * i) / uN, v);
				right.l = left;
				right = left;
				below = below.l;
				below.a = right;
			}
			bottomRight = bottomRight.a;
		}

		return first;

	}


	protected int notDrawn;

	private void splitRootMesh(Corner first) {

		Corner nextAbove, nextLeft;

		Corner current = first;
		while (current.a != null) {
			nextAbove = current.a;
			while (current.l != null) {
				nextLeft = current.l;
				if (nextLeft.a == null) { // already splitted by last row
					nextLeft = nextLeft.l;
				}
				// App.debug(current.u + "," + current.v);
				current.split(false);
				current = nextLeft;
			}
			current = nextAbove;
		}

	}

	private boolean split(boolean draw) {

		if (currentSplitStoppedIndex == currentSplitIndex){
			// swap stacks
			Corner[] tmp = currentSplit;
			currentSplit = nextSplit;
			nextSplit = tmp;
			currentSplitIndex = nextSplitIndex;
			nextSplitIndex = 0;
			currentSplitStoppedIndex = 0;
		}
		
		
		while (currentSplitStoppedIndex < currentSplitIndex && loopSplitIndex < MAX_SPLITS_IN_ONE_UPDATE) {
			currentSplit[currentSplitStoppedIndex].split(false);
			currentSplitStoppedIndex++;
			
			if (drawListIndex + (currentSplitIndex - currentSplitStoppedIndex) + nextSplitIndex >= MAX_DRAW){ // no room left for new draw
				return false;
			}
			
			if (nextSplitIndex >= MAX_SPLIT){ // no room left for new split
				return false;
			} 
		}
		

		//debug("nextSplitIndex = " + nextSplitIndex + " , drawListIndex = " + drawListIndex);

		
		if (loopSplitIndex < MAX_SPLITS_IN_ONE_UPDATE && nextSplitIndex > 0){
			return split(false);
		}
		
		return true; // went to end of loop

	}

	private CoordsFloat3 evaluatedPoint = new CoordsFloat3();
	private CoordsFloat3 evaluatedNormal = new CoordsFloat3();

	protected CoordsFloat3 evaluatePoint(double u, double v) {
		surfaceGeo.evaluatePoint(u, v, evaluatedPoint);

		if (!evaluatedPoint.isDefined()) {
			return CoordsFloat3.UNDEFINED;
		}

		if (inCullingBox(evaluatedPoint)) {
			return evaluatedPoint.copyVector();
		}

		return CoordsFloat3.UNDEFINED;
	}


	protected CoordsFloat3 evaluatePoint(double u, double v, CoordsFloat3 p) {

		// p is final value: use evaluatedPoint to compute
		if (p == null || p.isFinalUndefined()) {
			surfaceGeo.evaluatePoint(u, v, evaluatedPoint);

			if (!evaluatedPoint.isDefined()) {
				return CoordsFloat3.UNDEFINED;
			}

			if (inCullingBox(evaluatedPoint)) {
				return evaluatedPoint.copyVector();
			}

			return CoordsFloat3.UNDEFINED;
		}

		// p is not final value
		surfaceGeo.evaluatePoint(u, v, p);

		if (!p.isDefined()) {
			return CoordsFloat3.UNDEFINED;
		}

		if (inCullingBox(p)) {
			return p;
		}

		return CoordsFloat3.UNDEFINED;
	}


	protected CoordsFloat3 evaluateNormal(double u, double v, CoordsFloat3 normal) {

		boolean defined;
		// normal is final value: use evaluatedNormal to compute
		if (normal == null || normal.isFinalUndefined()) {
			defined = surfaceGeo.evaluateNormal(u, v, evaluatedNormal);

			if (!defined) {
				return CoordsFloat3.UNDEFINED;
			}

			return evaluatedNormal.copyVector();
		}

		// normal is not final value
		defined = surfaceGeo.evaluateNormal(u, v, normal);

		if (!defined) {
			return CoordsFloat3.UNDEFINED;
		}

		return normal;

	}

	private class Corner {
		CoordsFloat3 p;
		CoordsFloat3 normal;
		double u, v;
		boolean isNotEnd;
		Corner a, l; // above, left
		
		public Corner(){
			
		}

		public Corner(double u, double v) {
			set(u, v);
		}

		public void set(double u, double v) {
			this.u = u;
			this.v = v;
			p = evaluatePoint(u, v, p);
			if (p.isFinalUndefined()) {
				normal = CoordsFloat3.UNDEFINED;
			} else {
				normal = evaluateNormal(u, v, normal);
			}
			isNotEnd = true;
			a = null;
			l = null;
		}

		public Corner(double u, double v, CoordsFloat3 p) {
			set(u, v, p);
		}
		
		public void set(double u, double v, CoordsFloat3 p) {
			this.u = u;
			this.v = v;
			this.p = p;
			normal = evaluateNormal(u, v, normal);
			
			isNotEnd = true;
			a = null;
			l = null;
		}
		
		/**
		 * draw this corner as part of "next to split" list
		 * @param surface surface plotter
		 */
		public void drawAsNextToSplit(PlotterSurface surface){
			
			if (this.p.isNotFinalUndefined()){
				if (a.p.isNotFinalUndefined()){
					if (l.p.isNotFinalUndefined()){
						drawTriangle(surface, this, a, l);
						if (l.a.p.isNotFinalUndefined()){
							drawTriangle(surface, l, a, l.a);
						}
					}else{
						if (l.a.p.isNotFinalUndefined()){
							drawTriangle(surface, this, a, l.a);
						}
					}
				}else{
					if (l.p.isNotFinalUndefined() && l.a.p.isNotFinalUndefined()){
						drawTriangle(surface, this, l.a, l);
					}
				}
			}else{ // this undefined
				if (l.p.isNotFinalUndefined() && a.p.isNotFinalUndefined() && l.a.p.isNotFinalUndefined()){
					drawTriangle(surface, l, a, l.a);
				}
			}
			
		}
		
		/**
		 * draw this corner as part of "still to split" list
		 * @param surface surface plotter
		 */
		public void drawAsStillToSplit(PlotterSurface surface){
			
			// create ring about corners
			int length;
			
			cornerForStillToSplit[0] = this;			
			cornerForStillToSplit[1] = this.l;

			
			if (this.l.a == null){ // a split occurred
				cornerForStillToSplit[2] = this.l.l;
				cornerForStillToSplit[3] = this.l.l.a;
				length = 4;
			}else{
				cornerForStillToSplit[2] = this.l.a;
				length = 3;
			}

			if (this.a.l == null){ // a split occurred
				cornerForStillToSplit[length] = this.a.a;
				length++;
				cornerForStillToSplit[length] = this.a;
				length++;				
			}else{
				cornerForStillToSplit[length] = this.a;
				length++;	
			}
			
			// check defined and create intermediate corners if needed
			Corner previous = cornerForStillToSplit[length - 1];
			int index = 0;
			for (int i = 0; i < length; i++){
				Corner current = cornerForStillToSplit[i];
				
				if (current.p.isNotFinalUndefined()){
					if (previous.p.isFinalUndefined()){
						// previous undefined -- current defined : create intermediate
						Corner c = new Corner();
						if (Kernel.isEqual(previous.u, current.u)){
							findV(current, previous, BOUNDARY_SPLIT, c);
						}else{
							findU(current, previous, BOUNDARY_SPLIT, c);
						}
						vertexForStillToSplit[index] = c.p;
						normalForStillToSplit[index] = c.normal;
						index++;
					}
					// add current for drawing
					vertexForStillToSplit[index] = current.p;
					normalForStillToSplit[index] = current.normal;
					index++;
				}else{
					if (previous.p.isNotFinalUndefined()){ 
						// previous defined -- current undefined : create intermediate
						Corner c = new Corner();
						if (Kernel.isEqual(previous.u, current.u)){
							findV(previous, current, BOUNDARY_SPLIT, c);
						}else{
							findU(previous, current, BOUNDARY_SPLIT, c);
						}
						vertexForStillToSplit[index] = c.p;
						normalForStillToSplit[index] = c.normal;
						index++;
					}
				}
				
				previous = current;
			}
			
			if (index < 3){
				App.debug("index = "+index);
				return;
			}
			
			
			CoordsFloat3 v0 = vertexForStillToSplit[0];
			CoordsFloat3 n0 = normalForStillToSplit[0];
			for (int i = 1; i < index - 1; i++){
				drawTriangle(surface, 
						v0, n0, 
						vertexForStillToSplit[i+1], normalForStillToSplit[i+1],
						vertexForStillToSplit[i], normalForStillToSplit[i]);
			}

			
			
		}

		public void split(boolean draw) {


			Corner left, above, subLeft, subAbove;

			if (l.a == null) {
				left = l.l;
				subLeft = l;
			} else {
				left = l;
				subLeft = null;
			}

			if (a.l == null) {
				above = a.a;
				subAbove = a;
			} else {
				above = a;
				subAbove = null;
			}

			if (p.isFinalUndefined()) {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// all undefined: nothing to draw /0/
							notDrawn++;
						} else {
							// l.a is defined /1/
							// find defined between l.a and a
							Corner n = newCorner(); 
							findU(left.a, above, BOUNDARY_SPLIT, n);
							// find defined between l.a and l
							Corner w = newCorner();  
							findV(left.a, left, BOUNDARY_SPLIT, w);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined() || w.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, n, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, n, w)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								n.l = left.a;
								above.l = n;
								// new neighbors
								w.a = left.a;
								left.a = w;

								// draw
								addToDrawList(w.a, n, w, w.a);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// a defined /1/
							// find defined between a and l.a
							Corner n = newCorner(); 
							findU(above, left.a, BOUNDARY_SPLIT, n);
							// find defined between a and this
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = newCorner();
								findV(above, this, BOUNDARY_SPLIT, e);
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined() || e.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, n, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, n, e)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subAbove == null) {
									this.a = e;
									e.a = above;
								}
								n.l = left.a;
								above.l = n;

								// drawing
								addToDrawList(left.a, n, e, above);
							}
						} else {
							// a and l.a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, left.a);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, left.a)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner(); 
									findV(above, this, BOUNDARY_SPLIT, e);
								}
								// find defined between l.a and left
								Corner w = newCorner(); 
								findV(left.a, left, BOUNDARY_SPLIT, w);

								if (!draw){
									//check distances
									double d = getDistanceNoLoop(above, e, w, left.a);
									if (Double.isInfinite(d)) { // d > maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check angle
										if (isAngleOKNoLoop(maxBend, above, e, w, left.a)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subAbove == null) {
										// new neighbors
										this.a = e;
										e.a = above;
									}
									// new neighbors
									w.a = left.a;
									left.a = w;

									// drawing
									addToDrawList(w.a, e, above, left.a, w);
								}
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// l defined /1/
							// find defined between l and this
							Corner s;
							if (subLeft != null) {
								s = subLeft;
							} else {
								s = newCorner();
								findU(left, this, BOUNDARY_SPLIT, s);
							}
							// find defined between l and l.a
							Corner w = newCorner();
							findV(left, left.a, BOUNDARY_SPLIT, w);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined() || w.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left, s, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left, s, w)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subLeft == null) {
									this.l = s;
									s.l = left;
								}
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, s, w, left);

							}
						} else {
							// l and l.a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(left, this, BOUNDARY_SPLIT, s);
								}
								// find defined between l.a and a
								Corner n = newCorner();
								findU(left.a, above, BOUNDARY_SPLIT, n);

								if (!draw){
									// check distances
									double d = getDistanceNoLoop(left.a, n, s, left);
									if (Double.isInfinite(d)) { // d > maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check angle
										if (isAngleOKNoLoop(maxBend, left.a, n, s, left)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subLeft == null) {
										// new neighbors
										this.l = s;
										s.l = left;
									}
									// new neighbors
									n.l = left.a;
									above.l = n;

									// drawing
									addToDrawList(left.a, s, n, left.a, left);
								}
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// l and a not undefined /2/diag/
							boolean split;
							if (draw) { // time to draw
								split = false;
							}else{ // check distance
								double d = getDistance(left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							}
						} else {
							// l, a and l.a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, left, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(left, this, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(above, this, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}

								// drawing
								addToDrawList(left.a, left, above, left.a);
							}
						}
					}
				}
			} else {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this defined /1/
							// find defined between this and l
							Corner s;
							if (subLeft != null) {
								s = subLeft;
							} else {
								s = newCorner();
								findU(this, left, BOUNDARY_SPLIT, s);
							}
							// find defined between this and a
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = newCorner();
								findV(this, above, BOUNDARY_SPLIT, e);
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined() || e.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, s, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, s, e)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subLeft == null) {
									this.l = s;
									s.l = left;
								}
								if (subAbove == null) {
									this.a = e;
									e.a = above;
								}

								// drawing
								addToDrawList(left.a, s, e, this);
							}
						} else {
							// this and l.a not undefined /2/diag/
							boolean split;
							if (draw) { // time to draw
								split = false;
							}else{ // check distance
								double d = getDistance(left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this and a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and l
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(this, left, BOUNDARY_SPLIT, s);
								}
								// find defined between a and l.a
								Corner n = newCorner();
								findU(above, left.a, BOUNDARY_SPLIT, n);

								if (!draw){
									// check distances
									double d = getDistanceNoLoop(this, s, n, above);
									if (Double.isInfinite(d)) { // d > maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check angle
										if (isAngleOKNoLoop(maxBend, this, s, n, above)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subLeft == null) {
										// new neighbors
										this.l = s;
										s.l = left;
									}
									// new neighbors
									n.l = left.a;
									above.l = n;

									// drawing
									addToDrawList(left.a, this, above, n, s);

								}
							}
						} else {
							// this, a and l.a defined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, left.a, this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and l
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(this, left, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between l.a and l
								Corner w = newCorner();
								findV(left.a, left, BOUNDARY_SPLIT, w);
								// new neighbors
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, above, left.a, this);
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this and l defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(this, above, BOUNDARY_SPLIT, e);
								}
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left, left.a, BOUNDARY_SPLIT, w);
								
								if (!draw){
									//check distances
									double d = getDistanceNoLoop(this, e, w, left);
									if (Double.isInfinite(d)) { // d > maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check angle
										if (isAngleOKNoLoop(maxBend, this, e, w, left)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subAbove == null) {
										// new neighbors
										this.a = e;
										e.a = above;
									}
									// new neighbors
									w.a = left.a;
									left.a = w;

									// drawing
									addToDrawList(w.a, this, e, w, left);
								}
							}
						} else {
							// this, l and l.a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left, left.a, this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l.a and a
								Corner n = newCorner();
								findU(left.a, above, BOUNDARY_SPLIT, n);
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(this, above, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}
								// new neighbors
								n.l = left.a;
								above.l = n;

								// drawing
								addToDrawList(left.a, left, left.a, this);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this, l and a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							}else{ // check distance
								double d = getDistance(this, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, left, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between a and l.a
								Corner n = newCorner();
								findU(above, left.a, BOUNDARY_SPLIT, n);
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left, left.a, BOUNDARY_SPLIT, w);
								// new neighbors
								n.l = left.a;
								above.l = n;
								// new neighbors
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, this, left, above);

							}
						} else {
							// this, l, a and l.a defined /4/
							if (draw) {
								// drawing
								addToDrawList(left.a, this, left, above, left.a);
							} else {
								// check distances
								double d = getDistance(this, left, above, left.a);
								if (Double.isInfinite(d) || (d > maxRWDistanceNoAngleCheck && !isAngleOK(maxBend, this, left, above, left.a))) {
									split(subLeft, left, subAbove, above);
								} else {
									// drawing
									addToDrawList(left.a, this, left, above, left.a);
								}
							}
						}
					}
				}
			}

		}

		private void split(Corner subLeft, Corner left, Corner subAbove, Corner above) {
			// new corners
			double um = (u + left.u) / 2;
			double vm = (v + above.v) / 2;
			if (subLeft != null) {
				um = subLeft.u;
			}
			if (subAbove != null) {
				vm = subAbove.v;
			}
			Corner e;
			if (subAbove != null) {
				e = subAbove;
			} else {
				e = newCorner(u, vm);
				// new neighbors
				this.a = e;
				e.a = above;
			}
			Corner s;
			if (subLeft != null) {
				s = subLeft;
			} else {
				s = newCorner(um, v);
				// new neighbors
				this.l = s;
				s.l = left;
			}
			Corner m = newCorner(um, vm);
			s.a = m;
			e.l = m;
			Corner n = newCorner(um, above.v);
			n.l = above.l;
			above.l = n;
			m.a = n;
			Corner w = newCorner(left.u, vm);
			w.a = left.a;
			left.a = w;
			m.l = w;
			// next split
			addToNextSplit(this);
			addToNextSplit(s);
			addToNextSplit(e);
			addToNextSplit(m);
			
			loopSplitIndex += 4;
		}

		private void addToDrawList(Corner end, Corner... corners) {

			CornerAndCenter cc = drawList[drawListIndex];
			if (cc == null) {
				cc = new CornerAndCenter(this);
				drawList[drawListIndex] = cc;
			} else {
				cc.setCorner(this);
			}
			drawListIndex++;
			loopSplitIndex++;

			setBarycenter(cc.getCenter(), cc.getCenterNormal(), corners);

			end.isNotEnd = false;
		}

		private void findU(Corner defined, Corner undefined, int depth, Corner corner) {
			findU(defined.p, defined.u, defined.u, undefined.u,
					defined.v, depth, corner, true);
		}

		private void findU(CoordsFloat3 lastDefined, double uLastDef, double uDef,
				double uUndef, double vRow, int depth, Corner corner, boolean lastDefinedIsFirst) {

			double uNew = (uDef + uUndef) / 2;
			CoordsFloat3 coords = evaluatePoint(uNew, vRow);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					if (lastDefinedIsFirst){
						corner.set(uLastDef, vRow, lastDefined.copyVector());
					}else{
						corner.set(uLastDef, vRow, lastDefined);
					}
				}else{
					corner.set(uNew, vRow, coords);
				}
			}else{
				if (coords.isFinalUndefined()) {
					findU(lastDefined, uLastDef, uDef, uNew, vRow, depth - 1, corner, lastDefinedIsFirst);
				}else{
					findU(coords, uNew, uNew, uUndef, vRow, depth - 1, corner, false);
				}
			}

		}

		private void findV(Corner defined, Corner undefined, int depth, Corner corner) {
			findV(defined.p, defined.v, defined.v, undefined.v,
					defined.u, depth, corner, true);
		}

		private void findV(CoordsFloat3 lastDefined, double vLastDef, double vDef,
				double vUndef, double uRow, int depth, Corner corner, boolean lastDefinedIsFirst) {

			double vNew = (vDef + vUndef) / 2;
			CoordsFloat3 coords = evaluatePoint(uRow, vNew);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					if (lastDefinedIsFirst){
						corner.set(uRow, vLastDef, lastDefined.copyVector());
					}else{
						corner.set(uRow, vLastDef, lastDefined);
					}
				}else{
					corner.set(uRow, vNew, coords);
				}
			}else{
				if (coords.isFinalUndefined()) {
					findV(lastDefined, vLastDef, vDef, vNew, uRow, depth - 1, corner, lastDefinedIsFirst);
				}else{
					findV(coords, vNew, vNew, vUndef, uRow, depth - 1, corner, false);
				}
			}

		}




	}

	/**
	 * set center as barycenter for points
	 * 
	 * @param center
	 *            center
	 * @param normal
	 *            normal for center point
	 * @param c
	 *            corners
	 * 
	 */
	static protected void setBarycenter(CoordsFloat3 center, CoordsFloat3 normal, Corner... c) {
		setBarycenter(center, normal, c.length, c);
	}
	
	
	/**
	 * set center as barycenter for points
	 * 
	 * @param center
	 *            center
	 * @param normal
	 *            normal for center point
	 * @param c
	 *            corners
	 * 
	 */
	static protected void setBarycenter(CoordsFloat3 center, CoordsFloat3 normal, int length, Corner... c) {
		float f = 1f / length;
		center.set(0f, 0f, 0f);
		normal.set(0f, 0f, 0f);
		for (int j = 0; j < length; j++) {
			center.addInside(c[j].p);
			normal.addInside(c[j].normal);
		}
		center.mulInside(f);
		normal.mulInside(f);

		// if (!center.isDefined()) {
		// App.printStacktrace("!center.isDefined()");
		// }
		// if (!normal.isDefined()) {
		// App.printStacktrace("!normal.isDefined()");
		// }

	}
	
	/**
	 * used to draw "still to split" corners
	 */
	protected CornerAndCenter ccForStillToSplit;
	
	protected Corner[] cornerForStillToSplit;
	
	protected CoordsFloat3[] vertexForStillToSplit, normalForStillToSplit;

	/**
	 * max distance in real world from view
	 */
	private double maxRWPixelDistance;
	/**
	 * max distance in real world for splitting
	 */
	private double maxRWDistance;
	/**
	 * max distance in real world under which we don't check angles
	 */
	protected double maxRWDistanceNoAngleCheck;
	protected double maxBend;

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @return distance between c1 and c2, or POSITIVE_INFINITY if distance is
	 *         more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2) {

		double ret = 0;
		
		double d = Math.abs(c1.p.x - c2.p.x);
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		d = Math.abs(c1.p.y - c2.p.y);
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		d = Math.abs(c1.p.z - c2.p.z);
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c4 / c4-c1, or
	 *         POSITIVE_INFINITY if distance is more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2, Corner c3, Corner c4) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c4);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c4, c1);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c4, or POSITIVE_INFINITY
	 *         if distance is more than maxRWDistance
	 */
	protected double getDistanceNoLoop(Corner c1, Corner c2, Corner c3, Corner c4) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c4);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c1, or POSITIVE_INFINITY
	 *         if distance is more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2, Corner c3) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c1);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * Returns whether the angle between the vectors (vx, vy) and (wx, wy) is
	 * smaller than MAX_BEND, where MAX_BEND = tan(MAX_ANGLE).
	 */
	private static boolean isAngleOK(CoordsFloat3 v, CoordsFloat3 w, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = v.x * w.x + v.y * w.y + v.z * w.z;

		if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		}

		// angle < 90 degrees
		// small angle: |det(v, w)| < MAX_BEND * (v . w)
		double d1 = v.x * w.y - v.y * w.x;
		double d2 = v.y * w.z - v.z * w.y;
		double d3 = v.z * w.x - v.x * w.z;
		double det = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
		return det < bend * innerProduct;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2) {

		if (!isAngleOK(c1.normal, c2.normal, bend)) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2, Corner c3) {

		if (!isAngleOK(c1.normal, c2.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal, c3.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal, c1.normal, bend)) {
			return false;
		}

		return true;
	}


	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4 and c4-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2, Corner c3, Corner c4) {

		if (!isAngleOK(c1.normal, c2.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal, c3.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal, c4.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c4.normal, c1.normal, bend)) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4
	 */
	protected static boolean isAngleOKNoLoop(double bend, Corner c1, Corner c2, Corner c3, Corner c4) {

		if (!isAngleOK(c1.normal, c2.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal, c3.normal, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal, c4.normal, bend)) {
			return false;
		}

		return true;
	}

	private class CornerAndCenter {
		private Corner corner;
		private CoordsFloat3 center;
		private CoordsFloat3 centerNormal;

		public CornerAndCenter() {
			center = new CoordsFloat3();
			centerNormal = new CoordsFloat3();
		}
		
		public CornerAndCenter(Corner corner) {
			this();
			setCorner(corner);
		}

		/**
		 * set the corner
		 * 
		 * @param corner
		 *            corner
		 */
		public void setCorner(Corner corner) {
			this.corner = corner;
		}

		/**
		 * 
		 * @return corner
		 */
		public Corner getCorner() {
			return corner;
		}

		/**
		 * 
		 * @return center
		 */
		public CoordsFloat3 getCenter() {
			return center;
		}

		/**
		 * 
		 * @return center normal
		 */
		public CoordsFloat3 getCenterNormal() {
			return centerNormal;
		}

		public void drawDebug(PlotterSurface surface) {

			surface.startTrianglesWireFrame();
			draw(surface);
			surface.endGeometryDirect();

			surface.startTrianglesWireFrameSurface();
			draw(surface);
			surface.endGeometryDirect();

		}

		public void draw(PlotterSurface surface) {

			Corner current, sw, ne;

			Corner p1, p2;

			// go left
			current = corner;
			// get first defined point on south (if exists)
			Corner sw1 = current;
			Corner sw2 = sw1;
			// draw south
			p1 = sw1;
			do {
				p2 = current.l;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						if (sw1.p.isFinalUndefined()) {
							sw1 = p1;
						}
						drawTriangle(surface, center, centerNormal, p2, p1);
					}
					p1 = p2;
					sw2 = p2;
				}
				current = current.l;
			} while (current.a == null);

			sw = current;

			// go above
			current = corner;
			// get first defined point on east (if exists)
			Corner ne1 = current;
			Corner ne2 = ne1;
			// draw east
			p1 = ne1;
			do {
				p2 = current.a;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, center, centerNormal, p1, p2);
						if (ne1.p.isFinalUndefined()) {
							ne1 = p1;
						}
					}
					p1 = p2;
					ne2 = p2;
				}
				current = current.a;
			} while (current.l == null);
			ne = current;

			// west side
			current = sw;
			p1 = sw2;
			if (sw1.p.isFinalUndefined()) {
				sw1 = p1;
			}
			do {
				p2 = current.a;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, center, centerNormal, p2, p1);
						if (sw1.p.isFinalUndefined()) {
							sw1 = p1;
						}
					}
					p1 = p2;
					sw2 = p2;
				}
				current = current.a;
			} while (current.isNotEnd);

			// north side
			current = ne;
			p1 = ne2;
			if (ne1.p.isFinalUndefined()) {
				ne1 = p1;
			}
			do {
				p2 = current.l;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, center, centerNormal, p1, p2);
						if (ne1.p.isFinalUndefined()) {
							ne1 = p1;
						}
					}
					p1 = p2;
					ne2 = p2;
				}
				current = current.l;
			} while (current.isNotEnd);

			// closure triangles if needed
			if (sw1 != ne1) {
				drawTriangleCheckCorners(surface, center, centerNormal, sw1, ne1);
			}
			if (sw2 != ne2) {
				drawTriangleCheckCorners(surface, center, centerNormal, ne2, sw2);
			}
			if (ne1.p.isFinalUndefined() && ne2.p.isFinalUndefined()) {
				drawTriangleCheckCorners(surface, center, centerNormal, sw2, sw1);
			}
			if (sw1.p.isFinalUndefined() && sw2.p.isFinalUndefined()) {
				drawTriangleCheckCorners(surface, center, centerNormal, ne1, ne2);
			}
		}


	}

	/**
	 * draw triangle with surface plotter
	 * 
	 * @param surface
	 *            surface plotter
	 * @param p0
	 *            first point
	 * @param n0
	 *            first point normal
	 * 
	 * @param c1
	 *            second point
	 * @param c2
	 *            third point
	 */
	static final protected void drawTriangle(PlotterSurface surface, CoordsFloat3 p0, CoordsFloat3 n0, Corner c1, Corner c2) {

		surface.normalDirect(n0);
		surface.vertexDirect(p0);
		surface.normalDirect(c2.normal);
		surface.vertexDirect(c2.p);
		surface.normalDirect(c1.normal);
		surface.vertexDirect(c1.p);

	}
	
	/**
	 * draw triangle with surface plotter
	 * 
	 * @param surface
	 * @param p0
	 * @param n0
	 * @param p1
	 * @param n1
	 * @param p2
	 * @param n2
	 */
	static final protected void drawTriangle(PlotterSurface surface, CoordsFloat3 p0, CoordsFloat3 n0, CoordsFloat3 p1, CoordsFloat3 n1, CoordsFloat3 p2, CoordsFloat3 n2) {

		surface.normalDirect(n0);
		surface.vertexDirect(p0);
		surface.normalDirect(n2);
		surface.vertexDirect(p2);
		surface.normalDirect(n1);
		surface.vertexDirect(p1);

	}

	
	/**
	 * 
	 * @param surface surface plotter
	 * @param c0 first point
	 * @param c1
	 *            second point
	 * @param c2
	 *            third point
	 */
	static final protected void drawTriangle(PlotterSurface surface, Corner c0, Corner c1, Corner c2) {
		
		drawTriangle(surface, c0.p, c0.normal, c1, c2);

	}

	/**
	 * draw triangle with surface plotter, check if second and third points are
	 * defined
	 * 
	 * @param surface
	 *            surface plotter
	 * @param p0
	 *            first point
	 * @param n0
	 *            first point normal
	 * 
	 * @param c1
	 *            second point
	 * @param c2
	 *            third point
	 */
	static final protected void drawTriangleCheckCorners(PlotterSurface surface, CoordsFloat3 p0, CoordsFloat3 n0, Corner c1, Corner c2) {
		if (c1.p.isFinalUndefined()) {
			return;
		}
		if (c2.p.isFinalUndefined()) {
			return;
		}

		drawTriangle(surface, p0, n0, c1, c2);
	}


	/**
	 * add the corner to next split array
	 * 
	 * @param corner
	 *            corner
	 */
	protected void addToNextSplit(Corner corner) {
		nextSplit[nextSplitIndex] = corner;
		nextSplitIndex++;
	}

	/**
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @return new corner calculated for parameters u, v
	 */
	protected Corner newCorner(double u, double v) {
		Corner c = cornerList[cornerListIndex];
		if (c == null) {
			c = new Corner(u, v);
			cornerList[cornerListIndex] = c;
		} else {
			c.set(u, v);
		}
		cornerListIndex++;
		return c;
	}
	
	
	/**
	 * @return new corner
	 */
	protected Corner newCorner() {
		Corner c = cornerList[cornerListIndex];
		if (c == null) {
			c = new Corner();
			cornerList[cornerListIndex] = c;
		} 
		cornerListIndex++;
		return c;
		
//		return new Corner();
	}

	
	
//	private static abstract class PlotterOrCounter{
//		
//		
//		/**
//		 * draw triangle with surface plotter
//		 * 
//		 * @param p0
//		 *            first point
//		 * @param n0
//		 *            first point normal
//		 * 
//		 * @param c1
//		 *            second point
//		 * @param c2
//		 *            third point
//		 */
//		abstract public void drawTriangle(Coords p0, Coords n0, Corner c1, Corner c2);
//		
//		/**
//		 * 
//		 * @param c0 first point
//		 * @param c1
//		 *            second point
//		 * @param c2
//		 *            third point
//		 */
//		final public void drawTriangle(Corner c0, Corner c1, Corner c2) {
//			
//			drawTriangle(c0.p, c0.normal, c1, c2);
//
//		}
//	}
//	
//	private static abstract class Plotter extends PlotterOrCounter{
//		
//		
//		private PlotterSurface surface;
//		
//		final public void setSurface(PlotterSurface surface){
//			this.surface = surface;
//		}
//
//		@Override
//		final public void drawTriangle(Coords p0, Coords n0, Corner c1, Corner c2) {
//
//			surface.normalDirect(n0);
//			surface.vertexDirect(p0);
//			surface.normalDirect(c2.normal);
//			surface.vertexDirect(c2.p);
//			surface.normalDirect(c1.normal);
//			surface.vertexDirect(c1.p);
//
//		}
//	}
//	
//	private static abstract class Counter extends PlotterOrCounter{
//		
//		
//
//		@Override
//		final public void drawTriangle(Coords p0, Coords n0, Corner c1, Corner c2) {
//
//
//
//		}
//		
//	
//	}
	
}
