package com.xinliangjishipin.pushwms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class PushPoOrder {
    private String pkOrder;
    private String billCode;
    private String warehouseCode;
    private String ownerId;
    private String pkSupplierName;
    private String pkSupplierCode;
    private List<PurchaseOrderDetail> items;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dMakeDate;

    public PushPoOrder(){

    }

    public PushPoOrder(PurchaseOrder purchaseOrder){
        this.pkOrder = purchaseOrder.getPkOrder();
        this.billCode = purchaseOrder.getBillCode();
        this.ownerId = purchaseOrder.getOwnerId();
        this.pkSupplierName = purchaseOrder.getPkSupplierName();
        this.pkSupplierCode = purchaseOrder.getPkSupplierCode();
        this.dMakeDate = purchaseOrder.getdMakeDate();
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

    public List<PurchaseOrderDetail> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderDetail> items) {
        this.items = items;
    }

    public Date getdMakeDate() {
        return dMakeDate;
    }

    public void setdMakeDate(Date dMakeDate) {
        this.dMakeDate = dMakeDate;
    }
}
