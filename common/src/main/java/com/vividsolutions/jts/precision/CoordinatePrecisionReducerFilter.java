package com.vividsolutions.jts.precision;

import com.vividsolutions.jts.geom.*;

/**
 * Reduces the precision of the {@link Coordinate}s in a
 * {@link CoordinateSequence} to match the supplied {@link PrecisionModel}.
 * Uses {@link PrecisionModel#makePrecise(double)}.
 * The input is modified in-place, so
 * it should be cloned beforehand if the
 * original should not be modified.
 * 
 * @author mbdavis
 *
 */
public class CoordinatePrecisionReducerFilter
	implements CoordinateSequenceFilter
{
	private PrecisionModel precModel;
	
	/**
	 * Creates a new precision reducer filter.
	 * 
	 * @param precModel the PrecisionModel to use 
	 */
	public CoordinatePrecisionReducerFilter(PrecisionModel precModel)
	{
		this.precModel = precModel;
	}
	
	/**
	 * Rounds the Coordinates in the sequence to match the PrecisionModel
	 */
	public void filter(CoordinateSequence seq, int i)
	{
		seq.setOrdinate(i, 0, precModel.makePrecise(seq.getOrdinate(i, 0)));
		seq.setOrdinate(i, 1, precModel.makePrecise(seq.getOrdinate(i, 1)));
	}
  
	/**
	 * Always runs over all geometry components.
	 *  
	 * @return false
	 */
  public boolean isDone()  {  	return false;  }
  
  /**
   * Always reports that the geometry has changed
   * 
   * @return true
   */
  public boolean isGeometryChanged() { return true;   }
}
