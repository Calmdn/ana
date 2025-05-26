package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.SparkJobLogs;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SparkJobLogsMapper {

    /**
     * 插入作业日志
     */
    @Insert("INSERT INTO spark_job_logs (job_name, start_time, end_time, status, " +
            "input_deliver_path, input_pickup_path, output_path, processed_records, " +
            "error_message, execution_time_seconds, time_format_used, default_year) " +
            "VALUES (#{jobName}, #{startTime}, #{endTime}, #{status}, " +
            "#{inputDeliverPath}, #{inputPickupPath}, #{outputPath}, #{processedRecords}, " +
            "#{errorMessage}, #{executionTimeSeconds}, #{timeFormatUsed}, #{defaultYear})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertJob(SparkJobLogs job);

    /**
     * 根据ID查找作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE id = #{id}")
    SparkJobLogs findById(@Param("id") Long id);

    /**
     * 查找最近的作业
     */
    @Select("SELECT * FROM spark_job_logs ORDER BY created_at DESC LIMIT #{limit}")
    List<SparkJobLogs> findRecentJobs(@Param("limit") int limit);

    /**
     * 根据状态查找作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE status = #{status} ORDER BY created_at DESC")
    List<SparkJobLogs> findByStatus(@Param("status") String status);

    /**
     * 根据作业名称查找
     */
    @Select("SELECT * FROM spark_job_logs WHERE job_name = #{jobName} ORDER BY created_at DESC")
    List<SparkJobLogs> findByJobName(@Param("jobName") String jobName);

    /**
     * 查找成功的作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE status = 'SUCCESS' ORDER BY created_at DESC LIMIT #{limit}")
    List<SparkJobLogs> findSuccessfulJobs(@Param("limit") int limit);

    /**
     * 根据时间范围查找作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE start_time >= #{startTime} AND start_time <= #{endTime} " +
            "ORDER BY start_time DESC")
    List<SparkJobLogs> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查找长时间运行的作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE execution_time_seconds > #{thresholdSeconds} " +
            "ORDER BY execution_time_seconds DESC")
    List<SparkJobLogs> findLongRunningJobs(@Param("thresholdSeconds") int thresholdSeconds);

    /**
     * 更新作业状态
     */
    @Update("UPDATE spark_job_logs SET status = #{status}, end_time = #{endTime}, " +
            "execution_time_seconds = #{executionTimeSeconds}, error_message = #{errorMessage} " +
            "WHERE id = #{id}")
    int updateJobStatus(@Param("id") Long id,
                        @Param("status") String status,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("executionTimeSeconds") Integer executionTimeSeconds,
                        @Param("errorMessage") String errorMessage);

    /**
     * 更新作业处理记录数
     */
    @Update("UPDATE spark_job_logs SET processed_records = #{processedRecords} WHERE id = #{id}")
    int updateProcessedRecords(@Param("id") Long id, @Param("processedRecords") Long processedRecords);

    /**
     * 获取作业执行趋势
     */
    @Select("SELECT DATE(start_time) as job_date, status, COUNT(*) as count " +
            "FROM spark_job_logs " +
            "WHERE start_time >= #{startTime} " +
            "GROUP BY DATE(start_time), status " +
            "ORDER BY job_date DESC")
    List<Map<String, Object>> getJobExecutionTrend(@Param("startTime") LocalDateTime startTime);

    /**
     * 获取作业类型统计
     */
    @Select("SELECT job_name, status, COUNT(*) as count, " +
            "AVG(execution_time_seconds) as avg_execution_time, " +
            "SUM(processed_records) as total_processed_records " +
            "FROM spark_job_logs " +
            "WHERE start_time >= #{startTime} " +
            "GROUP BY job_name, status " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getJobTypeStatistics(@Param("startTime") LocalDateTime startTime);

    /**
     * 获取小时级作业分布
     */
    @Select("SELECT HOUR(start_time) as hour, status, COUNT(*) as count " +
            "FROM spark_job_logs " +
            "WHERE DATE(start_time) = CURDATE() " +
            "GROUP BY HOUR(start_time), status " +
            "ORDER BY hour")
    List<Map<String, Object>> getHourlyJobDistribution();

    /**
     * 统计作业数量
     */
    @Select("SELECT COUNT(*) FROM spark_job_logs WHERE status = #{status}")
    int countJobsByStatus(@Param("status") String status);

    /**
     * 统计总作业数
     */
    @Select("SELECT COUNT(*) FROM spark_job_logs")
    int countTotalJobs();

    /**
     * 获取平均执行时间
     */
    @Select("SELECT AVG(execution_time_seconds) FROM spark_job_logs WHERE status = 'SUCCESS' AND execution_time_seconds IS NOT NULL")
    Double getAverageExecutionTime();

    /**
     * 获取总处理记录数
     */
    @Select("SELECT SUM(processed_records) FROM spark_job_logs WHERE processed_records IS NOT NULL")
    Long getTotalProcessedRecords();

    /**
     * 清理旧的作业日志
     */
    @Delete("DELETE FROM spark_job_logs WHERE created_at < #{cutoffTime}")
    int cleanupOldJobs(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 查找有错误信息的作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE error_message IS NOT NULL AND error_message != '' " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<SparkJobLogs> findJobsWithErrors(@Param("limit") int limit);

    /**
     * 查找超过指定处理记录数的作业
     */
    @Select("SELECT * FROM spark_job_logs WHERE processed_records > #{threshold} " +
            "ORDER BY processed_records DESC LIMIT #{limit}")
    List<SparkJobLogs> findJobsWithHighRecordCount(@Param("threshold") long threshold, @Param("limit") int limit);
}