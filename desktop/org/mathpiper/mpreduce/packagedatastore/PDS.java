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



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.io.streams.LispStream;

// This class (and PDSInputStream & PDSOutputStream) support a crude
// version of a file-system-within-a-file.  No sub-directoried are
// allowed. Allocation is strictly sequential. There are potentially
// SEVERE constraints on having access to multiple members of the
// one file. These rules are
//  (a) All access must be strictly nested. If a member x is open
//      for either reading or writing and one opens a different member
//      y then you may not read from or write to x again until y has
//      been closed.
//  (b) Only one member may be open for writing at any one time.
// Adherence to these constraints my not be fully checked, and violating
// them might cause arbitrary confusion.
//
// Failure to close a member after opening it for writing will leave
// that member visible but with zero length.
// Exiting from the entire system without closing a PDS may leave it
// containing old and unwanted material.
// Replacing members of a PDS may cause the file to grow by a large
// amount until it is closed.

// Three bits of serious re-work are needed here and in related places:
// (a) When I delete or replace a PDS member at present the space
//     that had been used is left vacant and wasted. The PDS should be
//     compacted sometime.
// (b) Right now my code probably only supports a single image file,
//     and functions to get module dates, copy and rename modules etc are
//     not in place. They need to be provided.
//
// It might also be better to have two sub-classes of PDS for the versions
// that read from a file and from in-store data?

public class PDS
{

String name;
boolean writeable;
boolean untidy;

RandomAccessFile f;
Vector data;
HashMap directory;

private static final int bufferShift = 12;
private static final int bufferSize  = 1<<bufferShift;
private static final int bufferMask  = bufferSize-1;

private byte [] buffer;
private long bufferPos, pos;
private boolean bufferValid;

void seek(long n) throws IOException
{
    if (f != null) f.seek(n);
    else if (data != null)
    {   pos = n;
        long newBufferPos = n & ~bufferMask;
        if (newBufferPos != bufferPos)
        {   bufferPos = newBufferPos;
            bufferValid = false;
        }
    }
}

int readCount = 0;

int read() throws IOException
{
    int c;
    if (f != null)
    {   c = f.read();
        return c;
    }
    else if (data != null)
    {   if (!bufferValid)
        {   // unpack a new chunk of data...
            buffer = (byte [])data.get((int)(bufferPos >> bufferShift));
            bufferValid = true;
        }
        int n = (int)(pos++ & bufferMask);
        c = buffer[n] & 0xff;
        if (n == bufferMask)
        {   bufferPos += bufferSize;
            bufferValid = false;
        }
        return c;
    }
    else return -1;
}

int read(byte [] b, int off, int len) throws IOException
{
    int c;
    if (f != null) return f.read(b, off, len);
    else if (data != null)
    {   if (!bufferValid)
        {   // unpack a new chunk of data...
            buffer = (byte [])data.get((int)(bufferPos >> bufferShift));
            bufferValid = true;
        }
        int p = (int)(pos & bufferMask);
        int n = bufferSize - p;  // bytes left in buffer
        if (n < len) len = n;    // trim request
        for (int i=0; i<len; i++) b[off+i] = buffer[p+i];
        pos += len;
        p += len;
        if (p == bufferSize)
        {   bufferPos += bufferSize;
            bufferValid = false;
        }
        return len;
    }
    else return -1;
}

long getFilePointer() throws IOException
{
    if (f != null) return f.getFilePointer();
    else return pos;
}

long length() throws IOException
{
    if (f != null) return f.length();
    else if (data != null) return bufferSize*data.size();
    else return 0;
}

int memberData, memberStart;

public void print() throws ResourceException // print to Java standard output (for debugging)
{
    Jlisp.println("PDS " + this + " " + name +
                          " W=" + writeable + " U=" + untidy);
    Jlisp.println("f = " + f + " memberData = " + memberData);
    if (directory != null)
    {   Vector v = new Vector(10, 10);
        for (Iterator k = directory.keySet().iterator(); k.hasNext();)
        {   Object key = k.next();
            PDSEntry val = (PDSEntry)directory.get(key);
            v.add(val);
        }
        Object [] v1 = v.toArray();
        PDSEntry.ordering = PDSEntry.orderName;
        Arrays.sort(v1);
        for (int k = 0; k<v1.length; k++)
        {   PDSEntry val = (PDSEntry)(v1[k]);
            StringBuffer sb = new StringBuffer(val.name);
            while (sb.length() < 24) sb.append(" ");
            sb.append("pos: " + val.loc);
            while (sb.length() < 38) sb.append(" ");
            sb.append("len: " + val.len);
            while (sb.length() < 50) sb.append(" ");
            sb.append(LispStream.dFormat.format(new Date(val.date)));
            Jlisp.println(sb.toString());
        }
    }
    Jlisp.println("----");
}


public LispObject members() throws ResourceException
{
    LispObject r = Environment.nil;
    if (directory != null)
    {   for (Iterator k = directory.keySet().iterator(); k.hasNext();)
        {   Object key = k.next();
            r = new Cons(new LispString((String)key), r);
        }
    }
    return r;
}


public PDS(InputStream is) throws IOException
{
    name = "Resource image data";
    data = new Vector(500, 500);
    int k = 0;
    for (;;)
    {   byte [] b = new byte[bufferSize];
        int p = 0;
        while (p != bufferSize)
        {   int n = is.read(b, p, bufferSize-p);
            if (n < 1) break;
            p += n;
        }
        data.add(b);
        k++;
        if (p < bufferSize) break;
    }
    is.close();
    pos = 0;
    bufferValid = false;
    f = null;
    writeable = false;
    untidy = false;
    memberData = 0;
    memberStart = 0;
    directory = new HashMap();
    try
    {   readDirectory();
    }
    catch (IOException e)
    {   data = null;
        directory = null;
        throw e;
    }
}


public PDS(String name, boolean writeable) throws IOException, ResourceException
{
    this.name = name;
    this.writeable = writeable;
    data = null;
    untidy = false;
    memberData = 0;
    memberStart = 0;
    directory = new HashMap();
    if (writeable)
    {   File ff = new File(name);
        if (ff.exists())
        {   Jlisp.lispErr.println("File exists already");
            if (ff.canWrite())
            {   Jlisp.lispErr.println("Can write to file OK");
                f = new RandomAccessFile(ff, "rw");
                Jlisp.lispErr.println("f = " + f);
            }
            else
            {   f = new RandomAccessFile(ff, "r");
                Jlisp.lispErr.println("Falling back to read-only access");
                writeable = false;  // fall back to read-only access
            }
            try
            {   readDirectory();
                Jlisp.lispErr.println("index read");
            }
            catch (IOException e)
            {   Jlisp.lispErr.println("IO error reading index " + e.getMessage());
                if (writeable)
                {   try
                    {   f.close();
                    }
                    catch (IOException e1)
                    {
                    }
                    f = null; // create it later if really needed
                }
                else
                {   f = null;
                    directory = null;
                    throw e;
                }
            }
        }
        else f = null;  // must create it on demand!
    }
    else 
    {   f = new RandomAccessFile(name, "r");
        try
        {   readDirectory();
        }
        catch (IOException e)
        {   f = null;
            directory = null;
            throw e;
        }
    }
}

void close() throws IOException, ResourceException
{
    Jlisp.lispErr.println("Closing the PDS");
    writeable = false;
    if (f != null)
    {   RandomAccessFile f1 = f;
        f = null;
        f1.close();
    }
    data = null;
}


// The format of a PDS is as follows:
//
// The first B-byte block is an index block. The contents of an index
// block are shown soon, but a point is that each can contain a chaining
// word that refers to the next index block. Although these blocks are
// all B bytes long that may not occus at B-byte offsets in the file.
//
// An index block has the characters "JL" in its first two bytes as a 
// minimal magic number. The next 4 bytes either hold 0 or the (byte)
// address of the next index block.
// Following that (ie starting at byte 6) are a succession of records. Each
// has a length byte (n). If n=0 we have reached the end of what is in this
// block.  Otherwise there follow n bytes of characters that name a member,
// then 4+4+8 bytes for the location, length and date of that item.

static final int DirectoryBlockSize = 2048;

void readDirectory() throws IOException
{
    byte [] buffer = new byte[DirectoryBlockSize];
    long p = 0;
    int i;
    do
    {   seek(p);
        for (i=0; i<buffer.length; i++)
            buffer[i] = (byte)read();
// The first two bytes of any index block are expected to contain the
// characters JL as at least minimal identification.
        if ((buffer[0] & 0xff) != ('J' & 0xff) ||
            (buffer[1] & 0xff) != ('L' & 0xff)) 
            throw new IOException("Not a Jlisp image file (header)");
// Next each index block has an 4-byte number which is either 0 or the
// position in the file of a subsequent chunk of index information. Each
// index chunk is DirectoryBlockSize bytes but they do not have to be 
// positioned at DirectoryBlockSize-byte aligned positions within the file.
// Having an 4-byte offset here is not enough for a general offset (which 
// would use 8 bytes) and it limits the size of a PDS to 2 Gbytes.
        p = 0;
        for (i=0; i<4; i++) p = (p<<8) + (buffer[i+2] & 0xff);
// The chaining should refer to a place within the file!
        if (p > length()-buffer.length)
            throw new IOException("Not a Jlisp image file (chaining)");
        int n = 6, l;
        for (;;)
        {   l = buffer[n++] & 0xff; // length code
            if (l == 0) break;      // end of what is packed into this chunk
            if ((n + l + 16) > buffer.length)
                throw new IOException("Not a Jlisp image file (name length)");
            byte [] name = new byte[l];
            for (i=0; i<l; i++) name[i] = buffer[n++];
            int loc = 0;
            for (i=0; i<4; i++) loc = (loc<<8) + (buffer[n++] & 0xff);
            int len = 0;
            for (i=0; i<4; i++) len = (len<<8) + (buffer[n++] & 0xff);
            long date = 0;
            for (i=0; i<8; i++) date = (date<<8) + (buffer[n++] & 0xff);
            String nn = new String(name, "UTF8");
            directory.put(nn, new PDSEntry(nn, loc, len, date));
        }
    } while (p != 0);
}

void addToDirectory(String member) throws IOException, ResourceException
{
    if (!writeable)
        throw new IOException("Attempt to update a read-only image file");
    Jlisp.println("Image member " + member + " being created");
    long p = 0;
    int n, i;
    byte [] buffer = new byte[DirectoryBlockSize];
    if (f == null)
    {   f = new RandomAccessFile(name, "rw"); // create it now
        f.write('J');
        f.write('L');
        for (i=2; i<buffer.length; i++)
            f.write(0);   // empty directory. 
    }
    long loc = f.length();   // end of the file - where new module will go
    if (directory.get(member) != null)
    {
// Need to replace an existing member. Must set p to the address in the
// RandomAccessFile for the block of directory involved, and n to the
// offset within that block (pre-loaded into buffer) where the address
// information must go.
        boolean found = false;
        do
        {   long p1;
            seek(p);
            for (i=0; i<buffer.length; i++)
                buffer[i] = (byte)read();
// The first two bytes of any index block are expected to contain the
// characters JL as at least minimal identification.
            if ((buffer[0] & 0xff) != ('J' & 0xff) ||
                (buffer[1] & 0xff) != ('L' & 0xff)) 
                throw new IOException("Not a Jlisp image file (header)");
            p1 = 0;
            for (i=0; i<4; i++) p1 = (p1<<8) + (buffer[i+2] & 0xff);
// The chaining should refer to a place within the file!
            if (p1 > f.length()-buffer.length)
                throw new IOException("Not a Jlisp image file (chaining)");
            n = 6;
            int l;
            for (;;)
            {   l = buffer[n++] & 0xff; // length code
                if (l == 0) break;      // end of what is packed into this chunk
                if ((n + l + 16) > buffer.length)
                    throw new IOException(
                        "Not a Jlisp image file (name length)");
                byte [] name = new byte[l];
                for (i=0; i<l; i++) name[i] = buffer[n++];
                String nn = new String(name, "UTF8");
                if (member.equals(nn))
                {   found = true;
                    break;
                }
                n += 16; // skip position, length and date info
            }
            if (found) break;
            p = p1;
        } while (p != 0);
        if (!found) throw new IOException("member not found in PDS");
        untidy = true;
    }
    else
    {   
// I have a genuine new member to add.
// First I search to find the final index block.
        for (int kk=0;;)
        {   seek(p);
            Jlisp.println("Seeking to " + p);
            if (kk++ > 100)
            {   Jlisp.println("Looping...");
                throw new IOException("Sorry - mangled image file");
            }
            for (i=0; i<buffer.length; i++)
                buffer[i] = (byte)read();
            int p1 = 0;
            for (i=0; i<4; i++) p1 = (p1 << 8) + (buffer[i+2] & 0xff);
            if (p1==0) break;   // have found last block
            p = p1;
        }
// Now scan down the block to find the last entry in it.
        n = 6;
        int l;
        for (;;)
        {   l = buffer[n++] & 0xff;
            if (l == 0) break; // end of data in this chunk
            n += l + 16;
        }
        Jlisp.println("Found the end");
// Next check to see if there is space in the block to add the entry that
// is wanted here.
        byte [] nb = member.getBytes("UTF8");
        int nbl = nb.length;
// Two things limit the length of a module name: firstly I want a one-byte
// length code to suffice (eg 256 bytes). Secondly (and less stringently)
// I require that a name can fit within a single index block. NB the 256
// byte limit is on the byte-length of the data, not the number of
// characters (inclusion of non-ASCII chars may call for more than 1 byte
// being needed per char).
        if (nbl >= 0x100)
            throw new IOException("Name " + member +
                                  " for PSD member is too long");
        if (n + nbl + 16 >= buffer.length) 
// Would overflow this chunk, so chain on another one.
        {   long end = loc;
            loc = loc + DirectoryBlockSize;   // allow for the index block
            Jlisp.println("end = " + end);
            f.seek(p+2);
            f.write((int)(end >> 24));
            f.write((int)(end >> 16));
            f.write((int)(end >> 8));
            f.write((int)end);
            p = end;
            for (i=0; i<buffer.length; i++) buffer[i] = 0;
            buffer[0] = (byte)'J';
            buffer[1] = (byte)'L';
            // bytes 2,3,4,5 are now all zero
            n = 7; // as if the termination has just been read
        }
// Now append the new entry within the current chunk.
        buffer[n-1] = (byte)nbl;
        for (i=0; i<nbl; i++) buffer[n++] = nb[i]; // copy name across
    }
// new chunk will go on end of file
    memberData = (int)(p + n); // fill data in when member is closed
    Jlisp.println("p=" + p + " n = " + n + " mD = " + memberData);
    memberStart = (int)loc;    // suppose that 32-bit offsets are enough
    for (int j=0; j<16; j++)   // put in zeros for data (at this stage)
        buffer[n++] = 0;
// Write the updated block back into the file.
    f.seek(p);
    for (i=0; i<buffer.length; i++) f.write(buffer[i]);
    f.seek(f.length());
}

public LispObject modulep(String s)
{
    Object d = directory.get(s);
    if (d == null) return Environment.nil;
    long date = ((PDSEntry)d).date;
    if (date == 0) return Environment.nil;
    return new LispString(LispStream.dFormat.format(new Date(date)));
}

}

// end of PDS.java

