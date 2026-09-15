package com.shopsphere.orderservice.dto.product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdsRequest {

  private List<Long> ids;
}
