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

package geogebra.html5.gawt;


public class GeometryUtil {
    public static final double EPSILON = Math.pow(10, -14);

    public static int intersectLinesWithParams(double x1, double y1, double x2, double y2,
                                               double x3, double y3, double x4, double y4,
                                               double[] params) {
        double dx = x4 - x3;
        double dy = y4 - y3;
        double d = dx * (y2 - y1) - dy * (x2 - x1);
        // double comparison
        if (Math.abs(d) < EPSILON) {
            return 0;
        }

        params[0] = (- dx * (y1 - y3) + dy * (x1 - x3)) / d;
        
        if (dx != 0) {
            params[1] = (line(params[0], x1, x2) - x3) / dx;
        } else if (dy != 0) {
            params[1] = (line(params[0], y1, y2) - y3) / dy;
        } else {
            params[1] = 0.0;
        }
        
        if (params[0] >= 0 && params[0] <= 1 && params[1] >= 0 && params[1] <= 1) {
            return 1;
        }
        
        return 0;
    }
    
    /**
     * The method checks up if line (x1, y1) - (x2, y2) and line (x3, y3) - (x4, y4)
     * intersect. If lines intersect then the result parameters are saved to point
     * array. The size of array point must be at least 2.
     * @return the method returns 1 if two lines intersect in the defined interval,  
     * 			otherwise 0
     */
    public static int intersectLines(double x1, double y1, double x2, double y2,
                                     double x3, double y3, double x4, double y4,
                                     double[] point) {
        double A1 = -(y2 - y1);
        double B1 = (x2 - x1);
        double C1 = x1 * y2 - x2 *  y1;
        double A2 = - (y4 - y3);
        double B2 = (x4 - x3);
        double C2 = x3 * y4 - x4 * y3;
        double coefParallel = A1 * B2 - A2 * B1;
        // double comparison
        if (x3 == x4 && y3 == y4 && (A1 * x3 + B1 * y3 + C1 == 0) && 
        		(x3 >= Math.min(x1, x2)) && (x3 <= Math.max(x1, x2)) && 
        		(y3 >= Math.min(y1, y2)) && (y3 <= Math.max(y1, y2))) {
        	return 1;
        }
        if (Math.abs(coefParallel) < EPSILON) {
            return 0;
        }
        point[0] = (B1 * C2 - B2 * C1) / coefParallel;
        point[1] = (A2 * C1 - A1 * C2) / coefParallel;
        if (point[0] >= Math.min(x1, x2) && point[0] >= Math.min(x3, x4) && 
        	point[0] <= Math.max(x1, x2) && point[0] <= Math.max(x3, x4) && 
        	point[1] >= Math.min(y1, y2) && point[1] >= Math.min(y3, y4) && 
            point[1] <= Math.max(y1, y2) && point[1] <= Math.max(y3, y4)) {
            return 1;
        }
        return 0;
    }

    /**
     * It checks up if there is intersection of the line (x1, y1) - (x2, y2) and
     * the quad curve (qx1, qy1) - (qx2, qy2) - (qx3, qy3). The parameters of the intersection
     * area saved to params array. Therefore the params size must be at learst 4.
     * @return The method returns the quantity of roots lied in the defined interval 
     */
    public static int intersectLineAndQuad(double x1, double y1, double x2, double y2,
                                           double qx1, double qy1, double qx2, double qy2, 
                                           double qx3, double qy3, double[] params) {
        double[] eqn = new double[3];
        double[] t = new double[2];
        double[] s = new double[2];
        double dy = y2 - y1;
        double dx = x2 - x1;
        int quantity = 0;
        int count = 0;

        eqn[0] = dy * (qx1 - x1) - dx * (qy1 - y1);
        eqn[1] = 2 * dy * (qx2 - qx1) - 2 * dx * (qy2 - qy1);
        eqn[2] = dy * (qx1 - 2 * qx2 + qx3) - dx *(qy1 -2 * qy2 + qy3);
        
        if ((count = Crossing.solveQuad(eqn, t)) == 0) {
            return 0;
        }

        for (int i = 0; i < count; i++) {
            if (dx != 0) {
                s[i] = (quad(t[i], qx1, qx2, qx3) - x1) / dx;
            } else if (dy != 0) {
                s[i] = (quad(t[i], qy1, qy2, qy3) - y1) / dy;
            } else {
            	s[i] = 0.0;
            }
            if (t[i] >= 0 && t[i] <= 1 && s[i] >= 0 && s[i] <= 1) {
                params[2 * quantity] = t[i];
                params[2 * quantity + 1] = s[i];
                ++quantity;
            }
        }

        return quantity;
    }

    /**
     * It checks up if the line (x1, y1) - (x2, y2) and
     * the cubic curve (cx1, cy1) - (cx2, cy2) - (cx3, cy3) - (cx4, cy4). 
     * The points of the intersection is saved to points array. 
     * Therefore the points size must be at learst 6. 
     * @return The method returns the quantity of roots lied in the defined interval 
     */
    public static int intersectLineAndCubic(double x1, double y1, double x2, double y2,
                                            double cx1, double cy1, double cx2, double cy2,
                                            double cx3, double cy3, double cx4, double cy4,
                                            double[] params) {
        double[] eqn = new double[4];
        double[] t = new double[3];
        double[] s = new double[3];
        double dy = y2 - y1;
        double dx = x2 - x1;
        int quantity = 0;
        int count = 0;

        eqn[0] = (cy1 - y1) * dx + (x1 - cx1) * dy;
        eqn[1] = - 3 * (cy1 - cy2) * dx + 3 * (cx1 - cx2) * dy ;
        eqn[2] = (3 * cy1 - 6 * cy2 + 3 * cy3) * dx - (3 * cx1 - 6 * cx2 + 3 * cx3) * dy;
        eqn[3] = (- 3 * cy1 + 3 * cy2 - 3 * cy3 + cy4) * dx + 
        		 (3 * cx1 - 3 * cx2 + 3 * cx3 - cx4) * dy;

        if ((count = Crossing.solveCubic(eqn, t)) == 0) {
            return 0;
        }
        
        for (int i = 0; i < count; i++) {
            if (dx != 0) {
                s[i] = (cubic(t[i], cx1, cx2, cx3, cx4) - x1) / dx;
            } else if (dy != 0) {
                s[i] = (cubic(t[i], cy1, cy2, cy3, cy4) - y1) / dy;
            } else {
            	s[i] = 0.0;
            }
            if (t[i] >= 0 && t[i] <= 1 && s[i] >= 0 && s[i] <= 1) {
                params[2 * quantity] = t[i];
                params[2 * quantity + 1] = s[i];
                ++quantity;
            }
        }

        return quantity;
    }

    /**
     * The method checks up if two quads (x1, y1) - (x2, y2) - (x3, y3) and 
     * (qx1, qy1) - (qx2, qy2) - (qx3, qy3) intersect. The result is saved to 
     * point array. Size of points should be at learst 4. 
     * @return the method returns the quantity of roots lied in the interval
     */
    public static int intersectQuads(double x1, double y1, double x2, double y2,
                                     double x3, double y3, double qx1, double qy1,
                                     double qx2, double qy2, double qx3, double qy3,
                                     double[] params) {
 
    	double[] initParams = new double[2];
        double[] xCoefs1 = new double[3];
    	double[] yCoefs1 = new double[3];
    	double[] xCoefs2 = new double[3];
    	double[] yCoefs2 = new double[3];
    	int quantity = 0;
    	
    	xCoefs1[0] = x1 - 2 * x2 + x3;
    	xCoefs1[1] = - 2 * x1 + 2 * x2;
    	xCoefs1[2] = x1;
    	
    	yCoefs1[0] = y1 - 2 * y2 + y3;
    	yCoefs1[1] = - 2 * y1 + 2 * y2;
    	yCoefs1[2] = y1;
    	
    	xCoefs2[0] = qx1 - 2 * qx2 + qx3;
    	xCoefs2[1] = - 2 * qx1 + 2 * qx2;
    	xCoefs2[2] = qx1;
    	
    	yCoefs2[0] = qy1 - 2 * qy2 + qy3;
    	yCoefs2[1] = - 2 * qy1 + 2 * qy2;
    	yCoefs2[2] = qy1;
    	
    	// initialize params[0] and params[1]
        params[0] = params[1] = 0.25;
        quadNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
    	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
    	// initialize params
        params[0] = params[1] = 0.75;
        quadNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
            	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        return quantity;
    }

    /**
     * It checks up if the quad (x1, y1) - (x2, y2) - (x3, y3) and
     * the cubic (cx1, cy1) - (cx2, cy2) - (cx3, cy3) - (cx4, cy4) curves intersect. 
     * The points of the intersection is saved to points array. 
     * The points size should be at learst 6. 
     * @return The method returns the quantity of the intersection points 
     * 		   lied in the interval. 
     */
    public static int intersectQuadAndCubic(double qx1, double qy1, double qx2, double qy2,
                                            double qx3, double qy3, double cx1, double cy1,
                                            double cx2, double cy2, double cx3, double cy3,
                                            double cx4, double cy4,
                                            double[] params) {
    	int quantity = 0;
        double[] initParams = new double[3];
        double[] xCoefs1 = new double[3];
    	double[] yCoefs1 = new double[3];
    	double[] xCoefs2 = new double[4];
    	double[] yCoefs2 = new double[4];
    	xCoefs1[0] = qx1 - 2 * qx2 + qx3;
    	xCoefs1[1] = 2* qx2 - 2 * qx1;
    	xCoefs1[2] = qx1;

        yCoefs1[0] = qy1 - 2 * qy2 + qy3;
    	yCoefs1[1] = 2* qy2 - 2 * qy1;
    	yCoefs1[2] = qy1;

        xCoefs2[0] = - cx1 + 3 * cx2 - 3 * cx3 + cx4;
    	xCoefs2[1] = 3 * cx1 - 6 * cx2 + 3 * cx3;
    	xCoefs2[2] = - 3 * cx1 + 3 * cx2;
        xCoefs2[3] = cx1;

        yCoefs2[0] = - cy1 + 3 * cy2 - 3 * cy3 + cy4;
    	yCoefs2[1] = 3 * cy1 - 6 * cy2 + 3 * cy3;
    	yCoefs2[2] = - 3 * cy1 + 3 * cy2;
        yCoefs2[3] = cy1;

        // initialize params[0] and params[1]
        params[0] = params[1] = 0.25;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
    	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
    	// initialize params
        params[0] = params[1] = 0.5;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
            	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        params[0] = params[1] = 0.75;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
            	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        return quantity;
    }

    /**
     * The method checks up if two cubic curves (x1, y1) - (x2, y2) - (x3, y3) - (x4, y4) 
     * and (cx1, cy1) - (cx2, cy2) - (cx3, cy3) - (cx4, cy4) intersect. The result is saved to 
     * point array. Size of points should be at learst 6. 
     * @return the method returns the quantity of the intersection points lied in the interval
     */
    public static int intersectCubics(double x1, double y1, double x2, double y2,
                                      double x3, double y3, double x4, double y4,
                                      double cx1, double cy1, double cx2, double cy2,
                                      double cx3, double cy3, double cx4, double cy4,
                                      double[] params) {
 
    	int quantity = 0;
        double[] initParams = new double[3];
        double[] xCoefs1 = new double[4];
    	double[] yCoefs1 = new double[4];
    	double[] xCoefs2 = new double[4];
    	double[] yCoefs2 = new double[4];
    	xCoefs1[0] = - x1 + 3 * x2 - 3 * x3 + x4;
    	xCoefs1[1] = 3 * x1 - 6 * x2 + 3 * x3;
    	xCoefs1[2] = - 3 * x1 + 3 * x2;
        xCoefs1[3] = x1;

        yCoefs1[0] = - y1 + 3 * y2 - 3 * y3 + y4;
    	yCoefs1[1] = 3 * y1 - 6 * y2 + 3 * y3;
    	yCoefs1[2] = - 3 * y1 + 3 * y2;
        yCoefs1[3] = y1;

        xCoefs2[0] = - cx1 + 3 * cx2 - 3 * cx3 + cx4;
    	xCoefs2[1] = 3 * cx1 - 6 * cx2 + 3 * cx3;
    	xCoefs2[2] = - 3 * cx1 + 3 * cx2;
        xCoefs2[3] = cx1;

        yCoefs2[0] = - cy1 + 3 * cy2 - 3 * cy3 + cy4;
    	yCoefs2[1] = 3 * cy1 - 6 * cy2 + 3 * cy3;
    	yCoefs2[2] = - 3 * cy1 + 3 * cy2;
        yCoefs2[3] = cy1;

        // TODO
        params[0] = params[1] = 0.25;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
    	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
  
    	params[0] = params[1] = 0.5;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
            	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        params[0] = params[1] = 0.75;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
            	if (initParams[0] <= 1 && initParams[0] >= 0 &&
                initParams[1] >=0 && initParams[1] <=1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        return quantity;
    }

    public static double line(double t, double x1, double x2) {
        return x1 * (1.0 - t) + x2 * t;
    }

    public static double quad(double t, double x1, double x2, double x3) {
        return x1 * (1.0 - t) * (1.0 - t) + 2.0 * x2 * t * (1.0 - t) + x3 * t * t;
    }

    public static double cubic(double t, double x1, double x2, double x3, double x4) {
        return x1 * (1.0 - t) * (1.0 - t) * (1.0 - t) +
                3.0 * x2 * (1.0 - t) * (1.0 - t) * t +
                3.0 * x3 * (1.0 - t) * t * t +
                x4 * t * t * t;
    }
    
    // x, y - the coordinates of new vertex
    // t0 - ?
    public static void subQuad(double coef[], double t0, boolean left) {
    	if (left) {
    		coef[2] = (1 - t0) * coef[0] + t0 * coef[2];
    		coef[3] = (1 - t0) * coef[1] + t0 * coef[3];
    	} else {
    		coef[2] = (1 - t0) * coef[2] + t0 * coef[4];
    		coef[3] = (1 - t0) * coef[3] + t0 * coef[5];
    	}
    }
    
    public static void subCubic(double coef[], double t0, boolean left) {
    	if (left) {
    		coef[2] = (1 - t0) * coef[0] + t0 * coef[2];
    		coef[3] = (1 - t0) * coef[1] + t0 * coef[3];
    	} else {
    		coef[4] = (1 - t0) * coef[4] + t0 * coef[6];
    		coef[5] = (1 - t0) * coef[5] + t0 * coef[7];
    	}
    }
    
    private static void cubicNewton(double xCoefs1[], double yCoefs1[], double xCoefs2[], 
    								double yCoefs2[], double[] params) {
    	double t = 0.0, s = 0.0;
    	double t1 = params[0];
        double s1 = params[1];
    	double d, dt, ds;
        
        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
    		d = -(3 * t * t * xCoefs1[0] + 2 * t * xCoefs1[1] + xCoefs1[2]) * 
    			(3 * s * s * yCoefs2[0] + 2 * s * yCoefs2[1] + yCoefs2[2]) +
    		    (3 * t * t * yCoefs1[0] + 2 * t * yCoefs1[1] + yCoefs1[2]) *
    			(3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

            dt = (t * t * t * xCoefs1[0] + t * t * xCoefs1[1] + t * xCoefs1[2] +
    			  xCoefs1[3] - s * s * s * xCoefs2[0] - s * s * xCoefs2[1] - 
    			  s * xCoefs2[2] - xCoefs2[3]) * (- 3 * s * s * yCoefs2[0] - 
    			  2 * s * yCoefs2[1] - yCoefs2[2]) + (t * t * t * yCoefs1[0] + 
    			  t * t * yCoefs1[1] + t * yCoefs1[2] + yCoefs1[3] - s * s *s * yCoefs2[0] - 
    			  s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) * 
    			 (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);
    		
    		ds = (3 * t * t * xCoefs1[0] + 2 * t * xCoefs1[1] + xCoefs1[2]) *
    		     (t * t * t * yCoefs1[0] + t * t * yCoefs1[1] + t * yCoefs1[2] + 
    		      yCoefs1[3] - s * s * s * yCoefs2[0] - s * s * yCoefs2[1] - 
    		      s * yCoefs2[2] - yCoefs2[3]) - (3 * t * t * yCoefs1[0] + 
    		      2 * t * yCoefs1[1] + yCoefs1[2]) * (t * t * t * xCoefs1[0] + 
    		      t * t * xCoefs1[1] + t * xCoefs1[2] + xCoefs1[3] - 
    		      s * s * s * xCoefs2[0] - s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]);
    		
    		t1 = t - dt / d;
    		s1 = s - ds / d;
    	}
        params[0] = t1;
        params[1] = s1;
    }

    private static void quadAndCubicNewton(double xCoefs1[], double yCoefs1[], 
    		                               double xCoefs2[], double yCoefs2[],
                                           double[] params) {
    	double t = 0.0, s = 0.0;
    	double t1 = params[0];
        double s1 = params[1];
    	double d, dt, ds;
        
        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
    		d = -(2 *t * xCoefs1[0] + xCoefs1[1]) *
    			(3 * s * s * yCoefs2[0] + 2 * s * yCoefs2[1] + yCoefs2[2]) +
                (2 *t * yCoefs1[0] + yCoefs1[1]) *
    			(3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2])    ;

    		dt = (t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[2] +
    			  - s * s * s * xCoefs2[0] - s * s * xCoefs2[1] -
    			  s * xCoefs2[2] - xCoefs2[3]) * (- 3 * s * s * yCoefs2[0] -
    			  2 * s * yCoefs2[1] - yCoefs2[2]) + (t * t * yCoefs1[0] +
    			  t * yCoefs1[1] + yCoefs1[2] - s * s *s * yCoefs2[0] -
    			  s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) *
    			 (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

    		ds = (2 * t * xCoefs1[0] + xCoefs1[1]) *
    		     (t * t * yCoefs1[0] + t * yCoefs1[1] + yCoefs1[2] -
                  s * s * s * yCoefs2[0] - s * s * yCoefs2[1] -
    		      s * yCoefs2[2] - yCoefs2[3]) - (2 * t * yCoefs1[0] +
    		      yCoefs1[1]) * (t * t * xCoefs1[0] +
    		      t * xCoefs1[1] + xCoefs1[2] - s * s * s * xCoefs2[0] -
                  s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]);

    		t1 = t - dt / d;
    		s1 = s - ds / d;
    	}
        params[0] = t1;
        params[1] = s1;
    }

    private static void quadNewton(double xCoefs1[], double yCoefs1[], double xCoefs2[],
    							   double yCoefs2[], double params[]) {
    	double t = 0.0, s = 0.0;
    	double t1 = params[0];
    	double s1 = params[1];
    	double d, dt, ds;
        
        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
    		t = t1;
    		s = s1;
    		d = - (2 * t * xCoefs1[0] + xCoefs1[1]) * (2 * s * yCoefs2[0] + yCoefs2[1]) + 
    			(2 * s * xCoefs2[0] + xCoefs2[1]) * (2 * t * yCoefs1[0] + yCoefs1[1]);
    		
    		dt = - (t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[1] - s * s * xCoefs2[0] - 
    			 s * xCoefs2[1] -xCoefs2[2]) * (2 * s * yCoefs2[0] + yCoefs2[1]) + 
    			 (2 * s * xCoefs2[0] + xCoefs2[1]) * (t * t * yCoefs1[0] + t * yCoefs1[1] + 
    			  yCoefs1[2] - s * s * yCoefs2[0] - s * yCoefs2[1] - yCoefs2[2]);
    		
    		ds = (2 * t * xCoefs1[0] + xCoefs1[1]) * (t * t * yCoefs1[0] + t * yCoefs1[1] + 
    			  yCoefs1[2] - s * s * yCoefs2[0] - s * yCoefs2[1] - yCoefs2[2]) - 
    			  (2 * t * yCoefs1[0] + yCoefs1[1]) * (t * t * xCoefs1[0] + t * xCoefs1[1] + 
    			  xCoefs1[2] - s * s * xCoefs2[0] - s * xCoefs2[1] - xCoefs2[2]);
    		
    		t1 = t - dt / d;
    		s1 = s - ds / d;
    	}
    	params[0] = t1;
    	params[1] = s1;
    }
    
}
