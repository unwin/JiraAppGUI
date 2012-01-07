/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jiraappgui;


import com.lowagie.text.DocumentException;
import java.util.logging.Level;
import java.util.logging.Logger;



import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xhtmlrenderer.pdf.ITextRenderer;


/**
 *
 * @author rogerunwin
 */
public class htmlToPdf {
    String cssFix;
    String dateRange;
    public htmlToPdf(String dateRange) {
        this.dateRange = dateRange;
        cssFix = "@page {\n" +
                 "-fs-flow-top: \"header\";\n" +
                 "-fs-flow-bottom: \"footer\";\n" +
                 "-fs-flow-left: \"left\";\n" +
                 "-fs-flow-right: \"right\";\n" +
                 "padding: 1em;\n" +
                 "size: 11in 8.5in;\n" +
                 "margin: 5% 5%;\n" +
                 "  @top-right {\n" +
                 "     content: element(header);" +
                 "  }" +
                 "  @bottom-left {\n" +
                 "     content: element(footer);" +
                 "  }" +
                 "}\n" +
                 "\n" +
                 "#footer {\n" +
                 "font: bold serif;\n" +
                 "position: absolute; top: 0; right: 0; \n" +
                 "text-align:right;\n" +
                 "-fs-move-to-flow: \"footer\";\n" +
//                 "display: block;\n" +
                 "position: running(footer);\n" +
                 "}\n" +
                 "\n" +
                 "#header {\n" +
                 "font: bold serif;\n" +
                 "position: absolute; top: 0; left: 0;\n" +
                 "-fs-move-to-flow: \"header\";\n" +
//                 "display: block;\n" +
                 "position: running(header);\n" +
                 "}\n" +
                 "\n" +
                 ".once { font-weight: bold }" + 
                 "#pagenumber:before {\n" +
                 "content: counter(page); \n" +
                 "}\n" +
                 "#pagecount:before {\n" +
                 "content: counter(pages);  \n" +
                 "}\n" +
                 "-->";
    }


    public void HtmlToPDF2(String html, String pdfFileName) {

      //<h2>
      //Bi-weekly Report on <a href="http://ci.oceanobservatories.org/tasks">OOI
      //CI Subsystem Tasks</a><span class="smalltext"> ( 381 tasks) </span>
      //</h2>
      String tasks = null;
        Pattern p =
            Pattern.compile("<h2>.*\\(([0-9]+ tasks).*</h2>", Pattern.MULTILINE|Pattern.DOTALL);

        Matcher m = p.matcher(html);
        if (m.find())
          tasks =m.group(1);


        html = Pattern.compile( "<h2>.*([0-9] tasks).*</h2>", Pattern.MULTILINE|Pattern.DOTALL).matcher(html).replaceAll("");

        /* new code to fix pdf issues */
        html = html.replaceAll("&hl=en#gid=0", ""); // Thanks to google for constructing a URL that breaks this pdf library */
        //html = html.replaceAll("&#160;", "");
        //html = html.replaceAll("&#gid=0", "");
        //html = html.replaceAll("&&#gid=0", "");
        //html = html.replaceAll("&#", "");
        //html = html.replaceAll("&&#", "");
        //html = html.replaceAll("hl=en_US", "");
        //html = html.replaceAll("hl=en", "");
        //System.out.println(html);
        /* new code to fix pdf issues */


        html = html.replaceAll("table \\{", "table { -fs-keep-with-inline: keep; -fs-table-paginate: paginate; -fs-text-decoration-extent: block;"); // line or block
        html = html.replaceFirst("-->", cssFix);
        html = html.replaceFirst("<div id=\"footer\">", "<div id=\"footer\" style=\"\">Page <span id=\"pagenumber\"/> of <span id=\"pagecount\"/>");
        html = html.replaceFirst("<div id=\"header\">", "<div id=\"header\" style=\"\"><center>Bi-weekly Report on OOI CI Iteration Tasks" + dateRange + "<br/>(" + tasks + ")</center>");
        OutputStream os = null;
        try {
            os = new FileOutputStream(pdfFileName);
        } catch (FileNotFoundException ex) {
            System.out.println("FAil Location 1\n");
            Logger.getLogger(htmlToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }


        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(html);

        renderer.layout();
        try {
            renderer.createPDF(os);
        } catch (DocumentException ex) {
            Logger.getLogger(htmlToPdf.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("FAil Location 2\n");
        }
        try {
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(htmlToPdf.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("FAil Location 3\n");
        }


    }

}
