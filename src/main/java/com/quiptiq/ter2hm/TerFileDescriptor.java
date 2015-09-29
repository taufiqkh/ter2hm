package com.quiptiq.ter2hm;

import java.nio.ShortBuffer;

/**
 * Created with IntelliJ IDEA. User: taufiq Date: 29/09/15 Time: 3:48 PM To change this template use File | Settings |
 * File Templates.
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
        this.elevations = elevations;
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

    public ShortBuffer getElevations() {
        return elevations;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public int getBaseHeight() {
        return baseHeight;
    }
}
