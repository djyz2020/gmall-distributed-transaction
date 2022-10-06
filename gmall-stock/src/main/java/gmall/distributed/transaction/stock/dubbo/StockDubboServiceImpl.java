package gmall.distributed.transaction.stock.dubbo;

import com.alibaba.dubbo.config.annotation.Service;

import gmall.distributed.transaction.common.response.ObjectResponse;
import io.seata.core.context.RootContext;
import gmall.distributed.transaction.common.dto.CommodityDTO;
import gmall.distributed.transaction.common.dubbo.StockDubboService;
import gmall.distributed.transaction.stock.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "2.0.0", protocol = "${dubbo.protocol.id}", registry = "${dubbo.registry.id}", timeout = 3000)
@Slf4j
public class StockDubboServiceImpl implements StockDubboService {

    @Autowired
    private IStockService stockService;

    @Override
    public ObjectResponse decreaseStock(CommodityDTO commodityDTO) {
        log.info("全局事务id ：{}", RootContext.getXID());
        return stockService.decreaseStock(commodityDTO);
    }
}
