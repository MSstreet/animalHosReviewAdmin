package com.example.animalhosreviewadmin.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewVerificationScheduler {

    private final JobLauncher jobLauncher;
    private final Job reviewVerificationJob;

    // 매 시간마다 실행 (0분 0초)
    @Scheduled(cron = "0 0 * * * ?")
    public void runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(reviewVerificationJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 