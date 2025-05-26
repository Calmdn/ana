package com.logistics.service.controller;

import com.logistics.service.dto.OperationalEfficiencyDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.OperationalEfficiencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/operational-efficiency")
@CrossOrigin(origins = "*")
public class OperationalEfficiencyController {

    @Autowired
    private OperationalEfficiencyService operationalEfficiencyService;

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
     * 获取小时级别效率数据
     */
    @GetMapping("/hourly/{city}")
    public SimpleResponse<List<OperationalEfficiencyDTO>> getHourlyEfficiency(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的小时级运营效率", city, date);
            List<OperationalEfficiencyDTO> data = operationalEfficiencyService.getHourlyEfficiency(city, date);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取小时效率数据失败", e);
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
}