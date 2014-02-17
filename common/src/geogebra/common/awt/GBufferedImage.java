package geogebra.common.awt;


public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	int getWidth();

	int getHeight();

	geogebra.common.awt.GGraphics2D createGraphics();

	GBufferedImage getSubimage(int i, int j, int size, int size2);
	
	/**
	 * 
	 * @return ARGB pixel data
	 */
	public int[] getData();

}
