package dev.maxrave.pipepipe.extractor.exceptions;

public class AntiBotException extends ParsingException {

    public AntiBotException(String message) {
        super(message);
    }

    public AntiBotException(String message, Throwable cause) {
        super(message, cause);
    }
}
