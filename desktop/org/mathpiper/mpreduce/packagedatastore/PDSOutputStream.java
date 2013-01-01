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



import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.mathpiper.mpreduce.exceptions.ResourceException;

public class PDSOutputStream extends OutputStream
{

PDS pds;
String member;
int length;

long savedPosition;

public PDSOutputStream(PDS pds, String member) throws IOException, ResourceException
{
    this.pds = pds;
    this.member = member;
    if (pds.f != null) savedPosition = pds.f.getFilePointer();
    else savedPosition = -1;
    if (pds.memberData != 0)
        throw new IOException("Attempt to have two output files open in one PDS");
    pds.addToDirectory(member);
    length = 0;
}

public void close() throws IOException
{
    if (pds == null) return;

    pds.f.seek(pds.memberData);
    pds.f.write(pds.memberStart >> 24);
    pds.f.write(pds.memberStart >> 16);
    pds.f.write(pds.memberStart >> 8);
    pds.f.write(pds.memberStart);
    pds.f.write(length >> 24);
    pds.f.write(length >> 16);
    pds.f.write(length >> 8);
    pds.f.write(length);
    long date = new Date().getTime();
    pds.f.write((int)(date >> 56));
    pds.f.write((int)(date >> 48));
    pds.f.write((int)(date >> 40));
    pds.f.write((int)(date >> 32));
    pds.f.write((int)(date >> 24));
    pds.f.write((int)(date >> 16));
    pds.f.write((int)(date >> 8));
    pds.f.write((int)date);
    pds.memberData = 0;
    pds.directory.put(member,
        new PDSEntry(member, pds.memberStart, length, date));
    if (savedPosition >= 0) pds.f.seek(savedPosition);
    pds = null;
}

public void write(int c) throws IOException
{
    pds.f.write(c);
    length++;
}

}

// end of PDSInputStream.java


