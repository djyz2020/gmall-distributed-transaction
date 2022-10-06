package gmall.distributed.transaction.order.service;

import com.baomidou.mybatisplus.service.IService;
import gmall.distributed.transaction.common.dto.OrderDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;
import gmall.distributed.transaction.order.entity.Order;

public interface IOrderService extends IService<Order> {

    /**
     * 创建订单
     */
    ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO);
}
