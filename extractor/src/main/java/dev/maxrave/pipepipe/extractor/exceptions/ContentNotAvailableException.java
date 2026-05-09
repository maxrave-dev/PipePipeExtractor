package dev.maxrave.pipepipe.extractor.exceptions;

public class ContentNotAvailableException extends ParsingException {
    public ContentNotAvailableException(final String message) {
        super(message);
    }

    public ContentNotAvailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
