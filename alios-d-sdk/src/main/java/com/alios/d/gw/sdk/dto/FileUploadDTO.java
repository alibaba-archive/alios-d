package com.alios.d.gw.sdk.dto;

import com.alibaba.fastjson.JSONObject;

/**
 * @author auto create
 */
public class FileUploadDTO {
    private String tenantId;
    private Integer eventId;
    private String dataId;
    private String itemId;
    private Long clientTimestamp;
    private Long serverTimestamp;
    private Integer reissueCount;
    private JSONObject data;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getClientTimestamp() {
        return clientTimestamp;
    }

    public void setClientTimestamp(Long clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }

    public Long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public Integer getReissueCount() {
        return reissueCount;
    }

    public void setReissueCount(Integer reissueCount) {
        this.reissueCount = reissueCount;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
