package com.manager.ads.Service;

import org.springframework.beans.factory.annotation.Autowired;
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

   
    public double getPriceForCampaign(String adsType, int deviceCount) {
        Product product = productRepository.findByName(adsType + " Ads")
                                        .orElseThrow(() -> new RuntimeException("Product not found"));
        return (product.getPrice() * deviceCount);
    }
}