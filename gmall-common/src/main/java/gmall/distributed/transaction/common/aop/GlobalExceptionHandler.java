package gmall.distributed.transaction.common.aop;

import gmall.distributed.transaction.common.enums.RspStatusEnum;
import gmall.distributed.transaction.common.exception.DefaultException;
import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ObjectResponse exceptionHandler(Exception e) {
        log.error("【系统抛出Exception异常】 -异常内容：{}", e);
        ObjectResponse objectResponse = new ObjectResponse<>();
        objectResponse.setStatus(RspStatusEnum.FAIL.getCode());
        objectResponse.setMessage(RspStatusEnum.FAIL.getMessage());
        try {
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
        } catch (TransactionException transactionException) {
            log.error(transactionException.getLocalizedMessage());
        }
        return objectResponse;
    }

    @ExceptionHandler(DefaultException.class)
    @ResponseBody
    public ObjectResponse defaultException(DefaultException e) {
        log.error("【系统抛出DefaultException异常】 - 异常内容：{}", e);
        ObjectResponse objectResponse = new ObjectResponse<>();
        objectResponse.setStatus(RspStatusEnum.FAIL.getCode());
        objectResponse.setMessage(RspStatusEnum.FAIL.getMessage());
        try {
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
        } catch (TransactionException transactionException) {
            log.error(transactionException.getLocalizedMessage());
        }
        return objectResponse;
    }
}
