package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AlphaComposite;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageOp;
import geogebra.common.awt.Color;
import geogebra.common.awt.Composite;
import geogebra.common.awt.Dimension;
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
import geogebra.web.openjdk.awt.geom.PathIterator;
import geogebra.web.openjdk.awt.geom.Shape;

import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.ImageElement;

public class Graphics2D extends geogebra.common.awt.Graphics2D {
	
	protected final Canvas canvas;
	private final Context2d context;

	private Font currentFont = new Font("normal");
	private Color color;
	private AffineTransform savedTransform;
	private float [] dash_array = null;

	/**
	 * @param canvas
	 */
	public Graphics2D(Canvas canvas) {
	    this.canvas = canvas;
	    this.context = canvas.getContext2d();
	    savedTransform = new geogebra.web.awt.AffineTransform();
    }

	
	@Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	//
	@Override
    public void drawImage(geogebra.common.awt.BufferedImage img, BufferedImageOp op, int x,
	        int y) {
		context.drawImage(((BufferedImage) img).getImageElement(), x, y);
	}

	
	@Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawString(AttributedCharacterIterator iterator, float x,
	        float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}


	/**
	 * @param shape
	 */
	public void fill(Shape shape) {
		if (shape == null) {
			AbstractApplication.printStacktrace("Error in EuclidianView.fill");
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
		context.fill();		
	}


	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	
	@Override
    public GraphicsConfiguration getDeviceConfiguration() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void setComposite(Composite comp) {
		context.setGlobalAlpha(((geogebra.web.awt.AlphaComposite)comp).getAlpha());
		
//		if (comp != null) {
//			float alpha  = ((geogebra.web.awt.AlphaComposite) comp).getAlpha();
//			if (alpha >= 0f && alpha < 1f) {
//				context.setGlobalAlpha(alpha);
//			}
//			context.setGlobalAlpha(0.5d);
//			context.restore();
//		}
	}

	
	@Override
    public void setPaint(Paint paint) {
		if(paint  instanceof Color){
			context.setFillStyle(Color.getColorString((Color)paint));	
			context.setStrokeStyle(Color.getColorString((Color)paint));
		}
		else if(paint instanceof GradientPaint){
			context.setFillStyle(((GradientPaint)paint).getGradient(context));
		}
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void setStroke(BasicStroke stroke) {
		if (stroke != null) {
			context.setLineWidth(((geogebra.web.awt.BasicStroke)stroke).getLineWidth());
			context.setLineCap(((geogebra.web.awt.BasicStroke)stroke).getEndCapString());
			context.setLineJoin(((geogebra.web.awt.BasicStroke)stroke).getLineJoinString());

			float [] dasharr = ((geogebra.web.awt.BasicStroke)stroke).getDashArray();
			if (dasharr != null) {
				JsArrayNumber jsarrn = JsArrayNumber.createArray().cast();
				jsarrn.setLength(dasharr.length);
				for (int i = 0; i < dasharr.length; i++)
					jsarrn.set(i, dasharr[i]);
				setStrokeDash( jsarrn );
			} else {
				setStrokeDash(null);
			}
			dash_array = dasharr;
		}
	}

	public native void setStrokeDash(JsArrayNumber dasharray) /*-{
		if (typeof $wnd.canvasHelpers.context.mozDash != 'undefined') {
			$wnd.canvasHelpers.context.mozDash = dasharray;
		} else if (typeof $wnd.canvasHelpers.context.webkitLineDash != 'undefined') {
			$wnd.canvasHelpers.context.webkitLineDash = dasharray;
		}
	}-*/;


	@Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public Object getRenderingHint(Key hintKey) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void setRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void addRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public RenderingHints getRenderingHints() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void translate(int x, int y) {
		context.translate(x, y);
		savedTransform.translate(x, y);
	}

	
	@Override
    public void translate(double tx, double ty) {
		context.translate(tx, ty);
		savedTransform.translate(tx, ty);

	}

	
	@Override
    public void rotate(double theta) {
		context.rotate(theta);
		savedTransform.concatenate(
				new geogebra.web.awt.AffineTransform(
						Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta), 0, 0));

	}

	
	@Override
    public void rotate(double theta, double x, double y) {
		context.translate(x, y);
		context.rotate(theta);
		context.translate(-x, -y);
		savedTransform.concatenate(
				new geogebra.web.awt.AffineTransform(
						Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta), x, y));
	}

	
	@Override
    public void scale(double sx, double sy) {
		context.scale(sx, sy);
		savedTransform.scale(sx, sy);
	}

	
	@Override
    public void shear(double shx, double shy) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
//		savedTransform.concatenate(
//				new geogebra.web.awt.AffineTransform(
//						1, shy, shx, 1, 0, 0));
		
	}

	
	@Override
    public void transform(AffineTransform Tx) {
		context.transform(Tx.getScaleX(), Tx.getShearY(),
				Tx.getShearX(), Tx.getScaleY(),
				((geogebra.web.awt.AffineTransform)Tx).getTranslateX(),
				((geogebra.web.awt.AffineTransform)Tx).getTranslateY());
		savedTransform.concatenate(Tx);
	}

	
	@Override
    public void setTransform(AffineTransform Tx) {
		context.setTransform(Tx.getScaleX(), Tx.getShearY(),
		Tx.getShearX(), Tx.getScaleY(),
		((geogebra.web.awt.AffineTransform)Tx).getTranslateX(),
		((geogebra.web.awt.AffineTransform)Tx).getTranslateY());
		savedTransform = Tx;

	}

	
	@Override
    public AffineTransform getTransform() {
		AffineTransform ret = new geogebra.web.awt.AffineTransform();
		ret.setTransform(savedTransform);
		return ret;
	}
	
	
	@Override
    public Paint getPaint() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public Composite getComposite() {
		return new geogebra.web.awt.AlphaComposite(3, (float) context.getGlobalAlpha());
	
//		context.save();
//		//just to not return null;
//		return new geogebra.web.awt.AlphaComposite(0, 0) {
//		};
	}

	
	@Override
    public void setBackground(Color color) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public Color getBackground() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}


	@Override
    public BasicStroke getStroke() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

		return new geogebra.web.awt.BasicStroke(
			(float) context.getLineWidth(), 
			geogebra.web.awt.BasicStroke.getCap(context.getLineCap()),
			geogebra.web.awt.BasicStroke.getJoin(context.getLineJoin()),
			0,
			dash_array,
			0
		);
	}

	public void clip(Shape s) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public FontRenderContext getFontRenderContext() {
		return new geogebra.web.awt.FontRenderContext(context);
	}

	
	@Override
    public Color getColor() {
		return color;
	}

	
	@Override
    public geogebra.common.awt.Font getFont() {
		return currentFont;
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
    	if(font instanceof geogebra.web.awt.Font){
    		currentFont=(geogebra.web.awt.Font)font;
    		//TODO: pass other parameters here as well
    		try{
    		context.setFont(currentFont.getFullFontString());
    		}
    		catch(Throwable t){
    			AbstractApplication.debug(currentFont.getFullFontString());
    			t.printStackTrace();
    		}
    	}
	    
    }

	
    @Override
    public void setColor(Color fillColor) {
    	context.setStrokeStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+(fillColor.getAlpha()/255d)+")");
    	context.setFillStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+(fillColor.getAlpha()/255d)+")");
    	this.color=fillColor;
    }

	@Override
    public void clip(geogebra.common.awt.Shape s) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }


	
    @Override
    public void drawImage(geogebra.common.awt.BufferedImage img, int x, int y,
            BufferedImageOp op) {
    	BufferedImage bi = geogebra.web.awt.BufferedImage.getGawtImage(img);
    	if(bi==null)
    		return;
    	context.drawImage(bi.getImageElement(), x, y);
    }

	
    @Override
    public void fillRect(int x, int y, int w, int h) {
    	context.fillRect(x, y, w, h);
    }

	
    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {

    	/* TODO: there is some differences between the result of
    	 * geogebra.awt.Graphics.drawLine(...) function.
    	 * Here is an attempt to make longer the vertical and horizontal lines:  
    	 
    	int x_1 = Math.min(x1,x2);
    	int y_1 = Math.min(y1,y2);
    	int x_2 = Math.max(x1,x2);
    	int y_2 = Math.max(y1,y2);
    	
    	if(x1==x2){
    		y_1--;
    		y_2++;
    	} else if(y1==y2){
    		x_1--;
    		x_2++;
    	}
    	 	
    	context.beginPath();
    	context.moveTo(x_1, y_1);
    	context.lineTo(x_2, y_2);
    	context.closePath();
    	context.stroke();
*/
    	context.beginPath();
    	context.moveTo(x1, y1);
    	context.lineTo(x2, y2);
    	context.closePath();
    	context.stroke();

    	
    }



	@Override
    public void setClip(Object shape) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }
	@Override
	public void drawRect(int x, int y, int width, int height) {
	   context.rect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		
	}


	public void setWidth(int w) {
	    canvas.setWidth(w+"px");	    
    }


	public void setHeight(int h) {
	    canvas.setHeight(h+"px");
    }


	public void setPreferredSize(Dimension preferredSize) {
	    setWidth((int) preferredSize.getWidth());
	    setHeight((int) preferredSize.getHeight());
	    setCoordinateSpaceHeight(getOffsetHeight());
	    setCoordinateSpaceWidth(getOffsetWidth());
    }
	
	public Canvas getCanvas() {
		return this.canvas;
	}


	@Override
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
	    roundRect(x,y,width,height,arcHeight);
	    context.stroke();
	    
    }
	
	/**
	 * Using arc, because arc to has buggy implementation in some browsers 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param r
	 */
	private void roundRect(int x,int y,int w,int h,int r){
		context.beginPath();
		int ey = y+h;
		int ex = x+w;
		float r2d = (float)Math.PI/180;
	    context.moveTo(x+r,y);
	    context.lineTo(ex-r,y);
	    context.arc(ex-r,y+r,r,r2d*270,r2d*360,false);
	    context.lineTo(ex,ey-r);
	    context.arc(ex-r,ey-r,r,r2d*0,r2d*90,false);
	    context.lineTo(x+r,ey);
	    context.arc(x+r,ey-r,r,r2d*90,r2d*180,false);
	    context.lineTo(x,y+r);
	    context.arc(x+r,y+r,r,r2d*180,r2d*270,false);
	    
		context.closePath();
	}

	@Override
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
		roundRect(x,y,width,height,arcHeight);
	    context.fill();
	    
    }

}
