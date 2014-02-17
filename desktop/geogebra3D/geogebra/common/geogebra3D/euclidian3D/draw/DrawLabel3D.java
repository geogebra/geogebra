package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.awt.GAffineTransformD;
import geogebra.awt.GBufferedImageD;
import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

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
    protected GFont font;
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
    protected int height, width;
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
    protected GGraphics2D tempGraphics = (new GBufferedImageD(1, 1, GBufferedImage.TYPE_INT_ARGB)).createGraphics();
	
    
	
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
	public void update(String text, GFont font, GColor color,
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
	public void update(String text, GFont font, GColor backgroundColor, GColor color,
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

			GRectangle rectangle = getBounds();

			int xMin = (int) rectangle.getMinX()-1;
			int xMax = (int) rectangle.getMaxX()+1;
			int yMin = (int) rectangle.getMinY()-1;
			int yMax = (int) rectangle.getMaxY()+1;

			//Application.debug(text+"\nxMin="+xMin+", xMax="+xMax+", advance="+textLayout.getAdvance());


			width=xMax-xMin;height=yMax-yMin;
			xOffset2=xMin;
			yOffset2=-yMax;

			//creates a 2D image
			GBufferedImage bimg = new GBufferedImageD(width, height, GBufferedImage.TYPE_INT_ARGB);
			GGraphics2D g2d = bimg.createGraphics();

			GAffineTransform gt = new GAffineTransformD();
			gt.scale(1, -1d);
			gt.translate(-xMin, -yMax); //put the baseline on the label anchor
			g2d.transform(gt);

			g2d.setColor(GColor.BLACK);
			g2d.setFont(font);
			g2d.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

			draw(g2d);		

			//creates the texture
			int[] intData = bimg.getData();
			buffer = ByteBuffer.wrap(ARGBtoAlpha(intData));
			
			//g2d.dispose();

			// update the texture
			updateTexture();
			waitForReset = false;
			//Application.debug("textureIndex = "+textureIndex);
		}
		
		this.xOffset = xOffset;// + xOffset2;
		this.yOffset = yOffset;// + yOffset2;


	}
	
	protected boolean hasIndex = false;
	
	
	
	protected GRectangle getBounds(){
		GRectangle rectangle = EuclidianStatic.drawMultiLineText(view.getApplication(), text, 0, 0, tempGraphics, false);
		if(text.contains("_")){ //text contains subscript
			hasIndex = true;
			geogebra.common.awt.GPoint p = 
				EuclidianStatic.drawIndexedString(view.getApplication(), tempGraphics, text, 0, 0, false, false);
			rectangle.setRect(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight()+p.y);
		}else{
			hasIndex = false;
		}
		
		return rectangle;
	}
	
	
	
	
	
	protected void draw(GGraphics2D g2d){
		if (hasIndex)
			EuclidianStatic.drawIndexedString(view.getApplication(), g2d, text, 0, 0, false, false);
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
	 * @param renderer renderer
	 */
	public void draw(Renderer renderer){
		
		draw(renderer, false);
	}
	
	/**
	 * draws the label
	 * @param renderer renderer
	 * @param forPicking says if it's for picking
	 */
	public void draw(Renderer renderer, boolean forPicking){
		
		if (!isVisible)
    		return;
		
		if (textureIndex==0)
    		return;
		
		Coords v = view.getToScreenMatrix().mul(origin);
		int x = (int) (v.getX()+xOffset);
		if (anchor && xOffset<0){ 
			x-=width;
		}else{
			x+=xOffset2;
		}
			
		int y = (int) (v.getY()+yOffset);
		if (anchor && yOffset<0){ 
			y-=height;
		}else{
			y+=yOffset2;
		}
		
		
		int z = (int) v.getZ();
		

		if (forPicking){
			renderer.getGeometryManager().rectangle(x, y, z, width, height);
			
		}else{

			//draw background
			if (backgroundColor!=null){
				renderer.setColor(backgroundColor);
				renderer.disableTextures();
				renderer.getGeometryManager().rectangle(x, y, z, width, height);
			}

			//draw text
			draw(renderer,x,y,z);
			
		}
	}
	
	/**
	 * draw at (x,y,z)
	 * @param renderer renderer
	 * @param x x
	 * @param y y
	 * @param z z
	 */
	protected void draw(Renderer renderer, int x, int y, int z){
		//draw text
		renderer.setColor(color);
		renderer.enableTextures();
		renderer.getTextures().setTextureNearest(textureIndex);
		renderer.getGeometryManager().rectangle(x, y, z, width2, height2);

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
