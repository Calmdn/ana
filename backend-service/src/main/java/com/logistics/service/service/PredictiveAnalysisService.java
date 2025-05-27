package com.logistics.service.service;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import com.logistics.service.dao.mapper.PredictiveAnalysisDataMapper;
import com.logistics.service.dto.PredictiveAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PredictiveAnalysisService {

    @Autowired
    private PredictiveAnalysisDataMapper predictiveAnalysisMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存预测分析数据
     */
    @Transactional
    public int savePredictiveAnalysis(PredictiveAnalysisData data) {
        validatePredictiveAnalysisData(data);
        return predictiveAnalysisMapper.insertPredictiveAnalysis(data);
    }

    /**
     * 批量保存预测分析数据
     */
    @Transactional
    public int batchSavePredictiveAnalysis(List<PredictiveAnalysisData> dataList) {
        for (PredictiveAnalysisData data : dataList) {
            validatePredictiveAnalysisData(data);
        }
        return predictiveAnalysisMapper.batchInsertPredictiveAnalysis(dataList);
    }

    /**
     * 获取指定城市的预测分析数据
     */
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByCity(String city, String dataType, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("predictive_analysis:%s:%s:%s:%s", city, dataType, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<PredictiveAnalysisDTO> cache = (List<PredictiveAnalysisDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取预测分析[city={}, type={}], size={}", city, dataType, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 预测分析[city={}, type={}]", city, dataType);
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByCityAndTypeAndDateRange(city, dataType, startDate, endDate);

            List<PredictiveAnalysisDTO> result = data.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 30, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存预测分析[city={}, type={}]，ttl=30m", city, dataType);
            }

            return result;
        } catch (Exception e) {
            log.error("获取预测分析数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期查询预测数据
     */
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByDate(String city, LocalDate date) {
        try {
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByCityAndDate(city, date);
            return data.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取指定日期预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询预测数据
     */
    public List<PredictiveAnalysisDTO> getPredictiveAnalysisByConditions(String city, String regionId, String dataType,
                                                                         LocalDate startDate, LocalDate endDate) {
        try {
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByConditions(city, regionId, dataType, startDate, endDate);
            return data.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最新预测数据
     */
    public List<PredictiveAnalysisDTO> getLatestPredictions(String city) {
        return getLatestPredictionsByType(city, "prediction", 24); // 获取最近24小时的预测
    }

    /**
     * 按类型获取最新预测数据
     */
    public List<PredictiveAnalysisDTO> getLatestPredictionsByType(String city, String dataType, int limit) {
        try {
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findLatestPredictions(city, dataType, limit);
            return data.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最新预测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取历史趋势数据
     */
    public List<PredictiveAnalysisDTO> getHistoricalTrends(String city) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        return getPredictiveAnalysisByCity(city, "trend", startDate, endDate);
    }

    /**
     * 获取容量分析数据
     */
    public List<PredictiveAnalysisDTO> getCapacityAnalysis(String city) {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(7);
        return getPredictiveAnalysisByCity(city, "capacity", startDate, endDate);
    }

    /**
     * 获取订单量趋势
     */
    public List<Map<String, Object>> getOrderVolumeTrend(String city, String dataType, LocalDate startDate) {
        return predictiveAnalysisMapper.getOrderVolumeTrend(city, dataType, startDate);
    }

    /**
     * 获取小时分布分析
     */
    public List<Map<String, Object>> getHourlyDistribution(String city, LocalDate startDate) {
        return predictiveAnalysisMapper.getHourlyDistribution(city, startDate);
    }

    /**
     * 获取效率预测趋势
     */
    public List<Map<String, Object>> getEfficiencyTrend(String city, LocalDate startDate) {
        return predictiveAnalysisMapper.getEfficiencyTrend(city, startDate);
    }

    /**
     * 获取容量分析统计
     */
    public List<Map<String, Object>> getCapacityAnalysisStats(String city, LocalDate startDate) {
        return predictiveAnalysisMapper.getCapacityAnalysis(city, startDate);
    }

    /**
     * 获取数据类型统计
     */
    public List<Map<String, Object>> getDataTypeStats(String city, LocalDate startDate) {
        return predictiveAnalysisMapper.getDataTypeStats(city, startDate);
    }

    /**
     * 获取预测汇总统计
     */
    public Map<String, Object> getPredictiveSummary(String city, LocalDate startDate) {
        return predictiveAnalysisMapper.getPredictiveSummary(city, startDate);
    }

    /**
     * 获取城市间预测对比
     */
    public List<Map<String, Object>> getCityPredictiveComparison(List<String> cities, String dataType,
                                                                 LocalDate startDate, LocalDate endDate) {
        return predictiveAnalysisMapper.getCityPredictiveComparison(cities, dataType, startDate, endDate);
    }

    /**
     * 更新预测分析数据
     */
    @Transactional
    public int updatePredictiveAnalysis(PredictiveAnalysisData data) {
        validatePredictiveAnalysisData(data);
        return predictiveAnalysisMapper.updatePredictiveAnalysis(data);
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public int cleanupOldData(LocalDate cutoffDate) {
        return predictiveAnalysisMapper.cleanupOldPredictions(cutoffDate);
    }

    /**
     * 统计记录数
     */
    public int countByCityAndType(String city, String dataType) {
        return predictiveAnalysisMapper.countByCityAndType(city, dataType);
    }

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