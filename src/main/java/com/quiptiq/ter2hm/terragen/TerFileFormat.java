package com.quiptiq.ter2hm.terragen;

/**
 * Contains .ter file format information.
 */
public class TerFileFormat {
    public static final int CHUNK_MARKER_SIZE = 4;
    public static final int X_POINT_SIZE = 4;
    public static final int Y_POINT_SIZE = 4;
    public static final int SIZE_SIZE = 4;
    public static final int SCALE_OFFSET_SIZE = 4;
    public static final int SCALE_SIZE = 3 * SCALE_OFFSET_SIZE;
    public static final int CURVE_MODE_SIZE = 4;
    public static final int RADIUS_SIZE = 4;
    public static final int HEIGHT_SCALE_SIZE = 2;
    public static final int BASE_HEIGHT_SIZE = 2;
    public static final int ELEVATION_SIZE = 2;

    /** Default radius as specified by the Terragen file format spec **/
    public static final int DEFAULT_RADIUS = 6370;
    /** Default curve mode as specified by the Terragen file format spec **/
    public static final CurveMode DEFAULT_CURVE_MODE = CurveMode.FLAT;
}
