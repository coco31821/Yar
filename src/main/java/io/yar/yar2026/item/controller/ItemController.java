package io.yar.yar2026.item.controller;


import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.item.ItemService;
import io.yar.yar2026.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ApiResponse<List<ItemResponse>> getAllItems(){
        List<ItemResponse> response = itemService.getItems();

        return ApiResponse.ok(response,null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ItemResponse> getItem(@PathVariable Long id) {

        ItemResponse response = itemService.getItem(id);

        return ApiResponse.ok(response,null);
    }







}
