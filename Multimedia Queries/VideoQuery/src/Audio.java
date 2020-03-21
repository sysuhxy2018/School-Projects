import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Audio {
    private String path;
    // 1Kb, if the size is too large, lag will be obvious. Also, it seems the size cannot be less than 1Kb
    private static final int EXTERNAL_BUFFER_SIZE = 3584000;
    private InputStream waveStream;
    private AudioInputStream audioInputStream;
    private BufferedInputStream bufferedIn;
    private AudioFormat audioFormat;
    private DataLine.Info info;
    private SourceDataLine dataLine;
    private int readBytes;
    private byte[] audioBuffer;
    private boolean status;
    private int current;

    public Audio(String path) {
        this.path = path;
        this.status = false;
        current = 0;
        initial(path);
    }

    private void initial(String audioName) {
        try{
            waveStream = new FileInputStream(audioName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioInputStream = null;
        try {
            bufferedIn = new BufferedInputStream(waveStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        audioFormat = audioInputStream.getFormat();
        info = new DataLine.Info(SourceDataLine.class, audioFormat);
        dataLine = null;
        try {
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat, EXTERNAL_BUFFER_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        readBytes = 0;
        audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];
        try {
            readBytes = audioInputStream.read(audioBuffer, 0,
                    audioBuffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (status)
            return;
        status = true;
        dataLine.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status && current < readBytes) {
                    int len = 4096;
                    if (current + len > readBytes)
                        len = readBytes - current;
                    dataLine.write(audioBuffer, current, len);
                    current += len;
                }
                status = false;
            }
        }).start();
    }

    public void pause() {
        status = false;
        dataLine.stop();
    }

    public void stop() {
        status = false;
        current = 0;
        dataLine.close();
        try {
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat, EXTERNAL_BUFFER_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrent(int pos, int frames) {
        dataLine.flush();
        double rate = (pos - 1.0) / (frames - 1.0);
        int bytes = (int)Math.floor(readBytes * rate);
        bytes--;
        if (bytes < 0)
            bytes = 0;
        // The offset should be always a multiple of 4
        bytes = bytes / 4 * 4;
        current = bytes;
    }
}
