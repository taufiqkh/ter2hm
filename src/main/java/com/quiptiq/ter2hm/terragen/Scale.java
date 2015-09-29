package com.quiptiq.ter2hm.terragen;

/**
 * Scale, in x, y, z
 */
public class Scale {

    private final float x;
    private final float y;
    private final float z;

    public Scale(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
