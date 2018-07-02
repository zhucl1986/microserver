package cn.com.agree.evs.tool;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToFoConverter;
import org.apache.poi.hwpf.converter.WordToFoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URLEncoder;

@Component
public class Fo2PdfTool {
	private static Logger logger = LoggerFactory.getLogger(Fo2PdfTool.class);
	private static FopFactory fopFactory = FopFactory.newInstance();
	/**
	 * 将FO文件转换成PDF
	 * @param foFile
	 * @param pdfFile
	 * @throws Exception
	 */
	public void fo2pdf(File foFile, File pdfFile) throws Exception {
		logger.info("fo文件转pdf开始");
		OutputStream out = null;
		try {
			File fopXml = new File("./fop/fop.xml");
			String path = URLEncoder.encode(fopXml.getAbsolutePath(), "UTF-8");
			fopFactory.setUserConfig("file:/" + path.replaceAll("\\\\", "/"));
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			out = new FileOutputStream(pdfFile);
			out = new BufferedOutputStream(out);
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Source src = new StreamSource(foFile);
			Result res = new SAXResult(fop.getDefaultHandler());
			transformer.transform(src, res);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != out){
				out.close();
			}
		}
		logger.info("fo文件转pdf结束");
	}

	/**
	 * 将Word转换成FO
	 * @param word
	 * @param fo
	 * @throws Exception
	 */
	public void convertWord2FO(File word, File fo) throws Exception {
		Document doc = process(word);
		FileWriter out = new FileWriter(fo);
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
		// transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.transform(domSource, streamResult);
		out.close();
	}

	private Document process(File docFile) throws Exception {
		HWPFDocumentCore hwpfDocument = WordToFoUtils.loadDoc(docFile);
		WordToFoConverter wordToFoConverter = new WordToFoConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument());
		wordToFoConverter.processDocument(hwpfDocument);
		return wordToFoConverter.getDocument();
	}
}
