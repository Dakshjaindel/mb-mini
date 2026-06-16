package com.example.mbmini.RepoConnections;

import com.example.mbmini.Entities.Catalog;
import org.springframework.data.repository.CrudRepository;

public interface JPARepo extends CrudRepository<Catalog,Long> {

}
