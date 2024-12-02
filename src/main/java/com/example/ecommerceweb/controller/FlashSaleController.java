package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.entity.FlashSaleItem;
import com.example.ecommerceweb.entity.FlashSale;
import com.example.ecommerceweb.service.CategoryService;
import com.example.ecommerceweb.service.FlashSaleItemService;
import com.example.ecommerceweb.service.FlashSaleService;
import com.example.ecommerceweb.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.ecommerceweb.util.Static.*;
import static com.example.ecommerceweb.util.DivideList.divideList;

@Slf4j
@Controller
@RequestMapping("${api.prefix}/shop-grid")
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final Translator translator;


    @GetMapping("/flash-sale")
    @ResponseBody
    public ResponseData<?> getFlashSale() {
        FlashSale currentFlashSale = flashSaleService.getCurrentFlashSale() != null ? flashSaleService.getCurrentFlashSale() : new FlashSale();
        return new ResponseData<>(200, translator.toLocated("get.flash-sale.success"), currentFlashSale);
    }


    @GetMapping("/filterByPrice")
    @ResponseBody
    public List<ProductDTO> filterProducts(
            @RequestParam(value = "minamount", required = false) String minAmount,
            @RequestParam(value = "maxamount", required = false) String maxAmount) {
        int min, max;
        try {
            min = minAmount != null ? Integer.parseInt(minAmount) : 0;
            max = maxAmount != null ? Integer.parseInt(maxAmount) : Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            min = 0;
            max = Integer.MAX_VALUE;
        }
        return productService.getProductByPriceRange(min, max).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
