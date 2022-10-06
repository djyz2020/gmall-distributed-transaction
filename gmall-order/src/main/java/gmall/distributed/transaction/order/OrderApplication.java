package gmall.distributed.transaction.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "gmall.distributed.transaction.order")
@EnableDiscoveryClient
@MapperScan({"gmall.distributed.transaction.order.mapper"})
@EnableDubbo(scanBasePackages = "gmall.distributed.transaction.order")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}

