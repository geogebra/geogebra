package geogebra.common.awt;


/** Class for integer tuples **/
public class GPoint {

	public GPoint(){
		x=0;
		y=0;
	}
	public GPoint(int x,int y){
		this.x=x;
		this.y=y;
	}
	/** y-coordinate **/
	public int y = 0;
	/** x-coordinate **/
	public int x = 0;
	
	/**
	 * Set x and y at the same time
	 * @param x x-coord
	 * @param y y-coord
	 */
	public void setLocation(int x,int y){
		this.x=x;
		this.y=y;
	}
	
	/**
	 * Take both coords from a point
	 * @param p point
	 */
	public void setLocation(GPoint p){
		this.x=p.x;
		this.y=p.y;
	}
	
	public int getY(){
		return y;
	}
	
	public int getX(){
		return x;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof GPoint))
			return false;
		return ((GPoint)o).x==x && ((GPoint)o).y==y;
	}
	
	@Override
	public int hashCode(){
		return (x << 16) ^ y;
	}
	
	public double distance(GPoint d) {
		return Math.sqrt((x - d.x) * (x - d.x) + (y - d.y) * (y - d.y));
	}

	public double distance(double dx, double dy) {
		return (int) Math.sqrt((x - dx) * (x - dx) + (y - dy) * (y - dy));
	}

}
