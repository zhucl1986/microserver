package cn.com.agree.evs.tool;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

@Component
public class WatermarkTool {

	public String addWatermark(File pdffile, String watermarkFile) throws Exception {
		try {
			PDDocument pdf = PDDocument.load(pdffile);
			PDImageXObject pdo = PDImageXObject.createFromFile(watermarkFile, pdf);
			PDPageContentStream stream = null;
			for (PDPage page : pdf.getPages()) {
				try {
					stream = new PDPageContentStream(pdf, page, AppendMode.APPEND, false);
					stream.drawImage(pdo, 100, 100);
				} catch (Exception e) {
					throw e;
				} finally {
					if (stream != null) {
						stream.close();
						stream = null;
					}
				}
			}
			pdf.save(pdffile);
			return "success";
		} catch (Exception e) {
			throw e;
		}
	}

}
