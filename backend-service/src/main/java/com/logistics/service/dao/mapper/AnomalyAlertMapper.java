package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.AnomalyAlert;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AnomalyAlertMapper {

    @Insert("INSERT INTO anomaly_alerts (alert_type, city, order_id, courier_id, severity, " +
            "description, anomaly_value, threshold_value, is_resolved) " +
            "VALUES (#{alertType}, #{city}, #{orderId}, #{courierId}, #{severity}, " +
            "#{description}, #{anomalyValue}, #{thresholdValue}, #{isResolved})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAlert(AnomalyAlert alert);

    @Select("SELECT * FROM anomaly_alerts WHERE city = #{city} AND is_resolved = false " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<AnomalyAlert> findUnresolvedByCity(@Param("city") String city, @Param("limit") int limit);

    @Select("SELECT * FROM anomaly_alerts WHERE is_resolved = false " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<AnomalyAlert> findUnresolved(@Param("limit") int limit);

    @Update("UPDATE anomaly_alerts SET is_resolved = true, resolved_at = NOW() WHERE id = #{id}")
    int resolveAlert(@Param("id") Long id);

    @Select("SELECT alert_type, COUNT(*) as count FROM anomaly_alerts " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY alert_type")
    List<java.util.Map<String, Object>> getAlertStats(@Param("days") int days);

    @Delete("DELETE FROM anomaly_alerts WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int cleanupOldAlerts();
}