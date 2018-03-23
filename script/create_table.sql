DROP TABLE "CS_ORDER_EXCHANGE_INFO";
CREATE TABLE "CS_ORDER_EXCHANGE_INFO" (
  "out_order_id" CHAR(20) NOT NULL,
  "order_type" CHAR(1) DEFAULT '' NOT NULL,
  "push_count" NUMBER(2) DEFAULT 0 NOT NULL,
  "push_status" CHAR(1) DEFAULT 0 NOT NULL,
  "push_channel" VARCHAR2(10),
  "created_user" VARCHAR2(20) DEFAULT '',
  "created_time" DATE,
  "updated_user" VARCHAR2(20) DEFAULT '',
  "updated_time" DATE,
  "request_content" VARCHAR2(4000) DEFAULT '',
  "response_time" DATE,
  "response_content" VARCHAR2(4000) DEFAULT ''
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;


COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."out_order_id" is '请求单号';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."order_type" is '请求类型 1-采购入库单；2-销售出库单';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_count" is '推送次数';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_status" is '推送状态 0-推送失败，1-推送成功';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_channel" is '推送wms简称';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."created_user" is '推送人';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."created_time" is '推送创建时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."updated_user" is '修改人';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."updated_time" IS '最后修改时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."request_content" IS '请求信息';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."response_time" IS '响应时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."response_content" IS '响应信息';
COMMIT;


DROP TABLE "CS_WAREHOUSE_CONFIG";
CREATE TABLE "CS_WAREHOUSE_CONFIG" (
  "wms_name" VARCHAR2(50) NOT NULL,
  "wms_short" VARCHAR2(20) NOT NULL,
  "wms_warehouse_code" VARCHAR2(10) NOT NULL,
  "nc_warehouse_code" VARCHAR2(20) DEFAULT 0 NOT NULL,
  "nc_warehouse_name" VARCHAR2(30),
  "flag" CHAR (1) DEFAULT '0' NOT NULL
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;


COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_name" is '外部wms系统名称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_short" is '外部wms系统简称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_warehouse_code" is '外部wms系统仓库编码';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."nc_warehouse_code" is 'nc系统仓库编码';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."nc_warehouse_name" is 'nc系统仓库名称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."flag" is '是否启用';


INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","flag")
VALUES('唯捷wms系统','WJ','WJ001','WH001','北京仓库','1');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","flag")
VALUES('唯捷wms系统','WJ','WJ001','WH002','北京虚拟仓库','1');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","flag")
VALUES('唯捷wms系统','WJ','WJ003','WH003','上海仓库','1');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","flag")
VALUES('马上配wms系统','MSP','SPC','WH00500','山普仓','1');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","flag")
VALUES('马上配wms系统','MSP','JDC','WH004','嘉定仓','1');
COMMIT;
