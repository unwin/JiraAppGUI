/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jiraappgui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.Timer;

import javax.xml.transform.TransformerConfigurationException;

import java.io.ByteArrayInputStream;


/**
 *
 * @author rogerunwin
 */
public class update extends Thread {
    private final Timer busyIconTimer;
    private final JEditorPane jEditorPane1;
    private final String url;
    private final JButton run;
    private Date StartDate;
    private Date EndDate;
    private final String username;
    private final String password;
    private Map <String, ArrayList<String>> versions;
    private final JiraAppGUIView parrent;
    private domPruner dp;
    private String xsltData;
    private String csvxsltData;
    private String csv;
    private ByteArrayInputStream is2;
    private xslt csvTransformer = null;

    update(JiraAppGUIView parrent, Timer busyIconTimer, JEditorPane jEditorPane1, String url, JButton run, Date startDate, Date endDate, String username, String password) {
        this.busyIconTimer = busyIconTimer;
        this.jEditorPane1 = jEditorPane1;
        this.url = url;
        this.run = run;
        this.StartDate = startDate;
        this.EndDate = endDate;
        this.username = username;
        this.password = password;
        this.parrent = parrent;

        
        try {
            csvTransformer = new xslt(readCsvXsltFile());  // would prefer input stream
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setDateRange(Date startDate, Date endDate) {
        this.StartDate = startDate;
        this.EndDate = endDate;
    }


    public void setVersions(Map <String, ArrayList<String>> versions) {
        List <String>l = new ArrayList(versions.keySet());
        /*
        for (String proj : l) {
            ArrayList<String> vers = versions.get(proj);
            for (int x = 0; x < vers.size();x++)
                parrent.log("XXXX " + proj + " --VERSIONS-----------------------------" + vers.get(x));
         
        }
        */

        this.versions = versions;
    }

    public void filter(Map <String, List<String>>selected) {
        parrent.log("\n\n--FILTER-----------------------------\n\n");
        xslt transformer = null;
        try {
            transformer = new xslt(xsltData);  // would prefer input stream
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }


        try {

            jEditorPane1.setContentType("text/html");
            dp.setDateRange(StartDate, EndDate);


            
            // clone the input stream so we can use it twice.
            byte[] bytes = convertStreamToString(dp.filter(selected, true)).getBytes("UTF-8");


            InputStream is = new ByteArrayInputStream(bytes);
            is2 = new ByteArrayInputStream(bytes);

            csv = csvTransformer.transform(is2);
            String html = transformer.transform(is);

            html = html.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&").replaceAll("&quot;", "\\\"").replaceAll("&amp;", "&");
            html = html.replaceAll("<br/>", "");
            html = html.replaceAll("\\[BWR\\-P\\]", "").replaceAll("\\[BWR\\-I\\]", "");

            String[] foo = html.split("<body>", 10);

            html = foo[1];
            foo = html.split("</body>", 10);
            html = foo[0];


            parrent.output(html);
//            jEditorPane1.setText(" " + html); // html
//            jEditorPane1.setVisible(true);
            System.gc();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void unfilter() {
        parrent.log("\n\n--UNFILTER-----------------------------\n\n");
        xslt transformer = null;
        try {
            transformer = new xslt(xsltData);  // would prefer input stream
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }


        try {

            jEditorPane1.setContentType("text/html");
            dp.setDateRange(StartDate, EndDate);


            // clone the input stream so we can use it twice.
            byte[] bytes = convertStreamToString(dp.filter(null, false)).getBytes("UTF-8");
        

            InputStream is = new ByteArrayInputStream(bytes);
            is2 = new ByteArrayInputStream(bytes);
            
            String html = transformer.transform(is);
            csv = csvTransformer.transform(is2);
            html = html.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&").replaceAll("&quot;", "\\\"").replaceAll("&amp;", "&");
            html = html.replaceAll("<br/>", "");

            html = html.replaceAll("\\[BWR\\-P\\]", "").replaceAll("\\[BWR\\-I\\]", "");

            String[] foo = html.split("<body>", 10);

            html = foo[1];
            foo = html.split("</body>", 10);
            html = foo[0];


            parrent.output(html);
//            jEditorPane1.setText(" " + html); // html
//            jEditorPane1.setVisible(true);
            System.gc();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        busyIconTimer.start();
        parrent.log("\n\n--RUN-----------------------------\n\n");
        webGetter wg = null;
        try {
            wg = new webGetter(url, username, password);
        } catch (MalformedURLException ex) {
            parrent.log("ERROR IN WEBGETTER " + ex.toString());
        }

        InputStream xmlStream = null;
        try {
            xmlStream = wg.fetch();  // can return input stream
        } catch (IOException ex) {
            IncorrectUnPwAlert incorrectUnPwAlert = new IncorrectUnPwAlert(parrent.getFrame(), ex.toString());
        }

        InputStream is = null;
        dp = new domPruner(StartDate, EndDate, parrent);
        try {
            is = dp.test(xmlStream);
            parrent.displayJiraXml(dp.getJiraXML());
        } catch (Exception ex) {
            parrent.log("ERROR IN THE DOM PRUNER");
            //Logger.getLogger(update.class.getName()).log(Level.SEVERE, null, ex);
        }

        setVersions(dp.getVersions());

        xslt transformer = null;
        try {
            transformer = new xslt(xsltData);  // would prefer input stream
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }
        String _is = null;
        try {
            _is = convertStreamToString(is);
        } catch (IOException ex) {
            Logger.getLogger(update.class.getName()).log(Level.SEVERE, null, ex);
        }


        // clone the input stream so we can use it twice.
        byte[] bytes = null;
        try {
            bytes = _is.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(update.class.getName()).log(Level.SEVERE, null, ex);
        }

        is = new ByteArrayInputStream(bytes);
        is2 = new ByteArrayInputStream(bytes);

        try {

            jEditorPane1.setContentType("text/html");
            String html = transformer.transform(is);

            csv = csvTransformer.transform(is2);
            html = html.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&").replaceAll("&quot;", "\\\"").replaceAll("&amp;", "&");
            html = html.replaceAll("<br/>", "");
            
            html = html.replaceAll("\\[BWR\\-P\\]", "").replaceAll("\\[BWR\\-I\\]", "");
            
            String[] foo = html.split("<body>", 10);

            html = foo[1];
            foo = html.split("</body>", 10);
            html = foo[0];
            parrent.output(html);
//            jEditorPane1.setText(" " + html); // html
//            jEditorPane1.setVisible(true);


        } catch (Exception e) {
            e.printStackTrace();
        }








        run.setEnabled(true);
        busyIconTimer.stop();
        parrent.resetTimer();
        parrent.updateProjectVersions(versions);
        parrent.enablePostRunControls();
        System.gc();
    }

    void setXsltData(String text) {
        xsltData = text;
    }

    void saveCSV() {
        
    }

    public String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }


    String readCsvXsltFile() {
        String doc = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/xslt/csvConverter_v1.0.xsl")));
        String line;
        try {
            while (null != (line = br.readLine())) {
                doc += line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    String getCSV() {
        String modCsv = csv;
        String output = "\"WBS NUMBER\",\"WBS NAME\",\"TASK ID\",\"TASK SUMMARY\",\"ASSIGNED\",\"RESOLUTION\",\"STATUS\",\"CREATED\",\"UPDATED\",\"RESOLVED\",\"DUE\",\"VERSION\",\"fixVERSION\",\"Issue Links\",\"Subtasks\"";
        modCsv = modCsv.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&").replaceAll("&quot;", "'").replaceAll("&amp;", "&");
        for (String line : modCsv.split("\n")) {
            line = line.replaceAll("\"_-!-_\"", "\"\"");
            line = line.replaceAll("_-!-_", " ");
            if (!line.equalsIgnoreCase("")) {
                line = line.replaceFirst("^'([0-9.]*) {0,1}", "\"$1\",\"");
                output += line + "\n";
            }
        }
        return output;
    }
}
