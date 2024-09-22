/* General HTML and Body styles */
html, body {
  min-height: 100%;
  min-width: 1230px;
  width: 100%;
  height: 100%;
}

/* Header */
#header {
  height: 62px;
  border: none;
  width: 100%;
}

#masterHeader {
  padding: 5px;
  background-color: #F3F3F3;
}

/* Banner Module */
.bannerModule {
  background: url(../../images/bkg-header-top.gif) repeat-x;
}

.bannerModule .top {
  font-size: 1px;
  background: #f3f3f3 url(../../images/bkg-header-top-corners.gif) no-repeat;
  width: 7px;
  height: 7px;
}

.bannerModule .content {
  background: #FFF url(../../images/bkg-header.gif) repeat-y;
  height: 40px;
  width: 100%;
}

.bannerModule .right {
  background-position: -12px 0px;
  font-size: 1px;
  float: right;
}

.bannerModule .base {
  background: url(../../images/bkg-header-base.gif);
  font-size: 1px;
  width: 100%;
}

.bannerModule .base .corners {
  background: #F3F3F3 url(../../images/bkg-header-base-corners.gif) no-repeat;
  width: 10px;
  height: 7px;
}

.bannerModule .base .right {
  background-position: -11px 0px;
  float: right;
}

/* Logo */
#logo {
  position: absolute;
  left: 13px;
  top: 23px;
}

/* Main section */
main {
  padding: 5px;
}

.holder {
  position: absolute;
  top: 0;
  left: 0;
}

.content-top {
  margin: 0 5px;
}

/* Banner Content */
#bannerContentSmall {
  background: transparent url(../images/bkg-content-header.gif) no-repeat right top;
  height: 7px;
}

#bannerContentSmall .header {
  background: transparent url(../images/bkg-content-left.gif) repeat-y scroll;
  height: 7px;
}

#bannerContentSmall .headerLeft {
  background: transparent url(../images/bkg-content-header-left.gif) no-repeat;
  height: 7px;
  font-size: 8px;
}

.content-bottom {
  background: #FFF url(../images/bkg-header-right.gif) right repeat-y;
  margin-right: 8px;
  padding-bottom: 0;
  height: auto;
}

/* Top-most div */
#top-most-div {
  background: transparent url(../images/bkg-content-left.gif) repeat-y scroll 0% 0%;
  margin-right: 5px;
  height: auto;
  text-indent: 6px;
}

/* Footer */
div.footer {
  padding-left: 0;
  background: transparent url(../images/bkg-content-base.gif) no-repeat scroll 0% 0%;
  height: 7px;
  position: relative;
}

div.footer img {
  bottom: 0;
  position: absolute;
  right: 0;
}

.footer {
  position: absolute;
  bottom: 0;
  left: 0;
  font-size: 90%;
  z-index: 10;
  padding: 10px 0;
}

/* Breadcrumb */
.breadcrumb {
  padding: 5px 0;
  position: inherit;
  border-bottom: dotted 1px #CCC;
  border-top: dotted 1px #CCC;
  z-index: 1;
  height: 100%;
  overflow: auto;
}

.breadcrumbleftInside {
  float: left;
  font-size: 135%;
  vertical-align: middle;
}

/* Information divs */
.info-div-left,
.info-div-right {
  width: 450px;
}

.highlight {
  padding: 5px;
  border-bottom: dotted 1px #CCC;
  border-top: dotted 1px #CCC;
  background: #F3F3F3;
  margin: 0 4px 0 6px;
}

.div-error-panel {
  padding: 10px 0 20px 0;
  height: 15px;
  font-size: 80%;
  color: red;
}

.div-save-button {
  padding: 5px 0 10px;
}

.submit-button {
  height: 40%;
}

.highlight-search-button {
  height: 40%;
  width: 90px;
}

.div-middle-content {
  margin: 5px;
  padding-bottom: 50px;
  height: auto;
}

.masterContent {
  position: inherit;
  z-index: 2;
}

/* Tables */
.table,
.table-xml,
.table-xml-inner {
  width: 98%;
  margin: 0;
  border: 1.4px solid #CCC;
  font-size: 9pt;
  text-align: left;
  border-spacing: 0;
  table-layout: fixed;
}

.table TH,
.table-xml TH {
  background-color: #aaa;
  color: #222;
  height: 14px;
  padding: 5px;
  font-weight: normal;
}

.table TR.alt {
  background: #F3F3F3;
}

.table TD,
.table-xml-inner TD,
.table-xml-inner-text TD {
  padding: 5px;
  border: 1px solid #dddddd;
}

/* Inputs */
.input,
.input-xml,
.input-new-xml {
  width: 100%;
  min-height: 400px;
  vertical-align: top;
  text-align: left;
  resize: vertical;
  outline: none;
}

.input-xml:focus,
.input-new-xml:focus {
  border: 1px solid #ddd;
  box-shadow: 0 0 10px rgb(252, 244, 244);
}

.highlight-input {
  margin: 0 40px 0 0;
  padding: 0;
  border-radius: 3px;
  font-size: 12px;
}

.highlight-location-dropdown,
.input-dropdown {
  font-size: 12px;
  width: 51%;
  min-height: 15.5pt;
}

.stream-dropdown {
  width: 50.6%;
}

.div-collapse {
  margin: 10px 4px 0 4px;
  width: 75%;
  border: 1.4px solid #d3d3d3;
  padding-right: 5px;
}

/* Buttons */
.table-edit-button,
.save-button,
.save-property-button {
  margin: 10px 4px 0 4px;
  width: 60px;
}

.save-property-button {
  width: 130px;
}

/* Pagination */
.pagination {
  float: right;
  margin-right: 140px;
  width: 60px;
}

.pagination TD {
  padding-right: 25px;
  font-size: 15px;
}

.dialog {
  position: fixed;
  bottom: 1rem;
  right: 1rem;
  padding: 1rem;
  border-radius: 5px;
  background-color: rgb(68, 145, 173);
}

.entry-div {
  margin-left: 5px;
}

#main-div {
  margin: 0 25px;
  padding-left: 0;
  width: 100%;
}

a {
  color: blue;
}
