package cn.com.agree.evs.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	private static Log logger = LogFactory.getLog(FileUtils.class);

	public static String readFile(String path, boolean isTrim) {
		InputStream ins = null;
		try {
			ins = new FileInputStream(path);
			byte[] tempBytes = new byte[1024];
			byte[] data = new byte[ins.available()];
			int byteRead = 0;
			int index = 0;
			while ((byteRead = ins.read(tempBytes)) != -1) {
				System.arraycopy(tempBytes, 0, data, index, byteRead);
				index = index + byteRead;
			}
			return isTrim ? new String(data).trim() : new String(data);
		} catch (Exception e) {
			logger.error("evs文件读取异常:" + path, e);
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					ins = null;
				}
			}
		}
		return "";
	}

	public static void writeFile(String filePath, String data, boolean append) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(filePath, append);
			out.write(data.getBytes());
			out.flush();
		} catch (Exception e) {
			logger.error("evs文件写入异常:" + filePath, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					out = null;
				}
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param srcFileName
	 *            待复制的文件名
	 * @param destFileName
	 *            目标文件名
	 * @param overlay
	 *            如果目标文件存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 * @throws Exception
	 */
	public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) throws Exception {
		File srcFile = new File(srcFileName);
		// 判断源文件是否存在
		if (!srcFile.exists() || !srcFile.isFile()) {
			return false;
		}
		// 判断目标文件是否存在
		File destFile = new File(destFileName);
		if (destFile.exists()) {
			// 如果目标文件存在并允许覆盖
			if (overlay) {
				// 删除已经存在的目标文件，无论目标文件是目录还是单个文件
				new File(destFileName).delete();
			}
		} else {
			// 如果目标文件所在目录不存在，则创建目录
			if (!destFile.getParentFile().exists()) {
				// 目标文件所在目录不存在
				if (!destFile.getParentFile().mkdirs()) {
					// 复制文件失败：创建目标文件所在目录失败
					return false;
				}
			}
		}
		// 复制文件
		int byteread = 0; // 读取的字节数
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 剔除路径和不存在的文件
	 * 
	 * @param srcPaths
	 * @return
	 */
	public static String[] removeNotExistPath(String[] srcPaths) {
		List<String> pathList = new ArrayList<String>();
		for (String path : srcPaths) {
			File file = new File(path);
			if (file.isFile()) {
				pathList.add(path);
			}
		}
		String[] destPaths = new String[pathList.size()];
		pathList.toArray(destPaths);
		return destPaths;
	}

	public static Document readAftContent(String filePath, String encoding) {
		Document document = null;
		try {
			SAXReader sax = new SAXReader();
			sax.setEncoding(encoding);
			document = sax.read(new File(filePath));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * 获取PDF文件路径
	 * 
	 * @param filePath
	 * @param fileName
	 * @param defaultPath
	 * @return
	 * @throws IOException
	 */
	public static File getPdfFile(String filePath, String fileName, String defaultPath) throws IOException {
		if (StringUtils.isNullOrBlank(filePath)) {
			File defaultDir = new File(defaultPath);
			File pdfFile = new File(defaultDir, fileName);
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
			}
			return pdfFile;
		} else {
			File file = new File(filePath);
			if (file.isDirectory()) {
				File pdfFile = new File(file, fileName);
				if (!pdfFile.exists()) {
					pdfFile.createNewFile();
				}
				return pdfFile;
			} else if(file.isFile()) {
				if (!file.exists()) {
					file.createNewFile();
				}
				return file;
			} else {
				if (!file.exists()) {
					file.createNewFile();
				}
				return file;
			}
		}
	}
	
	/**
	 * 保存文档 
	 * @param document
	 * @param file
	 * @throws Exception
	 */
    public static void saveDocument(Document document, File file) throws Exception {
        XMLWriter output = null;
        try {
            OutputFormat format = new OutputFormat(); // 获取输出的指定格式
            format.setEncoding("GB18030");
            OutputStreamWriter outstream = new OutputStreamWriter(new FileOutputStream(file), "GB18030");
            output = new XMLWriter(outstream, format);
            output.setEscapeText(false);
            output.write(document);
            output.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    output = null;
                }
            }
        }
    }
}
