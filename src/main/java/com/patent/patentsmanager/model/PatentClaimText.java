package com.patent.patentsmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "PATENT_CLAIM_TEXT")
@EntityListeners(AuditingEntityListener.class)
public class PatentClaimText {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "CLAIM_TEXT",length = 1000000)
    private String claimText;

    @Column(name = "CREATED_DATE")
    @CreatedDate
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @LastModifiedDate
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER", referencedColumnName = "PATENT_APPLICATION_NUMBER")
    private Patent patent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimText() {
        return claimText;
    }

    public void setClaimText(String claimText) {
        this.claimText = claimText;
    }

    public Patent getPatent() {
        return patent;
    }

    public void setPatent(Patent patent) {
        this.patent = patent;
    }

    @JsonCreator
    public PatentClaimText(String claimText){
        this.claimText = claimText;
    }

    public PatentClaimText() {

    }
}
