
package cn.com.agree.evs.tool;

import org.springframework.stereotype.Component;

import cn.com.agree.evs.common.DateUtils;
import cn.com.agree.evs.common.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetSvgFileTool {

    public File getSvgFile(String signDataFile) throws Exception {
        BufferedWriter bw = null;
        try {
        	String signData = FileUtils.readFile(signDataFile, true);
        	String[] pointData = null;
            String timeFlag = DateUtils.getDateByPattern(DateUtils.allPattern);
            String svgFile = timeFlag + ".svg";
            File output = new File("evsfile/svg/", svgFile);
            bw = new BufferedWriter(new FileWriter(output));
            List<String> lineInfoList = new ArrayList<String>();
            String headInfo = signData.substring(0, signData.indexOf("("));
            String[] tmpInfo = signData.substring(signData.indexOf("(")).split("\\)");
            for (int i = 0; i < tmpInfo.length; i++) {
                lineInfoList.add(tmpInfo[i].replaceAll("\\(", ""));
            }
            String[] head = headInfo.split(",");
            int width = Integer.parseInt(head[0]);
            int height = Integer.parseInt(head[1]);
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bw.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
            bw.write("<svg width=\"" + width + "\" height=\"" + height + "\">\n");
            List<List<String[]>> groupList = new ArrayList<List<String[]>>();
            double a = 0.15;
            double b = 0.15;
            for (String line : lineInfoList) {
                String[] pointDataString = line.split(";");
                String startx = "";
                String starty = "";
                String endx = "";
                String endy = "";
                List<String[]> tmp = new ArrayList<String[]>();
                for (int iterater = 0; iterater < pointDataString.length; iterater++) {
                    String pointString = pointDataString[iterater];
                    pointData = pointString.split(",");
                    startx = endx;
                    starty = endy;
                    endx = pointData[0];
                    endy = pointData[1];
                    if (startx.equals("")) {
                        continue;
                    }
                    Double r = Double.parseDouble(pointData[2]) * 8 / 2;
                    Double sx = Double.parseDouble(startx);
                    Double sy = Double.parseDouble(starty);
                    Double ex = Double.parseDouble(endx);
                    Double ey = Double.parseDouble(endy);
                    Double R = Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
                    if (R == 0.0) continue;
                    Double sin = (ex - sx) / R;
                    Double cos = (ey - sy) / R;
                    Double sx1 = sx + r * cos;
                    Double sx2 = sx - r * cos;
                    Double sy1 = sy - r * sin;
                    Double sy2 = sy + r * sin;
                    tmp.add(new String[]{String.valueOf(sx1), String.valueOf(sy1), String.valueOf(sx2), String.valueOf(sy2)});
                }
                groupList.add(tmp);
            }
            String path = "";
            for (List<String[]> line : groupList) {
                if (line.size() < 4) {
                    if (line.size() < 2) {
                        continue;
                    }
                    double lastpx = Double.parseDouble(line.get(0)[0]);
                    double lastpy = Double.parseDouble(line.get(0)[1]);
                    double nextpx = Double.parseDouble(line.get(line.size() - 1)[0]);
                    double nextpy = Double.parseDouble(line.get(line.size() - 1)[1]);
                    double width1 = Double.parseDouble(pointData[2]) * 100;
                    String circle = "<circle cx=\"" + lastpx + "\" cy=\"" + lastpy + "\" r=\"" + width1 / 2 + "\"/>\n";
                    bw.write(circle);
                    path = "<path d=\"M " + lastpx + " " + lastpy + " L " + nextpx + " " + nextpy + " \" "
                            + "stroke=\"#000000\" fill=\"none\" style=\"stroke-width: " + width1 + "px;\"/>";
                    bw.write(path + "\n");
                    String circle2 = "<circle cx=\"" + nextpx + "\" cy=\"" + nextpy + "\" r=\"" + width1 / 2 + "\"/>\n";
                    bw.write(circle2);
                    continue;
                }
                path = "<path d=\"";
                for (int i = 1; i < line.size(); i++) {
                    String[] point = line.get(i);
                    if (i == 1) {
                        path = path + "M " + point[0] + " " + point[1] + " ";
                    } else {
                        if (i > 1 && i < line.size() - 2) {
                            double lastpx = Double.parseDouble(line.get(i - 2)[0]);
                            double lastpy = Double.parseDouble(line.get(i - 2)[1]);
                            double nextpx = Double.parseDouble(line.get(i)[0]);
                            double nextpy = Double.parseDouble(line.get(i)[1]);
                            double nnextpx = Double.parseDouble(line.get(i + 1)[0]);
                            double nnextpy = Double.parseDouble(line.get(i + 1)[1]);
                            double px = Double.parseDouble(line.get(i - 1)[0]);
                            double py = Double.parseDouble(line.get(i - 1)[1]);
                            double pAx = px + (nextpx - lastpx) * a;
                            double pAy = py + (nextpy - lastpy) * a;
                            double pBx = nextpx - (nnextpx - px) * b;
                            double pBy = nextpy - (nnextpy - py) * b;
                            path = path + "C " + String.valueOf(pAx) + " " + String.valueOf(pAy) + " "
                                    + String.valueOf(pBx) + " " + String.valueOf(pBy)
                                    + " " + point[0] + " " + point[1] + " ";
                        } else
                            path = path + "L " + point[0] + " " + point[1] + " ";
                    }
                }
                for (int i = line.size() - 1; i >= 1; i--) {
                    String[] point = line.get(i);
                    if (i < line.size() - 2 && i > 1) {
                        double lastpx = Double.parseDouble(line.get(i + 2)[2]);
                        double lastpy = Double.parseDouble(line.get(i + 2)[3]);
                        double nextpx = Double.parseDouble(line.get(i)[2]);
                        double nextpy = Double.parseDouble(line.get(i)[3]);
                        double nnextpx = Double.parseDouble(line.get(i - 1)[2]);
                        double nnextpy = Double.parseDouble(line.get(i - 1)[3]);
                        double px = Double.parseDouble(line.get(i + 1)[2]);
                        double py = Double.parseDouble(line.get(i + 1)[3]);
                        double pAx = px + (nextpx - lastpx) * a;
                        double pAy = py + (nextpy - lastpy) * a;
                        double pBx = nextpx - (nnextpx - px) * b;
                        double pBy = nextpy - (nnextpy - py) * b;
                        path = path + "C " + String.valueOf(pAx) + " " + String.valueOf(pAy) + " "
                                + String.valueOf(pBx) + " " + String.valueOf(pBy)
                                + " " + point[2] + " " + point[3] + " ";
                    }
                    path = path + "L " + point[2] + " " + point[3] + " ";
                }
                double lastpx = Double.parseDouble(line.get(2)[2]);
                double lastpy = Double.parseDouble(line.get(2)[3]);
                double nextpx = Double.parseDouble(line.get(1)[0]);
                double nextpy = Double.parseDouble(line.get(1)[1]);
                double nnextpx = Double.parseDouble(line.get(2)[0]);
                double nnextpy = Double.parseDouble(line.get(2)[1]);
                double px = Double.parseDouble(line.get(1)[2]);
                double py = Double.parseDouble(line.get(1)[3]);
                double pAx = px + (nextpx - lastpx) * 0.5;
                double pAy = py + (nextpy - lastpy) * 0.5;
                double pBx = nextpx - (nnextpx - px) * 0.5;
                double pBy = nextpy - (nnextpy - py) * 0.5;
                path = path + "C " + String.valueOf(pAx) + " " + String.valueOf(pAy) + " "
                        + String.valueOf(pBx) + " " + String.valueOf(pBy)
                        + " " + String.valueOf(nextpx) + " " + String.valueOf(nextpy) + " ";
                path = path + "\" stroke=\"#000000\"   style=\"stroke-width: 1px;\"/>\n";
                bw.write(path);
            }
            bw.write("</svg>");
            bw.flush();
            return output;
        } catch (Exception e) {
            throw e;
        } finally {
        	try {
	            if (bw != null) {
                    bw.close();
	            }
        	} catch (IOException e) {
        		bw = null;
        	}
        }
    }

}
