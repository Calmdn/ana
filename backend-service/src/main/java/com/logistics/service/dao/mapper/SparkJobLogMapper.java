package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.SparkJobLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SparkJobLogMapper {

    @Insert("INSERT INTO spark_job_logs (job_name, start_time, status, input_deliver_path, input_pickup_path, output_path, created_at) " +
            "VALUES (#{jobName}, #{startTime}, #{status}, #{inputDeliverPath}, #{inputPickupPath}, #{outputPath}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertJobLog(SparkJobLog jobLog);

    @Update("UPDATE spark_job_logs SET end_time = #{endTime}, status = #{status}, " +
            "processed_records = #{processedRecords}, error_message = #{errorMessage}, " +
            "execution_time_seconds = TIMESTAMPDIFF(SECOND, start_time, #{endTime}) " +
            "WHERE id = #{id}")
    int updateJobLog(SparkJobLog jobLog);

    @Select("SELECT * FROM spark_job_logs WHERE status = 'RUNNING' ORDER BY start_time DESC")
    List<SparkJobLog> findRunningJobs();

    @Select("SELECT * FROM spark_job_logs ORDER BY start_time DESC LIMIT #{limit}")
    List<SparkJobLog> findRecentJobs(@Param("limit") int limit);

    @Select("SELECT COUNT(*) as total_jobs, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_jobs, " +
            "ROUND(SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate " +
            "FROM spark_job_logs WHERE start_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    List<java.util.Map<String, Object>> getJobStats(@Param("days") int days);

    @Delete("DELETE FROM spark_job_logs WHERE start_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int cleanupOldLogs();
}