package com.manager.ads.Repository;

import com.manager.ads.Entity.ConsultationDevice;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
