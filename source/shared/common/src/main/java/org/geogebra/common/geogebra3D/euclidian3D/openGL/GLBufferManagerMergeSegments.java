package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * manager for packing buffers with merging segments
 */
abstract public class GLBufferManagerMergeSegments extends GLBufferManager {

	static final private int SPLIT_AVAILABLE_LIMIT = 2;

	private Index startIndex;
	private Index endIndex;
	private TreeMap<Integer, LinkedList<BufferPackAbstract>> availableBufferPacks = new TreeMap<>();
	private boolean mayNeedToRemoveBuffers = false;

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
		if (list != null) {
			list.remove(segment);
			if (list.isEmpty()) {
				availableSegments.remove(currentLengths);
			}
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
		mayNeedToRemoveBuffers = true;
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

	@Override
	protected void useAnotherBufferPack() {
		Entry<Integer, LinkedList<BufferPackAbstract>> entry = availableBufferPacks
				.ceilingEntry(elementsLength);
		if (entry != null) {
			BufferPackAbstract buffer = entry.getValue().getFirst();
			currentBufferPack = buffer;
		} else {
			super.useAnotherBufferPack();
		}
	}

	@Override
	protected void addToLengthToCurrentBufferPack(int elementsLengthToAdd,
			int indicesLengthToAdd) {
		if (!currentBufferPack.isBigBuffer()) {
			removeFromAvailableBufferPacks(currentBufferPack);
		}
		currentBufferPack.addToLength(elementsLengthToAdd, indicesLengthToAdd);
		if (!currentBufferPack.isBigBuffer()) {
			addToAvailableBufferPacks(currentBufferPack);
		}
	}

	private void addToAvailableBufferPacks(BufferPackAbstract buffer) {
		int length = BufferPackAbstract.ELEMENT_SIZE_MAX
				- buffer.elementsLength;
		LinkedList<BufferPackAbstract> list = availableBufferPacks.get(length);
		if (list == null) {
			list = new LinkedList<>();
			availableBufferPacks.put(length, list);
		}
		list.add(buffer);
	}

	private void removeFromAvailableBufferPacks(BufferPackAbstract buffer) {
		int length = BufferPackAbstract.ELEMENT_SIZE_MAX
				- buffer.elementsLength;
		LinkedList<BufferPackAbstract> list = availableBufferPacks.get(length);
		if (list != null) {
			list.remove(buffer);
			if (list.isEmpty()) {
				availableBufferPacks.remove(length);
			}
		}
	}

	@Override
	public void reset() {
		availableBufferPacks.clear();
		mayNeedToRemoveBuffers = false;
		super.reset();
	}

	/**
	 * update buffer pack list and remove empty buffers
	 */
	public void update() {
		if (mayNeedToRemoveBuffers) {
			int size = bufferPackList.size();
			if (size > 1) { // don't remove if only one buffer
				for (int i = size - 1; i >= 0; i--) {
					BufferPackAbstract bufferPack = bufferPackList.get(i);
					if (currentBufferPack != bufferPack && bufferPack.elementsLength > 0
							&& bufferPack.getSegmentEnds().size() == 1) {
						BufferSegment segment = bufferPack.getSegmentEnds()
								.firstEntry().getValue();
						if (segment.elementsOffset == 0 && segment
								.getElementsAvailableLength() == bufferPack.elementsLength) {
							bufferPackList.remove(i);
							removeFromAvailableSegments(segment);
							removeFromAvailableBufferPacks(bufferPack);
						}
					}
				}
			}
		}
		mayNeedToRemoveBuffers = false;
	}

}
