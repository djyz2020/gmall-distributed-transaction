package gmall.distributed.transaction.stock.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import gmall.distributed.transaction.stock.entity.Stock;
import org.apache.ibatis.annotations.Param;

public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 扣减商品库存
     *
     * @Param: commodityCode 商品code  count扣减数量
     * @Return:
     */
    int decreaseStock(@Param("commodityCode") String commodityCode, @Param("count") Integer count);
}
