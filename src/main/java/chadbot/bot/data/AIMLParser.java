package chadbot.bot.data;

import chadbot.bot.synonyms.SynonymGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AIMLParser {
    private InputStream AIMLFile;
    private Document doc;
    private SynonymGroup[] SynonymGroupArr;
    private PatternTemplate[] patternTemplateArr;
    private String defaultResponse;

    public AIMLParser(InputStream AIMLFile) {
        this.AIMLFile = AIMLFile;
        parseDoc(this.AIMLFile);
    }

    //Parse SynonymGroups:
    private SynonymGroup[] parseSynonymGroupArr(Document doc){
        NodeList groups = doc.getElementsByTagName("synonym-group");
        SynonymGroup[] sg = new SynonymGroup[groups.getLength()];
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            NodeList synonymList = group.getElementsByTagName("synonym");
            String[] synonymArr = new String[synonymList.getLength()];

            for (int j = 0; j < synonymList.getLength(); j++) {
                Element synonymElement = (Element) synonymList.item(j);
                String synonym = synonymElement.getTextContent().toLowerCase();
                synonymArr[j] = synonym;
            }
            SynonymGroup newGroup = new SynonymGroup(synonymArr);
            sg[i] = newGroup;
        }
        return sg;
    }

    //Parse ResponseTemplates:
    private PatternTemplate[] parsePatternTemplate(Document doc){
        NodeList patternArr = doc.getElementsByTagName("pattern");
        NodeList templateArr = doc.getElementsByTagName("template");
        PatternTemplate[] pt = new PatternTemplate[patternArr.getLength()];
        for (int i = 0; i < patternArr.getLength(); i++) {
            String textContent = patternArr.item(i).getTextContent();
            String response = templateArr.item(i).getTextContent();
            PatternTemplate currentTemplate = new PatternTemplate(textContent, response);
            pt[i] = currentTemplate;
        }
        return pt;
    }

    //Parse Default Response:
    private String parseDefaultResponse (Document doc){
        return doc.getElementsByTagName("default").item(0).getTextContent();
    }


    //ITERATE FILE METHOD, USE TRY CATCH:
    private void parseDoc(InputStream AIMLFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.doc = builder.parse(AIMLFile);
            this.SynonymGroupArr = parseSynonymGroupArr(doc);
            this.patternTemplateArr = parsePatternTemplate(doc);
            this.defaultResponse = parseDefaultResponse(doc);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public SynonymGroup[] getSynonymGroups(){
        return this.SynonymGroupArr;
    }

    public PatternTemplate[] getPatternTemplate(){
        return this.patternTemplateArr;
    }

    public String getDefaultResponse(){
        return this.defaultResponse;
    }

}
