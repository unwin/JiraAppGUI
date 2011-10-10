/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jiraappgui;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.OutputKeys;
import org.xml.sax.SAXException;
/**
 *
 * @author rogerunwin
 *
 * origional code taken from http://www.roseindia.net/xml/XMLwithXSLT.shtml
 *
 */
public class xslt {
    private Transformer transformer;

    public InputStream stringToStreamSource(String data) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return is;
    }

    public xslt(String xsltData) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();

        transformer = factory.newTransformer(new StreamSource(stringToStreamSource(xsltData)));

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }


//    public xslt(String xsltFile) throws TransformerConfigurationException {
//        TransformerFactory factory = TransformerFactory.newInstance();
//
//        transformer = factory.newTransformer(new StreamSource(this.getClass().getResource(xsltFile).toString()));
//
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    }

    public String transform(InputStream xmlStream) throws TransformerException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Result result = new StreamResult(bos);

        Source source = new StreamSource(xmlStream);

        transformer.transform(source, result);

        return bos.toString();
    }

}
