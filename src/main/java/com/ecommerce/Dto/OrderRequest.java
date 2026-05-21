package com.ecommerce.Dto;

import java.math.BigDecimal;

public class OrderRequest {
    private Long productId;
    private Long quantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }
}
