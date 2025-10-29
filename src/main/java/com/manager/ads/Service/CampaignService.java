package com.manager.ads.Service;


import org.springframework.stereotype.Service;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.Product;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Repository.ProductRepository;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final S3Service s3Service;

    public CampaignService(CampaignRepository campaignRepository, S3Service s3Service) {
        this.campaignRepository = campaignRepository;
        this.s3Service = s3Service;
    }

    public Campaign createCampaign(String title, String type, String description, String objective,
            String brandCategory, String adsType, MultipartFile adFile, User user, String startDate, String endDate, double totalPrice ,
            int totalDevice, List<Integer> selectedDevices , boolean draft) throws IOException {
        // 1️⃣ Upload file to S3
        String fileUrl = s3Service.uploadFile(adFile);

        // 2️⃣ Create and save campaign
        Campaign campaign = new Campaign();
        campaign.setTitle(title);
        campaign.setType(type);
        campaign.setDescription(description);
        campaign.setObjective(objective);
        campaign.setStartDate(LocalDate.parse(startDate));
        campaign.setEndDate(LocalDate.parse(endDate));
        campaign.setDeviceCount(totalDevice);
        campaign.setTotalPrice(totalPrice);
        campaign.setBrandCategory(brandCategory);
        campaign.setAdsType(adsType);
        campaign.setAdUrl(fileUrl);
        campaign.setUser(user);
        campaign.setDraft(draft);

        return campaignRepository.save(campaign);
    }

}