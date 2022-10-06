package gmall.distributed.transaction.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import gmall.distributed.transaction.order.entity.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 创建订单
     *
     * @Param: order 订单信息
     * @Return:
     */
    void createOrder(@Param("order") Order order);
}
