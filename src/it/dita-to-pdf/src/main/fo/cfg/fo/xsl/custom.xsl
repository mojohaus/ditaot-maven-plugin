<?xml version='1.0'?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
  version="1.1">

  <!-- ============================================================================= -->
  <!--  give bottom footer more room -->
  <!-- ============================================================================= -->
  <xsl:variable name="page-margin-bottom" select="'30mm'"/>


  <!-- ============================================================================= -->
  <!--  Global vars used in this file -->
  <!-- ============================================================================= -->
  <xsl:variable name="productName" select="$map//*[contains(@class,' bookmap/booklibrary ')]" />
  <xsl:variable name="productVersion" >
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'productVersion'" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="bookid">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'bookId'" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="bookName" select="$map//*[contains(@class,' bookmap/mainbooktitle ')]" />
  <xsl:variable name="feedbackStr">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'feedbackStr'" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="versionStr">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'versionStr'" />
    </xsl:call-template>
  </xsl:variable>

  <!-- ============================================================================= -->
  <!--  override cover page transformation                                           -->
  <!--  due to a bug in FO PDF plugin, this template also creates notice page right  -->
  <!--   after cover page                                                            -->
  <!-- ============================================================================= -->
  <xsl:template name="createFrontMatter_1.0">
    <xsl:message>
      calling customized createFrontMatter_1.0 ...
      <xsl:value-of select="current-date()" />
    </xsl:message>
    <fo:page-sequence master-reference="front-matter" xsl:use-attribute-sets="__force__page__count">
      <!-- xsl:call-template name="insertFrontMatterStaticContents" /-->
      <fo:flow flow-name="xsl-region-body">

        <!-- set the banner, make sure booklibrary contains no text -->
        <fo:block xsl:use-attribute-sets="__frontmatter__banner__image">
          <xsl:apply-templates select="$map//*[contains(@class,' bookmap/booktitlealt ')][1]" />
        </fo:block>

        <!-- set the product name -->
        <xsl:call-template name="insertCoverPageProjectName" />

        <!-- set the version -->
        <fo:block xsl:use-attribute-sets="__frontmatter__product__version">
          <xsl:value-of select="$versionStr" />&#xA0;
          <xsl:value-of select="$productVersion" />
        </fo:block>

        <!-- set book name -->
        <fo:block xsl:use-attribute-sets="__frontmatter__product__bookname">
          <xsl:value-of select="$bookName" />
        </fo:block>

        <!-- set company logo -->
        <fo:block xsl:use-attribute-sets="__frontmatter__company__logo" break-after="page">>
          <xsl:apply-templates select="$map//*[contains(@class,' bookmap/booktitlealt ')][2]" />
        </fo:block>

        <xsl:call-template name="insertNotices" />

      </fo:flow>

    </fo:page-sequence>
  </xsl:template>

  <xsl:template name="insertCoverPageProjectName">
    <fo:block xsl:use-attribute-sets="__frontmatter__product__name">
      <fo:inline xsl:use-attribute-sets="__frontmatter__product__name__text">
        <xsl:value-of select="$productName" />
      </fo:inline>
      <fo:inline xsl:use-attribute-sets="__frontmatter__product__name__trademark">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'trademarkChar'" />
        </xsl:call-template>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <!--  disable mini toc at the chapter's 1st page -->
  <xsl:template match="*" mode="createMiniToc">
  </xsl:template>

  <!-- remove fo:block from original template -->
  <xsl:template match="*[contains(@class, ' bookmap/booktitlealt ')]">
    <xsl:apply-templates />
  </xsl:template>

  <!-- ============================================================================= -->
  <!-- Notice page processing                                                        -->
  <!-- ============================================================================= -->

  <xsl:attribute-set name="notices.page">
    <xsl:attribute name="padding-top">60pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="notices.bold">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="notices.text">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-bottom">10pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template name="createCurrentDate">
    <xsl:variable name="date" select="current-date()" />
    <xsl:variable name="year" select="year-from-date($date)" />
    <xsl:variable name="month" select="month-from-date($date)" />
    <xsl:variable name="day" select="day-from-date($date)" />

    <xsl:value-of select="$day" />
    &#xA0;
    <xsl:choose>
      <xsl:when test="$month = '1' or $month = '01'">
        January
      </xsl:when>
      <xsl:when test="$month = '2' or $month = '02'">
        February
      </xsl:when>
      <xsl:when test="$month = '3' or $month = '03'">
        March
      </xsl:when>
      <xsl:when test="$month = '4' or $month = '04'">
        April
      </xsl:when>
      <xsl:when test="$month = '5' or $month = '05'">
        May
      </xsl:when>
      <xsl:when test="$month = '6' or $month = '06'">
        June
      </xsl:when>
      <xsl:when test="$month = '7' or $month = '07'">
        July
      </xsl:when>
      <xsl:when test="$month = '8' or $month = '08'">
        August
      </xsl:when>
      <xsl:when test="$month = '9' or $month = '09'">
        September
      </xsl:when>
      <xsl:when test="$month = '10'">
        October
      </xsl:when>
      <xsl:when test="$month = '11'">
        November
      </xsl:when>
      <xsl:when test="$month = '12'">
        December
      </xsl:when>
    </xsl:choose>
    &#xA0;
    <xsl:value-of select="$year" />
  </xsl:template>

  <xsl:template name="insertNotices">

    <fo:block xsl:use-attribute-sets="notices.page">
      <xsl:value-of select="$productName" />&#xA0;
      <xsl:value-of select="$bookName" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:value-of select="$versionStr" />&#xA0;
      <xsl:value-of select="$productVersion" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="createCurrentDate" />
    </fo:block>


    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:value-of select="$bookid" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.copyright.text'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.bold">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.trademarks.label'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.trademarks.text'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.bold">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.regulatoryCompliance.label'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.regulatoryCompliance.text'" />
      </xsl:call-template>
    </fo:block>

  </xsl:template>


  <!-- ============================================================================= -->
  <!-- Footer/header                                                                 -->
  <!-- ============================================================================= -->

  <xsl:template name="createFooterLine1">
    <xsl:value-of select="$productName" />&#xA0;
    <xsl:value-of select="$versionStr" />&#xA0;
    <xsl:value-of select="$productVersion" />&#xA0;
    <xsl:value-of select="$bookName" />
  </xsl:template>

  <xsl:template name="createFooterLine3">
    http://mojo.codehaus.org/dita-maven-plugin
    <fo:basic-link external-destination="url('http://mojo.codehaus.org/dita-maven-plugin')">
      •&#xA0;<xsl:value-of select="$feedbackStr" />
    </fo:basic-link>
  </xsl:template>

  <xsl:template name="insertOddFooterContent">
    <fo:block xsl:use-attribute-sets="__body__odd__footer__1">
      <xsl:call-template name="createFooterLine1" />
    </fo:block>

    <!--  line 2 -->
    <fo:table>
      <fo:table-column column-number="1" />
      <fo:table-column column-number="2" />
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__odd__footer__2_pagenumber">
              <fo:page-number />
            </fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__odd__footer__2">
              <xsl:value-of select="$bookid" />
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:block xsl:use-attribute-sets="__body__odd__footer__3">
      <xsl:call-template name="createFooterLine3" />
    </fo:block>

  </xsl:template>

  <xsl:template name="insertEvenFooterContent">
    <fo:block xsl:use-attribute-sets="__body__even__footer__1">
      <xsl:call-template name="createFooterLine1" />
    </fo:block>

    <!--  line 2 -->
    <fo:table>
      <fo:table-column column-number="1" />
      <fo:table-column column-number="2" />
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__even__footer__2">
              <xsl:value-of select="$bookid" />
            </fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__even__footer__2_pagenumber">
              <fo:page-number />
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:block xsl:use-attribute-sets="__body__even__footer__3">
      <xsl:call-template name="createFooterLine3" />
    </fo:block>
  </xsl:template>

  <xsl:template name="insertTocOddFooter">
    <fo:static-content flow-name="odd-toc-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>

  <xsl:template name="insertTocEvenFooter">
    <fo:static-content flow-name="even-toc-footer">
      <xsl:call-template name="insertEvenFooterContent" />
    </fo:static-content>
  </xsl:template>

  <xsl:template name="insertBodyFirstFooter">
    <fo:static-content flow-name="first-body-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>


  <xsl:template name="insertBodyOddFooter">
    <fo:static-content flow-name="odd-body-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>

  <xsl:template name="insertBodyEvenFooter">
    <fo:static-content flow-name="even-body-footer">
      <xsl:call-template name="insertEvenFooterContent" />
    </fo:static-content>
  </xsl:template>


  <xsl:attribute-set name="__frontmatter__banner__image">
    <xsl:attribute name="margin-top">25pt</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="margin-bottom">100pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__frontmatter__product__name">
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-family">Sans</xsl:attribute>
    <xsl:attribute name="margin-bottom">50pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__frontmatter__product__name__text">
    <xsl:attribute name="font-size">36pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__frontmatter__product__name__trademark">
    <xsl:attribute name="font-size">24pt</xsl:attribute>
  </xsl:attribute-set>


  <xsl:attribute-set name="__frontmatter__product__version">
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-size">18pt</xsl:attribute>
    <xsl:attribute name="font-family">Sans</xsl:attribute>
    <xsl:attribute name="margin-bottom">5pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__frontmatter__product__bookname">
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-size">10.5pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="margin-bottom">200pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__frontmatter__company__logo">
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__odd__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__footer__1">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__footer__2">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__footer__2_pagenumber">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__footer__3">
    <xsl:attribute name="color">blue</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">20pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__odd__footer__1">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__odd__footer__2">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__odd__footer__2_pagenumber">
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__odd__footer__3">
    <xsl:attribute name="color">blue</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">20pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__odd__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__even__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>

</xsl:stylesheet>