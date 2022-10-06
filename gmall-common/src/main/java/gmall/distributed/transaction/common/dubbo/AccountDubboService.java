package gmall.distributed.transaction.common.dubbo;

import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;

public interface AccountDubboService {

    /**
     * 从账户扣钱
     */
    ObjectResponse decreaseAccount(AccountDTO accountDTO);
}
