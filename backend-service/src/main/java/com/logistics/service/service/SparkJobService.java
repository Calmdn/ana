package com.logistics.service.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SparkJobService {

    @Value("${logistics.spark.home}")
    private String sparkHome;

    @Value("${logistics.spark.app-jar}")
    private String appJar;

    @Value("${logistics.spark.master}")
    private String master;

    @Value("${logistics.hdfs.input.deliver-path}")
    private String deliverPath;

    @Value("${logistics.hdfs.input.pickup-path}")
    private String pickupPath;

    @Value("${logistics.hdfs.output.base-path}")
    private String outputPath;

    /**
     * 执行物流分析任务
     */
    public String executeAnalysisJob() {
        try {
            log.info("🚀 开始执行Spark物流分析任务...");
            log.info("📦 Spark Home: {}", sparkHome);
            log.info("📄 App JAR: {}", appJar);
            log.info("🎯 Master: {}", master);

            CountDownLatch latch = new CountDownLatch(1);

            SparkAppHandle handle = new SparkLauncher()
                    .setSparkHome(sparkHome)
                    .setAppResource(appJar)
                    .setMainClass("com.logistics.spark.EnhancedCityLogisticsAnalysis")
                    .setMaster(master)
                    .setConf(SparkLauncher.EXECUTOR_MEMORY, "2g")
                    .setConf(SparkLauncher.DRIVER_MEMORY, "1g")
                    .setConf(SparkLauncher.EXECUTOR_CORES, "2")
                    .addAppArgs(deliverPath, pickupPath, outputPath)
                    .setVerbose(true)
                    .startApplication(new SparkAppHandle.Listener() {
                        @Override
                        public void stateChanged(SparkAppHandle handle) {
                            log.info("📊 Spark任务状态: {}", handle.getState());
                            if (handle.getState().isFinal()) {
                                latch.countDown();
                            }
                        }

                        @Override
                        public void infoChanged(SparkAppHandle handle) {
                            log.debug("ℹ️ Spark信息更新: {}", handle.getState());
                        }
                    });

            log.info("✅ Spark应用已启动，Application ID: {}", handle.getAppId());

            // 等待任务完成，最多等待30分钟
            boolean finished = latch.await(30, TimeUnit.MINUTES);

            if (!finished) {
                log.warn("⚠️ Spark任务执行超时");
                return "任务执行超时，Application ID: " + handle.getAppId();
            }

            if (handle.getState() == SparkAppHandle.State.FINISHED) {
                log.info("🎉 Spark任务执行成功！");
                return "✅ 任务执行成功，Application ID: " + handle.getAppId();
            } else {
                log.error("❌ Spark任务执行失败，状态: {}", handle.getState());
                return "❌ 任务执行失败，状态: " + handle.getState();
            }

        } catch (IOException | InterruptedException e) {
            log.error("💥 执行Spark任务时发生异常", e);
            return "🚨 任务执行异常: " + e.getMessage();
        }
    }

    /**
     * 检查Spark环境
     */
    public String checkSparkEnvironment() {
        try {
            log.info("🔍 检查Spark环境...");

            // 简单检查配置
            if (sparkHome == null || sparkHome.trim().isEmpty()) {
                return "❌ Spark Home未配置";
            }

            if (appJar == null || appJar.trim().isEmpty()) {
                return "❌ Spark应用JAR路径未配置";
            }

            return "✅ Spark环境配置正常";

        } catch (Exception e) {
            return "❌ Spark环境检查失败: " + e.getMessage();
        }
    }
}