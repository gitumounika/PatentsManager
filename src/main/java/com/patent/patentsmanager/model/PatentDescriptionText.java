package com.patent.patentsmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="PATENT_DESCRIPTION_TEXT")
@EntityListeners(AuditingEntityListener.class)
public class PatentDescriptionText {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "DESCRIPTION_TEXT" ,length = 1000000)
    private String descText;

    @Column(name = "CREATED_DATE")
    @CreatedDate
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @LastModifiedDate
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER", referencedColumnName = "PATENT_APPLICATION_NUMBER")
    private Patent patent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public Patent getPatent() {
        return patent;
    }

    public void setPatent(Patent patent) {
        this.patent = patent;
    }

    @JsonCreator
    public PatentDescriptionText(String descText){
        this.descText = descText;
    }

    public PatentDescriptionText(){

    }

}
