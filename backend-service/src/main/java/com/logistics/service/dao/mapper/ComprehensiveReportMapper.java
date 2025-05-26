package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.ComprehensiveReport;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ComprehensiveReportMapper {

    /**
     * 插入综合报告
     */
    @Insert("INSERT INTO comprehensive_reports (city, region_id, analysis_date, week_start, report_type, " +
            "total_deliveries, total_pickups, active_couriers, active_pickup_couriers, served_aois, " +
            "avg_delivery_time, avg_pickup_time, total_distance, fast_deliveries, on_time_pickups, " +
            "avg_orders_per_courier, avg_distance_per_order, fast_delivery_rate, on_time_pickup_rate) " +
            "VALUES (#{city}, #{regionId}, #{analysisDate}, #{weekStart}, #{reportType}, " +
            "#{totalDeliveries}, #{totalPickups}, #{activeCouriers}, #{activePickupCouriers}, #{servedAois}, " +
            "#{avgDeliveryTime}, #{avgPickupTime}, #{totalDistance}, #{fastDeliveries}, #{onTimePickups}, " +
            "#{avgOrdersPerCourier}, #{avgDistancePerOrder}, #{fastDeliveryRate}, #{onTimePickupRate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertReport(ComprehensiveReport report);

    /**
     * 根据城市、类型和日期范围查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = #{reportType} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<ComprehensiveReport> findByCityAndTypeAndDateRange(@Param("city") String city,
                                                            @Param("reportType") String reportType,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 根据城市和日期范围查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<ComprehensiveReport> findByCityAndDateRange(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据报告类型查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE report_type = #{reportType} " +
            "ORDER BY analysis_date DESC")
    List<ComprehensiveReport> findByReportType(@Param("reportType") String reportType);

    /**
     * 查找最新的日报
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = 'daily' " +
            "ORDER BY analysis_date DESC LIMIT #{limit}")
    List<ComprehensiveReport> findLatestDailyReports(@Param("city") String city, @Param("limit") int limit);

    /**
     * 查找最新的周报
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = 'weekly' " +
            "ORDER BY week_start DESC LIMIT #{limit}")
    List<ComprehensiveReport> findLatestWeeklyReports(@Param("city") String city, @Param("limit") int limit);

    /**
     * 获取城市报告趋势
     */
    @Select("SELECT analysis_date, report_type, fast_delivery_rate, on_time_pickup_rate, " +
            "total_deliveries, total_pickups, active_couriers " +
            "FROM comprehensive_reports " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getCityReportTrend(@Param("city") String city,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 获取效率排行
     */
    @Select("SELECT city, AVG(fast_delivery_rate) as avg_fast_delivery_rate, " +
            "AVG(on_time_pickup_rate) as avg_on_time_pickup_rate, " +
            "AVG(avg_orders_per_courier) as avg_orders_per_courier " +
            "FROM comprehensive_reports " +
            "WHERE analysis_date >= #{startDate} " +
            "GROUP BY city " +
            "ORDER BY avg_fast_delivery_rate DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCityEfficiencyRanking(@Param("startDate") LocalDate startDate,
                                                       @Param("limit") int limit);

    /**
     * 根据ID查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE id = #{id}")
    ComprehensiveReport findById(@Param("id") Long id);

    /**
     * 更新报告
     */
    @Update("UPDATE comprehensive_reports SET " +
            "total_deliveries = #{totalDeliveries}, total_pickups = #{totalPickups}, " +
            "active_couriers = #{activeCouriers}, fast_delivery_rate = #{fastDeliveryRate}, " +
            "on_time_pickup_rate = #{onTimePickupRate} " +
            "WHERE id = #{id}")
    int updateReport(ComprehensiveReport report);

    /**
     * 删除旧报告
     */
    @Delete("DELETE FROM comprehensive_reports WHERE analysis_date < #{cutoffDate}")
    int cleanupOldReports(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计报告数量
     */
    @Select("SELECT COUNT(*) FROM comprehensive_reports WHERE city = #{city} AND report_type = #{reportType}")
    int countReportsByCityAndType(@Param("city") String city, @Param("reportType") String reportType);
}