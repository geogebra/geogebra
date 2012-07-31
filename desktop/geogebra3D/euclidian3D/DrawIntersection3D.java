package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.Renderer;

import java.nio.FloatBuffer;

public class DrawIntersection3D extends Drawable3DCurves{
	
	/** A triangle list for the intersection */
	CurveTriList tris;
	
	/**
	 * @param a_view3d
	 * @param object1 the first object to intersect
	 * @param object2 the second object to intersect
	 * @throws Exception if there's an error when computing the intersection
	 */
	public DrawIntersection3D(EuclidianView3D a_view3d, SurfaceTriList object1, SurfaceTriList object2) throws Exception {
		super(a_view3d);
		
		init(object1, object2);
	}
	
	/**
	 * 
	 * @param object1
	 * @param object2
	 * @throws Exception
	 */
	private void init(SurfaceTriList object1, SurfaceTriList object2) throws Exception{
		
		//bruteforce for now
		FloatBuffer ver1 = object1.getTriangleBuffer();
		FloatBuffer ver2 = object2.getTriangleBuffer();
		
		int cnt1 = object1.getChunkAmt();
		int cnt2 = object2.getChunkAmt();
		
		float[] t1 = new float[9]; float[] t2 = new float[9];
		ver1.rewind(); ver2.rewind();
		
		Boolean intersect = false;
		Boolean coplanar = false;
		float[] segment;
		for(int i = 0; i < cnt1; i++) {
			ver1.get(t1);
			for(int j = 0; j < cnt2; j++){
				ver2.get(t2);
				segment = Collision.triTriIntersect(t1, t2, coplanar, intersect);
				
				if(coplanar){ // TODO: handle coplanearity
					throw new Exception("coplanearity not yet supported");
				} else if(intersect) {
					tris.add(segment, true);
				}
			}
		}
	}

	@Override
	protected void updateForView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean updateForItSelf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		
	}

	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
}
