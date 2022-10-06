package gmall.distributed.transaction.stock.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
public class Stock extends Model<Stock> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String commodityCode;

    private String name;

    private Integer count;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Stock{" + ", id=" + id + ", commodityCode=" + commodityCode + ", name=" + name + ", count=" + count + "}";
    }
}
