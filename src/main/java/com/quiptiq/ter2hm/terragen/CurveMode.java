package com.quiptiq.ter2hm.terragen;

/**
* Curve mode
*/
public enum CurveMode {
    FLAT(0),
    DRAPED(1),
    UNKNOWN(-1);

    private int mode;
    CurveMode(int mode) {
        this.mode = mode;
    }

    /**
     * Returns the curve mode represented by the given integer.
     * @param mode
     * @return
     */
    public static CurveMode forMode(int mode) {
        if (mode == FLAT.mode) {
            return FLAT;
        } else if (mode == DRAPED.mode) {
            return DRAPED;
        } else {
            throw new IllegalArgumentException("Invalid curve mode " + mode);
        }
    }
}
