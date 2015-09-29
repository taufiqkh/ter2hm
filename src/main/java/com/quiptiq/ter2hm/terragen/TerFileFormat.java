package com.quiptiq.ter2hm.terragen;

import java.nio.ByteOrder;

/**
 * Contains .ter file format information, as defined in the spec at
 * http://www.planetside.co.uk/terragen/dev/tgterrain.html.
 */
public class TerFileFormat {
    /**
     * Number of bytes for a chunk marker
     */
    public static final int CHUNK_MARKER_SIZE = 4;

    /**
     * Number of bytes for the x point
     */
    public static final int X_POINT_SIZE = 4;

    /**
     * Number of bytes for the y point
     */
    public static final int Y_POINT_SIZE = 4;

    /**
     * Number of bytes for the size
     */
    public static final int SIZE_SIZE = 4;

    /**
     * Number of bytes for a single scale offset
     */
    public static final int SCALE_OFFSET_SIZE = 4;

    /**
     * Number of bytes for the scale offsets
     */
    public static final int SCALE_SIZE = 3 * SCALE_OFFSET_SIZE;

    /**
     * Number of bytes for the curve mode
     */
    public static final int CURVE_MODE_SIZE = 4;

    /**
     * Number of bytes for the radius
     */
    public static final int RADIUS_SIZE = 4;

    /**
     * Number of bytes for the height scale
     */
    public static final int HEIGHT_SCALE_SIZE = 2;

    /**
     * Number of bytes for the base height
     */
    public static final int BASE_HEIGHT_SIZE = 2;

    /**
     * Number of bytes for a single elevation number
     */
    public static final int ELEVATION_SIZE = 2;

    /** Default radius as specified by the Terragen file format spec **/
    public static final int DEFAULT_RADIUS = 6370;

    /** Default curve mode as specified by the Terragen file format spec **/
    public static final CurveMode DEFAULT_CURVE_MODE = CurveMode.FLAT;

    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
}
