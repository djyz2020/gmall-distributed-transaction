package gmall.distributed.transaction.account.dubbo;

import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.core.context.RootContext;
import gmall.distributed.transaction.account.service.IAccountService;
import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.dubbo.AccountDubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountDubboServiceImpl implements AccountDubboService {

    @Autowired
    private IAccountService accountService;

    @Override
    public ObjectResponse decreaseAccount(AccountDTO accountDTO) {
        System.out.println("全局事务id ：" + RootContext.getXID());
        return accountService.decreaseAccount(accountDTO);
    }
}
