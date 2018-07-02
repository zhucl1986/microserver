package cn.com.agree.evs.tool;

import com.alibaba.fastjson.JSON;

import cn.com.agree.evs.common.Base64Utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetPdfImgTool {

    public String getPdfImgData(boolean needBase64Data, int dpi, File pdfFile) throws Exception {
        BufferedImage image = null;
        ByteArrayOutputStream bos = null;
        try {
            List<String> dataList = new ArrayList<String>();
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCounter = 0;
            File imgFile = null;
            String fileName = "";
            String pdfFilePath = pdfFile.getAbsolutePath();
            for (PDPage page : document.getPages()) {
                image = pdfRenderer.renderImageWithDPI(pageCounter, dpi, ImageType.RGB);
                pageCounter++;
                if (needBase64Data) {
                    bos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", bos);
                    dataList.add(new String(Base64Utils.encodeBase64(bos.toByteArray())));
                } else {
                    fileName = pdfFilePath.substring(0, pdfFilePath.lastIndexOf("."))
                            + ("_" + pageCounter) + ".jpg";
                    imgFile = new File(fileName);
                    ImageIO.write(image, "jpg", imgFile);
                    dataList.add(fileName);
                }
            }
            document.close();
            dataList.add(0, pdfFilePath);
            return JSON.toJSONString(dataList);
        } catch (Exception e) {
            throw e;
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    public static void main(String[] args) {
		
	}
}
