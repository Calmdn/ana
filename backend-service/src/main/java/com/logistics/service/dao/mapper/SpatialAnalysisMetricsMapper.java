package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface SpatialAnalysisMetricsMapper {

    /**
     * 插入空间分析数据
     */
    @Insert("INSERT INTO spatial_analysis_metrics (city, analysis_date, analysis_hour, lng_grid, lat_grid, " +
            "aoi_id, aoi_type, delivery_count, pickup_count, unique_couriers, orders_in_aoi, couriers_in_aoi, " +
            "avg_delivery_time, avg_pickup_time, avg_delivery_distance, avg_pickup_distance, " +
            "delivery_density, pickup_density, orders_per_courier) " +
            "VALUES (#{city}, #{analysisDate}, #{analysisHour}, #{lngGrid}, #{latGrid}, " +
            "#{aoiId}, #{aoiType}, #{deliveryCount}, #{pickupCount}, #{uniqueCouriers}, #{ordersInAoi}, #{couriersInAoi}, " +
            "#{avgDeliveryTime}, #{avgPickupTime}, #{avgDeliveryDistance}, #{avgPickupDistance}, " +
            "#{deliveryDensity}, #{pickupDensity}, #{ordersPerCourier})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSpatialAnalysis(SpatialAnalysisMetrics metrics);

    /**
     * 根据城市和日期范围查找空间分析
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC, analysis_hour DESC")
    List<SpatialAnalysisMetrics> findByCityAndDateRange(@Param("city") String city,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 根据城市、AOI类型和日期查找空间分析
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE city = #{city} AND aoi_type = #{aoiType} " +
            "AND analysis_date = #{date} " +
            "ORDER BY delivery_count + pickup_count DESC")
    List<SpatialAnalysisMetrics> findByCityAndAoiTypeAndDate(@Param("city") String city,
                                                             @Param("aoiType") Integer aoiType,
                                                             @Param("date") LocalDate date);

    /**
     * 根据AOI ID和日期范围查找
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE aoi_id = #{aoiId} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<SpatialAnalysisMetrics> findByAoiIdAndDateRange(@Param("aoiId") String aoiId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    /**
     * 查找城市热点区域
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE city = #{city} AND analysis_date = #{date} " +
            "AND (delivery_count + pickup_count) > 0 " +
            "ORDER BY (delivery_count + pickup_count) DESC " +
            "LIMIT 20")
    List<SpatialAnalysisMetrics> findHotspotsByCity(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 查找密度分析数据
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE city = #{city} AND analysis_date = #{date} " +
            "AND (delivery_density > 0 OR pickup_density > 0) " +
            "ORDER BY (delivery_density + pickup_density) DESC")
    List<SpatialAnalysisMetrics> findDensityAnalysisByCity(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 获取AOI类型统计
     */
    @Select("SELECT aoi_type, COUNT(*) as aoi_count, " +
            "SUM(delivery_count) as total_deliveries, " +
            "SUM(pickup_count) as total_pickups, " +
            "AVG(delivery_density) as avg_delivery_density " +
            "FROM spatial_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY aoi_type " +
            "ORDER BY total_deliveries DESC")
    List<Map<String, Object>> getAoiTypeStatistics(@Param("city") String city,
                                                   @Param("startDate") LocalDate startDate);

    /**
     * 获取空间分布热力图数据
     */
    @Select("SELECT lng_grid, lat_grid, " +
            "SUM(delivery_count) as total_deliveries, " +
            "SUM(pickup_count) as total_pickups, " +
            "AVG(delivery_density) as avg_delivery_density " +
            "FROM spatial_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date = #{date} " +
            "GROUP BY lng_grid, lat_grid " +
            "HAVING (total_deliveries > 0 OR total_pickups > 0)")
    List<Map<String, Object>> getHeatmapData(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 获取配送员空间分布
     */
    @Select("SELECT aoi_id, aoi_type, SUM(unique_couriers) as total_couriers, " +
            "AVG(orders_per_courier) as avg_orders_per_courier " +
            "FROM spatial_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY aoi_id, aoi_type " +
            "ORDER BY total_couriers DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCourierSpatialDistribution(@Param("city") String city,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("limit") int limit);

    /**
     * 获取时段空间活跃度
     */
    @Select("SELECT analysis_hour, aoi_type, " +
            "AVG(delivery_count + pickup_count) as avg_activity " +
            "FROM spatial_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date = #{date} " +
            "GROUP BY analysis_hour, aoi_type " +
            "ORDER BY analysis_hour, avg_activity DESC")
    List<Map<String, Object>> getHourlySpatialActivity(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 根据ID查找空间分析数据
     */
    @Select("SELECT * FROM spatial_analysis_metrics WHERE id = #{id}")
    SpatialAnalysisMetrics findById(@Param("id") Long id);

    /**
     * 更新空间分析数据
     */
    @Update("UPDATE spatial_analysis_metrics SET " +
            "delivery_count = #{deliveryCount}, pickup_count = #{pickupCount}, " +
            "unique_couriers = #{uniqueCouriers}, delivery_density = #{deliveryDensity} " +
            "WHERE id = #{id}")
    int updateSpatialAnalysis(SpatialAnalysisMetrics metrics);

    /**
     * 删除旧的空间分析数据
     */
    @Delete("DELETE FROM spatial_analysis_metrics WHERE analysis_date < #{cutoffDate}")
    int cleanupOldSpatialData(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计空间分析记录数
     */
    @Select("SELECT COUNT(*) FROM spatial_analysis_metrics WHERE city = #{city}")
    int countByCity(@Param("city") String city);
}