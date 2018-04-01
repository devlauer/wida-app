-- Wida type tables

-- Table: wida.propertydefinition

-- DROP TABLE wida.propertydefinition;

CREATE TABLE wida.propertydefinition
(
    dtype character varying(31) COLLATE pg_catalog."default" NOT NULL,
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    cardinality character varying(255) COLLATE pg_catalog."default" NOT NULL,
    column_name character varying(20) COLLATE pg_catalog."default" NOT NULL,
    description character varying(200) COLLATE pg_catalog."default",
    display_name character varying(50) COLLATE pg_catalog."default",
    local_name character varying(50) COLLATE pg_catalog."default",
    local_namespace character varying(100) COLLATE pg_catalog."default",
    openchoice boolean,
    orderable boolean NOT NULL,
    property_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    query_name character varying(50) COLLATE pg_catalog."default",
    queryable boolean NOT NULL,
    required boolean NOT NULL,
    updatability character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT propertydefinition_pkey PRIMARY KEY (id),
    CONSTRAINT unq_propertydefinition_query_name UNIQUE (query_name)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.propertydefinition
    OWNER to wida;

-- Index: x_propertydefinition_query_name

-- DROP INDEX wida.x_propertydefinition_query_name;

CREATE INDEX x_propertydefinition_query_name
    ON wida.propertydefinition USING btree
    (query_name COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Table: wida.prop_def_boolean

-- DROP TABLE wida.prop_def_boolean;

CREATE TABLE wida.prop_def_boolean
(
    prop_def_boolean_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_boolean_pkey PRIMARY KEY (prop_def_boolean_id),
    CONSTRAINT fk_prop_def_boolean__prop_def_boolean_id FOREIGN KEY (prop_def_boolean_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_boolean
    OWNER to wida;
    
-- Table: wida.prop_def_boolean_default

-- DROP TABLE wida.prop_def_boolean_default;

CREATE TABLE wida.prop_def_boolean_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value boolean,
    CONSTRAINT fk_prop_def_boolean_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_boolean_default
    OWNER to wida;    
    
-- Table: wida.prop_def_datetime

-- DROP TABLE wida.prop_def_datetime;

CREATE TABLE wida.prop_def_datetime
(
    resolution character varying(255) COLLATE pg_catalog."default",
    prop_def_datetime_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_datetime_pkey PRIMARY KEY (prop_def_datetime_id),
    CONSTRAINT fk_prop_def_datetime__prop_def_datetime_id FOREIGN KEY (prop_def_datetime_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_datetime
    OWNER to wida;
    
-- Table: wida.prop_def_datetime_default

-- DROP TABLE wida.prop_def_datetime_default;

CREATE TABLE wida.prop_def_datetime_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value timestamp without time zone,
    CONSTRAINT fk_prop_def_datatime_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_datetime_default
    OWNER to wida;    
    
-- Table: wida.prop_def_decimal

-- DROP TABLE wida.prop_def_decimal;

CREATE TABLE wida.prop_def_decimal
(
    max_value numeric(19,2),
    min_value numeric(19,2),
    "precision" character varying(255) COLLATE pg_catalog."default",
    prop_def_decimal_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_decimal_pkey PRIMARY KEY (prop_def_decimal_id),
    CONSTRAINT fk_prop_def_decimal__prop_def_decimal_id FOREIGN KEY (prop_def_decimal_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_decimal
    OWNER to wida;
    
-- Table: wida.prop_def_decimal_default

-- DROP TABLE wida.prop_def_decimal_default;

CREATE TABLE wida.prop_def_decimal_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value numeric(19,2),
    CONSTRAINT fk_prop_def_decimal_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_decimal_default
    OWNER to wida;
    
-- Table: wida.prop_def_html

-- DROP TABLE wida.prop_def_html;

CREATE TABLE wida.prop_def_html
(
    prop_def_html_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_html_pkey PRIMARY KEY (prop_def_html_id),
    CONSTRAINT fk_prop_def_html__prop_def_html_id FOREIGN KEY (prop_def_html_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_html
    OWNER to wida;
    
-- Table: wida.prop_def_html_default

-- DROP TABLE wida.prop_def_html_default;

CREATE TABLE wida.prop_def_html_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT fk_prop_def_html_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_html_default
    OWNER to wida;
    
-- Table: wida.prop_def_id

-- DROP TABLE wida.prop_def_id;

CREATE TABLE wida.prop_def_id
(
    prop_def_id_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_id_pkey PRIMARY KEY (prop_def_id_id),
    CONSTRAINT fk_prop_def_id__prop_def_id_id FOREIGN KEY (prop_def_id_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_id
    OWNER to wida;
    
-- Table: wida.prop_def_id_default

-- DROP TABLE wida.prop_def_id_default;

CREATE TABLE wida.prop_def_id_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT fk_prop_def_id_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_id_default
    OWNER to wida;
    
-- Table: wida.prop_def_integer

-- DROP TABLE wida.prop_def_integer;

CREATE TABLE wida.prop_def_integer
(
    max_value numeric(19,2),
    min_value numeric(19,2),
    prop_def_integer_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_integer_pkey PRIMARY KEY (prop_def_integer_id),
    CONSTRAINT fk_prop_def_integer__prop_def_integer_id FOREIGN KEY (prop_def_integer_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_integer
    OWNER to wida;
    
-- Table: wida.prop_def_integer_default

-- DROP TABLE wida.prop_def_integer_default;

CREATE TABLE wida.prop_def_integer_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value numeric(19,2),
    CONSTRAINT fk_prop_def_integer_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_integer_default
    OWNER to wida;
    
-- Table: wida.prop_def_string

-- DROP TABLE wida.prop_def_string;

CREATE TABLE wida.prop_def_string
(
    max_length numeric(19,2),
    prop_def_string_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_string_pkey PRIMARY KEY (prop_def_string_id),
    CONSTRAINT fk_prop_def_string__prop_def_string_id FOREIGN KEY (prop_def_string_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_string
    OWNER to wida;
    
-- Table: wida.prop_def_string_default

-- DROP TABLE wida.prop_def_string_default;

CREATE TABLE wida.prop_def_string_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT fk_prop_def_string_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_string_default
    OWNER to wida;
    
-- Table: wida.prop_def_uri

-- DROP TABLE wida.prop_def_uri;

CREATE TABLE wida.prop_def_uri
(
    prop_def_uri_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT prop_def_uri_pkey PRIMARY KEY (prop_def_uri_id),
    CONSTRAINT fk_prop_def_uri__prop_def_uri_id FOREIGN KEY (prop_def_uri_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_uri
    OWNER to wida;
    
-- Table: wida.prop_def_uri_default

-- DROP TABLE wida.prop_def_uri_default;

CREATE TABLE wida.prop_def_uri_default
(
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    default_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT fk_prop_def_uri_default__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.prop_def_uri_default
    OWNER to wida;    
    
-- Table: wida.type_base

-- DROP TABLE wida.type_base;

CREATE TABLE wida.type_base
(
    discriminator character varying(31) COLLATE pg_catalog."default" NOT NULL,
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    controllable_acl boolean NOT NULL,
    controllable_policy boolean NOT NULL,
    creatable boolean NOT NULL,
    description character varying(200) COLLATE pg_catalog."default",
    display_name character varying(50) COLLATE pg_catalog."default",
    fileable boolean NOT NULL,
    fulltext_indexed boolean NOT NULL,
    included_in_super_type_query boolean NOT NULL,
    inherit_properties boolean,
    local_name character varying(50) COLLATE pg_catalog."default",
    local_namespace character varying(100) COLLATE pg_catalog."default",
    parent_id character varying(50) COLLATE pg_catalog."default",
    query_name character varying(50) COLLATE pg_catalog."default",
    queryable boolean NOT NULL,
    tablename character varying(30) COLLATE pg_catalog."default" NOT NULL,
    tm_create boolean,
    tm_update boolean,
    tm_delete boolean,
    CONSTRAINT type_base_pkey PRIMARY KEY (id),
    CONSTRAINT unq_type_base__query_name UNIQUE (query_name),
    CONSTRAINT fk_type_base__parent_id FOREIGN KEY (parent_id)
        REFERENCES wida.type_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.type_base
    OWNER to wida;

-- Index: x_type_base_parent_id

-- DROP INDEX wida.x_type_base_parent_id;

CREATE INDEX x_type_base_parent_id
    ON wida.type_base USING btree
    (parent_id COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: x_type_base_query_name

-- DROP INDEX wida.x_type_base_query_name;

CREATE INDEX x_type_base_query_name
    ON wida.type_base USING btree
    (query_name COLLATE pg_catalog."default")
    TABLESPACE pg_default;    
    
-- Table: wida.type_document

-- DROP TABLE wida.type_document;

CREATE TABLE wida.type_document
(
    contentstream_allowed character varying(255) COLLATE pg_catalog."default" NOT NULL,
    versionable boolean NOT NULL,
    documenttype_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT type_document_pkey PRIMARY KEY (documenttype_id),
    CONSTRAINT fk_type_document__documenttype_id FOREIGN KEY (documenttype_id)
        REFERENCES wida.type_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.type_document
    OWNER to wida;    
    
-- Table: wida.type_to_prop_def

-- DROP TABLE wida.type_to_prop_def;

CREATE TABLE wida.type_to_prop_def
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    prop_def_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT fk_type_to_prop_def__id FOREIGN KEY (id)
        REFERENCES wida.type_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_type_to_prop_def__prop_def_id FOREIGN KEY (prop_def_id)
        REFERENCES wida.propertydefinition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.type_to_prop_def
    OWNER to wida;    


-- Table: wida.type_to_secondary_type

-- DROP TABLE wida.type_to_secondary_type;

CREATE TABLE wida.type_to_secondary_type
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    secondary_type_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT type_to_secondary_type_pkey PRIMARY KEY (id, secondary_type_id),
    CONSTRAINT fk_type_to_secondary_type__secondary_type_id FOREIGN KEY (secondary_type_id)
        REFERENCES wida.type_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_type_to_secondary_type__id FOREIGN KEY (id)
        REFERENCES wida.type_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wida.type_to_secondary_type
    OWNER to wida;    
-- Wida data tables (only fixed ones, the dynamic ones are created with new data types)