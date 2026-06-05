package com.example.mbmini;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Validated
@RestController
public class CatalogController {

    @Autowired
    private JPAService service;

    @PostMapping({"/data"})
    public @ResponseBody String create(@Valid @RequestBody CatalogDTO dto){
        Catalog catalog= new Catalog(dto.getProductName(),
                dto.getQuantity(),
                dto.getPrice(),
                dto.getIsActive());
        return service.create(catalog);
    }

    @PutMapping({"/data"})
    public @ResponseBody String update(@Valid @RequestBody CatalogUpdateDTO updateDTO){
        service.Update(updateDTO.getId(),updateDTO.getProductName(), updateDTO.getQuantity(), updateDTO.getPrice(),updateDTO.getIsActive());
        return "Catalog updated from json body";
    }
    @GetMapping({"/data/{id}"})
    public @ResponseBody String getbyId( @PathVariable Long id){
        return service.Get(id);
    }

    @PostMapping({"/data/cache/refresh"})
    public String cacheRefresh(){
        return service.cacheRefresh();
    }



}
