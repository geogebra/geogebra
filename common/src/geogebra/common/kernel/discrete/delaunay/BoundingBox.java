package geogebra.common.kernel.discrete.delaunay;

/**
 * Created by IntelliJ IDEA.
 * User: Aviad Segev
 * Date: 22/11/2009
 * Time: 20:29:56
 * BoundingBox represents a horizontal bounding rectangle defined by its lower left
 * and upper right point. This is usually used as a rough approximation of the
 * bounded geometry
 */
public class BoundingBox
{
	/**
   *  the minimum x-coordinate
   */
  private double minx;

  /**
   *  the maximum x-coordinate
   */
  private double maxx;

  /**
   *  the minimum y-coordinate
   */
  private double miny;

  /**
   *  the maximum y-coordinate
   */
  private double maxy;

	/**
	 * Creates an empty  bounding box
	 */
	public BoundingBox()
	{
		setToNull();
	}

	/**
	 * Copy constructor
	 * @param other         the copied bounding box
	 */
	public BoundingBox(BoundingBox other)
	{
		if(other.isNull())
			setToNull();
		else
			init(other.minx, other.maxx, other.miny, other.maxy);
	}

	/**
	 * Creates a bounding box given the extent
	 * @param minx      minimum x coordinate
	 * @param maxx      maximum x coordinate
	 * @param miny      minimum y coordinate
	 * @param maxy      maximum y coordinate
	 */
	public BoundingBox(double minx, double maxx, double miny, double maxy)
	{
		init(minx,maxx, miny, maxy);
	}

	/**
	 * Create a bounding box between lowerLeft and upperRight
	 * @param lowerLeft     lower left point of the box
	 * @param upperRight    upper left point of the box
	 */
	public BoundingBox(Point_dt lowerLeft, Point_dt upperRight)
	{
		init(lowerLeft.x, upperRight.x, lowerLeft.y, upperRight.y);
	}

   /**
   *  Initialize a BoundingBox for a region defined by maximum and minimum values.
   *
   *@param  x1  the first x-value
   *@param  x2  the second x-value
   *@param  y1  the first y-value
   *@param  y2  the second y-value
   */
	private void init(double x1, double x2, double y1, double y2)
	{
		if (x1 < x2) {
			minx = x1;
			maxx = x2;
		}
		else {
			minx = x2;
			maxx = x1;
		}
		if (y1 < y2) {
			miny = y1;
			maxy = y2;
		}
		else {
			miny = y2;
			maxy = y1;
		}
	}

	/**
   *  Makes this BoundingBox a "null" envelope, that is, the envelope
   *  of the empty geometry.
   */
	private void setToNull()
	{
		minx = 0;
		maxx = -1;
		miny = 0;
		maxy = -1;
	}

	/**
   *  Returns true if this BoundingBox is a "null"
   *  envelope.
   *
   *@return    true if this BoundingBox is uninitialized
   *      or is the envelope of the empty geometry.
   */
	public boolean isNull()
	{
		return maxx < minx;
	}

	/**
   * Tests if the other BoundingBox lies wholely inside this BoundingBox
   *
   *@param  other the BoundingBox to check
   *@return true if this BoundingBox contains the other BoundingBox
   */
	public boolean contains(BoundingBox other) {
		return !(isNull() || other.isNull()) &&
				other.minx >= minx &&
				other.maxy <= maxx &&
				other.miny >= miny &&
				other.maxy <= maxy;
	}

	/**
	 * Unify the BoundingBoxes of this and the other BoundingBox
	 * @param other     another BoundingBox
	 * @return              The union of the two BoundingBoxes
	 */
	public BoundingBox unionWith(BoundingBox other)
	{
		if (other.isNull()) {
			return new BoundingBox(this);
		}
		if (isNull()) {
			return new BoundingBox(other);
		}
		else {
			return new BoundingBox(
					Math.min(minx, other.minx),
					Math.max(maxx, other.maxx),
					Math.min(miny, other.miny),
					Math.max(maxy, other.maxy));
		}
	}

	/**
	 * @return  Minimum x value
	 */
	public double minX()
	{
		return minx;
	}

	/**
	 * @return  Minimum y value
	 */
	public double minY()
	{
		return miny;
	}

	/**
	 * @return  Maximum x value
	 */
	public double maxX()
	{
		return maxx;
	}

	/**
	 * @return  Maximum y value
	 */
	public double maxY()
	{
		return maxy;
	}

	/**
	 * @return  Width of the bounding box
	 */
	public double getWidth()
	{
		return maxx - minx;
	}

	/**
	 * @return  Height of the bounding box
	 */
	public double getHeight()
	{
		return maxy - miny;
	}

	/**
	 * @return      Maximum coordinate of bounding box
	 */
	public Point_dt getMinPoint()
	{
		return new Point_dt(minx, miny);
	}

	/**
	 * @return      Minimum coordinate of bounding box
	 */
	public Point_dt getMaxPoint()
	{
		return new Point_dt(maxx, maxy);
	}
}