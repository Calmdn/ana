package com.logistics.service.service;

import com.logistics.service.dao.entity.ComprehensiveReport;
import com.logistics.service.dao.mapper.ComprehensiveReportMapper;
import com.logistics.service.dto.ComprehensiveReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Slf4j
@Service
public class ComprehensiveReportService {

    @Autowired
    private ComprehensiveReportMapper comprehensiveReportMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定城市的综合报告
     */
    public List<ComprehensiveReportDTO> getReportsByCity(String city, String reportType, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("reports:%s:%s:%s:%s", city, reportType, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<ComprehensiveReportDTO> cache = (List<ComprehensiveReportDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取综合报告[city={}, type={}], size={}", city, reportType, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 综合报告[city={}, type={}]", city, reportType);
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByCityAndTypeAndDateRange(city, reportType, startDate, endDate);

            List<ComprehensiveReportDTO> result = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 120, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存综合报告[city={}, type={}]，ttl=120m", city, reportType);
            }

            return result;
        } catch (Exception e) {
            log.error("获取综合报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新的日报
     */
    public List<ComprehensiveReportDTO> getLatestDailyReports(String city) {
        return getReportsByCity(city, "daily", LocalDate.now().minusDays(7), LocalDate.now());
    }

    /**
     * 获取最新的周报
     */
    public List<ComprehensiveReportDTO> getLatestWeeklyReports(String city) {
        return getReportsByCity(city, "weekly", LocalDate.now().minusWeeks(4), LocalDate.now());
    }

    private ComprehensiveReportDTO convertToDTO(ComprehensiveReport report) {
        ComprehensiveReportDTO dto = new ComprehensiveReportDTO();
        dto.setCity(report.getCity());
        dto.setRegionId(report.getRegionId());
        dto.setAnalysisDate(report.getAnalysisDate());
        dto.setWeekStart(report.getWeekStart());
        dto.setReportType(report.getReportType());
        dto.setTotalDeliveries(report.getTotalDeliveries());
        dto.setTotalPickups(report.getTotalPickups());
        dto.setActiveCouriers(report.getActiveCouriers());
        dto.setActivePickupCouriers(report.getActivePickupCouriers());
        dto.setServedAois(report.getServedAois());
        dto.setAvgDeliveryTime(report.getAvgDeliveryTime() != null ? BigDecimal.valueOf(report.getAvgDeliveryTime()) : null);
        dto.setAvgPickupTime(report.getAvgPickupTime() != null ? BigDecimal.valueOf(report.getAvgPickupTime()) : null);
        dto.setTotalDistance(report.getTotalDistance() != null ? BigDecimal.valueOf(report.getTotalDistance()) : null);
        dto.setFastDeliveries(report.getFastDeliveries());
        dto.setOnTimePickups(report.getOnTimePickups());
        dto.setAvgOrdersPerCourier(report.getAvgOrdersPerCourier() != null ? BigDecimal.valueOf(report.getAvgOrdersPerCourier()) : null);
        dto.setAvgDistancePerOrder(report.getAvgDistancePerOrder() != null ? BigDecimal.valueOf(report.getAvgDistancePerOrder()) : null);
        dto.setFastDeliveryRate(report.getFastDeliveryRate() != null ? BigDecimal.valueOf(report.getFastDeliveryRate()) : null);
        dto.setOnTimePickupRate(report.getOnTimePickupRate() != null ? BigDecimal.valueOf(report.getOnTimePickupRate()) : null);
        return dto;
    }
}