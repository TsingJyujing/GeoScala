package tsingjyujing.geo.runner;

import tsingjyujing.geo.util.Tools;
import tsingjyujing.geo.util.file.text.TextFileLineReader;
import tsingjyujing.geo.util.file.text.TextFileLineWriter;
import tsingjyujing.geo.util.mathematical.GPSUtil;

import java.util.ArrayList;
import java.util.List;

public class GetZipMaxDistance {
    public static void main(String[] args) {
        TextFileLineReader textFileLineReader = new TextFileLineReader("exam/data.csv");
        TextFileLineWriter textFileLineWriter = new TextFileLineWriter("exam/result.txt");
        List<double[]> data = new ArrayList<>();
        int j = 0;
        for (String line : textFileLineReader) {
            j++;
            final double[] unit = new double[3];
            final String[] splitLine = line.split(",");
            for (int i = 0; i < 3; i++) {
                unit[i] = Double.parseDouble(splitLine[i]);
            }
            data.add(unit);
            if (j >= 10000) {
                break;
            }
        }

        Tools.toJava(GPSUtil.getMaxDistanceToLine(Tools.readGPSData(data))).forEach(
                x -> textFileLineWriter.writeln(x.toString())
        );
    }
}
