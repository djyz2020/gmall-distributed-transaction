package gmall.distributed.transaction.account.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import gmall.distributed.transaction.account.entity.Account;
import org.apache.ibatis.annotations.Param;

public interface AccountMapper extends BaseMapper<Account> {

    int decreaseAccount(@Param("userId") String userId, @Param("amount") Double amount);

    int testGlobalLock(@Param("userId") String userId);
}
