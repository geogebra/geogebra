package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.LinkedList;
import java.util.Map;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerCurvesClipped extends GLBufferManager {

	static final private int ELEMENTS_SIZE_START = 2048;
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 6;
	static final private int SPLIT_AVAILABLE_LIMIT = 2;

	private Index startIndex;
	private Index endIndex;

	/**
	 * constructor
	 */
	public GLBufferManagerCurvesClipped() {
		startIndex = new Index();
		endIndex = new Index();
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		return 3 * 2 * size * PlotterBrush.LATITUDES;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		if (currentBufferSegment.bufferPack instanceof BufferPackBigCurve) {
			BufferPackBigCurve bufferPack = (BufferPackBigCurve) currentBufferSegment.bufferPack;
			putToIndicesForCurve(BufferPackBigCurve.CURVE_SIZE_MAX);
			bufferPack.cloneIndices();
		} else {
			putToIndicesForCurve(size);
		}
	}

	/**
	 * set all indices to last element so it will draw no triangle
	 */
	private void setIndicesDegenerated() {
		indicesIndex = currentBufferSegment.indicesOffset;
		int index = currentBufferSegment.getElementsLength() - 1;
		for (int i = 0; i < currentBufferSegment.getIndicesLength(); i++) {
			putToIndices(index);
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void draw(RendererShadersInterface r, boolean hidden) {
		((TexturesShaders) r.getTextures()).setPackedDash();
		r.setDashTexture(hidden ? Textures.DASH_PACKED_HIDDEN : Textures.DASH_PACKED);
		drawBufferPacks(r);
	}

	@Override
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

	@Override
	protected BufferPackAbstract createBufferPack() {
		if (elementsLength > BufferPackAbstract.ELEMENT_SIZE_MAX) {
			return new BufferPackBigCurve(this);
		}
		return super.createBufferPack();
	}

	@Override
	protected void addCurrentToAvailableSegmentsMayMerge() {
		setAlphaToTransparent();
		setIndicesDegenerated();
		currentBufferSegment.getStart(startIndex);
		currentBufferSegment.getEnd(endIndex);
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
			currentBufferSegment.elementsOffset = previous.elementsOffset;
			currentBufferSegment.indicesOffset = previous.indicesOffset;
			currentBufferSegment.addToAvailableLengths(previous);
		}
		currentLengths.setAvailableLengths(currentBufferSegment);
		addToAvailableSegments(currentBufferSegment);
	}

	@Override
	protected void addToAvailableSegments(BufferSegment bufferSegment) {
		super.addToAvailableSegments(bufferSegment);
		currentBufferPack.getSegmentEnds().put(endIndex, bufferSegment);
	}

	@Override
	protected BufferSegment getAvailableSegment() {
		Map.Entry<Index, LinkedList<BufferSegment>> entry = availableSegments
				.ceilingEntry(currentLengths);
		if (entry == null) {
			return null;
		}
		LinkedList<BufferSegment> list = entry.getValue();
		BufferSegment ret = list.pop();
		if (list.isEmpty()) {
			availableSegments.remove(entry.getKey());
		}
		currentBufferPack = ret.bufferPack;
		ret.getEnd(endIndex);
		currentBufferPack.getSegmentEnds().remove(endIndex);
		ret.setLengths(currentLengths);
		if (ret.getElementsAvailableLength() > ret.getElementsLength()
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
			remainSegment.getEnd(endIndex);
			addToAvailableSegments(remainSegment);
			ret.setAvailableLengths(eLength, iLength);
			currentLengths.setLengths(ret);
		}
		return ret;
	}

}
