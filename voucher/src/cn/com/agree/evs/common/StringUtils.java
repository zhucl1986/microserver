package cn.com.agree.evs.common;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class StringUtils {
    /**
     * 判断对象是否为空，为空返回空字符串
     * @param obj
     * @return ""
     */
    public static String dealNull(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * 判断对象是否为空，为空返回字符串零
     * @param obj
     * @return "0"
     */
    public static String dealDigit(Object obj) {
        if (obj == null) {
            return "0";
        } else {
            return obj.toString();
        }
    }

    /**
     * 截取两个字符串中间的字符串（不包括传入的字符串本身）
     * @param ss       源字符串
     * @param strStart 其实字符串
     * @param endStart 结束字符串
     * @return 中间的字符串
     */
    public static String getMiddleString(String ss, String strStart, String endStart) {
        int start = ss.indexOf(strStart);
        int end = ss.indexOf(endStart);
        if (start == -1 || end == -1 || start >= end) {
            return "";
        }
        return ss.substring(start + strStart.length(), ss.indexOf(endStart));
    }

    /**
     * 将数组整合成字符串
     * @param strArr 要整合的字符串数组
     * @param split  整合时每个元素中间的分隔符
     * @return 整合后的字符串
     */
    public static String jion(String[] strArr, String split) {
        if (strArr == null)
            return "";
        String _str = "";
        for (int i = 0; i < strArr.length; i++) {
            if (_str.equals(""))
                _str = strArr[i];
            else
                _str += split + strArr[i];
        }
        return _str;
    }

    /**
     * 字符串重复拼接
     * @param str   源字符串
     * @param split 重复拼接时的分隔符
     * @param n     拼接次数
     * @return 拼接后的字符串
     */
    public static String jion(String str, String split, int n) {
        if (str == null)
            return "";
        String _str = "";
        for (int i = 0; i < n; i++) {
            if (_str.equals(""))
                _str = str;
            else
                _str += split + str;
        }
        return _str;
    }

    /**
     * 左去字符
     * @param src  源字符串
     * @param ch   要去掉的目标字符
     * @param nLen 目标长度（<=0,直到没有目标字符为止）
     * @return 字符串
     */
    public static String ltrim(String src, char ch, int nLen) {
        if (src == null || src.length() <= nLen) {
            return src;
        }
        char[] czSrc = src.toCharArray();
        int i, j;
        int n = czSrc.length;
        for (i = 0; (n - i) > nLen; i++) {
            if (czSrc[i] != ch)
                break;
        }
        char[] czRet = new char[n - i];
        for (j = 0; i < czSrc.length; i++, j++) {
            czRet[j] = czSrc[i];
        }
        return new String(czRet);
    }

    /**
     * 获取随机数
     *
     * @param length 随机数位数
     * @return length位的随机数，以字符串表示
     */
    public static String random(int length) {
        int range = 10;
        String ret = "";
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int temp = rand.nextInt(range);
            ret = ret + temp;
        }
        return ret;
    }

    /**
     * 获取随机数（不重复）
     * @param range 随机数范围，如传入10 则范围为0-9
     * @param count 随机数位数
     * @return 字符串数组，长度为count，内容为0到range-1的随机数
     */
    public static String[] random(int range, int count) {
        if (count > range) {
            return null;
        }
        String[] randstr = new String[count];
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < range; i++) {
            list.add(String.valueOf(i));
        }
        Random rand = new Random();
        int n = 0;
        for (int i = 0; i < count; i++) {
            int j = rand.nextInt(list.size());
            randstr[n] = (String) list.get(j);
            list.remove(list.get(j));
            n++;
        }
        return randstr;
    }

    /**
     * MD5加密
     * @param s 明文
     * @return 返回加密后的密文
     */
    public static String EncMD5(String s) {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 字符串数组转成字符串（由逗号分隔）
     * @param strArray 源字符串数组
     * @return 返回字符串
     */
    public static String arrayToString(String strArray[]) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            if ("".equals(buffer.toString())) {
                buffer.append(strArray[i]);
            } else {
                buffer.append("," + strArray[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 带分隔符的字符串转成字符串数组
     * @param str     源字符串
     * @param sep_str 分隔符
     * @return 字符串数组
     */
    public static String[] stringToArray(String str, String sep_str) {
        StringTokenizer strToken = new StringTokenizer(str, sep_str);
        int tokenCount = strToken.countTokens();
        String str_array[] = new String[tokenCount];
        for (int i = 0; i < tokenCount; i++) {
            str_array[i] = strToken.nextToken();
        }
        return str_array;
    }

    /**
     * 字符串编码转换，ISO8859-1转换成GBK
     * @param s 源字符串
     * @return 转完后的字符串
     * @throws Exception
     */
    public static String transferGBK(String s) throws Exception {
        if (s == null || "".equals(s)) {
            return "";
        }
        return new String(s.getBytes("iso8859-1"), "gbk");
    }

    /**
     * 字符串编码转换，GBK转换成ISO8859-1
     * @param s 源字符串
     * @return 转完后的字符串
     * @throws Exception
     */
    public static String transferISO(String s) throws Exception {
        if (s == null || "".equals(s)) {
            return "";
        }
        return new String(s.getBytes("gbk"), "iso8859-1");
    }

    /**
     * 左补字符串
     * @param sourcestr 需要补字符的串
     * @param regex     替补字符
     * @param len       补后字符串长度
     * @return String
     * @throws Exception
     */
    public static String fixLeftChar(String sourcestr, char regex, int len) throws Exception {
        if (sourcestr == null) {
            throw new Exception("输入字符串为空");
        }
        int strlen = sourcestr.length();
        for (int i = strlen; i < len; i++) {
            sourcestr = regex + sourcestr;
        }
        return sourcestr;
    }

    /**
     * 右补字符串
     * @param sourcestr 需要补字符的串
     * @param regex     替补字符
     * @param len       补后字符串长度
     * @return String
     * @throws Exception
     */
    public static String fixRightChar(String sourcestr, char regex, int len) throws Exception {
        if (sourcestr == null) {
            throw new Exception("输入字符串为空");
        }
        int strlen = sourcestr.length();
        for (int i = strlen; i < len; i++) {
            sourcestr = sourcestr + regex;
        }
        return sourcestr;
    }

    /**
     * 判断字符串是否为空或NULL
     * @param data
     * @return
     */
    public static boolean isNullOrBlank(String data){
        boolean isNullOrBlank = false;
        if (null == data || "".equals(data)){
            isNullOrBlank = true;
        }
        return isNullOrBlank;
    }
    
	public static String dealBool(Object obj) {
		if (obj == null) {
			return "false";
		} else {
			return obj.toString();
		}
	}
}
