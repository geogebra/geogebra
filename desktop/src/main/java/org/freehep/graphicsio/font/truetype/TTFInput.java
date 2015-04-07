package org.freehep.graphicsio.font.truetype;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Data input for true type files. All methods are named as the data formats in
 * the true type specification.
 * 
 * @author Simon Fischer
 * @version $Id: TTFInput.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class TTFInput {

    private Stack filePosStack = new Stack();

    private int tempFlags;

    // --------------- IO ---------------

    public abstract void seek(long offset) throws IOException;

    abstract long getPointer() throws IOException;

    public void pushPos() throws IOException {
        filePosStack.push(new Long(getPointer()));
    }

    public void popPos() throws IOException {
        seek(((Long) filePosStack.pop()).longValue());
    }

    // ---------- Simple Data Types --------------

    public abstract int readRawByte() throws IOException;

    public abstract int readByte() throws IOException;

    public abstract short readShort() throws IOException;

    public abstract int readUShort() throws IOException;

    public abstract long readULong() throws IOException;

    public abstract int readLong() throws IOException;

    public abstract byte readChar() throws IOException;

    public final short readFWord() throws IOException {
        return readShort();
    }

    public final int readUFWord() throws IOException {
        return readUShort();
    }

    public final double readFixed() throws IOException {
        int major = readShort();
        int minor = readShort();
        return (double) major + (double) minor / 16384d;
    }

    public final double readF2Dot14() throws IOException {
        int major = readByte();
        int minor = readByte();
        int fraction = minor + ((major & 0x3f) << 8);
        int mantissa = major >> 6;
        if (mantissa >= 2)
            mantissa -= 4;
        return (double) mantissa + (double) fraction / 16384d;
    }

    // ------------------------------------------------------------

    public final void checkShortZero() throws IOException {
        if (readShort() != 0) {
            System.err.println("Reserved bit should be 0.");
        }
    }

    public static final boolean checkZeroBit(int b, int bit, String name)
            throws IOException {
        if (flagBit(b, bit)) {
            System.err.println("Reserved bit " + bit + " in " + name
                    + " not 0.");
            return false;
        } else {
            return true;
        }
    }

    // ---------------- Flags --------------------

    /**
     * Reads unsigned short flags into a temporary variable which can be queried
     * using the flagBit method.
     */
    public void readUShortFlags() throws IOException {
        tempFlags = readUShort();
    }

    /**
     * Reads byte flags into a temporary variable which can be queried using the
     * flagBit method.
     */
    public void readByteFlags() throws IOException {
        tempFlags = readByte();
    }

    public boolean flagBit(int bit) {
        return flagBit(tempFlags, bit);
    }

    public static boolean flagBit(int b, int bit) {
        return (b & (1 << bit)) > 0;
    }

    // ---------------- Arrays -------------------

    public abstract void readFully(byte[] b) throws IOException;

    public int[] readFFFFTerminatedUShortArray() throws IOException {
        List values = new LinkedList();
        int ushort = -1;
        do {
            ushort = readUShort();
            values.add(new Integer(ushort));
        } while (ushort != 0xFFFF);
        int[] shorts = new int[values.size()];
        Iterator i = values.iterator();
        int j = 0;
        while (i.hasNext()) {
            shorts[j++] = ((Integer) i.next()).intValue();
        }
        return shorts;
    }

    public int[] readUShortArray(int n) throws IOException {
        int[] temp = new int[n];
        for (int i = 0; i < temp.length; i++)
            temp[i] = readUShort();
        return temp;
    }

    public short[] readShortArray(int n) throws IOException {
        short[] temp = new short[n];
        for (int i = 0; i < temp.length; i++)
            temp[i] = readShort();
        return temp;
    }

}
