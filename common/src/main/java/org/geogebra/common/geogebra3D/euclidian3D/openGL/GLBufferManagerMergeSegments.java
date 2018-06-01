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

	/**
	 * remove the segment from available segments list
	 * 
	 * @param segment
	 *            segment
	 */
	final protected void removeFromAvailableSegments(BufferSegment segment) {
		currentLengths.setAvailableLengths(segment);
		LinkedList<BufferSegment> list = availableSegments.get(currentLengths);
		list.remove(segment);
		if (list.isEmpty()) {
			availableSegments.remove(currentLengths);
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
			removeFromAvailableSegments(previous);
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
			removeFromAvailableSegments(following);
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

	// protected void drawBufferPacks(RendererShadersInterface r) {
	//
	// String s = "\nbufferPackList: " + bufferPackList.size();
	// // Log.debug("bufferPackList: " + bufferPackList.size());
	// for (int i = bufferPackList.size() - 1; i >= 0; i--) {
	// // for (BufferPackAbstract bufferPack : bufferPackList) {
	// BufferPackAbstract bufferPack = bufferPackList.get(i);
	// s += "\nbufferPack.elementsLength = " + bufferPack.elementsLength;
	// for (Index index : bufferPack.getSegmentEnds().keySet()) {
	// // s+="\n"+index.toString();
	// BufferSegment seg = bufferPack.getSegmentEnds().get(index);
	// s += "\n" + seg;
	// }
	// // Log.debug(
	// // "bufferPack.elementsLength: " + bufferPack.elementsLength);
	// if (bufferPack.elementsLength > 0) {
	// if (bufferPack.getSegmentEnds().size() == 1) {
	// BufferSegment segment = bufferPack.getSegmentEnds()
	// .firstEntry().getValue();
	// if (segment.elementsOffset == 0 && segment
	// .getElementsAvailableLength() == bufferPack.elementsLength) {
	// Log.debug("ICI");
	// // bufferPackList.remove(i);
	// } else {
	// bufferPack.draw(r);
	// }
	// } else {
	// bufferPack.draw(r);
	// }
	// }
	// }
	// Log.debug(s);
	// }

}
