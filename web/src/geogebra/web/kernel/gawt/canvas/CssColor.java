package geogebra.web.kernel.gawt.canvas;

public class CssColor {
	
	  /*
	   * Some basic color strings that are often used for the web.
	   * Compiler should optimize these out if they are not used.
	   */
	  public static final CssColor ALPHA_GREY = new CssColor(0,0,0,0.3);
	  public static final CssColor ALPHA_RED = new CssColor(255,0,0,0.3);
	  public static final CssColor BLACK = new CssColor(0,0,0);
	  public static final CssColor BLUE = new CssColor(49,140,224);
	  public static final CssColor BLUEVIOLET = new CssColor(138,43,226);
	  public static final CssColor CYAN = new CssColor(95,162,224);
	  public static final CssColor GREEN = new CssColor(35,239,36);
	  public static final CssColor GREY = new CssColor(169,169,169);
	  public static final CssColor LIGHTGREY = new CssColor(238,238,238);
	  public static final CssColor ORANGE = new CssColor(248,130,71);
	  public static final CssColor PEACH = new CssColor(255,211,147);
	  public static final CssColor PINK = new CssColor(255,0,255);
	  public static final CssColor RED = new CssColor(255,0,0);
	  public static final CssColor SKY_BLUE = new CssColor(198,222,250);
	  public static final CssColor WHITE = new CssColor(255,255,255);
	  public static final CssColor YELLOW = new CssColor(255,255,0);
	  public static final CssColor DARK_ORANGE = new CssColor(196,70,7);
	  public static final CssColor BRIGHT_ORANGE = new CssColor(251,92,12);  
	  public static final CssColor DARK_BLUE = new CssColor(12,106,193);
	  
	  private String colorStr = "";
	  
	  /**
	   * Create a new Color object with the specified RGB 
	   * values.
	   * 
	   * @param r red value 0-255
	   * @param g green value 0-255
	   * @param b blue value 0-255
	   */
	  public CssColor(int r, int g, int b) {
	    this.colorStr = "rgb(" + r + "," + g + "," + b + ")";
	  }
	  
	  /**
	   * Create a new Color object with the specified RGBA 
	   * values.
	   * 
	   * @param r red value 0-255
	   * @param g green value 0-255
	   * @param b blue value 0-255
	   * @param a alpha channel value 0-1
	   */
	  public CssColor(int r, int g, int b, double a) {
	    this.colorStr = "rgba(" + r + "," + g + "," + b + "," + a + ")";
	  }
	  
	  /**
	   * Create a Color using a valid CSSString. 
	   * We do not do any validation so be careful!
	   */
	  public CssColor(String colorStr) {
	    this.colorStr = colorStr;
	  }
	  
	  @Override
	public String toString() {
	    return this.colorStr;
	  }

}
