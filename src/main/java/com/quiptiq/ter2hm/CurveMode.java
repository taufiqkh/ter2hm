package com.quiptiq.ter2hm;

/**
* Curve mode
*/
enum CurveMode {
    FLAT(0),
    DRAPED(1),
    UNKNOWN(-1);

    private int mode;
    CurveMode(int mode) {
        this.mode = mode;
    }

    public static CurveMode forMode(int mode) {
        if (mode == 0) {
            return FLAT;
        } else if (mode == 1) {
            return DRAPED;
        } else {
            return UNKNOWN;
        }
    }
}
