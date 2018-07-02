package cn.com.agree.evs.voucher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.com.agree.evs.common.Constants;
import cn.com.agree.evs.common.DataCache;
import cn.com.agree.evs.common.DateUtils;
import cn.com.agree.evs.common.FileUtils;
import cn.com.agree.evs.common.StringUtils;
import cn.com.agree.evs.tool.Aft2FoTool;
import cn.com.agree.evs.tool.Fo2PdfTool;
import cn.com.agree.evs.tool.GetPdfImgTool;
import cn.com.agree.evs.tool.SecurityTool;
import cn.com.agree.evs.tool.WatermarkTool;

import java.io.File;
import java.util.Map;

@Service
public class CreateAft2Pdf {
    private static Logger logger = LoggerFactory.getLogger(CreateAft2Pdf.class);
    @Value("${evs.isFoSave}")
    private boolean isFoSave;
    @Autowired
    private Aft2FoTool aft2FoTool;
    @Autowired
    private Fo2PdfTool fo2PdfTool;
    @Autowired
    private GetPdfImgTool getPdfImgTool;
    @Autowired
    private WatermarkTool watermarkTool;
    @Autowired
    private SecurityTool securityTool;
    
    @SuppressWarnings("unchecked")
	public String create() {
        try {
        	Map<String, Object> dataMap = DataCache.dataMap.get();
            String userDir =  System.getProperty("user.dir");
            String timestamp = DateUtils.getDateByPattern(DateUtils.allPattern);
            String foFileName = timestamp + Constants.foSuffix;
            String foSaveDir = userDir + File.separator + Constants.evsFileDir + File.separator + Constants.foDir;
            File foFileDir = new File(foSaveDir);
            File foFile = new File(foFileDir, foFileName);
            //aft转fo文件
            aft2FoTool.aft2fo(foFile);
            String pdfFileName = timestamp + Constants.pdfSuffix;
            String pdfFilePath = (String) dataMap.get(Constants.pdfDirPath);
            String pdfDefaultPath = userDir + File.separator + Constants.evsFileDir + File.separator + Constants.pdfDir;
            File pdfFile = FileUtils.getPdfFile(pdfFilePath, pdfFileName, pdfDefaultPath);
            fo2PdfTool.fo2pdf(foFile, pdfFile);
            //追加aft数据
            aft2FoTool.addData(pdfFile);
            //是否保存fo文件
            if(!isFoSave){
                foFile.delete();
            }
            //获取pdf图片数据
            String imgDatas = "";
            String getImgData = (String) dataMap.get(Constants.getImgData);
            if (!StringUtils.isNullOrBlank(getImgData) && !"false".equals(getImgData.trim())) {
				String imgDpiStr = (String) dataMap.get(Constants.imgDpi);
				int imgDpi = StringUtils.isNullOrBlank(imgDpiStr) ? 300 : Integer.parseInt(imgDpiStr);
				if ("base64".equals(getImgData.trim())) {
					imgDatas = getPdfImgTool.getPdfImgData(true, imgDpi, pdfFile);
				} else if ("img".equals(getImgData.trim())) {
					imgDatas = getPdfImgTool.getPdfImgData(false, imgDpi, pdfFile);
				}
			}
            if (!StringUtils.isNullOrBlank(imgDatas)) {
				return imgDatas;
			}
            //追加水印数据
            if (dataMap.containsKey(Constants.watermark)) {
            	String watermarkFile = (String) dataMap.get(Constants.watermark);
            	watermarkTool.addWatermark(pdfFile, watermarkFile);
			}
            //追加安全控制
            if (dataMap.containsKey(Constants.securityControl)) {
            	Map<String, String> securityMap = (Map<String, String>) dataMap.get(Constants.securityControl);
            	securityTool.addSecurityControl(securityMap, pdfFile.getAbsolutePath());
			}
            return "noImgData;" + pdfFile.getAbsolutePath();
        } catch (Exception e){
            e.printStackTrace();
            logger.error("电子凭证文件创建失败", e);
            return "fail;电子凭证文件创建失败，"+e.getMessage();
        }
    }
}
