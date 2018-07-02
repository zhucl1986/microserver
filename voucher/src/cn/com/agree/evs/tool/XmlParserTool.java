package cn.com.agree.evs.tool;

import org.dom4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class XmlParserTool {
    private static Logger logger = LoggerFactory.getLogger(XmlParserTool.class);
    
    public Map<String, Object> parse(String methodName, String xmlStr) {
        try {
            Document document = DocumentHelper.parseText(xmlStr);
            if ("createVoucher".equals(methodName)) {
            	Map<String, Object> reqMap = parseVoucherData(document);
            	DataCache.dataMap.set(reqMap);
            	return reqMap;
			} else if("createSeal".equals(methodName)){
				Map<String, Object> reqMap = parseSealData(document);
            	DataCache.dataMap.set(reqMap);
            	return reqMap;
			}
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("请求XML报文解析异常");
        }
		return null;
    }

    @SuppressWarnings("unchecked")
	private Map<String, Object> parseSealData(Document document) {
        Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
        Element root = document.getRootElement();
        List<?> rootAttributes = root.attributes();
        for (Object rootAttribute : rootAttributes) {
            Attribute attribute = (Attribute) rootAttribute;
            paramsMap.put(root.getName() + Constants.separator + attribute.getName(), attribute.getValue());
        }
        Element sealElement = root.element(Constants.seal);
        List<?> sealAttributes = sealElement.attributes();
        for (Object sealAttribute : sealAttributes) {
            Attribute attribute = (Attribute) sealAttribute;
            paramsMap.put(sealElement.getName() + Constants.separator + attribute.getName(), attribute.getValue());
        }
        List<Element> elements = sealElement.elements();
        for(Element element : elements) {
        	List<?> elemAttributes = element.attributes();
            for (Object elemAttribute : elemAttributes) {
                Attribute attribute = (Attribute) elemAttribute;
                paramsMap.put(element.getName() + Constants.separator + attribute.getName(), attribute.getValue());
            }
            String text = element.getText();
            paramsMap.put(element.getName() + Constants.separator + Constants.text, text);
        }
		return paramsMap;
	}

	private Map<String, Object> parseVoucherData(Document document) {
        Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
        Element root = document.getRootElement();
        List<?> rootAttributes = root.attributes();
        for (Object rootAttribute : rootAttributes) {
            Attribute attribute = (Attribute) rootAttribute;
            paramsMap.put(root.getName() + Constants.separator + attribute.getName(), attribute.getValue());
        }
        // 1.pdf节点
        Element pdfEle = root.element(Constants.pdf);
        parseElement2Map(paramsMap, pdfEle);
        // 2.数据节点
        String[] params = { Constants.tempImage, Constants.sealImage, Constants.signImage, Constants.printData };
        for (int i = 0; i < params.length; i++) {
            List<?> eles = pdfEle.elements(params[i]);
            for (Object eleObj : eles) {
                Element element = (Element) eleObj;
                if (null == element) {
                    continue;
                }
                parseElement2Map(paramsMap, element);
            }
        }
        // 3.安全节点
        boolean enableSecuritySetting = Boolean.parseBoolean((String) paramsMap.get(Constants.enableSecurity));
        if (enableSecuritySetting) {
            Map<String, Object> secrityMap = new HashMap<String, Object>();
            Element securityEle = pdfEle.element(Constants.securityControl);
            if (Boolean.parseBoolean(securityEle.attributeValue(Constants.DEFAULT))) {
                secrityMap.put(Constants.defaultSettings, true);
            } else {
                List<?> eles = securityEle.elements();
                for (int i = 0; i < eles.size(); i++) {
                    Element ele = (Element) eles.get(i);
                    secrityMap.put(ele.getName(), ele.getText());
                }
            }
            paramsMap.put(Constants.securityControl, secrityMap);
        }
        // 4.水印节点
        Element wmElement = pdfEle.element(Constants.watermark);
        if (wmElement != null) {
            paramsMap.put(Constants.watermark, wmElement.attributeValue(Constants.src));
        }
        return paramsMap;
    }

    private void parseElement2Map(Map<String, Object> paramsMap, Element ele) {
        List<?> atts = ele.attributes();
        for (int i = 0; i < atts.size(); i++) {
            Attribute att = (Attribute) atts.get(i);
            paramsMap.put(ele.getName() + Constants.separator + att.getName(), att.getValue());
        }
        String[] attss = new String[] {"src","location","size","pageNo","locateType","locateData"};
        for(String att : attss) {
            paramsMap.put(ele.getName() + Constants.separator + att, ele.attributeValue(att));
        }
        List<?> children = ele.elements();
        if (children.isEmpty() && null != ele.getText() && !"".equals(ele.getText())) {
            paramsMap.put(ele.getName() + Constants.separator + Constants.text, ele.getText());
        }
    }
    
    
}
