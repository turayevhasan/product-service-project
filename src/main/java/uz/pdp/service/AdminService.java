package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.conf.security.CustomUserDetails;
import uz.pdp.dto.*;
import uz.pdp.model.Attachment;
import uz.pdp.model.AuthUser;
import uz.pdp.model.Product;
import uz.pdp.repository.AttachmentRepository;
import uz.pdp.repository.ProductRepository;
import uz.pdp.repository.RoleRepository;
import uz.pdp.repository.UserRepository;
import uz.pdp.utils.ExcelWorker;
import uz.pdp.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class AdminService {
    private final UserRepository userRepository;
    private final ExcelWorker excelWorker;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(UserRepository userRepository, ExcelWorker excelWorker, RoleRepository roleRepository, ProductRepository productRepository, AttachmentRepository attachmentRepository, ProductService productService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.excelWorker = excelWorker;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.attachmentRepository = attachmentRepository;
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
    }

    public String downloadUserExcelFileNameByRole(String role, String fileName) {
        List<AuthUser> users = userRepository.getAllByRole(role);
        try {
            return excelWorker.writeUserDetailsToExcelAndGetName(users, fileName);
        } catch (IOException e) {
            return "Fail to write user details to excel";
        }
    }

    public void updateUser(UpdateUserDTO dto) {
        int userId = dto.id();
        AuthUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found !!!");
        } else {
            if (!dto.name().isEmpty()) {
                user.setName(dto.name());
            }
            if (dto.age() != 0) {
                user.setAge(dto.age());
            }
            if (!dto.gender().isEmpty()) {
                user.setGender(dto.gender());
            }
            if (!dto.password().isEmpty()) {
                if (Utils.isValidPassword(dto.password())) {
                    user.setPassword(passwordEncoder.encode(dto.password()));
                } else {
                    throw new RuntimeException("Password must be at least 6 characters");
                }
            }
            userRepository.update(user);
        }

    }

    public long saveProductImageDetails(Attachment image) {
        if (image.getOriginalName().isBlank()) {
            throw new RuntimeException("Image original name is blank");
        }
        if (image.getGeneratedName().isBlank()) {
            throw new RuntimeException("Image generated name is blank");
        }
        if (image.getContentType().isBlank()) {
            throw new RuntimeException("Image content type is blank");
        }
        if (image.getSize() < 0) {
            throw new RuntimeException("Image size is negative");
        }
        return attachmentRepository.save(image);
    }

    public List<AuthUser> getAllUsers(String role) {
        return userRepository.getAllByRole(role);
    }

    public int getUserSizeByRole(String role) {
        return userRepository.getAllByRole(role).size();
    }

    public int getProductSize() {
        return productRepository.getSizeOfAll();
    }

    public int getUserAddedHowManyProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AuthUser authUser = userDetails.getAuthUser();

        if (authUser != null) {
            Integer size = productRepository.getProductSizeByUserId(authUser.getId());
            return size == null ? 0 : size;
        }
        return 0;
    }

    public String deleteUser(int id) {
        String deletedUserRole = userRepository.delete(id);
        if (deletedUserRole != null) {
            return deletedUserRole;
        }
        return "USER";
    }

    public void changeUserRole(int userId, String roleCode) {
        try {
            roleRepository.updateUserRole(userId, roleCode);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public long createProduct(ProductAddDTO dto) {
        if (dto.name().isBlank()) {
            throw new RuntimeException("Product name is blank");
        }
        if (dto.description().isBlank()) {
            throw new RuntimeException("Product description is blank");
        }
        if (dto.country().isBlank()) {
            throw new RuntimeException("Product country is blank");
        }
        if (dto.price() < 0) {
            throw new RuntimeException("Product price is less than 0");
        }

        return productRepository.save(dto);
    }

    public void addImageToProduct(long productId, long imageId) {
        productRepository.addAttachmentIdToProductId(productId, imageId);
    }

    public void deleteProduct(long deleteProductId) throws IOException {
        List<ProductDTO> products = productService.getProducts();
        ProductDTO deleteProductDetails = null;
        for (ProductDTO product : products) {
            if (product.id() == deleteProductId) {
                deleteProductDetails = product;
                break;
            }
        }
        if (deleteProductDetails == null) {
            throw new RuntimeException("Product not found");
        } else {
            List<AttachmentDTO> images = deleteProductDetails.images();
            for (AttachmentDTO image : images) {
                attachmentRepository.delete(image.attachmentID());
                Path path = Path.of("E:/spring-core/downloads/product_images/" + image.generatedName());
                Files.delete(path);
            }
            productRepository.delete(deleteProductId);
        }
    }

    public void updateProduct(UpdateProductDTO dto) {
        int productId = dto.id();
        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        } else {
            if(!dto.name().isEmpty()){
                product.setName(dto.name());
            }
            if (!dto.description().isEmpty()) {
                product.setDescription(dto.description());
            }
            if (!dto.country().isEmpty()) {
                product.setProducedCountry(dto.country());
            }
            if (dto.price() != 0) {
                product.setPrice(dto.price());
            }

            productRepository.update(product);
        }
    }
}
