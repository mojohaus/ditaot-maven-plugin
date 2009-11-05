<?xml version='1.0'?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
  version="1.1">

  <xsl:template name="insertBodyFirstFooter">

    <fo:static-content flow-name="first-body-footer">
      <fo:block xsl:use-attribute-sets="__body__odd__footer__1">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Product Name'" />
        </xsl:call-template>
      </fo:block>

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
                <xsl:call-template name="insertVariable">
                  <xsl:with-param name="theVariableID" select="'Book ID'" />
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>

      <fo:block xsl:use-attribute-sets="__body__odd__footer__3">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Site URL'" />
        </xsl:call-template>
      </fo:block>

    </fo:static-content>

  </xsl:template>

  <xsl:template name="insertBodyOddFooter">

    <fo:static-content flow-name="odd-body-footer">
      <fo:block xsl:use-attribute-sets="__body__odd__footer__1">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Product Name'" />
        </xsl:call-template>
      </fo:block>

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
                <xsl:call-template name="insertVariable">
                  <xsl:with-param name="theVariableID" select="'Book ID'" />
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>

      <fo:block xsl:use-attribute-sets="__body__odd__footer__3">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Site URL'" />
        </xsl:call-template>
      </fo:block>

    </fo:static-content>

  </xsl:template>

  <xsl:template name="insertBodyEvenFooter">

    <fo:static-content flow-name="even-body-footer">
      <fo:block xsl:use-attribute-sets="__body__even__footer__1">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Product Name'" />
        </xsl:call-template>
      </fo:block>

      <fo:table>
        <fo:table-column column-number="1" />
        <fo:table-column column-number="2" />
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
              <fo:block xsl:use-attribute-sets="__body__even__footer__2">
                <xsl:call-template name="insertVariable">
                  <xsl:with-param name="theVariableID" select="'Book ID'" />
                </xsl:call-template>
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
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'Site URL'" />
        </xsl:call-template>
      </fo:block>

    </fo:static-content>

  </xsl:template>

  <xsl:template match="*" mode="createMiniToc">
  </xsl:template>
  
  <xsl:attribute-set name="__body__odd__header">
    <xsl:attribute name="margin-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__body__even__header">
    <xsl:attribute name="margin-top">20pt</xsl:attribute>
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
    <xsl:attribute name="margin-bottom">20pt</xsl:attribute> <!--  dont seem to work -->
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
    <xsl:attribute name="margin-bottom">20pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__odd__header">
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__even__header">
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__odd__footer">
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="__toc__even__footer">
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
  </xsl:attribute-set>



</xsl:stylesheet>