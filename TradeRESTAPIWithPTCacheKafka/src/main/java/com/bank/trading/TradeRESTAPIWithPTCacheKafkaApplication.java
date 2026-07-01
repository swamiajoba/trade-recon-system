package com.bank.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//
//import javax.cache.CacheManager;

@SpringBootApplication
@EnableScheduling
//@EnableCaching
public class TradeRESTAPIWithPTCacheKafkaApplication {

	public static void main(String[] args) {

		SpringApplication.run(TradeRESTAPIWithPTCacheKafkaApplication.class, args);
	}

//	@Bean
//    CommandLineRunner cacheCheck(CacheManager cacheManager) {
//		return args -> {
//			System.out.println("Cache Manager = " + cacheManager.getClass());
//			System.out.println("Caches = " + cacheManager.getCacheNames());
//		};
//	}
}
