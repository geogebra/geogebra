package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.BufferedImageOp;
import geogebra.common.awt.Color;
import geogebra.common.awt.ColorAdapter;
import geogebra.common.awt.Composite;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.GlyphVector;
import geogebra.common.awt.GraphicsConfiguration;
import geogebra.common.awt.Image;
import geogebra.common.awt.ImageObserver;
import geogebra.common.awt.Key;
import geogebra.common.awt.Paint;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.RenderableImage;
import geogebra.common.awt.RenderedImage;
import geogebra.common.awt.RenderingHints;
import geogebra.web.kernel.gawt.BufferedImage;
import geogebra.web.kernel.gawt.Font;
import geogebra.web.kernel.gawt.PathIterator;
import geogebra.web.kernel.gawt.Shape;

import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;

public class Graphics2D extends geogebra.common.awt.Graphics2D {
	
	protected final Canvas canvas;
	private final Context2d context;
	
	private Font currentFont = new Font("normal");

	/**
	 * @param canvas
	 */
	public Graphics2D(Canvas canvas) {
	    this.canvas = canvas;
	    this.context = canvas.getContext2d();
    }

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		// TODO Auto-generated method stub

	}

	//tmp@Override
	/**<p>
	 * Draws a shape.
	 * </p>
	 * 
	 * @param shape
	 */
	public void draw(Shape shape) {
		if (shape == null) {
			GWT.log("Error in EuclidianView.draw");
			return;
		}
		context.beginPath();
		PathIterator it = shape.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				context.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				context.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				context.bezierCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				context.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
		context.stroke();

	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public void drawImage(BufferedImageAdapter img, BufferedImageOp op, int x,
	        int y) {
		context.drawImage((BufferedImage) img, x, y);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y) {
		context.fillText(str, x, y);
	}

	@Override
	public void drawString(String str, float x, float y) {
		context.fillText(str, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
	        float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub

	}


	public void fill(Shape shape) {
		if (shape == null) {
			GWT.log("Error in EuclidianView.fill");
			return;
		}
		// TODO Auto-generated method stub
		context.beginPath();
		PathIterator it = shape.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				context.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				context.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				context.bezierCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				context.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
			default:
				break;
			}
			it.next();
		}
		context.fill();		
	}


	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaint(Paint paint) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void setStroke(Stroke stroke) {
		if (stroke != null) {
			context.setLineWidth(stroke.getLineWidth());
			context.setLineCap(stroke.getLineCap());
			context.setLineJoin(stroke.getLineJoin());
		}
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void translate(double tx, double ty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(double theta, double x, double y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(double sx, double sy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shear(double shx, double shy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transform(AffineTransform Tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public Stroke getStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clip(Shape s) {
		// TODO Auto-generated method stub

	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public geogebra.common.awt.Font getFont() {
		return currentFont;
	}

	//@Override
	public void setFont(Font font) {
		currentFont = font;
	}

	@Override
	public void setPaint(Color fillColor) {
		context.setFillStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+fillColor.getAlpha()/255+")");	
		context.setStrokeStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+fillColor.getAlpha()/255+")");

	}

	public void setCoordinateSpaceHeight(int height) {
		canvas.setCoordinateSpaceHeight(height);
    }

	public void setCoordinateSpaceWidth(int width) {
	    canvas.setCoordinateSpaceWidth(width);
    }

	public int getOffsetWidth() {
		return canvas.getOffsetWidth();
    }

	public int getOffsetHeight() {
	   return canvas.getOffsetHeight();
    }

	public int getCoordinateSpaceWidth() {
	   return canvas.getCoordinateSpaceWidth();
    }

	public int getCoordinateSpaceHeight() {
	    return canvas.getCoordinateSpaceHeight();
    }

	public int getAbsoluteTop() {
	    return canvas.getAbsoluteTop();
    }

	public int getAbsoluteLeft() {
	    return canvas.getAbsoluteLeft(); 
    }

	@Override
    public void setFont(geogebra.common.awt.Font font) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setStroke(BasicStroke s) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(Color selColor) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void draw(Object shape) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void fill(Object shape) {
	    // TODO Auto-generated method stub
	    
    }
}
