package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OperationalEfficiencyMetricsMapper {

    /**
     * 插入运营效率数据
     */
    int insertEfficiencyMetrics(OperationalEfficiencyMetrics metrics);

    /**
     * 批量插入运营效率数据
     */
    int batchInsertEfficiencyMetrics(@Param("list") List<OperationalEfficiencyMetrics> metricsList);

    /**
     * 根据城市和日期范围查找运营效率
     */
    List<OperationalEfficiencyMetrics> findByCityAndDateRange(@Param("city") String city,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);

    /**
     * 根据配送员和日期范围查找运营效率
     */
    List<OperationalEfficiencyMetrics> findByCourierAndDateRange(@Param("courierId") Integer courierId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    /**
     * 根据区域和日期范围查找运营效率
     */
    List<OperationalEfficiencyMetrics> findByRegionAndDateRange(@Param("regionId") Integer regionId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    /**
     * 按城市和日期查找运营效率数据
     */
    List<OperationalEfficiencyMetrics> findByCityAndDate(@Param("city") String city,
                                                         @Param("date") LocalDate date);

    /**
     * 多条件查询运营效率
     */
    List<OperationalEfficiencyMetrics> findByConditions(@Param("city") String city,
                                                        @Param("regionId") Integer regionId,
                                                        @Param("courierId") Integer courierId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 获取城市效率趋势
     */
    List<Map<String, Object>> getCityEfficiencyTrend(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate);

    /**
     * 获取配送员效率排行
     */
    List<Map<String, Object>> getCourierEfficiencyRanking(@Param("city") String city,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("limit") int limit);

    /**
     * 获取区域效率排行
     */
    List<Map<String, Object>> getRegionEfficiencyRanking(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("limit") int limit);

    /**
     * 获取效率分布统计
     */
    List<Map<String, Object>> getEfficiencyDistribution(@Param("city") String city,
                                                        @Param("startDate") LocalDate startDate);

    /**
     * 获取低效率警告
     */
    List<OperationalEfficiencyMetrics> findLowEfficiencyAlerts(@Param("threshold") double threshold,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("limit") int limit);

    /**
     * 获取高效率表现
     */
    List<OperationalEfficiencyMetrics> findHighEfficiencyPerformance(@Param("threshold") double threshold,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("limit") int limit);

    /**
     * 获取运营效率汇总统计
     */
    Map<String, Object> getEfficiencySummary(@Param("city") String city,
                                             @Param("startDate") LocalDate startDate);

    /**
     * 获取最新运营效率数据
     */
    OperationalEfficiencyMetrics findLatestByCity(@Param("city") String city);

    /**
     * 获取配送员最新效率数据
     */
    OperationalEfficiencyMetrics findLatestByCourier(@Param("courierId") Integer courierId);

    /**
     * 更新运营效率数据
     */
    int updateEfficiencyMetrics(OperationalEfficiencyMetrics metrics);

    /**
     * 删除旧的运营效率数据
     */
    int cleanupOldMetrics(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计运营效率记录数
     */
    int countByCity(@Param("city") String city);

    /**
     * 统计配送员记录数
     */
    int countByCourier(@Param("courierId") Integer courierId);

    /**
     * 获取城市间效率对比
     */
    List<Map<String, Object>> getCityEfficiencyComparison(@Param("cities") List<String> cities,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}