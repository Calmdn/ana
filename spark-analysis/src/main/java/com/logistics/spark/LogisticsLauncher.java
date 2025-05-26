package com.logistics.spark;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LogisticsLauncher {

    // 配置您的数据路径
    private static final String DELIVERY_DATA_PATH = "hdfs://localhost:9000/user/calmdn/lade/raw/deliver/*";
    private static final String PICKUP_DATA_PATH = "hdfs://localhost:9000/user/calmdn/lade/raw/pickup/*";
    private static final String OUTPUT_PATH = "hdfs://localhost:9000/user/calmdn/lade/analysis";

    public static void main(String[] args) {
        System.out.println("🚀 物流数据分析系统启动器");
        System.out.println("当前时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("启动用户: Calmdn");
        System.out.println("===============================================");

        try {
            // 显示配置信息
            System.out.println("📋 系统配置:");
            System.out.println("  配送数据路径: " + DELIVERY_DATA_PATH);
            System.out.println("  取件数据路径: " + PICKUP_DATA_PATH);
            System.out.println("  输出路径: " + OUTPUT_PATH);
            System.out.println("===============================================");

            // 准备启动参数
            String[] analysisArgs = {
                    DELIVERY_DATA_PATH,
                    PICKUP_DATA_PATH,
                    OUTPUT_PATH
            };

            // 启动分析程序
            System.out.println("🚀 启动物流数据分析程序...");
            EnhancedCityLogisticsAnalysis.main(analysisArgs);

            System.out.println("===============================================");
            System.out.println("🎉 程序执行完成！");
            System.out.println("📁 结果已保存到: " + OUTPUT_PATH);

        } catch (Exception e) {
            System.err.println("❌ 程序执行失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}