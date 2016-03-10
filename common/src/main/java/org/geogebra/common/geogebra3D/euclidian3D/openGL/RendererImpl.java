package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;

public interface RendererImpl extends RendererShadersInterface{

	public abstract void setClipPlanes(double[][] minMax);

	public abstract void setColor(float r, float g, float b, float a);

	public abstract void initMatrix();

	public abstract void initMatrixForFaceToScreen();

	public abstract void resetMatrix();

	public abstract void glLoadName(int loop);

	public abstract void updateOrthoValues();

	public abstract void enableTextures();

	public abstract void disableTextures();

	public abstract void setLineWidth(int width);

	public abstract void enableFading();

	public abstract void enableDash();

	public abstract void setDashTexture(int index);

	public abstract void setLabelOrigin(Coords origin);

	public abstract void enableLighting();

	public abstract void disableLighting();

	public abstract void initLighting();

	public abstract boolean useShaders();

	public abstract void setMatrixView();

	public abstract void unsetMatrixView();

	public abstract void pushSceneMatrix();

	public abstract void setLightPosition(float[] values);

	public abstract void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1);

	public abstract void setLight(int light);

	public abstract void setColorMaterial();

	public abstract void setLightModel();

	public abstract void setAlphaFunc();

	public abstract void setView();

	public abstract void setStencilLines();

	public abstract void viewOrtho();

	public abstract void viewPersp();

	public abstract void viewGlasses();

	public abstract void viewOblique();

	public abstract Manager createManager();

	public abstract float[] getLightPosition();

	public abstract void drawSurfacesOutline();

	public abstract void enableClipPlanes();

	public abstract void disableClipPlanes();

	public abstract void useShaderProgram();

	public abstract void draw();

	public abstract void dispose();

	public abstract void updatePerspValues();

	public abstract void updateGlassesValues();

	public abstract void updateProjectionObliqueValues();

	public abstract void enableTexturesForText();

	public abstract void initRenderingValues();

	public abstract void drawFaceToScreenAbove();

	public abstract void drawFaceToScreenBelow();

	public abstract void enableLightingOnInit();

	public abstract void initCulling();

	public abstract void drawTranspNotCurved();

	public abstract void disableCulling();

	public abstract void setCullFaceFront();

	public abstract void setCullFaceBack();

	public void initShaders();

	public abstract void disableShine();

	public abstract void enableShine();

	public void setBufferLeft();

	public void setBufferRight();

	public void clearColorBuffer();

	public void clearDepthBuffer();

	public void setStencilFunc(int value);

	public void enableCulling();

	public void disableBlending();

	public void enableBlending();

	public void enableMultisample();

	public void disableMultisample();

	public void enableAlphaTest();

	public void disableAlphaTest();

	public void enableDepthMask();

	public void disableDepthMask();

	public void enableDepthTest();

	public void disableDepthTest();

	public void setColorMask(boolean r, boolean g, boolean b, boolean a);

	public void setClearColor(float r, float g, float b, float a);

	public void setPolygonOffset(float factor, float units);

	public void enableTextures2D();

	public void disableTextures2D();

	public void genTextures2D(int number, int[] index);

	public void bindTexture(int index);

}