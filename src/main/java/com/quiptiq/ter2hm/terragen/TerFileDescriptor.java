package com.quiptiq.ter2hm.terragen;

import com.quiptiq.ter2hm.terragen.CurveMode;
import com.quiptiq.ter2hm.terragen.Scale;

import java.nio.ShortBuffer;

/**
 * Terragen file descriptor. This class is not thread safe, as the elevations cannot be guaranteed to be immutable.
 * This class will not modify the initial buffer.
 */
public class TerFileDescriptor {
    private final int xPoints;
    private final int yPoints;
    private final int size;
    private final Scale scale;
    private final float radius;
    private final CurveMode curveMode;
    private final int heightScale;
    private final int baseHeight;
    private final ShortBuffer elevations;

    public TerFileDescriptor(int xPoints, int yPoints, int size, Scale scale, float radius, CurveMode curveMode,
                             int heightScale, int baseHeight, ShortBuffer elevations) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.size = size;
        this.scale = scale;
        this.radius = radius;
        this.curveMode=  curveMode;
        this.heightScale = heightScale;
        this.baseHeight = baseHeight;
        this.elevations = elevations.asReadOnlyBuffer();
        this.elevations.mark();
    }

    public int getXPoints() {
        return xPoints;
    }

    public int getYPoints() {
        return yPoints;
    }

    public int getSize() {
        return size;
    }

    public Scale getScale() {
        return scale;
    }

    public float getRadius() {
        return radius;
    }

    public CurveMode getCurveMode() {
        return curveMode;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public short getElevation(int x, int y) {
        short elevation = elevations.get(y + x * yPoints);
        elevations.reset();
        return elevation;
    }

    public float absoluteHeight(short elevation) {
        return (float) (baseHeight + elevation * (heightScale / 65536.0));
    }
}
