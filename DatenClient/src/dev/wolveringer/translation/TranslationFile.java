package dev.wolveringer.translation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.Files;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.client.futures.FutureResponseTransformer;
import dev.wolveringer.client.futures.InstandProgressFuture;
import dev.wolveringer.dataserver.player.LanguageType;
import lombok.Getter;

public class TranslationFile {
	private static final DocumentBuilder xmlBuilder;
	private static final Transformer xmlTransformer;

	static {
		DocumentBuilder builder = null;
		Transformer transformer = null;

		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		xmlBuilder = builder;

		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		xmlTransformer = transformer;
	}

	@Getter
	private File file;
	@Getter
	private LanguageType language;
	@Getter
	private boolean exsist;
	@Getter
	private Document xmlFile;
	@Getter
	private Double version = 0D;
	@Getter
	private ClientWrapper handle;
	private String updatedContains;
	private HashMap<String, String> translations = new HashMap<>();
	
	public TranslationFile(LanguageType language,ClientWrapper handle) {
		this.language = language;
		this.handle = handle;
		if(!new File(TranslationManager.languageDir, language.getShortName()).exists())
			new File(TranslationManager.languageDir, language.getShortName()).mkdirs();
		this.file = new File(TranslationManager.languageDir, language.getShortName() + "/EpicPvPMC Text.xml");
		this.exsist = file.exists();
		if (!exsist){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.err.println("Cant find translation for " + language + " (" + file + ")");
		}
		if (exsist) {
			readXML(file);
		}
	}

	private void readXML(File input){
		try {
			xmlFile = xmlBuilder.parse(file);
			paradiseXML();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readXML(String input){
		try {
			xmlFile = xmlBuilder.parse(new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8"))));
			paradiseXML();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void paradiseXML(){
		String version = null;
		if (xmlFile.getDocumentElement().getElementsByTagName("version").getLength() != 0)
			version = xmlFile.getDocumentElement().getElementsByTagName("version").item(0).getTextContent();
		if (version == null || version.equalsIgnoreCase(""))
			System.out.println("Missing version for: " + language);
		if(version == null)
			version = "underknown";
		try {
			this.version = Double.parseDouble(version);
		} catch (Exception e) {
			this.version = 0D;
			System.out.println("Cant paradise version for: " + language + " (" + version + "). Using version 0D");
			Element nversion = xmlFile.createElement("version");
			xmlFile.getDocumentElement().appendChild(nversion);
			nversion.setTextContent(""+version);
			xmlFile.getDocumentElement().getElementsByTagName("version").item(0).setTextContent(version+"");
			saveDocument();
		}
		
		NodeList list = xmlFile.getDocumentElement().getElementsByTagName("string");
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				translations.put(e.getAttribute("name").toLowerCase(), ((Node) e.getChildNodes().item(0)).getTextContent());
			}
		}
	}
	
	public void saveDocument(){
		try {
			if(!file.exists())
				file.createNewFile();
			FileOutputStream os = new FileOutputStream(file);
			xmlTransformer.transform(new DOMSource(xmlFile), new StreamResult(os));
			os.flush();
			os.close();
		} catch (TransformerException | IOException e) {
			e.printStackTrace();
		}
	}

	public ProgressFuture<Boolean> checkUpdate(){//PacketLanguageRequest
		if(updatedContains != null)
			return new InstandProgressFuture<Boolean>() {
				@Override
				public Boolean get() {
					return true;
				}
			};
		return new FutureResponseTransformer<String, Boolean>(handle.requestLanguageUpdate(language, version)) {
			@Override
			public Boolean transform(String obj) {
				updatedContains = obj;
				return obj != null;
			}
		};
	}
	
	public void updateLanguage(){
		readXML(updatedContains);
		saveDocument();
		updatedContains = null;
	}
	
	public String getFileAsString() {
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTranslation(String key){
		return translations.get(key);
	}
	
	public double computeRelativeTranslatedTexts(LanguageType other){
		if(handle.getTranslationManager().getTranslationFile(language) == null)
			return -1D;
		return (double)translations.size()/ (double)handle.getTranslationManager().getTranslationFile(language).translations.size()*100D;
	}
}
