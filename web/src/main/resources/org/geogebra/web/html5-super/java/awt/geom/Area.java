/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
// This file was later modified by GeoGebra Inc.

package java.awt.geom;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.harmonyhelper.Crossing;
import java.awt.harmonyhelper.CrossingHelper;
import java.awt.harmonyhelper.CurveCrossingHelper;
import java.awt.harmonyhelper.GeometryUtil;
import java.awt.harmonyhelper.IntersectPoint;

import java.util.NoSuchElementException;

public class Area implements Shape, Cloneable {

    /**
     * the coordinates array of the shape vertices
     */
	private double coords[] = new double[20];
	
	/**
	 * the coordinates quantity
	 */
	private int coordsSize = 0;
	
	/**
	 * the rules array for the drawing of the shape edges
	 */
	private int rules[] = new int[10];
	
	/**
	 * the rules quantity
	 */
	private int rulesSize = 0;
	
	/**
	 * offsets[i] - index in array of coords and i - index in array of rules
	 */
	private int offsets[] = new int[10];
	
	/**
	 * the quantity of MOVETO rule occurences
	 */
	private int moveToCount = 0;
	
	/**
	 * true if the shape is polygon
	 */
	private boolean isPolygonal = true;

	public Area() {
	}

	public Area(Shape s) {
		double segmentCoords[] = new double[6];
		double lastMoveX = 0.0;
		double lastMoveY = 0.0;
		int rulesIndex = 0;
		int coordsIndex = 0;
		
		for (PathIterator pi = s.getPathIterator(null); 
		        !pi.isDone(); pi.next()) {
			coords = adjustSize(coords, coordsIndex + 6);
			rules = adjustSize(rules, rulesIndex + 1);
			offsets = adjustSize(offsets, rulesIndex + 1);
			rules[rulesIndex] = pi.currentSegment(segmentCoords);
			offsets[rulesIndex] = coordsIndex;
			
			switch (rules[rulesIndex]) {
                case PathIterator.SEG_MOVETO:
                    coords[coordsIndex++] = segmentCoords[0];
                    coords[coordsIndex++] = segmentCoords[1];
                    lastMoveX = segmentCoords[0];
                    lastMoveY = segmentCoords[1];
                    ++moveToCount;
                    break;
                case PathIterator.SEG_LINETO:
                    if ((segmentCoords[0] != lastMoveX) || 
                    		(segmentCoords[1] != lastMoveY)) {
                        coords[coordsIndex++] = segmentCoords[0];
                        coords[coordsIndex++] = segmentCoords[1];
                    } else {
                        --rulesIndex;
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    System.arraycopy(segmentCoords, 0, coords, coordsIndex, 4);
                    coordsIndex += 4;
                    isPolygonal = false;
                    break;
                case PathIterator.SEG_CUBICTO:
                    System.arraycopy(segmentCoords, 0, coords, coordsIndex, 6);
                    coordsIndex += 6;
                    isPolygonal = false;
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
            }
            ++rulesIndex;
		}
		
		if ((rulesIndex != 0) && 
				(rules[rulesIndex - 1] != PathIterator.SEG_CLOSE)) {
			rules[rulesIndex] = PathIterator.SEG_CLOSE;
			offsets[rulesIndex] = coordsSize;
		}
		
		rulesSize = rulesIndex;
		coordsSize = coordsIndex;
	}

	public boolean contains(double x, double y) {
        return !isEmpty() &&
                   containsExact(x, y) > 0;
    }

	public boolean contains(double x, double y, double width, double height) {
		int crossCount = Crossing.intersectPath(getPathIterator(null), x, y,
				width, height);
		return crossCount != Crossing.CROSSING &&
			       Crossing.isInsideEvenOdd(crossCount);
	}

	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean equals(Area obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		Area area = (Area)clone();
		area.subtract(obj);
		return area.isEmpty();
	}

	public boolean intersects(double x, double y, double width, double height) {
		if ((width <= 0.0) || (height <= 0.0)) {
			return false;
		} else if (!getBounds2D().intersects(x, y, width, height)) {
			return false;
		}
		
		int crossCount = Crossing.intersectShape(this, x, y, width, height);
		return Crossing.isInsideEvenOdd(crossCount);
	}

	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	public Rectangle2D getBounds2D() {
		double maxX = coords[0];
		double maxY = coords[1];
		double minX = coords[0];
		double minY = coords[1];

		for (int i = 0; i < coordsSize;) {
			minX = Math.min(minX, coords[i]);
			maxX = Math.max(maxX, coords[i++]);
			minY = Math.min(minY, coords[i]);
			maxY = Math.max(maxY, coords[i++]);
		}
		
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	public PathIterator getPathIterator(AffineTransform t) {
		return new AreaPathIterator(this, t);
	}
	
	public PathIterator getPathIterator(AffineTransform t, double flatness) {
		return new FlatteningPathIterator(getPathIterator(t), flatness);
	}
	
	public boolean isEmpty() {
		return (rulesSize == 0) && (coordsSize == 0);
	}

	public boolean isPolygonal() {
		return isPolygonal;
	}

	public boolean isRectangular() {
        return (isPolygonal) && (rulesSize <= 5) && (coordsSize <= 8) &&
               (coords[1] == coords[3]) && (coords[7] == coords[5]) &&
               (coords[0] == coords[6]) && (coords[2] == coords[4]);
    }

	public boolean isSingular() {
		return (moveToCount <= 1);
	}

	public void reset() {
		coordsSize = 0;
		rulesSize = 0;
	}

	public void transform(AffineTransform t) {
		copy(new Area(t.createTransformedShape(this)), this);
	}

	public Area createTransformedArea(AffineTransform t) {
		return new Area(t.createTransformedShape(this));
	}

	//AR @Override
    public Object clone() {
		Area area = new Area();
		copy(this, area);
		return area;
	}

	public void add(Area area) {
    	if (area == null || area.isEmpty()) {
    	    return;
    	} else if (isEmpty()) {
    	    copy(area, this);
    		return;
    	}

		if (isPolygonal() && area.isPolygonal()) {
			addPolygon(area);
		} else {
			addCurvePolygon(area);
		}
		
		if (getAreaBoundsSquare() < GeometryUtil.EPSILON) {
		    reset();
		}
	}
	   
	public void intersect(Area area) {
		if (area == null) {
		    return;
		} else if (isEmpty() || area.isEmpty()) {
		    reset();
			return;		
		}
		
		if (isPolygonal() && area.isPolygonal()) {
			intersectPolygon(area);
		} else {
			intersectCurvePolygon(area);
		}
		
		if (getAreaBoundsSquare() < GeometryUtil.EPSILON) {
		    reset();
		}
	}
	
	public void subtract(Area area) {
		if (area == null || isEmpty() || area.isEmpty()) {
		    return;
		}

		if (isPolygonal() && area.isPolygonal()) {
			subtractPolygon(area);
		} else {
			subtractCurvePolygon(area);
		}
		
		if (getAreaBoundsSquare() < GeometryUtil.EPSILON) {
		    reset();
		}
	}
	
 	public void exclusiveOr(Area area) {
		Area a = (Area) clone();
		a.intersect(area);
		add(area);
		subtract(a);
	}

	private void addCurvePolygon(Area area) {
		CurveCrossingHelper crossHelper = new CurveCrossingHelper(
	            new double[][] { coords, area.coords },  
		        new int[] { coordsSize, area.coordsSize }, 
		        new int[][] { rules, area.rules },
				new int[] { rulesSize, area.rulesSize }, 
				new int[][] { offsets, area.offsets });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0) {
			if (area.contains(getBounds2D())) {
				copy(area, this);
			} else if (!contains(area.getBounds2D())) {
				coords = adjustSize(coords, coordsSize + area.coordsSize);
				System.arraycopy(area.coords, 0, coords, coordsSize,
								 area.coordsSize);
				coordsSize += area.coordsSize;
				rules = adjustSize(rules, rulesSize + area.rulesSize);
				System.arraycopy(area.rules, 0, rules, rulesSize, 
								 area.rulesSize);
				rulesSize += area.rulesSize;
				offsets = adjustSize(offsets, rulesSize + area.rulesSize);
				System.arraycopy(area.offsets, 0, offsets, rulesSize, 
								 area.rulesSize);
			}
			
			return;
		}

        double[] resultCoords = new double[coordsSize + area.coordsSize + 
                                                       intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
            int curIndex = point.getEndIndex(true);
            
            if (curIndex < 0) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], 
            		                      coords[2 * curIndex + 1]) > 0) { 
            	isCurrentArea = false;
            } else {
            	isCurrentArea = true;
            }

            IntersectPoint nextPoint = getNextIntersectPoint(intersectPoints, 
                                                             point, 
                                                             isCurrentArea);
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = point.getRuleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;
            
            if ((point.getRuleIndex(isCurrentArea) > 
                    nextPoint.getRuleIndex(isCurrentArea))) {
            	int rulesSize = (isCurrentArea) ? this.rulesSize : 
            		                              area.rulesSize;
            	resultCoordPos = includeCoordsAndRules(offset + 1, rulesSize,
            			                               rules, offsets, 
            			                               resultRules, 
            			                               resultOffsets, 
            			                               resultCoords, coords, 
            			                               resultRulesPos, 
            			                               resultCoordPos,
            			                               point, isCurrentArea, 
            			                               false, 0);
            	resultRulesPos += rulesSize - offset - 1; 
            	offset = 1;
            	isCopyUntilZero = true;
            }
            
            int length = nextPoint.getRuleIndex(isCurrentArea) - offset + 1;
            
            if (isCopyUntilZero) {
            	offset = 0;
            }
            
           	resultCoordPos = includeCoordsAndRules(offset, length, rules, 
           			                               offsets, resultRules, 
           			                               resultOffsets, resultCoords,
           			                               coords, resultRulesPos, 
           			                               resultCoordPos, point, 
           			                               isCurrentArea, true, 0);
            resultRulesPos += length - offset; 
            point = nextPoint;
        } while (point != intersectPoints[0]);
        
        resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
		this.coords = resultCoords;
		this.rules = resultRules;
		this.offsets = resultOffsets;
		this.coordsSize = resultCoordPos;
		this.rulesSize = resultRulesPos;
	}

    private void addPolygon(Area area) {
		CrossingHelper crossHelper = new CrossingHelper(new double[][] {coords,
				                                        area.coords }, 
				                                        new int[] {coordsSize, 
				                                        area.coordsSize });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0) {
			if (area.contains(getBounds2D())) {
				copy(area, this);
			} else if (!contains(area.getBounds2D())) {
				coords = adjustSize(coords, coordsSize + area.coordsSize);
				System.arraycopy(area.coords, 0, coords, coordsSize,
								 area.coordsSize);
				coordsSize += area.coordsSize;
				rules = adjustSize(rules, rulesSize + area.rulesSize);
				System.arraycopy(area.rules, 0, rules, rulesSize, 
								 area.rulesSize);
				rulesSize += area.rulesSize;
				offsets = adjustSize(offsets, rulesSize + area.rulesSize);
				System.arraycopy(area.offsets, 0, offsets, rulesSize, 
								 area.rulesSize);
			}
			return;
		}

        double[] resultCoords = new double[coordsSize + area.coordsSize + 
                                                       intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.getEndIndex(true);
            if (curIndex < 0) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], 
            		                      coords[2 * curIndex + 1]) > 0) { 
            	isCurrentArea = false;
            } else {
            	isCurrentArea = true;
            }

            IntersectPoint nextPoint = getNextIntersectPoint(intersectPoints, 
            		                                         point, 
            		                                         isCurrentArea);
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            int offset = 2 * point.getEndIndex(isCurrentArea);
 
            if ((offset >= 0) && 
            	    (nextPoint.getBegIndex(isCurrentArea) < 
            		    point.getEndIndex(isCurrentArea))) {
                int coordSize = (isCurrentArea) ? this.coordsSize : 
                	                              area.coordsSize;
                int length = coordSize - offset;
                System.arraycopy(coords, offset, 
                		         resultCoords, resultCoordPos, length);
                
                for (int i = 0; i < length / 2; i++) {
                	resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                	resultOffsets[resultRulesPos++] = resultCoordPos;
                	resultCoordPos += 2;
                }
                
                offset = 0;
            }
            
            if (offset >= 0) {
                int length = 2 * nextPoint.getBegIndex(isCurrentArea) - offset + 2;
                System.arraycopy(coords, offset, 
            		             resultCoords, resultCoordPos, length);
            
                for (int i = 0; i < length / 2; i++) {
            	    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            	    resultOffsets[resultRulesPos++] = resultCoordPos;
            	    resultCoordPos += 2;
                }
            }

            point = nextPoint;
        } while (point != intersectPoints[0]);
        
        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
		coords = resultCoords;
		rules = resultRules;
		offsets = resultOffsets;
		coordsSize = resultCoordPos;
		rulesSize = resultRulesPos;
	}
    
 	private void intersectCurvePolygon(Area area) {
		CurveCrossingHelper crossHelper = new CurveCrossingHelper(
				new double[][] {coords, area.coords }, 
				new int[] { coordsSize, area.coordsSize }, 
				new int[][] { rules, area.rules },
				new int[] { rulesSize, area.rulesSize }, 
				new int[][] { offsets, area.offsets });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0) {
			if (contains(area.getBounds2D())) {
				copy(area, this);
			} else if (!area.contains(getBounds2D())) {
				reset();
			}
			return;
		}

        double[] resultCoords = new double[coordsSize + area.coordsSize + 
                                                       intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        IntersectPoint nextPoint = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
 
            int curIndex = point.getEndIndex(true);
            if ((curIndex < 0) || (area.containsExact(
            		coords[2 * curIndex], coords[2 * curIndex + 1]) == 0)) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], 
            		                      coords[2 * curIndex + 1]) > 0) { 
            	isCurrentArea = true;
            } else {
            	isCurrentArea = false;
            }
            
            nextPoint = getNextIntersectPoint(intersectPoints, point, isCurrentArea);
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = point.getRuleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;
            
            if (point.getRuleIndex(isCurrentArea) > 
                    nextPoint.getRuleIndex(isCurrentArea)) {
            	int rulesSize = (isCurrentArea) ? this.rulesSize : 
            		                              area.rulesSize;
            	resultCoordPos = includeCoordsAndRules(offset + 1, rulesSize, 
            			                               rules, offsets, 
            			                               resultRules, 
            			                               resultOffsets, 
            			                               resultCoords, coords, 
            			                               resultRulesPos, 
            			                               resultCoordPos, point, 
            			                               isCurrentArea, false, 
            			                               1);
            	resultRulesPos += rulesSize - offset - 1; 
            	offset = 1;
            	isCopyUntilZero = true;
            }
            
            int length = nextPoint.getRuleIndex(isCurrentArea) - offset + 1;
            
            if (isCopyUntilZero) {
            	offset = 0;
            	isCopyUntilZero = false;
            }
            if ((length == offset) && 
            	(nextPoint.getRule(isCurrentArea) != PathIterator.SEG_LINETO) && 
                (nextPoint.getRule(isCurrentArea) != PathIterator.SEG_CLOSE) &&
            	(point.getRule(isCurrentArea) != PathIterator.SEG_LINETO) && 
            	(point.getRule(isCurrentArea) != PathIterator.SEG_CLOSE)) {
            	
            	isCopyUntilZero = true;
            	length++;
            }
            
           	resultCoordPos = includeCoordsAndRules(offset, length, rules, 
           			                               offsets, resultRules, 
           			                               resultOffsets, resultCoords, 
           			                               coords, resultRulesPos, 
           			                               resultCoordPos, nextPoint, 
           			                               isCurrentArea, true, 1);
            resultRulesPos = ((length <= offset) || (isCopyUntilZero)) ? 
            		resultRulesPos + 1 : resultRulesPos + length; 

            point = nextPoint;
        } while (point != intersectPoints[0]);
        
        if (resultRules[resultRulesPos - 1] == PathIterator.SEG_LINETO) {
        	resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        } else {
        	resultCoords[resultCoordPos++] = nextPoint.getX();
            resultCoords[resultCoordPos++] = nextPoint.getY();
        	resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        }
        
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
		coords = resultCoords;
		rules = resultRules;
		offsets = resultOffsets;
		coordsSize = resultCoordPos;
		rulesSize = resultRulesPos;
	}

	private void intersectPolygon(Area area) {
		CrossingHelper crossHelper = new CrossingHelper(new double[][] {coords, 
				                                        area.coords }, 
				                                        new int[] { coordsSize, 
				                                        area.coordsSize });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0) {
			if (contains(area.getBounds2D())) {
				copy(area, this);
			} else if (!area.contains(getBounds2D())) {
				reset();
			}
			return;
		}

        double[] resultCoords = new double[coordsSize + area.coordsSize + 
                                                        intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + 
                                                        intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + 
                                                        intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos; 
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.getEndIndex(true);

            if ((curIndex < 0) || 
            	(area.containsExact(coords[2 * curIndex], 
            		                coords[2 * curIndex + 1]) == 0)) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], 
            		                      coords[2 * curIndex + 1]) > 0) { 
            	isCurrentArea = true;
            } else {
            	isCurrentArea = false;
            }

            IntersectPoint nextPoint = getNextIntersectPoint(intersectPoints, 
            		                                         point, 
            		                                         isCurrentArea);
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            int offset = 2 * point.getEndIndex(isCurrentArea);
            if ((offset >= 0) && 
            		(nextPoint.getBegIndex(isCurrentArea) < 
            		    point.getEndIndex(isCurrentArea))) {
                int coordSize = (isCurrentArea) ? this.coordsSize : 
                	                              area.coordsSize;
                int length = coordSize - offset;
                System.arraycopy(coords, offset, 
                		         resultCoords, resultCoordPos, length);
                
                for (int i = 0; i < length / 2; i++) {
                	resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                	resultOffsets[resultRulesPos++] = resultCoordPos;
                	resultCoordPos += 2;
                }
                
                offset = 0;
            }
            
            if (offset >= 0) {
            	int length = 2 * nextPoint.getBegIndex(isCurrentArea) - 
            	                 offset + 2;
            	System.arraycopy(coords, offset, 
            			         resultCoords, resultCoordPos, length);
            	
            	for (int i = 0; i < length / 2; i++) {
            		resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            		resultOffsets[resultRulesPos++] = resultCoordPos;
            		resultCoordPos += 2;
            	}
            }

            point = nextPoint;
        } while (point != intersectPoints[0]);
        
        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
		coords = resultCoords;
		rules = resultRules;
		offsets = resultOffsets;
		coordsSize = resultCoordPos;
		rulesSize = resultRulesPos;
	}

	private void subtractCurvePolygon(Area area) {
		CurveCrossingHelper crossHelper = new CurveCrossingHelper(
				new double[][] { coords, area.coords }, 
				new int[] { coordsSize, area.coordsSize }, 
				new int[][] { rules, area.rules },
				new int[] { rulesSize, area.rulesSize }, 
				new int[][] { offsets, area.offsets });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0 && contains(area.getBounds2D())) {
			copy(area, this);
			return;
		}

        double[] resultCoords = new double[coordsSize + area.coordsSize + 
                                                       intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + 
                                                       intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
            int curIndex = offsets[point.getRuleIndex(true)] % coordsSize;
            
            if (area.containsExact(coords[curIndex], 
            		               coords[curIndex + 1]) == 0) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[curIndex],
            		                      coords[curIndex + 1]) > 0) { 
            	isCurrentArea = false;
            } else {
            	isCurrentArea = true;
            }
  
            IntersectPoint nextPoint = (isCurrentArea) ? 
            		getNextIntersectPoint(intersectPoints, point, 
            				              isCurrentArea):
            		getPrevIntersectPoint(intersectPoints, point, 
            				              isCurrentArea);	
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = (isCurrentArea) ? point.getRuleIndex(isCurrentArea) :
            	                         nextPoint.getRuleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;
         
            if (((isCurrentArea) && 
            	 (point.getRuleIndex(isCurrentArea) > 
            	  nextPoint.getRuleIndex(isCurrentArea))) ||
            	((!isCurrentArea) && 
            	 (nextPoint.getRuleIndex(isCurrentArea) > 
            	  nextPoint.getRuleIndex(isCurrentArea)))) {
            	
            	int rulesSize = (isCurrentArea) ? this.rulesSize : 
            		                              area.rulesSize;
            	resultCoordPos = includeCoordsAndRules(offset + 1, rulesSize, 
            			                               rules, offsets, 
            			                               resultRules, 
            			                               resultOffsets, 
            			                               resultCoords, coords, 
            			                               resultRulesPos, 
            			                               resultCoordPos, point, 
            			                               isCurrentArea, false, 
            			                               2);
            	resultRulesPos += rulesSize - offset - 1; 
            	offset = 1;
            	isCopyUntilZero = true;
            }
            
            int length = nextPoint.getRuleIndex(isCurrentArea) - offset + 1;
            
            if (isCopyUntilZero) {
            	offset = 0;
            	isCopyUntilZero = false;
            }
            
           	resultCoordPos = includeCoordsAndRules(offset, length, rules, 
           			                               offsets, resultRules, 
           			                               resultOffsets, resultCoords, 
           			                               coords, resultRulesPos, 
           			                               resultCoordPos, point, 
           			                               isCurrentArea, true, 2);
           	
           	if ((length == offset) && 
           		((rules[offset] == PathIterator.SEG_QUADTO) || 
           		 (rules[offset] == PathIterator.SEG_CUBICTO))) {
           		
           		resultRulesPos++;
    		} else {
           	    resultRulesPos = (length < offset || isCopyUntilZero) ? 
           	    		resultRulesPos + 1 : resultRulesPos + length - offset;
    		}

            point = nextPoint;
        } while (point != intersectPoints[0]);
        
        resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
		coords = resultCoords;
		rules = resultRules;
		offsets = resultOffsets;
		coordsSize = resultCoordPos;
		rulesSize = resultRulesPos;
	}

	private void subtractPolygon(Area area) {
		CrossingHelper crossHelper = new CrossingHelper(new double[][] {coords, 
				                                        area.coords }, 
				                                        new int[] { coordsSize, 
				                                        area.coordsSize });
		IntersectPoint[] intersectPoints = crossHelper.findCrossing();

		if (intersectPoints.length == 0) {
		    if (contains(area.getBounds2D())) {
		        copy(area, this);
		        return;
			} 
		    return;
		}

        double[] resultCoords = new double[2 * (coordsSize + area.coordsSize + 
                                                       intersectPoints.length)];
        int[] resultRules = new int[2 * (rulesSize + area.rulesSize + 
                                                       intersectPoints.length)];
        int[] resultOffsets = new int[2 * (rulesSize + area.rulesSize + 
                                                       intersectPoints.length)];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;
        int countPoints = 0;
        boolean curArea = false;
        boolean addArea = false;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;
        
        do {
        	resultCoords[resultCoordPos++] = point.getX();
            resultCoords[resultCoordPos++] = point.getY();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.getEndIndex(true);
            
            if ((curIndex < 0) || 
            		(area.isVertex(coords[2 * curIndex], coords[2 * curIndex + 1]) && 
            		     crossHelper.containsPoint(new double[] {coords[2 * curIndex], 
            				                       coords[2 * curIndex + 1]}) && 
            		(coords[2 * curIndex] != point.getX() || 
            			 coords[2 * curIndex + 1] != point.getY()))) {
            	isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], 
            		                      coords[2 * curIndex + 1]) > 0) { 
            	isCurrentArea = false;
            } else {
            	isCurrentArea = true;
            }
            
            if (countPoints >= intersectPoints.length) {
                isCurrentArea = !isCurrentArea;
            }
            	 
            if (isCurrentArea) {
                curArea = true;
            } else {
                addArea = true;
            }

            IntersectPoint nextPoint = (isCurrentArea) ? 
            		getNextIntersectPoint(intersectPoints, point, isCurrentArea):
            		getPrevIntersectPoint(intersectPoints, point, isCurrentArea);	
            double[] coords = (isCurrentArea) ? this.coords : area.coords;
            
            int offset = (isCurrentArea) ? 2 * point.getEndIndex(isCurrentArea): 
            							 2 * nextPoint.getEndIndex(isCurrentArea);
            
            if ((offset > 0) && 
            	(((isCurrentArea) && 
            	  (nextPoint.getBegIndex(isCurrentArea) < 
            			  point.getEndIndex(isCurrentArea))) ||
            	  ((!isCurrentArea) && 
            	  (nextPoint.getEndIndex(isCurrentArea) < 
            			  nextPoint.getBegIndex(isCurrentArea))))) {
            	
                int coordSize = (isCurrentArea) ? this.coordsSize : 
                	                              area.coordsSize;
                int length = coordSize - offset; 
                
                if (isCurrentArea) {
                	System.arraycopy(coords, offset, 
                			         resultCoords, resultCoordPos, length);
                } else {
                	double[] temp = new double[length];
                	System.arraycopy(coords, offset, temp, 0, length);
                	reverseCopy(temp);
                	System.arraycopy(temp, 0, 
                			         resultCoords, resultCoordPos, length);
                }
                
                for (int i = 0; i < length / 2; i++) {
                	resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                	resultOffsets[resultRulesPos++] = resultCoordPos;
                	resultCoordPos += 2;
                }
                
                offset = 0;
            }
            
            if (offset >= 0) {
            	int length = (isCurrentArea) ? 
            			         2 * nextPoint.getBegIndex(isCurrentArea) - offset + 2:
            	                 2 * point.getBegIndex(isCurrentArea) - offset + 2;
            			         
            	if (isCurrentArea) {
            		System.arraycopy(coords, offset, 
            				         resultCoords, resultCoordPos, length);
            	} else {
            		double[] temp = new double[length];
            		System.arraycopy(coords, offset, temp, 0, length);
            		reverseCopy(temp);
            		System.arraycopy(temp, 0, 
            				         resultCoords, resultCoordPos, length);
            	}
            	
            	for (int i = 0; i < length / 2; i++) {
            		resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            		resultOffsets[resultRulesPos++] = resultCoordPos;
            		resultCoordPos += 2;
            	}
            }

            point = nextPoint;
            countPoints++;
        } while (point != intersectPoints[0] || !(curArea && addArea));
        
        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
	    coords = resultCoords;
	    rules = resultRules;
	    offsets = resultOffsets;
	    coordsSize = resultCoordPos;
	    rulesSize = resultRulesPos;
	}
	
	private IntersectPoint getNextIntersectPoint(IntersectPoint[] iPoints,
			                                        IntersectPoint isectPoint, 
			                                        boolean isCurrentArea) {
	    int endIndex = isectPoint.getEndIndex(isCurrentArea);
		if (endIndex < 0) {
			return iPoints[Math.abs(endIndex) - 1];
		}

		IntersectPoint firstIsectPoint = null;
		IntersectPoint nextIsectPoint = null;
		for (IntersectPoint point : iPoints) {
			int begIndex = point.getBegIndex(isCurrentArea);
			
			if (begIndex >= 0) {
				if (firstIsectPoint == null) {
					firstIsectPoint = point;
				} else if (begIndex < firstIsectPoint
						.getBegIndex(isCurrentArea)) {
					firstIsectPoint = point;
				}
			}

			if (endIndex <= begIndex) {
				if (nextIsectPoint == null) {
					nextIsectPoint = point;
				} else if (begIndex < 
						       nextIsectPoint.getBegIndex(isCurrentArea)) {
					nextIsectPoint = point;
				}
			}
		}

		return (nextIsectPoint != null) ? nextIsectPoint : firstIsectPoint;
	}

	private IntersectPoint getPrevIntersectPoint(IntersectPoint[] iPoints,
			                                     IntersectPoint isectPoint, 
			                                     boolean isCurrentArea) {

		int begIndex = isectPoint.getBegIndex(isCurrentArea);
		
		if (begIndex < 0) {
			return iPoints[Math.abs(begIndex) - 1];
		}

		IntersectPoint firstIsectPoint = null;
		IntersectPoint predIsectPoint = null;
		for (IntersectPoint point : iPoints) {
			int endIndex = point.getEndIndex(isCurrentArea);
			
			if (endIndex >= 0) {
				if (firstIsectPoint == null) {
					firstIsectPoint = point;
				} else if (endIndex < firstIsectPoint
						.getEndIndex(isCurrentArea)) {
					firstIsectPoint = point;
				}
			}

			if (endIndex <= begIndex) {
				if (predIsectPoint == null) {
					predIsectPoint = point;
				} else if (endIndex > 
				               predIsectPoint.getEndIndex(isCurrentArea)) {
					predIsectPoint = point;
				}
			}
		}

		return (predIsectPoint != null) ? predIsectPoint : firstIsectPoint;
	}

	
	private int includeCoordsAndRules(int offset, int length, int[] rules,
			                          int[] offsets, int[] resultRules, 
			                          int[] resultOffsets, double[] resultCoords, 
			                          double[] coords, int resultRulesPos,
			                          int resultCoordPos, IntersectPoint point, 
			                          boolean isCurrentArea, boolean way, 
			                          int operation) {

		double[] temp = new double[8 * length];
		int coordsCount = 0;
		boolean isMoveIndex = true;
		boolean isMoveLength = true;
		boolean additional = false;

		if (length <= offset) {
			for (int i = resultRulesPos; i < resultRulesPos + 1; i++) {
				resultRules[i] = PathIterator.SEG_LINETO;
			}
		} else {
			int j = resultRulesPos;
			for (int i = offset; i < length; i++) {
				resultRules[j++] = PathIterator.SEG_LINETO;
			}
		}

		if ((length == offset) &&
			((rules[offset] == PathIterator.SEG_QUADTO) || 
			 (rules[offset] == PathIterator.SEG_CUBICTO))) {
			length++;
			additional = true;
		}
		for (int i = offset; i < length; i++) {
			int index = offsets[i];
			
			if (!isMoveIndex) {
				index -= 2;
			}
			
			if (!isMoveLength) {
				length++;
				isMoveLength = true;
			}
			
			switch (rules[i]) {
			    case PathIterator.SEG_MOVETO:
			    	isMoveIndex = false;
			    	isMoveLength = false;
				    break;
			    case PathIterator.SEG_LINETO:
			    case PathIterator.SEG_CLOSE:
				    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
				    resultOffsets[resultRulesPos++] = resultCoordPos + 2;
				    boolean isLeft = CrossingHelper.compare(coords[index],
						    coords[index + 1], point.getX(), point.getY()) > 0;
						    
				    if (way || !isLeft) {
					    temp[coordsCount++] = coords[index];
					    temp[coordsCount++] = coords[index + 1];
				    }
				    break;
			    case PathIterator.SEG_QUADTO:
				    resultRules[resultRulesPos] = PathIterator.SEG_QUADTO;
				    resultOffsets[resultRulesPos++] = resultCoordPos + 4;
				    double[] coefs = new double[] { coords[index - 2],
						    coords[index - 1], coords[index], coords[index + 1],
						    coords[index + 2], coords[index + 3] };
				    isLeft = CrossingHelper.compare(coords[index - 2],
						    coords[index - 1], point.getX(), point.getY()) > 0;
						    
				    if ((!additional) && (operation == 0 || operation == 2)) {
					    isLeft = !isLeft;
					    way = false;
				    }
				    GeometryUtil
						.subQuad(coefs, point.getParam(isCurrentArea), isLeft);
				    
				    if (way || isLeft) {
					    temp[coordsCount++] = coefs[2];
					    temp[coordsCount++] = coefs[3];
				    } else {
					    System.arraycopy(coefs, 2, temp, coordsCount, 4);
					    coordsCount += 4;
				    }
				    break;
			    case PathIterator.SEG_CUBICTO:
				    resultRules[resultRulesPos] = PathIterator.SEG_CUBICTO;
				    resultOffsets[resultRulesPos++] = resultCoordPos + 6;
				    coefs = new double[] {coords[index - 2], coords[index - 1],
						                  coords[index], coords[index + 1], 
						                  coords[index + 2], coords[index + 3], 
						                  coords[index + 4], coords[index + 5] };
				    isLeft = CrossingHelper.compare(coords[index - 2],
						    coords[index - 1], point.getX(), point.getY()) > 0;
				    GeometryUtil.subCubic(coefs, point.getParam(isCurrentArea),
						                  !isLeft);
				    
				    if (isLeft) {
					    System.arraycopy(coefs, 2, temp, coordsCount, 6);
					    coordsCount += 6;
				    } else {
					    System.arraycopy(coefs, 2, temp, coordsCount, 4);
					    coordsCount += 4;
				    }
				    break;
		    }
		}

        if (operation == 2 && !isCurrentArea && coordsCount > 2) {
			reverseCopy(temp);
			System.arraycopy(temp, 0, resultCoords, resultCoordPos, coordsCount);
		} else {
			System.arraycopy(temp, 0, resultCoords, resultCoordPos, coordsCount);
		}
        
		return (resultCoordPos + coordsCount);
	}
	
	// the method check up the array size and necessarily increases it. 
	private static double[] adjustSize(double[] array, int newSize) {
		if (newSize <= array.length) {
			return array;
		}
		double[] newArray = new double[2 * newSize];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

	private static int[] adjustSize(int[] array, int newSize) {
		if (newSize <= array.length) {
			return array;
		}
		int[] newArray = new int[2 * newSize];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

	private void copy(Area src, Area dst) {
		dst.coordsSize = src.coordsSize;
		dst.coords = new double[src.coords.length];
		for (int i = 0; i < src.coords.length; i++)
			dst.coords[i] = src.coords[i];
		//AR src.coords.clone();
		dst.rulesSize = src.rulesSize;
		dst.rules = new int[src.rules.length];
		for (int i = 0; i < src.rules.length; i++)
			dst.rules[i] = src.rules[i];
		//AR src.rules.clone();
		dst.moveToCount = src.moveToCount;
		dst.offsets = new int[src.offsets.length];
		for (int i = 0; i < src.offsets.length; i++)
			dst.offsets[i] = src.offsets[i];
		//AR src.offsets.clone();
	}

    private int containsExact(double x, double y) {
        PathIterator pi = getPathIterator(null);
        int crossCount = Crossing.crossPath(pi, x, y);
        
        if (Crossing.isInsideEvenOdd(crossCount)) {
            return 1;
        }

        double[] segmentCoords = new double[6];
        double[] resultPoints = new double[6];
        int rule;
        double curX = -1;
        double curY = -1;
        double moveX = -1;
        double moveY = -1;
        
        for (pi = getPathIterator(null); !pi.isDone(); pi.next()) {
            rule = pi.currentSegment(segmentCoords);
            switch (rule) {
                case PathIterator.SEG_MOVETO:
                    moveX = curX = segmentCoords[0];
                    moveY = curY = segmentCoords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    if (GeometryUtil.intersectLines(curX, curY, 
                    		segmentCoords[0], segmentCoords[1], x, y, x, y, 
                    		resultPoints) != 0) {
                        return 0;
                    }
                    curX = segmentCoords[0];
                    curY = segmentCoords[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    if (GeometryUtil.intersectLineAndQuad(x, y, x, y, 
                    		curX, curY, segmentCoords[0], segmentCoords[1], 
                    		segmentCoords[2], segmentCoords[3], 
                    		resultPoints) > 0) {
                        return 0;
                    }
                    curX = segmentCoords[2];
                    curY = segmentCoords[3];
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (GeometryUtil.intersectLineAndCubic(x, y, x, y, 
                    		curX, curY, segmentCoords[0], segmentCoords[1], 
                    		segmentCoords[2], segmentCoords[3], segmentCoords[4], 
                    		segmentCoords[5], resultPoints) > 0) {
                        return 0;
                    }
                    curX = segmentCoords[4];
                    curY = segmentCoords[5];
                    break;
                case PathIterator.SEG_CLOSE:
                    if (GeometryUtil.intersectLines(curX, curY, moveX, moveY,
                    		x, y, x, y, resultPoints) != 0) {
                        return 0;
                    }
                    curX = moveX;
                    curY = moveY;
                    break;
            }
        }
        return -1;
    }

    private void reverseCopy(double[] coords) {
    	double[] temp = new double[coords.length];
    	System.arraycopy(coords, 0, temp, 0, coords.length);
    	
    	for (int i = 0; i < coords.length;) {
    		coords[i] = temp[coords.length - i - 2];
    		coords[i + 1] = temp[coords.length - i - 1]; 
    		i = i + 2;
    	}
    }
    
    private double getAreaBoundsSquare() {
        Rectangle2D bounds = getBounds2D();
        return bounds.getHeight() * bounds.getWidth();
    }

    private boolean isVertex(double x, double y) {
        for (int i = 0; i < coordsSize;) {
    	    if (x == coords[i++] && y == coords[i++]) {
    		    return true;
    		}
    	}
    	return false;
    }

    // the internal class implements PathIterator
	private class AreaPathIterator implements PathIterator {

		AffineTransform transform;
		Area area;
		int curRuleIndex = 0;
		int curCoordIndex = 0;

		AreaPathIterator(Area area) {
			this(area, null);
		}

		AreaPathIterator(Area area, AffineTransform t) {
			this.area = area;
			this.transform = t;
		}

		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		public boolean isDone() {
			return curRuleIndex >= rulesSize;
		}

		public void next() {
			switch (rules[curRuleIndex]) {
			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_LINETO:
				curCoordIndex += 2;
				break;
			case PathIterator.SEG_QUADTO:
				curCoordIndex += 4;
				break;
			case PathIterator.SEG_CUBICTO:
				curCoordIndex += 6;
				break;
			}
			curRuleIndex++;
		}

		public int currentSegment(double[] c) {
			if (isDone()) {
				throw new NoSuchElementException(/*AR Messages.getString(*/"awt.4B"/*AR )*/); //$NON-NLS-1$
			}
			
			int count = 0;
			
			switch (rules[curRuleIndex]) {
				case PathIterator.SEG_CUBICTO:
					c[4] = coords[curCoordIndex + 4];
					c[5] = coords[curCoordIndex + 5];
					count = 1;
				case PathIterator.SEG_QUADTO:
					c[2] = coords[curCoordIndex + 2];
					c[3] = coords[curCoordIndex + 3];
					count += 1;
				case PathIterator.SEG_MOVETO:
				case PathIterator.SEG_LINETO:
					c[0] = coords[curCoordIndex];
					c[1] = coords[curCoordIndex + 1];
					count += 1;
			}
			
			if(transform != null) {
	            transform.transform(c, 0, c, 0, count);
			}
			
			return rules[curRuleIndex];
		}

		public int currentSegment(float[] c) {
			double[] doubleCoords = new double[6];
			int rule = currentSegment(doubleCoords);
			
			for (int i = 0; i < 6; i++) {
				c[i] = (float) doubleCoords[i];
			}
			return rule;
		}
	}
}
