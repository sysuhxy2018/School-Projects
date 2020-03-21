import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VideoQuery {

    public static void main(String[] args) {
        long sT = System.currentTimeMillis();
        String queryWavFile = "query/" + args[0];
        String queryRgbFile = "query/" + args[1];
        //String queryWavFile = "query/second/second";
        //String queryRgbFile = "query/second/second";

        System.out.println("Comparing audio......");
        AudioCompare audioCompare = new AudioCompare();
        ArrayList<DataClip> audioOutput = audioCompare.getAudioMatchRate(queryWavFile, queryRgbFile);
        System.out.println("Audio results are loaded completely!");

        System.out.println("Comparing HSV......");
        HistDescriptor hd = new HistDescriptor();
        // 这个是第一次跑的时候要调用，需要生成本地的.dat文件，存储直方图信息。可能需要一点时间。
        // 这个执行完后可以看到database_videos里面的每一个子目录里面都有一份.dat文件。
        // 有了这些.dat文件后，后面第二次，第三次...就不需要调用了
        // hd.generateAll();
        ArrayList<DataClip> videoOutput = hd.compareAll(queryWavFile, queryRgbFile);
        System.out.println("Video results are loaded completely!");


        //set new rate
        ArrayList<DataClip> output = videoOutput;
        for(int i = 0; i < 7; i++){
            double newRate = audioOutput.get(i).getMatchRate() * 0.1 + videoOutput.get(i).getMatchRate() * 0.9;
            output.get(i).setMatchRate(newRate);
        }

        // 按匹配度从大到小排序
        Collections.sort(output, new Comparator<DataClip>() {
            @Override
            public int compare(DataClip o1, DataClip o2) {
                if (o1.getMatchRate() > o2.getMatchRate()) {
                    return -1;
                }
                else if (o1.getMatchRate() < o2.getMatchRate()) {
                    return 1;
                }
                return 0;
            }
        });

        ControlPanel cp = new ControlPanel(queryWavFile, queryRgbFile, output);
        cp.showPanel();
        System.out.println((System.currentTimeMillis() - sT) * 0.001);
    }
}
