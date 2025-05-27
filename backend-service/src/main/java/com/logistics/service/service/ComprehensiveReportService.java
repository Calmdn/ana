package com.logistics.service.service;

import com.logistics.service.dao.entity.ComprehensiveReport;
import com.logistics.service.dao.mapper.ComprehensiveReportMapper;
import com.logistics.service.dto.ComprehensiveReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ComprehensiveReportService {

    @Autowired
    private ComprehensiveReportMapper comprehensiveReportMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== 基础CRUD操作 ====================

    /**
     * 保存报告（插入或更新）
     */
    @Transactional
    public boolean saveReport(ComprehensiveReportDTO reportDTO) {
        try {
            ComprehensiveReport report = convertToEntity(reportDTO);

            // 先查询是否存在
            ComprehensiveReport existing = comprehensiveReportMapper.findByCityDateAndType(
                    report.getCity(), report.getDate(), report.getReportType());

            int result;
            if (existing != null) {
                // 存在则更新
                result = comprehensiveReportMapper.updateReport(report);
                log.info("✅ 更新报告成功，城市: {}, 日期: {}", report.getCity(), report.getDate());
            } else {
                // 不存在则插入
                result = comprehensiveReportMapper.insertReport(report);
                log.info("✅ 插入报告成功，城市: {}, 日期: {}", report.getCity(), report.getDate());
            }

            if (result > 0) {
                clearReportsCache();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("保存报告失败", e);
            return false;
        }
    }

    /**
     * 根据城市、日期和类型获取报告
     */
    public ComprehensiveReportDTO getReportByCityDateAndType(String city, LocalDate date, String reportType) {
        try {
            String key = String.format("report:specific:%s:%s:%s", city, date, reportType);

            @SuppressWarnings("unchecked")
            ComprehensiveReportDTO cache = (ComprehensiveReportDTO) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取特定报告");
                return cache;
            }

            ComprehensiveReport report = comprehensiveReportMapper.findByCityDateAndType(city, date, reportType);
            if (report != null) {
                ComprehensiveReportDTO dto = convertToDTO(report);
                redisTemplate.opsForValue().set(key, dto, 60, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存特定报告，ttl=60m");
                return dto;
            }
            return null;
        } catch (Exception e) {
            log.error("获取特定报告失败", e);
            return null;
        }
    }

    /**
     * 更新报告
     */
    @Transactional
    public boolean updateReport(ComprehensiveReportDTO reportDTO) {
        try {
            ComprehensiveReport report = convertToEntity(reportDTO);
            int result = comprehensiveReportMapper.updateReport(report);

            if (result > 0) {
                clearReportsCache();
                log.info("✅ 更新报告成功");
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新报告失败", e);
            return false;
        }
    }

    // ==================== 查询操作 ====================

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
     * 根据城市和日期范围获取报告（不限类型）
     */
    public List<ComprehensiveReportDTO> getReportsByDateRange(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("reports:range:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<ComprehensiveReportDTO> cache = (List<ComprehensiveReportDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取日期范围报告");
                return cache;
            }

            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByCityAndDateRange(city, startDate, endDate);
            List<ComprehensiveReportDTO> result = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 90, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存日期范围报告，ttl=90m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取日期范围报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据报告类型获取报告
     */
    public List<ComprehensiveReportDTO> getReportsByType(String reportType) {
        try {
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByReportType(reportType);
            return reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据类型获取报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新的日报
     */
    public List<ComprehensiveReportDTO> getLatestDailyReports(String city) {
        try {
            String key = "reports:daily:latest:" + city;

            @SuppressWarnings("unchecked")
            List<ComprehensiveReportDTO> cache = (List<ComprehensiveReportDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取最新日报");
                return cache;
            }

            List<ComprehensiveReport> reports = comprehensiveReportMapper.findLatestDailyReports(city, 10);
            List<ComprehensiveReportDTO> result = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 30, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存最新日报，ttl=30m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取最新日报失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新的周报
     */
    public List<ComprehensiveReportDTO> getLatestWeeklyReports(String city) {
        try {
            String key = "reports:weekly:latest:" + city;

            @SuppressWarnings("unchecked")
            List<ComprehensiveReportDTO> cache = (List<ComprehensiveReportDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取最新周报");
                return cache;
            }

            List<ComprehensiveReport> reports = comprehensiveReportMapper.findLatestWeeklyReports(city, 8);
            List<ComprehensiveReportDTO> result = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 60, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存最新周报，ttl=60m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取最新周报失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取城市最新报告
     */
    public ComprehensiveReportDTO getLatestReportByCity(String city) {
        try {
            ComprehensiveReport report = comprehensiveReportMapper.findLatestByCity(city);
            return report != null ? convertToDTO(report) : null;
        } catch (Exception e) {
            log.error("获取最新报告失败", e);
            return null;
        }
    }

    /**
     * 获取所有城市列表
     */
    public List<String> getAllCities() {
        try {
            String key = "reports:cities:all";

            @SuppressWarnings("unchecked")
            List<String> cache = (List<String>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取城市列表");
                return cache;
            }

            List<String> cities = comprehensiveReportMapper.findAllCities();

            if (!cities.isEmpty()) {
                redisTemplate.opsForValue().set(key, cities, 240, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存城市列表，ttl=240m");
            }

            return cities;
        } catch (Exception e) {
            log.error("获取城市列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据日期获取所有城市报告
     */
    public List<ComprehensiveReportDTO> getReportsByDate(LocalDate date) {
        try {
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByDate(date);
            return reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取报告失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 统计分析 ====================

    /**
     * 获取城市报告趋势
     */
    public List<Map<String, Object>> getCityReportTrend(String city, LocalDate startDate) {
        try {
            String key = String.format("trend:reports:%s:%s", city, startDate);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cache = (List<Map<String, Object>>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取报告趋势");
                return cache;
            }

            List<Map<String, Object>> result = comprehensiveReportMapper.getCityReportTrend(city, startDate);

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 120, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存报告趋势，ttl=120m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取报告趋势失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取城市效率排行
     */
    public List<Map<String, Object>> getCityEfficiencyRanking(LocalDate startDate, int limit) {
        try {
            String key = String.format("ranking:efficiency:%s:%d", startDate, limit);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cache = (List<Map<String, Object>>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取效率排行");
                return cache;
            }

            List<Map<String, Object>> result = comprehensiveReportMapper.getCityEfficiencyRanking(startDate, limit);

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 180, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存效率排行，ttl=180m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取效率排行失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计指定时间范围内的总配送量
     */
    public Long getTotalDeliveriesByDateRange(String city, LocalDate startDate, LocalDate endDate) {
        try {
            Long total = comprehensiveReportMapper.sumDeliveriesByDateRange(city, startDate, endDate);
            return total != null ? total : 0L;
        } catch (Exception e) {
            log.error("获取总配送量失败", e);
            return 0L;
        }
    }

    /**
     * 获取配送效率统计
     */
    public List<Map<String, Object>> getDeliveryEfficiencyStats(LocalDate startDate, LocalDate endDate) {
        try {
            return comprehensiveReportMapper.getDeliveryEfficiencyStats(startDate, endDate);
        } catch (Exception e) {
            log.error("获取配送效率统计失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计报告数量
     */
    public int countReportsByCityAndType(String city, String reportType) {
        try {
            return comprehensiveReportMapper.countReportsByCityAndType(city, reportType);
        } catch (Exception e) {
            log.error("统计报告数量失败", e);
            return 0;
        }
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧报告数据
     */
    @Transactional
    public int cleanupOldReports(int daysToKeep) {
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(daysToKeep);
            int deleted = comprehensiveReportMapper.cleanupOldReports(cutoffDate);

            if (deleted > 0) {
                clearReportsCache();
                log.info("✅ 清理旧报告数据成功，删除 {} 条记录", deleted);
            }

            return deleted;
        } catch (Exception e) {
            log.error("清理旧报告数据失败", e);
            return 0;
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 清除报告相关缓存
     */
    private void clearReportsCache() {
        try {
            redisTemplate.delete(redisTemplate.keys("reports:*"));
            redisTemplate.delete(redisTemplate.keys("report:*"));
            redisTemplate.delete(redisTemplate.keys("trend:*"));
            redisTemplate.delete(redisTemplate.keys("ranking:*"));
            log.info("🗑️ 已清除报告相关缓存");
        } catch (Exception e) {
            log.warn("清除报告缓存失败", e);
        }
    }

    /**
     * Entity转DTO
     */
    private ComprehensiveReportDTO convertToDTO(ComprehensiveReport report) {
        ComprehensiveReportDTO dto = new ComprehensiveReportDTO();
        dto.setCity(report.getCity());
        dto.setRegionId(report.getRegionId());
        dto.setDate(report.getDate());
        dto.setTotalDeliveries(report.getTotalDeliveries());
        dto.setActiveCouriers(report.getActiveCouriers());
        dto.setServedAois(report.getServedAois());
        dto.setAvgDeliveryTime(report.getAvgDeliveryTime());
        dto.setTotalDistance(report.getTotalDistance());
        dto.setFastDeliveries(report.getFastDeliveries());
        dto.setAvgOrdersPerCourier(report.getAvgOrdersPerCourier());
        dto.setAvgDistancePerOrder(report.getAvgDistancePerOrder());
        dto.setFastDeliveryRate(report.getFastDeliveryRate());
        dto.setReportType(report.getReportType());
        dto.setGeneratedAt(report.getGeneratedAt());
        return dto;
    }

    /**
     * DTO转Entity
     */
    private ComprehensiveReport convertToEntity(ComprehensiveReportDTO dto) {
        ComprehensiveReport report = new ComprehensiveReport();
        report.setCity(dto.getCity());
        report.setRegionId(dto.getRegionId());
        report.setDate(dto.getDate());
        report.setTotalDeliveries(dto.getTotalDeliveries());
        report.setActiveCouriers(dto.getActiveCouriers());
        report.setServedAois(dto.getServedAois());
        report.setAvgDeliveryTime(dto.getAvgDeliveryTime());
        report.setTotalDistance(dto.getTotalDistance());
        report.setFastDeliveries(dto.getFastDeliveries());
        report.setAvgOrdersPerCourier(dto.getAvgOrdersPerCourier());
        report.setAvgDistancePerOrder(dto.getAvgDistancePerOrder());
        report.setFastDeliveryRate(dto.getFastDeliveryRate());
        report.setReportType(dto.getReportType());
        report.setGeneratedAt(dto.getGeneratedAt() != null ? dto.getGeneratedAt() : LocalDateTime.now());
        return report;
    }
}