package com.manager.ads.Service;



import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.ConsultationDevice;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Repository.ConsultationDeviceRepository;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ConsultationDeviceRepository consultationDeviceRepository;
    private final S3Service s3Service;

    public CampaignService(CampaignRepository campaignRepository, S3Service s3Service , ConsultationDeviceRepository consultationDeviceRepository) {
        this.campaignRepository = campaignRepository;
        this.consultationDeviceRepository = consultationDeviceRepository;
        this.s3Service = s3Service;
    }

    public Campaign createCampaign(
            String title,
            String type,
            String description,
            String brandName,
            String brandCategory,
            String adsType,
            MultipartFile adFile,
            User user,
            String startDate,
            String endDate,
            double totalPrice,
            int totalDevice,
            List<Long> selectedDeviceIds,
            boolean draft
    ) throws IOException {

        // 1️⃣ Upload file to S3
        String fileUrl = s3Service.uploadFile(adFile);
        

        // 2️⃣ Fetch device entities using IDs
        List<ConsultationDevice> selectedDevices = consultationDeviceRepository.findAllById(selectedDeviceIds);

        // 3️⃣ Create and save campaign
        Campaign campaign = new Campaign();
        campaign.setTitle(title);
        campaign.setType(type);
        campaign.setDescription(description);
        campaign.setBrandName(brandName);
        campaign.setStartDate(LocalDate.parse(startDate));
        campaign.setEndDate(LocalDate.parse(endDate));
        campaign.setDeviceCount(totalDevice);
        campaign.setTotalPrice(totalPrice);
        campaign.setBrandCategory(brandCategory);
        campaign.setAdsType(adsType);
        campaign.setAdUrl(fileUrl);
        campaign.setUser(user);
        campaign.setDraft(draft);

        // ✅ This line is crucial — link devices to campaign
        campaign.setSelectedDevices(selectedDevices);

        // 4️⃣ Save and return campaign
        return campaignRepository.save(campaign);
    }
}