package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.SparkJobLogs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SparkJobLogsMapper {

    /**
     * 插入作业日志
     */
    int insertJob(SparkJobLogs job);

    /**
     * 批量插入作业日志
     */
    int batchInsertJobs(@Param("list") List<SparkJobLogs> jobList);

    /**
     * 根据ID查找作业
     */
    SparkJobLogs findById(@Param("id") Long id);

    /**
     * 查找最近的作业
     */
    List<SparkJobLogs> findRecentJobs(@Param("limit") int limit);

    /**
     * 根据状态查找作业
     */
    List<SparkJobLogs> findByStatus(@Param("status") String status);

    /**
     * 根据作业名称查找
     */
    List<SparkJobLogs> findByJobName(@Param("jobName") String jobName);

    /**
     * 查找成功的作业
     */
    List<SparkJobLogs> findSuccessfulJobs(@Param("limit") int limit);

    /**
     * 根据时间范围查找作业
     */
    List<SparkJobLogs> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查找长时间运行的作业
     */
    List<SparkJobLogs> findLongRunningJobs(@Param("thresholdSeconds") int thresholdSeconds);

    /**
     * 更新作业状态
     */
    int updateJobStatus(@Param("id") Long id,
                        @Param("status") String status,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("executionTimeSeconds") Integer executionTimeSeconds,
                        @Param("errorMessage") String errorMessage);

    /**
     * 更新作业处理记录数
     */
    int updateProcessedRecords(@Param("id") Long id, @Param("processedRecords") Long processedRecords);

    /**
     * 获取作业执行趋势
     */
    List<Map<String, Object>> getJobExecutionTrend(@Param("startTime") LocalDateTime startTime);

    /**
     * 获取作业类型统计
     */
    List<Map<String, Object>> getJobTypeStatistics(@Param("startTime") LocalDateTime startTime);

    /**
     * 获取小时级作业分布
     */
    List<Map<String, Object>> getHourlyJobDistribution();

    /**
     * 统计作业数量
     */
    int countJobsByStatus(@Param("status") String status);

    /**
     * 统计总作业数
     */
    int countTotalJobs();

    /**
     * 获取平均执行时间
     */
    Double getAverageExecutionTime();

    /**
     * 获取总处理记录数
     */
    Long getTotalProcessedRecords();

    /**
     * 清理旧的作业日志
     */
    int cleanupOldJobs(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 查找有错误信息的作业
     */
    List<SparkJobLogs> findJobsWithErrors(@Param("limit") int limit);

    /**
     * 查找超过指定处理记录数的作业
     */
    List<SparkJobLogs> findJobsWithHighRecordCount(@Param("threshold") long threshold, @Param("limit") int limit);
}