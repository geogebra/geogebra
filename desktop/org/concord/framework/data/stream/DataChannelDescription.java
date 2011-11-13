/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.9 $
 * $Date: 2007-06-26 19:07:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.data.stream;

import java.awt.Color;
import java.util.ArrayList;

import org.concord.framework.data.DataDimension;


/**
 * DataChannelDescription
 * Class name and description
 *
 * Date created: Aug 24, 2004
 *
 * @author imoncada<p>
 *
 */
public class DataChannelDescription
{
	private String name;
	
	private DataDimension unit = null;
	private float tuneValue = 1.0f;

	// power of 10 precision
	private int precision;
	
	private boolean usePrecision = false;

	// The absolute min and max of the data or NaN if
	// not available
	private float absoluteMin = Float.NaN;
	private float absoluteMax = Float.NaN;

	// The recommended min and max of the data or NaN if
	// not available
	private float recommendMin = Float.NaN;
	private float recommendMax = Float.NaN;
	
	//This indicates if the data is numeric, so the precision will have to be used, etc
	private boolean numericData = true;
	
	//Whether a column in a data table representing this channel should be locked
	private boolean locked;
	
	// Possible values for this data channel
	private ArrayList<Object> possibleValues = new ArrayList<Object>();

	private Color color;

	/**
	 * 
	 */
	public DataChannelDescription()
	{
		this("");
	}

	/**
	 * 
	 */
	public DataChannelDescription(String name)
	{
		this.name = name;
	}
	
	public DataChannelDescription(String name, int precision)
	{
		this(name);
		setPrecision(precision);
	}	
	
	public void	setUnit(DataDimension unit)
	{
		this.unit = unit;
	}

	public DataDimension getUnit()
	{
		return unit;
	}

	public void setTuneValue(float tuneValue)
	{
		this.tuneValue = tuneValue;
	}
	
	public float getTuneValue()
	{
		return tuneValue;
	}

	/**
	 * @return Returns the absoluteMax.
	 */
	public float getAbsoluteMax() {
		return absoluteMax;
	}
	/**
	 * @param absoluteMax The absoluteMax to set.
	 */
	public void setAbsoluteMax(float absoluteMax) {
		this.absoluteMax = absoluteMax;
	}
	/**
	 * @return Returns the absoluteMin.
	 */
	public float getAbsoluteMin() {
		return absoluteMin;
	}
	/**
	 * @param absoluteMin The absoluteMin to set.
	 */
	public void setAbsoluteMin(float absoluteMin) {
		this.absoluteMin = absoluteMin;
	}
	/**
	 * @return Returns the precision.
	 */
	public int getPrecision() {
		return precision;
	}
	/**
	 * @param precision The precision to set.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
		usePrecision = true;
	}
	/**
	 * @return Returns the recommendMax.
	 */
	public float getRecommendMax() {
		return recommendMax;
	}
	/**
	 * @param recommendMax The recommendMax to set.
	 */
	public void setRecommendMax(float recommendMax) {
		this.recommendMax = recommendMax;
	}
	/**
	 * @return Returns the recommendMin.
	 */
	public float getRecommendMin() {
		return recommendMin;
	}
	/**
	 * @param recommendMin The recommendMin to set.
	 */
	public void setRecommendMin(float recommendMin) {
		this.recommendMin = recommendMin;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return Returns the numericData.
	 */
	public boolean isNumericData()
	{
		return numericData;
	}
	
	/**
	 * @param numericData The numericData to set.
	 */
	public void setNumericData(boolean numericData)
	{
		this.numericData = numericData;
	}
	/**
	 * @return Returns the usePrecision.
	 */
	public boolean isUsePrecision()
	{
		return usePrecision;
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}
	
	public ArrayList<Object> getPossibleValues() {
		return this.possibleValues;
	}
	
	public void setPossibleValues(ArrayList<Object> values) {
		this.possibleValues = values;
	}
	
	/**
	 * For multi-channel bar graphs, we can set the color of
	 * each channel/bar here.
	 * @return
	 */
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public Object getCopy() {
		DataChannelDescription channelDesc = new DataChannelDescription();
		channelDesc.absoluteMax = this.absoluteMax;
		channelDesc.absoluteMin = this.absoluteMin;
		channelDesc.name = this.name;
		channelDesc.numericData = this.numericData;
		channelDesc.precision = this.precision;
		channelDesc.recommendMax = this.recommendMax;
		channelDesc.recommendMin = this.recommendMin;
		channelDesc.tuneValue = this.tuneValue;
		channelDesc.unit = this.unit;
		channelDesc.usePrecision = this.usePrecision;
		channelDesc.possibleValues = this.possibleValues;
		return channelDesc;
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DataChannelDescription)){
			return false;
		}
		
		DataChannelDescription channelDesc = 
			(DataChannelDescription)obj;
		
		if(floatEquals(channelDesc.absoluteMax, this.absoluteMax) &&
				floatEquals(channelDesc.absoluteMin, this.absoluteMin) &&
				stringEquals(channelDesc.name, this.name) &&				
				channelDesc.numericData == this.numericData &&
				channelDesc.precision == this.precision &&
				floatEquals(channelDesc.recommendMax, this.recommendMax) &&
				floatEquals(channelDesc.recommendMin, this.recommendMin) &&
				floatEquals(channelDesc.tuneValue,this.tuneValue) &&
				unitEquals(channelDesc.unit,this.unit) &&
				channelDesc.usePrecision == this.usePrecision &&
				channelDesc.possibleValues.equals(this.possibleValues)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * This is separated out so it can be handled by Waba.  Waba will
	 * most likely not implement Float.compare correctly.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public final static boolean floatEquals(float f1, float f2)
	{
		return Float.compare(f1, f2) == 0;
	}
	
	public final static boolean stringEquals(String s1, String s2)
	{
		// This will handle the null, null case, as well as the trivial
		// case
		if(s1 == s2){
			return true;
		}
		
		// These are special cased because some JVM (waba) don't handle
		// String.equals(null) 
		if(s1 != null && s2 == null){
			return false;
		}
		
		if(s1 == null && s2 != null){
			return false;
		}
		
		return s1.equals(s2);
	}
	
	public final static boolean unitEquals(DataDimension u1, DataDimension u2)
	{
		if(u1 == u2){
			return true;
		}
		
		if(u1 == null){
			// u2 has to be != null if we got past the above check
			return false;
		}
		
		return u1.equals(u2);
	}
}
