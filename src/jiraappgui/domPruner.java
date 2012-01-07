/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jiraappgui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author unwin
 */
public class domPruner {

    private Date StartDate = null;
    private Date EndDate = null;
    private String version = null;
    private String project = null;
    private boolean added = false;
    private String key = null;
    private String fixVersion = null;
    private Map<String, ArrayList<String>> versions = new HashMap(); // <String, ArrayList<String>>
    private Map<String, List<String>> selected = null;
    private String xml = "";
    String deleteMode = null;
    private final JiraAppGUIView mainWindow;
    private Boolean should_filter = true;
    private String url = null;

    public String getJiraXML() {
        return xml;
    }

    domPruner(Date StartDate, Date EndDate, JiraAppGUIView parrent) {
        mainWindow = parrent;
        this.StartDate = StartDate;
        this.StartDate.setHours(0);
        this.StartDate.setMinutes(0);
        this.StartDate.setSeconds(1);
        this.EndDate = EndDate;
        this.EndDate.setHours(23);
        this.EndDate.setMinutes(59);
        this.EndDate.setSeconds(59);
    }

    public void setDateRange(Date StartDate, Date EndDate) {
        this.StartDate = StartDate;
        this.StartDate.setHours(0);
        this.StartDate.setMinutes(0);
        this.StartDate.setSeconds(1);
        this.EndDate = EndDate;
        this.EndDate.setHours(23);
        this.EndDate.setMinutes(59);
        this.EndDate.setSeconds(59);
    }

    public Map<String, ArrayList<String>> getVersions() {
        return versions;
    }

    public InputStream test(InputStream XML) throws ParserConfigurationException, SAXException, IOException {
        selected = null;
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = fact.newDocumentBuilder();
        Document doc = builder.parse(XML);

        Node node = doc.getDocumentElement();

        xml = domToString(doc);


        traverse("/", "", node);


        return domToInputStream(doc);
    }

    public InputStream domToInputStream(Document doc) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        Result result = new StreamResult(bos);
        byte[] bytes = null;
        try {
            bytes = domToString(doc).getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(domPruner.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ByteArrayInputStream(bytes);
    }

    public String domToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(domPruner.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            transformer.transform(domSource, result);
        } catch (TransformerException ex) {
            Logger.getLogger(domPruner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return writer.toString();
    }

    public Node traverse(String path, String nodeName, Node node) {

        if (path.equals("/")) {
            project = null;
            version = null;
            fixVersion = null;
        }

        Node delete = null;

        if (node.getNodeName().equals("item")) {
            project = null;
            version = null;
            fixVersion = null;
            added = false;
//            mainWindow.log("RESETTING PROJECT AND VERSION");
        }

        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();

            for (int x = 0; x < children.getLength(); x++) {
                Node n = children.item(x);
                Node d = traverse(path + "/" + node.getNodeName(), node.getNodeName(), n);
                if (d != null) {
                    delete = d;
                    if (deleteMode.equals("item")) {
                        if (delete.getNodeName().equals("item")) {
//                            displayNodeTree(delete);
                            node.removeChild(delete);

                            delete = null;
                            d = null;
                            deleteMode = "";
                        }
                    }
                    if (deleteMode.equals("comment")) {
                        if (delete.getNodeName().equals("comment")) {
//                            displayNodeTree(delete);
                            node.removeChild(delete);

                            delete = null;
                            d = null;
                            deleteMode = "";
                        }
                    }

                }
            }

        } else {
            if (nodeName.equals("key")) {
                key = node.getNodeValue();
            }


            if (nodeName.equals("project")) {
                project = node.getNodeValue();
            }


            if (nodeName.equals("version")) {
                if (version == null) {
                    version = node.getNodeValue();
                } else {
                    version = version + "||" + node.getNodeValue();
                }
            }


            if (nodeName.equals("votes")) {
                if (version == null) {      // EMERGENCY VERSION HACK
                    version = fixVersion;   // EMERGENCY VERSION HACK
                }                           // EMERGENCY VERSION HACK
                if ((version != null) && (project != null) && (added == false)) {
                    if (version.matches(".*\\|\\|.*")) {
                        for (String v : version.split("\\|\\|")) {
                            if (selected == null) {
                                added = true;
                                if ((project != null) && (v != null)) {
                                    ArrayList<String> vers = new ArrayList<String>();
                                    if (versions.get(project) == null) {
                                        vers.add(v);
                                        versions.put(project, vers);
                                    } else {
                                        vers = versions.get(project);
                                        if (!vers.contains(v)) {
                                            vers.add(v);
                                        }
                                    }

                                    versions.put(project, vers);
                                }
                            }
                        }

                    } else {
                        if (selected == null) {
                            added = true;
                            if ((project != null) && (version != null)) {
                                ArrayList<String> vers = new ArrayList<String>();
                                if (versions.get(project) == null) {
                                    vers.add(version);
                                    versions.put(project, vers);
                                } else {
                                    vers = versions.get(project);
                                    if (!vers.contains(version)) {
                                        vers.add(version);
                                    }
                                }

                                versions.put(project, vers);
                            }
                        }
                    }
                }
            }

            if (nodeName.equals("fixVersion")) {
                fixVersion = node.getNodeValue();
            }





            if (nodeName.equals("votes")) {
                if (((deleteMode == null) || (!deleteMode.equals("item")))
                        && (selected != null) && (project != null) && (version != null)) {

                    if (selected.get(project) != null) {
                        List<String> vers = selected.get(project);
                        Boolean deleteIt = true;

                        for (String ver : vers) {
                            if (version.indexOf(ver) != -1) {
                                deleteIt = false;
                            }
                        }

                        if (deleteIt) {
                            deleteMode = "item";
                            delete = node;
                        }
                    }
                }
            }


            if (nodeName.equals("comment")) {
                node.setNodeValue(node.getNodeValue().replaceAll("&lt;br/&gt;", "\n"));
                node.setNodeValue(node.getNodeValue().replaceAll("&amp;", "&"));
                node.setNodeValue(node.getNodeValue().replaceAll("&gt;", ">"));
                node.setNodeValue(node.getNodeValue().replaceAll("&lt;", "<"));
                node.setNodeValue(node.getNodeValue().replaceAll("<[bB][rR] *>", "\n"));
                node.setNodeValue(node.getNodeValue().replaceAll("<br>", "<P />"));
                node.setNodeValue(node.getNodeValue().replaceAll("[\\\r\\\n]+", "<P />"));

                SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
                Date date = null;
                try {
                    date = (Date) formatter.parse(node.getParentNode().getAttributes().getNamedItem("created").getNodeValue());
                } catch (ParseException ex) {
                    Logger.getLogger(domPruner.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (date.before(StartDate) || date.after(EndDate)) {
                    if (deleteMode == null) {
                        deleteMode = "comment";
                    } else if (!deleteMode.equals("item")) {
                        deleteMode = "comment";
                    }

                    delete = node;
                }
            }

        }

        if ((nodeName.equals("subtasks") == true) && (should_filter == true)) {
            if ((version == null) || (project == null)) {
                deleteMode = "item";
                delete = node;
            }
        }


        if (delete != null) {
            return node;
        } else {
            return null;
        }

    }

    public InputStream filter(Map<String, List<String>> selected, Boolean filter) throws ParserConfigurationException, SAXException, IOException {
        this.selected = selected;
        this.should_filter = filter;
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));

        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = fact.newDocumentBuilder();
        Document doc = builder.parse(is);


        Node node = doc.getDocumentElement();

        traverse("/", "", node);
        return domToInputStream(doc);
    }

    private void displayNodeTree(Node delete) {

        Transformer xf = null;
        try {
            xf = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            mainWindow.log("ERROR GETTING NEW INSTANCE OF THE TRANSFORMER FACTORY. " + ex.toString());
        }

        DOMResult dr = new DOMResult();
        try {
            xf.transform(new DOMSource(delete), dr);
        } catch (TransformerException ex) {
            mainWindow.log("ERROR PERFORMING THE TRANSFORM. " + ex.toString());
        }
        Document newDoc = (Document) dr.getNode();
    }
}


