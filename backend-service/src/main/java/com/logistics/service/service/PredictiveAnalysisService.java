package com.logistics.service.service;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import com.logistics.service.dao.mapper.PredictiveAnalysisDataMapper;
import com.logistics.service.dto.PredictiveAnalysisDTO;
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
public class PredictiveAnalysisService {

    @Autowired
    private PredictiveAnalysisDataMapper predictiveAnalysisMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
     * 获取最新预测数据
     */
    public List<PredictiveAnalysisDTO> getLatestPredictions(String city) {
        return getPredictiveAnalysisByCity(city, "prediction", LocalDate.now(), LocalDate.now().plusDays(7));
    }

    /**
     * 获取历史趋势数据
     */
    public List<PredictiveAnalysisDTO> getHistoricalTrends(String city) {
        return getPredictiveAnalysisByCity(city, "trend", LocalDate.now().minusDays(30), LocalDate.now());
    }

    /**
     * 获取容量分析数据
     */
    public List<PredictiveAnalysisDTO> getCapacityAnalysis(String city) {
        return getPredictiveAnalysisByCity(city, "capacity", LocalDate.now().minusDays(7), LocalDate.now().plusDays(7));
    }

    /**
     * 获取小时级预测数据
     */
    public List<PredictiveAnalysisDTO> getHourlyPredictions(String city, LocalDate date) {
        try {
            List<PredictiveAnalysisData> data = predictiveAnalysisMapper.findByCityAndDateGroupByHour(city, date);
            return data.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取小时预测数据失败", e);
            return new ArrayList<>();
        }
    }

    private PredictiveAnalysisDTO convertToDTO(PredictiveAnalysisData data) {
        PredictiveAnalysisDTO dto = new PredictiveAnalysisDTO();
        dto.setCity(data.getCity());
        dto.setRegionId(data.getRegionId());
        dto.setAnalysisDate(data.getAnalysisDate());
        dto.setAnalysisHour(data.getAnalysisHour());
        dto.setOrderVolume(data.getOrderVolume());
        dto.setCourierCount(data.getCourierCount());
        dto.setAvgDuration(data.getAvgDuration() != null ? BigDecimal.valueOf(data.getAvgDuration()) : null);
        dto.setTotalDistance(data.getTotalDistance() != null ? BigDecimal.valueOf(data.getTotalDistance()) : null);
        dto.setVolumeTrend(data.getVolumeTrend());
        dto.setEfficiencyScore(data.getEfficiencyScore() != null ? BigDecimal.valueOf(data.getEfficiencyScore()) : null);
        dto.setDailyOrders(data.getDailyOrders());
        dto.setRequiredCouriers(data.getRequiredCouriers());
        dto.setPeakHour(data.getPeakHour());
        dto.setOrdersPerCourierDay(data.getOrdersPerCourierDay() != null ? BigDecimal.valueOf(data.getOrdersPerCourierDay()) : null);
        dto.setCapacityUtilization(data.getCapacityUtilization());
        dto.setDataType(data.getDataType());
        return dto;
    }
}