package com.patent.patentsmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.patent.patentsmanager.constants.PatentConstants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PATENT")
public class Patent {

    @Id
    @Column(name = "PATENT_APPLICATION_NUMBER", unique = true)
    @JsonProperty("patentApplicationNumber")
    private String patentApplicationNumber;

    @Column(name = "INVENTION_SUBJECT_MATTER_CATEGORY")
    @JsonProperty("inventionSubjectMatterCategory")
    private String inventionSubjectMatterCategory;

    @Column(name = "FILING_DATE")
    @JsonFormat(pattern = "MM-DD-YYYY")
    @JsonProperty("filingDate")
    private Date filingDate;

    @JsonProperty("mainCPCSymbolText")
    @Column(name = "MAIN_CPCS_SYMBOL_TEXT")
    private String mainCPCSymbolText;

    @Column(name = "INVENTOR_NAMES")
    private String inventorNames;

    @JsonProperty("inventorNameArrayText")
    @Transient
    private List<String> inventorNameArrayText;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER")
    @JsonProperty("abstractText")
    private List<PatentAbstractText> abstractText;

    @Column(name = "ASSIGNEE_ENTITY_NAME")
    @JsonProperty("assigneeEntityName")
    private String assigneeEntityName;

    @Column(name = "ASSIGNEE_POSTAL_ADDRESS_TEXT")
    @JsonProperty("assigneePostalAddressText")
    private String assigneePostalAddressText;

    @Column(name = "INVENTION_TITLE")
    @JsonProperty("inventionTitle")
    private String inventionTitle;

    @Column(name = "FILE_LOCATION_URI")
    @JsonProperty("filelocationURI")
    private String filelocationURI;

    @Column(name = "ARCHIVE_URI")
    @JsonProperty("archiveURI")
    private String archiveURI;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER")
    @JsonProperty("claimText")
    private List<PatentClaimText> claimText;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PATENT_APPLICATION_NUMBER")
    @JsonProperty("descriptionText")
    private List<PatentDescriptionText> descriptionText;

    @Column(name = "PUBLICATION_DATE")
    @JsonFormat(pattern = "MM-DD-YYYY")
    @JsonProperty("publicationDate")
    private Date publicationDate;

    @Column(name = "PUBLICATION_DOCUMENT_IDENTIFIER")
    @JsonProperty("publicationDocumentIdentifier")
    private String publicationDocumentIdentifier;

    @Column(name = "DOWNLOADED_STATUS")
    private String downloadedStatus;

    @Column(name = "PROCESSED_STATUS")
    private String processedStatus;

    @CreatedDate
    @Column(name = "INSERTED_ON")
    private Date insertedOn;

    @Column(name = "UPDATED_ON")
    @LastModifiedDate
    private Date updatedOn;

    public Patent(String patentApplicationNumber){
        this.patentApplicationNumber = patentApplicationNumber;
    }

    public String getPatentApplicationNumber() {
        return patentApplicationNumber;
    }

    public void setPatentApplicationNumber(String patentApplicationNumber) {
        this.patentApplicationNumber = patentApplicationNumber;
    }

    public String getInventionSubjectMatterCategory() {
        return inventionSubjectMatterCategory;
    }

    public void setInventionSubjectMatterCategory(String inventionSubjectMatterCategory) {
        this.inventionSubjectMatterCategory = inventionSubjectMatterCategory;
    }

    public Date getFilingDate() {
        return filingDate;
    }

    public void setFilingDate(Date filingDate) {
        this.filingDate = filingDate;
    }

    public String getMainCPCSymbolText() {
        return mainCPCSymbolText;
    }

    public void setMainCPCSymbolText(String mainCPCSymbolText) {
        this.mainCPCSymbolText = mainCPCSymbolText;
    }

    public List<String> getInventorNameArrayText() {
        return inventorNameArrayText;
    }

    public void setInventorNameArrayText(List<String> inventorNameArrayText) {
        this.inventorNameArrayText = inventorNameArrayText;
    }

    public List<PatentAbstractText> getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(List<PatentAbstractText> abstractText) {
        this.abstractText = abstractText;
    }

    public String getAssigneeEntityName() {
        return assigneeEntityName;
    }

    public void setAssigneeEntityName(String assigneeEntityName) {
        this.assigneeEntityName = assigneeEntityName;
    }

    public String getAssigneePostalAddressText() {
        return assigneePostalAddressText;
    }

    public void setAssigneePostalAddressText(String assigneePostalAddressText) {
        this.assigneePostalAddressText = assigneePostalAddressText;
    }

    public String getInventionTitle() {
        return inventionTitle;
    }

    public void setInventionTitle(String inventionTitle) {
        this.inventionTitle = inventionTitle;
    }

    public String getInventorNames() {
        return inventorNames;
    }

    public void setInventorNames(String inventorNames) {
        if (null != inventorNameArrayText && !inventorNameArrayText.isEmpty()) {
            this.inventorNames = String.join(PatentConstants.COMMA_DELIMITER, inventorNameArrayText);
        } else {
            this.inventorNames = inventorNames;
        }
    }

    public String getFilelocationURI() {
        return filelocationURI;
    }

    public void setFilelocationURI(String filelocationURI) {
        this.filelocationURI = filelocationURI;
    }

    public String getArchiveURI() {
        return archiveURI;
    }

    public void setArchiveURI(String archiveURI) {
        this.archiveURI = archiveURI;
    }

    public List<PatentClaimText> getClaimText() {
        return claimText;
    }

    public void setClaimText(List<PatentClaimText> claimText) {
        this.claimText = claimText;
    }

    public List<PatentDescriptionText> getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(List<PatentDescriptionText> descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getDownloadedStatus() {
        return downloadedStatus;
    }

    public void setDownloadedStatus(String downloadedStatus) {
        this.downloadedStatus = downloadedStatus;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublicationDocumentIdentifier() {
        return publicationDocumentIdentifier;
    }

    public void setPublicationDocumentIdentifier(String publicationDocumentIdentifier) {
        this.publicationDocumentIdentifier = publicationDocumentIdentifier;
    }

    public String getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(String processedStatus) {
        this.processedStatus = processedStatus;
    }
}