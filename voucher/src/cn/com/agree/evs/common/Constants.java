package cn.com.agree.evs.common;

public class Constants {
	// 凭证相关参数
	public static final float heightCoeff = 2.834643f;
	public static final int lineHeight = 4;
    public static final String baseDir = ".";
    public static final String bottomLineSpace = "pdf_bottomLineSpace";
    public static final String calUnit = "mm";
    public static final String createPdf = "createPdf";
    public static final String DEFAULT = "default";
    public static final String defaultSettings = "defaultSettings";
    public static final String enableSecurity = "pdf_enableSecurity";
    public static final String evsFileDir = "evsfile";
    public static final String foDir = "fo";
    public static final String foFileName = "foFileName";
    public static final String foSuffix = ".fo";
    public static final String fontSimHei = "./fop/fonts/SIMHEI.TTF";
    public static final String fontSimSun = "./fop/fonts/simsun.ttf";
    public static final String getImgData = "pdf_getImgData";
    public static final String lineTimes = "pdf_lineTimes";
    public static final String imgDpi = "pdf_imgDpi";
    public static final String operateType = "elecVoucher_operationType";
    public static final String pdf = "pdf";
    public static final String pdfDirPath = "pdf_dirPath";
    public static final String pdfNotice = "pdf_notice";
    public static final String printData = "print_data";
    public static final String paperSize = "pdf_paperSize";
    public static final String paperFo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" +
            "<layout-master-set xmlns=\"http://www.w3.org/1999/XSL/Format\">" +
            "<simple-page-master margin-bottom=\"0mm\" margin-left=\"0mm\" margin-right=\"0mm\" margin-top=\"0mm\" master-name=\"s1-default\" page-height=\"@page_height@\" page-width=\"@page_width@\">" +
            "<region-body margin-bottom=\"@body_margin_bottom@\" margin-left=\"0mm\" margin-right=\"0mm\" margin-top=\"0mm\"/>	" +
            "<region-before extent=\"0mm\" region-name=\"xsl-region-before-default\"/>" +
            "<region-after extent=\"@after_extent@\" region-name=\"xsl-region-after-default\"/>	" +
            "</simple-page-master>" +
            "<page-sequence-master master-name=\"s1\">" +
            "<repeatable-page-master-alternatives>" +
            "<conditional-page-master-reference master-reference=\"s1-default\"/>	" +
            "</repeatable-page-master-alternatives>" +
            "</page-sequence-master>	" +
            "</layout-master-set>" +
            "</fo:root>";
    public static final String printDataInFile = "print_data_isDataInFile";
    public static final String printDataLocation = "print_data_location";
    public static final String printDataMultiSheet = "print_data_multiSheet";
    public static final String printDataText = "print_data_text";
    public static final String pdfDir = "pdf";
    public static final String pdfSuffix = ".pdf";
    public static final String separator = "_";
    public static final String separator1 = "\\|";
    public static final String seal = "seal";
    public static final String sealImage = "seal_image";
    public static final String sealImageLocation = "seal_image_location";
    public static final String sealImageLocateData = "seal_image_locateData";
    public static final String sealImageLocateType = "seal_image_locateType";
    public static final String sealImagePageNo = "seal_image_pageNo";
    public static final String sealImageSize = "seal_image_size";
    public static final String sealImageSrc = "seal_image_src";
    public static final String signImage = "sign_image";
    public static final String signImageLocation = "sign_image_location";
    public static final String signImageLocateData = "sign_image_locateData";
    public static final String signImageLocateType = "sign_image_locateType";
    public static final String signImagePageNo = "sign_image_pageNo";
    public static final String signImageSize = "sign_image_size";
    public static final String signImageSrc = "sign_image_src";
    public static final String securityControl = "security_control";
    public static final String src = "src";
    public static final String svgDir = "svg";
    public static final String tempImage = "temp_image";
    public static final String text = "text";
    public static final String tempImageLocation = "temp_image_location";
    public static final String tempImageSize = "temp_image_size";
    public static final String tempImageSrc = "temp_image_src";
    public static final String tempLocateType = "temp_image_locateType";
    public static final String templateDir = "template";
    public static final String watermark = "watermark"; 
    
    // 印章相关参数
	public static final String 章外观 = "seal_appearance";
	public static final String 章颜色 = "seal_color";
	public static final String 横坐标偏移毫米 = "seal_print_offsetX";
	public static final String 纵坐标偏移毫米 = "seal_print_offsetY";
	public static final String 边粗 = "seal_sideWidth";
	public static final String 路径 = "seal_dirPath";
	public static final String 机构文字 = "seal_org_text";
	public static final String 机构字体 = "seal_org_fontType";
	public static final String 机构字体大小 = "seal_org_fontSize";
	public static final String 机构纵向毫米 = "seal_org_location";
	public static final String 标识码 = "seal_code_text";
	public static final String 标识码字体 = "seal_code_fontType";
	public static final String 标识码字体大小 = "seal_code_fontSize";
	public static final String 标识码纵向毫米 = "seal_code_location";
	public static final String 章类型文字 = "seal_sealType_text";
	public static final String 章类型字体 = "seal_sealType_fontType";
	public static final String 章类型字体大小 = "seal_sealType_fontSize";
	public static final String 章类型纵向毫米 = "seal_sealType_location";
	public static final String 正方形宽 = "seal_rect_width";
	public static final String 正方形高 = "seal_rect_height";
	public static final String 正方形纵向毫米 = "seal_rect_location";
	public static final String 圆形 = "roundSeal";
	public static final String 三角形 = "triSeal";
	public static final String 椭圆形 = "ovalSeal";
	public static final String 方形 = "rectSeal";
	public static final String 桂林方形 = "guilinSeal";
	public static final String 方形2 = "rectSeal2";
	public static final String 图片 = "image";
	public static final String 菱形 = "diamondSeal";
	public static final String 边长 = "seal_lineLength"; // 三角形&矩形共用属性
	public static final String 垂直边长 = "seal_verticalLineLen"; //矩形独有属性
	// 圆形&椭圆形共用属性
	public static final String 直径 = "seal_diameter";
	public static final String 外圈文字 = "seal_topCircle_text";
	public static final String 外圈起始角度 = "seal_topCircle_angle";
	public static final String 外圈字体 = "seal_topCircle_fontType";
	public static final String 外圈字体大小 = "seal_topCircle_fontSize";
	public static final String 外圈间隙因子 = "seal_topCircle_interval";
	public static final String 五角星横线粗= "seal_star_lineWidth";
	public static final String 五角星纵向毫米 = "seal_star_location";
	public static final String 五角星横线长度 = "seal_star_lineLength";
	public static final String 五角星半径 = "seal_star_radius";
	// 椭圆形独有属性
	public static final String 下外圈文字 = "seal_botCircle_text";
	public static final String 下外圈起始角度 = "seal_botCircle_angle";
	public static final String 垂直直径 = "seal_verticalDiameter";
	public static final String 下外圈字体 = "seal_botCircle_fontType";
	public static final String 下外圈字体大小 = "seal_botCircle_fontSize";
	public static final String 下外圈间隙因子= "seal_botCircle_interval";
	
    // 二维码
	public static final String 二维码图像横向毫米 = "tdCodeInfo_print_offsetX";
	public static final String 二维码图像纵向毫米 = "tdCodeInfo_print_offsetY";
	public static final char 二维码排错率 = 'M'; //设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
	public static final String 二维码编码格式  = "utf-8";
	public static final int 二维码单元偏移量 = 2;
	public static final int 二维码单元边长 = 2;
	public static final String 二维码图片缩放比 ="tdCodeInfo_ratio";//取值范围大于0
	public static final String 二维码尺寸 = "tdCodeInfo_size"; //取值范围1-40
	public static final String 二维码内容 = "tdCodeInfo_text"; //长度为0-800字节
	public static final String 二维码图像透明 = "tdCodeInfo_isAlpha";//二维码图像透明标记（透明“true”，不透明“false”）
	// 暗码
	public static final String 暗码字体 = "seal_darkcode_fontType";
	public static final String 暗码字体大小 = "seal_darkcode_fontSize";
	public static final String 暗码内容 = "seal_darkcode_text";
	//骑缝章
	public static final String 骑缝章是否使用 = "seal_qifeng_isqf";
	public static final String 骑缝章分隔份数 = "seal_qifeng_count";
	public static final String 骑缝章第一份百分比 = "seal_qifeng_percent";
	//设置分辨率
	public static final String 分辨率_X = "seal_dpi_x";
	public static final String 分辨率_Y = "seal_dpi_y";
}
