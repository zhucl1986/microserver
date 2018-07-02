package cn.com.agree.evs.tool;

import com.alibaba.fastjson.JSON;

import cn.com.agree.evs.common.BaseKeys;
import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;
import cn.com.agree.evs.common.DateUtils;
import cn.com.agree.evs.common.FileUtils;
import cn.com.agree.evs.common.StringUtils;
import cn.com.agree.evs.entity.PrintDataParser;
import cn.com.agree.evs.entity.SubPrintData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Txt2FoTool {
	private static Log logger = LogFactory.getLog(Txt2FoTool.class);

	@Autowired
	Environment environment;
	@Autowired
	PrintDataParser printDataParser;
	@Autowired
	GetSvgFileTool getSvgFileTool;

	public void createFo(String newFoFile, String foSaveDir) throws Exception {
		Map<String, Object> map = DataCache.dataMap.get();
		File foSaveFile = new File(foSaveDir);
		String paperFo = replacePaperSize(map);
		Document doc = DocumentHelper.parseText(paperFo);
		Element root = doc.getRootElement();
		List<String> printDataList = handleCutSheet();
		String printDataLocation = (String) map.get(Constants.printDataLocation);
		int pageNum = printDataList.size();
		for (int i = 0; i < printDataList.size(); i++) {
			Element page = createPageElement(root, "section_s" + i);
			// 1.创建模板
			createTemplateElement(page, map, foSaveFile);
			// 2.创建内容
			String printData = printDataList.get(i);
			if (!StringUtils.isNullOrBlank(printData)) {
				createTextElement(printData, page, printDataLocation);
			}
			// 3.创建印章
			createGraphicsInfo(Constants.sealImage, page, map, foSaveFile, pageNum, i); 
		    // 4.创建签名
			createGraphicsInfo(Constants.signImage, page, map, foSaveFile, pageNum, i);
		
		}
		// 5.保存文档
		FileUtils.saveDocument(doc, new File(foSaveFile, newFoFile));
	}

	private String replacePaperSize(Map<String, Object> dataMap) {
		String paperSize = (String) dataMap.get(Constants.paperSize);
		String[] size = paperSize.split("#");
		String paperFo = Constants.paperFo.replace("@page_height@", size[1] + Constants.calUnit)
				.replace("@page_width@", size[0] + Constants.calUnit)
				.replace("@body_margin_bottom@", "-" + size[1] + Constants.calUnit)
				.replace("@after_extent@", size[1] + Constants.calUnit);
		return paperFo;
	}

	/**
	 * 印章、签名页面打印逻辑
	 * @param page
	 * @param map
	 * @param templateDir
	 * @param pageNum 总页数
	 * @param curPageNo 当前页码
	 * @throws Exception
	 */
	private void createGraphicsInfo(String imagType, Element page, Map<String, Object> map, File templateDir, int pageNum, int curPageNo) throws Exception {
		String pageNo = "";
		if (Constants.sealImage.equals(imagType)) {
			pageNo = (String) map.get(Constants.sealImagePageNo);
		}
		if (Constants.signImage.equals(imagType)) {
			pageNo = (String) map.get(Constants.signImagePageNo);
		}
		if (StringUtils.isNullOrBlank(pageNo)) { //页码送空，默认最后一页
			if (curPageNo == pageNum - 1) {
				createGraphicsElement(imagType, page, map, templateDir);
			}
			return;
		} else if ("*".equals(pageNo)) {
			createGraphicsElement(imagType, page, map, templateDir);
		}
	}

	private void relocateImage(Map<String, Object> map, int paperLength, int dataLocation) {
		//印章图片重新定位
		String sealImageLocation = (String) map.get(Constants.sealImageLocation);
		String[] location = sealImageLocation.split("#");
		String top = StringUtils.isNullOrBlank(location[0]) ? "0" : location[0];
		int imgTop = Integer.parseInt(top);
		String imageSize = (String) map.get(Constants.sealImageSize);
		int imgHeight = new BigDecimal(imageSize.split("#")[1]).intValue() + 1;
		String locationAfter = "";
		if (dataLocation < imgHeight && imgTop < 0) {
			locationAfter = "0#" + location[1];
		} else if ((paperLength - dataLocation) < imgHeight && imgTop > 0) {
			locationAfter = "-" + imgHeight + "#" + location[1];
		}
		map.put(Constants.sealImageLocation, locationAfter);
	}

	/**
     * 拆分凭证打印内容
     * @param pdfObj
     * @return 打印内容分组
     */
	private List<String> handleCutSheet(){
		Map<String, Object> map = DataCache.dataMap.get();
		//1.分页判断
		String isDataInFile = (String) map.get(Constants.printDataInFile);
		String printData = (String) map.get(Constants.printDataText);
		if(isDataInFile != null && isDataInFile.equals("true")){
			printData = FileUtils.readFile(printData, false);
		}
		List<String> printDataList = new ArrayList<String>();
		if(StringUtils.isNullOrBlank(printData)){
			return printDataList;
		}
		String multiSheet = (String) map.get(Constants.printDataMultiSheet);
		if(!StringUtils.isNullOrBlank(multiSheet) && "true".equals(multiSheet)){
			printDataList = JSON.parseObject(printData, List.class);
		}else{
			int paperLength = Integer.parseInt(((String) map.get(Constants.paperSize)).split("#")[1]);
			String[] dataArray = printData.split("\n",-1);
			StringBuffer sheetData = new StringBuffer();
			for (int i = 0; i < dataArray.length; i++) {
				sheetData.append(dataArray[i]);
				sheetData.append("\n");
				if(i > 0 && i % (paperLength/Constants.lineHeight - 2 * Constants.lineHeight) == 0){ //距离底边空隙为两倍行高
					printDataList.add(new String(sheetData));
					sheetData = new StringBuffer();
				}
			}
			if(sheetData.length() > 0 && !new String(sheetData).equals("\n")){
				printDataList.add(new String(sheetData));
			}
			if(printDataList.size() > 1){
				//relocateImage(map, paperLength, new String(sheetData).split("\n",-1).length * Constants.lineHeight);
			}
		}
		if(printDataList.size() == 0){
			printDataList.add("");
		}
		return printDataList;
	}

	/**
	 * 创建页面信息
	 * 
	 * @param root
	 * @param pageId
	 * @return
	 */
	private Element createPageElement(Element root, String pageId) {
		Element page = root.addElement("fo:page-sequence");
		page.addAttribute("id", pageId);
		page.addAttribute("format", "");
		page.addAttribute("master-reference", "s1");
		page.addElement("fo:flow").addAttribute("flow-name", "xsl-region-body");
		return page;
	}

	/**
	 * 创建图片信息
	 * 
	 * @param page
	 * @param imageObj
	 * @param tempDir
	 * @throws Exception
	 */
	private void createTemplateElement(Element page, Map<String, Object> map, File tempDir) throws Exception {
		String tempImageSrc = (String) map.get(Constants.tempImageSrc);
		if (StringUtils.isNullOrBlank(tempImageSrc)) {
			return;
		}
		File tempFile = new File(tempImageSrc);
		if (!tempFile.exists()) {
			return;
		}
		Element flow = page.element("flow");
		Element graphics = flow.addElement("fo:block");
		graphics.setParent(flow);
		String location = (String) map.get(Constants.tempImageLocation);
		String[] locationParams = location.split("#");
		String top = locationParams[0].equals("") ? "0" : locationParams[0];
		String left = locationParams[1].equals("") ? "0" : locationParams[1];
		graphics.addAttribute("font-size", "10.5pt");
		graphics.addAttribute("line-height", "100%");
		graphics.addAttribute("space-after", "0" + Constants.calUnit);
		graphics.addAttribute("space-before", "0" + Constants.calUnit);
		graphics.addAttribute("margin-top", top + Constants.calUnit);
		graphics.addAttribute("margin-left", left + Constants.calUnit);
		Element element = graphics.addElement("inline", "http://www.w3.org/1999/XSL/Format").addAttribute("font-size",
				"10.5pt");
		element = element.addElement("block");
		element = element.addElement("inline").addAttribute("font-size", "10.5pt");
		element = element.addElement("external-graphic");
		element.addAttribute("text-align", "center");
		element.addAttribute("src", "file:" + tempFile.getAbsolutePath().replaceAll("\\\\", "/"));
		String size = (String) map.get(Constants.tempImageSize);
		String[] params = size.split("#");
		element.addAttribute("content-width", params[0] + Constants.calUnit);
		element.addAttribute("content-height", params[1] + Constants.calUnit);
	}

	/**
	 * 创建图片信息
	 * 
	 * @param page
	 * @param imageObj
	 * @param tempDir
	 * @throws Exception
	 */
	private void createGraphicsElement(String imagType, Element page, Map<String, Object> imageObj, File tempDir) throws Exception {
		File imageFile = null;
		if (imageObj == null || imageObj.isEmpty()) {
			return;
		}
		String imagePath = "";
		if (Constants.sealImage.equals(imagType)) {
			imagePath =  (String) imageObj.get(Constants.sealImageSrc);
		}
		if (Constants.signImage.equals(imagType)) {
			imagePath =  (String) imageObj.get(Constants.signImageSrc);
		}
		if (null == imagePath || "".equals(imagePath)) {
			return;
		}
		if (imagePath.contains(":")) {
			imageFile = new File(imagePath);
		} else {
			imageFile = new File(tempDir, imagePath);
		}
		if (imageFile.getName().endsWith(".txt")) {
			imageFile = getSvgFileTool.getSvgFile(imageFile.getAbsolutePath());
		}
		if (!imageFile.exists() || imageFile.isDirectory()) {
			logger.error("需要合成的图片文件不存在：" + imageFile.getAbsolutePath());
			throw new FileNotFoundException("需要合成的图片文件不存在：" + imageFile.getAbsolutePath());
		}
		createGraphicsElementOnPage(imagType, page, imageObj, imageFile);
	}

	private void createGraphicsElement(Element root, String imgProps, File tempDir) throws Exception {
		File tmp_img;
		if (imgProps == null || imgProps.trim().equals("")) {
			return;
		} else {
			if (imgProps.split("\\|")[0].trim().equals("")) {
				return;
			}
			if (imgProps.split("\\|")[0].contains(":")) {
				tmp_img = new File(imgProps.split("\\|")[0]);
			} else {
				tmp_img = new File(tempDir, imgProps.split("\\|")[0]);
			}
		}
		logger.info("imgProps:" + imgProps);
		List<?> pages = root.elements("page-sequence");
		Element page = null;
		String pageNo = imgProps.split("\\|", -1)[3];
		if (pageNo.equals("*")) {
			for (int i = 0; i < pages.size(); i++) {
				page = (Element) pages.get(i);
				createGraphicsElementOnPage(page, imgProps, tmp_img);
			}
			return;
		} else if (pageNo.equals("")) {
			pageNo = "-1";
		}
		int pageIndex = Integer.parseInt(pageNo);
		if (pageIndex > -1) {
			page = (Element) pages.get(pageIndex);
		} else {
			page = (Element) pages.get(pages.size() - 1);
		}
		createGraphicsElementOnPage(page, imgProps, tmp_img);
	}

	private void createGraphicsElementOnPage(String imagType,Element page, Map<String, Object> imageObj, File tmp_img) throws Exception {
		String location = "";
		String size = "";
		if (Constants.sealImage.equals(imagType)) {
			location = (String) imageObj.get(Constants.sealImageLocation);
			size = (String) imageObj.get(Constants.sealImageSize);
		}
		if (Constants.signImage.equals(imagType)) {
			location = (String) imageObj.get(Constants.signImageLocation);
			size = (String) imageObj.get(Constants.signImageSize);
		}
		String[] locationParams = location.split("#");
		String top = StringUtils.isNullOrBlank(locationParams[0]) ? "0" : locationParams[0];
		String left = StringUtils.isNullOrBlank(locationParams[1]) ? "0" : locationParams[1];
		String[] params = size.split("#");
		
		Element flow = page.element("flow");
		Element graphics = flow.addElement("fo:block");
		graphics.setParent(flow);
		graphics.addAttribute("font-size", "10.5pt");
		graphics.addAttribute("line-height", "100%");
		graphics.addAttribute("space-after", "0" + Constants.calUnit);
		graphics.addAttribute("space-before", "0" + Constants.calUnit);
		graphics.addAttribute("margin-top", top + Constants.calUnit);
		graphics.addAttribute("margin-left", left + Constants.calUnit);
		Element element = graphics.addElement("inline", "http://www.w3.org/1999/XSL/Format").addAttribute("font-size", "10.5pt");
		element = element.addElement("block");
		element = element.addElement("inline").addAttribute("font-size", "10.5pt");
		element = element.addElement("external-graphic");
		element.addAttribute("text-align", "center");
		element.addAttribute("src", "file:///" + tmp_img.getAbsolutePath().replaceAll("\\\\", "/"));
		element.addAttribute("content-width", params[0] + Constants.calUnit);
		element.addAttribute("content-height", params[1] + Constants.calUnit);
	}

	/**
	 * 创建页面图片
	 * 
	 * @param page
	 * @param imgProps
	 * @param tmp_img
	 * @throws Exception
	 */
	private void createGraphicsElementOnPage(Element page, String imgProps, File tmp_img) throws Exception {
		Element flow = page.element("flow");
		Element graphics = flow.addElement("fo:block");
		graphics.setParent(flow);

		String top = (imgProps.split("\\|")[1].split("#")[0].equals("")) ? "0" : imgProps.split("\\|")[1].split("#")[0];
		String left = (imgProps.split("\\|")[1].split("#")[1].equals("")) ? "0"
				: imgProps.split("\\|")[1].split("#")[1];

		graphics.addAttribute("font-size", "10.5pt");
		graphics.addAttribute("line-height", "100%");
		graphics.addAttribute("space-after", "0" + Constants.calUnit);
		graphics.addAttribute("space-before", "0" + Constants.calUnit);

		graphics.addAttribute("margin-top", top + Constants.calUnit);
		graphics.addAttribute("margin-left", left + Constants.calUnit);

		Element element = graphics.addElement("inline", "http://www.w3.org/1999/XSL/Format").addAttribute("font-size",
				"10.5pt");
		element = element.addElement("block");
		element = element.addElement("inline").addAttribute("font-size", "10.5pt");
		element = element.addElement("external-graphic");
		element.addAttribute("text-align", "center");
		element.addAttribute("src", tmp_img.getAbsolutePath().replaceAll("\\\\", "/"));
		element.addAttribute("content-width", imgProps.split("\\|")[2].split("#")[0] + Constants.calUnit);
		element.addAttribute("content-height", imgProps.split("\\|")[2].split("#")[1] + Constants.calUnit);
	}

	private void createTextElement(String printData, Element page, String text_location) throws Exception {
		String[] location = text_location.split("#");
		Element flow = page.element("flow");
		Element text = flow.addElement("fo:block");
		text.setParent(flow);
		text.addAttribute("font-size", "10.5pt");
		text.addAttribute("line-height", "100%");
		text.addAttribute("space-after", "0" + Constants.calUnit);
		text.addAttribute("space-before", "0" + Constants.calUnit);
		text.addAttribute("text-align", "justify");
		text.addAttribute("margin-top", location[0] + Constants.calUnit);
		text.addAttribute("margin-left", location[1] + Constants.calUnit);
		Element element = text.addElement("inline", "http://www.w3.org/1999/XSL/Format").addAttribute("font-size",
				"10.5pt");
		Element rootBlock = element.addElement("block");
		List<SubPrintData> subDataList = printDataParser.parsePrintData(printData);
		List<List<SubPrintData>> allDataList = new ArrayList<List<SubPrintData>>();
		List<SubPrintData> lineDataList = new ArrayList<SubPrintData>();
		for (int i = 0; i < subDataList.size(); i++) {
			lineDataList.add(subDataList.get(i));
			if (subDataList.get(i).getDataType().equals(BaseKeys.ENTER)) {
				allDataList.add(lineDataList);
				lineDataList = new ArrayList<SubPrintData>();
			}
			if (i == subDataList.size() - 1 && !lineDataList.isEmpty()) {
				allDataList.add(lineDataList);
				lineDataList = new ArrayList<SubPrintData>();
			}
		}
		Element lineblock = null;
		String fontStr = "";
		Element inLineblock = null;
		for (int i = 0; i < allDataList.size(); i++) {
			lineDataList = allDataList.get(i);
			lineblock = rootBlock.addElement("block");
			lineblock.addAttribute("font-size", "10.5pt");
			lineblock.addAttribute("font-family", "SimSun");
			lineblock.addAttribute("margin-top", "0in");
			lineblock.addAttribute("margin-left", "0in");
			for (int j = 0; j < lineDataList.size(); j++) {
				SubPrintData spd = lineDataList.get(j);
				String dataType = spd.getDataType();
				String fontType = "pdf." + dataType + "_font";
				fontStr = environment.getProperty(fontType);
				fontStr = fontStr == null ? "" : fontStr;
				dealWithBlankSpace(lineblock, fontStr, spd);
			}
			if (lineblock.elements().size() == 0) {
				inLineblock = lineblock.addElement("inline");
				inLineblock.addAttribute("font-family", "SimSun");
				inLineblock.addAttribute("font-size", "10.5pt");
				inLineblock.setText("&#160;");
			}
		}
	}

	private void dealWithBlankSpace(Element lineblock, String fontStr, SubPrintData spd) {
		String text = spd.getData();
		if (text == null || text.equals("")) {
			return;
		}
		String[] stra = text.split("|");
		boolean isBlank = true;
		List<String> list = new ArrayList<String>();
		int index = 0;
		if (!stra[1].equals(" ")) {
			isBlank = false;
		}
		for (int i = 1; i < stra.length; i++) {
			if (stra[i].equals(" ") && !isBlank) {
				list.add(text.substring(index, i - 1));
				isBlank = true;
				index = i - 1;
			} else if (!stra[i].equals(" ") && isBlank) {
				list.add(text.substring(index, i - 1));
				isBlank = false;
				index = i - 1;
			}
		}
		if (index < text.length()) {
			list.add(text.substring(index, text.length()));
		}
		Map<String, Object> map = DataCache.dataMap.get();
		String notice = (String) map.get(Constants.pdfNotice);
		Element inLineblock = null;
		for (int i = 0; i < list.size(); i++) {
			inLineblock = lineblock.addElement("inline");
			inLineblock.addAttribute("font-family", "SimSun");
			if (!fontStr.equals("")) {
				inLineblock.addAttribute("font-size", fontStr);
			} else {
				inLineblock.addAttribute("font-size", "10.5pt");
			}
			if (notice != null && !notice.equals("") && list.get(i).contains(notice.split("\\|", -1)[0])) {
				inLineblock.addAttribute("font-size", notice.split("\\|", -1)[1] + "pt");
				inLineblock.addAttribute("color", notice.split("\\|", -1)[2]);
				if (notice.split("\\|", -1).length > 3) {
					inLineblock.addAttribute("font-weight", notice.split("\\|", -1)[3]);
				}
			}
			inLineblock.setText(list.get(i).replaceAll(" ", "&#160;"));
		}
	}

	public String createFoPlus(String foFileName, String foSaveDir) throws Exception {
		Map<String, Object> map = DataCache.dataMap.get();
		File foSaveFile = new File(foSaveDir);
		File foFile = new File(foSaveFile, foFileName);
		if (!foFile.exists()) {
			throw new RuntimeException("FO文件不存在" + foFile.getAbsolutePath());
		}
		SAXReader reader = new SAXReader();
		Document doc = reader.read(foFile);
		Element root = doc.getRootElement();
		Element flow = root.element("page-sequence").element("flow");
		String[] params = { Constants.sealImage, Constants.signImage };
		for (int i = 0; i < params.length; i++) {
			List valList = (List) map.get(params[i]);
			for (int j = 0; j < valList.size(); j++) {
				createGraphicsElement(flow, (String) valList.get(j), foSaveFile);
			}
		}
		String timeFlag = DateUtils.getDateByPattern(DateUtils.allPattern);
		foFileName = timeFlag + ".fo1";
		foFile = new File(foSaveDir, foFileName);
		FileUtils.saveDocument(doc, foFile);
		dealWithRemark(foFile, foFileName);
		return foFileName.substring(0, foFileName.length() - 1);
	}

	private void dealWithRemark(File foFile, String foFileName) throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(foFile));
			bw = new BufferedWriter(
					new FileWriter(new File(foFile.getParentFile(), foFileName.substring(0, foFileName.length() - 1))));
			String str = "";
			StringBuffer sb = new StringBuffer();
			while (str != null) {
				str = br.readLine();
				if (str == null) {
					break;
				}
				if (str.contains("?")) {
					if (str.contains("<?xml version=")) {
						sb.append(str);
					} else {
						sb.append(str.replaceAll("\\?", "&#160;"));
					}
				} else {
					sb.append(str);
				}
			}
			bw.write(new String(sb));
			bw.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			foFile.delete();
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					bw = null;
				}
			}
		}
	}

}
