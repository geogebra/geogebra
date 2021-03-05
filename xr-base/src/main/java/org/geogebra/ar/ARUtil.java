package org.geogebra.ar;

/**
 *
 * Utiles for AR for iOS and Android
 *
 */

public final class ARUtil {

    private final static float corner = 0.07f;
    private final static float thickness = corner / 30f;
    private final static float length = corner / 2f;

    private static final int COORDS_PER_VERTEX = 2; // x, z
    private static final int VERTICES_PER_CORNER = 6;
    private static final int CORNERS_NB = 4;
    private static final int INDICES_PER_TRIANGLE = 3;
    private static final int TRIANGLES_PER_CORNER = 4;

    /**
     * return indexData for focus square in AR
     */
    static public short[] getFocusSquareIndexData() {
        short[] indexData = new short[TRIANGLES_PER_CORNER * INDICES_PER_TRIANGLE * CORNERS_NB];
        int index = 0;
        for (int i = 0; i < CORNERS_NB; i++) {
            short shift = (short) (i * VERTICES_PER_CORNER);
            indexData[index++] = shift;
            indexData[index++] = (short) (shift + 1);
            indexData[index++] = (short) (shift + 2);

            indexData[index++] = shift;
            indexData[index++] = (short) (shift + 2);
            indexData[index++] = (short) (shift + 3);

            indexData[index++] = (short) (shift + 2);
            indexData[index++] = (short) (shift + 4);
            indexData[index++] = (short) (shift + 5);

            indexData[index++] = (short) (shift + 2);
            indexData[index++] = (short) (shift + 5);
            indexData[index++] = (short) (shift + 3);
        }
        return indexData;
    }

    /**
     * return positionData for focus square in AR
     */
    static public float[] getFocusSquarePositionData() {
        float[] positionData = new float[VERTICES_PER_CORNER * COORDS_PER_VERTEX * CORNERS_NB];
        int index = 0;
        index = addCorner(positionData, index, 1, 1);
        index = addCorner(positionData, index, -1, 1);
        index = addCorner(positionData, index, -1, -1);
        addCorner(positionData, index, 1, -1);

        return positionData;
    }

    static private int addCorner(float[] positionData, int startIndex, int cornerX, int cornerY) {
        int index = startIndex;
        positionData[index++] = (corner - length) * cornerX;
        positionData[index++] = (corner + thickness) * cornerY;

        positionData[index++] = (corner - length) * cornerX;
        positionData[index++] = (corner - thickness) * cornerY;

        positionData[index++] = (corner - thickness) * cornerX;
        positionData[index++] = (corner - thickness) * cornerY;

        positionData[index++] = (corner + thickness) * cornerX;
        positionData[index++] = (corner + thickness) * cornerY;

        positionData[index++] = (corner - thickness) * cornerX;
        positionData[index++] = (corner - length) * cornerY;

        positionData[index++] = (corner + thickness) * cornerX;
        positionData[index++] = (corner - length) * cornerY;
        return index;
    }
}
