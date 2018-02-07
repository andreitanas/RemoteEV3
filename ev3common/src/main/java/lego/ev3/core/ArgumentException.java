package lego.ev3.core;

/**
 * Created by Andrei Tanas on 14-11-28.
 */
public class ArgumentException extends Throwable {
    public ArgumentException(String message, String argument) {
        super(message);
    }
}
