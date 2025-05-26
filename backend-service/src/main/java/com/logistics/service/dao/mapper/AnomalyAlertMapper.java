package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.AnomalyAlert;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AnomalyAlertMapper {

    /**
     * 插入异常告警
     */
    @Insert("INSERT INTO anomaly_alerts (anomaly_type, city, order_id, courier_id, anomaly_severity, " +
            "anomaly_value, threshold_value, description, original_time, analysis_date, analysis_hour, is_resolved) " +
            "VALUES (#{anomalyType}, #{city}, #{orderId}, #{courierId}, #{anomalySeverity}, " +
            "#{anomalyValue}, #{thresholdValue}, #{description}, #{originalTime}, #{analysisDate}, #{analysisHour}, #{isResolved})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAlert(AnomalyAlert alert);

    /**
     * 根据城市查找未解决的告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE city = #{city} AND (is_resolved = false OR is_resolved IS NULL) " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findUnresolvedByCity(@Param("city") String city);

    /**
     * 根据严重程度查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE anomaly_severity = #{severity} " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findBySeverity(@Param("severity") String severity);

    /**
     * 根据异常类型查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE anomaly_type = #{anomalyType} " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findByAnomalyType(@Param("anomalyType") String anomalyType);

    /**
     * 根据城市和日期范围查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findByCityAndDateRange(@Param("city") String city,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    /**
     * 根据配送员和日期范围查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE courier_id = #{courierId} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findByCourierAndDateRange(@Param("courierId") String courierId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 根据城市查找高风险告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE city = #{city} AND anomaly_severity = 'HIGH' " +
            "AND (is_resolved = false OR is_resolved IS NULL) " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findHighRiskAlertsByCity(@Param("city") String city);

    /**
     * 解决告警
     */
    @Update("UPDATE anomaly_alerts SET is_resolved = true, resolved_at = #{resolvedAt} WHERE id = #{id}")
    int resolveAlert(@Param("id") Long id, @Param("resolvedAt") LocalDateTime resolvedAt);

    /**
     * 批量解决告警
     */
    @Update("UPDATE anomaly_alerts SET is_resolved = true, resolved_at = #{resolvedAt} WHERE id IN " +
            "<foreach collection='alertIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>")
    int resolveAlertsBatch(@Param("alertIds") List<Long> alertIds, @Param("resolvedAt") LocalDateTime resolvedAt);

    /**
     * 根据ID查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE id = #{id}")
    AnomalyAlert findById(@Param("id") Long id);

    /**
     * 根据订单ID查找告警
     */
    @Select("SELECT * FROM anomaly_alerts WHERE order_id = #{orderId} " +
            "ORDER BY created_at DESC")
    List<AnomalyAlert> findByOrderId(@Param("orderId") String orderId);

    /**
     * 获取今日告警统计
     */
    @Select("SELECT anomaly_type, anomaly_severity, COUNT(*) as count " +
            "FROM anomaly_alerts " +
            "WHERE analysis_date = #{date} " +
            "GROUP BY anomaly_type, anomaly_severity " +
            "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getTodayAlertStats(@Param("date") LocalDate date);

    /**
     * 获取指定天数内的告警统计
     */
    @Select("SELECT anomaly_type, anomaly_severity, COUNT(*) as count " +
            "FROM anomaly_alerts " +
            "WHERE analysis_date >= #{startDate} " +
            "GROUP BY anomaly_type, anomaly_severity " +
            "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getAlertStatsByDays(@Param("startDate") LocalDate startDate);

    /**
     * 获取城市告警趋势
     */
    @Select("SELECT analysis_date, anomaly_severity, COUNT(*) as count " +
            "FROM anomaly_alerts " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "GROUP BY analysis_date, anomaly_severity " +
            "ORDER BY analysis_date DESC")
    List<java.util.Map<String, Object>> getCityAlertTrend(@Param("city") String city,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 获取小时级告警分布
     */
    @Select("SELECT analysis_hour, anomaly_type, COUNT(*) as count " +
            "FROM anomaly_alerts " +
            "WHERE city = #{city} AND analysis_date = #{date} " +
            "GROUP BY analysis_hour, anomaly_type " +
            "ORDER BY analysis_hour")
    List<java.util.Map<String, Object>> getHourlyAlertDistribution(@Param("city") String city,
                                                                   @Param("date") LocalDate date);

    /**
     * 获取配送员告警排行
     */
    @Select("SELECT courier_id, COUNT(*) as alert_count, " +
            "SUM(CASE WHEN anomaly_severity = 'HIGH' THEN 1 ELSE 0 END) as high_risk_count " +
            "FROM anomaly_alerts " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY courier_id " +
            "ORDER BY alert_count DESC " +
            "LIMIT #{limit}")
    List<java.util.Map<String, Object>> getCourierAlertRanking(@Param("city") String city,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("limit") int limit);

    /**
     * 更新告警描述
     */
    @Update("UPDATE anomaly_alerts SET description = #{description} WHERE id = #{id}")
    int updateAlertDescription(@Param("id") Long id, @Param("description") String description);

    /**
     * 更新告警严重程度
     */
    @Update("UPDATE anomaly_alerts SET anomaly_severity = #{severity} WHERE id = #{id}")
    int updateAlertSeverity(@Param("id") Long id, @Param("severity") String severity);

    /**
     * 删除旧告警（清理数据）
     */
    @Delete("DELETE FROM anomaly_alerts WHERE created_at < #{cutoffDate}")
    int cleanupOldAlerts(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 统计未解决告警数量
     */
    @Select("SELECT COUNT(*) FROM anomaly_alerts WHERE (is_resolved = false OR is_resolved IS NULL)")
    int countUnresolvedAlerts();

    /**
     * 统计指定城市未解决告警数量
     */
    @Select("SELECT COUNT(*) FROM anomaly_alerts WHERE city = #{city} AND (is_resolved = false OR is_resolved IS NULL)")
    int countUnresolvedAlertsByCity(@Param("city") String city);

    /**
     * 统计指定时间范围内的告警数量
     */
    @Select("SELECT COUNT(*) FROM anomaly_alerts WHERE analysis_date >= #{startDate} AND analysis_date <= #{endDate}")
    int countAlertsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 查找最近的告警
     */
    @Select("SELECT * FROM anomaly_alerts ORDER BY created_at DESC LIMIT #{limit}")
    List<AnomalyAlert> findRecentAlerts(@Param("limit") int limit);

    /**
     * 查找异常值超过阈值最多的告警
     */
    @Select("SELECT * FROM anomaly_alerts " +
            "WHERE anomaly_value > threshold_value " +
            "ORDER BY (anomaly_value - threshold_value) DESC " +
            "LIMIT #{limit}")
    List<AnomalyAlert> findHighestDeviationAlerts(@Param("limit") int limit);
}