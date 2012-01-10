package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AlphaComposite;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.BufferedImageOp;
import geogebra.common.awt.Color;
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
import geogebra.common.main.AbstractApplication;
import geogebra.web.kernel.gawt.BufferedImage;
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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	//tmp
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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return false;
	}

	//
	public void drawImage(BufferedImageAdapter img, BufferedImageOp op, int x,
	        int y) {
		context.drawImage(((BufferedImage) img).getImageElement(), x, y);
	}

	
	@Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void drawString(AttributedCharacterIterator iterator, float x,
	        float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}


	/**
	 * @param shape
	 */
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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return false;
	}

	
	@Override
    public GraphicsConfiguration getDeviceConfiguration() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public void setComposite(Composite comp) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void setPaint(Paint paint) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void setStroke(BasicStroke stroke) {
		if (stroke != null) {
			context.setLineWidth(((geogebra.web.awt.BasicStroke)stroke).getLineWidth());
			context.setLineCap(((geogebra.web.awt.BasicStroke)stroke).getEndCapString());
			context.setLineJoin(((geogebra.web.awt.BasicStroke)stroke).getLineJoinString());
		}
	}

	
	@Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public Object getRenderingHint(Key hintKey) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public void setRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void addRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public RenderingHints getRenderingHints() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public void translate(int x, int y) {
		context.translate(x, y);
	}

	
	@Override
    public void translate(double tx, double ty) {
		context.translate(tx, ty);

	}

	
	@Override
    public void rotate(double theta) {
		context.rotate(theta);

	}

	
	@Override
    public void rotate(double theta, double x, double y) {
		context.translate(x, y);
		context.rotate(theta);
		context.translate(-x, -y);
	}

	
	@Override
    public void scale(double sx, double sy) {
		context.scale(sx, sy);
	}

	
	@Override
    public void shear(double shx, double shy) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	}

	
	@Override
    public void transform(AffineTransform Tx) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public void setTransform(AffineTransform Tx) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public AffineTransform getTransform() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public Paint getPaint() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public Composite getComposite() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public void setBackground(Color color) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public Color getBackground() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public BasicStroke getStroke() {
		// TODO Auto-generated method stub
		return new geogebra.web.awt.BasicStroke((float) context.getLineWidth(), 
				geogebra.web.awt.BasicStroke.getCap(context.getLineCap()),
				geogebra.web.awt.BasicStroke.getJoin(context.getLineJoin()));
	}

	public void clip(Shape s) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub

	}

	
	@Override
    public FontRenderContext getFontRenderContext() {
		return new geogebra.web.awt.FontRenderContext(context);
	}

	
	@Override
    public Color getColor() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		return null;
	}

	
	@Override
    public geogebra.common.awt.Font getFont() {
		return currentFont;
	}

	//
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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	
    @Override
    public void setColor(Color selColor) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	
    @Override
    public void draw(Object shape) {
	  draw((geogebra.web.awt.Shape) shape);
    }

	
    @Override
    public void fill(Object shape) {
	    fill((geogebra.web.awt.Shape) shape);
    }

	@Override
    public void clip(geogebra.common.awt.Shape s) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }


	
    public void drawImage(BufferedImageAdapter img, int x, int y,
            BufferedImageOp op) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	
    public void fillRect(int i, int j, int k, int l) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
    }

	
    public void drawLine(int x1, int y1, int x2, int y2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	
    public void setComposite(AlphaComposite alphaComp) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }


	@Override
    public void setClip(Object shape) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }


	@Override
    public void draw(geogebra.common.awt.Shape s) {
		draw(geogebra.web.awt.GenericShape.getGawtShape(s));
    }


	@Override
    public void fill(geogebra.common.awt.Shape s) {
		fill(geogebra.web.awt.GenericShape.getGawtShape(s));
    }


	@Override
    public geogebra.common.awt.Shape getClip() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    return null;
    }
	@Override
	public void drawRect(int x, int y, int width, int height) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
		
	}

}
