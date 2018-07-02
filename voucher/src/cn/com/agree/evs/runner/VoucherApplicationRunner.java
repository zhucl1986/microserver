package cn.com.agree.evs.runner;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;

/**
 * @subject 无纸化预加载
 * @author zhucl
 * @date 2018-06-25 19:16:40
 * @version v1.0 
 * @subject 加载顺序value值越小越先加载
 */
@Component
@Order(value = 1)
public class VoucherApplicationRunner implements ApplicationRunner {
	private static Logger logger = LoggerFactory.getLogger(VoucherApplicationRunner.class);
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
        //校验相关路径是否存在，不存在创建相关路径
		logger.info("文件路径校验开始");
		//校验文件根路径 ./evsfile
		String evsPath = Constants.baseDir + File.separator + Constants.evsFileDir;
		File evsFile = new File(evsPath);
		if (!evsFile.exists()) {
			evsFile.mkdirs();
		}
		//校验PDF路径  ./evsfile/pdf
		String pdfPath = evsPath + File.separator + Constants.pdfDir;
		File pdfFile = new File(pdfPath);
		if (!pdfFile.exists()) {
			pdfFile.mkdirs();
		}
		//校验模板路径 ./evsfile/template
		String templatePath = evsPath + File.separator + Constants.templateDir;
		File templateFile = new File(templatePath);
		if (!templateFile.exists()) {
			templateFile.mkdirs();
		}
		//校验FO路径 ./evsfile/fo
		String foPath = evsPath + File.separator + Constants.foDir;
		File foFile = new File(foPath);
		if (!foFile.exists()) {
			foFile.mkdirs();
		}
		//校验SVG路径 ./evsfile/svg
		String svgPath = evsPath + File.separator + Constants.svgDir;
		File svgFile = new File(svgPath);
		if (!svgFile.exists()) {
			svgFile.mkdirs();
		}
		logger.info("文件路径校验结束");
		logger.info("加载字体开始");
		DataCache.pFontBlackFile = new File(Constants.fontSimHei);
		DataCache.pFontNormalFile = new File(Constants.fontSimSun);
		logger.info("加载字体结束");
	}

}
