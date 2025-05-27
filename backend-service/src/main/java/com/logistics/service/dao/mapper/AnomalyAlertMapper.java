package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.AnomalyAlert;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnomalyAlertMapper {

    // ==================== 基础CRUD操作 ====================

    /**
     * 插入异常告警
     */
    int insertAlert(AnomalyAlert alert);

    /**
     * 根据ID查找告警
     */
    AnomalyAlert findById(@Param("id") Long id);

    /**
     * 解决告警
     */
    int resolveAlert(@Param("id") Long id, @Param("resolvedAt") LocalDateTime resolvedAt);

    /**
     * 批量解决告警
     */
    int resolveAlertsBatch(@Param("alertIds") List<Long> alertIds, @Param("resolvedAt") LocalDateTime resolvedAt);

    /**
     * 更新告警描述
     */
    int updateAlertDescription(@Param("id") Long id, @Param("description") String description);

    /**
     * 更新告警严重程度
     */
    int updateAlertSeverity(@Param("id") Long id, @Param("severity") String severity);

    /**
     * 删除旧告警（清理数据）
     */
    int cleanupOldAlerts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // ==================== 查询操作 ====================

    /**
     * 动态查询告警 - 支持多种条件组合
     */
    List<AnomalyAlert> findAlerts(@Param("city") String city,
                                  @Param("anomalyType") String anomalyType,
                                  @Param("severity") String severity,
                                  @Param("courierId") String courierId,
                                  @Param("orderId") String orderId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("isResolved") Boolean isResolved,
                                  @Param("limit") Integer limit);

    /**
     * 查找最近的告警
     */
    List<AnomalyAlert> findRecentAlerts(@Param("limit") int limit);

    /**
     * 查找异常值超过阈值最多的告警
     */
    List<AnomalyAlert> findHighestDeviationAlerts(@Param("limit") int limit);

    // ==================== 统计操作 ====================

    /**
     * 动态统计告警数量
     */
    int countAlerts(@Param("city") String city,
                    @Param("anomalyType") String anomalyType,
                    @Param("severity") String severity,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate,
                    @Param("isResolved") Boolean isResolved);

    /**
     * 获取告警统计分析
     */
    List<Map<String, Object>> getAlertStats(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("groupBy") String groupBy);

    /**
     * 获取城市告警趋势
     */
    List<Map<String, Object>> getCityAlertTrend(@Param("city") String city,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * 获取小时级告警分布
     */
    List<Map<String, Object>> getHourlyAlertDistribution(@Param("city") String city,
                                                         @Param("date") LocalDate date);

    /**
     * 获取配送员告警排行
     */
    List<Map<String, Object>> getCourierAlertRanking(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("limit") int limit);
}