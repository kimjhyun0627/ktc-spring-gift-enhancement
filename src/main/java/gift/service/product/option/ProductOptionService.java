package gift.service.product.option;

import gift.dto.product.option.OptionResponse;
import gift.entity.member.value.Role;
import java.util.List;

public interface ProductOptionService {

    List<OptionResponse> getOptions(Long productId, Role role);

    OptionResponse addOption(Long productId, String name, int quantity, Role role);

    void decreaseOptionAmount(Long productId, Long optionId, int amount, Role role);
}
