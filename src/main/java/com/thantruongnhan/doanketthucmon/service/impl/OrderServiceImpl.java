package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.*;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentMethod;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus;
import com.thantruongnhan.doanketthucmon.repository.*;
import com.thantruongnhan.doanketthucmon.service.OrderService;
import com.thantruongnhan.doanketthucmon.service.WorkShiftService;
import com.thantruongnhan.doanketthucmon.util.SecurityUtil;
import com.thantruongnhan.doanketthucmon.controller.OrderWebSocketController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.thantruongnhan.doanketthucmon.entity.enums.Status;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderWebSocketController orderWebSocketController;
    private final BillRepository billRepository;
    private final TableRepository tableRepository;
    private final PromotionRepository promotionRepository;
    private final SecurityUtil securityUtil;
    private final BranchRepository branchRepository;
    private final WorkShiftService workShiftService;
    private final BranchProductRepository branchProductRepository;
    private final RecipeRepository recipeRepository;
    private final BranchIngredientRepository branchIngredientRepository;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderWebSocketController orderWebSocketController,
            BillRepository billRepository,
            TableRepository tableRepository,
            PromotionRepository promotionRepository,
            SecurityUtil securityUtil,
            BranchRepository branchRepository,
            WorkShiftService workShiftService,
            BranchProductRepository branchProductRepository,
            RecipeRepository recipeRepository,
            BranchIngredientRepository branchIngredientRepository) {
        this.orderRepository = orderRepository;
        this.orderWebSocketController = orderWebSocketController;
        this.billRepository = billRepository;
        this.tableRepository = tableRepository;
        this.securityUtil = securityUtil;
        this.promotionRepository = promotionRepository;
        this.branchRepository = branchRepository;
        this.workShiftService = workShiftService;
        this.branchProductRepository = branchProductRepository;
        this.recipeRepository = recipeRepository;
        this.branchIngredientRepository = branchIngredientRepository;
    }

    /**
     * HELPER METHOD: Lấy sản phẩm từ BranchProduct
     */
    private BranchProduct getBranchProduct(Long branchId, Long productId) {
        return branchProductRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new RuntimeException(
                        "Sản phẩm ID " + productId + " không có sẵn tại chi nhánh này hoặc đã bị vô hiệu hóa!"));
    }

    private BigDecimal getPriceFromBranchProduct(BranchProduct branchProduct) {
        if (branchProduct.getCustomPrice() != null && branchProduct.getCustomPrice() > 0) {
            return BigDecimal.valueOf(branchProduct.getCustomPrice());
        }
        return branchProduct.getProduct().getPrice();
    }

    // trừ kho
    private void deductIngredients(Order order) {
        Long branchId = order.getBranch().getId();

        System.out.println("========== BẮT ĐẦU TRỪ KHO ==========");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Branch ID: " + branchId);

        for (OrderItem item : order.getItems()) {
            Long productId = item.getProduct().getId();
            int orderQuantity = item.getQuantity();

            System.out.println("\n--- Sản phẩm: " + item.getProduct().getName() + " ---");
            System.out.println("Product ID: " + productId);
            System.out.println("Order Quantity: " + orderQuantity);

            List<Recipe> recipes = recipeRepository.findByProductId(productId);
            System.out.println("Số recipe tìm thấy: " + recipes.size());

            for (Recipe recipe : recipes) {
                Long ingredientId = recipe.getIngredient().getId();
                double quantityRequired = recipe.getQuantityRequired();
                double totalRequired = quantityRequired * orderQuantity;

                System.out.println("\n  Nguyên liệu: " + recipe.getIngredient().getName());
                System.out.println("  Ingredient ID: " + ingredientId);
                System.out.println("  Quantity Required (per product): " + quantityRequired);
                System.out.println("  Total Required: " + totalRequired);

                BranchIngredient stock = branchIngredientRepository
                        .findByBranchIdAndIngredientId(branchId, ingredientId)
                        .orElseThrow(() -> new RuntimeException(
                                "Chi nhánh không có nguyên liệu: " + recipe.getIngredient().getName()));

                System.out.println("  Stock hiện tại: " + stock.getQuantity());
                System.out.println("  Đơn vị: " + stock.getIngredient().getUnit());

                if (stock.getQuantity() < totalRequired) {
                    System.out.println("  ❌ KHÔNG ĐỦ KHO!");
                    throw new RuntimeException(
                            "Không đủ nguyên liệu: " + recipe.getIngredient().getName()
                                    + " (Cần: " + totalRequired + ", Còn: " + stock.getQuantity() + ")");
                }

                stock.setQuantity(stock.getQuantity() - totalRequired);
                branchIngredientRepository.save(stock);
                System.out.println("  Đã trừ. Còn lại: " + stock.getQuantity());
            }
        }

        System.out.println("========== KẾT THÚC TRỪ KHO ==========");
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        System.out.println("========== CREATE ORDER START ==========");

        // 0. LẤY THÔNG TIN NHÂN VIÊN HIỆN TẠI
        User currentUser = null;
        boolean isEmployee = false;

        try {
            currentUser = securityUtil.getCurrentUser();
            String roleName = currentUser.getRole().name();
            if (roleName.equals("EMPLOYEE") || roleName.equals("MANAGER") || roleName.equals("ADMIN")) {
                order.setEmployee(currentUser);
                isEmployee = true;
            } else if (roleName.equals("CUSTOMER")) {
                isEmployee = false;
            }
        } catch (Exception e) {
            // handle error
        }
        System.out.println("👤 Nhân viên tạo đơn: " + currentUser.getUsername());
        if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
            order.setCustomerName("Khách lẻ");
        }
        System.out.println("👤 Tên khách hàng: " + order.getCustomerName());
        // 1. XỬ LÝ BRANCH
        Branch assignedBranch = null;
        if (order.getBranch() == null || order.getBranch().getId() == null) {
            if (currentUser.getBranch() != null) {
                assignedBranch = currentUser.getBranch();
                System.out.println("🏢 Sử dụng branch của nhân viên: Branch ID = " + assignedBranch.getId());
            } else {
                throw new RuntimeException("Nhân viên chưa được gán chi nhánh!");
            }
        } else {
            assignedBranch = branchRepository.findById(order.getBranch().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh!"));
        }
        order.setBranch(assignedBranch);

        // 2. XỬ LÝ WORK SHIFT
        WorkShift assignedShift = null;
        try {
            assignedShift = workShiftService.getCurrentActiveShift()
                    .orElse(null); // ← Cho phép null nếu không có ca
            if (assignedShift != null) {
                System.out.println("⏰ Ca làm việc: " + assignedShift.getName());
                order.setWorkShift(assignedShift);
            } else {
                System.out.println("⏰ Không có ca làm việc đang hoạt động");
            }
        } catch (Exception e) {
            System.out.println("⏰ Không thể lấy ca làm việc: " + e.getMessage());
        }
        order.setWorkShift(assignedShift);

        // 3. Validate và load Table
        if (order.getTable() != null && order.getTable().getId() != null) {
            TableEntity table = tableRepository.findById(order.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn!"));

            // KIỂM TRA XEM BÀN ĐÃ CÓ ĐƠN CHƯA THANH TOÁN CHƯA
            List<Order> existingOrders = orderRepository.findByTableIdAndStatusNotIn(
                    table.getId(),
                    Arrays.asList(OrderStatus.PAID, OrderStatus.CANCELED));

            if (!existingOrders.isEmpty()) {
                // Có đơn chưa thanh toán -> GỘP VÀO ĐƠN CŨ
                Order existingOrder = existingOrders.get(0);
                existingOrder.getItems().size();
                System.out.println(
                        "🔄 Bàn " + table.getNumber() + " đã có đơn #" + existingOrder.getId() + " chưa thanh toán");
                System.out.println("📦 Gộp món mới vào đơn hiện tại...");

                // CẬP NHẬT THÔNG TIN CHO ĐƠN CŨ (NẾU CHƯA CÓ)
                if (existingOrder.getEmployee() == null) {
                    existingOrder.setEmployee(currentUser);
                    System.out.println("👤 Cập nhật employee cho đơn cũ");
                }
                if (existingOrder.getBranch() == null) {
                    existingOrder.setBranch(assignedBranch);
                    System.out.println("🏢 Cập nhật branch cho đơn cũ");
                }
                if (existingOrder.getWorkShift() == null) {
                    existingOrder.setWorkShift(assignedShift);
                    System.out.println("⏰ Cập nhật work shift cho đơn cũ");
                }

                // THÊM CÁC MÓN MỚI TỪ BRANCHPRODUCT
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    for (OrderItem newItem : order.getItems()) {
                        if (newItem.getProduct() != null && newItem.getProduct().getId() != null) {
                            Long productId = newItem.getProduct().getId();

                            // LẤY TỪ BRANCHPRODUCT ĐỂ VALIDATE
                            BranchProduct branchProduct = getBranchProduct(assignedBranch.getId(), productId);
                            Product product = branchProduct.getProduct();

                            // Kiểm tra xem sản phẩm đã có trong đơn chưa
                            boolean found = false;
                            for (OrderItem existingItem : existingOrder.getItems()) {
                                if (existingItem.getProduct().getId().equals(productId)) {
                                    // Cộng dồn số lượng
                                    existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                                    existingItem.calculateSubtotal();
                                    found = true;
                                    System.out.println("Cộng dồn: " + product.getName() + " x" + newItem.getQuantity());
                                    break;
                                }
                            }

                            if (!found) {
                                // Thêm món mới
                                OrderItem itemToAdd = new OrderItem();
                                itemToAdd.setOrder(existingOrder);
                                itemToAdd.setProduct(product);
                                itemToAdd.setBranchProduct(branchProduct);
                                itemToAdd.setQuantity(newItem.getQuantity());

                                // SỬ DỤNG GIÁ TỪ FRONTEND NẾU CÓ
                                if (newItem.getPrice() != null && newItem.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                                    itemToAdd.setPrice(newItem.getPrice());
                                    System.out.println("Sử dụng giá từ frontend: " + newItem.getPrice());
                                } else {
                                    itemToAdd.setPrice(getPriceFromBranchProduct(branchProduct));
                                    System.out.println("Fallback: Lấy giá từ BranchProduct");
                                }

                                itemToAdd.calculateSubtotal();
                                existingOrder.getItems().add(itemToAdd);
                                System.out.println("Thêm món mới: " + product.getName() + " x" + newItem.getQuantity()
                                        + " - Giá: " + itemToAdd.getPrice());
                            }
                        }
                    }
                }

                // CHUYỂN TRẠNG THÁI VỀ PREPARING NẾU ĐÃ COMPLETED
                if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
                    existingOrder.setStatus(OrderStatus.PREPARING);
                    System.out.println("🔄 Đơn đã hoàn thành -> chuyển về PREPARING");
                }

                // Tính lại tổng tiền
                existingOrder.recalcTotal();
                BigDecimal originalTotal = existingOrder.getTotalAmount();
                System.out.println("💰 Tổng tiền gốc sau khi gộp: " + originalTotal);

                // Áp dụng promotion nếu có
                if (existingOrder.getPromotion() != null && existingOrder.getPromotion().getId() != null) {
                    Promotion promotion = promotionRepository.findById(existingOrder.getPromotion().getId())
                            .orElse(null);

                    if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                        BigDecimal discount = BigDecimal.ZERO;

                        if (promotion.getDiscountPercentage() != null
                                && promotion.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                            discount = originalTotal.multiply(promotion.getDiscountPercentage())
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        } else if (promotion.getDiscountAmount() != null
                                && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            discount = promotion.getDiscountAmount();
                        }

                        BigDecimal finalTotal = originalTotal.subtract(discount);
                        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                            finalTotal = BigDecimal.ZERO;
                        }

                        existingOrder.setTotalAmount(finalTotal);
                        System.out.println("🎁 Discount: " + discount);
                        System.out.println("💰 Final Total: " + finalTotal);
                    } else {
                        existingOrder.setPromotion(null);
                    }
                }

                // Cập nhật thời gian
                existingOrder.setUpdatedAt(LocalDateTime.now());

                // Lưu đơn hàng đã gộp
                Order savedOrder = orderRepository.save(existingOrder);

                savedOrder.getItems().size();

                System.out.println("Đã gộp món vào đơn #" + savedOrder.getId());
                System.out.println("========== CREATE ORDER END (MERGED) ==========");

                // Gửi WebSocket update
                orderWebSocketController.sendOrderUpdate(savedOrder);

                return savedOrder;
            }

            // Nếu không có đơn nào -> tạo mới bình thường
            table.setStatus(Status.OCCUPIED);
            table.setUpdatedAt(LocalDateTime.now());
            tableRepository.save(table);
            order.setTable(table);
        }

        // 4. XỬ LÝ ORDERITEMS CHO ĐƠN MỚI
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            System.out.println("📦 Processing " + order.getItems().size() + " items...");

            List<OrderItem> processedItems = new ArrayList<>();

            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Long productId = item.getProduct().getId();

                    // LẤY TỪ BRANCHPRODUCT ĐỂ VALIDATE
                    BranchProduct branchProduct = getBranchProduct(assignedBranch.getId(), productId);
                    Product product = branchProduct.getProduct();

                    item.setProduct(product);
                    item.setBranchProduct(branchProduct);

                    // SỬ DỤNG GIÁ TỪ FRONTEND NẾU CÓ
                    if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        System.out
                                .println("Sử dụng giá từ frontend: " + item.getPrice() + " cho " + product.getName());
                        // GIỮ NGUYÊN GIÁ TỪ FRONTEND
                    } else {
                        // FALLBACK: Lấy từ BranchProduct
                        item.setPrice(getPriceFromBranchProduct(branchProduct));
                        System.out.println("Fallback: Lấy giá từ BranchProduct: " + item.getPrice() + " cho "
                                + product.getName());
                    }

                    item.setOrder(order);
                    item.calculateSubtotal();

                    processedItems.add(item);

                    System.out.println("Thêm sản phẩm: " + product.getName() + " x" + item.getQuantity() +
                            " - Giá: " + item.getPrice());
                }
            }

            order.setItems(processedItems);
        }

        // 5. Xử lý Promotion (nếu có)
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion promotion = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);

            if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                order.setPromotion(promotion);
                System.out.println("🎁 Promotion applied: " + promotion.getName());
            }
        }

        // 6. Tính tổng tiền (trước khi áp promotion)
        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        System.out.println("💰 Original Total: " + originalTotal);

        // 7. Áp dụng promotion (nếu có)
        if (order.getPromotion() != null) {
            Promotion promo = order.getPromotion();
            BigDecimal discount = BigDecimal.ZERO;

            if (promo.getDiscountPercentage() != null && promo.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                discount = originalTotal.multiply(promo.getDiscountPercentage())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else if (promo.getDiscountAmount() != null && promo.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                discount = promo.getDiscountAmount();
            }

            BigDecimal finalTotal = originalTotal.subtract(discount);
            if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                finalTotal = BigDecimal.ZERO;
            }

            order.setTotalAmount(finalTotal);
            System.out.println("🎁 Discount: " + discount);
            System.out.println("💰 Final Total: " + finalTotal);
        }

        // 8. Set thời gian
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // 9. Set trạng thái mặc định
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // 10. Lưu order mới
        Order savedOrder = orderRepository.save(order);

        savedOrder.getItems().size();

        System.out.println("Order saved with ID: " + savedOrder.getId());
        System.out.println("========== CREATE ORDER END ==========");

        orderWebSocketController.sendNewOrder(savedOrder);

        return savedOrder;
    }

    /**
     * Tính lại tổng tiền sau khi áp dụng khuyến mãi (nếu có).
     */
    private BigDecimal applyPromotion(Order order, BigDecimal originalTotal) {
        if (order.getPromotion() == null) {
            return originalTotal;
        }

        try {
            Promotion promo = order.getPromotion();

            // Nếu khuyến mãi có ngày hết hạn → kiểm tra
            if (promo.getEndDate() != null && promo.getEndDate().isBefore(LocalDate.now())) {
                order.setPromotion(null);
                return originalTotal;
            }

            // Nếu là giảm theo %
            if (promo.getDiscountPercentage() != null && promo.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = originalTotal.multiply(promo.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                return originalTotal.subtract(discountAmount);
            }

            // Nếu là giảm theo số tiền cố định
            if (promo.getDiscountAmount() != null && promo.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discounted = originalTotal.subtract(promo.getDiscountAmount());
                return discounted.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discounted;
            }

            return originalTotal;
        } catch (Exception e) {
            System.err.println("Lỗi khi áp dụng khuyến mãi: " + e.getMessage());
            return originalTotal;
        }
    }

    @Override
    @Transactional
    public Order addMultipleProductsToOrder(Long orderId, List<Map<String, Object>> newItems) {
        System.out.println("========== ADD MULTIPLE PRODUCTS START ==========");

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        System.out.println("📦 Đơn hàng hiện tại: #" + order.getId() + " - Status: " + order.getStatus());

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Không thể thêm món vào đơn đã thanh toán");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Không thể thêm món vào đơn đã hủy");
        }

        boolean wasCompleted = (order.getStatus() == OrderStatus.COMPLETED);
        Long branchId = order.getBranch().getId();

        for (Map<String, Object> item : newItems) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer quantity = (Integer) item.get("quantity");

            System.out.println("➕ Thêm sản phẩm #" + productId + " x" + quantity);

            // LẤY TỪ BRANCHPRODUCT
            BranchProduct branchProduct = getBranchProduct(branchId, productId);
            Product product = branchProduct.getProduct();

            boolean productExists = false;
            for (OrderItem existingItem : order.getItems()) {
                if (existingItem.getProduct().getId().equals(productId)) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    existingItem.calculateSubtotal();
                    productExists = true;
                    System.out.println("Đã cộng dồn số lượng sản phẩm #" + productId);
                    break;
                }
            }

            if (!productExists) {
                OrderItem newItem = new OrderItem();
                newItem.setOrder(order);
                newItem.setProduct(product);
                newItem.setBranchProduct(branchProduct); // Set BranchProduct
                newItem.setQuantity(quantity);
                newItem.setPrice(getPriceFromBranchProduct(branchProduct)); // Lấy giá từ BranchProduct
                newItem.calculateSubtotal();
                order.getItems().add(newItem);
                System.out.println("Đã thêm sản phẩm mới #" + productId);
            }
        }

        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        System.out.println("💰 Tổng tiền gốc: " + originalTotal);

        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion promotion = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);

            if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                order.setPromotion(promotion);
                BigDecimal finalTotal = applyPromotion(order, originalTotal);
                order.setTotalAmount(finalTotal);
                System.out.println("🎁 Đã áp dụng khuyến mãi: " + promotion.getName());
                System.out.println("💰 Tổng tiền sau khuyến mãi: " + finalTotal);
            } else {
                order.setPromotion(null);
            }
        }

        if (wasCompleted) {
            order.setStatus(OrderStatus.PREPARING);
            System.out.println("🔄 Đơn đã hoàn thành -> chuyển về PREPARING");
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        updatedOrder.getItems().size();

        System.out.println("Đã lưu đơn hàng với tổng tiền: " + updatedOrder.getTotalAmount());
        System.out.println("========== ADD MULTIPLE PRODUCTS END ==========");

        orderWebSocketController.sendOrderUpdate(updatedOrder);

        return updatedOrder;
    }

    @Override
    @Transactional
    public Order addProductToOrder(Long orderId, Product product, int quantity) {
        System.out.println("========== ADD SINGLE PRODUCT START ==========");

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Không thể thêm món vào đơn đã thanh toán");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Không thể thêm món vào đơn đã hủy");
        }

        boolean wasCompleted = (order.getStatus() == OrderStatus.COMPLETED);
        Long branchId = order.getBranch().getId();

        // LẤY TỪ BRANCHPRODUCT
        BranchProduct branchProduct = getBranchProduct(branchId, product.getId());

        boolean productExists = false;
        for (OrderItem existingItem : order.getItems()) {
            if (existingItem.getProduct().getId().equals(product.getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.calculateSubtotal();
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setBranchProduct(branchProduct); // Set BranchProduct
            item.setQuantity(quantity);
            item.setPrice(getPriceFromBranchProduct(branchProduct)); // Lấy giá từ BranchProduct
            item.calculateSubtotal();
            order.getItems().add(item);
        }

        order.recalcTotal();
        BigDecimal finalTotal = applyPromotion(order, order.getTotalAmount());
        order.setTotalAmount(finalTotal);

        if (wasCompleted) {
            order.setStatus(OrderStatus.PREPARING);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);

        updated.getItems().size();

        System.out.println("Thêm sản phẩm thành công");
        System.out.println("========== ADD SINGLE PRODUCT END ==========");

        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderStatus status, PaymentMethod paymentMethod) {
        System.out.println("========== UPDATE ORDER WITH PAYMENT START ==========");
        System.out.println("Order ID: " + id);
        System.out.println("New Status: " + status);
        System.out.println("Payment Method: " + paymentMethod);

        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

        LocalDateTime now = LocalDateTime.now();
        order.setStatus(status);
        order.setUpdatedAt(now);

        // XỬ LÝ PROMOTION
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion freshPromo = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);
            if (freshPromo != null && Boolean.TRUE.equals(freshPromo.getIsActive())) {
                order.setPromotion(freshPromo);
            } else {
                order.setPromotion(null);
            }
        }

        // TÍNH LẠI TỔNG TIỀN
        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        BigDecimal finalTotal = applyPromotion(order, originalTotal);
        order.setTotalAmount(finalTotal);

        // XỬ LÝ THANH TOÁN
        if (status == OrderStatus.PAID) {
            // KIỂM TRA ĐÃ CÓ BILL CHƯA
            boolean billExists = billRepository.existsByOrderId(order.getId());

            if (billExists) {
                System.out.println("⚠️ Bill already exists for order #" + order.getId());
            } else {
                order.setPaidAt(now);

                Bill bill = Bill.builder()
                        .order(order)
                        .totalAmount(finalTotal)
                        .paymentMethod(paymentMethod)
                        .paymentStatus(PaymentStatus.PAID)
                        .issuedAt(now)
                        .notes("Hóa đơn tự động cho đơn #" + order.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

                billRepository.save(bill);
                System.out.println("💵 Bill created successfully");
            }

            freeOrUpdateTable(order, Status.FREE);

        } else if (status == OrderStatus.CANCELED) {
            freeOrUpdateTable(order, Status.FREE);
        } else if (status == OrderStatus.COMPLETED) {
            freeOrUpdateTable(order, Status.OCCUPIED);
        }

        Order updated = orderRepository.save(order);

        // FORCE LOAD
        updated.getItems().size();
        if (updated.getTable() != null)
            updated.getTable().getNumber();
        if (updated.getPromotion() != null)
            updated.getPromotion().getName();

        System.out.println("Order updated successfully");
        System.out.println("========== UPDATE ORDER WITH PAYMENT END ==========");

        orderWebSocketController.sendOrderUpdate(updated);

        return updated;
    }

    private void freeOrUpdateTable(Order order, Status status) {
        if (order.getTable() != null && order.getTable().getId() != null) {
            TableEntity table = tableRepository.findById(order.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn!"));
            table.setStatus(status);
            table.setUpdatedAt(LocalDateTime.now());
            tableRepository.save(table);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        orderWebSocketController.sendOrderDeleted(id);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchOrders(keyword.toLowerCase());
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        System.out.println("========== UPDATE ORDER STATUS START ==========");
        System.out.println("Order ID: " + id);
        System.out.println("New Status: " + status);

        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

        System.out.println("Current Status: " + order.getStatus());

        OrderStatus oldStatus = order.getStatus();

        LocalDateTime now = LocalDateTime.now();
        order.setStatus(status);
        order.setUpdatedAt(now);

        if (status == OrderStatus.COMPLETED && oldStatus != OrderStatus.COMPLETED) {
            System.out.println("🔄 Trạng thái chuyển sang COMPLETED - Bắt đầu trừ nguyên liệu...");

            try {
                deductIngredients(order);
                System.out.println("Trừ nguyên liệu thành công!");
            } catch (Exception e) {
                System.err.println("❌ LỖI KHI TRỪ NGUYÊN LIỆU:");
                e.printStackTrace();
                throw new RuntimeException("Không thể trừ nguyên liệu: " + e.getMessage(), e);
            }

            freeOrUpdateTable(order, Status.OCCUPIED);
        }

        Order updated = orderRepository.save(order);

        // FORCE LOAD TẤT CẢ RELATIONSHIPS
        updated.getItems().size();
        updated.getItems().forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().getName();
                // FORCE LOAD CATEGORY (đây là nguyên nhân chính)
                if (item.getProduct().getCategory() != null) {
                    item.getProduct().getCategory().getName();
                }
            }
            if (item.getBranchProduct() != null) {
                item.getBranchProduct().getId();
            }
        });

        if (updated.getPromotion() != null) {
            updated.getPromotion().getName();
        }
        if (updated.getTable() != null) {
            updated.getTable().getNumber();
        }
        if (updated.getBranch() != null) {
            updated.getBranch().getName();
        }
        if (updated.getEmployee() != null) {
            updated.getEmployee().getUsername();
        }

        System.out.println("Saved Status: " + updated.getStatus());
        System.out.println("========== UPDATE ORDER STATUS END ==========");

        orderWebSocketController.sendOrderUpdate(updated);

        return updated;
    }
}