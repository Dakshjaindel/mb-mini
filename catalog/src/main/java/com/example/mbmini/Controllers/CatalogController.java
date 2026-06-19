package com.example.mbmini.Controllers;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.DTOs.CatalogDTO;
import com.example.mbmini.DTOs.CatalogUpdateDTO;
import com.example.mbmini.Services.JPAService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
public class CatalogController {

    @Autowired
    private JPAService service;

    @PostMapping({"/catalog"})
    public @ResponseBody String create(@Valid @RequestBody CatalogDTO dto){
        Catalog catalog= new Catalog(dto.getProductName(),
                dto.getQuantity(),
                dto.getPrice(),
                dto.getIsActive());
        return service.create(catalog);
    }

    @PutMapping({"/catalog"})
    public @ResponseBody String update(@Valid @RequestBody CatalogUpdateDTO updateDTO){
        service.Update(updateDTO.getId(),updateDTO.getProductName(), updateDTO.getQuantity(), updateDTO.getPrice(),updateDTO.getIsActive());
        return "Catalog updated from json body";
    }
    @GetMapping({"/catalog"})
    public List<Catalog> getAll() {
        return service.findAll();
    }

    @GetMapping({"/catalog/{id}"})
    public @ResponseBody Catalog getbyId( @PathVariable Long id){
        return service.Get(id);
    }

    @PostMapping({"/consumer/catalog/cache/refresh"})
    public String cacheRefresh(){
        return service.cacheRefresh();
    }



}
