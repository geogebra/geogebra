package geogebra3D.euclidian3D;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;


/**
 * Class for drawing labels of 3D elements
 * @author matthieu
 *
 */
public class DrawLabel3D {
	
	/** text of the label */
    protected String text;    
    /** font of the label */
    protected Font font;
    /** color of the label */
    private Coords backgroundColor, color;
    /** origin of the label (left-bottom corner) */
    private Coords origin; 
    /** x and y offset */
    private float xOffset, yOffset;   
    private float xOffset2, yOffset2;   
    /** says if there's an anchor to do */
    private boolean anchor;
    /** says if the label is visible */
    private boolean isVisible;
    
    /** width and height of the text */
    private int height, width;
    /** width and height of the texture */
    private int height2, width2;
    
    /** buffer containing the texture */
    private ByteBuffer buffer;
    /** index of the texture used for this label */
    private int textureIndex;
    
    /** current view where this label is drawn */
	protected EuclidianView3D view;
	
	/** says it wait for reset */
	private boolean waitForReset;
	
	

	/** shift for getting alpha value */
    private static final int ALPHA_SHIFT = 24;
    
	
    /** temp graphics used for calculate bounds */
    protected Graphics2D tempGraphics = (new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)).createGraphics();
	
    
	
	/**
	 * common constructor
	 * @param view
	 */
	public DrawLabel3D(EuclidianView3D view){
		this.view = view;
	}
	
	/**
	 * update the label
	 * @param text
	 * @param fontsize
	 * @param color
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	public void update(String text, Font font, GColor color,
			Coords v, float xOffset, float yOffset){

		update(text, font, null, color, v, xOffset, yOffset);
	}
	
	
	/**
	 * update the label
	 * @param text
	 * @param fontsize
	 * @param color
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	public void update(String text, Font font, GColor backgroundColor, GColor color,
			Coords v, float xOffset, float yOffset){
		
		if (text.length()==0)
			return;
		
		this.origin = v;
		//if (v==null)Application.debug(text);
		this.color = new Coords((double) color.getRed()/255, 
				(double) color.getGreen()/255, (double) color.getBlue()/255,1);
		
		if (backgroundColor!=null)
			this.backgroundColor = new Coords((double) backgroundColor.getRed()/255, 
					(double) backgroundColor.getGreen()/255, (double) backgroundColor.getBlue()/255,1);
		else
			this.backgroundColor = null;
		
		
		if (view.isGrayScaled())
			this.color.convertToGrayScale();
		
		setIsVisible(true);
		
		


		if (waitForReset || !text.equals(this.text) || !font.equals(this.font)){

			this.text = text;
			this.font = font;

			tempGraphics.setFont(font);

			Rectangle2D rectangle = getBounds();

			int xMin = (int) rectangle.getMinX()-1;
			int xMax = (int) rectangle.getMaxX()+1;
			int yMin = (int) rectangle.getMinY()-1;
			int yMax = (int) rectangle.getMaxY()+1;

			//Application.debug(text+"\nxMin="+xMin+", xMax="+xMax+", advance="+textLayout.getAdvance());


			width=xMax-xMin;height=yMax-yMin;
			xOffset2=xMin;
			yOffset2=-yMax;

			//creates a 2D image
			BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bimg.createGraphics();

			AffineTransform gt = new AffineTransform();
			gt.scale(1, -1d);
			gt.translate(-xMin, -yMax); //put the baseline on the label anchor
			g2d.transform(gt);

			g2d.setColor(Color.BLACK);
			g2d.setFont(font);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			draw(g2d);		

			//creates the texture
			int[] intData = ((DataBufferInt) bimg.getRaster().getDataBuffer()).getData();
			buffer = ByteBuffer.wrap(ARGBtoAlpha(intData));
			/*
		if (text.contains("3d")){
			try {
				ImageIO.write(bimg, "png", new File("image.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			 */
			g2d.dispose();

			// update the texture
			updateTexture();
			waitForReset = false;
			//Application.debug("textureIndex = "+textureIndex);
		}
		
		this.xOffset = xOffset + xOffset2;
		this.yOffset = yOffset + yOffset2;


	}
	
	protected boolean hasIndex = false;
	
	
	protected Rectangle2D getBounds(){
		Rectangle2D rectangle = (new TextLayout(text, font, new FontRenderContext(null, false, false))).getBounds();	
		if(text.contains("_")){ //text contains subscript
			//Application.debug("yMin="+yMin+", yMax="+yMax);
			hasIndex = true;
			geogebra.common.awt.GPoint p = 
				EuclidianStatic.drawIndexedString(view.getApplication(), new geogebra.awt.GGraphics2DD(tempGraphics), text, 0, 0, false, false);
			rectangle.setRect(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight()+p.y);
		}else
			hasIndex = false;
		return rectangle;
	}
	
	protected void draw(Graphics2D g2d){
		if (hasIndex)
			EuclidianStatic.drawIndexedString(view.getApplication(), new geogebra.awt.GGraphics2DD(g2d), text, 0, 0, false, false);
		else
			g2d.drawString(text, 0, 0);	
	}
	
	/**
	 * set the label to be reset
	 */
	public void setWaitForReset(){
		waitForReset = true;
	}
	
	/**
	 * sets the anchor
	 * @param flag
	 */
	public void setAnchor(boolean flag){
		anchor = flag;
	}
	
	/**
	 * draws the label
	 * @param renderer
	 */
	public void draw(Renderer renderer){
		
		if (!isVisible)
    		return;
		
		if (textureIndex==0)
    		return;
		
		Coords v = view.getToScreenMatrix().mul(origin);
		int x = (int) (v.getX()+xOffset);
		if (anchor && xOffset<0) x-=width;
			
		int y = (int) (v.getY()+yOffset);
		if (anchor && yOffset<0) y-=height;
		
		
		int z = (int) v.getZ();
		
		
		//draw background
		if (backgroundColor!=null){
			renderer.setColor(backgroundColor);
			renderer.disableTextures();
			renderer.getGeometryManager().getText().rectangle(x, y, z, width, height);
		}
		
		//draw text
		renderer.setColor(color);
		renderer.enableTextures();
		renderer.getTextures().setTextureLinear(textureIndex);
		renderer.getGeometryManager().getText().rectangle(x, y, z, width2, height2);
		
		
		
	}

	/**
	 * update the texture
	 */
    public void updateTexture() {
    	
    	
    	if (textureIndex!=0 && !waitForReset){
    		view.getRenderer().getTextures().removeTexture(textureIndex);
    		textureIndex = 0;
    	}
    	
    	
    	textureIndex = view.getRenderer().getTextures().createAlphaTexture(
    			width2, height2, 
    			buffer);
    	
    }
	

	/** 
	 * sets the visibility of the label
	 * @param flag
	 */
	public void setIsVisible(boolean flag){
		isVisible = flag;
	}
	
	
	
	
    /** get alpha channel of the array ARGB description
     * @param pix
     * @return the alpha channel of the array ARGB description
     */
    protected byte[] ARGBtoAlpha(int[] pix) {
    	
    	//calculates 2^n dimensions
    	int w = firstPowerOfTwoGreaterThan(width);
    	int h = firstPowerOfTwoGreaterThan(height);
    	
    	//Application.debug("width="+width+",height="+height+"--w="+w+",h="+h);
    	
     	//get alpha channel and extends to 2^n dimensions
		byte[] bytes = new byte[w*h];
		int bytesIndex = 0;
		int pixIndex = 0;
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				bytes[bytesIndex] = 
					(byte) (pix[pixIndex] >> ALPHA_SHIFT);
				bytesIndex++;
				pixIndex++;
			}
			bytesIndex+=w-width;
		}
		
		//update width and height
		width2=w;
		height2=h;
		
		return bytes;
    }
    

    /**
     * 
     * @param val
     * @return first power of 2 greater than val
     */
    static final private int firstPowerOfTwoGreaterThan(int val){
    	
    	int ret = 1;
    	while(ret<val)
    		ret*=2;   	
    	return ret;
    	
    }

}
