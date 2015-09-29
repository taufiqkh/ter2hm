package com.quiptiq.ter2hm;

/**
* Indicates file format problems while reading a .ter file format.
*/
class TerReaderException extends Exception {
    public TerReaderException(String message) {
        super(message);
    }

    public TerReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
