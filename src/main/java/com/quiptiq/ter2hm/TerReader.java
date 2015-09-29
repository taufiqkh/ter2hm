package com.quiptiq.ter2hm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Reader for .ter data, using the spec defined at http://www.planetside.co.uk/terragen/dev/tgterrain.html.
 */
public class TerReader {
    private static final int CHUNK_MARKER_SIZE = 4;
    private static final int X_POINT_SIZE = 4;
    private static final int Y_POINT_SIZE = 4;
    private static final int SIZE_SIZE = 4;
    private static final int SCALE_OFFSET_SIZE = 4;
    private static final int SCALE_SIZE = 3 * SCALE_OFFSET_SIZE;
    private static final int CURVE_MODE_SIZE = 4;
    private static final int RADIUS_SIZE = 4;
    private static final int HEIGHT_SCALE_SIZE = 2;
    private static final int BASE_HEIGHT_SIZE = 2;
    private static final int ELEVATION_SIZE = 2;

    /** Default radius as specified by the Terragen file format spec **/
    private static final int DEFAULT_RADIUS = 6370;
    /** Default curve mode as specified by the Terragen file format spec **/
    private static final CurveMode DEFAULT_CURVE_MODE = CurveMode.FLAT;

    private final InputStream inputStream;
    private final byte[] HEADER = "TERRAGENTERRAIN ".getBytes(StandardCharsets.US_ASCII);

    public TerReader(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Ter input stream cannot be null");
        }
        this.inputStream = inputStream;
    }

    public TerFileDescriptor read() throws IOException, TerReaderException {
        byte[] header = new byte[HEADER.length];
        int bytesRead = inputStream.read(header);
        if (bytesRead != HEADER.length) {
            throw new TerReaderException("Could not read header, " + bytesRead + " bytes read");
        }
        boolean mustBeSquare = true;
        int xPoints = -1;
        int yPoints = -1;
        int size = -1;
        Scale scale = null;
        float radius = DEFAULT_RADIUS;
        CurveMode curveMode = CurveMode.FLAT;
        ShortBuffer elevations = null;
        int heightScale = 0;
        int baseHeight = 0;
        while(true) {
            ChunkType chunkType = readChunkType();
            if (chunkType == null) {
                // end of file
                if (elevations == null) {
                    elevations = ShortBuffer.allocate(0);
                }
                return new TerFileDescriptor(xPoints, yPoints, size, scale, radius, curveMode, heightScale, baseHeight,
                        elevations);
            }
            switch (chunkType) {
                case SIZE:
                    size = readSize();
                    break;
                case SCAL:
                    scale = readScale();
                    break;
                case XPTS:
                    mustBeSquare = false;
                    xPoints = readXPoints();
                    break;
                case YPTS:
                    mustBeSquare = false;
                    yPoints = readYPoints();
                    break;
                case CRAD:
                    radius = readRadius();
                    break;
                case CRVM:
                    curveMode = readCurveMode();
                    break;
                case ALTW:
                    if (mustBeSquare) {
                        if (size < 0) {
                            throw new TerReaderException("No size specified");
                        }
                        heightScale = readHeightScale();
                        baseHeight = readBaseHeight();
                        elevations = readElevations(size);
                    } else if (xPoints < 0 || yPoints < 0) {
                        throw new TerReaderException("X and Y points not properly specified");
                    } else {
                        heightScale = readHeightScale();
                        baseHeight = readBaseHeight();
                        elevations = readElevations(xPoints, yPoints);
                    }
                    break;
                case EOF:
                    return new TerFileDescriptor(xPoints, yPoints, size, scale, radius, curveMode, heightScale,
                            baseHeight, elevations);
                default:
                    System.err.println("Unhandled chunk type: " + chunkType);
                    System.exit(1);
            }
        }
    }

    private ByteBuffer readBytes(ChunkType chunkType, int expectedLength) throws IOException, TerReaderException {
        byte[] bytes = new byte[expectedLength];
        int bytesRead = inputStream.read(bytes);
        if (bytesRead != expectedLength) {
            throw new TerReaderException(
                    "Expected " + expectedLength + " bytes reading chunk type " + chunkType + ". Found " + bytesRead);
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }

    private int readXPoints() throws IOException, TerReaderException {
        return readBytes(ChunkType.XPTS, X_POINT_SIZE).getInt();
    }

    private int readYPoints() throws IOException, TerReaderException {
        return readBytes(ChunkType.YPTS, Y_POINT_SIZE).getInt();
    }

    private int readSize() throws IOException, TerReaderException {
        return readBytes(ChunkType.SIZE, SIZE_SIZE).getInt();
    }

    private Scale readScale() throws IOException, TerReaderException {
        ByteBuffer buffer = readBytes(ChunkType.SCAL, SCALE_SIZE);
        return new Scale(buffer.getFloat(), buffer.getFloat(SCALE_OFFSET_SIZE), buffer.getFloat(2 * SCALE_OFFSET_SIZE));
    }

    private float readRadius() throws IOException, TerReaderException {
        return readBytes(ChunkType.CRAD, RADIUS_SIZE).getFloat();
    }

    private CurveMode readCurveMode() throws IOException, TerReaderException {
        return CurveMode.forMode(readBytes(ChunkType.CRVM, CURVE_MODE_SIZE).getInt());
    }

    private int readHeightScale() throws IOException, TerReaderException {
        return readBytes(ChunkType.ALTW, HEIGHT_SCALE_SIZE).getShort();
    }

    private int readBaseHeight() throws IOException, TerReaderException {
        return readBytes(ChunkType.ALTW, BASE_HEIGHT_SIZE).getShort();
    }

    private ShortBuffer readElevations(int xPoints, int yPoints) throws IOException, TerReaderException {
        return readBytes(ChunkType.ALTW, ELEVATION_SIZE * xPoints * yPoints).asShortBuffer().asReadOnlyBuffer();
    }

    private ShortBuffer readElevations(int size) throws IOException, TerReaderException {
        return readBytes(ChunkType.ALTW, ELEVATION_SIZE * size * size).asShortBuffer().asReadOnlyBuffer();
    }

    private ChunkType readChunkType() throws IOException, TerReaderException {
        byte[] chunkMarkerBytes = new byte[CHUNK_MARKER_SIZE];
        int bytesRead = inputStream.read(chunkMarkerBytes);
        if (bytesRead == -1) {
            return null;
        }
        String chunkMarker = new String(chunkMarkerBytes, StandardCharsets.US_ASCII);
        try {
            return ChunkType.forMarker(chunkMarker);
        } catch (IllegalArgumentException e) {
            throw new TerReaderException("Unknown chunk marker type: " + chunkMarker, e);
        }
    }

    private static enum ChunkType {
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

}
