package gift.controller.admin;

import gift.annotation.CurrentRole;
import gift.dto.product.ProductForm;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(@CurrentRole Role role, Model model) {
        model.addAttribute("products", productService.getAllProducts(role));
        return "admin/product_list";
    }

    @GetMapping("/new")
    public String createForm(@CurrentRole Role role, Model model) {
        model.addAttribute("productForm", new ProductForm(null, "", null, ""));
        return "admin/product_form";
    }

    @PostMapping("/new")
    public String create(
            @CurrentRole Role role,
            @Valid @ModelAttribute ProductForm productForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse
    ) {
        String errorView = checkErrors(bindingResult, httpServletResponse);
        if (errorView != null) {
            return errorView;
        }
        productService.createProduct(
                productForm.getName(),
                productForm.getPrice(),
                productForm.getImageUrl(),
                role
        );
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @CurrentRole Role role,
            @PathVariable Long id,
            Model model
    ) {
        Product p = productService.getProductById(id, role)
                .orElseThrow(() -> new ProductNotFoundException(id));
        ProductForm form = new ProductForm(
                p.id().id(),
                p.name().name(),
                p.price().price(),
                p.imageUrl().url()
        );
        model.addAttribute("productForm", form);
        return "admin/product_form";
    }

    @PutMapping("/{id}")
    public String update(
            @CurrentRole Role role,
            @PathVariable Long id,
            @Valid @ModelAttribute ProductForm productForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse
    ) {
        String errorView = checkErrors(bindingResult, httpServletResponse);
        if (errorView != null) {
            return errorView;
        }
        productService.updateProduct(
                id,
                productForm.getName(),
                productForm.getPrice(),
                productForm.getImageUrl(),
                role
        );
        return "redirect:/admin/products";
    }

    @DeleteMapping("/{id}")
    public String delete(@CurrentRole Role role, @PathVariable Long id) {
        productService.deleteProduct(id, role);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/unhide")
    public String unhide(@CurrentRole Role role, @PathVariable Long id) {
        productService.unhideProduct(id, role);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/hide")
    public String hide(@CurrentRole Role role, @PathVariable Long id) {
        productService.hideProduct(id, role);
        return "redirect:/admin/products";
    }

    private String checkErrors(BindingResult bindingResult,
            HttpServletResponse httpServletResponse) {
        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return "admin/product_form";
        }
        return null;
    }
}
