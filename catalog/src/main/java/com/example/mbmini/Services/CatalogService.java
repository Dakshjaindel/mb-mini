package com.example.mbmini.Services;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.RepoConnections.CatalogRepo;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import com.example.mbminishared.ItemRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {
    @Autowired
    private CatalogRepo repository;

    @Autowired
    private RedisMethods redisMethods;


    @Autowired
    private CatalogRedisService catalogRedisService;

    @Autowired
    private EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ItemRepo itemRepo;


    public String create(Catalog catalog){
        Catalog saved= repository.save(catalog);
        redisMethods.addInRedis(saved,saved.getId().toString());
        String key= String.valueOf(saved.getId());
        return "Saved with the Id"+ key;
    }


    public String Update(long Id,String productName,Integer quantity,BigDecimal price, Boolean isActive){
        catalogRedisService.Update( Id, productName, quantity, price,  isActive);
        Catalog catalog=repository.findById(Id).orElseThrow(()->new RuntimeException("Catalog not Found with Id: "+ Id));
        if (productName != null){
            catalog.setProductName(productName);
        }
        if (quantity != null){
            catalog.setQuantity(quantity);
        }
        if (price !=null){
            catalog.setPrice(price);
        }
        if ( isActive !=null){
            catalog.setActive(isActive);
        }
        repository.save(catalog);
        
        return "Updated Successfully";
    }


    public String updateQuantity(Long productId, Integer quantity){
        Catalog catalog=repository.findById(productId).orElseThrow(()-> new RuntimeException("Catalog not found to be updated."));
        catalogRedisService.Update(productId,catalog.getProductName(),quantity,catalog.getPrice(),catalog.getIsActive());
        catalog.setQuantity(quantity);
        repository.save(catalog);
        Integer sumOfItemDemand= itemRepo.findAllByProductId(productId).stream().map(com.example.mbminishared.BasketItem::getQuantity).mapToInt(Integer::intValue).sum();
        List<com.example.mbminishared.BasketItem> currItems=itemRepo.findAllByProductIdAndFlagOrderByCreatedAt(productId,1);
        for (com.example.mbminishared.BasketItem item: currItems){
            if (sumOfItemDemand<=quantity){
                break;
            }
            sumOfItemDemand=sumOfItemDemand-item.getQuantity();
            item.setFlag(0);
            itemRepo.save(item);
        }


        return "Catalog updated with baskets changed";
    }


    public Catalog Get(Long id) {
        // 1. Try Redis
        Catalog catalog = redisMethods.getFromRedis(id.toString(), Catalog.class);

        // 2. Fallback to SQL if not in Redis
        if (catalog == null) {
            catalog = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Catalog not found with id: " + id));

            // 3. Backfill Redis
            catalogRedisService.Create(catalog, catalog.getId());
        }

        return catalog;
    }


    public List<Catalog> findAll(Integer pageSize,Integer pageNo,String similar,String productNameFilter,String quantityFilter) {
        Integer offset=(pageNo-1) * pageSize;

        CriteriaBuilder cb= em.getCriteriaBuilder();
        CriteriaQuery<Catalog> cq= cb.createQuery(Catalog.class);
        Root<Catalog> root=cq.from(Catalog.class);
        List<Predicate> predicates = new ArrayList<>();
        if (similar!=null && !similar.isEmpty()){
            predicates.add(cb.like(root.get("productName"),"%"+similar+"%"));
        }
        cq.where(predicates);

        List<Order> orders=new ArrayList<>();

        if (productNameFilter!=null){
            orders.add(productNameFilter.equalsIgnoreCase("DESC")
                    ? cb.desc(root.get("productName"))
                    : cb.asc(root.get("productName")));
        }
        if (quantityFilter != null) {
            orders.add(quantityFilter.equalsIgnoreCase("DESC")
                    ? cb.desc(root.get("quantity"))
                    : cb.asc(root.get("quantity")));
        }
        if (orders.isEmpty()) {
            orders.add(cb.asc(root.get("id")));
        }
        cq.orderBy(orders);
        return em.createQuery(cq).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }


    public String cacheRefresh(){
        List<Catalog> catalogs=new ArrayList<>();
        repository.findAll().forEach(catalogs::add);
        if (catalogs.isEmpty()){
            return "Na data in MYSQL DB";
        }
        catalogRedisService.refreshCache(catalogs);
        return "Data refreshed in redis";

    }


}
