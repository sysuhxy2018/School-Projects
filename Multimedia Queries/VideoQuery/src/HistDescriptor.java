import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.normalize;
import static org.opencv.imgproc.Imgproc.*;


public class HistDescriptor {

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private int hbins = 30, sbins = 32;
    // Notice that MaOfInt/Float is all 1-dimensional vector
    private MatOfInt channels = new MatOfInt(0, 1);
    private MatOfInt histSize = new MatOfInt(hbins, sbins);
    private MatOfFloat ranges = new MatOfFloat(0F, 180F, 0F, 256F);

    private Mat readMat(String name) {
        int height = 288, width = 352;
        int frameLength = height * width * 3;
        File file = new File(name);
        Mat mat = Mat.eye(height, width, CvType.CV_8UC3);
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            long len = frameLength;
            byte[] bytes = new byte[(int) len];
            raf.read(bytes);
            int ind = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // OpenCV use BGR instead of RGB
                    mat.put(y, x, new byte[]{
                            bytes[ind + height * width * 2],
                            bytes[ind + height * width],
                            bytes[ind]
                    });
                    ind++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mat;
    }

    public void generateAll() {
        try {
            for (String name : DataDir.dir) {
                // All directories should be based on src/ dir
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(name + "_hist.dat", false));
                for (int i = 1; i <= 600; i++) {
                    Mat mat = readMat(name + String.format("%03d", i) + ".rgb");
                    cvtColor(mat, mat, COLOR_BGR2HSV);
                    List<Mat> src = new ArrayList<>();
                    src.add(mat);
                    Mat hist = new Mat();
                    calcHist(src, channels, new Mat(), hist, histSize, ranges);
                    normalize(hist, hist, 0, 1, NORM_MINMAX, -1, new Mat());

                    ArrayList<ArrayList<Float>> table = new ArrayList<>();
                    for (int y = 0; y < hist.rows(); y++) {
                        float[] cell = new float[1];
                        ArrayList<Float> row = new ArrayList<>();
                        for (int x = 0; x < hist.cols(); x++) {
                            hist.get(y, x, cell);
                            row.add(cell[0]);
                        }
                        table.add(row);
                    }
                    output.writeObject(table);
                }
                System.out.println(name + " hist descriptor is finished!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DataClip> compareAll(String wavQuery, String rgbQuery) {
        ArrayList<DataClip> res = new ArrayList<>();
        try {
            ArrayList<Mat> queryTable = new ArrayList<>();
            for (int i = 1; i <= 150; i++) {
                Mat mat = readMat(rgbQuery + String.format("%03d", i) + ".rgb");
                cvtColor(mat, mat, COLOR_BGR2HSV);
                List<Mat> src = new ArrayList<>();
                src.add(mat);
                Mat hist = new Mat();
                calcHist(src, channels, new Mat(), hist, histSize, ranges);
                normalize(hist, hist, 0, 1, NORM_MINMAX, -1, new Mat());
                queryTable.add(hist);
            }

            for (String name : DataDir.dir) {
                ArrayList<Mat> compTable = new ArrayList<>();
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(name + "_hist.dat"));
                for (int i = 0; i < 600; i++) {
                    Mat histComp = new Mat(30, 32, CvType.CV_32F);
                    ArrayList<ArrayList<Float>> tmp = (ArrayList<ArrayList<Float>>)input.readObject();
                    for (int y = 0; y < 30; y++) {
                        for (int x = 0; x < 32; x++) {
                            histComp.put(y, x, new float[]{tmp.get(y).get(x)});
                        }
                    }
                    compTable.add(histComp);
                }

                // Scan all the intervals
                int start = 0;
                double ma = 0;
                for (int i = 0; i <= 450; i++) {
                    double sum = 0;
                    for (int j = 0; j < 150; j++) {
                        sum += Math.abs(compareHist(queryTable.get(j), compTable.get(i + j), CV_COMP_CORREL));
                    }
                    if (sum > ma) {
                        ma = sum;
                        start = i;
                    }
                }
                res.add(new DataClip(name, name, start + 1, start + 150, ma / 150 * 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
