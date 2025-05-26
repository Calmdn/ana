package com.logistics.spark.monitor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

public class SparkJobMonitor {
    private Properties mysqlProps;
    private String mysqlUrl;

    public SparkJobMonitor(Properties mysqlProps, String mysqlUrl) {
        this.mysqlProps = mysqlProps;
        this.mysqlUrl = mysqlUrl;
    }

    /**
     * 开始作业监控
     */
    public Long startJobTracking(String jobName, String deliverPath, String pickupPath,
                                 String outputPath, String timeFormatUsed, Integer defaultYear) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            String sql = "INSERT INTO spark_job_logs " +
                    "(job_name, start_time, status, input_deliver_path, input_pickup_path, " +
                    "output_path, time_format_used, default_year, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, jobName);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, "RUNNING");
            stmt.setString(4, deliverPath);
            stmt.setString(5, pickupPath);
            stmt.setString(6, outputPath);
            stmt.setString(7, timeFormatUsed);
            if (defaultYear != null) {
                stmt.setInt(8, defaultYear);
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    Long jobId = rs.getLong(1);
                    System.out.println("✅ 开始监控作业: " + jobName + " (ID: " + jobId + ")");
                    return jobId;
                }
            }
            throw new RuntimeException("Failed to get generated job ID");

        } catch (Exception e) {
            System.err.println("❌ 启动作业监控失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rs, stmt, connection);
        }
    }

    /**
     * 完成作业监控
     */
    public void completeJobTracking(Long jobId, boolean success, long processedRecords, String errorMessage) {
        if (jobId == null) {
            System.err.println("⚠️ 作业ID为空，无法完成监控");
            return;
        }

        Connection connection = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();

            // 获取开始时间计算执行时间
            String selectSql = "SELECT start_time FROM spark_job_logs WHERE id = ?";
            selectStmt = connection.prepareStatement(selectSql);
            selectStmt.setLong(1, jobId);
            rs = selectStmt.executeQuery();

            Integer executionSeconds = null;
            if (rs.next()) {
                Timestamp startTime = rs.getTimestamp("start_time");
                if (startTime != null) {
                    LocalDateTime start = startTime.toLocalDateTime();
                    LocalDateTime end = LocalDateTime.now();
                    executionSeconds = (int) java.time.Duration.between(start, end).getSeconds();
                }
            }

            // 更新作业状态
            String updateSql = "UPDATE spark_job_logs " +
                    "SET end_time = ?, status = ?, processed_records = ?, error_message = ?, execution_time_seconds = ? " +
                    "WHERE id = ?";

            updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            updateStmt.setString(2, success ? "SUCCESS" : "FAILED");
            updateStmt.setLong(3, processedRecords);
            updateStmt.setString(4, errorMessage);
            if (executionSeconds != null) {
                updateStmt.setInt(5, executionSeconds);
            } else {
                updateStmt.setNull(5, Types.INTEGER);
            }
            updateStmt.setLong(6, jobId);

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                String status = success ? "SUCCESS" : "FAILED";
                String emoji = success ? "✅" : "❌";
                System.out.println(emoji + " 作业监控完成 - ID: " + jobId + ", 状态: " + status +
                        ", 处理记录: " + processedRecords +
                        (executionSeconds != null ? ", 执行时间: " + executionSeconds + "秒" : ""));
            }

        } catch (Exception e) {
            System.err.println("❌ 完成作业监控失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, selectStmt, connection);
            closeResources(null, updateStmt, null);
        }
    }

    /**
     * 更新作业进度
     */
    public void updateJobProgress(Long jobId, long processedRecords, String progressMessage) {
        if (jobId == null) return;

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = getConnection();
            String sql = "UPDATE spark_job_logs SET processed_records = ? WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, processedRecords);
            stmt.setLong(2, jobId);

            stmt.executeUpdate();
            if (progressMessage != null) {
                System.out.println("📊 作业进度更新 - ID: " + jobId + ", 已处理: " + processedRecords + " - " + progressMessage);
            }

        } catch (Exception e) {
            System.err.println("⚠️ 更新作业进度失败: " + e.getMessage());
        } finally {
            closeResources(null, stmt, connection);
        }
    }

    /**
     * 记录分析模块完成情况
     */
    public void logModuleCompletion(Long jobId, String moduleName, long moduleProcessedRecords) {
        if (jobId == null) return;

        System.out.println("✅ 模块完成: " + moduleName + " (处理记录: " + moduleProcessedRecords + ")");
        // 这里可以选择更新进度或者记录到另一个详细日志表
        updateJobProgress(jobId, moduleProcessedRecords, "完成模块: " + moduleName);
    }

    /**
     * 记录模块开始
     */
    public void logModuleStart(Long jobId, String moduleName) {
        if (jobId == null) return;
        System.out.println("🚀 开始执行模块: " + moduleName);
    }

    /**
     * 记录错误信息
     */
    public void logError(Long jobId, String errorMessage, Exception e) {
        System.err.println("❌ 作业执行错误 - ID: " + jobId + ", 错误: " + errorMessage);
        if (e != null) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL驱动加载失败", e);
        }
        return DriverManager.getConnection(mysqlUrl, mysqlProps);
    }

    /**
     * 关闭数据库资源
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("关闭数据库资源时出错: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接
     */
    public boolean testConnection() {
        Connection connection = null;
        try {
            connection = getConnection();
            System.out.println("✅ 数据库连接测试成功");
            return true;
        } catch (Exception e) {
            System.err.println("❌ 数据库连接测试失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, null, connection);
        }
    }
}