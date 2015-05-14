package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import com.google.gwt.core.client.JsArrayInteger;
import com.googlecode.gwtgl.array.ArrayBuffer;
import com.googlecode.gwtgl.array.IntBasedTypedArray;
import com.googlecode.gwtgl.array.JsArrayUtil;
import com.googlecode.gwtgl.array.TypedArray;

/**
 * {@link TypedArray} that contains 16 Bit integer values.
 */
public class MyInt16Array extends IntBasedTypedArray<MyInt16Array> {

	/**
	 * Creates a new instance of the {@link TypedArray} using the given
	 * {@link ArrayBuffer} to read/write values from/to.
	 *
	 * @param buffer
	 *            the underlying {@link ArrayBuffer} of the newly created
	 *            {@link TypedArray}.
	 * @return the created {@link TypedArray}.
	 */
	public static native MyInt16Array create(ArrayBuffer buffer) /*-{
		return new Int16Array(buffer);
	}-*/;

	/**
	 * Creates a new instance of the {@link TypedArray} using the given
	 * {@link ArrayBuffer} to read/write values from/to.
	 * <p/>
	 * The {@link TypedArray} is created using the byteOffset to specify the
	 * starting point (in bytes) of the {@link TypedArray} relative to the
	 * beginning of the underlying {@link ArrayBuffer}. The byte offset must
	 * match (multiple) the value length of this {@link TypedArray}.
	 * <p/>
	 * if the byteLength is not valid for the given {@link ArrayBuffer}, an
	 * exception is thrown
	 *
	 * @param buffer
	 *            the underlying {@link ArrayBuffer} of the newly created
	 *            {@link TypedArray}.
	 * @param byteOffset
	 *            the offset relative to the beginning of the ArrayBuffer
	 *            (multiple of the value length of this {@link TypedArray})
	 * @return the newly created {@link TypedArray}.
	 */
	public static native MyInt16Array create(ArrayBuffer buffer, int byteOffset) /*-{
		return new Int16Array(buffer, byteOffset);
	}-*/;

	/**
	 * Creates a new instance of the {@link TypedArray} using the given
	 * {@link ArrayBuffer} to read/write values from/to.
	 * <p/>
	 * The {@link TypedArray} is created using the byteOffset and length to
	 * specify the start and end (in bytes) of the {@link TypedArray} relative
	 * to the beginning of the underlying {@link ArrayBuffer}. The byte offset
	 * must match (multiple) the value length of this {@link TypedArray}. The
	 * length is in values of the type of the {@link TypedArray}
	 * <p/>
	 * if the byteLength or length is not valid for the given
	 * {@link ArrayBuffer}, an exception is thrown
	 *
	 * @param buffer
	 *            the underlying {@link ArrayBuffer} of the newly created
	 *            {@link TypedArray}.
	 * @param byteOffset
	 *            the offset relative to the beginning of the ArrayBuffer
	 *            (multiple of the value length of this {@link TypedArray})
	 * @param length
	 *            the lenght of the {@link TypedArray} in vales.
	 * @return the newly created {@link TypedArray}.
	 */
	public static native MyInt16Array create(ArrayBuffer buffer,
			int byteOffset, int length) /*-{
		return new Int16Array(buffer, byteOffset, length);
	}-*/;

	/**
	 * Creates a new instance of the {@link TypedArray} of the given length in
	 * values. All values are set to 0.
	 *
	 * @param length
	 *            the length in values of the type used by this
	 *            {@link TypedArray}
	 * @return the created {@link TypedArray}.
	 */
	public static native MyInt16Array create(int length) /*-{
		return new Int16Array(length);
	}-*/;

	/**
	 * Creates a new instance of the {@link TypedArray} of the length of the
	 * given array in values. The values are set to the values of the given
	 * array.
	 *
	 * @param array
	 *            the array to get the values from
	 * @return the created {@link TypedArray}.
	 */
	public static MyInt16Array create(short[] array) {
		return create(JsArrayUtil.wrapArray(array));
	}

	/**
	 * Creates a new instance of the {@link TypedArray} of the same length as
	 * the given {@link TypedArray}. The values are set to the values of the
	 * given {@link TypedArray}.
	 *
	 * @param array
	 *            the {@link TypedArray} to get the values from
	 * @return the created {@link TypedArray}.
	 */
	public static native MyInt16Array create(MyInt16Array array) /*-{
		return new Int16Array(array);
	}-*/;

	/**
	 * Creates a new instance of the {@link TypedArray} of the length of the
	 * given array in values. The values are set to the values of the given
	 * array.
	 *
	 * @param array
	 *            the array to get the values from
	 * @return the created {@link TypedArray}.
	 */
	public static native MyInt16Array create(JsArrayInteger array) /*-{
		return new Int16Array(array);
	}-*/;

	/**
	 * protected standard constructor as specified by
	 * {@link com.google.gwt.core.client.JavaScriptObject}.
	 */
	protected MyInt16Array() {
		super();
	}

}