package com.xinliangjishipin.pushwms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author gengbeijun
 */
public class PurchaseOrder {
    private String pkOrder;
    private String billCode;
    private String warehouseCode;
    private String ownerId;
    private String pkSupplierName;
    private String pkSupplierCode;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dMakeDate;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String pkOrder, String billCode, String pkSupplierName, String pkSupplierCode, String ownerId, Date dMakeDate) {
        this.pkOrder = pkOrder;
        this.billCode = billCode;
        this.pkSupplierName = pkSupplierName;
        this.pkSupplierCode = pkSupplierCode;
        this.ownerId = ownerId;
        this.dMakeDate = dMakeDate;
    }



    public String getPkOrder() {
        return pkOrder;
    }

    public void setPkOrder(String pkOrder) {
        this.pkOrder = pkOrder;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPkSupplierName() {
        return pkSupplierName;
    }

    public void setPkSupplierName(String pkSupplierName) {
        this.pkSupplierName = pkSupplierName;
    }

    public String getPkSupplierCode() {
        return pkSupplierCode;
    }

    public void setPkSupplierCode(String pkSupplierCode) {
        this.pkSupplierCode = pkSupplierCode;
    }

    public Date getdMakeDate() {
        return dMakeDate;
    }

    public void setdMakeDate(Date dMakeDate) {
        this.dMakeDate = dMakeDate;
    }

}
