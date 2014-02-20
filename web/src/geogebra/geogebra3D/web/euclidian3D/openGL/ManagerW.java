package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;

public class ManagerW extends Manager{
	
	private RendererW renderer;

	public ManagerW(RendererW renderer, EuclidianView3D view3d) {
	    super(renderer, view3d);
    }

	@Override
    protected void setRenderer(Renderer renderer) {
	   this.renderer = (RendererW) renderer;	    
    }

	@Override
    protected Renderer getRenderer() {
	    return renderer;
    }

	@Override
    public int startNewList() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void endList() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void startGeometry(Type type) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void endGeometry() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public int startPolygons() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void drawPolygon(Coords n, Coords[] v) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setDummyTexture() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void endPolygons() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void draw(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void drawInObjFormat(GeoElement geo, int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void remove(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void vertex(float x, float y, float z) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void vertexInt(int x, int y, int z) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void vertices(double[] vertices) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void normal(float x, float y, float z) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void texture(float x, float y) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void color(float r, float g, float b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void color(float r, float g, float b, float a) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void lineWidth(float width) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void pointSize(float size) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void rectangle(int x, int y, int z, int width, int height) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void rectangleBounds(int x, int y, int z, int width, int height) {
	    // TODO Auto-generated method stub
	    
    }

}
