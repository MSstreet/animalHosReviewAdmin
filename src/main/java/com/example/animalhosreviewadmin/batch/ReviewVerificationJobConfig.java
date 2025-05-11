package com.example.animalhosreviewadmin.batch;

import com.example.animalhosreviewadmin.domain.ReceiptStatus;
import com.example.animalhosreviewadmin.domain.Review;
import com.example.animalhosreviewadmin.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class ReviewVerificationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReviewRepository reviewRepository;
    private final Tesseract tesseract;

    @Bean
    public Job reviewVerificationJob() {
        return new JobBuilder("reviewVerificationJob", jobRepository)
                .start(reviewVerificationStep())
                .build();
    }

    @Bean
    public Step reviewVerificationStep() {
        return new StepBuilder("reviewVerificationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Review> pendingReviews = reviewRepository.findByReceiptStatusAndDeleteYnFalse(ReceiptStatus.PENDING);
                    
                    for (Review review : pendingReviews) {
                        boolean isApproved = autoVerifyReview(review);
                        
                        review.setReceiptStatus(isApproved ? ReceiptStatus.APPROVED : ReceiptStatus.REJECTED);
                        review.setVerificationComment(isApproved ? "자동 검증 승인" : "자동 검증 거절");
                        review.setVerifiedAt(LocalDateTime.now());
                        review.setApproveYn(isApproved);
                        
                        reviewRepository.save(review);
                    }
                    
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private boolean autoVerifyReview(Review review) {
        // 1. 리뷰 내용 검증
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            return false;
        }

        // 2. 점수 검증
        if (review.getPriceScore() < 0 || review.getPriceScore() > 5 ||
            review.getKindnessScore() < 0 || review.getKindnessScore() > 5 ||
            review.getEffectScore() < 0 || review.getEffectScore() > 5) {
            return false;
        }

        // 3. 병원명 일치 여부 확인
        if (!review.getHospitalName().equals(review.getPetHospitalEntity().getHospitalName())) {
            return false;
        }

        // 4. 영수증 파일 존재 여부 확인
        if (review.getFileName() == null || review.getFileName().trim().isEmpty()) {
            return false;
        }

        // 5. 영수증 이미지 검증
        try {
            return verifyReceiptImage(review);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyReceiptImage(Review review) throws TesseractException, IOException {
        // 1. 이미지 파일 로드
        File imageFile = new File(review.getFilePath());
        if (!imageFile.exists()) {
            return false;
        }

        // 2. 이미지에서 텍스트 추출
        BufferedImage image = ImageIO.read(imageFile);
        String extractedText = tesseract.doOCR(image);

        // 3. 영수증 특성 검증
        return verifyReceiptCharacteristics(extractedText, review);
    }

    private boolean verifyReceiptCharacteristics(String extractedText, Review review) {
        // 1. 금액 패턴 확인 (숫자 + 원)
        boolean hasAmount = Pattern.compile("\\d+원").matcher(extractedText).find();

        // 2. 병원명 포함 여부 확인
        boolean hasHospitalName = extractedText.contains(review.getHospitalName());

        // 3. 날짜 패턴 확인 (YYYY-MM-DD 또는 YYYY/MM/DD)
        boolean hasDate = Pattern.compile("\\d{4}[-/]\\d{2}[-/]\\d{2}").matcher(extractedText).find();

        // 4. 영수증 키워드 확인
        boolean hasReceiptKeywords = extractedText.contains("영수증") || 
                                   extractedText.contains("거래명세서") || 
                                   extractedText.contains("진료비");

        return hasAmount && hasHospitalName && hasDate && hasReceiptKeywords;
    }
} 