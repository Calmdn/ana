package com.logistics.service.controller;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import com.logistics.service.dto.OperationalEfficiencyDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.OperationalEfficiencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/operational-efficiency")
@CrossOrigin(origins = "*")
public class OperationalEfficiencyController {

    @Autowired
    private OperationalEfficiencyService operationalEfficiencyService;

    /**
     * 保存运营效率数据
     */
    @PostMapping
    public SimpleResponse<String> saveEfficiencyMetrics(@RequestBody OperationalEfficiencyMetrics metrics) {
        try {
            log.info("保存运营效率数据: city={}, regionId={}, courierId={}, date={}",
                    metrics.getCity(), metrics.getRegionId(), metrics.getCourierId(), metrics.getDate());
            int result = operationalEfficiencyService.saveEfficiencyMetrics(metrics);
            return SimpleResponse.success("保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("保存运营效率数据失败", e);
            return SimpleResponse.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存运营效率数据
     */
    @PostMapping("/batch")
    public SimpleResponse<String> batchSaveEfficiencyMetrics(@RequestBody List<OperationalEfficiencyMetrics> metricsList) {
        try {
            log.info("批量保存运营效率数据，数量: {}", metricsList.size());
            int result = operationalEfficiencyService.batchSaveEfficiencyMetrics(metricsList);
            return SimpleResponse.success("批量保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("批量保存运营效率数据失败", e);
            return SimpleResponse.error("批量保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日运营效率
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getTodayEfficiency(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日运营效率", city);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getTodayEfficiency(city);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取今日运营效率失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的效率数据
     */
    @GetMapping("/date/{city}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getEfficiencyByDate(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的运营效率", city, date);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getEfficiencyByDate(city, date);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取指定日期效率数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定时间范围的运营效率
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getEfficiencyByRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的运营效率", city, startDate, endDate);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getEfficiencyByCity(city, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取运营效率失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员效率数据
     */
    @GetMapping("/courier/{courierId}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getEfficiencyByCourier(
            @PathVariable Integer courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求配送员 {} 时间范围 {} 到 {} 的效率数据", courierId, startDate, endDate);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getEfficiencyByCourier(courierId, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送员效率数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取区域效率数据
     */
    @GetMapping("/region/{regionId}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getEfficiencyByRegion(
            @PathVariable Integer regionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求区域 {} 时间范围 {} 到 {} 的效率数据", regionId, startDate, endDate);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getEfficiencyByRegion(regionId, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取区域效率数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 多条件查询效率数据
     */
    @GetMapping("/search")
    public SimpleResponse<List<OperationalEfficiencyDTO>> searchEfficiency(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) Integer courierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("多条件查询效率数据: city={}, regionId={}, courierId={}, startDate={}, endDate={}",
                    city, regionId, courierId, startDate, endDate);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getEfficiencyByConditions(
                    city, regionId, courierId, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("多条件查询效率数据失败", e);
            return SimpleResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市效率趋势
     */
    @GetMapping("/trend/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCityEfficiencyTrend(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的效率趋势", city, startDate);
            List<Map<String, Object>> data = operationalEfficiencyService.getCityEfficiencyTrend(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取城市效率趋势失败", e);
            return SimpleResponse.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员效率排行
     */
    @GetMapping("/ranking/courier")
    public SimpleResponse<List<Map<String, Object>>> getCourierEfficiencyRanking(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 从 {} 开始的配送员效率排行，限制 {} 条", city, startDate, limit);
            List<Map<String, Object>> data = operationalEfficiencyService.getCourierEfficiencyRanking(city, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送员效率排行失败", e);
            return SimpleResponse.error("获取排行数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取区域效率排行
     */
    @GetMapping("/ranking/region")
    public SimpleResponse<List<Map<String, Object>>> getRegionEfficiencyRanking(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 从 {} 开始的区域效率排行，限制 {} 条", city, startDate, limit);
            List<Map<String, Object>> data = operationalEfficiencyService.getRegionEfficiencyRanking(city, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取区域效率排行失败", e);
            return SimpleResponse.error("获取排行数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取效率分布统计
     */
    @GetMapping("/distribution/{city}")
    public SimpleResponse<List<Map<String, Object>>> getEfficiencyDistribution(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的效率分布统计", city, startDate);
            List<Map<String, Object>> data = operationalEfficiencyService.getEfficiencyDistribution(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取效率分布统计失败", e);
            return SimpleResponse.error("获取分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取低效率告警
     */
    @GetMapping("/alerts/low-efficiency")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getLowEfficiencyAlerts(
            @RequestParam double threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求低效率告警，阈值: {}, 开始日期: {}, 限制: {}", threshold, startDate, limit);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getLowEfficiencyAlerts(threshold, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取低效率告警失败", e);
            return SimpleResponse.error("获取告警数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取高效率表现
     */
    @GetMapping("/performance/high-efficiency")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getHighEfficiencyPerformance(
            @RequestParam double threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求高效率表现，阈值: {}, 开始日期: {}, 限制: {}", threshold, startDate, limit);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getHighEfficiencyPerformance(threshold, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取高效率表现失败", e);
            return SimpleResponse.error("获取表现数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取运营效率汇总统计
     */
    @GetMapping("/summary/{city}")
    public SimpleResponse<Map<String, Object>> getEfficiencySummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的运营效率汇总", city, startDate);
            Map<String, Object> data = operationalEfficiencyService.getEfficiencySummary(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取运营效率汇总失败", e);
            return SimpleResponse.error("获取汇总数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新运营效率数据
     */
    @GetMapping("/latest/{city}")
    public SimpleResponse<OperationalEfficiencyDTO> getLatestEfficiency(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新运营效率数据", city);
            OperationalEfficiencyDTO data = operationalEfficiencyService.getLatestEfficiencyByCity(city);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取最新运营效率数据失败", e);
            return SimpleResponse.error("获取最新数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员最新效率数据
     */
    @GetMapping("/latest/courier/{courierId}")
    public SimpleResponse<OperationalEfficiencyDTO> getLatestEfficiencyByCourier(@PathVariable Integer courierId) {
        try {
            log.info("请求配送员 {} 的最新效率数据", courierId);
            OperationalEfficiencyDTO data = operationalEfficiencyService.getLatestEfficiencyByCourier(courierId);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送员最新效率数据失败", e);
            return SimpleResponse.error("获取最新数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市间效率对比
     */
    @GetMapping("/comparison")
    public SimpleResponse<List<Map<String, Object>>> getCityEfficiencyComparison(
            @RequestParam List<String> cities,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市间效率对比，城市: {}, 时间范围: {} 到 {}", cities, startDate, endDate);
            List<Map<String, Object>> data = operationalEfficiencyService.getCityEfficiencyComparison(cities, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取城市间效率对比失败", e);
            return SimpleResponse.error("获取对比数据失败: " + e.getMessage());
        }
    }

    /**
     * 更新运营效率数据
     */
    @PutMapping
    public SimpleResponse<String> updateEfficiencyMetrics(@RequestBody OperationalEfficiencyMetrics metrics) {
        try {
            log.info("更新运营效率数据: city={}, regionId={}, courierId={}, date={}",
                    metrics.getCity(), metrics.getRegionId(), metrics.getCourierId(), metrics.getDate());
            int result = operationalEfficiencyService.updateEfficiencyMetrics(metrics);
            return SimpleResponse.success("更新成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("更新运营效率数据失败", e);
            return SimpleResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 清理旧数据
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        try {
            log.info("清理 {} 之前的旧数据", cutoffDate);
            int result = operationalEfficiencyService.cleanupOldData(cutoffDate);
            return SimpleResponse.success("清理完成，删除行数: " + result);
        } catch (Exception e) {
            log.error("清理旧数据失败", e);
            return SimpleResponse.error("清理失败: " + e.getMessage());
        }
    }

    /**
     * 统计城市效率记录数
     */
    @GetMapping("/count/{city}")
    public SimpleResponse<Integer> countByCity(@PathVariable String city) {
        try {
            log.info("统计城市 {} 的效率记录数", city);
            int count = operationalEfficiencyService.countByCity(city);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计城市效率记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }

    /**
     * 统计配送员记录数
     */
    @GetMapping("/count/courier/{courierId}")
    public SimpleResponse<Integer> countByCourier(@PathVariable Integer courierId) {
        try {
            log.info("统计配送员 {} 的记录数", courierId);
            int count = operationalEfficiencyService.countByCourier(courierId);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计配送员记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }
}