<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>
<xsl:template match="/rss/channel">
<xsl:for-each select="item">
'<xsl:value-of select="component"/>","<xsl:value-of select="key"/>","<xsl:value-of select="summary"/>","<xsl:value-of select="assignee"/>","<xsl:value-of select="status"/>","<xsl:value-of select="resolution"/>",<xsl:value-of  select="substring(substring-before(substring-after(created,' '), ':'), 0,12)"/>,<xsl:value-of  select="substring(substring-before(substring-after(updated,' '), ':'), 0,12)"/>,<xsl:value-of  select="substring(substring-before(substring-after(resolved,' '), ':'), 0,12)"/>,<xsl:value-of  select="substring(substring-before(substring-after(due,' '), ':'), 0,12)"/>,"<xsl:value-of select="version"/>","<xsl:value-of select="fixVersion"/>","<xsl:for-each select="./issuelinks/issuelinktype/*">_-!-_<xsl:value-of select="@description"/><xsl:for-each select="./issuelink/issuekey">_-!-_<xsl:value-of select="."/></xsl:for-each></xsl:for-each>","<xsl:for-each select="./subtasks/subtask"><xsl:value-of select="."/>_-!-_</xsl:for-each>"
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>