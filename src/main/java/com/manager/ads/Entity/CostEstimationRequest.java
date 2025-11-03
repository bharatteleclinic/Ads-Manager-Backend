package com.manager.ads.Entity;

import java.util.List;

import lombok.Data;

@Data
public class CostEstimationRequest {
    private String adType;
    private double basePrice;
    private String brandCategory;
    private String brandName;
    private String campaignDescription;
    private String campaignTitle;
    private String campaignType;
    private Creatives creatives;
    private String startDate;
    private String endDate;
    private double gst;
    private double subTotal;
    private double totalPrice;
    private String userId;
    private List<DeviceInfo> selectedDevices;

    @Data
    public static class Creatives {
        private String fullScreenVideo;
        private String fullScreenImage;
        private String bannerImage;
    }

    @Data
    public static class DeviceInfo {
        private String city;
        private String state;
        private String pin;
        private double lat;
        private double lng;
    }
}