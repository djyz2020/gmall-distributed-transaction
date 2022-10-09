package gmall.distributed.transaction.account.service;

import com.baomidou.mybatisplus.service.IService;
import gmall.distributed.transaction.account.entity.Account;
import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;

public interface IAccountService extends IService<Account> {

    /**
     * 扣用户钱
     */
    ObjectResponse decreaseAccount(AccountDTO accountDTO);

    String testGlobalLock();
}
