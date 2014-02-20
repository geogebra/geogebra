package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.awt.GColor;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.geogebra3D.web.euclidian3D.EuclidianViewW3D;

/**
 * class for web openGL renderer
 * @author mathieu
 *
 */
public class RendererW extends Renderer{

	/**
	 * constructor
	 * @param view 3D view
	 */
	public RendererW(EuclidianViewW3D view) {
	    super(view);

    }
	

	@Override
    protected Manager createManager() {
	    return new ManagerW(this, view3D);
    }
	
	
	

	@Override
    public void display() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableCulling() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableCulling() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setCullFaceFront() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setCullFaceBack() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableBlending() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableBlending() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLight(int light) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableTextures() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableTextures() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableMultisample() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableMultisample() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableAlphaTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableAlphaTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableLighting() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableLighting() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void enableClipPlane(int n) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void disableClipPlane(int n) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setClipPlane(int n, double[] equation) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setMatrixView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void unsetMatrixView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableDepthMask() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableDepthMask() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableDepthTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableDepthTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setLineWidth(float width) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(Coords color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(GColor color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setLayer(float l) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void initMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void resetMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void drawMouseCursor() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setGLForPicking() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void pushSceneMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void storePickingInfos(Hits3D hits3d, int pointAndCurvesLoop,
            int labelLoop) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void doPick() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void pickIntersectionCurves() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void glLoadName(int loop) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightPosition(int light, float[] values) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
            float ambiant1, float diffuse1) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setClearColor(float r, float g, float b, float a) {
	    // TODO Auto-generated method stub
	    
    }


	@Override
    protected void setColorMaterial() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightModel() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setAlphaFunc() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void resumeAnimator() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void disableStencilLines() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setStencilLines() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewOrtho() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewPersp() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewGlasses() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewOblique() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableTextures2D() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableTextures2D() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void genTextures2D(int number, int[] index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void bindTexture(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void removeTexture(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setTextureLinear() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setTextureNearest() {
	    // TODO Auto-generated method stub
	    
    }

}
