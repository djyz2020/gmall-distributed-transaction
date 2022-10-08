package gmall.distributed.transaction.stock.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import gmall.distributed.transaction.common.dto.CommodityDTO;
import gmall.distributed.transaction.common.enums.RspStatusEnum;
import gmall.distributed.transaction.common.exception.DefaultException;
import gmall.distributed.transaction.common.response.ObjectResponse;
import gmall.distributed.transaction.stock.entity.Stock;
import gmall.distributed.transaction.stock.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Override
    @Transactional(rollbackFor = DefaultException.class)
    public ObjectResponse decreaseStock(CommodityDTO commodityDTO) {
        log.info("扣减库存开始，扣减库存：{}", commodityDTO.getCount());
        int stock = baseMapper.decreaseStock(commodityDTO.getCommodityCode(), commodityDTO.getCount());
        ObjectResponse<Object> response = new ObjectResponse<>();
        if (stock > 0) {
            response.setStatus(RspStatusEnum.SUCCESS.getCode());
            response.setMessage(RspStatusEnum.SUCCESS.getMessage());
            log.info("扣减库存成功！");
            return response;
        }

        response.setStatus(RspStatusEnum.FAIL.getCode());
        response.setMessage(RspStatusEnum.FAIL.getMessage());
        log.info("扣减库存失败！");
        return response;
    }
}
