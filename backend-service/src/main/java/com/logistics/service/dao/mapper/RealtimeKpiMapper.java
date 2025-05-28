package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.RealtimeKpi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface RealtimeKpiMapper {

    /**
     * 插入或更新KPI数据
     */
    int insertOrUpdateKpi(RealtimeKpi kpi);

    /**
     * 批量插入KPI数据
     */
    int batchInsertKpi(@Param("list") List<RealtimeKpi> kpiList);

    /**
     * 根据城市和日期查找KPI
     */
    List<RealtimeKpi> findByCityAndDate(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 根据城市、日期和小时查找KPI
     */
    RealtimeKpi findByCityAndDateAndHour(@Param("city") String city,
                                         @Param("date") LocalDate date,
                                         @Param("hour") Integer hour);

    /**
     * 获取城市最近几天的KPI数据
     */
    List<RealtimeKpi> findRecentKpiByCity(@Param("city") String city, @Param("days") int days);

    /**
     * 获取多个城市的KPI数据
     */
    List<RealtimeKpi> findByCitiesAndDate(@Param("cities") List<String> cities, @Param("date") LocalDate date);

    /**
     * 获取KPI趋势数据
     */
    List<Map<String, Object>> getKpiTrend(@Param("city") String city, @Param("startDate") LocalDate startDate);

    /**
     * 获取小时级KPI统计
     */
    List<Map<String, Object>> getHourlyKpiStats(@Param("city") String city, @Param("date") LocalDate date);

    /**
     * 获取城市KPI排行
     */
    List<Map<String, Object>> getCityKpiRanking(@Param("date") LocalDate date, @Param("limit") int limit);

    /**
     * 获取KPI汇总统计
     */
    Map<String, Object> getKpiSummary(@Param("city") String city, @Param("startDate") LocalDate startDate);

    /**
     * 获取最新KPI数据
     */
    RealtimeKpi findLatestKpiByCity(@Param("city") String city);

    /**
     * 清理旧的KPI数据
     */
    int cleanupOldKpi(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计KPI记录数
     */
    int countByCity(@Param("city") String city);
}