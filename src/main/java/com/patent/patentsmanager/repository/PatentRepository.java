package com.patent.patentsmanager.repository;

import com.patent.patentsmanager.model.Patent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatentRepository extends CrudRepository<Patent,String> {


    List<Patent> findByDownloadedStatus(String status);
    List<Patent> findByProcessedStatus(String status);
    List<Patent> findByProcessedStatusAndDownloadedStatus(String pStatus, String dStatus);
    long countByProcessedStatus(String status);


    /**
     * Named Queries
     * @param keyword
     * @return
     */
    @Query(value = "select * from patent p where p.patent_application_number like %:keyword%", nativeQuery = true)
    List<Patent> findByKeyword(@Param("keyword") String keyword);

}
