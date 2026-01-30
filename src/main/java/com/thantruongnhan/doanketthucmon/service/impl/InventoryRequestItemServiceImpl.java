package com.thantruongnhan.doanketthucmon.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Ingredient;
import com.thantruongnhan.doanketthucmon.entity.InventoryRequest;
import com.thantruongnhan.doanketthucmon.entity.InventoryRequestItem;
import com.thantruongnhan.doanketthucmon.entity.enums.RequestStatus;
import com.thantruongnhan.doanketthucmon.repository.IngredientRepository;
import com.thantruongnhan.doanketthucmon.repository.InventoryRequestItemRepository;
import com.thantruongnhan.doanketthucmon.repository.InventoryRequestRepository;
import com.thantruongnhan.doanketthucmon.service.InventoryRequestItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestItemServiceImpl
        implements InventoryRequestItemService {

    private final InventoryRequestItemRepository itemRepo;
    private final InventoryRequestRepository requestRepo;
    private final IngredientRepository ingredientRepo;

    @Override
    public InventoryRequestItem addItem(Long requestId, Long ingredientId, Double quantity) {

        InventoryRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Cannot modify approved/rejected request");
        }

        if (itemRepo.existsByRequestIdAndIngredientId(requestId, ingredientId)) {
            throw new RuntimeException("Ingredient already exists in this request");
        }

        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        InventoryRequestItem item = InventoryRequestItem.builder()
                .request(request)
                .ingredient(ingredient)
                .quantity(quantity)
                .build();

        return itemRepo.save(item);
    }

    @Override
    public InventoryRequestItem updateQuantity(Long itemId, Double quantity) {

        InventoryRequestItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getRequest().getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Cannot modify approved/rejected request");
        }

        item.setQuantity(quantity);
        return itemRepo.save(item);
    }

    @Override
    public void deleteItem(Long itemId) {

        InventoryRequestItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getRequest().getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Cannot delete item of approved/rejected request");
        }

        itemRepo.delete(item);
    }

    @Override
    public List<InventoryRequestItem> getItemsByRequest(Long requestId) {
        return itemRepo.findByRequestId(requestId);
    }
}
