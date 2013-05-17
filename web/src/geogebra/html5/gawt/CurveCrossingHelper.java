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


import geogebra.web.openjdk.awt.geom.PathIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurveCrossingHelper {
    private double[][] coords;
    private int[][] rules;
    private int[] sizes;
    private int[] rulesSizes;
    private int[][] offsets;
    private List<IntersectPoint> isectPoints = new ArrayList<IntersectPoint>();

    public CurveCrossingHelper(double[][] coords, int[] sizes,
            int[][] rules, int[] rulesSizes,
            int[][] offsets) {
        this.coords = coords;
        this.rules = rules;
        this.sizes = sizes;
        this.rulesSizes = rulesSizes;
        this.offsets = offsets;
    }

    public IntersectPoint[] findCrossing() {
        double[] edge1 = new double[8];
        double[] edge2 = new double[8];
        double[] points = new double[6];
        double[] params = new double[6];
        double[] mp1 = new double[2];
        double[] cp1 = new double[2];
        double[] mp2 = new double[2];
        double[] cp2 = new double[2];
        int rule1, rule2, endIndex1, endIndex2;
        int ipCount = 0;

        for (int i = 0; i < rulesSizes[0]; i++) {
            rule1 = rules[0][i];
            endIndex1 = getCurrentEdge(0, i, edge1, mp1, cp1);
            for (int j = 0; j < rulesSizes[1]; j++) {
                ipCount = 0;
                rule2 = rules[1][j];
                endIndex2 = getCurrentEdge(1, j, edge2, mp2, cp2);
                if (((rule1 == PathIterator.SEG_LINETO) ||
                        (rule1 == PathIterator.SEG_CLOSE)) &&
                        ((rule2 == PathIterator.SEG_LINETO) ||
                                (rule2 == PathIterator.SEG_CLOSE))) {

                    ipCount = GeometryUtil.intersectLinesWithParams(
                            edge1[0], edge1[1], edge1[2], edge1[3],
                            edge2[0], edge2[1], edge2[2], edge2[3],
                            params);

                    if (ipCount != 0) {
                        points[0] = GeometryUtil.line(
                                params[0], edge1[0], edge1[2]);
                        points[1] = GeometryUtil.line(
                                params[0], edge1[1], edge1[3]);
                    }
                } else if (((rule1 == PathIterator.SEG_LINETO) ||
                        (rule1 == PathIterator.SEG_CLOSE)) &&
                        (rule2 == PathIterator.SEG_QUADTO)) {
                    ipCount = GeometryUtil.intersectLineAndQuad(
                            edge1[0], edge1[1], edge1[2],
                            edge1[3], edge2[0], edge2[1],
                            edge2[2], edge2[3], edge2[4],
                            edge2[5],  params);
                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.line(params[2*k], edge1[0], edge1[2]);
                        points[2*k + 1] = GeometryUtil.line(params[2*k], edge1[1], edge1[3]);
                    }
                } else if (rule1 == PathIterator.SEG_QUADTO &&
                        (rule2 == PathIterator.SEG_LINETO || rule2 == PathIterator.SEG_CLOSE)) {
                    ipCount = GeometryUtil.intersectLineAndQuad(
                            edge2[0], edge2[1], edge2[2],
                            edge2[3], edge1[0], edge1[1],
                            edge1[2], edge1[3], edge1[4],
                            edge1[5], params);
                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.line(
                                params[2*k + 1], edge2[0], edge2[2]);
                        points[2*k + 1] = GeometryUtil.line(
                                params[2*k + 1], edge2[1], edge2[3]);
                    }
                } else if ((rule1 == PathIterator.SEG_CUBICTO) &&
                        ((rule2 == PathIterator.SEG_LINETO) ||
                                (rule2 == PathIterator.SEG_CLOSE))) {
                    ipCount = GeometryUtil.intersectLineAndCubic(
                            edge1[0], edge1[1], edge1[2],
                            edge1[3], edge1[4], edge1[5],
                            edge1[6], edge1[7], edge2[0],
                            edge2[1], edge2[2], edge2[3],
                            params);

                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.line(
                                params[2*k + 1], edge2[0], edge2[2]);
                        points[2*k + 1] = GeometryUtil.line(
                                params[2*k + 1], edge2[1], edge2[3]);
                    }
                } else if (((rule1 == PathIterator.SEG_LINETO) ||
                        (rule1 == PathIterator.SEG_CLOSE)) &&
                        (rule2 == PathIterator.SEG_CUBICTO)) {
                    ipCount = GeometryUtil.intersectLineAndCubic(
                            edge1[0], edge1[1], edge1[2],
                            edge1[3], edge2[0], edge2[1],
                            edge2[2], edge2[3], edge2[4],
                            edge2[5], edge2[6], edge2[7],
                            params);

                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.line(
                                params[2*k], edge1[0], edge1[2]);
                        points[2*k + 1] = GeometryUtil.line(
                                params[2*k], edge1[1], edge1[3]);
                    }
                } else if ((rule1 == PathIterator.SEG_QUADTO) &&
                        (rule2 == PathIterator.SEG_QUADTO)) {
                    ipCount = GeometryUtil.intersectQuads(
                            edge1[0], edge1[1], edge1[2], edge1[3],
                            edge1[4], edge1[5], edge2[0], edge2[1],
                            edge2[2], edge2[3], edge2[4], edge2[5],
                            params);
                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.quad(
                                params[2*k], edge1[0], edge1[2], edge1[4]);
                        points[2*k + 1] = GeometryUtil.quad(
                                params[2*k], edge1[1], edge1[3], edge1[5]);
                    }
                } else if ((rule1 == PathIterator.SEG_QUADTO) &&
                        (rule2 == PathIterator.SEG_CUBICTO)) {
                    ipCount = GeometryUtil.intersectQuadAndCubic(
                            edge1[0], edge1[1], edge1[2],
                            edge1[3], edge1[4], edge1[5],
                            edge2[0], edge2[1], edge2[2],
                            edge2[3], edge2[4], edge2[5],
                            edge2[6], edge2[7], params);

                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.quad(
                                params[2*k], edge1[0], edge1[2], edge1[4]);
                        points[2*k + 1] = GeometryUtil.quad(
                                params[2*k], edge1[1], edge1[3], edge1[5]);
                    }
                } else if ((rule1 == PathIterator.SEG_CUBICTO) &&
                        (rule2 == PathIterator.SEG_QUADTO)) {
                    ipCount = GeometryUtil.intersectQuadAndCubic(
                            edge2[0], edge2[1], edge2[2],
                            edge2[3], edge2[4], edge2[5],
                            edge1[0], edge1[1], edge1[2],
                            edge1[3], edge1[4], edge1[5],
                            edge2[6], edge2[7], params);

                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.quad(
                                params[2*k + 1], edge2[0], edge2[2], edge2[4]);
                        points[2*k + 1] = GeometryUtil.quad(
                                params[2*k + 1], edge2[1], edge2[3], edge2[5]);
                    }
                } else if ((rule1 == PathIterator.SEG_CUBICTO) &&
                        (rule2 == PathIterator.SEG_CUBICTO)) {
                    ipCount = GeometryUtil.intersectCubics(
                            edge1[0], edge1[1], edge1[2], edge1[3],
                            edge1[4], edge1[5], edge1[6], edge1[7],
                            edge2[0], edge2[1], edge2[2], edge2[3],
                            edge2[4], edge2[5], edge2[6], edge2[7],
                            params);

                    for (int k = 0; k < ipCount; k++) {
                        points[2*k] = GeometryUtil.cubic(
                                params[2*k], edge1[0], edge1[2], edge1[4], edge1[6]);
                        points[2*k + 1] = GeometryUtil.cubic(
                                params[2*k], edge1[1], edge1[3], edge1[5], edge1[7]);
                    }
                }

                endIndex1 = i;
                endIndex2 = j;
                int begIndex1 = i - 1;
                int begIndex2 = j - 1;

                for (int k = 0; k < ipCount; k++) {
                    IntersectPoint ip = null;
                    if (!containsPoint(points[2*k], points[2*k + 1])) {
                        for (Iterator<IntersectPoint> iter = isectPoints.iterator();
                        iter.hasNext(); ) {
                            ip = iter.next();
                            if ((begIndex1 == ip.getBegIndex(true)) &&
                                    (endIndex1 == ip.getEndIndex(true))) {

                                if (ip.getParam(true) > params[2*k]) {
                                    endIndex1 = - (isectPoints.indexOf(ip) + 1);
                                    ip.setBegIndex1(-(isectPoints.size() + 1));
                                } else {
                                    begIndex1 = - (isectPoints.indexOf(ip) + 1);
                                    ip.setEndIndex1(-(isectPoints.size() + 1));
                                }
                            }

                            if ((begIndex2 == ip.getBegIndex(false)) &&
                                    (endIndex2 == ip.getEndIndex(false))) {

                                if (ip.getParam(false) > params[2*k + 1]) {
                                    endIndex2 = - (isectPoints.indexOf(ip) + 1);
                                    ip.setBegIndex2(-(isectPoints.size() + 1));
                                } else {
                                    begIndex2 = - (isectPoints.indexOf(ip) + 1);
                                    ip.setEndIndex2(-(isectPoints.size() + 1));
                                }
                            }
                        }

                        if (rule1 == PathIterator.SEG_CLOSE) {
                            rule1 = PathIterator.SEG_LINETO;
                        }

                        if (rule2 == PathIterator.SEG_CLOSE) {
                            rule2 = PathIterator.SEG_LINETO;
                        }

                        isectPoints.add(new IntersectPoint(begIndex1, endIndex1,
                                rule1, i,
                                begIndex2, endIndex2,
                                rule2, j,
                                points[2*k], points[2*k + 1],
                                params[2*k], params[2*k + 1]));
                    }
                }
            }
        }
        return isectPoints.toArray(new IntersectPoint[isectPoints.size()]);
    }

    private int getCurrentEdge(int areaIndex, int index,
            double[] c, double[] mp, double[] cp) {
        int endIndex = 0;

        switch (rules[areaIndex][index]) {
        case PathIterator.SEG_MOVETO:
            cp[0] = mp[0] = coords[areaIndex][offsets[areaIndex][index]];
            cp[1] = mp[1] = coords[areaIndex][offsets[areaIndex][index] + 1];
            break;
        case PathIterator.SEG_LINETO:
            c[0] = cp[0];
            c[1] = cp[1];
            cp[0] = c[2] = coords[areaIndex][offsets[areaIndex][index]];
            cp[1] = c[3] = coords[areaIndex][offsets[areaIndex][index] + 1];
            endIndex = 0;
            break;
        case PathIterator.SEG_QUADTO:
            c[0] = cp[0];
            c[1] = cp[1];
            c[2] = coords[areaIndex][offsets[areaIndex][index]];
            c[3] = coords[areaIndex][offsets[areaIndex][index] + 1];
            cp[0] = c[4] = coords[areaIndex][offsets[areaIndex][index] + 2];
            cp[1] = c[5] = coords[areaIndex][offsets[areaIndex][index] + 3];
            endIndex = 2;
            break;
        case PathIterator.SEG_CUBICTO:
            c[0] = cp[0];
            c[1] = cp[1];
            c[2] = coords[areaIndex][offsets[areaIndex][index]];
            c[3] = coords[areaIndex][offsets[areaIndex][index] + 1];
            c[4] = coords[areaIndex][offsets[areaIndex][index] + 2];
            c[5] = coords[areaIndex][offsets[areaIndex][index] + 3];
            cp[0] = c[6] = coords[areaIndex][offsets[areaIndex][index] + 4];
            cp[1] = c[7] = coords[areaIndex][offsets[areaIndex][index] + 5];
            endIndex = 4;
            break;
        case PathIterator.SEG_CLOSE:
            c[0] = cp[0];
            c[1] = cp[1];
            cp[0] = c[2] = mp[0];
            cp[1] = c[3] = mp[1];
            if (offsets[areaIndex][index] >= sizes[areaIndex]) {
                endIndex = -sizes[areaIndex];
            } else {
                endIndex = 0;
            }
            break;
        }
        return offsets[areaIndex][index] + endIndex;
    }

    private boolean containsPoint(double x, double y) {
        IntersectPoint ipoint;

        for (Iterator<IntersectPoint> i = isectPoints.iterator(); i.hasNext(); ) {
            ipoint = i.next();

            if ((Math.abs(ipoint.getX() - x) < Math.pow(10, -6)) &&
                    (Math.abs(ipoint.getY() - y) < Math.pow(10, -6))) {
                return true;
            }
        }

        return false;
    }
}
