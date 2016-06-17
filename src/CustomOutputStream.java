import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void write(int b) throws IOException {
        this.textArea.append(String.valueOf((char)b));
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }
}
