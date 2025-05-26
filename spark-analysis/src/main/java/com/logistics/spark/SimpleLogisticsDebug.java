package com.logistics.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

import java.util.Properties;

/**
 * 简化版物流数据分析 - 仅用于调试
 * 功能：读取配送数据 → 按城市统计 → 保存结果
 */
public class SimpleLogisticsDebug {

    private static String mysqlUrl;
    private static Properties mysqlProps;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("用法: <配送数据路径> <取件数据路径> <输出路径>");
            System.exit(1);
        }

        String deliverPath = args[0];
        String pickupPath = args[1];
        String outputPath = args[2];

        // 初始化Spark
        SparkSession spark = SparkSession.builder()
                .appName("简化物流分析调试")
                .master("local[*]")
                .getOrCreate();

        // 初始化MySQL配置
        initMySQLConfig();

        try {
            System.out.println("=== 开始简化调试 ===");
            System.out.println("配送数据: " + deliverPath);
            System.out.println("输出路径: " + outputPath);

            // 1. 读取原始数据
            System.out.println("\n📖 读取配送数据...");
            Dataset<Row> deliveryRaw = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(deliverPath);

            long rawCount = deliveryRaw.count();
            System.out.println("📊 原始数据记录数: " + rawCount);

            if (rawCount == 0) {
                System.out.println("❌ 原始数据为空，退出");
                return;
            }

            // 显示原始数据样本
            System.out.println("📋 原始数据样本:");
            deliveryRaw.show(5, false);
            System.out.println("📋 数据结构:");
            deliveryRaw.printSchema();

            // 2. 简单数据处理：按城市统计配送数量
            System.out.println("\n⚙️ 进行简单统计...");
            Dataset<Row> cityStats = deliveryRaw
                    .filter(col("city").isNotNull())
                    .groupBy("city")
                    .agg(
                            count("order_id").alias("total_orders"),
                            countDistinct("courier_id").alias("unique_couriers"),
                            current_date().alias("analysis_date")
                    )
                    .orderBy(desc("total_orders"));

            long statsCount = cityStats.count();
            System.out.println("📊 统计结果记录数: " + statsCount);

            if (statsCount == 0) {
                System.out.println("❌ 统计结果为空");
                return;
            }

            // 显示统计结果
            System.out.println("📋 城市统计结果:");
            cityStats.show(10, false);

            // 3. 保存到HDFS
            System.out.println("\n💾 保存到HDFS...");
            cityStats.write()
                    .mode("overwrite")
                    .parquet(outputPath + "/city_stats");

            System.out.println("✅ HDFS保存完成");

            // 4. 保存到MySQL
            System.out.println("\n💾 保存到MySQL...");
            writeCityStatsToMySQL(cityStats);

            System.out.println("🎉 简化调试完成！");

        } catch (Exception e) {
            System.err.println("❌ 错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }

    /**
     * 初始化MySQL配置
     */
    private static void initMySQLConfig() {
        mysqlProps = new Properties();
        mysqlProps.setProperty("user", "root");
        mysqlProps.setProperty("password", "root");
        mysqlProps.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        mysqlUrl = "jdbc:mysql://localhost:3306/logistics_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    /**
     * 写入城市统计到MySQL
     */
    private static void writeCityStatsToMySQL(Dataset<Row> cityStats) {
        try {
            System.out.println("🔍 MySQL写入调试:");

            // 检查数据
            long count = cityStats.count();
            System.out.println("📊 待写入记录数: " + count);

            if (count == 0) {
                System.out.println("❌ 没有数据可写入");
                return;
            }

            // 显示将要写入的数据
            System.out.println("📋 将要写入MySQL的数据:");
            cityStats.show(5, false);

            // 写入MySQL
            cityStats.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "debug_city_stats", mysqlProps);

            System.out.println("✅ MySQL写入完成");

            // 验证写入
            Dataset<Row> verification = SparkSession.getActiveSession().get().read()
                    .jdbc(mysqlUrl, "debug_city_stats", mysqlProps);

            long mysqlCount = verification.count();
            System.out.println("✅ MySQL验证 - 表中记录数: " + mysqlCount);

            if (mysqlCount > 0) {
                System.out.println("📋 MySQL中的数据:");
                verification.show(3, false);
            }

        } catch (Exception e) {
            System.err.println("❌ MySQL写入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}