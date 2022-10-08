package gmall.distributed.transaction.business.service;

import com.alibaba.dubbo.config.annotation.Reference;
import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.core.context.RootContext;
import gmall.distributed.transaction.common.dto.BusinessDTO;
import gmall.distributed.transaction.common.dto.CommodityDTO;
import gmall.distributed.transaction.common.dto.OrderDTO;
import gmall.distributed.transaction.common.dubbo.OrderDubboService;
import gmall.distributed.transaction.common.dubbo.StockDubboService;
import gmall.distributed.transaction.common.enums.RspStatusEnum;
import gmall.distributed.transaction.common.exception.DefaultException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @DubboReference
    private StockDubboService stockDubboService;

    @DubboReference
    private OrderDubboService orderDubboService;

    /**
     * 处理业务逻辑
     *
     * @param businessDTO 业务信息
     * @return ObjectResponse
     */
    @Override
    @GlobalTransactional(name = "dubbo-gts-seata", rollbackFor = Exception.class, timeoutMills = 300000)
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {
        log.info("开始全局事务，XID = " + RootContext.getXID());
        ObjectResponse<Object> objectResponse = new ObjectResponse<>();
        //1、扣减库存
        log.info("扣减库存开始...");
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setCount(businessDTO.getCount());
        ObjectResponse stockResponse = stockDubboService.decreaseStock(commodityDTO);
        log.info("扣减库存结束!");
        //2、创建订单
        log.info("创建订单开始...");
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderCount(businessDTO.getCount());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderDubboService.createOrder(orderDTO);
        log.info("创建订单结束！");
        if (stockResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }
        objectResponse.setStatus(RspStatusEnum.SUCCESS.getCode());
        objectResponse.setMessage(RspStatusEnum.SUCCESS.getMessage());
        objectResponse.setData(response.getData());
        return objectResponse;
    }
}
