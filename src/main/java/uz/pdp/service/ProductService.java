package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.pdp.dto.AttachmentDTO;
import uz.pdp.dto.ProductDTO;
import uz.pdp.model.Attachment;
import uz.pdp.model.AuthUser;
import uz.pdp.model.Product;
import uz.pdp.repository.AttachmentRepository;
import uz.pdp.repository.ProductRepository;
import uz.pdp.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    public List<ProductDTO> getProducts() throws IOException {
        List<ProductDTO> products = new ArrayList<>();
        List<Product> all = productRepository.getAll();

        int i = 0;
        for (Product product : all) {
            Optional<AuthUser> createdBy = userRepository.findById(product.getCreatorId());
            Integer productId = all.get(i).getId();
            List<Attachment> productImages = attachmentRepository.getAllAttachmentByProductId(productId);

            List<AttachmentDTO> images = new ArrayList<>();

            for (Attachment productImage : productImages) {
                Path path = Path.of("E:/spring-core/downloads/product_images/" + productImage.getGeneratedName());
                byte[] imageBytes = Files.readAllBytes(path);
                images.add(new AttachmentDTO(
                        productImage.getId(),
                        productImage.getGeneratedName(),
                        Base64.getEncoder().encodeToString(imageBytes),
                        productImage.getContentType())
                );
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = product.getCreatedAt().format(formatter);

            createdBy.ifPresent(authUser -> products.add(new ProductDTO(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getProducedCountry(),
                    product.getPrice() + "$",
                    formattedDate,
                    authUser.getName(),
                    images
            )));
            i++;
        }
        return products;
    }

}