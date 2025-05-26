package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OperationalEfficiencyMetricsMapper {

    /**
     * 插入运营效率数据
     */
    @Insert("INSERT INTO operational_efficiency_metrics (city, region_id, courier_id, analysis_date, analysis_hour, " +
            "total_orders, unique_aoi_served, total_distance, total_working_hours, avg_delivery_time, " +
            "orders_per_hour, distance_per_order, efficiency_score) " +
            "VALUES (#{city}, #{regionId}, #{courierId}, #{analysisDate}, #{analysisHour}, " +
            "#{totalOrders}, #{uniqueAoiServed}, #{totalDistance}, #{totalWorkingHours}, #{avgDeliveryTime}, " +
            "#{ordersPerHour}, #{distancePerOrder}, #{efficiencyScore})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEfficiencyMetrics(OperationalEfficiencyMetrics metrics);

    /**
     * 根据城市和日期范围查找运营效率
     */
    @Select("SELECT * FROM operational_efficiency_metrics WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC, analysis_hour DESC")
    List<OperationalEfficiencyMetrics> findByCityAndDateRange(@Param("city") String city,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);

    /**
     * 根据配送员和日期范围查找运营效率
     */
    @Select("SELECT * FROM operational_efficiency_metrics WHERE courier_id = #{courierId} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<OperationalEfficiencyMetrics> findByCourierAndDateRange(@Param("courierId") Integer courierId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    /**
     * 按城市和日期分组获取小时级效率数据
     */
    @Select("SELECT * FROM operational_efficiency_metrics WHERE city = #{city} AND analysis_date = #{date} " +
            "ORDER BY analysis_hour ASC")
    List<OperationalEfficiencyMetrics> findByCityAndDateGroupByHour(@Param("city") String city,
                                                                    @Param("date") LocalDate date);

    /**
     * 获取城市效率趋势
     */
    @Select("SELECT analysis_date, AVG(efficiency_score) as avg_efficiency_score, " +
            "AVG(orders_per_hour) as avg_orders_per_hour, " +
            "AVG(distance_per_order) as avg_distance_per_order " +
            "FROM operational_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_date " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getCityEfficiencyTrend(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate);

    /**
     * 获取配送员效率排行
     */
    @Select("SELECT courier_id, AVG(efficiency_score) as avg_efficiency_score, " +
            "AVG(orders_per_hour) as avg_orders_per_hour, " +
            "SUM(total_orders) as total_orders " +
            "FROM operational_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY courier_id " +
            "ORDER BY avg_efficiency_score DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCourierEfficiencyRanking(@Param("city") String city,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("limit") int limit);

    /**
     * 获取小时级效率分布
     */
    @Select("SELECT analysis_hour, AVG(efficiency_score) as avg_efficiency_score, " +
            "AVG(orders_per_hour) as avg_orders_per_hour, " +
            "COUNT(*) as record_count " +
            "FROM operational_efficiency_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_hour " +
            "ORDER BY analysis_hour ASC")
    List<Map<String, Object>> getHourlyEfficiencyDistribution(@Param("city") String city,
                                                              @Param("startDate") LocalDate startDate);

    /**
     * 获取低效率警告
     */
    @Select("SELECT * FROM operational_efficiency_metrics " +
            "WHERE efficiency_score < #{threshold} AND analysis_date >= #{startDate} " +
            "ORDER BY efficiency_score ASC " +
            "LIMIT #{limit}")
    List<OperationalEfficiencyMetrics> findLowEfficiencyAlerts(@Param("threshold") double threshold,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("limit") int limit);

    /**
     * 根据ID查找运营效率数据
     */
    @Select("SELECT * FROM operational_efficiency_metrics WHERE id = #{id}")
    OperationalEfficiencyMetrics findById(@Param("id") Long id);

    /**
     * 更新运营效率数据
     */
    @Update("UPDATE operational_efficiency_metrics SET " +
            "total_orders = #{totalOrders}, efficiency_score = #{efficiencyScore}, " +
            "orders_per_hour = #{ordersPerHour} " +
            "WHERE id = #{id}")
    int updateEfficiencyMetrics(OperationalEfficiencyMetrics metrics);

    /**
     * 删除旧的运营效率数据
     */
    @Delete("DELETE FROM operational_efficiency_metrics WHERE analysis_date < #{cutoffDate}")
    int cleanupOldMetrics(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计运营效率记录数
     */
    @Select("SELECT COUNT(*) FROM operational_efficiency_metrics WHERE city = #{city}")
    int countByCity(@Param("city") String city);
}