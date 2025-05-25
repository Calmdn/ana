package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.RealtimeKpi;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RealtimeKpiMapper {

    @Select("SELECT * FROM realtime_kpi WHERE city = #{city} AND date = #{date} ORDER BY hour")
    List<RealtimeKpi> findBycityAndDate(@Param("city") String city, @Param("date") LocalDate date);

    @Select("SELECT * FROM realtime_kpi WHERE city = #{city} AND date = #{date} AND hour = #{hour}")
    RealtimeKpi findByCityAndDateAndHour(@Param("city") String city,
                                          @Param("date") LocalDate date,
                                          @Param("hour") Integer hour);

    @Insert("INSERT INTO realtime_kpi (city, date, hour, total_orders, active_couriers, " +
            "coverage_aois, orders_per_courier, orders_per_aoi, efficiency_score) " +
            "VALUES (#{city}, #{date}, #{hour}, #{totalOrders}, #{activeCouriers}, " +
            "#{coverageAois}, #{ordersPerCourier}, #{ordersPerAoi}, #{efficiencyScore}) " +
            "ON DUPLICATE KEY UPDATE " +
            "total_orders = VALUES(total_orders), active_couriers = VALUES(active_couriers), " +
            "coverage_aois = VALUES(coverage_aois), orders_per_courier = VALUES(orders_per_courier), " +
            "orders_per_aoi = VALUES(orders_per_aoi), efficiency_score = VALUES(efficiency_score), " +
            "updated_at = NOW()")
    int insertOrUpdateKpi(RealtimeKpi kpi);

    @Select("SELECT * FROM realtime_kpi WHERE city = #{city} " +
            "AND date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "ORDER BY date DESC, hour DESC")
    List<RealtimeKpi> findRecentKpiByCity(@Param("city") String city, @Param("days") int days);

    @Delete("DELETE FROM realtime_kpi WHERE date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)")
    int cleanupOldKpi();
}