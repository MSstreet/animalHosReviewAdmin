package com.example.animalhosreviewadmin.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        // Tesseract 데이터 파일 경로 설정 (tessdata 폴더)
        tesseract.setDatapath("tessdata");
        // 한국어 설정
        tesseract.setLanguage("kor+eng");
        return tesseract;
    }
}