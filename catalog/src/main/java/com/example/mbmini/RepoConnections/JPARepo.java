package com.example.mbmini.RepoConnections;

import com.example.mbmini.Entities.Catalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface JPARepo extends CrudRepository<Catalog,Long> {

}
