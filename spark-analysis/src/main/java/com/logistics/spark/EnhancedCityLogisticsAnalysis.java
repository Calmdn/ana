package com.logistics.spark;

import com.logistics.spark.monitor.SparkJobMonitor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import static org.apache.spark.sql.functions.*;
import scala.collection.JavaConverters;
import java.util.Arrays;

import java.util.Properties;

/**
 * 物流数据分析系统
 *
 * 功能模块：
 * 1. 时间效率分析 - 配送时效、取件时效分析
 * 2. 空间地理分析 - 热力图、配送密度、地理覆盖
 * 3. 运营效率分析 - 快递员效率、区域负载分析
 * 4. 预测分析数据 - 趋势预测、容量规划数据
 * 5. 成本效益分析 - 成本结构、效益评估
 * 6. KPI监控指标 - 核心指标、服务质量监控
 * 7. 异常检测分析 - 时间异常、距离异常检测
 * 8. 综合报表数据 - 日报、周报、月报数据
 *
 * 同时支持HDFS存储和MySQL实时数据写入
 */
public class EnhancedCityLogisticsAnalysis {

    // MySQL连接配置
    private static Properties mysqlProps;
    private static String mysqlUrl;
    private static SparkJobMonitor jobMonitor; // 添加监控器

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("使用方法: EnhancedCityLogisticsAnalysis <deliver_path> <pickup_path> <output_path>");
            System.err.println("示例: spark-submit --class com.logistics.spark.EnhancedCityLogisticsAnalysis " +
                    "app.jar hdfs://localhost:9000/data/deliver/* hdfs://localhost:9000/data/pickup/* " +
                    "hdfs://localhost:9000/output/analysis");
            System.exit(1);
        }

        String deliverPath = args[0];
        String pickupPath = args[1];
        String outputPath = args[2];

        // 初始化MySQL连接配置
        initMySQLConfig();

        // 初始化监控器
        jobMonitor = new SparkJobMonitor(mysqlProps, mysqlUrl);

        // 初始化Spark会话
        SparkSession spark = SparkSession.builder()
                .appName("物流分析系统")
                .master("local[*]")
                .config("spark.sql.adaptive.enabled", "true")
                .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .config("spark.sql.adaptive.advisoryPartitionSizeInBytes", "128MB")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();

        Long jobId = null;
        long totalProcessedRecords = 0;
        String errorMessage = null;
        boolean success = false;

        try {
            System.out.println("=== 启动增强版物流数据分析 ===");
            System.out.println("配送数据路径: " + deliverPath);
            System.out.println("取件数据路径: " + pickupPath);
            System.out.println("输出路径: " + outputPath);

            // 🚀 开始作业监控
            jobId = jobMonitor.startJobTracking(
                    "物流分析系统",
                    deliverPath,
                    pickupPath,
                    outputPath,
                    "yyyy-MM-dd HH:mm:ss",  // 时间格式
                    2024  // 默认年份
            );

            // 加载和清洗数据
            Dataset<Row> deliverRaw = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(deliverPath);

            Dataset<Row> pickupRaw = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(pickupPath);

            long rawDeliverCount = deliverRaw.count();
            long rawPickupCount = pickupRaw.count();
            System.out.println("原始数据记录数 - 配送: " + rawDeliverCount + ", 取件: " + rawPickupCount);

            // 📊 更新初始进度
            jobMonitor.updateJobProgress(jobId, rawDeliverCount + rawPickupCount, "数据加载完成");

            // 数据清洗和转换
            Dataset<Row> deliverClean = cleanAndTransformDeliveryData(deliverRaw);
            Dataset<Row> pickupClean = cleanAndTransformPickupData(pickupRaw);

            long cleanDeliverCount = deliverClean.count();
            long cleanPickupCount = pickupClean.count();
            totalProcessedRecords = cleanDeliverCount + cleanPickupCount;

            System.out.println("清洗后数据记录数 - 配送: " + cleanDeliverCount + ", 取件: " + cleanPickupCount);

            // 📊 更新清洗后进度
            jobMonitor.updateJobProgress(jobId, totalProcessedRecords, "数据清洗完成");

            // 缓存清洗后的数据
            deliverClean.cache();
            pickupClean.cache();

            // 执行8个分析模块
            System.out.println("\n=== 开始执行分析模块 ===");

            // 1. 时间效率分析
            System.out.println("📊 执行时间效率分析...");
            generateTimeEfficiencyMetrics(deliverClean, pickupClean, outputPath + "/time_efficiency", spark);
            jobMonitor.logModuleCompletion(jobId, "时间效率分析", totalProcessedRecords);

            // 2. 空间地理分析
            System.out.println("🗺️ 执行空间地理分析...");
            generateSpatialAnalysisMetrics(deliverClean, pickupClean, outputPath + "/spatial_analysis", spark);
            jobMonitor.logModuleCompletion(jobId, "空间地理分析", totalProcessedRecords);

            // 3. 运营效率分析
            System.out.println("⚡ 执行运营效率分析...");
            generateOperationalEfficiencyMetrics(deliverClean, pickupClean, outputPath + "/operational_efficiency", spark);
            jobMonitor.logModuleCompletion(jobId, "运营效率分析", totalProcessedRecords);

            // 4. 预测分析数据
            System.out.println("🔮 生成预测分析数据...");
            generatePredictiveAnalysisData(deliverClean, pickupClean, outputPath + "/predictive_data", spark);
            jobMonitor.logModuleCompletion(jobId, "预测分析数据", totalProcessedRecords);

            // 5. 成本效益分析
            System.out.println("💰 执行成本效益分析...");
            generateCostAnalysisMetrics(deliverClean, pickupClean, outputPath + "/cost_analysis", spark);
            jobMonitor.logModuleCompletion(jobId, "成本效益分析", totalProcessedRecords);

            // 6. KPI监控指标
            System.out.println("📈 生成KPI监控指标...");
            generateKPIMetrics(deliverClean, pickupClean, outputPath + "/kpi_metrics", spark);
            jobMonitor.logModuleCompletion(jobId, "KPI监控指标", totalProcessedRecords);

            // 7. 异常检测分析
            System.out.println("🔍 执行异常检测分析...");
            generateAnomalyDetectionMetrics(deliverClean, pickupClean, outputPath + "/anomaly_detection", spark);
            jobMonitor.logModuleCompletion(jobId, "异常检测分析", totalProcessedRecords);

            // 8. 综合报表数据
            System.out.println("📋 生成综合报表数据...");
            generateComprehensiveReports(deliverClean, pickupClean, outputPath + "/comprehensive_reports", spark);
            jobMonitor.logModuleCompletion(jobId, "综合报表数据", totalProcessedRecords);

            // 🎉 作业成功完成
            success = true;
            System.out.println("\n=== 所有分析模块执行完成 ===");
            System.out.println("增强版物流数据分析成功完成!");
            System.out.println("结果已保存到: " + outputPath);
            System.out.println("数据已同步到MySQL数据库");

        } catch (Exception e) {
            errorMessage = "分析过程中发生错误: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            success = false;
        } finally {
            // 📊 完成作业监控
            jobMonitor.completeJobTracking(jobId, success, totalProcessedRecords, errorMessage);

            spark.stop();

            if (!success) {
                System.exit(1);
            }
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
        mysqlUrl = "jdbc:mysql://localhost:3306/logistics_db";
    }

    /**
     * 清洗和转换配送数据
     * deliver数据格式：order_id, region_id, city, courier_id, lng, lat, aoi_id, aoi_type,
     * accept_time, accept_gps_time, accept_gps_lng, accept_gps_lat,
     * delivery_time, delivery_gps_time, delivery_gps_lng, delivery_gps_lat, ds
     */

    private static Dataset<Row> cleanAndTransformDeliveryData(Dataset<Row> deliverRaw) {
        return deliverRaw
                .filter(col("order_id").isNotNull())
                .filter(col("city").isNotNull())
                .filter(col("courier_id").isNotNull())
                .filter(col("delivery_time").isNotNull())
                // 在原时间字符串前添加年份
                .withColumn("delivery_time_with_year", concat(lit("2025-"), col("delivery_time")))
                .withColumn("accept_time_with_year", concat(lit("2025-"), col("accept_time")))
                .withColumn("delivery_gps_time_with_year", concat(lit("2025-"), col("delivery_gps_time")))
                .withColumn("accept_gps_time_with_year", concat(lit("2025-"), col("accept_gps_time")))
                // 使用完整的时间格式进行转换
                .withColumn("delivery_time", to_timestamp(col("delivery_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("accept_time", to_timestamp(col("accept_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("delivery_gps_time", to_timestamp(col("delivery_gps_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("accept_gps_time", to_timestamp(col("accept_gps_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                // 删除临时列
                .drop("delivery_time_with_year", "accept_time_with_year",
                        "delivery_gps_time_with_year", "accept_gps_time_with_year")
                .withColumn("date", to_date(col("delivery_time")))
                .withColumn("hour", hour(col("delivery_time")))
                // 计算配送时长（从接单到完成配送）
                .withColumn("delivery_duration_hours",
                        (unix_timestamp(col("delivery_time")).minus(unix_timestamp(col("accept_time")))).divide(3600))
                // 计算配送距离（GPS坐标之间的距离）
                .withColumn("delivery_distance_km",
                        calculateDistance(col("accept_gps_lng"), col("accept_gps_lat"),
                                col("delivery_gps_lng"), col("delivery_gps_lat")))
                .filter(col("delivery_duration_hours").between(0, 72)) // 过滤异常时间
                .filter(col("delivery_distance_km").between(0, 500));  // 过滤异常距离
    }

    /**
     * 清洗和转换取件数据
     * pickup数据格式：order_id, region_id, city, courier_id, accept_time, time_window_start, time_window_end,
     * lng, lat, aoi_id, aoi_type, pickup_time, pickup_gps_time, pickup_gps_lng, pickup_gps_lat,
     * accept_gps_time, accept_gps_lng, accept_gps_lat, ds
     */
    private static Dataset<Row> cleanAndTransformPickupData(Dataset<Row> pickupRaw) {
        return pickupRaw
                .filter(col("order_id").isNotNull())
                .filter(col("city").isNotNull())
                .filter(col("courier_id").isNotNull())
                .filter(col("pickup_time").isNotNull())
                // 在原时间字符串前添加年份，将"05-18 08:16:00"转换为"2025-05-18 08:16:00"
                .withColumn("pickup_time_with_year", concat(lit("2025-"), col("pickup_time")))
                .withColumn("accept_time_with_year", concat(lit("2025-"), col("accept_time")))
                .withColumn("pickup_gps_time_with_year", concat(lit("2025-"), col("pickup_gps_time")))
                .withColumn("accept_gps_time_with_year", concat(lit("2025-"), col("accept_gps_time")))
                .withColumn("time_window_start_with_year", concat(lit("2025-"), col("time_window_start")))
                .withColumn("time_window_end_with_year", concat(lit("2025-"), col("time_window_end")))
                // 使用完整的时间格式进行转换
                .withColumn("pickup_time", to_timestamp(col("pickup_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("accept_time", to_timestamp(col("accept_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("pickup_gps_time", to_timestamp(col("pickup_gps_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("accept_gps_time", to_timestamp(col("accept_gps_time_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("time_window_start", to_timestamp(col("time_window_start_with_year"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("time_window_end", to_timestamp(col("time_window_end_with_year"), "yyyy-MM-dd HH:mm:ss"))
                // 删除临时列
                .drop("pickup_time_with_year", "accept_time_with_year",
                        "pickup_gps_time_with_year", "accept_gps_time_with_year",
                        "time_window_start_with_year", "time_window_end_with_year")
                .withColumn("date", to_date(col("pickup_time")))
                .withColumn("hour", hour(col("pickup_time")))
                // 计算取件时长（从接单到完成取件）
                .withColumn("pickup_duration_hours",
                        (unix_timestamp(col("pickup_time")).minus(unix_timestamp(col("accept_time")))).divide(3600))
                // 计算取件距离
                .withColumn("pickup_distance_km",
                        calculateDistance(col("accept_gps_lng"), col("accept_gps_lat"),
                                col("pickup_gps_lng"), col("pickup_gps_lat")))
                // 检查是否在时间窗口内完成
                .withColumn("within_time_window",
                        col("pickup_time").between(col("time_window_start"), col("time_window_end")))
                .filter(col("pickup_duration_hours").between(0, 48))
                .filter(col("pickup_distance_km").between(0, 300));
    }

    /**
     * 计算两点间距离（简化版，使用haversine公式）
     */
    private static org.apache.spark.sql.Column calculateDistance(
            org.apache.spark.sql.Column lng1, org.apache.spark.sql.Column lat1,
            org.apache.spark.sql.Column lng2, org.apache.spark.sql.Column lat2) {
        // 简化距离计算，实际项目中建议使用更精确的公式
        return sqrt(
                pow(lng2.minus(lng1).multiply(lit(111.0)), lit(2))
                        .plus(pow(lat2.minus(lat1).multiply(lit(111.0)), lit(2)))
        );
    }

    /**
     * 1. 时间效率分析 - 修正字段名称
     */
    private static void generateTimeEfficiencyMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 配送时间效率分析
            Dataset<Row> deliveryTimeMetrics = delivery
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_deliveries"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            percentile_approx(col("delivery_duration_hours"), lit(0.5), lit(10000)).alias("median_delivery_time"),
                            percentile_approx(col("delivery_duration_hours"), lit(0.95), lit(10000)).alias("p95_delivery_time"),
                            sum(when(col("delivery_duration_hours").leq(2), 1).otherwise(0)).alias("fast_deliveries"),
                            sum(when(col("delivery_duration_hours").between(2, 8), 1).otherwise(0)).alias("normal_deliveries"),
                            sum(when(col("delivery_duration_hours").gt(8), 1).otherwise(0)).alias("slow_deliveries")
                    )
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_deliveries")))
                    .withColumn("slow_delivery_rate", col("slow_deliveries").divide(col("total_deliveries")));

            // 取件时间效率分析
            Dataset<Row> pickupTimeMetrics = pickup
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_pickups"),
                            avg("pickup_duration_hours").alias("avg_pickup_time"),
                            percentile_approx(col("pickup_duration_hours"), lit(0.5), lit(10000)).alias("median_pickup_time"),
                            percentile_approx(col("pickup_duration_hours"), lit(0.95), lit(10000)).alias("p95_pickup_time"),
                            sum(when(col("pickup_duration_hours").leq(1), 1).otherwise(0)).alias("fast_pickups"),
                            sum(when(col("pickup_duration_hours").between(1, 4), 1).otherwise(0)).alias("normal_pickups"),
                            sum(when(col("pickup_duration_hours").gt(4), 1).otherwise(0)).alias("slow_pickups"),
                            // 新增：时间窗口遵守率
                            sum(when(col("within_time_window").equalTo(true), 1).otherwise(0)).alias("on_time_pickups")
                    )
                    .withColumn("fast_pickup_rate", col("fast_pickups").divide(col("total_pickups")))
                    .withColumn("slow_pickup_rate", col("slow_pickups").divide(col("total_pickups")))
                    .withColumn("on_time_pickup_rate", col("on_time_pickups").divide(col("total_pickups")));

            // 合并配送和取件数据
            Dataset<Row> combinedTimeMetrics = deliveryTimeMetrics
                    .join(pickupTimeMetrics,
                            JavaConverters.asScalaIteratorConverter(
                                    Arrays.asList("city", "date", "hour").iterator()
                            ).asScala().toSeq(),
                            "full_outer")
                    .na().fill(0);

            // 保存到HDFS
            deliveryTimeMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/delivery_time_metrics");

            pickupTimeMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/pickup_time_metrics");

            // 写入MySQL
            writeTimeEfficiencyToMySQL(combinedTimeMetrics);

            System.out.println("时间效率分析完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("时间效率分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 2. 空间地理分析 - 修正字段名称
     */
    private static void generateSpatialAnalysisMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 配送空间分析 - 使用delivery_gps_lng/lat
            Dataset<Row> deliverySpatialMetrics = delivery
                    .filter(col("delivery_gps_lng").isNotNull().and(col("delivery_gps_lat").isNotNull()))
                    .withColumn("lng_grid", floor(col("delivery_gps_lng").multiply(100)).divide(100))
                    .withColumn("lat_grid", floor(col("delivery_gps_lat").multiply(100)).divide(100))
                    .groupBy("city", "date", "lng_grid", "lat_grid")
                    .agg(
                            count("order_id").alias("delivery_count"),
                            countDistinct("courier_id").alias("unique_couriers"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            avg("delivery_distance_km").alias("avg_delivery_distance")
                    )
                    .withColumn("delivery_density", col("delivery_count").divide(0.01 * 0.01));

            // 取件空间分析 - 使用pickup_gps_lng/lat
            Dataset<Row> pickupSpatialMetrics = pickup
                    .filter(col("pickup_gps_lng").isNotNull().and(col("pickup_gps_lat").isNotNull()))
                    .withColumn("lng_grid", floor(col("pickup_gps_lng").multiply(100)).divide(100))
                    .withColumn("lat_grid", floor(col("pickup_gps_lat").multiply(100)).divide(100))
                    .groupBy("city", "date", "lng_grid", "lat_grid")
                    .agg(
                            count("order_id").alias("pickup_count"),
                            countDistinct("courier_id").alias("unique_couriers"),
                            avg("pickup_duration_hours").alias("avg_pickup_time"),
                            avg("pickup_distance_km").alias("avg_pickup_distance")
                    )
                    .withColumn("pickup_density", col("pickup_count").divide(0.01 * 0.01));

            // AOI区域覆盖分析
            Dataset<Row> regionCoverageMetrics = delivery
                    .groupBy("city", "date", "aoi_id", "aoi_type")
                    .agg(
                            count("order_id").alias("orders_in_aoi"),
                            countDistinct("courier_id").alias("couriers_in_aoi"),
                            avg("delivery_duration_hours").alias("avg_aoi_delivery_time")
                    )
                    .withColumn("orders_per_courier", col("orders_in_aoi").divide(col("couriers_in_aoi")));

            // 保存到HDFS
            deliverySpatialMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/delivery_spatial_metrics");

            pickupSpatialMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/pickup_spatial_metrics");

            regionCoverageMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/region_coverage_metrics");

            // 写入MySQL
            writeSpatialAnalysisToMySQL(deliverySpatialMetrics, regionCoverageMetrics);

            System.out.println("空间地理分析完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("空间地理分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 3. 运营效率分析 - 修正字段名称
     */
    private static void generateOperationalEfficiencyMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 快递员效率分析
            Dataset<Row> courierEfficiencyMetrics = delivery
                    .groupBy("city", "region_id", "courier_id", "date")
                    .agg(
                            count("order_id").alias("total_orders"),
                            countDistinct("aoi_id").alias("unique_aoi_served"),
                            sum("delivery_distance_km").alias("total_distance"),
                            sum("delivery_duration_hours").alias("total_working_hours"),
                            avg("delivery_duration_hours").alias("avg_delivery_time")
                    )
                    .withColumn("orders_per_hour", col("total_orders").divide(col("total_working_hours")))
                    .withColumn("distance_per_order", col("total_distance").divide(col("total_orders")))
                    .withColumn("efficiency_score",
                            col("orders_per_hour").multiply(0.6)
                                    .plus(lit(1).divide(col("avg_delivery_time")).multiply(0.4)))
                    .filter(col("total_working_hours").gt(0));

            // 区域负载分析
            Dataset<Row> regionLoadMetrics = delivery
                    .groupBy("city", "region_id", "date")
                    .agg(
                            count("order_id").alias("total_region_orders"),
                            countDistinct("courier_id").alias("active_couriers"),
                            countDistinct("aoi_id").alias("served_aois"),
                            avg("delivery_duration_hours").alias("avg_region_delivery_time")
                    )
                    .withColumn("orders_per_courier", col("total_region_orders").divide(col("active_couriers")))
                    .withColumn("orders_per_aoi", col("total_region_orders").divide(col("served_aois")))
                    .withColumn("region_load_score",
                            col("orders_per_courier").multiply(0.6).plus(col("orders_per_aoi").multiply(0.4)));

            // 保存到HDFS
            courierEfficiencyMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/courier_efficiency_metrics");

            regionLoadMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/region_load_metrics");

            // 写入MySQL
            writeOperationalEfficiencyToMySQL(courierEfficiencyMetrics, regionLoadMetrics);

            System.out.println("运营效率分析完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("运营效率分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 4. 预测分析数据生成 - 修正时间序列分析
     */
    private static void generatePredictiveAnalysisData(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 时间序列趋势数据 - 基于ds字段进行时间序列分析
            Dataset<Row> timeSeriesTrends = delivery
                    .withColumn("ds_date", to_date(col("ds"), "MMdd")) // ds格式为518 -> 5/18
                    .groupBy("city", "ds_date", "hour")
                    .agg(
                            count("order_id").alias("order_volume"),
                            countDistinct("courier_id").alias("courier_count"),
                            avg("delivery_duration_hours").alias("avg_duration"),
                            sum("delivery_distance_km").alias("total_distance")
                    )
                    .withColumn("volume_trend", lag("order_volume", 1).over(
                            Window.partitionBy("city").orderBy(
                                    unix_timestamp(col("ds_date")).plus(col("hour").multiply(3600))
                            )))
                    .withColumn("efficiency_score",
                            col("order_volume").divide(col("courier_count").multiply(col("avg_duration"))))
                    .withColumn("data_type", lit("HOURLY"))
                    .withColumn("region_id", lit(null).cast("string"));

            // 容量规划数据
            Dataset<Row> capacityPlanningData = delivery
                    .withColumn("ds_date", to_date(col("ds"), "MMdd"))
                    .groupBy("city", "region_id", "ds_date")
                    .agg(
                            count("order_id").alias("daily_orders"),
                            countDistinct("courier_id").alias("required_couriers"),
                            max("hour").alias("peak_hour"),
                            sum("delivery_distance_km").alias("total_daily_distance")
                    )
                    .withColumn("orders_per_courier_day", col("daily_orders").divide(col("required_couriers")))
                    .withColumn("capacity_utilization",
                            when(col("orders_per_courier_day").gt(30), lit("HIGH"))
                                    .when(col("orders_per_courier_day").gt(20), lit("MEDIUM"))
                                    .otherwise(lit("LOW")))
                    .withColumn("data_type", lit("DAILY"));

            // 保存到HDFS
            timeSeriesTrends.write()
                    .mode("overwrite")
                    .partitionBy("city")
                    .parquet(outputPath + "/time_series_trends");

            capacityPlanningData.write()
                    .mode("overwrite")
                    .partitionBy("city")
                    .parquet(outputPath + "/capacity_planning_data");

            // 写入MySQL
            writePredictiveAnalysisToMySQL(timeSeriesTrends, capacityPlanningData);

            System.out.println("预测分析数据生成完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("预测分析数据生成失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 5. 成本效益分析 - 根据实际数据结构调整
     */
    private static void generateCostAnalysisMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 成本结构分析
            Dataset<Row> costStructureMetrics = delivery
                    .withColumn("fuel_cost", col("delivery_distance_km").multiply(0.8)) // 每公里0.8元油费
                    .withColumn("time_cost", col("delivery_duration_hours").multiply(25)) // 每小时25元人工成本
                    .withColumn("total_delivery_cost", col("fuel_cost").plus(col("time_cost")))
                    .groupBy("city", "region_id", "date")
                    .agg(
                            sum("total_delivery_cost").alias("total_cost"),
                            sum("fuel_cost").alias("total_fuel_cost"),
                            sum("time_cost").alias("total_time_cost"),
                            count("order_id").alias("total_orders"),
                            sum("delivery_distance_km").alias("total_distance")
                    )
                    .withColumn("cost_per_order", col("total_cost").divide(col("total_orders")))
                    .withColumn("cost_per_km", col("total_cost").divide(col("total_distance")))
                    .withColumn("fuel_cost_ratio", col("total_fuel_cost").divide(col("total_cost")))
                    .withColumn("analysis_type", lit("REGION"));

            // 效益评估
            Dataset<Row> efficiencyROI = delivery
                    .groupBy("city", "courier_id", "date")
                    .agg(
                            count("order_id").alias("completed_orders"),
                            sum("delivery_distance_km").alias("distance_covered"),
                            sum("delivery_duration_hours").alias("working_hours")
                    )
                    .withColumn("productivity_score",
                            col("completed_orders").divide(col("working_hours")))
                    .withColumn("efficiency_rating",
                            when(col("productivity_score").gt(8), lit("EXCELLENT"))
                                    .when(col("productivity_score").gt(5), lit("GOOD"))
                                    .when(col("productivity_score").gt(3), lit("AVERAGE"))
                                    .otherwise(lit("NEEDS_IMPROVEMENT")))
                    .withColumn("analysis_type", lit("COURIER"));

            // 保存到HDFS
            costStructureMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/cost_structure_metrics");

            efficiencyROI.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/efficiency_roi_metrics");

            // 写入MySQL
            writeCostAnalysisToMySQL(costStructureMetrics, efficiencyROI);

            System.out.println("成本效益分析完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("成本效益分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 6. KPI监控指标生成 - 调整KPI计算逻辑
     */
    private static void generateKPIMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 核心KPI指标
            Dataset<Row> coreKPIs = delivery
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_orders"),
                            countDistinct("courier_id").alias("active_couriers"),
                            countDistinct("aoi_id").alias("coverage_aois"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            sum(when(col("delivery_duration_hours").leq(2), 1).otherwise(0)).alias("fast_deliveries")
                    )
                    .withColumn("orders_per_courier", col("total_orders").divide(col("active_couriers")))
                    .withColumn("orders_per_aoi", col("total_orders").divide(col("coverage_aois")))
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_orders")))
                    .withColumn("efficiency_score",
                            col("orders_per_courier").multiply(0.4)
                                    .plus(col("fast_delivery_rate").multiply(0.6)))
                    .withColumn("kpi_timestamp", current_timestamp());

            // 服务质量KPI - 包含取件服务
            Dataset<Row> serviceQualityKPIs = pickup
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_pickups"),
                            sum(when(col("within_time_window").equalTo(true), 1).otherwise(0)).alias("on_time_pickups"),
                            avg("pickup_duration_hours").alias("avg_pickup_duration")
                    )
                    .withColumn("on_time_pickup_rate", col("on_time_pickups").divide(col("total_pickups")))
                    .withColumn("pickup_service_score", col("on_time_pickup_rate").multiply(100));

            // 保存到HDFS
            coreKPIs.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/core_kpis");

            serviceQualityKPIs.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/service_quality_kpis");

            // 写入MySQL实时KPI表
            writeKPIsToMySQL(coreKPIs, serviceQualityKPIs, spark);

            System.out.println("KPI监控指标生成完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("KPI监控指标生成失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 7. 异常检测分析 - 调整异常检测阈值
     */
    private static void generateAnomalyDetectionMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 配送时间异常检测 - 根据实际数据调整阈值
            Dataset<Row> deliveryTimeAnomalies = delivery
                    .withColumn("delivery_time_zscore",
                            (col("delivery_duration_hours").minus(lit(4.0))).divide(lit(3.0))) // 均值4小时，标准差3小时
                    .filter(abs(col("delivery_time_zscore")).gt(2))
                    .select("order_id", "city", "courier_id", "delivery_duration_hours", "delivery_time_zscore", "date")
                    .withColumn("anomaly_type", lit("DELIVERY_TIME_ANOMALY"))
                    .withColumn("anomaly_severity",
                            when(abs(col("delivery_time_zscore")).gt(3), lit("HIGH"))
                                    .when(abs(col("delivery_time_zscore")).gt(2.5), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 取件时间异常检测
            Dataset<Row> pickupTimeAnomalies = pickup
                    .withColumn("pickup_time_zscore",
                            (col("pickup_duration_hours").minus(lit(2.0))).divide(lit(1.5))) // 均值2小时，标准差1.5小时
                    .filter(abs(col("pickup_time_zscore")).gt(2))
                    .select("order_id", "city", "courier_id", "pickup_duration_hours", "pickup_time_zscore", "date")
                    .withColumn("anomaly_type", lit("PICKUP_TIME_ANOMALY"))
                    .withColumn("anomaly_severity",
                            when(abs(col("pickup_time_zscore")).gt(3), lit("HIGH"))
                                    .when(abs(col("pickup_time_zscore")).gt(2.5), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 距离异常检测
            Dataset<Row> distanceAnomalies = delivery
                    .withColumn("distance_zscore",
                            (col("delivery_distance_km").minus(lit(10))).divide(lit(8))) // 均值10km，标准差8km
                    .filter(abs(col("distance_zscore")).gt(2))
                    .select("order_id", "city", "courier_id", "delivery_distance_km", "distance_zscore", "date")
                    .withColumn("anomaly_type", lit("DISTANCE_ANOMALY"))
                    .withColumn("anomaly_severity",
                            when(abs(col("distance_zscore")).gt(3), lit("HIGH"))
                                    .when(abs(col("distance_zscore")).gt(2.5), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 保存到HDFS
            deliveryTimeAnomalies.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/delivery_time_anomalies");

            pickupTimeAnomalies.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/pickup_time_anomalies");

            distanceAnomalies.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/distance_anomalies");

            // 写入MySQL异常告警表
            writeAnomaliesToMySQL(deliveryTimeAnomalies, pickupTimeAnomalies, distanceAnomalies, spark);

            System.out.println("异常检测分析完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("异常检测分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 8. 综合报表数据生成 - 根据数据结构调整
     */
    private static void generateComprehensiveReports(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 日报数据
            Dataset<Row> dailyReports = delivery
                    .groupBy("city", "region_id", "date")
                    .agg(
                            count("order_id").alias("total_deliveries"),
                            countDistinct("courier_id").alias("active_couriers"),
                            countDistinct("aoi_id").alias("served_aois"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            sum("delivery_distance_km").alias("total_distance"),
                            sum(when(col("delivery_duration_hours").leq(2), 1).otherwise(0)).alias("fast_deliveries")
                    )
                    .withColumn("avg_orders_per_courier", col("total_deliveries").divide(col("active_couriers")))
                    .withColumn("avg_distance_per_order", col("total_distance").divide(col("total_deliveries")))
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_deliveries")))
                    .withColumn("report_type", lit("DAILY"))
                    .withColumn("generated_at", current_timestamp());

            // 取件日报数据
            Dataset<Row> pickupDailyReports = pickup
                    .groupBy("city", "region_id", "date")
                    .agg(
                            count("order_id").alias("total_pickups"),
                            countDistinct("courier_id").alias("active_pickup_couriers"),
                            avg("pickup_duration_hours").alias("avg_pickup_time"),
                            sum(when(col("within_time_window").equalTo(true), 1).otherwise(0)).alias("on_time_pickups")
                    )
                    .withColumn("on_time_pickup_rate", col("on_time_pickups").divide(col("total_pickups")))
                    .withColumn("report_type", lit("PICKUP_DAILY"))
                    .withColumn("generated_at", current_timestamp());

            // 保存到HDFS
            dailyReports.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/daily_reports");

            pickupDailyReports.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/pickup_daily_reports");

            // 写入MySQL
            writeComprehensiveReportsToMySQL(dailyReports, pickupDailyReports);

            System.out.println("综合报表数据生成完成（HDFS + MySQL）");

        } catch (Exception e) {
            System.err.println("综合报表数据生成失败: " + e.getMessage());
            throw e;
        }
    }

    // ========== MySQL写入方法（修正字段匹配问题） ==========

    /**
     * 写入时间效率数据到MySQL
     */
    private static void writeTimeEfficiencyToMySQL(Dataset<Row> timeMetrics) {
        try {
            //Dataset<Row> recentData = timeMetrics
             //       .filter(col("date").geq(date_sub(current_date(), 7)));

            //recentData.write()
            timeMetrics.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "time_efficiency_metrics", mysqlProps);

            System.out.println("时间效率数据已写入MySQL");

            System.out.println("待写入记录数: " + timeMetrics.count());
        } catch (Exception e) {
            System.err.println("时间效率数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入空间地理分析数据到MySQL
     */
    private static void writeSpatialAnalysisToMySQL(Dataset<Row> spatialMetrics, Dataset<Row> regionMetrics) {
        try {
//            Dataset<Row> recentSpatialData = spatialMetrics
//                    .filter(col("date").geq(date_sub(current_date(), 7)));

            spatialMetrics.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "spatial_analysis_metrics", mysqlProps);

            System.out.println("空间地理分析数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("空间地理分析数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入运营效率数据到MySQL
     */
    private static void writeOperationalEfficiencyToMySQL(Dataset<Row> courierMetrics, Dataset<Row> regionMetrics) {
        try {
//            Dataset<Row> recentData = courierMetrics
//                    .filter(col("date").geq(date_sub(current_date(), 7)));

            courierMetrics.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "operational_efficiency_metrics", mysqlProps);

            System.out.println("✅ 运营效率数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 运营效率数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入预测分析数据到MySQL
     */
    private static void writePredictiveAnalysisToMySQL(Dataset<Row> timeSeriesData, Dataset<Row> capacityData) {
        try {
            timeSeriesData.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "predictive_analysis_data", mysqlProps);

            System.out.println("✅ 预测分析数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 预测分析数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入成本分析数据到MySQL
     */
    private static void writeCostAnalysisToMySQL(Dataset<Row> costMetrics, Dataset<Row> efficiencyMetrics) {
        try {
            Dataset<Row> recentData = costMetrics
                    .filter(col("date").geq(date_sub(current_date(), 7)));

            recentData.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "cost_analysis_metrics", mysqlProps);

            System.out.println("✅ 成本分析数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 成本分析数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入综合报表数据到MySQL
     */
    private static void writeComprehensiveReportsToMySQL(Dataset<Row> dailyReports, Dataset<Row> pickupReports) {
        try {
//            Dataset<Row> recentData = dailyReports
//                    .filter(col("date").geq(date_sub(current_date(), 30)));

            dailyReports.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "comprehensive_reports", mysqlProps);

            System.out.println("✅ 综合报表数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 综合报表数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入KPI数据到MySQL
     */
    private static void writeKPIsToMySQL(Dataset<Row> coreKPIs, Dataset<Row> serviceQualityKPIs, SparkSession spark) {
        try {
//            Dataset<Row> recentKPIs = coreKPIs
//                    .filter(col("date").geq(date_sub(current_date(), 7)));

            coreKPIs.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "realtime_kpi", mysqlProps);

            System.out.println("✅ KPI数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ KPI数据MySQL写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入异常数据到MySQL - 修正参数
     */
    private static void writeAnomaliesToMySQL(Dataset<Row> deliveryTimeAnomalies, Dataset<Row> pickupTimeAnomalies, Dataset<Row> distanceAnomalies, SparkSession spark) {
        try {
            // 统一异常数据格式
            Dataset<Row> allAnomalies = deliveryTimeAnomalies
                    .select(
                            col("order_id"), col("city"), col("courier_id"),
                            col("anomaly_type"), col("anomaly_severity"), col("date")
                    )
                    .union(pickupTimeAnomalies.select(
                            col("order_id"), col("city"), col("courier_id"),
                            col("anomaly_type"), col("anomaly_severity"), col("date")
                    ))
                    .union(distanceAnomalies.select(
                            col("order_id"), col("city"), col("courier_id"),
                            col("anomaly_type"), col("anomaly_severity"), col("date")
                    ));

            allAnomalies.write()
                    .mode("append")
                    .jdbc(mysqlUrl, "anomaly_alerts", mysqlProps);

            System.out.println("✅ 异常告警数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 异常数据MySQL写入失败: " + e.getMessage());
        }
    }
}