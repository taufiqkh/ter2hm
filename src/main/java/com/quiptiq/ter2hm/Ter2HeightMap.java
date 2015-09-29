package com.quiptiq.ter2hm;

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
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            System.exit(1);
            return;
        }
        TerReader terReader = new TerReader(inputStream);
        try {
            TerFileDescriptor terFile = terReader.read();
            System.out.println(".ter file " + fileName + "read successfully.");
            System.out.println("XPoints: " + terFile.getXPoints());
            System.out.println("YPoints: " + terFile.getYPoints());
            System.out.println("Size: " + terFile.getSize());
            Scale scale = terFile.getScale();
            System.out.println("Scale: " + scale);
            System.out.println("Radius: " + terFile.getRadius());
            System.out.println("Curvature mode: " + terFile.getCurveMode());
            System.out.println("Height scale: " + terFile.getHeightScale());
            System.out.println("Base height: " + terFile.getBaseHeight());
        } catch (IOException e) {
            System.err.println("Couldn't read " + fileName);
            e.printStackTrace();
            System.exit(1);
        } catch (TerReaderException e) {
            System.err.println("Unexpected format in file " + fileName);
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
