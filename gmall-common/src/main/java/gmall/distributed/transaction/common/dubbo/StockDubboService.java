package gmall.distributed.transaction.common.dubbo;

import gmall.distributed.transaction.common.dto.CommodityDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;

public interface StockDubboService {

    /**
     * 扣减库存
     */
    ObjectResponse decreaseStock(CommodityDTO commodityDTO);
}
