/*
 * JiraAppGUIView.java
 */

package jiraappgui;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;


import nu.xom.*;
import java.io.*;





/**
 * The application's main frame.
 */
public class JiraAppGUIView extends FrameView {
    private Map<String, ArrayList<String>> versions;
    //private int[] selected = new int[100];
    //private HashMap <String, List<String>>selected = new HashMap<String, List <String>>();
    private HashMap <String, List<String>>selected = new HashMap<String, List <String>>();
    private DefaultListModel lm1 = null; // in a rush to show this. cant think of a good name for it just now. perhaps later.
    private DefaultListModel lm2 = null;

    private String baseUrl = "https://jira.oceanobservatories.org/tasks/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?";

    private String filterClause = "AND+resolution+in+(%22Fixed%22%2C%22Dropped-No+Impact%22%2C%22Dropped-May+Impact%22%2C%22Work+Remains%22)+";
                                // "AND+resolution+in+(Unresolved%2C+Fixed%2C+%22Won%27t+Fix%22%2C+Incomplete%2C+%22Cannot+Reproduce%22)+";

    private String separator = "%2C+";

    private String projects = "CIDEV" + separator +
                              "CIDEVAS" + separator +
                              "CIDEVCOI" + separator +
                              "CIDEVCEI" + separator +
                              "CIDEVDM" + separator +
                              "CIDEVEOI" + separator +
                              "CIDEVMI" + separator +
                              "CIDEVSA" + separator +
                              "CIINT" + separator +
                              "CISA" + separator +
                              "CIUX" + separator +
                              "CPOP" + separator +
                              "SYSENG";

            //"CIDEVAS%2C+CIDEVMI%02C+CIDEVEOI%2C+CIDEV%2C+CIDEVCEI%2C+CIDEVCOI%2C+CIDEVDM%2C+CIDEVSA%2C+SYSENG%2C+CIINT%2C+CPOP%2C+CIUX%2C+CISA";
    private String dateRange = "";
                                    // Fixed, Dropped-No Impact     Dropped-May Impact      Work Remains
                                    // Fixed  Unresolved            Won't Fix       Incomplete Cannot Reproduce
    public JiraAppGUIView(SingleFrameApplication app) {
        super(app);
try {
//			LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
//			boolean found = false;
//			for (int i = 0; i < lnfs.length; i++) {
//                            System.out.println(lnfs[i].getName());
//				if (lnfs[i].getName().equals("Metal")) {
//					found = true;
//				}
//			}
//			if (!found) {
//				UIManager.installLookAndFeel("JGoodies Plastic 3D",
//						"com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
//			}
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Throwable t) {
//			try {
//                            System.out.println(" it is ignoring the look and feel");
//				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}


        
        initComponents();
        

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        //        statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });


        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
    //    progressBar.setVisible(false);

//        progressBar.setVisible(true);
//        progressBar.setValue(50);
        


		

        Calendar sDay = new GregorianCalendar();
        Calendar eDay = new GregorianCalendar();
        sDay.roll(Calendar.DAY_OF_YEAR, -14);
        startDateField.setDate(sDay.getTime());
        //eDay.roll(Calendar.DAY_OF_YEAR, 0);
        endDateField.setDate(eDay.getTime());
        endDateField.getJCalendar().setWeekOfYearVisible(false);
        startDateField.getJCalendar().setWeekOfYearVisible(false);
        //endDateField.setCalendar(bDay);



        

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();

                    statusAnimationLabel.setIcon(idleIcon);
//                    progressBar.setVisible(false);
//                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                   // statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(false);
//                    progressBar.setValue(value);
                }
            }
        });

        SaveHtml.setEnabled(false);
        SavePDF.setEnabled(false);
        FilterButton.setEnabled(false);
        saveCSV.setEnabled(false);
        RevisionList.setEnabled(true);
        ProjectList.setEnabled(true);

        xsltEditPanel.setText("");
        Document doc = xsltEditPanel.getDocument();

        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/xslt/jiraxml2BWR_v1.4.xsl")));
        String line;
        try {
            while (null != (line = br.readLine())) {
                doc.insertString(doc.getLength(), line + "\n", null);
            }
        } catch (IOException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }

        xsltEditPanel.setCaretPosition(0);

        setActiveTab(0);



    }

    public void resetTimer() {
        statusAnimationLabel.setIcon(idleIcon);
    }
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JiraAppGUIApp.getApplication().getMainFrame();
            aboutBox = new JiraAppGUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JiraAppGUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        PasswordField = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        UsernameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        filterStatusCheckbox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ProjectList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        RevisionList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        startDateField = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        endDateField = new com.toedter.calendar.JDateChooser();
        SaveHtml = new javax.swing.JButton();
        SavePDF = new javax.swing.JButton();
        run = new javax.swing.JButton();
        FilterButton = new javax.swing.JButton();
        statusAnimationLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        xmlUrlField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        TabPanel = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        output = new javax.swing.JEditorPane();
        LogScrollPane = new javax.swing.JScrollPane();
        LogPanel = new javax.swing.JEditorPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        RestoreXSLT = new javax.swing.JButton();
        LoadXSLT = new javax.swing.JButton();
        SaveXSLT = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        xsltEditPanel = new javax.swing.JEditorPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        JiraXmlPanel = new javax.swing.JTextArea();
        saveCSV = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        mainPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mainPanel.setMaximumSize(new java.awt.Dimension(1600, 1200));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(820, 600));
        mainPanel.setRequestFocusEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        PasswordField.setName("PasswordField"); // NOI18N
        PasswordField.setNextFocusableComponent(jList1);

        jLabel5.setText("Password");
        jLabel5.setName("PassWordLabel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "All", "CIDEV", "CIDEVAS", "CIDEVCOI", "CIDEVCEI", "CIDEVDM", "CIDEVEOI", "CIDEVMI", "CIDEVSA", "CIINT", "CISA", "CIUX", "CPOP", "SYSENG" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setName("ReportSelectorList"); // NOI18N
        jList1.setNextFocusableComponent(run);
        jList1.setSelectedIndex(0);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ReportSelectChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        UsernameField.setName("UsernameField"); // NOI18N
        UsernameField.setNextFocusableComponent(PasswordField);

        jLabel4.setText("Username");
        jLabel4.setName("UserNameLabel"); // NOI18N

        filterStatusCheckbox.setSelected(true);
        filterStatusCheckbox.setText("Filter Status");
        filterStatusCheckbox.setToolTipText("When Selected, only issues with a status in (Unresolved, Dropped-May Impact, Dropped-No Impact, Work Remains, Fixed,Won't Fix, Incomplete, Cannot Reproduce) are shown."); // NOI18N
        filterStatusCheckbox.setName("filterStatusCheckbox"); // NOI18N
        filterStatusCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterStatusChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(UsernameField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(filterStatusCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterStatusCheckbox))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        ProjectList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ProjectList.setName("ProjectList"); // NOI18N
        ProjectList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ProjectChanged(evt);
            }
        });
        jScrollPane3.setViewportView(ProjectList);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        RevisionList.setName("RevisionList"); // NOI18N
        RevisionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                VersionSelected(evt);
            }
        });
        jScrollPane4.setViewportView(RevisionList);

        jLabel1.setText("Start");
        jLabel1.setName("StartDateLabel"); // NOI18N

        startDateField.setDateFormatString("MM/dd/yyyy"); // NOI18N
        startDateField.setName("startDateField"); // NOI18N

        jLabel2.setText("End");
        jLabel2.setName("EndDateLabel"); // NOI18N

        endDateField.setDateFormatString("MM/dd/yyyy"); // NOI18N
        endDateField.setName("endDateField"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(endDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(startDateField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(endDateField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        SaveHtml.setText("Save HTML");
        SaveHtml.setName("SaveHtml"); // NOI18N
        SaveHtml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveHTMLDocument(evt);
            }
        });

        SavePDF.setText("Save PDF");
        SavePDF.setName("SavePDFButton"); // NOI18N
        SavePDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SavePDFDocument(evt);
            }
        });

        run.setText("Run");
        run.setName("run"); // NOI18N
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunPressed(evt);
            }
        });

        FilterButton.setText("Filter");
        FilterButton.setName("FilterButton"); // NOI18N
        FilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterPressed(evt);
            }
        });

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        jLabel6.setText("Filter jira tasks by project");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("Filter by iteration, comments by date");
        jLabel7.setName("jLabel7"); // NOI18N

        xmlUrlField.setText("https://jira.oceanobservatories.org/tasks/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=project+in+(CIDEV%2C+CIDEVAS%2C+CIDEVCOI%2C+CIDEVCEI%2C+CIDEVDM%2C+CIDEVEOI%2C+CIDEVMI%2C+CIDEVSA%2C+CIINT%2C+CISA%2C+CIUX%2C+CPOP%2C+SYSENG)+AND+issuetype+in+(Task%2C+%22Action+Item%22%2C+Summary)+AND+resolution+in+(%22Fixed%22%2C%22Dropped-No+Impact%22%2C%22Dropped-May+Impact%22%2C%22Work+Remains%22)+ORDER+BY+key+ASC"); // NOI18N
        xmlUrlField.setName("xmlUrlField"); // NOI18N
        xmlUrlField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xmlUrlFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("XML URL");
        jLabel3.setName("UrlLabel"); // NOI18N

        jButton1.setText("unFilter");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        TabPanel.setName("TabPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        output.setName("output"); // NOI18N
        output.setPreferredSize(new java.awt.Dimension(640, 480));
        jScrollPane1.setViewportView(output);

        TabPanel.addTab("Output", jScrollPane1);

        LogScrollPane.setName("LogScrollPane"); // NOI18N

        LogPanel.setName("LogPanel"); // NOI18N
        LogScrollPane.setViewportView(LogPanel);

        TabPanel.addTab("Log", LogScrollPane);

        jPanel3.setName("jPanel3"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        RestoreXSLT.setText("Restore");
        RestoreXSLT.setName("RestoreXSLT"); // NOI18N
        RestoreXSLT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestoreXSLTActionPerformed(evt);
            }
        });

        LoadXSLT.setText("Load");
        LoadXSLT.setName("LoadXSLT"); // NOI18N
        LoadXSLT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadXSLTActionPerformed(evt);
            }
        });

        SaveXSLT.setText("Save");
        SaveXSLT.setName("SaveXSLT"); // NOI18N
        SaveXSLT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveXSLTActionPerformed(evt);
            }
        });

        jLabel8.setText("WARNING: Changes made here will take effect in the next run/filter/unFilter.");
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RestoreXSLT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LoadXSLT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SaveXSLT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RestoreXSLT)
                    .addComponent(LoadXSLT)
                    .addComponent(SaveXSLT)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        xsltEditPanel.setName("xsltEditPanel"); // NOI18N
        jScrollPane6.setViewportView(xsltEditPanel);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
        );

        TabPanel.addTab("Edit XSLT", jPanel3);

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        JiraXmlPanel.setColumns(20);
        JiraXmlPanel.setEditable(false);
        JiraXmlPanel.setRows(5);
        JiraXmlPanel.setName("JiraXmlPanel"); // NOI18N
        jScrollPane5.setViewportView(JiraXmlPanel);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );

        TabPanel.addTab("Jira XML", jPanel5);

        saveCSV.setText("Save CSV");
        saveCSV.setName("saveCSV"); // NOI18N
        saveCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveCSVFile(evt);
            }
        });

        jLabel9.setText("On a Mac, use control-c/control-v for cut/paste");
        jLabel9.setName("jLabel9"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(run)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SavePDF, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SaveHtml)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveCSV)
                        .addGap(12, 12, 12)
                        .addComponent(statusAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FilterButton)
                        .addContainerGap())))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xmlUrlField, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 927, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(TabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 967, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xmlUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FilterButton)
                        .addComponent(jButton1))
                    .addComponent(run)
                    .addComponent(SavePDF)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SaveHtml)
                        .addComponent(saveCSV)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents
    
    private update u;
    private void RunPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunPressed
        if ((UsernameField.getText().toString().length() > 1) && (PasswordField.getText().toString().length() > 1)) {
//            System.out.println(" " + PasswordField.getText().toString().length() + UsernameField.getText().toString().length());
            run.setEnabled(false);


            u = new update(this, busyIconTimer, output, xmlUrlField.getText(), run, startDateField.getDate(), endDateField.getDate(), UsernameField.getText(), PasswordField.getText());
            u.setXsltData(xsltEditPanel.getText());
            Thread thread = u;

            thread.start();
        } else {
            UnPwDialog win = new UnPwDialog(this.getFrame());
        }

    }//GEN-LAST:event_RunPressed

    private void ReportSelectChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ReportSelectChanged
        if ((jList1.getSelectedValue().equals("All")) && (!xmlUrlField.getText().equals(baseUrl + "jqlQuery=project+in+(" +projects +")+AND+issuetype+in+(Task%2C+%22Action+Item%22%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC")))
            xmlUrlField.setText(baseUrl + "jqlQuery=project+in+(" +projects +")+AND+issuetype+in+(Task%2C+%22Action+Item%22%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC");
        else if ((!jList1.getSelectedValue().equals("All")) && (!xmlUrlField.getText().equals(baseUrl + "jqlQuery=project+in+("  + jList1.getSelectedValue() + ")+AND+issuetype+in+(Task%2C+\"Action+Item\"%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC")))
            xmlUrlField.setText(baseUrl + "jqlQuery=project+in+("  + jList1.getSelectedValue() + ")+AND+issuetype+in+(Task%2C+\"Action+Item\"%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC");
    }//GEN-LAST:event_ReportSelectChanged

    private void SavePDFDocument(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SavePDFDocument
        //OOICI BWR Ending 02_02_02

        
        JFileChooser jfc = new JFileChooser("./");
        FileFilter filter1 = new ExtensionFileFilter("PDF", new String[] { "PDF", "pdf"});
        jfc.setFileFilter(filter1);
        jfc.setFileHidingEnabled(true);

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");

        jfc.setSelectedFile(new File("OOICI BWR (" + jList1.getSelectedValue() + ") Ending " + formatter.format(endDateField.getDate())));

        int result = jfc.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION ) {
            try {
                String fileName = jfc.getSelectedFile().getCanonicalPath();
                if ((!fileName.endsWith(".pdf")) && (!fileName.endsWith(".pdf")))
                    fileName = fileName + ".pdf";



                htmlToPdf h2p = new htmlToPdf(dateRange);

                h2p.HtmlToPDF2(output.getText().replaceAll("<br>","<BR  />"), fileName);
            } catch (Exception ex) {
                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }//GEN-LAST:event_SavePDFDocument

    private void SaveHTMLDocument(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveHTMLDocument

        JFileChooser jfc = new JFileChooser(".");
        FileFilter filter1 = new ExtensionFileFilter("HTML", new String[] { "HTML", "html"});
        jfc.setFileFilter(filter1);
        jfc.setFileHidingEnabled(true);


        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");



        jfc.setSelectedFile(new File("OOICI BWR (" + jList1.getSelectedValue() + ") Ending " + formatter.format(endDateField.getDate())));
        int result = jfc.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION ) {
            try {
                String fileName = jfc.getSelectedFile().getCanonicalPath();
                if ((!fileName.endsWith(".HTML")) && (!fileName.endsWith(".html")))
                    fileName = fileName + ".html";

                FileWriter fstream = new FileWriter(fileName);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(output.getText().replaceAll("<br>","<BR  />"));
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_SaveHTMLDocument


    void updateProjectVersions(Map<String, ArrayList<String>> versions) {
        this.versions = versions;

        if (lm1 == null) {
            lm1 = new DefaultListModel();
            ProjectList.setModel(lm1);
        } else
            lm1.clear();
        List <String>l = new ArrayList(versions.keySet());
        Collections.sort(l);

        for (String proj : l) {
            //log("PROJECT " + proj);
            int pos = lm1.getSize();
            lm1.add(pos, proj);
            List <String>v = new ArrayList();

            String greatest = null;
            for (int i = 0; i < versions.get(proj).size();i++) {

            //log("   VERSION " + versions.get(proj).get(i));
                if ((greatest == null) || (greatest.compareTo(versions.get(proj).get(i)) < 0)) {
                    greatest = versions.get(proj).get(i);
                }
            }
            v.add(greatest);
          //  log(proj +  " = " + v.get(0).toString());
            selected.put(proj, v);
        }

        ProjectList.setSelectedIndex(0);
        ProjectList.setVisible(true);


    }


boolean locked = false;
    private void ProjectChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ProjectChanged
//System.out.println("**IN** ProjectChanged");
        locked = true;
        if (ProjectList.getSelectedValue() != null) { // why did i have this commented out.
//            System.out.println(ProjectList.getSelectedValue().toString());
         
            ArrayList<String> vers = versions.get(ProjectList.getSelectedValue().toString());
        

            if (lm2 == null) {
                lm2 = new DefaultListModel();
                RevisionList.setModel(lm2);
            } else
                lm2.clear();

            for (String ver : vers) {
                int pos = lm2.getSize();
                lm2.add(pos, ver);
            }

            int[] selectedIndices = new int[selected.get(ProjectList.getSelectedValue().toString()).size()];
            int i = 0;

            for (int x = 0; x < RevisionList.getModel().getSize(); x++) 
                if (selected.get(ProjectList.getSelectedValue().toString())  .contains  (RevisionList.getModel().getElementAt(x).toString()))
                    selectedIndices[i++] = x;
                
            

            RevisionList.setSelectedIndices(selectedIndices);
        }
        locked = false;
    }//GEN-LAST:event_ProjectChanged

    public void log(String msg) {
        if (TabPanel.getSelectedIndex() != 1)
            TabPanel.setSelectedIndex(1);
        Document doc = LogPanel.getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }
        LogPanel.setCaretPosition( LogPanel.getText().length() );


    }

    public void output(String msg) {
        if (TabPanel.getSelectedIndex() != 0)
            TabPanel.setSelectedIndex(0);
        output.setText(msg);
        
        output.setCaretPosition( 0 );
    }

    private void VersionSelected(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_VersionSelected
        if (!locked) {
            int selections[] = RevisionList.getSelectedIndices();
            Object selectedValues[] = RevisionList.getSelectedValues();




            List <String> v = new ArrayList<String>();
            
            for (int i = 0, n = selections.length; i < n; i++) 
                v.add(selectedValues[i].toString());
                
            selected.put(ProjectList.getSelectedValue().toString(), v);
        }
    }//GEN-LAST:event_VersionSelected

    private void FilterPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterPressed


       



        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");
        dateRange = "<br/>for period of<br/>" + formatter.format(startDateField.getDate()) + " &#8212; " + formatter.format(endDateField.getDate());


        run.setEnabled(false);
        SaveHtml.setEnabled(true);
        SavePDF.setEnabled(true);
        saveCSV.setEnabled(true);
        u.setXsltData(xsltEditPanel.getText());
        u.setDateRange(startDateField.getDate(), endDateField.getDate());
        u.filter(selected);
        run.setEnabled(true);
    }//GEN-LAST:event_FilterPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // unFIlter
        u.setXsltData(xsltEditPanel.getText());
        u.setDateRange(startDateField.getDate(), endDateField.getDate());
        u.unfilter();
        dateRange = "";
    }//GEN-LAST:event_jButton1ActionPerformed

    private void RestoreXSLTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestoreXSLTActionPerformed
        xsltEditPanel.setText("");
        Document doc = xsltEditPanel.getDocument();

        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/xslt/jiraxml2BWR_v1.4.xsl")));
        String line;
        try {
            while (null != (line = br.readLine())) {
                doc.insertString(doc.getLength(), line + "\n", null);
            }
        } catch (IOException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        xsltEditPanel.setCaretPosition(0);
    }//GEN-LAST:event_RestoreXSLTActionPerformed

    public String loadFile(String file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }

        String line = null; //not declared within while loop

        try {

            while ((line = input.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();
    }


    private void LoadXSLTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadXSLTActionPerformed
        JFileChooser jfc = new JFileChooser(".");
        FileFilter filter1 = new ExtensionFileFilter("XSL", new String[] { "xsl", "XSL"});
        jfc.setFileFilter(filter1);
        jfc.setFileHidingEnabled(true);

        int result = jfc.showOpenDialog(xsltEditPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
//            xsltEditPanel.setText("");
//            Document doc = xsltEditPanel.getDocument();

            xsltEditPanel.setText(loadFile(jfc.getSelectedFile().toString()));
//            BufferedReader input = null;
//            try {
//                input = new BufferedReader(new FileReader(jfc.getSelectedFile().toString()));
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            String line = null; //not declared within while loop
//
//            try {
//                while ((line = input.readLine()) != null) {
//                    doc.insertString(doc.getLength(), line + "\n", null);
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (BadLocationException ex) {
//                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        xsltEditPanel.setCaretPosition(0);
    }//GEN-LAST:event_LoadXSLTActionPerformed

    private void SaveXSLTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveXSLTActionPerformed
        JFileChooser jfc = new JFileChooser(".");
        FileFilter filter1 = new ExtensionFileFilter("XSL", new String[] { "xsl", "XSL"});
        jfc.setFileFilter(filter1);
        jfc.setFileHidingEnabled(true);

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");

        jfc.setSelectedFile(new File("JiraReport-" + formatter.format(endDateField.getDate())));
        int result = jfc.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION ) {
            try {
                String fileName = jfc.getSelectedFile().getCanonicalPath();
                if ((!fileName.toLowerCase().endsWith(".xslt")) && (!fileName.toLowerCase().endsWith(".xsl")))
                    fileName = fileName + ".xsl";

                FileWriter fstream = new FileWriter(fileName);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(xsltEditPanel.getText());

                out.close();
            } catch (Exception ex) {
                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_SaveXSLTActionPerformed

    private void xmlUrlFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xmlUrlFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xmlUrlFieldActionPerformed

    private void FilterStatusChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterStatusChanged
        if (filterStatusCheckbox.getSelectedObjects() == null)
            filterClause = "";
        else
            filterClause = "AND+resolution+in+(%22Fixed%22%2C%22Dropped-No+Impact%22%2C%22Dropped-May+Impact%22%2C%22Work+Remains%22)+";

        if ((jList1.getSelectedValue().equals("All")) && (!xmlUrlField.getText().equals(baseUrl + "jqlQuery=project+in+(" +projects +")+AND+issuetype+in+(Task%2C+%22Action+Item%22%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC")))
            xmlUrlField.setText(baseUrl + "jqlQuery=project+in+(" +projects +")+AND+issuetype+in+(Task%2C+%22Action+Item%22%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC");
        else if ((!jList1.getSelectedValue().equals("All")) && (!xmlUrlField.getText().equals(baseUrl + "jqlQuery=project+in+("  + jList1.getSelectedValue() + ")+AND+issuetype+in+(Task%2C+\"Action+Item\"%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC")))
            xmlUrlField.setText(baseUrl + "jqlQuery=project+in+("  + jList1.getSelectedValue() + ")+AND+issuetype+in+(Task%2C+\"Action+Item\"%2C+Summary)+" + filterClause + "ORDER+BY+key+ASC");
    }//GEN-LAST:event_FilterStatusChanged

    private void SaveCSVFile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveCSVFile

        // i have u
        // u has access to dp
   

        JFileChooser jfc = new JFileChooser(".");
        FileFilter filter1 = new ExtensionFileFilter("CSV", new String[] { "CSV", "csv"});
        jfc.setFileFilter(filter1);
        jfc.setFileHidingEnabled(true);


        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");



        jfc.setSelectedFile(new File("OOICI BWR (" + jList1.getSelectedValue() + ") Ending " + formatter.format(endDateField.getDate())));
        int result = jfc.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION ) {
            try {
                String fileName = jfc.getSelectedFile().getCanonicalPath();
                if ((!fileName.endsWith(".CSV")) && (!fileName.endsWith(".csv")))
                    fileName = fileName + ".csv";

                FileWriter fstream = new FileWriter(fileName);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(u.getCSV());

                out.close();
            } catch (Exception ex) {
                Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }










        // TODO add your handling code here:
    }//GEN-LAST:event_SaveCSVFile

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FilterButton;
    private javax.swing.JTextArea JiraXmlPanel;
    private javax.swing.JButton LoadXSLT;
    private javax.swing.JEditorPane LogPanel;
    private javax.swing.JScrollPane LogScrollPane;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JList ProjectList;
    private javax.swing.JButton RestoreXSLT;
    private javax.swing.JList RevisionList;
    private javax.swing.JButton SaveHtml;
    private javax.swing.JButton SavePDF;
    private javax.swing.JButton SaveXSLT;
    private javax.swing.JTabbedPane TabPanel;
    private javax.swing.JTextField UsernameField;
    private com.toedter.calendar.JDateChooser endDateField;
    private javax.swing.JCheckBox filterStatusCheckbox;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JEditorPane output;
    private javax.swing.JButton run;
    private javax.swing.JButton saveCSV;
    private com.toedter.calendar.JDateChooser startDateField;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JTextField xmlUrlField;
    private javax.swing.JEditorPane xsltEditPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    void enablePostRunControls() {
        FilterButton.setEnabled(true);
        RevisionList.setEnabled(true);
        ProjectList.setEnabled(true);
        SaveHtml.setEnabled(true);
        SavePDF.setEnabled(true);
        saveCSV.setEnabled(true);
    }

    void setActiveTab(int i) {
        TabPanel.setSelectedIndex(i);
//        System.out.println("SETTING ACTIVE TAB " + i);
    }




public  String format(String xml) throws ParsingException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Serializer serializer = new Serializer(out);
    serializer.setIndent(4);  // or whatever you like
    serializer.write(new Builder().build(xml, ""));
    return out.toString("UTF-8");
}


    void displayJiraXml(String jiraXML) {
        try {
            JiraXmlPanel.setText(format(jiraXML));
        } catch (Exception ex) {
            Logger.getLogger(JiraAppGUIView.class.getName()).log(Level.SEVERE, null, ex);
            JiraXmlPanel.setText(jiraXML);
        }
        JiraXmlPanel.setCaretPosition(0);
    }


}
