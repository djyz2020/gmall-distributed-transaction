package gmall.distributed.transaction.account.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import gmall.distributed.transaction.account.entity.Account;
import gmall.distributed.transaction.account.mapper.AccountMapper;
import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.enums.RspStatusEnum;
import gmall.distributed.transaction.common.exception.DefaultException;
import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.spring.annotation.GlobalLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

    @Override
    public ObjectResponse decreaseAccount(AccountDTO accountDTO) {
        log.info("扣减账户开始...");
        int account = baseMapper.decreaseAccount(accountDTO.getUserId(), accountDTO.getAmount().doubleValue());
        ObjectResponse<Object> response = new ObjectResponse<>();
        if (account > 0) {
            response.setStatus(RspStatusEnum.SUCCESS.getCode());
            response.setMessage(RspStatusEnum.SUCCESS.getMessage());
            log.info("扣减账户成功！");
            return response;
        }

        response.setStatus(RspStatusEnum.FAIL.getCode());
        response.setMessage(RspStatusEnum.FAIL.getMessage());
        log.info("扣减账户失败！");
        return response;
    }

    @Override
    @GlobalLock
    @Transactional(rollbackFor = {Throwable.class})
    public String testGlobalLock() {
        baseMapper.testGlobalLock("1");
        log.info("Hi, i got the lock, and i will do some thing with holding this lock.");
        return "Get the local lock success.";
    }
}
