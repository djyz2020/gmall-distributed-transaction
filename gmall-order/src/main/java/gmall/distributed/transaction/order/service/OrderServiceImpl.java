package gmall.distributed.transaction.order.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import gmall.distributed.transaction.common.dto.AccountDTO;
import gmall.distributed.transaction.common.dto.OrderDTO;
import gmall.distributed.transaction.common.dubbo.AccountDubboService;
import gmall.distributed.transaction.common.enums.RspStatusEnum;
import gmall.distributed.transaction.common.response.ObjectResponse;
import gmall.distributed.transaction.order.entity.Order;
import gmall.distributed.transaction.order.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @DubboReference
    private AccountDubboService accountDubboService;

    /**
     * 创建订单
     *
     * @Param: OrderDTO  订单对象
     * @Return: OrderDTO  订单对象
     */
    @Override
    public ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO) {
        ObjectResponse<OrderDTO> response = new ObjectResponse<>();
        //扣减用户账户
        log.info("扣减账户开始...");
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUserId(orderDTO.getUserId());
        accountDTO.setAmount(orderDTO.getOrderAmount());
        ObjectResponse objectResponse = accountDubboService.decreaseAccount(accountDTO);
        log.info("扣减账户成功！");

        log.info("创建订单开始...");
        //生成订单号
        orderDTO.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        //生成订单
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setCount(orderDTO.getOrderCount());
        order.setAmount(orderDTO.getOrderAmount().doubleValue());
        try {
            baseMapper.createOrder(order);
            log.info("创建订单成功！");
        } catch (Exception e) {
            log.info("创建订单失败！");
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        if (objectResponse.getStatus() != 200) {
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        response.setStatus(RspStatusEnum.SUCCESS.getCode());
        response.setMessage(RspStatusEnum.SUCCESS.getMessage());
        return response;
    }
}
