package com.xinliangjishipin.pushwms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gengbeijun
 */
public class SOrderDetail implements Serializable{

    private String PkOrder;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dbilldate;
    private String warehouseCode;

    public String getPkOrder() {
        return PkOrder;
    }

    public void setPkOrder(String pkOrder) {
        PkOrder = pkOrder;
    }

    public Date getDbilldate() {
        return dbilldate;
    }

    public void setDbilldate(Date dbilldate) {
        this.dbilldate = dbilldate;
    }
}
