package com.manager.ads.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manager.ads.Entity.ConsultationDevice;

@Repository
public interface ConsultationDeviceRepository extends JpaRepository<ConsultationDevice, Long> {
    @Query(value = """
        SELECT 
            id,
            device_serial AS deviceSerial,
            model_number AS modelNumber,
            enterprise_id AS enterpriseId,
            ST_Y(location::geometry) AS latitude,
            ST_X(location::geometry) AS longitude
        FROM consultation_devices
        """, nativeQuery = true)
    List<Map<String, Object>> findAllWithLatLong();
}