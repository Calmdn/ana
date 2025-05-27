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
    @Insert("INSERT INTO comprehensive_reports (city, region_id, date, total_deliveries, active_couriers, " +
            "served_aois, avg_delivery_time, total_distance, fast_deliveries, avg_orders_per_courier, " +
            "avg_distance_per_order, fast_delivery_rate, report_type, generated_at) " +
            "VALUES (#{city}, #{regionId}, #{date}, #{totalDeliveries}, #{activeCouriers}, " +
            "#{servedAois}, #{avgDeliveryTime}, #{totalDistance}, #{fastDeliveries}, #{avgOrdersPerCourier}, " +
            "#{avgDistancePerOrder}, #{fastDeliveryRate}, #{reportType}, #{generatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertReport(ComprehensiveReport report);

    /**
     * 根据城市、类型和日期范围查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = #{reportType} " +
            "AND date >= #{startDate} AND date <= #{endDate} " +
            "ORDER BY date DESC")
    List<ComprehensiveReport> findByCityAndTypeAndDateRange(@Param("city") String city,
                                                            @Param("reportType") String reportType,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 根据城市和日期范围查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} " +
            "AND date >= #{startDate} AND date <= #{endDate} " +
            "ORDER BY date DESC")
    List<ComprehensiveReport> findByCityAndDateRange(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据报告类型查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE report_type = #{reportType} " +
            "ORDER BY date DESC")
    List<ComprehensiveReport> findByReportType(@Param("reportType") String reportType);

    /**
     * 查找最新的日报
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = 'daily' " +
            "ORDER BY date DESC LIMIT #{limit}")
    List<ComprehensiveReport> findLatestDailyReports(@Param("city") String city, @Param("limit") int limit);

    /**
     * 查找最新的周报
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND report_type = 'weekly' " +
            "ORDER BY date DESC LIMIT #{limit}")
    List<ComprehensiveReport> findLatestWeeklyReports(@Param("city") String city, @Param("limit") int limit);

    /**
     * 获取城市报告趋势
     */
    @Select("SELECT date, report_type, fast_delivery_rate, total_deliveries, active_couriers " +
            "FROM comprehensive_reports " +
            "WHERE city = #{city} AND date >= #{startDate} " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getCityReportTrend(@Param("city") String city,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 获取效率排行
     */
    @Select("SELECT city, AVG(fast_delivery_rate) as avg_fast_delivery_rate, " +
            "AVG(avg_orders_per_courier) as avg_orders_per_courier " +
            "FROM comprehensive_reports " +
            "WHERE date >= #{startDate} " +
            "GROUP BY city " +
            "ORDER BY avg_fast_delivery_rate DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCityEfficiencyRanking(@Param("startDate") LocalDate startDate,
                                                       @Param("limit") int limit);

    /**
     * 根据城市、日期和报告类型查找报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} AND date = #{date} AND report_type = #{reportType}")
    ComprehensiveReport findByCityDateAndType(@Param("city") String city,
                                              @Param("date") LocalDate date,
                                              @Param("reportType") String reportType);

    /**
     * 更新报告
     */
    @Update("UPDATE comprehensive_reports SET " +
            "total_deliveries = #{totalDeliveries}, active_couriers = #{activeCouriers}, " +
            "fast_delivery_rate = #{fastDeliveryRate}, avg_orders_per_courier = #{avgOrdersPerCourier} " +
            "WHERE city = #{city} AND date = #{date} AND report_type = #{reportType}")
    int updateReport(ComprehensiveReport report);

    /**
     * 删除旧报告
     */
    @Delete("DELETE FROM comprehensive_reports WHERE date < #{cutoffDate}")
    int cleanupOldReports(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计报告数量
     */
    @Select("SELECT COUNT(*) FROM comprehensive_reports WHERE city = #{city} AND report_type = #{reportType}")
    int countReportsByCityAndType(@Param("city") String city, @Param("reportType") String reportType);

    /**
     * 根据城市获取最新报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE city = #{city} " +
            "ORDER BY date DESC LIMIT 1")
    ComprehensiveReport findLatestByCity(@Param("city") String city);

    /**
     * 获取所有城市列表
     */
    @Select("SELECT DISTINCT city FROM comprehensive_reports ORDER BY city")
    List<String> findAllCities();

    /**
     * 根据日期获取所有城市报告
     */
    @Select("SELECT * FROM comprehensive_reports WHERE date = #{date} " +
            "ORDER BY city")
    List<ComprehensiveReport> findByDate(@Param("date") LocalDate date);

    /**
     * 统计指定时间范围内的总配送量
     */
    @Select("SELECT SUM(total_deliveries) FROM comprehensive_reports " +
            "WHERE city = #{city} AND date >= #{startDate} AND date <= #{endDate}")
    Long sumDeliveriesByDateRange(@Param("city") String city,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    /**
     * 获取配送效率统计
     */
    @Select("SELECT city, date, fast_delivery_rate, avg_orders_per_courier, avg_distance_per_order " +
            "FROM comprehensive_reports " +
            "WHERE date >= #{startDate} AND date <= #{endDate} " +
            "ORDER BY fast_delivery_rate DESC")
    List<Map<String, Object>> getDeliveryEfficiencyStats(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}