package com.quiptiq.ter2hm.terragen;

/**
* Indicates file format problems while reading a .ter file format.
*/
public class TerFormatException extends Exception {
    public TerFormatException(String message) {
        super(message);
    }

    public TerFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
