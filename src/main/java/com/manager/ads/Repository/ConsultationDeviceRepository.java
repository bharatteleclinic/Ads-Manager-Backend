package com.manager.ads.Repository;

import com.manager.ads.Entity.ConsultationDevice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationDeviceRepository extends JpaRepository<ConsultationDevice, Long> {
}
