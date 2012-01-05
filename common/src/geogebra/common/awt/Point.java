package geogebra.common.awt;

/** Class for integer tuples **/
public class Point {

	public Point(){
		x=0;
		y=0;
	}
	public Point(int x,int y){
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
	public void setLocation(Point p){
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
		if(!(o instanceof Point))
			return false;
		return ((Point)o).x==x && ((Point)o).y==y;
	}
	
	@Override
	public int hashCode(){
		return (x >> 16) ^ y;
	}

}
