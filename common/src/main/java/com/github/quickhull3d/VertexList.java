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
 * Maintains a double-linked list of vertices for use by QuickHull3D
 */
class VertexList {

    private Vertex head;

    private Vertex tail;

    /**
     * Clears this list.
     */
    public void clear() {
        head = tail = null;
    }

    /**
     * Adds a vertex to the end of this list.
     */
    public void add(Vertex vtx) {
        if (head == null) {
            head = vtx;
        } else {
            tail.next = vtx;
        }
        vtx.prev = tail;
        vtx.next = null;
        tail = vtx;
    }

    /**
     * Adds a chain of vertices to the end of this list.
     */
    public void addAll(Vertex vtx) {
        if (head == null) {
            head = vtx;
        } else {
            tail.next = vtx;
        }
        vtx.prev = tail;
        while (vtx.next != null) {
            vtx = vtx.next;
        }
        tail = vtx;
    }

    /**
     * Deletes a vertex from this list.
     */
    public void delete(Vertex vtx) {
        if (vtx.prev == null) {
            head = vtx.next;
        } else {
            vtx.prev.next = vtx.next;
        }
        if (vtx.next == null) {
            tail = vtx.prev;
        } else {
            vtx.next.prev = vtx.prev;
        }
    }

    /**
     * Deletes a chain of vertices from this list.
     */
    public void delete(Vertex vtx1, Vertex vtx2) {
        if (vtx1.prev == null) {
            head = vtx2.next;
        } else {
            vtx1.prev.next = vtx2.next;
        }
        if (vtx2.next == null) {
            tail = vtx1.prev;
        } else {
            vtx2.next.prev = vtx1.prev;
        }
    }

    /**
     * Inserts a vertex into this list before another specificed vertex.
     */
    public void insertBefore(Vertex vtx, Vertex next) {
        vtx.prev = next.prev;
        if (next.prev == null) {
            head = vtx;
        } else {
            next.prev.next = vtx;
        }
        vtx.next = next;
        next.prev = vtx;
    }

    /**
     * Returns the first element in this list.
     */
    public Vertex first() {
        return head;
    }

    /**
     * Returns true if this list is empty.
     */
    public boolean isEmpty() {
        return head == null;
    }
}
