package gmall.distributed.transaction.stock.service;

import com.baomidou.mybatisplus.service.IService;
import gmall.distributed.transaction.common.dto.CommodityDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;
import gmall.distributed.transaction.stock.entity.Stock;

public interface IStockService extends IService<Stock> {

    /**
     * 扣减库存
     */
    ObjectResponse decreaseStock(CommodityDTO commodityDTO);
}
