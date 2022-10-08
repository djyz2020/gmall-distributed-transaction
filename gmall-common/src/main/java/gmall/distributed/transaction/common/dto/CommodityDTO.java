package gmall.distributed.transaction.common.dto;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CommodityDTO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String commodityCode;

    private String name;

    private Integer count;
}
