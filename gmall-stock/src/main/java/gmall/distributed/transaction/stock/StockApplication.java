package gmall.distributed.transaction.stock;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "gmall.distributed.transaction.stock")
@EnableDiscoveryClient
@MapperScan({"gmall.distributed.transaction.stock.mapper"})
@EnableDubbo(scanBasePackages = "gmall.distributed.transaction.stock")
public class StockApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

}

