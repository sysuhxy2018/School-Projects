import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;

public class Video {
    private int width = 352, height = 288, current, end;
    private boolean status;
    private String path;
    private BufferedImage buffer;
    private JLabel displayArea;
    private JSlider progress;

    public Video(String path, int end, JLabel displayArea, JSlider progress) {
        this.path = path;
        this.current = 1;
        this.end = end;
        this.status = false;
        this.displayArea = displayArea;
        this.progress = progress;
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void readImg(String name) {
        try {
            int frameLength = width * height * 3;
            File file = new File(name);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            long len = frameLength;
            byte[] bytes = new byte[(int) len];
            raf.read(bytes);
            int ind = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    buffer.setRGB(x, y, pix);
                    ind++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (status)
            return;
        status = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status && current <= end) {
                    long startTime = System.nanoTime();
                    progress.setValue(current);
                    String name = path + String.format("%03d", current) + ".rgb";
                    readImg(name);
                    displayArea.setIcon(new ImageIcon(buffer));
                    // Rendering the image takes some time and to avoid error, it should be removed
                    long timeGap = System.nanoTime() - startTime;
                    long millis = (33333333 - timeGap) / 1000000;
                    long nanos = (33333333 - timeGap) % 1000000;
                    current++;
                    // Sleep time must be non-negative
                    if (timeGap >= 33333333)
                        continue;
                    try {
                        Thread.sleep(millis, (int) nanos);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                status = false;
            }
        }).start();
    }

    public void pause() {
        status = false;
    }

    public void stop() {
        current = 1;
        status = false;
        progress.setValue(1);
    }

    public void setCurrent(int pos) {
        current = pos;
    }

}
