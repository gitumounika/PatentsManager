create table patent
(
    patent_application_number         varchar(255) not null,
    archive_uri                       varchar(255),
    assignee_entity_name              varchar(255),
    assignee_postal_address_text      varchar(255),
    downloaded_status                 varchar(2),
    file_location_uri                 varchar(255),
    filing_date                       timestamp,
    inserted_on                       timestamp,
    invention_subject_matter_category varchar(255),
    invention_title                   varchar(255),
    inventor_names                    varchar(255),
    main_cpcs_symbol_text             varchar(255),
    processed_status                  varchar(2),
    publication_date                  timestamp,
    publication_document_identifier   varchar(255),
    updated_on                        timestamp,
    primary key (patent_application_number)
);
create table patent_abstract_text
(
    id                        bigint not null,
    abstract_text             varchar(1000000),
    created_date              timestamp,
    updated_date              timestamp,
    patent_application_number varchar(255),
    primary key (id)
);
create table patent_claim_text
(
    id                        bigint not null,
    claim_text                varchar(1000000),
    created_date              timestamp,
    updated_date              timestamp,
    patent_application_number varchar(255),
    primary key (id)
);
create table patent_description_text
(
    id                        bigint not null,
    created_date              timestamp,
    description_text          varchar(1000000),
    updated_date              timestamp,
    patent_application_number varchar(255),
    primary key (id)
);
create sequence hibernate_sequence start with 1 increment by 1;
alter table patent_abstract_text
    add constraint FK_PA_AT foreign key (patent_application_number) references patent;
alter table patent_claim_text
    add constraint FK_PA_CT foreign key (patent_application_number) references patent;
alter table patent_description_text
    add constraint FK_DT_PA foreign key (patent_application_number) references patent;