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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PATENT_ABSTRACT_TEXT")
public class PatentAbstractText {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ABSTRACT_TEXT" ,length = 1000000)
    private String abstractText;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER", referencedColumnName = "PATENT_APPLICATION_NUMBER")
    private Patent patent;

    @Column(name = "CREATED_DATE")
    @CreatedDate
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @LastModifiedDate
    private Date updatedOn;

    @JsonCreator
    public PatentAbstractText( String abstractText) {
        this.abstractText = abstractText;
    }

    public PatentAbstractText(){

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public Patent getPatent() {
        return patent;
    }

    public void setPatent(Patent patent) {
        this.patent = patent;
    }

}
