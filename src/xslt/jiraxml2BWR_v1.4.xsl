<?xml version="1.0"?>

<!-- Bi Weekly report Style sheet to Render Jira tasks as XML for COL -->



<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <!-- Set some parameters -->
    <xsl:param name="url" select="'http://ci.oceanobservatories.org/tasks'"/>

    <xsl:param name="title_key" select="'Jira Key'"/>
    <xsl:param name="title_summary" select="'Task Title'"/>
    <xsl:param name="title_status" select="'Task Status'"/>
    <xsl:param name="title_comments" select="'Comments regarding Progress, Issues or Highlights'"/>
    <xsl:param name="title_resolution" select="'Res'"/>

    <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ '" />
    <xsl:output method="html"/>


    <xsl:template match="/rss/channel">

        <html>
            <head>
                <!--link rel="stylesheet" href="http://confluence.atlassian.com/styles/main-action.css" type="text/css"/ -->

                <title>Project Report for OOI CI Tasks</title>
            </head>
            <body>
                <style type="text/css">
                    /* <![CDATA[ */
#red { color: red; }
#green { color: green; }
#blue { color: blue; }

#small {font-size:10px;}
#title {font-size:14px; font-weight: bold; }
p {font-size:12px;}

a:link { color: black; text-decoration: none; border-style: hidden;}
a:visited { color: black; text-decoration: none;border-style: hidden; }
a:hover { color: black; border-style: hidden; }

table, td, th
{
    text-align: left;
    font-size:12px;
    border-color: #000000;
    border-style: solid;
}

th
{
    font-size:14px;
}

table
{
    border-width: 0 0 1px 1px;
    border-spacing: 0;
    border-collapse: collapse;
}

th, td
{
    margin: 0;
    padding: 4px;
    border-width: 1px 1px 0 0;
    background-color: #FFFFFF;
}


h2{
    text-align: center;
    font-size:16px;
}

div {
        font-size:12px;
        margin: 0px;
}

                    /* ]]> */
                </style>
                <div id="footer"></div>
                <div id="header"></div>

                <h2>Bi-weekly Report on <a href="{$url}"> OOI CI Subsystem Tasks</a><span class="smalltext"> (<xsl:value-of select="count(item/type[@id=3])"/> tasks)</span></h2>



                <!-- CI Summary! -->
                <xsl:for-each select="item">
                    <xsl:if test="contains(key, 'CIDEV-') and type[@id=7]" >
                        <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                        <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                        <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                    </xsl:if>
                </xsl:for-each>



                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CISA')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> CI System Administration Subsystem: <xsl:value-of select="count(item[contains(key,'CISA')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CISA')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CISA')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CISA')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CISA')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CISA')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CISA')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CISA')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CISA') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CISA') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVAS')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> CI Development Analysis and Synthesis Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVAS')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVAS')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVAS')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVAS')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVAS')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVAS') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVAS') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVCOI')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> Common Operating Infrastructure Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVCOI')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVCOI')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVCOI')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVCOI')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCOI')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVCOI') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>

                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVCOI') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>

                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>



                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVCEI')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> Common Execution Infrastructure Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVCEI')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVCEI')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVCEI')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVCEI')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVCEI')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVCEI') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVCEI') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>


                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVDM')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> Data Management Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVDM')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVDM')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVDM')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVDM')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (
                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVDM')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVDM') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVDM') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>




                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVEOI')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> External Observatory Integration Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVEOI')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVEOI')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVEOI')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVEOI')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVEOI')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVEOI') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVEOI') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>




                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVMI')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> CI Development Marine Integration Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVMI')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVMI')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVMI')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVMI')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVMI')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVMI') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVMI') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>








                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIDEVSA')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> Sensing &amp; Acquisition Subsystem: <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIDEVSA')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIDEVSA')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIDEVSA')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIDEVSA')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIDEVSA')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIDEVSA') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIDEVSA') and type[@id=3]" > <!-- Filter on Subsystem and type TASK-->
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>

                </xsl:if>



                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIINT')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> System Integration, Test, and Validation Subsystem: <xsl:value-of select="count(item[contains(key,'CIINT')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIINT')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIINT')]/status[@id=5])"/> Resolved</span><br/>

                    <xsl:if test="((count(item[contains(key,'CIINT')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIINT')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIINT')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIINT')]/resolution[@id=9]) > 0))" >
                        <span id="small">
                            Resolved Tasks: (
                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIINT')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIINT') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIINT') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>

                    </table>
                </xsl:if>

                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'SYSENG')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> System Engineering and Architecture Subsystem: <xsl:value-of select="count(item[contains(key,'SYSENG')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'SYSENG')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'SYSENG')]/status[@id=5])"/> Resolved</span><br/>

                    <xsl:if test="((count(item[contains(key,'SYSENG')]/resolution[@id=1]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=2]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'SYSENG')]/resolution[@id=4]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=5]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'SYSENG')]/resolution[@id=7]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=8]) > 0) or (count(item[contains(key,'SYSENG')]/resolution[@id=9]) > 0))" >
                        <span id="small">
                            Resolved Tasks: (
                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'SYSENG')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'SYSENG') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'SYSENG') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>

                    </table>
                </xsl:if>




                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CIUX')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> User Experience Subsystem: <xsl:value-of select="count(item[contains(key,'CIUX')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CIUX')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CIUX')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CIUX')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CIUX')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CIUX')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CIUX')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CIUX')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CIUX') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CIUX') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>


                <!-- Subsystem header -->
                <xsl:if test="count(item[contains(key,'CPOP')]/type[@id=3]) > 0" >
                    <p>
                        &#160;
                    </p>
                    <br/>
                    <p>
                        &#160;
                    </p>
                    <span id="title"> CI CyberPop Subsystem: <xsl:value-of select="count(item[contains(key,'CPOP')]/type[@id=3])"/> tasks,
                        <xsl:value-of select="count(item[contains(key,'CPOP')]/status[@id=3])"/> In Progress,
                    <xsl:value-of select="count(item[contains(key,'CPOP')]/status[@id=5])"/> Resolved</span><br/>
                    <xsl:if test="((count(item[contains(key,'CPOP')]/resolution[@id=1]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=2]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=3]) > 0) or
                            (count(item[contains(key,'CPOP')]/resolution[@id=4]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=5]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=6]) > 0) or
                            (count(item[contains(key,'CPOP')]/resolution[@id=7]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=8]) > 0) or (count(item[contains(key,'CPOP')]/resolution[@id=9]) > 0))" >

                        <span id="small">
                            Resolved Tasks: (

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=1])"/> Fixed,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=2])"/> Won't Fix,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=3])"/> Duplicate,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=4])"/> Incomplete,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=5])"/> Cannot Reproduce,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=6])"/> Dropped-No Impact,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=7])"/> Dropped-May Impact,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=8])"/> Work Remains,

                            <xsl:value-of select="count(item[contains(key,'CPOP')]/resolution[@id=9])"/> Mistaken Entry

                            )
                        </span>
                    </xsl:if>
                    <!-- Summary Section-->
                    <xsl:for-each select="item">
                        <xsl:if test="contains(key, 'CPOP') and type[@id=7]" >
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="SummaryProgress"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="SummaryIssue"/>
                            <xsl:apply-templates select="comments/comment[contains(., '[BWR-T')]" mode="SpecialTask"/>
                        </xsl:if>
                    </xsl:for-each>
                    <p/>
                    <!-- Main Section-->
                    <table border="1" RULES="ALL" FRAME="BOX" width="100%">
                        <tr>
                            <th width="100">
                                <xsl:value-of select="$title_key"/>
                            </th>
                            <th width="140">
                                <xsl:value-of select="$title_status"/>
                            </th>
                            <th width="580">
                                <xsl:value-of select="$title_summary"/>
                            </th>
                        </tr>
                        <xsl:for-each select="item">
                            <xsl:sort select="status/@id" order="descending"/>
                            <xsl:if test="contains(key, 'CPOP') and type[@id=3]" >
                                <tr>
                                    <xsl:apply-templates select="key"/>
                                    <xsl:apply-templates select="status"/>
                                    <td width="560">
                                      <xsl:apply-templates select="summary"/>
                                      <div align="right" id="small">
                                        <xsl:apply-templates select="customfields/customfield/customfieldvalues/customfieldvalue[contains(., 'http')]" mode="DesignLink"/>
                                      </div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="DescriptionXXX" mode="DescriptionXXX"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-H')]" mode="Highlight"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-P')]" mode="Progress"/>
                                <xsl:apply-templates select="comments/comment[contains(., '[BWR-I')]" mode="Issue"/>
                            </xsl:if>
                        </xsl:for-each>
                    </table>
                </xsl:if>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="DescriptionXXX" mode="DescriptionXXX">
        <tr>
            <td width="100">DescriptionXXX </td>
            <td colspan="2">
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>




    <xsl:template match="key">
        <td width="120">
            <div>
                <strong>
                    <a>
                        <xsl:attribute name="href">https://www.oceanobservatories.org/tasks/browse/<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/>
                    </a>
                </strong>
            </div>
            <div align="right" id="small">
                <xsl:value-of select="../assignee"/>
            </div>
        </td>
    </xsl:template>

    <xsl:template match="summary">

            <xsl:value-of select="."/>


    </xsl:template>

    <xsl:template match="status">
        <td  width="140">
            <xsl:if test=". = 'In Progress'" >
                <div>
                    <xsl:value-of select="."/>
                </div>
            </xsl:if>
            <xsl:if test=". = 'Open'" >
                <div id="red">
                    <xsl:value-of select="."/>
                </div>
            </xsl:if>
            <xsl:if test=". = 'Reopened'" >
                <div id="red">
                    Open
                </div>
            </xsl:if>
            <xsl:if test=". = 'Closed'" >
                <div id="green">
                    <xsl:value-of select="."/>
                </div>
            </xsl:if>
            <xsl:if test=". = 'Resolved'" >
                <div id="green">
                    <xsl:value-of select="."/> (
                    <xsl:value-of select="../resolution"/>)
                </div>
            </xsl:if>
        </td>
    </xsl:template>

    <xsl:template match="resolution">
        <td>
            <p>
                <xsl:value-of select="."/>
            </p>
        </td>
    </xsl:template>

    <xsl:template match="comments/comment" mode="Progress">
        <xsl:if test="contains(., 'BWR-P')" >
            <tr>
                <td width="120">Progress </td>
                <td colspan="2" width="800px">
                    <div>
                        <xsl:value-of select="."/>
                    </div>
                    <div align="right" id="small">

                        <xsl:if test='string-length(./@created)=36'>
                            <xsl:value-of select='substring(./@created, 0, 16)'/>
                        </xsl:if>
                        <xsl:if test='not(string-length(./@created)=36)'>
                            <xsl:value-of select='substring(./@created, 0, 17)'/>
                        </xsl:if>
                    </div>

                </td>
            </tr>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comments/comment" mode="Highlight">
        <xsl:if test="contains(., 'BWR-H')" >
            <tr>
                <td  width="100">Highlight </td>
                <td colspan="2" width="720px">
                    <xsl:value-of select="."/>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comments/comment" mode="Issue">
        <xsl:if test="contains(., 'BWR-I')" >
            <tr>
                <td  width="100">Issue </td>
                <td colspan="2" width="720px"> PPP
                    <xsl:value-of select="."/>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comments/comment" mode="SummaryProgress">
        <xsl:if test="contains(., 'BWR-P')" >
            <p>
                <strong>Summary Progress: </strong>
                <xsl:value-of select="."/>
            </p>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comments/comment" mode="SummaryIssue">
        <xsl:if test="contains(., 'BWR-I')" >
            <p>
                <strong>Summary Issues: </strong>
                <xsl:value-of select="."/>
            </p>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comments/comment" mode="SpecialTask">
        <xsl:if test="contains(., 'BWR-T')" >
            <p>
                <strong>Special Task: </strong>
                <xsl:value-of select="."/>
            </p>
        </xsl:if>
    </xsl:template>

    <xsl:template match="customfields/customfield/customfieldvalues/customfieldvalue" mode="DesignLink">
      <a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>Design Doc</a>
    </xsl:template>
</xsl:stylesheet>
