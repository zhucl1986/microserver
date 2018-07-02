package cn.com.agree.evs.tool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;
import cn.com.agree.evs.common.FileUtils;
import cn.com.agree.evs.common.MathUtils;
import cn.com.agree.evs.common.StringUtils;

import java.io.*;
import java.util.*;

@Component
public class Aft2FoTool {
    private static Logger logger = LoggerFactory.getLogger(Aft2FoTool.class);
    private static Map<Integer, List<String>> printData;
    @Value("${evs.aft-encoding}")
    private String aftEncoding;
    @Autowired
    private GetSvgFileTool getSvgFileTool;

    @SuppressWarnings("unchecked")
	public void aft2fo(File foFile) throws Exception {
    	logger.info("AFT2Fo文件转换开始");
    	Map<String, Object> dataMap = DataCache.dataMap.get();
        String printDataText = (String) dataMap.get(Constants.printDataText);
        if(StringUtils.isNullOrBlank(printDataText)){
            return;
        }
        int aftPathNum = 1;
        String[] srcPaths = printDataText.split(Constants.separator1);
        String[] desPaths = FileUtils.removeNotExistPath(srcPaths);
        if (desPaths.length == 0){
            logger.info("可读取AFT文件个数为零");
            return;
        }
        aftPathNum = desPaths.length;
        Map<Integer, Document> pageDocMap = new HashMap<>();
        for (int i = 0; i < aftPathNum; i++){
            String path = desPaths[i];
            Document document = FileUtils.readAftContent(path, aftEncoding);
            pageDocMap.put(i, document);
        }
        if (pageDocMap.isEmpty()){
            return;
        }
        printData = prepareMultiPrtData(pageDocMap);
        // 计算页数
        int pageSize = claculatePageNum(dataMap, aftPathNum);
        logger.debug("总共页数为：" + pageSize);
        //预处理轨迹数据
        createSvgFile(dataMap);
        // 预处理FO模板
        String paperFo = replacePaperSize(dataMap);
        Document doc = DocumentHelper.parseText(paperFo);
        Element root = doc.getRootElement();
        // 2.添加模板图片
        for (int i = 0; i < pageSize; i++) {
        	DataCache.putValue("last_height", 0f);
        	DataCache.putValue("last_y", 0f); 
        	createPageElement(root, "section_s" + i);
            List<Element> pages = root.elements("page-sequence");
            Element pageElement = pages.get(i);
            createTemplateElement(dataMap, pageElement);
            createSealElement(dataMap, pageElement, i);
            createSignElement(dataMap, pageElement, i);
        }
        FileUtils.saveDocument(doc, foFile);
        logger.info("AFT2Fo文件转换结束");
    }

    private void createSvgFile(Map<String,Object> dataMap) throws Exception {
        String signImagePath = (String) dataMap.get(Constants.signImageSrc);
        File signImageFile;
        File svgFile;
        if (signImagePath.contains(":")){
            signImageFile = new File(signImagePath);
        } else {
            String userDir = System.getProperty("user.dir");
            String baseDir = userDir + File.separator + Constants.evsFileDir + File.separator + Constants.svgDir;
            signImageFile = new File(baseDir, signImagePath);
        }
        if (signImageFile.getName().endsWith(".txt")){
            svgFile = getSvgFileTool.getSvgFile(signImageFile.getAbsolutePath());
        } else {
            svgFile = signImageFile;
        }
        dataMap.put("SvgFile", svgFile);
    }
    
    private void createSignElement(Map<String,Object> dataMap, Element pageElement, int pageNo) throws Exception {
    	float last_height = (float) DataCache.getValue("last_height");
    	float last_y = (float) DataCache.getValue("last_y");
        String top = "";
        String left = "";
        float y = 0;
        float x = 0;
        String printDataLocation = (String) dataMap.get(Constants.printDataLocation);
        String[] location = printDataLocation.split("#");
        float x_advl = Float.parseFloat(location[0]);
        float y_advl = Float.parseFloat(location[1]);
        String locateType = (String) dataMap.get(Constants.signImageLocateType);
        String sealSize = (String) dataMap.get(Constants.signImageSize);
        if ("0".equals(locateType)) {// 文字定位
            String sealLocateData = (String) dataMap.get(Constants.signImageLocateData);
            String locateData = textLocate(pageNo, sealSize, sealLocateData);
            logger.info("签名文字定位" + locateData);
            if (locateData != null) {
                top = locateData.split("\\|", -1)[1];
                y = Float.parseFloat(locateData.split("\\|", -1)[1]);
                y = (y * 3 + y_advl) / 3;
                top = "" + (0 - last_height - last_y+ y);
                DataCache.putValue("last_y", y);
                x = Float.parseFloat(locateData.split("\\|", -1)[0]);
                x = (x * 3 + x_advl) / 6;
                left = "" + x;
            }
        } else {// 坐标定位
            String sealImageLocation = (String) dataMap.get(Constants.signImageLocation);
            String[] sealLocation = sealImageLocation.split("#");
            x = Float.parseFloat(sealLocation[1]);
            y = Float.parseFloat(sealLocation[0]);
            left = "" + x;
            top = "" + (0 - last_height - last_y + y);
            DataCache.putValue("last_y", y);
        }
        String[] size = sealSize.split("#");
        DataCache.putValue("last_height", Float.parseFloat(size[1]));
        File svgFile = (File) dataMap.get("SvgFile");
        createGraphicElement2(pageElement, size, top, left, svgFile);
    }

    private void createSealElement(Map<String,Object> dataMap, Element pageElement, int pageNo) {
    	float last_height = (float) DataCache.getValue("last_height");
    	float last_y = (float) DataCache.getValue("last_y");
        String top = "";
        String left = "";
        float y = 0;
        float x = 0;
        String printDataLocation = (String) dataMap.get(Constants.printDataLocation);
        String[] location = printDataLocation.split("#");
        float x_advl = Float.parseFloat(location[0]);
        float y_advl = Float.parseFloat(location[1]);
        String locateType = (String) dataMap.get(Constants.sealImageLocateType);
        String sealSize = (String) dataMap.get(Constants.sealImageSize);
        if ("0".equals(locateType)) {// 文字定位
            String sealLocateData = (String) dataMap.get(Constants.sealImageLocateData);
            String locateData = textLocate(pageNo, sealSize, sealLocateData);
            logger.info("文字定位" + locateData);
            if (locateData != null) {
                top = locateData.split("\\|", -1)[1];
                y = Float.parseFloat(locateData.split("\\|", -1)[1]);
                y = (y * 3 + y_advl) / 3;
                top = "" + (0 - last_height - last_y+ y);
                DataCache.putValue("last_y", y);
                x = Float.parseFloat(locateData.split("\\|", -1)[0]);
                x = (x * 3 + x_advl) / 6;
                left = "" + x;
            }
        } else {// 坐标定位
            String sealImageLocation = (String) dataMap.get(Constants.sealImageLocation);
            String[] sealLocation = sealImageLocation.split("#");
            x = Float.parseFloat(sealLocation[1]);
            y = Float.parseFloat(sealLocation[0]);
            left = "" + x;
            top = "" + (0 - last_height - last_y + y);
            DataCache.putValue("last_y", y);
        }
        String[] size = sealSize.split("#");
        DataCache.putValue("last_height", Float.parseFloat(size[1]));
        String sealImagePath = (String) dataMap.get(Constants.sealImageSrc);
        File sealFile = new File(sealImagePath);
        createGraphicElement(pageElement, size, top, left, sealFile);
    }

    private void createGraphicElement(Element pageElement, String[] size, String top, String left, File imageFile){
        Element flow = pageElement.element("flow");
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
        element.addAttribute("src", "file:" + imageFile.getAbsolutePath().replaceAll("\\\\", "/"));
        element.addAttribute("content-width", size[0] + Constants.calUnit);
        element.addAttribute("content-height", size[1] + Constants.calUnit);
    }

    private void createGraphicElement2(Element pageElement, String[] size, String top, String left, File imageFile){
        Element flow = pageElement.element("flow");
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

        element.addAttribute("src", "./" + imageFile.getPath().replace("\\", "/"));
        element.addAttribute("content-width", size[0] + Constants.calUnit);
        element.addAttribute("content-height", size[1] + Constants.calUnit);
    }

    private String textLocate(int pageNo, String size, String textStr) {
        String key = "";
        String value = "";
        int num = 0;
        int col_index = 0;
        int x = 0;
        String text = textStr.substring(textStr.indexOf("#") + 1);
        int times = Integer.parseInt(textStr.substring(0, textStr.indexOf("#")));
        if (pageNo < 0) {
            pageNo = printData.size() - 1;
        }
        List<String> list = printData.get(pageNo);
        for (int j = 0; j < list.size(); j++) {
            String[] prtData = ((String) list.get(j)).split("#@#");
            key = prtData[0];
            value = prtData[1];
            key = key.substring(key.indexOf("#") + 1);
            if (key.contains(text)) {
                col_index = 0;
                while (col_index != -1) {
                    col_index = key.indexOf(text, col_index);
                    if (col_index == -1) {
                        break;
                    } else {
                        col_index = col_index + text.length();
                        num++;
                    }
                    if (num == times) {
                        x = key.substring(0, key.indexOf(text)).getBytes().length + 1;
                        break;
                    }
                }
                if (num == times) {
                    break;
                }
            }
        }
        int imgX = 0;
        int imgY = 0;
        if (null != size && !"".equals(size)) {
            String[] sizes = size.split("#");
            imgX = Integer.parseInt(sizes[0])/2;
            imgY = Integer.parseInt(sizes[1])/2;
        }
        return (Float.parseFloat(value.split("\\|", -1)[0]) + x + imgX) + "|" + (Float.parseFloat(value.split("\\|", -1)[1]) - imgY);
    }

    private void createTemplateElement(Map<String, Object> dataMap, Element pageElement){
    	float last_height = (float) DataCache.getValue("last_height");
    	float last_y = (float) DataCache.getValue("last_y");
        Element flowElement = pageElement.element("flow");
        Element graphics = flowElement.addElement("fo:block");
        graphics.setParent(flowElement);
        String top = "";
        String left = "";
        float y = 0;
        float x = 0;
        //模板定位
        String tempImageLocation = (String) dataMap.get(Constants.tempImageLocation);
        String[] location = tempImageLocation.split("#");
        String tempLocateType = (String) dataMap.get(Constants.tempLocateType);
        if (null == tempLocateType) {
            x = Float.parseFloat(location[1]);
            y = Float.parseFloat(location[0]);
            left = "" + x;
            top = "" + (0 - last_height - last_y + y);
            DataCache.putValue("last_y", y);
        }
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
        String tempPath = (String) dataMap.get(Constants.tempImageSrc);
        File tempFile = new File(tempPath);
        element.addAttribute("src", "file:" + tempFile.getAbsolutePath().replaceAll("\\\\", "/"));
        String tempImageSize = (String) dataMap.get(Constants.tempImageSize);
        String[] size = tempImageSize.split("#");
        element.addAttribute("content-width", size[0] + Constants.calUnit);
        element.addAttribute("content-height", size[1] + Constants.calUnit);
        DataCache.putValue("last_height", Float.parseFloat(size[1]));
    }

    private Element createPageElement(Element root, String pageId) {
        Element page = root.addElement("fo:page-sequence");
        page.addAttribute("id", pageId);
        page.addAttribute("format", "");
        page.addAttribute("master-reference", "s1");
        page.addElement("fo:flow").addAttribute("flow-name", "xsl-region-body");
        return page;
    }


    /**
     * 计算页数
     */
    private int claculatePageNum(Map<String, Object> dataMap, int aftPathNum){
    	String maxTop = DataCache.getValue("maxTop") == null ? "0.00" : (String) DataCache.getValue("maxTop");
        // 1.计算pdf页数
        int pageSize = 1;
        String paperSize = (String) dataMap.get(Constants.paperSize);
        String bottomLineSpace = (String) dataMap.get(Constants.bottomLineSpace);
        if (null == bottomLineSpace || "".equals(bottomLineSpace)) {
            String space = MathUtils.subtractBigDecimal(paperSize.split("#")[1], maxTop);
            if (Float.valueOf(space) >= 0) {
                bottomLineSpace = space;
            } else {
                bottomLineSpace = "20.00";
            }
        }
        if (aftPathNum == 1) {
            if ("0.00".equals(maxTop)) { //存在pdf转pdf业务，print_data值为空
                pageSize = 1;
            } else {
                String page = MathUtils.divideBigDecimal(maxTop,
                        MathUtils.subtractBigDecimal(paperSize.split("#")[1], bottomLineSpace));
                int size = (int) Math.ceil(Float.valueOf(page));
                pageSize = size == 0 ? 1 : size;
            }
        } else {
            pageSize = aftPathNum;
        }
        return pageSize;
    }
    /**
     * 准备多个aft文件打印数据
     *
     * @throws Exception
     */
    private Map<Integer, List<String>> prepareMultiPrtData(Map<Integer, Document> pageDocMap) throws Exception {
        Map<Integer, List<String>> prtDatas = new HashMap<Integer, List<String>>();
        Iterator<Integer> iterator = pageDocMap.keySet().iterator();
        while (iterator.hasNext()) {
            int pageno = iterator.next();
            Document doc = pageDocMap.get(pageno);
            List<?> dataList = doc.getRootElement().element("Content").elements();
            List<String> prtData = new ArrayList<String>();
            for (int i = 0; i < dataList.size(); i++) {
                Element element = (Element) dataList.get(i);
                if (element.getName().equalsIgnoreCase("Table")) {// 处理表格式内容
                    String tableLeft = element.attributeValue("Left");
                    String top = element.attributeValue("Top");
                    String width = "";
                    String left = tableLeft;
                    List<?> trs = element.elements("Tr");
                    Element tr = null;
                    Element td = null;
                    for (int k = 0; k < trs.size(); k++) {
                        tr = (Element) trs.get(k);
                        width = "";
                        left = tableLeft;
                        if (k > 0) {
                            top = MathUtils.addBigDecimal(top, tr.attributeValue("Height"));
                        }
                        try {
                            setMaxTop(top);
                        } catch (Exception e) {
                            logger.error("设置top值出错!", e);
                            e.printStackTrace();
                        }
                        for (int j = 0; j < tr.elements("Td").size(); j++) {
                            td = (Element) (tr.elements("Td").get(j));
                            String textParams = MathUtils.addBigDecimal(left, width) + "|" + top;
                            prtData.add(td.getText() + "#@#" + textParams);
                            width = MathUtils.addBigDecimal(width, td.attributeValue("Width"));
                        }
                    }
                } else {// 处理普通文本内容
                    String isPrint = element.attributeValue("IsPrint");
                    if (null == isPrint || "".equals(isPrint) || Boolean.valueOf(isPrint)) {
                        String textParams = element.attributeValue("Left") + "|" + element.attributeValue("Top");
                        setMaxTop(element.attributeValue("Top"));
                        element = getEChild(element, textParams);
                        prtData.add(element.getText() + "#@#" + textParams);
                    }
                }
            }
            prtDatas.put(pageno, prtData);
        }
        return prtDatas;
    }

    private void setMaxTop(String top) throws Exception {
    	String maxTop = "0.00";
        float dtop = 0.00f;
        try {
            dtop = Float.valueOf(top);
        } catch (Exception e) {
            return;
        }
        if ("".equals(top)) {
            maxTop = top;
        } else if (dtop > Float.valueOf(maxTop)) {
            maxTop = top;
        }
        DataCache.putValue("maxTop", maxTop);
    }

    private Element getEChild(Element child, String textParams) {
        if (child.elements().size() > 0) {
            textParams = textParams + Constants.separator1 + ((Element) child.elements().get(0)).getName();
            child = getEChild((Element) child.elements().get(0), "");
        }
        return child;
    }

    private String replacePaperSize(Map<String, Object> dataMap){
        String paperSize = (String) dataMap.get(Constants.paperSize);
        String[] size = paperSize.split("#");
        String paperFo = Constants.paperFo.replace("@page_height@", size[1] + Constants.calUnit)
                .replace("@page_width@", size[0] + Constants.calUnit)
                .replace("@body_margin_bottom@", "-" + size[1] + Constants.calUnit)
                .replace("@after_extent@", size[1] + Constants.calUnit);
        return paperFo;
    }

	public void addData(File pdfFile) throws IOException {
    	logger.info("pdf追加数据开始");
    	Map<String, Object> dataMap = DataCache.dataMap.get();
        if (printData != null && !printData.isEmpty()) {
            Iterator<Integer> iterator = printData.keySet().iterator();
            PDDocument pdf = PDDocument.load(pdfFile);
            logger.info("pdf页数为：" + pdf.getNumberOfPages());
            File pFontBlackFile = DataCache.pFontBlackFile;
            PDType0Font	pFontBlack;
            if (null == pFontBlackFile) {
            	pFontBlack = PDType0Font.load(pdf, new File(Constants.fontSimHei));
			} else {
				pFontBlack = PDType0Font.load(pdf, pFontBlackFile);
			}
            File pFontNormalFile = DataCache.pFontNormalFile;
            PDType0Font pFontNormal;
            if (null == pFontNormalFile) {
            	pFontNormal = PDType0Font.load(pdf, new File(Constants.fontSimSun));
			} else {
				pFontNormal = PDType0Font.load(pdf, pFontNormalFile);
			}         
            while (iterator.hasNext()) {
                PDPageContentStream stream = null;
                try {
                    int pageNo = iterator.next();
                    List<?> prtDatasList = printData.get(pageNo);
                    if (prtDatasList == null || prtDatasList.isEmpty()) {
                        continue;
                    }
                    float x = 0;
                    float y = 0;
                    String printDataLocation = (String) dataMap.get(Constants.printDataLocation);
                    String[] location = printDataLocation.split("#");
                    float x_advl = Float.parseFloat(location[0]);
                    float y_advl = Float.parseFloat(location[1]);
                    float size = (float) 10.5;

                    String bottomLineSpace = (String) dataMap.get(Constants.bottomLineSpace);
                    if (null == bottomLineSpace || "".equals(bottomLineSpace)) {
                        bottomLineSpace = "10.00";
                    }
                    String notice = (String) dataMap.get(Constants.pdfNotice);
                    String[] notices = null;
                    if (notice != null) {
                        notices = notice.split("@@");
                    }
                    String value = "";
                    String text = "";
                    String value_y = "";
                    boolean isNotice = false;
                    PDPage page = pdf.getPage(pageNo);
                    float height = page.getArtBox().getUpperRightY();
                    stream = new PDPageContentStream(pdf, page, AppendMode.APPEND, true);
                    stream.beginText();
                    PDType0Font pFont = null;
                    for (int m = 0; m < prtDatasList.size(); m++) {
                        String[] prtData = ((String) prtDatasList.get(m)).split("#@#");
                        text = prtData[0];
                        value = prtData[1];
                        value_y = value.split("\\|", -1)[1];
                        size = (float) 10.5;
                        pFont = pFontNormal;
                        isNotice = false;
                        String page_y = value_y;
                        x = (float) ((Float.parseFloat(value.split("\\|", -1)[0]) * Constants.heightCoeff + x_advl));
                        y = (float) (height - ((Float.parseFloat(page_y) * Constants.heightCoeff + y_advl)));
                        if (value.contains("Doubleheight") && value.contains("Doublewidth")) {
                            size = size * 2;
                            y = (float) (height - ((Float.parseFloat(page_y) * Constants.heightCoeff + y_advl + size/2)));
                        }
                        if (value.contains("Bold")) {
                            pFont = pFontBlack;
                        }
                        if (notices != null) {
                            for (int i = 0; i < notices.length; i++) {
                                if (text.contains(notices[i].split("\\|")[0])) {
                                    isNotice = true;
                                    stream.setFont(pFont, size);
                                    stream.newLineAtOffset(x, y);
                                    stream.showText(text.substring(0, text.indexOf(notices[i].split("\\|")[0])));
                                    if (notices[i].split("\\|")[3].equals("1")) {
                                        stream.setFont(pFontBlack, Float.parseFloat(notices[i].split("\\|")[1]));
                                    } else {
                                        stream.setFont(pFont, Float.parseFloat(notices[i].split("\\|")[1]));
                                    }
                                    if (!notices[i].split("\\|")[2].equals("")) {
                                        stream.setNonStrokingColor(
                                                Integer.parseInt(notices[i].split("\\|")[2].split("#")[0]),
                                                Integer.parseInt(notices[i].split("\\|")[2].split("#")[1]),
                                                Integer.parseInt(notices[i].split("\\|")[2].split("#")[2]));
                                    }
                                    stream.showText(notices[i].split("\\|")[0]);
                                    stream.setFont(pFont, size);
                                    stream.setNonStrokingColor(0, 0, 0);
                                    stream.showText(text.substring(text.indexOf(notices[i].split("\\|")[0])
                                            + notices[i].split("\\|")[0].length()));
                                    stream.newLineAtOffset(-x, -y);
                                }
                            }
                        }
                        if (!isNotice) {
                            stream.setFont(pFont, size);
                            stream.newLineAtOffset(x, y);
                            stream.showText(text);
                            stream.newLineAtOffset(-x, -y);
                        }
                    }
                    stream.endText();
                    stream.close();
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            pdf.save(pdfFile);
            pdf.close();
        }
        logger.info("pdf追加数据结束");
    }
}
