package gmall.distributed.transaction.order.dubbo;

import gmall.distributed.transaction.common.dto.OrderDTO;
import gmall.distributed.transaction.common.dubbo.OrderDubboService;
import gmall.distributed.transaction.common.response.ObjectResponse;
import gmall.distributed.transaction.order.service.IOrderService;
import io.seata.core.context.RootContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "2.0.0", protocol = "${dubbo.protocol.id}", registry = "${dubbo.registry.id}", timeout = 3000)
public class OrderDubboServiceImpl implements OrderDubboService {

    @Autowired
    private IOrderService orderService;

    @Override
    public ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO) {
        System.out.println("全局事务id ：" + RootContext.getXID());
        return orderService.createOrder(orderDTO);
    }
}
