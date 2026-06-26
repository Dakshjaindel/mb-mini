package com.example.mbmini.Controllers;


import com.example.mbmini.DTOs.CatalogGetAllDTO;
import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.DTOs.CatalogCreateDTO;
import com.example.mbmini.DTOs.CatalogUpdateDTO;
import com.example.mbmini.Services.CatalogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
public class CatalogController {

    @Autowired
    private CatalogService service;

    @PostMapping({"/catalog"})
    public @ResponseBody String create(@Valid @RequestBody CatalogCreateDTO dto){
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
    @GetMapping({"/catalog/all"})
    public List<Catalog> getAll(@Valid@RequestBody CatalogGetAllDTO catalogGetAllDTO) {
        return service.findAll(catalogGetAllDTO.getPageSize(), catalogGetAllDTO.getPageNo(),catalogGetAllDTO.getSimilar(),catalogGetAllDTO.getProductNameFilter(), catalogGetAllDTO.getQuantityFilter());
    }

    @GetMapping({"/catalog/{id}"})
    public @ResponseBody Catalog getbyId( @PathVariable Long id){
        return service.Get(id);
    }

    @PostMapping({"/consumer /catalog/cache/refresh"})
    public String cacheRefresh(){
        return service.cacheRefresh();
    }


    @PutMapping({"/catalog/quantity"})
    public @ResponseBody String catalogQuantityUpdate(@Valid @RequestBody com.example.mbminiframework.Entity.CatalogQuantityUpdateDTO catalogQuantityUpdateDTO){
        return service.updateQuantity(catalogQuantityUpdateDTO.getProductId(), catalogQuantityUpdateDTO.getQuantity());
    }



}
