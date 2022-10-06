package gmall.distributed.transaction.common.dubbo;

import gmall.distributed.transaction.common.dto.OrderDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;

public interface OrderDubboService {

    /**
     * 创建订单
     */
    ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO);
}
