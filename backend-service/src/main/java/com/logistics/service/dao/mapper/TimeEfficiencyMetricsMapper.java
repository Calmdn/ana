package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface TimeEfficiencyMetricsMapper {

    /**
     * 插入时间效率数据
     */
    @Insert("INSERT INTO time_efficiency_metrics (city, analysis_date, analysis_hour, total_deliveries, " +
            "avg_delivery_time, median_delivery_time, p95_delivery_time, fast_deliveries, normal_deliveries, slow_deliveries, " +
            "fast_delivery_rate, slow_delivery_rate, total_pickups, avg_pickup_time, median_pickup_time, p95_pickup_time, " +
            "fast_pickups, normal_pickups, slow_pickups, fast_pickup_rate, slow_pickup_rate, on_time_pickups, on_time_pickup_rate) " +
            "VALUES (#{city}, #{analysisDate}, #{analysisHour}, #{totalDeliveries}, " +
            "#{avgDeliveryTime}, #{medianDeliveryTime}, #{p95DeliveryTime}, #{fastDeliveries}, #{normalDeliveries}, #{slowDeliveries}, " +
            "#{fastDeliveryRate}, #{slowDeliveryRate}, #{totalPickups}, #{avgPickupTime}, #{medianPickupTime}, #{p95PickupTime}, " +
            "#{fastPickups}, #{normalPickups}, #{slowPickups}, #{fastPickupRate}, #{slowPickupRate}, #{onTimePickups}, #{onTimePickupRate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTimeEfficiency(TimeEfficiencyMetrics metrics);

    /**
     * 根据城市和日期范围查找时间效率
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC, analysis_hour DESC")
    List<TimeEfficiencyMetrics> findByCityAndDateRange(@Param("city") String city,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 按城市和日期分组获取小时级时间效率数据
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE city = #{city} AND analysis_date = #{date} " +
            "ORDER BY analysis_hour ASC")
    List<TimeEfficiencyMetrics> findByCityAndDateGroupByHour(@Param("city") String city,
                                                             @Param("date") LocalDate date);

    /**
     * 获取取件效率分析
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "AND total_pickups > 0 " +
            "ORDER BY on_time_pickup_rate DESC")
    List<TimeEfficiencyMetrics> findPickupAnalysisByCity(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    /**
     * 获取高峰时段分析
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE city = #{city} AND analysis_date = #{date} " +
            "AND analysis_hour BETWEEN 7 AND 21 " +
            "ORDER BY (total_deliveries + total_pickups) DESC")
    List<TimeEfficiencyMetrics> findPeakHourAnalysis(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 获取配送效率趋势
     */
    @Select("SELECT analysis_date, AVG(fast_delivery_rate) as avg_fast_delivery_rate, " +
            "AVG(avg_delivery_time) as avg_delivery_time, " +
            "AVG(on_time_pickup_rate) as avg_on_time_pickup_rate " +
            "FROM time_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_date " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getDeliveryEfficiencyTrend(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate);

    /**
     * 获取小时级效率分布
     */
    @Select("SELECT analysis_hour, AVG(fast_delivery_rate) as avg_fast_delivery_rate, " +
            "AVG(on_time_pickup_rate) as avg_on_time_pickup_rate, " +
            "SUM(total_deliveries) as total_deliveries, " +
            "SUM(total_pickups) as total_pickups " +
            "FROM time_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_hour " +
            "ORDER BY analysis_hour ASC")
    List<Map<String, Object>> getHourlyEfficiencyDistribution(@Param("city") String city,
                                                              @Param("startDate") LocalDate startDate);

    /**
     * 获取效率等级统计
     */
    @Select("SELECT " +
            "CASE " +
            "  WHEN fast_delivery_rate >= 0.9 THEN '优秀' " +
            "  WHEN fast_delivery_rate >= 0.8 THEN '良好' " +
            "  WHEN fast_delivery_rate >= 0.7 THEN '一般' " +
            "  ELSE '需改进' " +
            "END as efficiency_level, " +
            "COUNT(*) as count, " +
            "AVG(fast_delivery_rate) as avg_rate " +
            "FROM time_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY efficiency_level")
    List<Map<String, Object>> getEfficiencyLevelStats(@Param("city") String city,
                                                      @Param("startDate") LocalDate startDate);

    /**
     * 获取慢配送分析
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE city = #{city} " +
            "AND slow_delivery_rate > #{threshold} AND analysis_date >= #{startDate} " +
            "ORDER BY slow_delivery_rate DESC " +
            "LIMIT #{limit}")
    List<TimeEfficiencyMetrics> findSlowDeliveryAnalysis(@Param("city") String city,
                                                         @Param("threshold") double threshold,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("limit") int limit);

    /**
     * 根据ID查找时间效率数据
     */
    @Select("SELECT * FROM time_efficiency_metrics WHERE id = #{id}")
    TimeEfficiencyMetrics findById(@Param("id") Long id);

    /**
     * 更新时间效率数据
     */
    @Update("UPDATE time_efficiency_metrics SET " +
            "fast_delivery_rate = #{fastDeliveryRate}, on_time_pickup_rate = #{onTimePickupRate}, " +
            "avg_delivery_time = #{avgDeliveryTime} " +
            "WHERE id = #{id}")
    int updateTimeEfficiency(TimeEfficiencyMetrics metrics);

    /**
     * 删除旧的时间效率数据
     */
    @Delete("DELETE FROM time_efficiency_metrics WHERE analysis_date < #{cutoffDate}")
    int cleanupOldTimeEfficiency(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计时间效率记录数
     */
    @Select("SELECT COUNT(*) FROM time_efficiency_metrics WHERE city = #{city}")
    int countByCity(@Param("city") String city);
}