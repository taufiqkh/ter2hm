package com.quiptiq.ter2hm.terragen;

import com.quiptiq.ter2hm.terragen.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Reader for .ter data, using the spec defined at http://www.planetside.co.uk/terragen/dev/tgterrain.html.
 */
public class TerReader {

    private final InputStream inputStream;
    private final byte[] HEADER = "TERRAGENTERRAIN ".getBytes(StandardCharsets.US_ASCII);

    public TerReader(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Ter input stream cannot be null");
        }
        this.inputStream = inputStream;
    }

    /**
     * Start reading the terragen input stream.
     *
     * @return Terragen file descriptor containing the information read.
     *
     * @throws IOException if any IO errors occur while reading the input stream.
     * @throws com.quiptiq.ter2hm.terragen.TerFormatException if any file format errors occur while reading the input stream.
     */
    public TerFileDescriptor read() throws IOException, TerFormatException {
        byte[] header = new byte[HEADER.length];
        int bytesRead = inputStream.read(header);
        if (bytesRead != HEADER.length) {
            throw new TerFormatException("Could not read header, " + bytesRead + " bytes read");
        }
        boolean mustBeSquare = true;
        int xPoints = -1;
        int yPoints = -1;
        int size = -1;
        Scale scale = null;
        float radius = TerFileFormat.DEFAULT_RADIUS;
        CurveMode curveMode = TerFileFormat.DEFAULT_CURVE_MODE;
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
                            throw new TerFormatException("No size specified");
                        }
                        xPoints = (int) Math.sqrt(size + 1);
                        if (xPoints * xPoints != size + 1) {
                            throw new TerFormatException(
                                    "Size " + size + " is not a square, but no X and Y points have been defined");
                        }
                        yPoints = xPoints;
                        heightScale = readHeightScale();
                        baseHeight = readBaseHeight();
                        elevations = readElevations(size);
                    } else if (xPoints < 0 || yPoints < 0) {
                        throw new TerFormatException("X and Y points not both properly specified");
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

    private ByteBuffer readBytes(ChunkType chunkType, int expectedLength) throws IOException, TerFormatException {
        byte[] bytes = new byte[expectedLength];
        int bytesRead = inputStream.read(bytes);
        if (bytesRead != expectedLength) {
            throw new TerFormatException(
                    "Expected " + expectedLength + " bytes reading chunk type " + chunkType + ". Found " + bytesRead);
        }
        return ByteBuffer.wrap(bytes).order(TerFileFormat.BYTE_ORDER);
    }

    private int readXPoints() throws IOException, TerFormatException {
        return readBytes(ChunkType.XPTS, TerFileFormat.X_POINT_SIZE).getInt();
    }

    private int readYPoints() throws IOException, TerFormatException {
        return readBytes(ChunkType.YPTS, TerFileFormat.Y_POINT_SIZE).getInt();
    }

    private int readSize() throws IOException, TerFormatException {
        return readBytes(ChunkType.SIZE, TerFileFormat.SIZE_SIZE).getInt();
    }

    private Scale readScale() throws IOException, TerFormatException {
        ByteBuffer buffer = readBytes(ChunkType.SCAL, TerFileFormat.SCALE_SIZE);
        return new Scale(buffer.getFloat(), buffer.getFloat(TerFileFormat.SCALE_OFFSET_SIZE),
                buffer.getFloat(2 * TerFileFormat.SCALE_OFFSET_SIZE));
    }

    private float readRadius() throws IOException, TerFormatException {
        return readBytes(ChunkType.CRAD, TerFileFormat.RADIUS_SIZE).getFloat();
    }

    private CurveMode readCurveMode() throws IOException, TerFormatException {
        int mode = readBytes(ChunkType.CRVM, TerFileFormat.CURVE_MODE_SIZE).getInt();
        try {
            return CurveMode.forMode(mode);
        } catch (IllegalArgumentException e) {
            throw new TerFormatException("Unknown curve mode number found: " + mode, e);
        }
    }

    private int readHeightScale() throws IOException, TerFormatException {
        return readBytes(ChunkType.ALTW, TerFileFormat.HEIGHT_SCALE_SIZE).getShort();
    }

    private int readBaseHeight() throws IOException, TerFormatException {
        return readBytes(ChunkType.ALTW, TerFileFormat.BASE_HEIGHT_SIZE).getShort();
    }

    private ShortBuffer readElevations(int xPoints, int yPoints) throws IOException, TerFormatException {
        return readBytes(ChunkType.ALTW, TerFileFormat.ELEVATION_SIZE * xPoints * yPoints).asShortBuffer();
    }

    private ShortBuffer readElevations(int size) throws IOException, TerFormatException {
        return readBytes(ChunkType.ALTW, TerFileFormat.ELEVATION_SIZE * (size + 1) * (size + 1)).asShortBuffer();
    }

    private ChunkType readChunkType() throws IOException, TerFormatException {
        byte[] chunkMarkerBytes = new byte[TerFileFormat.CHUNK_MARKER_SIZE];
        int bytesRead = inputStream.read(chunkMarkerBytes);
        if (bytesRead == -1) {
            return null;
        }
        String chunkMarker = new String(chunkMarkerBytes, StandardCharsets.US_ASCII);
        try {
            return ChunkType.forMarker(chunkMarker);
        } catch (IllegalArgumentException e) {
            throw new TerFormatException("Unknown chunk marker type: " + chunkMarker, e);
        }
    }

}
