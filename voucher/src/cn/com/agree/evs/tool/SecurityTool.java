package cn.com.agree.evs.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyRecipient;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.stereotype.Component;

@Component
public class SecurityTool {

	public void addSecurityControl(Map<String, String> securityMap, String pdffile) throws Exception {
		String[] params = null;
		if (Boolean.parseBoolean((String) securityMap.get("defaultSettings"))) {
			params = new String[] { pdffile, pdffile, "-canModifyAnnotations", "false", "-canModify", "false",
					"-canPrint", "false", "-canExtractContent", "false", "-canExtractForAccessibility", "false",
					"-canAssemble", "false", "-canFillInForm", "false" };
		} else {
			params = new String[securityMap.size() * 2 + 2];
			params[0] = pdffile;
			params[1] = pdffile;
			String key = "";
			int i = 2;
			for (Iterator<?> iterator = securityMap.keySet().iterator(); iterator.hasNext();) {
				key = (String) iterator.next();
				params[i] = "-" + key;
				params[i + 1] = (String) securityMap.get(key);
				i = i + 2;
			}
		}
		encrypt(params);
	}

	public void encrypt(String[] args) throws IOException, CertificateException {
		if (args.length < 1) {
			usage();
		} else {
			AccessPermission ap = new AccessPermission();
			String infile = null;
			String outfile = null;
			String certFile = null;
			String userPassword = null;
			String ownerPassword = null;
			int keyLength = 40;
			PDDocument document = null;
			try {
				for (int i = 0; i < args.length; i++) {
					String key = args[i];
					if (key.equals("-O")) {
						ownerPassword = args[++i];
					} else if (key.equals("-U")) {
						userPassword = args[++i];
					} else if (key.equals("-canAssemble")) {
						ap.setCanAssembleDocument(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canExtractContent")) {
						ap.setCanExtractContent(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canExtractForAccessibility")) {
						ap.setCanExtractForAccessibility(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canFillInForm")) {
						ap.setCanFillInForm(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canModify")) {
						ap.setCanModify(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canModifyAnnotations")) {
						ap.setCanModifyAnnotations(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canPrint")) {
						ap.setCanPrint(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-canPrintDegraded")) {
						ap.setCanPrintDegraded(args[++i].equalsIgnoreCase("true"));
					} else if (key.equals("-certFile")) {
						certFile = args[++i];
					} else if (key.equals("-keyLength")) {
						try {
							keyLength = Integer.parseInt(args[++i]);
						} catch (NumberFormatException e) {
							throw new NumberFormatException("Error: -keyLength is not an integer '" + args[i] + "'");
						}
					} else if (infile == null) {
						infile = key;
					} else if (outfile == null) {
						outfile = key;
					} else {
						usage();
					}
				}
				ap.setReadOnly();
				if (infile == null) {
					usage();
				}
				if (outfile == null) {
					outfile = infile;
				}
				document = PDDocument.load(new File(infile));
				if (!document.isEncrypted()) {
					if (certFile != null) {
						PublicKeyProtectionPolicy ppp = new PublicKeyProtectionPolicy();
						PublicKeyRecipient recip = new PublicKeyRecipient();
						recip.setPermission(ap);

						CertificateFactory cf = CertificateFactory.getInstance("X.509");

						InputStream inStream = null;
						try {
							inStream = new FileInputStream(certFile);
							X509Certificate certificate = (X509Certificate) cf.generateCertificate(inStream);
							recip.setX509(certificate);
						} finally {
							if (inStream != null) {
								inStream.close();
							}
						}
						ppp.addRecipient(recip);
						ppp.setEncryptionKeyLength(keyLength);
						try {
							document.protect(ppp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, ap);
						spp.setEncryptionKeyLength(keyLength);
						try {
							document.protect(spp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						document.save(outfile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("Error: Document is already encrypted.");
				}
			} finally {
				if (document != null) {
					document.close();
				}
			}
		}
	}

	/**
	 * This will print a usage message.
	 */
	private static void usage() {
		String message = "Usage: java -jar pdfbox-app-x.y.z.jar Encrypt [options] <inputfile> [outputfile]\n"
				+ "\nOptions:\n"
				+ "  -O <password>                            : Set the owner password (ignored if cert is set)\n"
				+ "  -U <password>                            : Set the user password (ignored if cert is set)\n"
				+ "  -certFile <path to cert>                 : Path to X.509 certificate\n"
				+ "  -canAssemble <true|false>                : Set the assemble permission\n"
				+ "  -canExtractContent <true|false>          : Set the extraction permission\n"
				+ "  -canExtractForAccessibility <true|false> : Set the extraction permission\n"
				+ "  -canFillInForm <true|false>              : Set the fill in form permission\n"
				+ "  -canModify <true|false>                  : Set the modify permission\n"
				+ "  -canModifyAnnotations <true|false>       : Set the modify annots permission\n"
				+ "  -canPrint <true|false>                   : Set the print permission\n"
				+ "  -canPrintDegraded <true|false>           : Set the print degraded permission\n"
				+ "  -keyLength <length>                      : The length of the key in bits "
				+ "(valid values: 40, 128 or 256, default is 40)\n"
				+ "\nNote: By default all permissions are set to true!";

		System.err.println(message);
		System.exit(1);
	}
}
