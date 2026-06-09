package com.example.mbmini.RepoConnections;

import com.example.mbmini.Catalog;
import org.springframework.data.repository.CrudRepository;

public interface JPARepo extends CrudRepository<Catalog,Long> {

}
