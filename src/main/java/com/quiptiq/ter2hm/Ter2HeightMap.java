package com.quiptiq.ter2hm;

import com.quiptiq.ter2hm.terragen.Scale;
import com.quiptiq.ter2hm.terragen.TerFileDescriptor;
import com.quiptiq.ter2hm.terragen.TerFormatException;
import com.quiptiq.ter2hm.terragen.TerReader;

import java.io.*;

/**
 * Main class
 */
public class Ter2HeightMap {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Need to specify .ter file");
            System.exit(1);
        }
        String fileName = args[0];
        FileInputStream fileInputStream;
        InputStream data = null;

        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Couldn find file: " + fileName);
            e.printStackTrace();
            System.exit(1);
            return;
        }
        try {
            data = new BufferedInputStream(fileInputStream);
            TerReader terReader = new TerReader(data);
            TerFileDescriptor terFile = terReader.read();
            System.out.println(fileName + " read successfully.");
            System.out.println("XPoints: " + terFile.getXPoints());
            System.out.println("YPoints: " + terFile.getYPoints());
            System.out.println("Size: " + terFile.getSize());
            Scale scale = terFile.getScale();
            System.out.println("Scale: " + scale);
            System.out.println("Radius: " + terFile.getRadius());
            System.out.println("Curvature mode: " + terFile.getCurveMode());
            System.out.println("Height scale: " + terFile.getHeightScale());
            System.out.println("Base height: " + terFile.getBaseHeight());
            Raw32Converter raw32Converter = new Raw32Converter();
            FileOutputStream fileOutputStream = new FileOutputStream("out.raw32");
            raw32Converter.convert(terFile, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.println("Couldn't read " + fileName);
            e.printStackTrace();
            System.exit(1);
        } catch (TerFormatException e) {
            System.err.println("Unexpected format in file " + fileName);
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
