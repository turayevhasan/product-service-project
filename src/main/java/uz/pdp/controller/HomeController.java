package uz.pdp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.dto.ProductDTO;
import uz.pdp.service.ProductService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final ProductService productService;

    @GetMapping("/home")
    public ModelAndView mainHome() throws IOException {
        ModelAndView view = new ModelAndView("/home/home");
        List<ProductDTO> products = productService.getProducts();
        view.addObject("products", products);
        return view;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }


}
