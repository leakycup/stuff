<?xml version="1.0" encoding="UTF-8"?>

<!-- DISCUSSION OBJECT TEMPLATE-->
<!-- Project: SearchMonkey -->
<!-- Owner: Aditya Sakhuja (asakhuja@yahoo-inc.com) -->

<!-- Searchmonkey Editorial Rule Version 2.0 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes"/>
<xsl:output omit-xml-declaration="yes"/>
<xsl:param name="CURRURL" select="'http://default.url'"/>
<xsl:param name="CRAWLED_TIME" select="'2007-12-12'"/>

<!-- DO NOT MODIFY ANYTHING ABOVE THIS LINE -->




<!-- FOR EDITORS: Please update the xpaths below -->
        <xsl:param name="postTitle" select="(//div[@class='postsubject'])[1]"/>
        <xsl:param name="createdDate" select="(//td[@class='postbottom'])[1]"/>
        <xsl:param name="creatorName" select="(//div[@class='postauthor'])[1]"/>
        <xsl:param name="numReplies" select="substring-before(substring-after((//td[contains(.,'messaggi ]')])[2],'['),'messaggi ]')"/>




	
<!-- DO NOT MODIFY ANYTHING BELOW THIS LINE -->

<!-- String select-URL(String web_site, String URL1,
                       String URL2 = "", String URL3 = "", String URL4 = "", String URL5 = ""
                       String exclude-inurl1 = "", String exclude-inurl2 = "", String exclude-inurl3 = "", String exclude-inurl4 = "", String exclude-inurl5 = "",
                       Boolean display = true
                       )
-->
  <xsl:template name="select-URL">
    <xsl:param name="web_site"/>
    <xsl:param name="URL1"/> 
    <xsl:param name="URL2"/> 
    <xsl:param name="URL3"/> 
    <xsl:param name="URL4"/> 
    <xsl:param name="URL5"/>
    
    <xsl:param name="exclude-inurl1"/>
    <xsl:param name="exclude-inurl2"/>
    <xsl:param name="exclude-inurl3"/>
    <xsl:param name="exclude-inurl4"/>
    <xsl:param name="exclude-inurl5"/>
    
    <xsl:param name="display"/>
    
    <xsl:variable name="__display">
      <xsl:choose>
        <xsl:when test="$display"><xsl:value-of select="$display"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="true()"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="not($__display)"><xsl:value-of select="string(//NaN)"/></xsl:when>
      <xsl:otherwise>
        <xsl:variable name="__raw_URL">
          <xsl:choose>
            <xsl:when test="($URL1) and (string-length($URL1) &gt; 0)"><xsl:value-of select="$URL1"/></xsl:when>
            <xsl:when test="($URL2) and (string-length($URL2) &gt; 0)"><xsl:value-of select="$URL2"/></xsl:when>
            <xsl:when test="($URL3) and (string-length($URL3) &gt; 0)"><xsl:value-of select="$URL3"/></xsl:when>
            <xsl:when test="($URL4) and (string-length($URL4) &gt; 0)"><xsl:value-of select="$URL4"/></xsl:when>
            <xsl:when test="($URL5) and (string-length($URL5) &gt; 0)"><xsl:value-of select="$URL5"/></xsl:when>
          </xsl:choose>
        </xsl:variable> 
        <xsl:choose>
          <xsl:when test="string-length($__raw_URL) = 0"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="$exclude-inurl1 and contains($__raw_URL, $exclude-inurl1)"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="$exclude-inurl2 and contains($__raw_URL, $exclude-inurl2)"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="$exclude-inurl3 and contains($__raw_URL, $exclude-inurl3)"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="$exclude-inurl4 and contains($__raw_URL, $exclude-inurl4)"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="$exclude-inurl5 and contains($__raw_URL, $exclude-inurl5)"><xsl:value-of select="string(//NaN)"/></xsl:when>
          <xsl:when test="starts-with($__raw_URL, 'http')"><xsl:value-of select="$__raw_URL"/></xsl:when>
          <xsl:when test="starts-with($__raw_URL, '/')"><xsl:value-of select="concat($web_site, $__raw_URL)"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="concat($web_site, '/', $__raw_URL)"/></xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


<!-- String select-valid-path(String string, String alternate_1 = "", String alternate_2 = "") -->
  <xsl:template name="select-valid-path">
    <xsl:param name="string"/>
    <xsl:param name="alternate_1"/> 
    <xsl:param name="alternate_2"/>
    <xsl:param name="alternate_3"/>
    <xsl:param name="alternate_4"/>
    
    <xsl:choose>
      <xsl:when test="($string) and (string-length($string) &gt; 0)"><xsl:value-of select="$string"/></xsl:when>
      <xsl:when test="($alternate_1) and (string-length($alternate_1) &gt; 0)"><xsl:value-of select="$alternate_1"/></xsl:when>
      <xsl:when test="($alternate_2) and (string-length($alternate_2) &gt; 0)"><xsl:value-of select="$alternate_2"/></xsl:when>
      <xsl:when test="($alternate_3) and (string-length($alternate_3) &gt; 0)"><xsl:value-of select="$alternate_3"/></xsl:when>
      <xsl:when test="($alternate_4) and (string-length($alternate_4) &gt; 0)"><xsl:value-of select="$alternate_4"/></xsl:when>
    </xsl:choose>
  </xsl:template>


<xsl:template match="/">
<xsl:param name="needEscaping" select="true()"/>
<xsl:variable name="isPostTitle" select="(string-length($postTitle) &gt; 0)"/> 
<xsl:if test="$isPostTitle">
<PROP name="xml.docfeed">
 <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>

  <feed>
   <adjunct id="com.yahoo.rules.searchmonkey" updated="{$CRAWLED_TIME}" version="1.0" scope="private">
    <item rel="dc:subject">
      <type typeof="sioc:Forum">
       <item rel="sioc:container_of">
        <type typeof="sioc:Post">
          <xsl:if test="normalize-space($postTitle)">
            <meta property="dc:title"><xsl:value-of select="$postTitle"/></meta>
          </xsl:if>
          <xsl:if test="normalize-space($createdDate)">
            <meta property="dc:created dc:date" datatype="xsd:date"><xsl:value-of select="$createdDate"/></meta>
          </xsl:if>
          <xsl:if test="normalize-space($creatorName)">  
           <item rel="sioc:has_creator dc:creator foaf:maker">
            <type typeof="sioc:User foaf:Person vcard:VCard">
             	 <meta property="vcard:fn foaf:name"><xsl:value-of select="$creatorName"/></meta>
            </type>
           </item>
          </xsl:if>
          <xsl:if test="normalize-space($numReplies) and not(starts-with(number($numReplies),'NaN'))">          
            <meta property="sioc:num_replies"><xsl:value-of select="$numReplies"/></meta>
          </xsl:if>
        </type>
      </item>
    </type>
   </item>
  </adjunct>
 </feed>

<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
</PROP>
<METAWORD word="sief:discussion"/>
<METAWORD word="sief:has_nextgen_object"/>
<METAWORD word="sief:manual_rule"/>
<METAWORD word="sief:com.yahoo.rules.searchmonkey"/>
</xsl:if>
</xsl:template>
</xsl:stylesheet>
