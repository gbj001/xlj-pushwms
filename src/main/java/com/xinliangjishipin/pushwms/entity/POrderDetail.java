package com.xinliangjishipin.pushwms.entity;

import java.io.Serializable;

public class POrderDetail implements Serializable{

    private String isGift;
    private String warehouseCode;
    private String materialCode;
    private String materialName;
    private Double quantity;
    private String measureUnits;
    private String pkOrderHeader;
    private String pkOrderBody;
    private String billCode;


    public POrderDetail(){

    }

    public POrderDetail(String isGift, String warehouseCode, String materialCode, String materialName, Double quantity, String measureUnits, String pkOrderHeader, String pkOrderBody, String billCode) {
        this.isGift = isGift;
        this.warehouseCode = warehouseCode;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.quantity = quantity;
        this.measureUnits = measureUnits;
        this.pkOrderHeader = pkOrderHeader;
        this.pkOrderBody = pkOrderBody;
        this.billCode = billCode;
    }

    @Override
    public String toString() {
        return "POrderDetail{" +
                "isGift='" + isGift + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", materialCode='" + materialCode + '\'' +
                ", materialName='" + materialName + '\'' +
                ", quantity=" + quantity +
                ", measureUnits='" + measureUnits + '\'' +
                ", pkOrderHeader='" + pkOrderHeader + '\'' +
                ", pkOrderBody='" + pkOrderBody + '\'' +
                ", billCode='" + billCode + '\'' +
                '}';
    }

    public String getIsGift() {
        return isGift;
    }

    public void setIsGift(String isGift) {
        this.isGift = isGift;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMeasureUnits() {
        return measureUnits;
    }

    public void setMeasureUnits(String measureUnits) {
        this.measureUnits = measureUnits;
    }

    public String getPkOrderHeader() {
        return pkOrderHeader;
    }

    public void setPkOrderHeader(String pkOrderHeader) {
        this.pkOrderHeader = pkOrderHeader;
    }

    public String getPkOrderBody() {
        return pkOrderBody;
    }

    public void setPkOrderBody(String pkOrderBody) {
        this.pkOrderBody = pkOrderBody;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }
}
