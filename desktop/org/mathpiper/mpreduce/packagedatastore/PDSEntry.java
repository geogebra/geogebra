package org.mathpiper.mpreduce.packagedatastore;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/




class PDSEntry implements Comparable
{
    String name; // name of this member
    int loc;     // location within the PDS (only 32 bits boo hiss!)
    int len;     // size of this member (also only 32 bits)
    long date;   // time last updated (64 bits)

    static final int orderName = 0;
    static final int orderLoc  = 1;
    static final int orderSize = 2;
    static final int orderDate = 3;
// The STATIC field here controls what ordering will be applied
// if I make an array of PDSEntry values and go Arrays.sort() on it.
    static int ordering = orderName;

public int compareTo(Object aa)
{
    if (!(aa instanceof PDSEntry)) return -1;
    PDSEntry a = (PDSEntry)aa;
    int r;
    switch (ordering)
    {
case orderName:
default:
        r = name.compareTo(a.name);
        if (r != 0) return r;
        else break;
case orderLoc:
        if (loc != a.loc) return a.loc - loc;
        else break;
case orderSize:
        if (len != a.len) return a.len - len;
        else break;
case orderDate:
        if (date != a.date) return (a.date < date) ? -1 : 1;
        else break;
    }
// If the primary key does not distinguish I will try the others in
// order to apply at least some ordering.
    r = name.compareTo(a.name);
    if (r != 0) return r;
    if (date != a.date) return (a.date < date) ? -1 : 1;
    if (len != a.len) return a.len - len;
    if (loc != a.loc) return a.loc - loc;
    return 0;
}

PDSEntry(String name, int loc, int len, long date)
{
    this.name = name;
    this.loc = loc;
    this.len = len;
    this.date = date;
}

}

// end of PDSEntry.java

