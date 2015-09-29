package com.quiptiq.ter2hm.terragen;

/**
* Type of data chunk in Terragen map format.
*/
public enum ChunkType {
    /** Number of data points in the x direction **/
    XPTS,
    /** Number of data points in the y direction **/
    YPTS,
    /** Number of data points along the shortest side - 1. **/
    SIZE,
    /** Scale of the terrain in metres per unit **/
    SCAL,
    /** Radius of the planet being rendered **/
    CRAD,
    /** Curve mode **/
    CRVM,
    /** Altitude, in 16 bit words **/
    ALTW,
    /** End of file marker **/
    EOF;

    public static ChunkType forMarker(String marker) {
        if (marker.equals("EOF ")) {
            return EOF;
        } else if (marker.equals("EOF")) {
            throw new IllegalArgumentException("EOF marker requires final space");
        } else {
            return ChunkType.valueOf(marker);
        }
    }
}
