import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

public class StdErrLevel extends Level {

    private StdErrLevel(String name, int value) {
        super(name, value);
    }

    public static Level STDERR =
            new StdErrLevel("STDERR", Level.INFO.intValue()+54);

    protected Object readResolve()
            throws ObjectStreamException {
        if (this.intValue() == STDERR.intValue())
            return STDERR;
        throw new InvalidObjectException("Unknown instance :" + this);
    }
}
