package com.alios.d.gw.sdk.dto;

import com.alibaba.fastjson.JSONObject;

/**
 * @author auto create
 */
public class FileUploadDTO {
    /**
     * 在知点中定义的事件id
     */
    private Integer eventId;
    /**
     * 流水号ID，用以标识唯一
     */
    private String dataId;
    /**
     * 数据所属主体ID，例如车的vin号，手机的imei号等
     */
    private String itemId;
    /**
     * 客户端上报的时间
     */
    private Long clientTimestamp;
    /**
     * 服务端上报的时间
     */
    private Long serverTimestamp;
    /**
     * 数据上传的重试次数
     */
    private Integer reissueCount;
    /**
     * 自定义的上报的字段
     */
    private JSONObject data;

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
