package cn.com.agree.evs.controller;

import com.alibaba.fastjson.JSON;

import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;
import cn.com.agree.evs.voucher.CreateAft2Pdf;
import cn.com.agree.evs.voucher.CreateTxt2Pdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MainController {
	@Autowired
	private CreateAft2Pdf createAft2Pdf;
	@Autowired
	private CreateTxt2Pdf createTxt2Pdf;
	
	@RequestMapping("/")
	public String index() {
		return "欢迎使用赞同科技无纸化系统";
	}

	@ResponseBody
	@RequestMapping(value = "/evs/voucher", method = RequestMethod.POST)
	public String createVoucher(@RequestParam(value = "evsData", required = true) String evsData) {
		Map<String, Object> dataMap = DataCache.dataMap.get();
		String operateType = (String) dataMap.get(Constants.operateType);
		String result = "";
		if ("createAftPdf".equals(operateType)) {
			result = createAft2Pdf.create();
		} else if("createPdf".equals(operateType)){
			result = createTxt2Pdf.create();
		} 
		return resultMessage(operateType, result);
	}
	
	@ResponseBody
	@RequestMapping(value = "/evs/seal", method = RequestMethod.POST)
	public String createSeal(@RequestParam(value = "evsData", required = true) String evsData) {
		Map<String, Object> dataMap = DataCache.dataMap.get();
		String operateType = (String) dataMap.get(Constants.operateType);
		String result = "";
		if ("createSeal".equals(operateType)) {
			//result = createSeal.create();
		}
		return resultMessage(operateType, result);
	}

	public String resultMessage(String handleType, String callResult) {
		String errorCode = "000000";
		String errorInfo = "交易成功！";
		if (callResult.startsWith("fail")) {
			if (callResult.startsWith("fail")) {
				errorCode = "000001";
				errorInfo = "交易失败！";
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='GBK'?>" + "<elecVoucher operationType='" + handleType + "'>");
		sb.append("<errorCode>" + errorCode + "</errorCode>" + "<errorInfo>" + errorInfo + "</errorInfo>");
		if (callResult.startsWith("noImgData")) {
			sb.append("<pdf><file>" + callResult.split(";")[1] + "</file></pdf></elecVoucher>");
		} else {
			List<?> list = JSON.parseObject(callResult.trim(), ArrayList.class);
			sb.append("<pdf><file>" + list.get(0) + "</file>");
			for (int i = 1; i < list.size(); i++) {
				sb.append("<imgData>" + list.get(i) + "</imgData>");
			}
			sb.append("</pdf></elecVoucher>");
		}
		return new String(sb);
	}
}
