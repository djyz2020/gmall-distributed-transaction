package gmall.distributed.transaction.account;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "gmall.distributed.transaction.account")
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan({"gmall.distributed.transaction.account.mapper"})
@EnableDubbo(scanBasePackages = "gmall.distributed.transaction.account")
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}

