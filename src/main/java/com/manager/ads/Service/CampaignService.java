package com.manager.ads.Service;


import org.springframework.stereotype.Service;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.Product;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Repository.ProductRepository;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final S3Service s3Service;
     private ProductRepository productRepository;

    public CampaignService(CampaignRepository campaignRepository, S3Service s3Service, ProductRepository productRepository) {
        this.campaignRepository = campaignRepository;
        this.s3Service = s3Service;
        this.productRepository = productRepository;
    }

    public Campaign createCampaign(String title,
                                   String type,
                                   String description,
                                   String objective,
                                   String brandCategory,
                                   String adsType,
                                   MultipartFile adFile,
                                   User user) throws IOException {
        // 1️⃣ Upload file to S3
        String fileUrl = s3Service.uploadFile(adFile);

        // 2️⃣ Create and save campaign
        Campaign campaign = new Campaign();
        campaign.setTitle(title);
        campaign.setType(type);
        campaign.setDescription(description);
        campaign.setObjective(objective);
        campaign.setBrandCategory(brandCategory);
        campaign.setAdsType(adsType);
        campaign.setAdUrl(fileUrl);
        campaign.setUser(user);

        return campaignRepository.save(campaign);
    }

    public Campaign updateCampaign(Long id,
                               String title,
                               String type,
                               String description,
                               String objective,
                               String brandCategory,
                               String adsType,
                               MultipartFile adFile,
                               User user) throws IOException {

    // ✅ Fetch existing campaign
    Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));

    // ✅ Upload new file if provided
    if (adFile != null && !adFile.isEmpty()) {
        String fileUrl = s3Service.uploadFile(adFile);
        campaign.setAdUrl(fileUrl);
    }

    // ✅ Update only non-null fields
    if (title != null) campaign.setTitle(title);
    if (type != null) campaign.setType(type);
    if (description != null) campaign.setDescription(description);
    if (objective != null) campaign.setObjective(objective);
    if (brandCategory != null) campaign.setBrandCategory(brandCategory);
    if (adsType != null) campaign.setAdsType(adsType);
    if (user != null) campaign.setUser(user);

    // ✅ Save and return updated campaign
    return campaignRepository.save(campaign);
}

   
    public double getPriceForCampaign(String adsType, int deviceCount) {
        Product product = productRepository.findByName(adsType + " Ads")
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getPrice() * deviceCount * 1.18;
    }
}