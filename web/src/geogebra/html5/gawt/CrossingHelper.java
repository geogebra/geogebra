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



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CrossingHelper {

    private double[][] coords;
    private int[] sizes;
    private List<IntersectPoint> isectPoints = new ArrayList<IntersectPoint>();

    public CrossingHelper(double[][] coords, int[] sizes) {
        this.coords = coords;
        this.sizes = sizes;
    }

    public IntersectPoint[] findCrossing() {
        int pointCount1 = sizes[0] / 2;
        int pointCount2 = sizes[1] / 2;
        int[] indices = new int[pointCount1 + pointCount2];

        for(int i = 0; i < pointCount1 + pointCount2; i++) {
            indices[i] = i;
        }

        sort(coords[0], pointCount1, coords[1], pointCount2, indices);
        // the set for the shapes edges storing
        List<Edge> edges = new ArrayList<Edge>();
        Edge edge;
        int begIndex, endIndex;
        int areaNumber;

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < pointCount1) {
                begIndex = indices[i];
                endIndex = indices[i] - 1;

                if (endIndex < 0) {
                    endIndex = pointCount1 - 1;
                }

                areaNumber = 0;
            } else if (indices[i] < pointCount1 + pointCount2) {
                begIndex = indices[i] - pointCount1;
                endIndex = indices[i] - 1 - pointCount1;

                if (endIndex < 0) {
                    endIndex = pointCount2 - 1;
                }

                areaNumber = 1;
            } else {
                throw new IndexOutOfBoundsException();
            }

            if (!removeEdge(edges, begIndex, endIndex)) {
                edge = new Edge(begIndex, endIndex, areaNumber);
                intersectShape(edges, coords[0], pointCount1,
                        coords[1], pointCount2, edge);
                edges.add(edge);
            }

            begIndex = indices[i];
            endIndex = indices[i] + 1;

            if ((begIndex < pointCount1) && (endIndex == pointCount1)) {
                endIndex = 0;
            } else if ((begIndex >= pointCount1) &&
                    (endIndex == (pointCount2 + pointCount1))) {
                endIndex = pointCount1;
            }

            if (endIndex < pointCount1) {
                areaNumber = 0;
            } else {
                areaNumber = 1;
                endIndex -= pointCount1;
                begIndex -= pointCount1;
            }

            if (!removeEdge(edges, begIndex, endIndex)) {
                edge = new Edge(begIndex, endIndex, areaNumber);
                intersectShape(edges, coords[0], pointCount1,
                        coords[1], pointCount2, edge);
                edges.add(edge);
            }
        }

        return isectPoints.toArray(new IntersectPoint[isectPoints.size()]);
    }

    private static boolean removeEdge(List<Edge> edges,  int begIndex, int endIndex) {

        for (Edge edge : edges) {
            if (edge.reverseCompare(begIndex, endIndex)) {
                edges.remove(edge);
                return true;
            }
        }

        return false;
    }

    // return the quantity of intersect points
    private void intersectShape(List<Edge> edges,
            double[] coords1, int length1,
            double[] coords2, int length2,
            Edge initEdge) {
        int areaOfEdge1, areaOfEdge2;
        int initBegin, initEnd;
        int addBegin, addEnd;
        double x1, y1, x2, y2, x3, y3, x4, y4;
        double[] point = new double[2];
        Edge edge;

        if (initEdge.areaNumber == 0) {
            x1 = coords1[2* initEdge.begIndex];
            y1 = coords1[2* initEdge.begIndex + 1];
            x2 = coords1[2* initEdge.endIndex];
            y2 = coords1[2* initEdge.endIndex + 1];
            areaOfEdge1 = 0;
        } else {
            x1 = coords2[2* initEdge.begIndex];
            y1 = coords2[2* initEdge.begIndex + 1];
            x2 = coords2[2* initEdge.endIndex];
            y2 = coords2[2* initEdge.endIndex + 1];
            areaOfEdge1 = 1;
        }

        for (Iterator<Edge> iter = edges.iterator(); iter.hasNext(); ) {
            edge = iter.next();

            if (edge.areaNumber == 0) {
                x3 = coords1[2* edge.begIndex];
                y3 = coords1[2* edge.begIndex + 1];
                x4 = coords1[2* edge.endIndex];
                y4 = coords1[2* edge.endIndex + 1];
                areaOfEdge2 = 0;
            } else {
                x3 = coords2[2* edge.begIndex];
                y3 = coords2[2* edge.begIndex + 1];
                x4 = coords2[2* edge.endIndex];
                y4 = coords2[2* edge.endIndex + 1];
                areaOfEdge2 = 1;
            }

            if ((areaOfEdge1 != areaOfEdge2) &&
                    (GeometryUtil.intersectLines(
                            x1, y1, x2, y2, x3, y3, x4, y4, point) == 1) &&
                            (!containsPoint(point))) {

                if (initEdge.areaNumber == 0) {
                    initBegin = initEdge.begIndex;
                    initEnd = initEdge.endIndex;
                    addBegin = edge.begIndex;
                    addEnd = edge.endIndex;
                } else {
                    initBegin = edge.begIndex;
                    initEnd = edge.endIndex;
                    addBegin = initEdge.begIndex;
                    addEnd = initEdge.endIndex;
                }

                if (((initEnd == length1 - 1) &&
                        (initBegin == 0 && initEnd > initBegin)) ||
                        (((initEnd != length1 - 1) || (initBegin != 0)) &&
                                ((initBegin != length1 - 1) || (initEnd != 0)) &&
                                (initBegin > initEnd))) {

                    int temp = initBegin;
                    initBegin = initEnd;
                    initEnd = temp;
                }

                if (((addEnd == length2 - 1) && (addBegin == 0) && (addEnd > addBegin)) ||
                        (((addEnd != length2 - 1) || (addBegin != 0)) &&
                                ((addBegin != length2 - 1) || (addEnd != 0)) && (addBegin > addEnd))) {

                    int temp = addBegin;
                    addBegin = addEnd;
                    addEnd = temp;
                }

                IntersectPoint ip;
                for (Iterator<IntersectPoint> i = isectPoints.iterator(); i.hasNext(); ) {
                    ip = i.next();

                    if ((initBegin == ip.getBegIndex(true)) &&
                            (initEnd == ip.getEndIndex(true))) {

                        if (compare(ip.getX(), ip.getY(), point[0], point[1]) > 0) {
                            initEnd = - (isectPoints.indexOf(ip) + 1);
                            ip.setBegIndex1(-(isectPoints.size() + 1));
                        } else {
                            initBegin = - (isectPoints.indexOf(ip) + 1);
                            ip.setEndIndex1(-(isectPoints.size() + 1));
                        }
                    }

                    if ((addBegin == ip.getBegIndex(false)) &&
                            (addEnd == ip.getEndIndex(false))) {

                        if (compare(ip.getX(), ip.getY(), point[0], point[1]) > 0) {
                            addEnd = - (isectPoints.indexOf(ip) + 1);
                            ip.setBegIndex2(-(isectPoints.size() + 1));
                        } else {
                            addBegin = - (isectPoints.indexOf(ip) + 1);
                            ip.setEndIndex2(-(isectPoints.size() + 1));
                        }
                    }
                }

                isectPoints.add(new IntersectPoint(initBegin, initEnd,
                        addBegin, addEnd,
                        point[0], point[1]));
            }
        }
    }

    // the array sorting
    private static void sort(double[] coords1, int length1,
            double[] coords2, int length2,
            int[] array) {
        int temp;
        int length = length1 + length2;
        double x1, y1, x2, y2;

        for (int i = 1; i < length; i++) {
            if (array[i-1] < length1) {
                x1 = coords1[2*array[i-1]];
                y1 = coords1[2*array[i-1] + 1];
            } else {
                x1 = coords2[2*(array[i-1] - length1)];
                y1 = coords2[2*(array[i-1] - length1) + 1];
            }
            if (array[i] < length1) {
                x2 = coords1[2*array[i]];
                y2 = coords1[2*array[i] + 1];
            } else {
                x2 = coords2[2*(array[i] - length1)];
                y2 = coords2[2*(array[i] - length1) + 1];
            }
            int j = i;
            while (j > 0 && compare(x1, y1, x2, y2) <= 0) {
                temp = array[j];
                array[j] = array[j-1];
                array[j-1] = temp;
                j--;
                if (j > 0) {
                    if (array[j-1] < length1) {
                        x1 = coords1[2*array[j-1]];
                        y1 = coords1[2*array[j-1] + 1];
                    } else {
                        x1 = coords2[2*(array[j-1] - length1)];
                        y1 = coords2[2*(array[j-1] - length1) + 1];
                    }
                    if (array[j] < length1) {
                        x2 = coords1[2*array[j]];
                        y2 = coords1[2*array[j] + 1];
                    } else {
                        x2 = coords2[2*(array[j] - length1)];
                        y2 = coords2[2*(array[j] - length1) + 1];
                    }
                }
            }
        }
    }

    public boolean containsPoint(double[] point) {
        IntersectPoint ipoint;

        for (Iterator<IntersectPoint> i = isectPoints.iterator(); i.hasNext(); ) {
            ipoint = i.next();

            if (ipoint.getX() == point[0] && ipoint.getY() == point[1]) {
                return true;
            }
        }

        return false;
    }

    public static int compare(double x1, double y1, double x2, double y2) {

        if ((x1 < x2) || (x1 == x2 && y1 < y2)) {
            return 1;
        } else if (x1 == x2 && y1 == y2) {
            return 0;
        }

        return -1;
    }

    private static final class Edge {
        final int begIndex;
        final int endIndex;
        final int areaNumber;

        Edge(int begIndex, int endIndex, int areaNumber) {
            this.begIndex = begIndex;
            this.endIndex = endIndex;
            this.areaNumber = areaNumber;
        }

        boolean reverseCompare (int begIndex, int endIndex) {
            return this.begIndex == endIndex && this.endIndex == begIndex;
        }
    }
}
