package com.quiptiq.ter2hm;

/**
* Created with IntelliJ IDEA. User: taufiq Date: 29/09/15 Time: 3:41 PM To change this template use File | Settings |
* File Templates.
*/
class TerReaderException extends Exception {
    public TerReaderException(String message) {
        super(message);
    }

    public TerReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
