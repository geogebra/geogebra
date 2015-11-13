package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
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

	public boolean drawQuadric(int type);

	public void initShaders();

}