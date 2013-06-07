package geogebra3D.euclidian3D.plots;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.CurveEvaluable3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.PlotterBrush;

/**
 * @author Andr� Eriksson
 * Tree representing a parametric curve
 */
public class CurveTree {
	
	// LEVEL OF DETAIL PARAMETERS
	
	/**a threshold for judging when a singularity has been passed.
	 * the cosine of the minimum angle for a singularity.
	 * used in {@link PlotterBrush} */
	public static final double discontinuityThreshold = -0.95;
	
	/** the difference in the paramater value used for tangent estimations
	 *  used in {@link CurveTreeNode} */
	public static final double deltaParam = 1e-8;
	
	/** amount of levels intially calculated*/
	public static final int initialLevels = 8;
	
	/**minimum amount of levels that is drawn*/
	public static final int forcedLevels = 6;
	
	/** threshold used for testing if two segments form a
	 *  large enough angle. the cosine of the minimum angle allowed */
	public static final double pCosThreshold = 0.995;
	
	/** threshold used for testing if the tangents of two consecutive pointss
	 *  are close enough. the cosine of the maximum angle allowed */
	public static final double tCosThreshold = 0.995;
	
	/** the minimum paramater distance allowed between two consecutive points */
	public static final double minParamDist = 1e-5;
	
	/** a factor for determining the level of detail depending on the camera zoom */
	public static final double distanceFactor = 10.0;
	
	// PRIVATE VARIABLES
	
	private CurveTreeNode root;
	private CurveTreeNode start, end;
	private CurveEvaluable3D curve;
	private EuclidianView3D view;
	private double radius =0;
	
	// FUNCTIONS
	
	/**
	 * @return the curve object for the tree
	 */
	public CurveEvaluable3D getCurve() { 
		return curve; 
	}
	
	/**
	 * @param curve	the curve
	 * @param view
	 */
	public CurveTree(CurveEvaluable3D curve, EuclidianView3D view) {
		double maxParam = curve.getMaxParameter();
		double minParam = curve.getMinParameter();
		double diff = maxParam-minParam;
		this.view=view;
		
		//create root
		this.curve = curve;
		double t = (minParam+maxParam)/2;
		root = new CurveTreeNode(curve.evaluateCurve3D(t), t, diff, 1, curve, null);
		
		//create start and end points
		start = new CurveTreeNode(curve.evaluateCurve3D(minParam), minParam, diff, 0, curve, null);
		end	  = new CurveTreeNode(curve.evaluateCurve3D(maxParam), maxParam, diff, 0, curve, null);
		
		addPoints(minParam,t,initialLevels-1);
		addPoints(t,maxParam,initialLevels-1);
	}
	
	/**
	 * @param r
	 * 			the radius of the viewing sphere
	 */
	public void setRadius(double r){
		this.radius = r;
	}
	
	/** Starts refining the tree
	 * 
	 * @param brush 
	 * 				a reference to the calling brush
	 */
	public void beginRefinement(PlotterBrush brush){
		refine(brush,start,root,end,1);
	}
	
	
	/**
	 * If the start point is well-defined and visible, it is drawn using
	 * brush.addPointToCurve().
	 * 
	 * @param brush 
	 * 				a reference to the calling brush
	 */
	public void drawStartPointIfVisible(PlotterBrush brush){
		Coords pos = start.getPos();
		if(pos.isDefined() && pos.isFinite() 
				&& pointVisible(pos)){
			brush.addPointToCurve3D(pos,start.getTangent());
			brush.setCurvePos(0);
		}
	}
	
	/**
	 * If the end point is well-defined and visible, it is drawn using
	 * brush.addPointToCurve().
	 * 
	 * @param brush 
	 * 				a reference to the calling brush
	 */
	public void drawEndPointIfVisible(PlotterBrush brush){
		Coords pos = end.getPos();
		if(pos.isDefined() && pos.isFinite() 
				&& pointVisible(pos))
			brush.addPointToCurve3D(pos,end.getTangent());
	}
	
	/**
	 * Function that recursively draws a curve segment depending on curvature,
	 * zoom level, and tangent information. Draws using brush.addPointToCurve().
	 * 
	 * @param brush 
	 * 				a reference to the calling brush
	 * @param n1 
	 * 				the left point of the segment
	 * @param n2 
	 * 				the center point of the segment
	 * @param n3 
	 * 				the end point of the segment
	 * @param level
	 * 				the current level of recursion
	 */
	public void refine(PlotterBrush brush, CurveTreeNode n1, CurveTreeNode n2, 
						 CurveTreeNode n3, int level){
		Coords p1 = n1.getPos();
		Coords p2 = n2.getPos();
		Coords p3 = n3.getPos();
		if(level <= forcedLevels || angleTooSharp(p1, p2, p3)){
			//if the left segment is visible and passes the distance test, refine it
			if(segmentVisible(p1,p2))
				if(distanceLargeEnough(n1, n2))
					refine(brush,n1,n2.getLeftChild(),n2,level+1);
			
			//draw the center point if it is defined
			if(p2.isDefined() && p2.isFinite())
				brush.addPointToCurve3D(p2,n2.getTangent());
			
			//if the right segment is visible and passes the distance test, refine it
			if(segmentVisible(p2,p3))
				if(distanceLargeEnough(n2, n3))
					refine(brush,n2,n2.getRightChild(),n3,level+1);
			
		} else {
			//if the left segment is visible, and the tangent and distance tests are passed
			//for the same segment, refine it 
			if(segmentVisible(p1,p2))
				if(tangentTooDifferent(n1,n2))
					if(distanceLargeEnough(n1, n2))
						refine(brush,n1,n2.getLeftChild(),n2,level+1);
			
			//draw the center point if it is defined
			if(p2.isDefined() && p2.isFinite())
				brush.addPointToCurve3D(p2,n2.getTangent());
			
			//if the right segment is visible, and the tangent and distance tests are passed
			//for the same segment, refine it 
			if(segmentVisible(p2,p3))
				if(tangentTooDifferent(n2,n3))
					if(distanceLargeEnough(n2, n3))
						refine(brush,n2,n2.getRightChild(),n3,level+1);
		}
	}
	
	/**
	 * Function that recursively inserts points between min and max.
	 * Only to be used in the constructor.
	 * 
	 * @param min 
	 * 				minimum parameter value
	 * @param max 
	 * 				maximum parameter value
	 * @param lev 
	 * 				the depth to which should continue
	 */
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		insert(curve.evaluateCurve3D(t), t);
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}
	
	/**
	 * Tests if a segment is partly or wholly within the viewing volume
	 * 
	 * @param n1 
	 * 				start of segment
	 * @param n2 
	 * 				end of segment
	 */
	private boolean segmentVisible(Coords n1, Coords n2) {
		if(n1.norm() < radius)
			return true;
		if(n2.norm() < radius)
			return true;
		if(!n2.isDefined() || !n2.isDefined())
			return true;
		double x1,x2,y1,y2,z1,z2,dx,dy,dz,u;
		
		x1=n1.getX();
		x2=n2.getX();
		y1=n1.getY();
		y2=n2.getY();
		z1=n1.getZ();
		z2=n2.getZ();
		dx=x2-x1;
		dy=y2-y1;
		dz=z2-z1;
		u=-(x1*dx+y1*dy+z1*dz)/(dx*dx+dy*dy+dz*dz);
		if((x1+u*dx)*(x1+u*dx)+(y1+u*dy)*(y1+u*dy)+(z1+u*dz)*(z1+u*dz)<radius*radius)
			return true;
		return false;
	}
	
	/**
	 * Tests if the given point is visible.
	 * Currently just tests if the point is within the viewing radius.
	 * 
	 * @param pos
	 * 				the point tested
	 * @return
	 */
	private boolean pointVisible(Coords pos){
		return pos.norm()<radius;
	}
	

	
	/** Tests if the segments defined by n1,n2,n3 are nearly in C1.
	 * 
	 * @param t1 
	 * 			the "leftmost" node
	 * @param n2 
	 * 			the "middle" node
	 * @param n3 
	 * 			the "rightmost" node
	 * @return false if the values are nearly continuous, otherwise true
	 */
	private boolean angleTooSharp(Coords p1, Coords p2, Coords p3){
		double x1 = p2.getX()-p1.getX(); double x2 = p3.getX()-p2.getX();
		double y1 = p2.getY()-p1.getY(); double y2 = p3.getY()-p2.getY();
		double z1 = p2.getZ()-p1.getZ(); double z2 = p3.getZ()-p2.getZ();
		double pCosAng = (x1*x2+y1*y2+z1*z2)/Math.sqrt((x1*x1+y1*y1+z1*z1)*(x2*x2+y2*y2+z2*z2));
		
		if( pCosAng < pCosThreshold || Double.isNaN(pCosAng))
			return true;
		return false;
	}
	
	/**
	 * Tests if a segment between two points should be refined based on how close to
	 * continuous their tangents are.
	 * 
	 * @param n1
	 * 			the first node of the segment
	 * @param n2
	 * 			the second node of the segment
	 * @return true if the cosine of the angle between the tangents is < than tCosThreshold
	 */
	private boolean tangentTooDifferent(CurveTreeNode n1, CurveTreeNode n2) {
		double tCosAng = n2.getTangent().dotproduct(n1.getTangent());
		if(tCosAng < tCosThreshold)
			return true;
		return false;
	}
	
	/**
	 * Tests if two nodes are far enough, both in their parameter values and their
	 * spatial values, to be refined.
	 * 
	 * @param n1
	 * 			the first node of the segment
	 * @param n2
	 * 			the second node of the segment
	 * @return
	 */
	private boolean distanceLargeEnough(CurveTreeNode n1, CurveTreeNode n2){
		//test parameter distance
		if(Math.abs(n1.getParam()-n2.getParam())<minParamDist)
			return false;
		
		Coords p1 = n1.getPos();
		Coords p2 = n2.getPos();
		double scale = view.getScale();
		double diff = p1.sub(p2).norm();
		if(diff>distanceFactor/scale)
			return true;
		if(Double.isNaN(diff) || Double.isInfinite(diff))
			return true;
		return false;
	}
	
	/** 
	 * inserts a node into the tree
	 * 
	 * @param pos 	
	 * 				node position
	 * @param param 
	 * 				node parameter value
	 */
	private void insert(Coords pos, double param) {root.insert(pos,param);}
}


/**
 * Class representing a node in CurveTree
 * 
 * @author Andr� Eriksson
 * @see CurveTree
 */
class CurveTreeNode{

	private Coords pos;
	private Coords tangent;
	private double param;
	
	private final int level;
	private final double diff;
	
	private CurveTreeNode[] children;
	private CurveEvaluable3D curve;

	/**
	 * @param pos	
	 * 				spatial position
	 * @param param 
	 * 				parameter value
	 * @param diff  
	 * 				the difference between the minimum and maximum parameter values
	 * @param level	
	 * 				the level of the tree
	 * @param curve 
	 * 				a reference to the curve
	 * @param parent 
	 */
	CurveTreeNode(Coords pos, double param, double diff, int level, 
			CurveEvaluable3D curve, CurveTreeNode parent){
		this.pos = pos.copyVector();
		this.param = param;
		this.children = new CurveTreeNode[2];
		this.level = level;
		this.diff = diff;
		this.curve=curve;
		
		approxTangent();
	}
	
	/**
	 * @return the spatial position of the node
	 */
	public Coords getPos(){return pos;}
	
	/**
	 * @return the parameter value at the point
	 */
	public double getParam(){return param;}
	
	/**
	 * @return the node tangent
	 */
	public Coords getTangent(){return tangent;}
	
	@Override
	public String toString() {
		return "CurveTreeNode [param=" + param + ", pos=" + pos + "]";
	}

	/**
	 * @return the node's left child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getLeftChild(){
		if(children[0]==null){
			double childParam = param-diff/Math.pow(2,level+1);
			Coords childPos = curve.evaluateCurve3D(childParam);
			children[0] = new CurveTreeNode(childPos,childParam, diff, level+1, curve, this);
		}
		return children[0];
	}
	
	/**
	 * @return the node's right child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getRightChild(){
		if(children[1]==null){
			double childParam = param+diff/Math.pow(2,level+1);
			Coords childPos = curve.evaluateCurve3D(childParam);
			children[1] = new CurveTreeNode(childPos,childParam, diff, level+1, curve, this);
		}
		return children[1];
	}
	
	/** Recursive function that inserts a node into the tree
	 * @param pos 	
	 * 				the node position
	 * @param param 
	 * 				the node parameter value
	 */
	public void insert(Coords pos, double param){
		int i = 0;
		
		if(param>this.param)
			i=1;
		
		if(children[i]==null)
			children[i] = new CurveTreeNode(pos, param, diff, this.level+1, curve, this);
		else 
			children[i].insert(pos,param);
	}
	
	/** 
	 * Approximates the tangent by a simple forward difference quotient.
	 * Should only be called in the constructor.
	 */
	private void approxTangent(){
		Coords d = curve.evaluateCurve3D(param+CurveTree.deltaParam);
		tangent = d.sub(pos).normalized();
	}
}