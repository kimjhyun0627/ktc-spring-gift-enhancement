package gift.service.product.option;

import gift.dto.product.option.OptionResponse;
import gift.entity.member.value.Role;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    public List<OptionResponse> getOptions(Long productId, Role role) {
        authorizeProduct(productId, role);

        return optionRepository.findAllByProduct_Id(new ProductId(productId)).stream()
                .map(OptionResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public OptionResponse addOption(Long productId, String name, int quantity, Role role) {
        Product product = authorizeProduct(productId, role);

        try {
            ProductOption option = ProductOption.of(product, name, quantity);
            ProductOption savedOption = optionRepository.save(option);
            return OptionResponse.of(savedOption);
        } catch (DataIntegrityViolationException ex) {
            throw new OptionAlreadyExistException();
        }
    }

    @Override
    public void decreaseOptionAmount(Long productId, Long optionId, int amount, Role role) {
        Product pproduct = authorizeProduct(productId, role);
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotFoundException(optionId));
        if (!option.getProduct().getId().equals(pproduct.getId())) {
            throw new OptionNotFoundException(optionId);
        }
        option.decreaseAmount(amount);
    }

    private Product authorizeProduct(Long productId, Role role) {
        Product product = productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (role.isUser() && product.isHidden()) {
            throw new ProductNotFoundException(productId);
        }
        return product;
    }
}

