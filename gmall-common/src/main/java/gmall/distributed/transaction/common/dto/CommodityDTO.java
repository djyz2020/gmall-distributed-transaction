package gmall.distributed.transaction.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CommodityDTO implements Serializable {

    private Integer id;

    private String commodityCode;

    private String name;

    private Integer count;
}
