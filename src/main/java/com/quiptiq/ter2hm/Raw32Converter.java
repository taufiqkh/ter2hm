package com.quiptiq.ter2hm;

import com.quiptiq.ter2hm.terragen.TerFileDescriptor;
import com.quiptiq.ter2hm.terragen.TerFileFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created with IntelliJ IDEA. User: taufiq Date: 29/09/15 Time: 10:06 PM To change this template use File | Settings |
 * File Templates.
 */
public class Raw32Converter {
    private static final int FLOAT_BYTES = 4;
    public Raw32Converter() {}

    public void convert(TerFileDescriptor terFile, OutputStream outputStream) throws IOException {
        byte[] columnBytes = new byte[terFile.getYPoints() * FLOAT_BYTES];
        ByteBuffer column = ByteBuffer.wrap(columnBytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int x = 0; x < terFile.getXPoints(); x++) {
            for (int y = 0; y < terFile.getYPoints(); y++) {
                column.putFloat(terFile.absoluteHeight(terFile.getElevation(x, y)));
            }
            outputStream.write(columnBytes);
            column.flip();
        }
    }
}
