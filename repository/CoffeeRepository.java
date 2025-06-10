package com.monarchsolutions.sms.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.monarchsolutions.sms.dto.coffe.UserPurchasesDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CoffeeRepository {
    
  @PersistenceContext
  private EntityManager em;

  /**
   * Returns the last 7 purchase rows for the given user,
   * each row with coffee_sale_id, sale, item_name, created_at,
   * quantity, unit_price and line total.
   */
  public List<UserPurchasesDTO> getUserCoffeePurchases(Long userId, String lang) {
    String sql = """
      SELECT
        cs.coffee_sale_id,
        cs.sale,
        CASE WHEN :lang = 'es' THEN cm.name_es ELSE cm.name_en END AS item_name,
        cs.created_at,
        cs.quantity,
        cs.unit_price,
        cs.quantity * cs.unit_price AS total
      FROM coffee_sales cs
      JOIN coffee_menu  cm ON cs.menu_id = cm.menu_id
      WHERE cs.user_id = :userId
      ORDER BY cs.created_at DESC
      LIMIT 7
      """;

    Query q = em.createNativeQuery(sql);
    q.setParameter("lang",   lang);
    q.setParameter("userId", userId);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<UserPurchasesDTO> list = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      UserPurchasesDTO dto = new UserPurchasesDTO();
      dto.setCoffee_sale_id( ((Number) r[0]).longValue());
      dto.setSale((Number) r[1]);
      dto.setItem_name((String) r[2]);
      dto.setCreated_at(((java.sql.Timestamp) r[3]).toLocalDateTime());
      dto.setQuantity((Number) r[4]);
      dto.setUnit_price((BigDecimal) r[5]);
      dto.setTotal((BigDecimal) r[6]);
      list.add(dto);
    }
    return list;
  }
  
}
