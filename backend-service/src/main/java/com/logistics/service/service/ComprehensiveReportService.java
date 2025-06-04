package com.logistics.service.service;

import com.logistics.service.dao.entity.ComprehensiveReport;
import com.logistics.service.dao.mapper.ComprehensiveReportMapper;
import com.logistics.service.dto.ComprehensiveReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComprehensiveReportService {

    @Autowired
    private ComprehensiveReportMapper comprehensiveReportMapper;

    // ==================== 基础CRUD操作 ====================

    /**
     * 保存报告（插入或更新）- 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"reports", "stats"}, allEntries = true)
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
                log.info("更新报告成功，城市: {}, 日期: {}，已清除缓存", report.getCity(), report.getDate());
            } else {
                // 不存在则插入
                result = comprehensiveReportMapper.insertReport(report);
                log.info(" 插入报告成功，城市: {}, 日期: {}，已清除缓存", report.getCity(), report.getDate());
            }

            return result > 0;
        } catch (Exception e) {
            log.error("保存报告失败", e);
            return false;
        }
    }

    /**
     * 根据城市、日期和类型获取报告 - 添加缓存
     */
    @Cacheable(value = "reports", key = "'specific:' + #city + ':' + #date + ':' + #reportType",
            unless = "#result == null")
    public ComprehensiveReportDTO getReportByCityDateAndType(String city, LocalDate date, String reportType) {
        try {
            log.info("  查询数据库获取特定报告[city={}, date={}, type={}]", city, date, reportType);
            ComprehensiveReport report = comprehensiveReportMapper.findByCityDateAndType(city, date, reportType);
            return report != null ? convertToDTO(report) : null;
        } catch (Exception e) {
            log.error("获取特定报告失败", e);
            return null;
        }
    }

    /**
     * 更新报告 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"reports", "stats"}, allEntries = true)
    public boolean updateReport(ComprehensiveReportDTO reportDTO) {
        try {
            ComprehensiveReport report = convertToEntity(reportDTO);
            int result = comprehensiveReportMapper.updateReport(report);

            if (result > 0) {
                log.info(" 更新报告成功，已清除缓存");
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
     * 获取指定城市的综合报告 - 添加缓存
     */
    @Cacheable(value = "reports",
            key = "'city:' + #city + ':' + #reportType + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<ComprehensiveReportDTO> getReportsByCity(String city, String reportType,
                                                         LocalDate startDate, LocalDate endDate) {
        try {
            log.info("  查询数据库获取综合报告[city={}, type={}]", city, reportType);
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByCityAndTypeAndDateRange(
                    city, reportType, startDate, endDate);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取综合报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期范围获取报告（不限类型）- 添加缓存
     */
    @Cacheable(value = "reports", key = "'range:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<ComprehensiveReportDTO> getReportsByDateRange(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("  查询数据库获取日期范围报告[city={}]", city);
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByCityAndDateRange(city, startDate, endDate);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取日期范围报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据报告类型获取报告 - 不缓存（数据量可能很大）
     */
    public List<ComprehensiveReportDTO> getReportsByType(String reportType) {
        try {
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByReportType(reportType);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据类型获取报告失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新的日报 - 添加缓存
     */
    @Cacheable(value = "reports", key = "'daily:latest:' + #city", unless = "#result.isEmpty()")
    public List<ComprehensiveReportDTO> getLatestDailyReports(String city) {
        try {
            log.info("  查询数据库获取最新日报[city={}]", city);
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findLatestDailyReports(city, 10);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最新日报失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新的周报 - 添加缓存
     */
    @Cacheable(value = "reports", key = "'weekly:latest:' + #city", unless = "#result.isEmpty()")
    public List<ComprehensiveReportDTO> getLatestWeeklyReports(String city) {
        try {
            log.info("  查询数据库获取最新周报[city={}]", city);
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findLatestWeeklyReports(city, 8);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最新周报失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取城市最新报告 - 添加缓存
     */
    @Cacheable(value = "reports", key = "'latest:' + #city", unless = "#result == null")
    public ComprehensiveReportDTO getLatestReportByCity(String city) {
        try {
            log.info("  查询数据库获取最新报告[city={}]", city);
            ComprehensiveReport report = comprehensiveReportMapper.findLatestByCity(city);
            return report != null ? convertToDTO(report) : null;
        } catch (Exception e) {
            log.error("获取最新报告失败", e);
            return null;
        }
    }

    /**
     * 获取所有城市列表 - 添加缓存（长期缓存）
     */
    @Cacheable(value = "stats", key = "'cities:all'", unless = "#result.isEmpty()")
    public List<String> getAllCities() {
        try {
            log.info("  查询数据库获取城市列表");
            return comprehensiveReportMapper.findAllCities();
        } catch (Exception e) {
            log.error("获取城市列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据日期获取所有城市报告 - 不缓存（一次性查询）
     */
    public List<ComprehensiveReportDTO> getReportsByDate(LocalDate date) {
        try {
            List<ComprehensiveReport> reports = comprehensiveReportMapper.findByDate(date);
            return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取报告失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 统计分析 ====================

    /**
     * 获取城市报告趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityReportTrend(String city, LocalDate startDate) {
        try {
            log.info("  查询数据库获取报告趋势[city={}]", city);
            return comprehensiveReportMapper.getCityReportTrend(city, startDate);
        } catch (Exception e) {
            log.error("获取报告趋势失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取城市效率排行 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'ranking:efficiency:' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityEfficiencyRanking(LocalDate startDate, int limit) {
        try {
            log.info("  查询数据库获取效率排行");
            return comprehensiveReportMapper.getCityEfficiencyRanking(startDate, limit);
        } catch (Exception e) {
            log.error("获取效率排行失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计指定时间范围内的总配送量 - 不缓存（统计查询性能好）
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
     * 获取配送效率统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'delivery_efficiency:' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getDeliveryEfficiencyStats(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("  查询数据库获取配送效率统计");
            return comprehensiveReportMapper.getDeliveryEfficiencyStats(startDate, endDate);
        } catch (Exception e) {
            log.error("获取配送效率统计失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计报告数量 - 不缓存（简单计数查询）
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
     * 清理旧报告数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"reports", "stats"}, allEntries = true)
    public int cleanupOldReports(int daysToKeep) {
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(daysToKeep);
            int deleted = comprehensiveReportMapper.cleanupOldReports(cutoffDate);

            if (deleted > 0) {
                log.info(" 清理旧报告数据成功，删除 {} 条记录，已清除缓存", deleted);
            }

            return deleted;
        } catch (Exception e) {
            log.error("清理旧报告数据失败", e);
            return 0;
        }
    }

    // ==================== 私有方法 ====================

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