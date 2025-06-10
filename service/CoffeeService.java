package com.monarchsolutions.sms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.coffe.SaleGroupDTO;
import com.monarchsolutions.sms.dto.coffe.UserPurchasesDTO;
import com.monarchsolutions.sms.repository.CoffeeRepository;

@Service
public class CoffeeService {
  private final CoffeeRepository repo;

  public CoffeeService(CoffeeRepository repo) {
    this.repo = repo;
  }

  /**
   * Fetches the last 7 rows, then groups them by `sale`.
   * Each group has the shared `sale` and `created_at`,
   * a computed group total, and the list of line-items.
   */
  public List<SaleGroupDTO> getGroupedUserCoffeePurchases(Long userId, String lang) {
    List<UserPurchasesDTO> flat = repo.getUserCoffeePurchases(userId, lang);

    // group by sale in insertion order
    var grouped = flat.stream()
      .collect(Collectors.groupingBy(
        UserPurchasesDTO::getSale,
        LinkedHashMap::new,
        Collectors.toList()
      ));

    List<SaleGroupDTO> result = new ArrayList<>(grouped.size());
    for (var entry : grouped.entrySet()) {
      Number sale = entry.getKey();
      List<UserPurchasesDTO> items = entry.getValue();

      // all items share the same created_at
      LocalDateTime createdAt = items.get(0).getCreated_at();

      // sum up the item-level totals
      BigDecimal groupTotal = items.stream()
        .map(dto -> dto.getTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

      result.add(new SaleGroupDTO(sale, createdAt, groupTotal, items));
    }

    return result;
  }

}
