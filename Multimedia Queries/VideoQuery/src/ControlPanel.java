import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.crypto.Data;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel {
    private JFrame jf;
    private JPanel panel;
    private JLabel queryList, matchedTitle;
    private JList<DataClip> matchedList;
    private JList<String> simiList;
    private JButton sync, reset;
    private String queryWavFile = "", queryRgbFile = "";
    private List<DataClip> matchFiles = new ArrayList<>();
    private Player query = null, match = null;

    public ControlPanel(String queryWavFile, String queryRgbFile, List<DataClip> matchFiles) {
        this.queryWavFile = queryWavFile;
        this.queryRgbFile = queryRgbFile;
        this.matchFiles = matchFiles;
    }

    public void showPanel() {
        jf = new JFrame("Control Panel");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setSize(1200, 1000);
        jf.setLocationRelativeTo(null);
        panel = new JPanel(null);
        jf.setContentPane(panel);
        queryList = new JLabel("Query: " + queryWavFile);
        matchedTitle = new JLabel("Matched Videos: ");
        matchedList = new JList<>();
        simiList = new JList<>();
        sync = new JButton("Sync Play");
        reset = new JButton("Reset Async");

        matchedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        simiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DataClip[] tmp = new DataClip[matchFiles.size()];
        String[] tmp2 = new String[matchFiles.size()];
        for (int i = 0; i < matchFiles.size(); i++) {
            tmp[i] = matchFiles.get(i);
            tmp2[i] = String.format("%.2f", matchFiles.get(i).getMatchRate()) + " %";
        }
        matchedList.setListData(tmp);
        simiList.setListData(tmp2);
        matchedList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Prevent twice call
                if (!e.getValueIsAdjusting()) {
                    if (query != null)
                        query.closeAll();
                    if (match != null)
                        match.closeAll();
                    refresh();
                }
            }
        });
        matchedList.setSelectedIndex(0);
        simiList.setSelectedIndex(0);
        sync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query.manualPlay();
                match.manualPlay();
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query.manualStop();
                match.manualStop();
            }
        });
        queryList.setBounds(100, 100, 300, 100);
        matchedTitle.setBounds(500, 100, 300, 40);
        matchedList.setBounds(matchedTitle.getX(), matchedTitle.getY() + matchedTitle.getHeight(), 300, 140);
        simiList.setBounds(matchedList.getX() + matchedList.getWidth(), matchedList.getY(), 100, 140);

        sync.setBounds(100, 800, 100, 20);
        reset.setBounds(220, 800, 120, 20);
        refresh();
        jf.setVisible(true);
    }

    private void setQuery() {
        query = new Player(queryWavFile + ".wav", queryRgbFile, 150, 100, 300, panel);
    }

    private void setMatch() {
        DataClip dc = matchedList.getSelectedValue();
        match = new Player(dc.getWavPath() + ".wav", dc.getRgbPath(), 600, 500, 300, panel);
        match.setInterval(dc.getLowerBound(), dc.getUpperBound());
    }

    private void refresh() {
        panel.removeAll();
        panel.repaint();
        panel.add(queryList);
        panel.add(matchedTitle);
        panel.add(matchedList);
        panel.add(simiList);
        panel.add(sync);
        panel.add(reset);
        setQuery();
        setMatch();
        panel.revalidate();
    }

}



