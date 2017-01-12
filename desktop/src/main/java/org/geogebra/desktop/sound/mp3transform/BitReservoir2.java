package org.geogebra.desktop.sound.mp3transform;

public class BitReservoir2 extends BitReservoir {

	private static final int BUFFER_SIZE = 4096;
	private static final int BUFFER_SIZE_MASK = BUFFER_SIZE - 1;
	private int writeIndex, bitCount;
	private final byte[] buffer = new byte[BUFFER_SIZE];

	@Override
	int getBitCount() {
		return bitCount;
	}

	@Override
	int getBits(int n) {
		int x = 0;
		for (int i = 0; i < n; i++) {
			x = (x << 1) | (getOneBit() == 0 ? 0 : 1);
		}
		return x;
	}

	@Override
	int getOneBit() {
		// byte
		int x = buffer[bitCount >>> 3] & (1 << (7 - (bitCount++ & 7)));
		bitCount = (bitCount + 1) & (BUFFER_SIZE * 8 - 1);
		return x;
		// int
		// return buffer[bitCount >>> 5] & (1 << (31 - (bitCount++ & 31)));
		// if(bitCount > 31) {
		// readByte();
		// }
		// return r;
	}

	// private void readByte() {
	// readIndex = (readIndex + 1) & BUFFER_SIZE_MASK;
	// current = buffer[readIndex];
	// bitCount = 0;
	// }

	@Override
	void putByte(int val) {
		int wi = writeIndex;
		buffer[wi] |= val << (8 * (wi & 3));
		writeIndex = (wi + 1) & BUFFER_SIZE_MASK;
	}

	@Override
	void rewindBits(int n) {
		bitCount -= n;
	}

	@Override
	void rewindBytes(int n) {
		bitCount -= n * 8;
	}

}
