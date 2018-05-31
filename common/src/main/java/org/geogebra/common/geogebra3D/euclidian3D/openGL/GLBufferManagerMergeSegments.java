package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.LinkedList;
import java.util.Map;

/**
 * manager for packing buffers with merging segments
 */
abstract public class GLBufferManagerMergeSegments extends GLBufferManager {

	static final private int SPLIT_AVAILABLE_LIMIT = 2;

	private Index startIndex;
	private Index endIndex;

	/**
	 * constructor
	 */
	public GLBufferManagerMergeSegments() {
		startIndex = new Index();
		endIndex = new Index();
	}

	/**
	 * set all indices to last element so it will draw no triangle
	 */
	final protected void setIndicesDegenerated() {
		indicesIndex = currentBufferSegment.indicesOffset;
		int index = currentBufferSegment.getElementsLength() - 1;
		for (int i = 0; i < currentBufferSegment.getIndicesLength(); i++) {
			putToIndices(index);
		}
	}

	@Override
	final protected void addCurrentToAvailableSegmentsMayMerge() {
		setAlphaToTransparent();
		setIndicesDegenerated();

		// merge with previous available segment
		currentBufferSegment.getStart(startIndex);
		BufferSegment previous = currentBufferPack.getSegmentEnds()
				.get(startIndex);
		if (previous != null) {
			currentLengths.setAvailableLengths(previous);
			LinkedList<BufferSegment> list = availableSegments
					.get(currentLengths);
			list.remove(previous);
			if (list.isEmpty()) {
				availableSegments.remove(currentLengths);
			}
			currentBufferPack.getSegmentEnds().remove(startIndex);
			previous.getStart(endIndex);
			currentBufferPack.getSegmentStarts().remove(endIndex);
			currentBufferSegment.elementsOffset = previous.elementsOffset;
			currentBufferSegment.indicesOffset = previous.indicesOffset;
			currentBufferSegment.addToAvailableLengths(previous);
		}

		// merge with following available segment
		currentBufferSegment.getEnd(endIndex);
		BufferSegment following = currentBufferPack.getSegmentStarts()
				.get(endIndex);
		if (following != null) {
			currentLengths.setAvailableLengths(following);
			LinkedList<BufferSegment> list = availableSegments
					.get(currentLengths);
			list.remove(following);
			if (list.isEmpty()) {
				availableSegments.remove(currentLengths);
			}
			currentBufferPack.getSegmentStarts().remove(endIndex);
			following.getEnd(startIndex);
			currentBufferPack.getSegmentEnds().remove(startIndex);
			currentBufferSegment.addToAvailableLengths(following);
		}

		currentBufferSegment.getStart(startIndex);
		currentBufferSegment.getEnd(endIndex);
		currentLengths.setAvailableLengths(currentBufferSegment);
		addToAvailableSegments(currentBufferSegment);
	}

	@Override
	final protected void addToAvailableSegments(BufferSegment bufferSegment) {
		super.addToAvailableSegments(bufferSegment);
		currentBufferPack.getSegmentEnds().put(new Index(endIndex),
				bufferSegment);
		currentBufferPack.getSegmentStarts().put(new Index(startIndex),
				bufferSegment);

	}

	@Override
	final protected BufferSegment getAvailableSegment() {
		Map.Entry<Index, LinkedList<BufferSegment>> entry = availableSegments
				.ceilingEntry(currentLengths);
		if (entry == null) {
			return null;
		}
		Index key = entry.getKey();
		if (currentLengths.hasFirstValueGreaterThan(key)) {
			return null;
		}
		LinkedList<BufferSegment> list = entry.getValue();
		BufferSegment ret = list.pop();
		if (list.isEmpty()) {
			availableSegments.remove(entry.getKey());
		}
		currentBufferPack = ret.bufferPack;
		ret.getStart(endIndex);
		currentBufferPack.getSegmentStarts().remove(endIndex);
		ret.getEnd(endIndex);
		currentBufferPack.getSegmentEnds().remove(endIndex);
		ret.setLengths(currentLengths);
		if (ret.getElementsAvailableLength() > ret.getElementsLength()
				* SPLIT_AVAILABLE_LIMIT * 2
				&& ret.getIndicesAvailableLength() > ret.getIndicesLength()
				* SPLIT_AVAILABLE_LIMIT * 2) {
			int size = getSizeForCurveFromElements(ret.getElementsLength());
			int eLength = getElementsLengthForCurve(
					size * SPLIT_AVAILABLE_LIMIT);
			int iLength = getIndicesLengthForCurve(
					size * SPLIT_AVAILABLE_LIMIT);
			BufferSegment remainSegment = new BufferSegment(currentBufferPack,
					ret.elementsOffset + eLength,
					ret.getElementsAvailableLength() - eLength,
					ret.indicesOffset + iLength,
					ret.getIndicesAvailableLength() - iLength);
			currentLengths.setAvailableLengths(remainSegment);
			remainSegment.getStart(startIndex);
			remainSegment.getEnd(endIndex);
			addToAvailableSegments(remainSegment);
			ret.setAvailableLengths(eLength, iLength);
			currentLengths.setLengths(ret);
		}
		return ret;
	}

}
