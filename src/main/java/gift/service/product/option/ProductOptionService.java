package gift.service.product.option;

import gift.dto.product.option.OptionResponse;
import java.util.List;

public interface ProductOptionService {

    List<OptionResponse> getOptions(Long productId);

    OptionResponse addOption(Long productId, String name, int quantity);

    void decreaseOption(Long optionId, int amount);
}
