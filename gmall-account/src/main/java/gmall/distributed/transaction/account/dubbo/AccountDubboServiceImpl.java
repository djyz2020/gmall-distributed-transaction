package gmall.distributed.transaction.account.dubbo;

import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.core.context.RootContext;
import gmall.distributed.transaction.account.service.IAccountService;
import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.dubbo.AccountDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DubboService(version = "2.0.0", protocol = "${dubbo.protocol.id}", registry = "${dubbo.registry.id}", timeout = 3000)
public class AccountDubboServiceImpl implements AccountDubboService {

    @Autowired
    private IAccountService accountService;

    @Override
    public ObjectResponse decreaseAccount(AccountDTO accountDTO) {
        log.info("全局事务id ：" + RootContext.getXID());
        return accountService.decreaseAccount(accountDTO);
    }
}
