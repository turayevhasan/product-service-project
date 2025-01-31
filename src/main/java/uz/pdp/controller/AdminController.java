package uz.pdp.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.conf.security.CustomUserDetails;
import uz.pdp.dto.*;
import uz.pdp.model.Attachment;
import uz.pdp.model.AuthUser;
import uz.pdp.repository.AttachmentRepository;
import uz.pdp.service.AdminService;
import uz.pdp.service.AuthUserService;
import uz.pdp.service.ProductService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthUserService authUserService;
    private final ProductService productService;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/home")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ModelAndView home() {
        ModelAndView view = new ModelAndView("admin/dashboard");

        int usersCount = adminService.getUserSizeByRole("USER");
        String usersExcelName = adminService.downloadUserExcelFileNameByRole("USER", "users_table");
        int adminsCount = adminService.getUserSizeByRole("ADMIN");
        String adminsExcelName = adminService.downloadUserExcelFileNameByRole("ADMIN", "admins_table");
        int managersCount = adminService.getUserSizeByRole("MANAGER");
        String managersExcelName = adminService.downloadUserExcelFileNameByRole("MANAGER", "managers_table");

        int productsCount = adminService.getProductSize();
        int adminAddedProducts = adminService.getUserAddedHowManyProducts();

        view.addObject("usersCount", usersCount);
        view.addObject("usersExcelName", usersExcelName);
        view.addObject("adminsCount", adminsCount);
        view.addObject("adminsExcelName", adminsExcelName);
        view.addObject("managersCount", managersCount);
        view.addObject("managersExcelName", managersExcelName);
        view.addObject("productsCount", productsCount);
        view.addObject("adminAddedProducts", adminAddedProducts);

        return view;
    }

    @PostMapping("/delete/user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String deleteUser(@RequestParam("deleteUserId") int id) {
        String roleCode = adminService.deleteUser(id);

        if (roleCode.equals("ADMIN")) {
            return "redirect:/admin/view/admins";
        } else if (roleCode.equals("MANAGER")) {
            return "redirect:/admin/view/managers";
        } else {
            return "redirect:/admin/view/users";
        }
    }

    @PostMapping("/change/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String changeRoleUser(
            @RequestParam("userId") int userId,
            @RequestParam("roleCode") String roleCode,
            @RequestParam("redirectPath") String redirectPath) {

        adminService.changeUserRole(userId, roleCode);

        return redirectPath;
    }

    @GetMapping("/view/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ModelAndView homeUsers() {
        ModelAndView view = new ModelAndView("admin/users");
        List<AuthUser> users = adminService.getAllUsers("USER");
        view.addObject("users", users);
        return view;
    }

    @GetMapping("/view/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ModelAndView homeAdmins() {
        ModelAndView view = new ModelAndView("admin/admins");
        List<AuthUser> admins = adminService.getAllUsers("ADMIN");
        view.addObject("admins", admins);
        return view;
    }

    @GetMapping("/view/managers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ModelAndView homeManagers() {
        ModelAndView view = new ModelAndView("admin/managers");
        List<AuthUser> managers = adminService.getAllUsers("MANAGER");
        view.addObject("managers", managers);
        return view;
    }

    @PostMapping("/create/user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String createNewUser(@ModelAttribute RegisterUserDTO dto) {
        authUserService.save(dto, true);
        return "redirect:/admin/view/users";
    }

    @PostMapping("/update/user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String updateUser(
            @RequestParam(name = "userId") int userId,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "age", required = false, defaultValue = "0") Integer age,
            @RequestParam(name = "gender", required = false, defaultValue = "") String gender,
            @RequestParam(name = "password", required = false, defaultValue = "") String password,
            @RequestParam(name = "redirectPath") String redirectPath
    ) {
        adminService.updateUser(new UpdateUserDTO(userId, name, age, gender, password));
        return redirectPath;
    }

    @PostMapping("/update/product")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String updateProduct(
            @RequestParam(name = "productId") int productId,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "description", required = false, defaultValue = "") String description,
            @RequestParam(name = "country", required = false, defaultValue = "") String country,
            @RequestParam(name = "price", required = false, defaultValue = "0") Long price,
            @RequestParam(name = "redirectPath") String redirectPath
    ) {
        adminService.updateProduct(new UpdateProductDTO(productId, name, description, country, price));
        return redirectPath;
    }

    @GetMapping("/view/products")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ModelAndView viewProduct() throws IOException {
        List<ProductDTO> products = productService.getProducts();
        ModelAndView view = new ModelAndView("admin/product");
        view.addObject("products", products);
        return view;
    }

    @PostMapping("/delete/product")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String deleteProduct(@RequestParam("deleteProductId") long deleteProductId) throws IOException {
        adminService.deleteProduct(deleteProductId);
        return "redirect:/admin/view/products";
    }

    @PostMapping("/create/product")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public String createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("country") String country,
            @RequestParam("price") long price,
            @RequestParam("images") MultipartFile[] images) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AuthUser admin = userDetails.getAuthUser();

        ProductAddDTO productAddDTO = new ProductAddDTO(name, description, country, price, admin.getId());

        long productId = adminService.createProduct(productAddDTO);

        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            String generatedFilename = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);
            String contentType = image.getContentType();
            long size = image.getSize();

            Attachment attachment = Attachment.builder()
                    .originalName(originalFilename)
                    .generatedName(generatedFilename)
                    .contentType(contentType)
                    .size(size)
                    .build();

            long imageId = adminService.saveProductImageDetails(attachment); //save image
            adminService.addImageToProduct(productId, imageId); //add product id with image id

            Path path = Path.of("E:/spring-core/downloads/product_images/" + generatedFilename);
            InputStream inputStream = image.getInputStream();
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING); //save image to folder
        }
        return "redirect:/admin/view/products";
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) {
        try {
            Path filePath = Path.of("E:/spring-core/downloads/files/" + filename);
            FileSystemResource resource = new FileSystemResource(filePath);

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
