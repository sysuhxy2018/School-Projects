import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.Hashtable;

public class Player {
    private String audioFile, videoFile;
    private int frames, posX, posY, left, right;
    private Video video;
    private Audio audio;
    private JLabel displayArea;
    private JButton play, pause, stop;
    private JSlider progress;
    private JCheckBox beginLeft, endRight;
    private Hashtable<Integer, JLabel> bound;
    private JPanel panel;

    public Player(String audioFile, String videoFile, int frames, int posX, int posY, JPanel panel) {
        this.audioFile = audioFile;
        this.videoFile = videoFile;
        this.frames = frames;
        this.posX = posX;
        this.posY = posY;
        this.panel = panel;
        this.left = 1;
        this.right = frames;
        bound = new Hashtable<>();
        displayArea = new JLabel();
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");
        progress = new JSlider(1, this.frames, 1);
        beginLeft = new JCheckBox("Always starts from left indicator");
        endRight = new JCheckBox("Always ends at right indicator");
        video = new Video(videoFile, this.frames, this.displayArea, this.progress);
        audio = new Audio(audioFile);
        setLayout();
        setPlay();
        setPause();
        setStop();
        setSlider();
        this.panel.add(displayArea);
        this.panel.add(play);
        this.panel.add(pause);
        this.panel.add(stop);
        this.panel.add(progress);
        this.panel.add(beginLeft);
        this.panel.add(endRight);
    }

    public int getPlayerWidth() {
        return video.getWidth();
    }

    public int getPlayerHeight() {
        return endRight.getY() + endRight.getHeight() - posY;
    }

    public void closeAll() {
        video.stop();
        audio.stop();
    }

    public int setInterval(int left, int right) {
        if (left > right)
            return -1;
        this.left = left;
        this.right = right;
        setTable();
        return 0;
    }

    private void setLayout() {
        int width = video.getWidth();
        int height = video.getHeight();
        displayArea.setBounds(posX, posY, width, height);
        progress.setBounds(posX, posY + height + 20, width, 50);
        play.setBounds(posX, progress.getY() + progress.getHeight() + 20, 60, 20);
        pause.setBounds(posX + play.getWidth() + 20, play.getY(), 80, 20);
        stop.setBounds(pause.getX() + pause.getWidth() + 20, pause.getY(), 60, 20);
        beginLeft.setBounds(posX, play.getY() + play.getHeight() + 20, width, 20);
        endRight.setBounds(posX, beginLeft.getY() + beginLeft.getHeight() + 20, width, 20);
    }

    private void setPlay() {
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (beginLeft.isSelected()) {
                    video.setCurrent(left);
                    audio.setCurrent(left, frames);
                }
                video.play();
                audio.play();
            }
        });
    }

    private void setPause() {
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                video.pause();
                audio.pause();
            }
        });
    }

    private void setStop() {
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                video.stop();
                audio.stop();
            }
        });
    }

    private void setSlider(){
        progress.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                video.setCurrent(progress.getValue());
                audio.setCurrent(progress.getValue(), frames);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        progress.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                video.pause();
                audio.pause();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (beginLeft.isSelected()) {
                    video.setCurrent(left);
                    audio.setCurrent(left, frames);
                }
                video.play();
                audio.play();
            }
        });
        progress.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (endRight.isSelected() && progress.getValue() == right) {
                    video.pause();
                    audio.pause();
                }
            }
        });
        setTable();
        progress.setPaintLabels(true);
    }

    private void setTable() {
        bound.clear();
        bound.put(left, new JLabel("|"));
        bound.put(right, new JLabel("|"));
        progress.setLabelTable(bound);
    }

    private void setConfig() {
        beginLeft.setSelected(true);
        endRight.setSelected(true);
    }

    private void unsetConfig() {
        beginLeft.setSelected(false);
        endRight.setSelected(false);
    }

    public void manualPlay() {
        setConfig();
        if (beginLeft.isSelected()) {
            video.setCurrent(left);
            audio.setCurrent(left, frames);
        }
        video.play();
        audio.play();
    }

    public void manualStop() {
        unsetConfig();
        video.stop();
        audio.stop();
    }
}
