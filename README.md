package com.example.demo.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="sample1")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ric;
    private String qdb_ric;
    private String seqno;
    private String exDate;
    private String payDate;
    private String type;
    private String typeCode;
    private String amount;
    private String ccy;
    private String stk_Ccy;
    private String bl_Event;
    private String declareDate;
    private String recordDate;
    private String fiscalYeDate;
    private String source;
    private String qdb_Type_Code;
    private String pdpId;
    private String takara_Amnt;
    private String takara_CpNetAmnt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public String getQdb_ric() {
        return qdb_ric;
    }

    public void setQdb_ric(String qdb_ric) {
        this.qdb_ric = qdb_ric;
    }

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getStk_Ccy() {
        return stk_Ccy;
    }

    public void setStk_Ccy(String stk_Ccy) {
        this.stk_Ccy = stk_Ccy;
    }

    public String getBl_Event() {
        return bl_Event;
    }

    public void setBl_Event(String bl_Event) {
        this.bl_Event = bl_Event;
    }

    public String getDeclareDate() {
        return declareDate;
    }

    public void setDeclareDate(String declareDate) {
        this.declareDate = declareDate;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getFiscalYeDate() {
        return fiscalYeDate;
    }

    public void setFiscalYeDate(String fiscalYeDate) {
        this.fiscalYeDate = fiscalYeDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQdb_Type_Code() {
        return qdb_Type_Code;
    }

    public void setQdb_Type_Code(String qdb_Type_Code) {
        this.qdb_Type_Code = qdb_Type_Code;
    }

    public String getPdpId() {
        return pdpId;
    }

    public void setPdpId(String pdpId) {
        this.pdpId = pdpId;
    }

    public String getTakara_Amnt() {
        return takara_Amnt;
    }

    public void setTakara_Amnt(String takara_Amnt) {
        this.takara_Amnt = takara_Amnt;
    }

    public String getTakara_CpNetAmnt() {
        return takara_CpNetAmnt;
    }

    public void setTakara_CpNetAmnt(String takara_CpNetAmnt) {
        this.takara_CpNetAmnt = takara_CpNetAmnt;
    }

    public String getGlobalPrimaryEsmp() {
        return globalPrimaryEsmp;
    }

    public void setGlobalPrimaryEsmp(String globalPrimaryEsmp) {
        this.globalPrimaryEsmp = globalPrimaryEsmp;
    }

    public String getRegionalPrimaryEsmp() {
        return regionalPrimaryEsmp;
    }

    public void setRegionalPrimaryEsmp(String regionalPrimaryEsmp) {
        this.regionalPrimaryEsmp = regionalPrimaryEsmp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsAusSplit() {
        return isAusSplit;
    }

    public void setIsAusSplit(String isAusSplit) {
        this.isAusSplit = isAusSplit;
    }

    public String getNxs_Ccy() {
        return nxs_Ccy;
    }

    public void setNxs_Ccy(String nxs_Ccy) {
        this.nxs_Ccy = nxs_Ccy;
    }

    public String getNxs_DvdId() {
        return nxs_DvdId;
    }

    public void setNxs_DvdId(String nxs_DvdId) {
        this.nxs_DvdId = nxs_DvdId;
    }

    public String getNxs_DivType() {
        return nxs_DivType;
    }

    public void setNxs_DivType(String nxs_DivType) {
        this.nxs_DivType = nxs_DivType;
    }

    public String getTakaraUpdated() {
        return takaraUpdated;
    }

    public void setTakaraUpdated(String takaraUpdated) {
        this.takaraUpdated = takaraUpdated;
    }

    public String getGlobalPrimary() {
        return globalPrimary;
    }

    public void setGlobalPrimary(String globalPrimary) {
        this.globalPrimary = globalPrimary;
    }

    public String getEquityRegionalPrimaryListing() {
        return equityRegionalPrimaryListing;
    }

    public void setEquityRegionalPrimaryListing(String equityRegionalPrimaryListing) {
        this.equityRegionalPrimaryListing = equityRegionalPrimaryListing;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    private String globalPrimaryEsmp;
    private String regionalPrimaryEsmp;
    private String countryCode;
    private String isAusSplit;
    private String nxs_Ccy;
    private String nxs_DvdId;
    private String nxs_DivType;
    private String takaraUpdated;
    private String globalPrimary;
    private String equityRegionalPrimaryListing;
    private String listingType;
}

