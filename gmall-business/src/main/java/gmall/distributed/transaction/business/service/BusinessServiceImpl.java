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

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @DubboReference(version = "2.0.0")
    private StockDubboService stockDubboService;

    @DubboReference(version = "2.0.0")
    private OrderDubboService orderDubboService;

    /**
     * 处理业务逻辑
     *
     * @param businessDTO 业务信息
     * @return ObjectResponse
     */
    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-gts-seata")
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {
        log.info("开始全局事务，XID = " + RootContext.getXID());
        ObjectResponse<Object> objectResponse = new ObjectResponse<>();
        //1、扣减库存
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setCount(businessDTO.getCount());
        ObjectResponse stockResponse = stockDubboService.decreaseStock(commodityDTO);
        //2、创建订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderCount(businessDTO.getCount());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderDubboService.createOrder(orderDTO);

        //打开注释测试事务发生异常后，全局回滚功能
        boolean flag = false;
        if (!flag) {
            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
        }

        if (stockResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }

        objectResponse.setStatus(RspStatusEnum.SUCCESS.getCode());
        objectResponse.setMessage(RspStatusEnum.SUCCESS.getMessage());
        objectResponse.setData(response.getData());
        return objectResponse;
    }
}
