package com.xinliangjishipin.pushwms.entity;

import java.util.List;

public class PushPoOrder {
    private String pkOrder;
    private String billCode;
    private String warehouseCode;
    private String ownerId;
    private String pkSupplierName;
    private String pkSupplierCode;
    private List<POrderDetail> items;

    public PushPoOrder(){

    }

    public PushPoOrder(POrder pOrder){
        this.pkOrder = pOrder.getPkOrder();
        this.billCode = pOrder.getBillCode();
        this.ownerId = pOrder.getOwnerId();
        this.pkSupplierName = pOrder.getPkSupplierName();
        this.pkSupplierCode = pOrder.getPkSupplierCode();
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

    public List<POrderDetail> getItems() {
        return items;
    }

    public void setItems(List<POrderDetail> items) {
        this.items = items;
    }
}
