package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CostAnalysisMetricsMapper {

    /**
     * 插入成本分析数据
     */
    @Insert("INSERT INTO cost_analysis_metrics (city, region_id, courier_id, analysis_date, " +
            "total_cost, total_fuel_cost, total_time_cost, total_orders, total_distance, " +
            "cost_per_order, cost_per_km, fuel_cost_ratio, working_hours, productivity_score, " +
            "efficiency_rating, analysis_type) " +
            "VALUES (#{city}, #{regionId}, #{courierId}, #{analysisDate}, " +
            "#{totalCost}, #{totalFuelCost}, #{totalTimeCost}, #{totalOrders}, #{totalDistance}, " +
            "#{costPerOrder}, #{costPerKm}, #{fuelCostRatio}, #{workingHours}, #{productivityScore}, " +
            "#{efficiencyRating}, #{analysisType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCostAnalysis(CostAnalysisMetrics metrics);

    /**
     * 根据城市和日期范围查找成本分析
     */
    @Select("SELECT * FROM cost_analysis_metrics WHERE city = #{city} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<CostAnalysisMetrics> findByCityAndDateRange(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据配送员和日期范围查找成本分析
     */
    @Select("SELECT * FROM cost_analysis_metrics WHERE courier_id = #{courierId} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<CostAnalysisMetrics> findByCourierAndDateRange(@Param("courierId") String courierId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 根据分析类型查找成本分析
     */
    @Select("SELECT * FROM cost_analysis_metrics WHERE analysis_type = #{analysisType} " +
            "AND analysis_date >= #{startDate} " +
            "ORDER BY analysis_date DESC")
    List<CostAnalysisMetrics> findByAnalysisType(@Param("analysisType") String analysisType,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 获取城市成本趋势
     */
    @Select("SELECT analysis_date, AVG(cost_per_order) as avg_cost_per_order, " +
            "AVG(cost_per_km) as avg_cost_per_km, AVG(fuel_cost_ratio) as avg_fuel_cost_ratio, " +
            "AVG(productivity_score) as avg_productivity_score " +
            "FROM cost_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_date " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getCityCostTrend(@Param("city") String city,
                                               @Param("startDate") LocalDate startDate);

    /**
     * 获取配送员成本排行
     */
    @Select("SELECT courier_id, AVG(cost_per_order) as avg_cost_per_order, " +
            "AVG(productivity_score) as avg_productivity_score, " +
            "SUM(total_orders) as total_orders " +
            "FROM cost_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY courier_id " +
            "ORDER BY avg_cost_per_order ASC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCourierCostRanking(@Param("city") String city,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("limit") int limit);

    /**
     * 获取效率评级统计
     */
    @Select("SELECT efficiency_rating, COUNT(*) as count, " +
            "AVG(cost_per_order) as avg_cost_per_order " +
            "FROM cost_analysis_metrics " +
            "WHERE city = #{city} AND analysis_date >= #{startDate} " +
            "GROUP BY efficiency_rating")
    List<Map<String, Object>> getEfficiencyRatingStats(@Param("city") String city,
                                                       @Param("startDate") LocalDate startDate);

    /**
     * 获取高成本告警
     */
    @Select("SELECT * FROM cost_analysis_metrics " +
            "WHERE cost_per_order > #{threshold} " +
            "ORDER BY cost_per_order DESC " +
            "LIMIT #{limit}")
    List<CostAnalysisMetrics> findHighCostAlerts(@Param("threshold") double threshold,
                                                 @Param("limit") int limit);

    /**
     * 根据ID查找成本分析
     */
    @Select("SELECT * FROM cost_analysis_metrics WHERE id = #{id}")
    CostAnalysisMetrics findById(@Param("id") Long id);

    /**
     * 更新成本分析数据
     */
    @Update("UPDATE cost_analysis_metrics SET " +
            "total_cost = #{totalCost}, cost_per_order = #{costPerOrder}, " +
            "productivity_score = #{productivityScore}, efficiency_rating = #{efficiencyRating} " +
            "WHERE id = #{id}")
    int updateCostAnalysis(CostAnalysisMetrics metrics);

    /**
     * 删除旧的成本分析数据
     */
    @Delete("DELETE FROM cost_analysis_metrics WHERE analysis_date < #{cutoffDate}")
    int cleanupOldCostAnalysis(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计成本分析数量
     */
    @Select("SELECT COUNT(*) FROM cost_analysis_metrics WHERE city = #{city}")
    int countByCit‌y(@Param("city") String city);
}