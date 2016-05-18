package com.github.quickhull3d;

/*
 * #%L
 * A Robust 3D Convex Hull Algorithm in Java
 * %%
 * Copyright (C) 2004 - 2014 John E. Lloyd
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/**
 * Basic triangular face used to form the hull.
 * <p>
 * The information stored for each face consists of a planar normal, a planar
 * offset, and a doubly-linked list of three <a href=HalfEdge>HalfEdges</a>
 * which surround the face in a counter-clockwise direction.
 * 
 * @author John E. Lloyd, Fall 2004
 */
class Face {

    protected static final int DELETED = 3;

    protected static final int NON_CONVEX = 2;

    protected static final int VISIBLE = 1;

    protected double area;

    protected HalfEdge he0;

    protected int mark = VISIBLE;

    protected Face next;

    protected int numVerts;

    protected Vertex outside;

    protected double planeOffset;

    private Point3d centroid;

    private Vector3d normal;

    public Face() {
        normal = new Vector3d();
        centroid = new Point3d();
        mark = VISIBLE;
    }

    public static Face create(Vertex[] vtxArray, int[] indices) {
        Face face = new Face();
        HalfEdge hePrev = null;
        for (int i = 0; i < indices.length; i++) {
            HalfEdge he = new HalfEdge(vtxArray[indices[i]], face);
            if (hePrev != null) {
                he.setPrev(hePrev);
                hePrev.setNext(he);
            } else {
                face.he0 = he;
            }
            hePrev = he;
        }
        face.he0.setPrev(hePrev);
        hePrev.setNext(face.he0);

        // compute the normal and offset
        face.computeNormalAndCentroid();
        return face;
    }

    public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2) {
        return createTriangle(v0, v1, v2, 0);
    }

    /**
     * Constructs a triangule Face from vertices v0, v1, and v2.
     * 
     * @param v0
     *            first vertex
     * @param v1
     *            second vertex
     * @param v2
     *            third vertex
     */
    public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2, double minArea) {
        Face face = new Face();
        HalfEdge he0 = new HalfEdge(v0, face);
        HalfEdge he1 = new HalfEdge(v1, face);
        HalfEdge he2 = new HalfEdge(v2, face);

        he0.prev = he2;
        he0.next = he1;
        he1.prev = he0;
        he1.next = he2;
        he2.prev = he1;
        he2.next = he0;

        face.he0 = he0;

        // compute the normal and offset
        face.computeNormalAndCentroid(minArea);
        return face;
    }

    public void computeCentroid(Point3d centroid) {
        centroid.setZero();
        HalfEdge he = he0;
        do {
            centroid.add(he.head().pnt);
            he = he.next;
        } while (he != he0);
        centroid.scale(1 / (double) numVerts);
    }

    public void computeNormal(Vector3d normal) {
        HalfEdge he1 = he0.next;
        HalfEdge he2 = he1.next;

        Point3d p0 = he0.head().pnt;
        Point3d p2 = he1.head().pnt;

        double d2x = p2.x - p0.x;
        double d2y = p2.y - p0.y;
        double d2z = p2.z - p0.z;

        normal.setZero();

        numVerts = 2;

        while (he2 != he0) {
            double d1x = d2x;
            double d1y = d2y;
            double d1z = d2z;

            p2 = he2.head().pnt;
            d2x = p2.x - p0.x;
            d2y = p2.y - p0.y;
            d2z = p2.z - p0.z;

            normal.x += d1y * d2z - d1z * d2y;
            normal.y += d1z * d2x - d1x * d2z;
            normal.z += d1x * d2y - d1y * d2x;

            he1 = he2;
            he2 = he2.next;
            numVerts++;
        }
        area = normal.norm();
        normal.scale(1 / area);
    }

    public void computeNormal(Vector3d normal, double minArea) {
        computeNormal(normal);

        if (area < minArea) {
            // make the normal more robust by removing
            // components parallel to the longest edge

            HalfEdge hedgeMax = null;
            double lenSqrMax = 0;
            HalfEdge hedge = he0;
            do {
                double lenSqr = hedge.lengthSquared();
                if (lenSqr > lenSqrMax) {
                    hedgeMax = hedge;
                    lenSqrMax = lenSqr;
                }
                hedge = hedge.next;
            } while (hedge != he0);

            Point3d p2 = hedgeMax.head().pnt;
            Point3d p1 = hedgeMax.tail().pnt;
            double lenMax = Math.sqrt(lenSqrMax);
            double ux = (p2.x - p1.x) / lenMax;
            double uy = (p2.y - p1.y) / lenMax;
            double uz = (p2.z - p1.z) / lenMax;
            double dot = normal.x * ux + normal.y * uy + normal.z * uz;
            normal.x -= dot * ux;
            normal.y -= dot * uy;
            normal.z -= dot * uz;

            normal.normalize();
        }
    }

    /**
     * Computes the distance from a point p to the plane of this face.
     * 
     * @param p
     *            the point
     * @return distance from the point to the plane
     */
    public double distanceToPlane(Point3d p) {
        return normal.x * p.x + normal.y * p.y + normal.z * p.z - planeOffset;
    }

    /**
     * Finds the half-edge within this face which has tail <code>vt</code> and
     * head <code>vh</code>.
     * 
     * @param vt
     *            tail point
     * @param vh
     *            head point
     * @return the half-edge, or null if none is found.
     */
    public HalfEdge findEdge(Vertex vt, Vertex vh) {
        HalfEdge he = he0;
        do {
            if (he.head() == vh && he.tail() == vt) {
                return he;
            }
            he = he.next;
        } while (he != he0);
        return null;
    }

    public Point3d getCentroid() {
        return centroid;
    }

    /**
     * Gets the i-th half-edge associated with the face.
     * 
     * @param i
     *            the half-edge index, in the range 0-2.
     * @return the half-edge
     */
    public HalfEdge getEdge(int i) {
        HalfEdge he = he0;
        while (i > 0) {
            he = he.next;
            i--;
        }
        while (i < 0) {
            he = he.prev;
            i++;
        }
        return he;
    }

    public HalfEdge getFirstEdge() {
        return he0;
    }

    /**
     * Returns the normal of the plane associated with this face.
     * 
     * @return the planar normal
     */
    public Vector3d getNormal() {
        return normal;
    }

    public void getVertexIndices(int[] idxs) {
        HalfEdge he = he0;
        int i = 0;
        do {
            idxs[i++] = he.head().index;
            he = he.next;
        } while (he != he0);
    }

    public String getVertexString() {
        String s = null;
        HalfEdge he = he0;
        do {
            if (s == null) {
                s = "" + he.head().index;
            } else {
                s += " " + he.head().index;
            }
            he = he.next;
        } while (he != he0);
        return s;
    }

    public int mergeAdjacentFace(HalfEdge hedgeAdj, Face[] discarded) {
        Face oppFace = hedgeAdj.oppositeFace();
        int numDiscarded = 0;

        discarded[numDiscarded++] = oppFace;
        oppFace.mark = DELETED;

        HalfEdge hedgeOpp = hedgeAdj.getOpposite();

        HalfEdge hedgeAdjPrev = hedgeAdj.prev;
        HalfEdge hedgeAdjNext = hedgeAdj.next;
        HalfEdge hedgeOppPrev = hedgeOpp.prev;
        HalfEdge hedgeOppNext = hedgeOpp.next;

        while (hedgeAdjPrev.oppositeFace() == oppFace) {
            hedgeAdjPrev = hedgeAdjPrev.prev;
            hedgeOppNext = hedgeOppNext.next;
        }

        while (hedgeAdjNext.oppositeFace() == oppFace) {
            hedgeOppPrev = hedgeOppPrev.prev;
            hedgeAdjNext = hedgeAdjNext.next;
        }

        HalfEdge hedge;

        for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next; hedge = hedge.next) {
            hedge.face = this;
        }

        if (hedgeAdj == he0) {
            he0 = hedgeAdjNext;
        }

        // handle the half edges at the head
        Face discardedFace;

        discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace;
        }

        // handle the half edges at the tail
        discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace;
        }

        computeNormalAndCentroid();
        checkConsistency();

        return numDiscarded;
    }

    public int numVertices() {
        return numVerts;
    }

    public void triangulate(FaceList newFaces, double minArea) {
        HalfEdge hedge;

        if (numVertices() < 4) {
            return;
        }

        Vertex v0 = he0.head();

        hedge = he0.next;
        HalfEdge oppPrev = hedge.opposite;
        Face face0 = null;

        for (hedge = hedge.next; hedge != he0.prev; hedge = hedge.next) {
            Face face = createTriangle(v0, hedge.prev.head(), hedge.head(), minArea);
            face.he0.next.setOpposite(oppPrev);
            face.he0.prev.setOpposite(hedge.opposite);
            oppPrev = face.he0;
            newFaces.add(face);
            if (face0 == null) {
                face0 = face;
            }
        }
        hedge = new HalfEdge(he0.prev.prev.head(), this);
        hedge.setOpposite(oppPrev);

        hedge.prev = he0;
        hedge.prev.next = hedge;

        hedge.next = he0.prev;
        hedge.next.prev = hedge;

        computeNormalAndCentroid(minArea);
        checkConsistency();

        for (Face face = face0; face != null; face = face.next) {
            face.checkConsistency();
        }

    }

    /**
     * return the squared area of the triangle defined by the half edge hedge0
     * and the point at the head of hedge1.
     * 
     * @param hedge0
     * @param hedge1
     * @return
     */
    public double areaSquared(HalfEdge hedge0, HalfEdge hedge1) {
        Point3d p0 = hedge0.tail().pnt;
        Point3d p1 = hedge0.head().pnt;
        Point3d p2 = hedge1.head().pnt;

        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dz1 = p1.z - p0.z;

        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        double dz2 = p2.z - p0.z;

        double x = dy1 * dz2 - dz1 * dy2;
        double y = dz1 * dx2 - dx1 * dz2;
        double z = dx1 * dy2 - dy1 * dx2;

        return x * x + y * y + z * z;
    }

    private void computeNormalAndCentroid() {
        computeNormal(normal);
        computeCentroid(centroid);
        planeOffset = normal.dot(centroid);
        int numv = 0;
        HalfEdge he = he0;
        do {
            numv++;
            he = he.next;
        } while (he != he0);
        if (numv != numVerts) {
            throw new InternalErrorException("face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
        }
    }

    private void computeNormalAndCentroid(double minArea) {
        computeNormal(normal, minArea);
        computeCentroid(centroid);
        planeOffset = normal.dot(centroid);
    }

    private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge) {
        Face discardedFace = null;

        if (hedgePrev.oppositeFace() == hedge.oppositeFace()) { // then there is
                                                                // a redundant
                                                                // edge that we
                                                                // can get rid
                                                                // off

            Face oppFace = hedge.oppositeFace();
            HalfEdge hedgeOpp;

            if (hedgePrev == he0) {
                he0 = hedge;
            }
            if (oppFace.numVertices() == 3) { // then we can get rid of the
                                              // opposite face altogether
                hedgeOpp = hedge.getOpposite().prev.getOpposite();

                oppFace.mark = DELETED;
                discardedFace = oppFace;
            } else {
                hedgeOpp = hedge.getOpposite().next;

                if (oppFace.he0 == hedgeOpp.prev) {
                    oppFace.he0 = hedgeOpp;
                }
                hedgeOpp.prev = hedgeOpp.prev.prev;
                hedgeOpp.prev.next = hedgeOpp;
            }
            hedge.prev = hedgePrev.prev;
            hedge.prev.next = hedge;

            hedge.opposite = hedgeOpp;
            hedgeOpp.opposite = hedge;

            // oppFace was modified, so need to recompute
            oppFace.computeNormalAndCentroid();
        } else {
            hedgePrev.next = hedge;
            hedge.prev = hedgePrev;
        }
        return discardedFace;
    }

    void checkConsistency() {
        // do a sanity check on the face
        HalfEdge hedge = he0;
        double maxd = 0;
        int numv = 0;

        if (numVerts < 3) {
            throw new InternalErrorException("degenerate face: " + getVertexString());
        }
        do {
            HalfEdge hedgeOpp = hedge.getOpposite();
            if (hedgeOpp == null) {
                throw new InternalErrorException("face " + getVertexString() + ": " + "unreflected half edge " + hedge.getVertexString());
            } else if (hedgeOpp.getOpposite() != hedge) {
                throw new InternalErrorException("face " + getVertexString() + ": " + "opposite half edge " + hedgeOpp.getVertexString() + " has opposite "
                        + hedgeOpp.getOpposite().getVertexString());
            }
            if (hedgeOpp.head() != hedge.tail() || hedge.head() != hedgeOpp.tail()) {
                throw new InternalErrorException("face " + getVertexString() + ": " + "half edge " + hedge.getVertexString() + " reflected by " + hedgeOpp.getVertexString());
            }
            Face oppFace = hedgeOpp.face;
            if (oppFace == null) {
                throw new InternalErrorException("face " + getVertexString() + ": " + "no face on half edge " + hedgeOpp.getVertexString());
            } else if (oppFace.mark == DELETED) {
                throw new InternalErrorException("face " + getVertexString() + ": " + "opposite face " + oppFace.getVertexString() + " not on hull");
            }
            double d = Math.abs(distanceToPlane(hedge.head().pnt));
            if (d > maxd) {
                maxd = d;
            }
            numv++;
            hedge = hedge.next;
        } while (hedge != he0);

        if (numv != numVerts) {
            throw new InternalErrorException("face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
        }

    }
}
