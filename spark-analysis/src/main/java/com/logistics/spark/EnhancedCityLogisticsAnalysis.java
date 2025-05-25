package com.logistics.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import static org.apache.spark.sql.functions.*;

import java.util.Properties;

/**
 * 增强版城市物流数据分析系统
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

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("使用方法: EnhancedCityLogisticsAnalysis <deliver_path> <pickup_path> <output_path>");
            System.err.println("示例: spark-submit --class com.logistics.spark.EnhancedCityLogisticsAnalysis " +
                    "app.jar hdfs://namenode:9000/data/deliver/* hdfs://namenode:9000/data/pickup/* " +
                    "hdfs://namenode:9000/output/analysis");
            System.exit(1);
        }

        String deliverPath = args[0];
        String pickupPath = args[1];
        String outputPath = args[2];

        // 初始化Spark会话
        SparkSession spark = SparkSession.builder()
                .appName("Enhanced-City-Logistics-Analysis")
                .config("spark.sql.adaptive.enabled", "true")
                .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .config("spark.sql.adaptive.advisoryPartitionSizeInBytes", "128MB")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();

        try {
            System.out.println("=== 启动增强版物流数据分析 ===");
            System.out.println("配送数据路径: " + deliverPath);
            System.out.println("取件数据路径: " + pickupPath);
            System.out.println("输出路径: " + outputPath);

            // 加载和清洗数据
            Dataset<Row> deliverRaw = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(deliverPath);

            Dataset<Row> pickupRaw = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(pickupPath);

            System.out.println("原始数据记录数 - 配送: " + deliverRaw.count() + ", 取件: " + pickupRaw.count());

            // 数据清洗和转换
            Dataset<Row> deliverClean = cleanAndTransformDeliveryData(deliverRaw);
            Dataset<Row> pickupClean = cleanAndTransformPickupData(pickupRaw);

            System.out.println("清洗后数据记录数 - 配送: " + deliverClean.count() + ", 取件: " + pickupClean.count());

            // 缓存清洗后的数据
            deliverClean.cache();
            pickupClean.cache();

            // 执行8个分析模块
            System.out.println("\n=== 开始执行分析模块 ===");

            // 1. 时间效率分析
            System.out.println("📊 执行时间效率分析...");
            generateTimeEfficiencyMetrics(deliverClean, pickupClean, outputPath + "/time_efficiency", spark);

            // 2. 空间地理分析
            System.out.println("🗺️ 执行空间地理分析...");
            generateSpatialAnalysisMetrics(deliverClean, pickupClean, outputPath + "/spatial_analysis", spark);

            // 3. 运营效率分析
            System.out.println("📈 执行运营效率分析...");
            generateOperationalEfficiencyMetrics(deliverClean, pickupClean, outputPath + "/operational_efficiency", spark);

            // 4. 预测分析数据
            System.out.println("🔮 生成预测分析数据...");
            generatePredictiveAnalysisData(deliverClean, pickupClean, outputPath + "/predictive_data", spark);

            // 5. 成本效益分析
            System.out.println("💰 执行成本效益分析...");
            generateCostAnalysisMetrics(deliverClean, pickupClean, outputPath + "/cost_analysis", spark);

            // 6. KPI监控指标
            System.out.println("📱 生成KPI监控指标...");
            generateKPIMetrics(deliverClean, pickupClean, outputPath + "/kpi_metrics", spark);

            // 7. 异常检测分析
            System.out.println("🚨 执行异常检测分析...");
            generateAnomalyDetectionMetrics(deliverClean, pickupClean, outputPath + "/anomaly_detection", spark);

            // 8. 综合报表数据
            System.out.println("📋 生成综合报表数据...");
            generateComprehensiveReports(deliverClean, pickupClean, outputPath + "/comprehensive_reports", spark);

            System.out.println("\n=== 所有分析模块执行完成 ===");
            System.out.println("🎉 增强版物流数据分析成功完成!");
            System.out.println("📁 结果已保存到: " + outputPath);

        } catch (Exception e) {
            System.err.println("❌ 分析过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            spark.stop();
        }
    }

    /**
     * 清洗和转换配送数据
     */
    private static Dataset<Row> cleanAndTransformDeliveryData(Dataset<Row> deliverRaw) {
        return deliverRaw
                .filter(col("order_id").isNotNull())
                .filter(col("city").isNotNull())
                .filter(col("courier_id").isNotNull())
                .withColumn("deliver_time", to_timestamp(col("deliver_time"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("finish_time", to_timestamp(col("finish_time"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("date", to_date(col("deliver_time")))
                .withColumn("hour", hour(col("deliver_time")))
                .withColumn("delivery_duration_hours",
                        (unix_timestamp(col("finish_time")).minus(unix_timestamp(col("deliver_time")))).divide(3600))
                .withColumn("delivery_distance_km", col("delivery_distance").divide(1000))
                .filter(col("delivery_duration_hours").between(0, 72)) // 过滤异常时间
                .filter(col("delivery_distance_km").between(0, 500));  // 过滤异常距离
    }

    /**
     * 清洗和转换取件数据
     */
    private static Dataset<Row> cleanAndTransformPickupData(Dataset<Row> pickupRaw) {
        return pickupRaw
                .filter(col("order_id").isNotNull())
                .filter(col("city").isNotNull())
                .filter(col("courier_id").isNotNull())
                .withColumn("pickup_time", to_timestamp(col("pickup_time"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("finish_time", to_timestamp(col("finish_time"), "yyyy-MM-dd HH:mm:ss"))
                .withColumn("date", to_date(col("pickup_time")))
                .withColumn("hour", hour(col("pickup_time")))
                .withColumn("pickup_duration_hours",
                        (unix_timestamp(col("finish_time")).minus(unix_timestamp(col("pickup_time")))).divide(3600))
                .withColumn("pickup_distance_km", col("pickup_distance").divide(1000))
                .filter(col("pickup_duration_hours").between(0, 48))
                .filter(col("pickup_distance_km").between(0, 300));
    }

    /**
     * 1. 时间效率分析
     */
    private static void generateTimeEfficiencyMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 配送时间效率分析
            Dataset<Row> deliveryTimeMetrics = delivery
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_deliveries"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            percentile_approx(col("delivery_duration_hours"), lit(0.5),lit(10000)).alias("median_delivery_time"),
                            percentile_approx(col("delivery_duration_hours"), lit(0.95),lit(10000)).alias("p95_delivery_time"),
                            sum(when(col("delivery_duration_hours").leq(1), 1).otherwise(0)).alias("fast_deliveries"),
                            sum(when(col("delivery_duration_hours").between(1, 4), 1).otherwise(0)).alias("normal_deliveries"),
                            sum(when(col("delivery_duration_hours").gt(4), 1).otherwise(0)).alias("slow_deliveries")
                    )
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_deliveries")))
                    .withColumn("slow_delivery_rate", col("slow_deliveries").divide(col("total_deliveries")));

            // 取件时间效率分析
            Dataset<Row> pickupTimeMetrics = pickup
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_pickups"),
                            avg("pickup_duration_hours").alias("avg_pickup_time"),
                            percentile_approx(col("pickup_duration_hours"), lit(0.5),lit(10000)).alias("median_pickup_time"),
                            percentile_approx(col("pickup_duration_hours"), lit(0.95),lit(10000)).alias("p95_pickup_time"),
                            sum(when(col("pickup_duration_hours").leq(0.5), 1).otherwise(0)).alias("fast_pickups"),
                            sum(when(col("pickup_duration_hours").between(0.5, 2), 1).otherwise(0)).alias("normal_pickups"),
                            sum(when(col("pickup_duration_hours").gt(2), 1).otherwise(0)).alias("slow_pickups")
                    )
                    .withColumn("fast_pickup_rate", col("fast_pickups").divide(col("total_pickups")))
                    .withColumn("slow_pickup_rate", col("slow_pickups").divide(col("total_pickups")));

            // 保存到HDFS
            deliveryTimeMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/delivery_time_metrics");

            pickupTimeMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/pickup_time_metrics");

            System.out.println("✅ 时间效率分析完成");

        } catch (Exception e) {
            System.err.println("❌ 时间效率分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 2. 空间地理分析
     */
    private static void generateSpatialAnalysisMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 配送空间分析 - 网格化热力图
            Dataset<Row> deliverySpatialMetrics = delivery
                    .withColumn("lng_grid", floor(col("lng").multiply(100)).divide(100)) // 0.01度网格
                    .withColumn("lat_grid", floor(col("lat").multiply(100)).divide(100))
                    .groupBy("city", "date", "lng_grid", "lat_grid")
                    .agg(
                            count("order_id").alias("delivery_count"),
                            countDistinct("courier_id").alias("unique_couriers"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            avg("delivery_distance_km").alias("avg_delivery_distance")
                    )
                    .withColumn("delivery_density", col("delivery_count").divide(0.01 * 0.01)); // 每平方度的配送密度

            // 区域覆盖分析
            Dataset<Row> regionCoverageMetrics = delivery
                    .groupBy("city", "date", "aoi_id")
                    .agg(
                            count("order_id").alias("orders_in_aoi"),
                            countDistinct("courier_id").alias("couriers_in_aoi"),
                            avg("delivery_duration_hours").alias("avg_aoi_delivery_time"),
                            first("aoi_name").alias("aoi_name")
                    )
                    .withColumn("orders_per_courier", col("orders_in_aoi").divide(col("couriers_in_aoi")));

            // 保存到HDFS
            deliverySpatialMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/delivery_spatial_metrics");

            regionCoverageMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/region_coverage_metrics");

            System.out.println("✅ 空间地理分析完成");

        } catch (Exception e) {
            System.err.println("❌ 空间地理分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 3. 运营效率分析
     */
    private static void generateOperationalEfficiencyMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 快递员效率分析
            Dataset<Row> courierEfficiencyMetrics = delivery
                    .groupBy("city", "region_id", "courier_id", "date", "hour")
                    .agg(
                            count("order_id").alias("total_orders"),
                            countDistinct("aoi_id").alias("unique_aoi_served"),
                            sum("delivery_distance_km").alias("total_distance"),
                            sum("delivery_duration_hours").alias("total_working_hours")
                    )
                    .withColumn("orders_per_hour", col("total_orders").divide(col("total_working_hours")))
                    .withColumn("distance_per_order", col("total_distance").divide(col("total_orders")))
                    .withColumn("working_hours", col("total_working_hours"))
                    .filter(col("total_working_hours").gt(0));

            // 区域负载分析
            Dataset<Row> regionLoadMetrics = delivery
                    .groupBy("city", "region_id", "date", "hour")
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

            System.out.println("✅ 运营效率分析完成");

        } catch (Exception e) {
            System.err.println("❌ 运营效率分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 4. 预测分析数据生成
     */
    private static void generatePredictiveAnalysisData(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 时间序列趋势数据
            Dataset<Row> timeSeriesTrends = delivery
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("order_volume"),
                            countDistinct("courier_id").alias("courier_count"),
                            avg("delivery_duration_hours").alias("avg_duration"),
                            sum("delivery_distance_km").alias("total_distance")
                    )
                    .withColumn("volume_trend", lag("order_volume", 1).over(
                            Window.partitionBy("city").orderBy(
                                    unix_timestamp(col("date")).plus(col("hour").multiply(3600))
                            )))
                    .withColumn("efficiency_score",
                            col("order_volume").divide(col("courier_count").multiply(col("avg_duration"))));

            // 容量规划数据
            Dataset<Row> capacityPlanningData = delivery
                    .groupBy("city", "region_id", "date")
                    .agg(
                            count("order_id").alias("daily_orders"),
                            countDistinct("courier_id").alias("required_couriers"),
                            max("hour").alias("peak_hour"),
                            sum("delivery_distance_km").alias("total_daily_distance")
                    )
                    .withColumn("orders_per_courier_day", col("daily_orders").divide(col("required_couriers")))
                    .withColumn("capacity_utilization",
                            when(col("orders_per_courier_day").gt(50), lit("HIGH"))
                                    .when(col("orders_per_courier_day").gt(30), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 保存到HDFS
            timeSeriesTrends.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/time_series_trends");

            capacityPlanningData.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/capacity_planning_data");

            System.out.println("✅ 预测分析数据生成完成");

        } catch (Exception e) {
            System.err.println("❌ 预测分析数据生成失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 5. 成本效益分析
     */
    private static void generateCostAnalysisMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 成本结构分析
            Dataset<Row> costStructureMetrics = delivery
                    .withColumn("fuel_cost", col("delivery_distance_km").multiply(0.5)) // 假设每公里0.5元油费
                    .withColumn("time_cost", col("delivery_duration_hours").multiply(20)) // 假设每小时20元人工成本
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
                    .withColumn("fuel_cost_ratio", col("total_fuel_cost").divide(col("total_cost")));

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
                            when(col("productivity_score").gt(10), lit("EXCELLENT"))
                                    .when(col("productivity_score").gt(7), lit("GOOD"))
                                    .when(col("productivity_score").gt(5), lit("AVERAGE"))
                                    .otherwise(lit("NEEDS_IMPROVEMENT")));

            // 保存到HDFS
            costStructureMetrics.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/cost_structure_metrics");

            efficiencyROI.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/efficiency_roi_metrics");

            System.out.println("✅ 成本效益分析完成");

        } catch (Exception e) {
            System.err.println("❌ 成本效益分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 6. KPI监控指标生成
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
                            sum(when(col("delivery_duration_hours").leq(1), 1).otherwise(0)).alias("on_time_deliveries")
                    )
                    .withColumn("orders_per_courier", col("total_orders").divide(col("active_couriers")))
                    .withColumn("orders_per_aoi", col("total_orders").divide(col("coverage_aois")))
                    .withColumn("on_time_rate", col("on_time_deliveries").divide(col("total_orders")))
                    .withColumn("efficiency_score",
                            col("orders_per_courier").multiply(0.4)
                                    .plus(col("on_time_rate").multiply(0.6)))
                    .withColumn("kpi_timestamp", current_timestamp());

            // 服务质量KPI
            Dataset<Row> serviceQualityKPIs = delivery
                    .groupBy("city", "date", "hour")
                    .agg(
                            count("order_id").alias("total_deliveries"),
                            sum(when(col("delivery_duration_hours").leq(1), 1).otherwise(0)).alias("fast_deliveries"),
                            sum(when(col("delivery_duration_hours").between(1, 4), 1).otherwise(0)).alias("normal_deliveries"),
                            sum(when(col("delivery_duration_hours").gt(4), 1).otherwise(0)).alias("slow_deliveries"),
                            avg("delivery_duration_hours").alias("avg_delivery_duration")
                    )
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_deliveries")))
                    .withColumn("slow_delivery_rate", col("slow_deliveries").divide(col("total_deliveries")))
                    .withColumn("service_score",
                            col("fast_delivery_rate").multiply(100).minus(col("slow_delivery_rate").multiply(50)));

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

            System.out.println("✅ KPI监控指标生成完成");

        } catch (Exception e) {
            System.err.println("❌ KPI监控指标生成失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 7. 异常检测分析
     */
    private static void generateAnomalyDetectionMetrics(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 时间异常检测
            Dataset<Row> timeAnomalies = delivery
                    .withColumn("delivery_time_zscore",
                            (col("delivery_duration_hours").minus(lit(2.5))).divide(lit(1.5))) // 假设均值2.5小时，标准差1.5小时
                    .filter(abs(col("delivery_time_zscore")).gt(2)) // Z-score > 2 视为异常
                    .select("order_id", "city", "courier_id", "delivery_duration_hours", "delivery_time_zscore", "date")
                    .withColumn("anomaly_type", lit("TIME_ANOMALY"))
                    .withColumn("anomaly_severity",
                            when(abs(col("delivery_time_zscore")).gt(3), lit("HIGH"))
                                    .when(abs(col("delivery_time_zscore")).gt(2.5), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 距离异常检测
            Dataset<Row> distanceAnomalies = delivery
                    .withColumn("distance_zscore",
                            (col("delivery_distance_km").minus(lit(15))).divide(lit(10))) // 假设均值15km，标准差10km
                    .filter(abs(col("distance_zscore")).gt(2))
                    .select("order_id", "city", "courier_id", "delivery_distance_km", "distance_zscore", "date")
                    .withColumn("anomaly_type", lit("DISTANCE_ANOMALY"))
                    .withColumn("anomaly_severity",
                            when(abs(col("distance_zscore")).gt(3), lit("HIGH"))
                                    .when(abs(col("distance_zscore")).gt(2.5), lit("MEDIUM"))
                                    .otherwise(lit("LOW")));

            // 保存到HDFS
            timeAnomalies.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/time_anomalies");

            distanceAnomalies.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/distance_anomalies");

            // 写入MySQL异常告警表
            writeAnomaliesToMySQL(timeAnomalies, distanceAnomalies, spark);

            System.out.println("✅ 异常检测分析完成");

        } catch (Exception e) {
            System.err.println("❌ 异常检测分析失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 8. 综合报表数据生成
     */
    private static void generateComprehensiveReports(Dataset<Row> delivery, Dataset<Row> pickup, String outputPath, SparkSession spark) {
        try {
            // 日报数据
            Dataset<Row> dailyReports = delivery
                    .groupBy("city", "region_id", "date")
                    .agg(
                            count("order_id").alias("total_orders"),
                            countDistinct("courier_id").alias("active_couriers"),
                            countDistinct("aoi_id").alias("served_aois"),
                            avg("delivery_duration_hours").alias("avg_delivery_time"),
                            sum("delivery_distance_km").alias("total_distance"),
                            sum(when(col("delivery_duration_hours").leq(1), 1).otherwise(0)).alias("fast_deliveries")
                    )
                    .withColumn("avg_orders_per_courier", col("total_orders").divide(col("active_couriers")))
                    .withColumn("avg_distance_per_order", col("total_distance").divide(col("total_orders")))
                    .withColumn("fast_delivery_rate", col("fast_deliveries").divide(col("total_orders")))
                    .withColumn("report_type", lit("DAILY"))
                    .withColumn("generated_at", current_timestamp());

            // 周报数据 (按周聚合)
            Dataset<Row> weeklyReports = delivery
                    .withColumn("week_start", date_trunc("week", col("date")))
                    .groupBy("city", "region_id", "week_start")
                    .agg(
                            count("order_id").alias("weekly_total_orders"),
                            countDistinct("courier_id").alias("weekly_active_couriers"),
                            avg("delivery_duration_hours").alias("weekly_avg_delivery_time"),
                            sum("delivery_distance_km").alias("weekly_total_distance")
                    )
                    .withColumn("report_type", lit("WEEKLY"))
                    .withColumn("generated_at", current_timestamp());

            // 保存到HDFS
            dailyReports.write()
                    .mode("overwrite")
                    .partitionBy("city", "date")
                    .parquet(outputPath + "/daily_reports");

            weeklyReports.write()
                    .mode("overwrite")
                    .partitionBy("city")
                    .parquet(outputPath + "/weekly_reports");

            System.out.println("✅ 综合报表数据生成完成");

        } catch (Exception e) {
            System.err.println("❌ 综合报表数据生成失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 写入KPI数据到MySQL
     */
    private static void writeKPIsToMySQL(Dataset<Row> coreKPIs, Dataset<Row> serviceQualityKPIs, SparkSession spark) {
        try {
            Properties mysqlProps = new Properties();
            mysqlProps.setProperty("user", "root");
            mysqlProps.setProperty("password", "password");
            mysqlProps.setProperty("driver", "com.mysql.cj.jdbc.Driver");

            String mysqlUrl = "jdbc:mysql://localhost:3306/logistics_db";

            // 只写入最近7天的KPI数据到MySQL
            Dataset<Row> recentKPIs = coreKPIs
                    .filter(col("date").geq(date_sub(current_date(), 7)))
                    .select("city", "date", "hour", "total_orders", "active_couriers",
                            "coverage_aois", "orders_per_courier", "orders_per_aoi", "efficiency_score");

            recentKPIs.write()
                    .mode("overwrite")
                    .jdbc(mysqlUrl, "realtime_kpi", mysqlProps);

            System.out.println("✅ KPI数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ MySQL写入失败，继续执行: " + e.getMessage());
        }
    }

    /**
     * 写入异常数据到MySQL
     */
    private static void writeAnomaliesToMySQL(Dataset<Row> timeAnomalies, Dataset<Row> distanceAnomalies, SparkSession spark) {
        try {
            Properties mysqlProps = new Properties();
            mysqlProps.setProperty("user", "root");
            mysqlProps.setProperty("password", "password");
            mysqlProps.setProperty("driver", "com.mysql.cj.jdbc.Driver");

            String mysqlUrl = "jdbc:mysql://localhost:3306/logistics_db";

            // 处理时间异常
            Dataset<Row> timeAlerts = timeAnomalies
                    .selectExpr(
                            "'TIME_ANOMALY' as alert_type",
                            "city",
                            "order_id",
                            "courier_id",
                            "anomaly_severity as severity",
                            "concat('配送时间异常: ', delivery_duration_hours, ' 小时') as description",
                            "delivery_duration_hours as anomaly_value",
                            "2.5 as threshold_value",
                            "false as is_resolved"
                    );

            // 处理距离异常
            Dataset<Row> distanceAlerts = distanceAnomalies
                    .selectExpr(
                            "'DISTANCE_ANOMALY' as alert_type",
                            "city",
                            "order_id",
                            "courier_id",
                            "anomaly_severity as severity",
                            "concat('配送距离异常: ', delivery_distance_km, ' 公里') as description",
                            "delivery_distance_km as anomaly_value",
                            "15.0 as threshold_value",
                            "false as is_resolved"
                    );

            // 合并写入
            Dataset<Row> allAlerts = timeAlerts.union(distanceAlerts);

            allAlerts.write()
                    .mode("append")
                    .jdbc(mysqlUrl, "anomaly_alerts", mysqlProps);

            System.out.println("✅ 异常告警数据已写入MySQL");

        } catch (Exception e) {
            System.err.println("⚠️ 异常数据MySQL写入失败，继续执行: " + e.getMessage());
        }
    }
}