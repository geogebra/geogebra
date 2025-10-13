package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.settings.EuclidianSettings;

public class EuclidianView3DNoGui extends EuclidianView3D {

	/**
	 * Euclidian 3D view for tests.
	 */
	public EuclidianView3DNoGui(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
		settings.addListener(this);
		start();
	}

	@Override
	public void repaint() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setToolTipText(String plainTooltip) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean suggestRepaint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Renderer createRenderer() {
		return new Renderer(this, Renderer.RendererType.SHADER) {
			@Override
			protected void doStartAR() {
				// no GL
			}

			@Override
			protected void setDepthFunc() {
				// no GL
			}

			@Override
			protected void enablePolygonOffsetFill() {
				// no GL
			}

			@Override
			protected void setBlendFunc() {
				// no GL
			}

			@Override
			public Object getCanvas() {
				return null;
			}

			@Override
			public void setLineWidth(double width) {
				// no GL
			}

			@Override
			public void enableTextures2D() {
				// no GL
			}

			@Override
			public void disableTextures2D() {
				// no GL
			}

			@Override
			public GBufferedImage createBufferedImage(DrawLabel3D label) {
				return null;
			}

			@Override
			public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
				// no GL
			}

			@Override
			public int createAlphaTexture(int sizeX, int sizeY, byte[] buf) {
				return 0;
			}

			@Override
			public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
				// no GL
			}

			@Override
			public void setTextureLinear() {
				// no GL
			}

			@Override
			public void setTextureNearest() {
				// no GL
			}

			@Override
			public void resumeAnimator() {
				// no GL
			}

			@Override
			public void setARShouldRestart() {
				// no GL
			}
		};
	}

	@Override
	protected void setTransparentCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean getShiftDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GFont getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setStyleBarMode(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateSizeKeepDrawables() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requestFocusInWindow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected CoordSystemAnimation newZoomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		// TODO Auto-generated method stub

	}

}
