package gift.service.product.option;

import gift.dto.product.option.OptionResponse;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.product.value.ProductId;
import gift.exception.custom.OptionAlreadyExistException;
import gift.exception.custom.OptionNotFoundException;
import gift.exception.custom.ProductNotFoundException;
import gift.repository.product.ProductRepository;
import gift.repository.product.option.ProductOptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;

    public ProductOptionServiceImpl(
            ProductRepository productRepository,
            ProductOptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponse> getOptions(Long productId) {
        productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return optionRepository.findAllByProduct_Id(new ProductId(productId)).stream()
                .map(ProductOption::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OptionResponse addOption(Long productId, String name, int quantity) {
        Product product = productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new ProductNotFoundException(productId));

        boolean exists = optionRepository.existsByProduct_IdAndName_Name(
                new ProductId(productId),
                name
        );
        if (exists) {
            throw new OptionAlreadyExistException();
        }

        ProductOption option = ProductOption.of(name, quantity);
        option.assignTo(product);
        ProductOption savedOption = optionRepository.save(option);
        return savedOption.toResponse();
    }

    @Override
    public void decreaseOption(Long optionId, int amount) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotFoundException(optionId));
        option.decrease(amount);
    }
}

