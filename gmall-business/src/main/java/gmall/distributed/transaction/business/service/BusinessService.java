package gmall.distributed.transaction.business.service;

import gmall.distributed.transaction.common.dto.BusinessDTO;
import gmall.distributed.transaction.common.response.ObjectResponse;

public interface BusinessService {

    ObjectResponse handleBusiness(BusinessDTO businessDTO);
}
