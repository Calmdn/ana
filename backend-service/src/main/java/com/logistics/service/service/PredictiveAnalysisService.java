package com.logistics.service.service;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import com.logistics.service.dao.mapper.PredictiveAnalysisDataMapper;
import com.logistics.service.dto.PredictiveAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PredictiveAnalysisService {

    @Autowired
    private PredictiveAnalysisDataMapper predictiveAnalysisMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存预测分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"predictions", "stats"}, allEntries = true)
    public int savePredictiveAnalysis(PredictiveAnalysisData data) {
        validatePredictiveAnalysisData(data);

        int result = predictiveAnalysisMapper.insertPredictiveAnalysis(data);
        if (result > 0) {
            log.info("保存预测分析数据成功，城市: {}，已清除缓存", data.getCity());
        }
        return result;
    }

    /**
     * 批量保存预测分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"predictions", "stats"}, allEntries = true)
    public int batchSavePredictiveAnalysis(List<PredictiveAnalysisData> dataList) {
        for (PredictiveAnalysisData data : dataList) {
            validatePredictiveAnalysisData(data);
        }

        int result = predictiveAnalysisMapper.batchInsertPredictiveAnalysis(dataList);
        if (result > 0) {
            log.info("批量保存预测分析数据成功，共保存 {} 条，已清除缓存", result);
        }
        return result;
    }

    /**
     * 更新预测分析数据 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"predictions", "stats"}, allEntries = true)
    public int updatePredictiveAnalysis(PredictiveAnalysisData data) {
        validatePredictiveAnalysisData(data);

        int result = predictiveAnalysisMapper.updatePredictiveAnalysis(data);
        if (result > 0) {
            log.info("更新预测分析数据成功，已清除缓存");
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 获取指定城市的预测分析数据 - 添加缓存
     */
    @Cacheable(value = "predictions",
            key = "'city:' + #city + ':' + #dataType + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByCity(String city, String dataType,
                                                                   LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取预测分析[city={}, type={}]", city, dataType);
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByCityAndTypeAndDateRange(
                    city, dataType, startDate, endDate);
            return data.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取预测分析数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期查询预测数据 - 添加缓存
     */
    @Cacheable(value = "predictions", key = "'date:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByDate(String city, LocalDate date) {
        try {
            log.info("查询数据库获取指定日期预测[city={}, date={}]", city, date);
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByCityAndDate(city, date);
            return data.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取指定日期预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询预测数据 - 添加缓存
     */
    @Cacheable(value = "predictions",
            key = "'conditions:' + #city + ':' + #regionId + ':' + #dataType + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByConditions(String city, String regionId, String dataType,
                                                                         LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取多条件预测[city={}, type={}]", city, dataType);
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByConditions(
                    city, regionId, dataType, startDate, endDate);
            return data.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新预测数据 - 添加缓存
     */
    @Cacheable(value = "predictions", key = "'latest:' + #city", unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getLatestPredictions(String city) {
        return getLatestPredictionsByType(city, "prediction", 24); // 获取最近24小时的预测
    }

    /**
     * 按类型获取最新预测数据 - 添加缓存
     */
    @Cacheable(value = "predictions", key = "'latest_type:' + #city + ':' + #dataType + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getLatestPredictionsByType(String city, String dataType, int limit) {
        try {
            log.info("查询数据库获取最新预测[city={}, type={}, limit={}]", city, dataType, limit);
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findLatestPredictions(city, dataType, limit);
            return data.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最新预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取历史趋势数据 - 添加缓存
     */
    @Cacheable(value = "predictions", key = "'historical_trends:' + #city", unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getHistoricalTrends(String city) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        return getPredictiveAnalysisByCity(city, "trend", startDate, endDate);
    }

    /**
     * 获取容量分析数据 - 添加缓存
     */
    @Cacheable(value = "predictions", key = "'capacity_analysis:' + #city", unless = "#result.isEmpty()")
    public List<PredictiveAnalysisDTO> getCapacityAnalysis(String city) {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(7);
        return getPredictiveAnalysisByCity(city, "capacity", startDate, endDate);
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取订单量趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'order_trend:' + #city + ':' + #dataType + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getOrderVolumeTrend(String city, String dataType, LocalDate startDate) {
        log.info("查询数据库获取订单量趋势[city={}, type={}]", city, dataType);
        return predictiveAnalysisMapper.getOrderVolumeTrend(city, dataType, startDate);
    }

    /**
     * 获取小时分布分析 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'hourly_distribution:' + #city + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getHourlyDistribution(String city, LocalDate startDate) {
        log.info("查询数据库获取小时分布[city={}]", city);
        return predictiveAnalysisMapper.getHourlyDistribution(city, startDate);
    }

    /**
     * 获取效率预测趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'efficiency_trend:' + #city + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getEfficiencyTrend(String city, LocalDate startDate) {
        log.info("查询数据库获取效率趋势[city={}]", city);
        return predictiveAnalysisMapper.getEfficiencyTrend(city, startDate);
    }

    /**
     * 获取容量分析统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'capacity_stats:' + #city + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCapacityAnalysisStats(String city, LocalDate startDate) {
        log.info("查询数据库获取容量分析统计[city={}]", city);
        return predictiveAnalysisMapper.getCapacityAnalysis(city, startDate);
    }

    /**
     * 获取数据类型统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'data_type_stats:' + #city + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getDataTypeStats(String city, LocalDate startDate) {
        log.info("查询数据库获取数据类型统计[city={}]", city);
        return predictiveAnalysisMapper.getDataTypeStats(city, startDate);
    }

    /**
     * 获取预测汇总统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'summary:' + #city + ':' + #startDate", unless = "#result == null")
    public Map<String, Object> getPredictiveSummary(String city, LocalDate startDate) {
        log.info("查询数据库获取预测汇总[city={}]", city);
        return predictiveAnalysisMapper.getPredictiveSummary(city, startDate);
    }

    /**
     * 获取城市间预测对比 - 添加缓存
     */
    @Cacheable(value = "stats",
            key = "'comparison:' + #cities.toString() + ':' + #dataType + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityPredictiveComparison(List<String> cities, String dataType,
                                                                 LocalDate startDate, LocalDate endDate) {
        log.info("查询数据库获取城市预测对比，城市数: {}, 类型: {}", cities.size(), dataType);
        return predictiveAnalysisMapper.getCityPredictiveComparison(cities, dataType, startDate, endDate);
    }

    /**
     * 统计记录数 - 不缓存（简单计数查询）
     */
    public int countByCityAndType(String city, String dataType) {
        return predictiveAnalysisMapper.countByCityAndType(city, dataType);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"predictions", "stats"}, allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = predictiveAnalysisMapper.cleanupOldPredictions(cutoffDate);
        if (result > 0) {
            log.info("清理旧预测分析数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 数据验证
     */
    private void validatePredictiveAnalysisData(PredictiveAnalysisData data) {
        if (data.getCity() == null || data.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (data.getDsDate() == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (data.getDataType() == null || data.getDataType().trim().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        if (data.getOrderVolume() == null || data.getOrderVolume() < 0) {
            throw new IllegalArgumentException("订单量不能为负数");
        }
    }

    /**
     * 实体转DTO
     */
    private PredictiveAnalysisDTO convertToDTO(PredictiveAnalysisData data) {
        PredictiveAnalysisDTO dto = new PredictiveAnalysisDTO();
        dto.setCity(data.getCity());
        dto.setRegionId(data.getRegionId());
        dto.setDsDate(data.getDsDate());
        dto.setHour(data.getHour());
        dto.setOrderVolume(data.getOrderVolume());
        dto.setCourierCount(data.getCourierCount());
        dto.setAvgDuration(data.getAvgDuration() != null ? BigDecimal.valueOf(data.getAvgDuration()) : null);
        dto.setTotalDistance(data.getTotalDistance() != null ? BigDecimal.valueOf(data.getTotalDistance()) : null);
        dto.setVolumeTrend(data.getVolumeTrend());
        dto.setEfficiencyScore(data.getEfficiencyScore() != null ? BigDecimal.valueOf(data.getEfficiencyScore()) : null);
        dto.setDataType(data.getDataType());
        return dto;
    }
}