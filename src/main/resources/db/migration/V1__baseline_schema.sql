-- Baseline schema. Generated from the JPA entities via the Hibernate MariaDB dialect so that it
-- matches exactly what `spring.jpa.hibernate.ddl-auto=validate` expects. Subsequent schema changes
-- must be added as new V<n>__*.sql migrations rather than by editing this file.

create table barcode (
    felt_roll_id bigint,
    id bigint not null auto_increment,
    scrap_piece_id bigint,
    type enum ('ROLL','SCRAP') not null,
    primary key (id)
) engine=InnoDB;

create table batch (
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table category (
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table category_field (
    category_id bigint not null,
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table felt (
    density float(53) not null,
    is_low_on_supply boolean default false not null,
    is_reordered boolean default false not null,
    price decimal(10,2) not null,
    thickness float(53) not null,
    felt_type_id bigint not null,
    id bigint not null auto_increment,
    supplier_id bigint not null,
    article_number varchar(255) not null,
    color varchar(255) not null,
    supplier_color varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table felt_roll (
    length float(53) not null,
    width float(53) not null,
    batch_id bigint,
    felt_id bigint not null,
    id bigint not null auto_increment,
    storage_id bigint,
    primary key (id)
) engine=InnoDB;

create table felt_stocktake (
    completed_at datetime(6),
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    description varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table felt_stocktake_item (
    mutation_applied bit not null,
    mutation_wanted bit not null,
    problem_acknowledged bit not null,
    id bigint not null auto_increment,
    new_storage_id bigint,
    stocktake_id bigint not null,
    barcode varchar(255),
    resolution_comment varchar(255),
    primary key (id)
) engine=InnoDB;

create table felt_stocktake_roll_or_scrap (
    density float(53) not null,
    length float(53) not null,
    price decimal(38,2) not null,
    thickness float(53) not null,
    width float(53) not null,
    expected_storage_id bigint,
    item_id bigint not null,
    roll_id bigint,
    scrap_id bigint,
    article_number varchar(255) not null,
    color varchar(255) not null,
    felt_type_name varchar(255) not null,
    supplier_name varchar(255) not null,
    type enum ('ROLL','SCRAP') not null,
    primary key (item_id)
) engine=InnoDB;

create table felt_stocktake_scan (
    corrected bit not null,
    voided bit not null,
    id bigint not null auto_increment,
    scanned_at datetime(6) not null,
    scanned_storage_id bigint not null,
    stocktake_id bigint not null,
    stocktake_item_id bigint not null,
    barcode varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table felt_stocktake_storage (
    closed bit not null,
    felt_stocktake_id bigint not null,
    storage_id bigint not null,
    primary key (felt_stocktake_id, storage_id)
) engine=InnoDB;

create table felt_type (
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table offer (
    offer_sent bit not null,
    created_at datetime(6),
    customer_id bigint not null,
    due_at datetime(6),
    id bigint not null auto_increment,
    updated_at datetime(6),
    state varchar(255) not null check ((state in ('OFFER','ORDER_CONFIRMATION','INVOICE','PAYMENT_REMINDER','FIRST_DUNNING_NOTICE','SECOND_DUNNING_NOTICE','COMPLETED','CANCELLED','NO_RESPONSE'))),
    primary key (id)
) engine=InnoDB;

create table offer_customer (
    id bigint not null auto_increment,
    city varchar(255),
    contact_person varchar(255),
    country varchar(255),
    email varchar(255),
    name varchar(255) not null,
    phone varchar(255),
    street varchar(255),
    vat_number varchar(255),
    zip varchar(255),
    primary key (id)
) engine=InnoDB;

create table offer_item (
    quantity integer not null,
    total_price decimal(19,4),
    unit_price decimal(19,4),
    created_at datetime(6),
    id bigint not null auto_increment,
    offer_id bigint not null,
    product_variant_id bigint,
    updated_at datetime(6),
    description varchar(1000),
    kind varchar(255) not null check ((kind in ('SCRAP','ROLL','PRODUCT'))),
    primary key (id)
) engine=InnoDB;

create table product (
    category_id bigint not null,
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table product_attribute (
    id bigint not null auto_increment,
    product_id bigint not null,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table product_attribute_value (
    id bigint not null auto_increment,
    product_attribute_id bigint not null,
    product_variant_id bigint not null,
    value varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table product_inventory (
    count integer not null,
    id bigint not null auto_increment,
    product_variant_id bigint not null,
    storage_id bigint not null,
    primary key (id)
) engine=InnoDB;

create table product_variant (
    price decimal(10,2) not null,
    id bigint not null auto_increment,
    product_id bigint not null,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table scrap_piece (
    length float(53) not null,
    width float(53) not null,
    batch_id bigint,
    felt_id bigint not null,
    id bigint not null auto_increment,
    storage_id bigint,
    primary key (id)
) engine=InnoDB;

create table storage (
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table supplier (
    id bigint not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table if exists barcode
   add constraint UK53euki8fn3rrqmb5au2b14rts unique (felt_roll_id);

alter table if exists barcode
   add constraint UKck3vvpx30e7mrowiac1t1p1lc unique (scrap_piece_id);

alter table if exists felt_type
   add constraint UKiolnnmsqmljk4htpmpmylil0 unique (name);

alter table if exists product_attribute_value
   add constraint uk_pav_variant_attribute unique (product_variant_id, product_attribute_id);

alter table if exists product_inventory
   add constraint uk_inventory_variant_storage unique (product_variant_id, storage_id);

alter table if exists storage
   add constraint UK5fe37ity4pov1usxcqr3b03nd unique (name);

alter table if exists supplier
   add constraint UKc3fclhmodftxk4d0judiafwi3 unique (name);

alter table if exists barcode
   add constraint FK2vvydxduyglne7xqk5j7wvwp8
   foreign key (felt_roll_id)
   references felt_roll (id)
   on delete cascade;

alter table if exists barcode
   add constraint FKa53194cdrmuvy3edld94wh3n2
   foreign key (scrap_piece_id)
   references scrap_piece (id)
   on delete cascade;

alter table if exists category_field
   add constraint FK2klnhoscyq24mh06sc9d44jef
   foreign key (category_id)
   references category (id);

alter table if exists felt
   add constraint FK62qsplmd9d35sqiam142y1ehg
   foreign key (felt_type_id)
   references felt_type (id);

alter table if exists felt
   add constraint FKgnklh0gsqdnxwdn3g6vlw3s6o
   foreign key (supplier_id)
   references supplier (id);

alter table if exists felt_roll
   add constraint FKeih1lh1qy9mtkyjklfxr6hx71
   foreign key (batch_id)
   references batch (id);

alter table if exists felt_roll
   add constraint FKdkq28hpqqf1lvcg69hf9e3vn8
   foreign key (felt_id)
   references felt (id);

alter table if exists felt_roll
   add constraint FK15vpvmyad5q01j3kxihrj8m2b
   foreign key (storage_id)
   references storage (id);

alter table if exists felt_stocktake_item
   add constraint FKjyc9hopht5swf2kdo31ise0k1
   foreign key (new_storage_id)
   references storage (id);

alter table if exists felt_stocktake_item
   add constraint FKowmegiykv6fsyefqnqjl7vv5
   foreign key (stocktake_id)
   references felt_stocktake (id)
   on delete cascade;

alter table if exists felt_stocktake_roll_or_scrap
   add constraint FK5fporsey4aj3bq7lx49sd5ruq
   foreign key (expected_storage_id)
   references storage (id);

alter table if exists felt_stocktake_roll_or_scrap
   add constraint FKi4mbfnqj3c9or5fpvyglrf803
   foreign key (roll_id)
   references felt_roll (id)
   on delete set null;

alter table if exists felt_stocktake_roll_or_scrap
   add constraint FKp9u8ccql4iwfp40o5fch99khv
   foreign key (scrap_id)
   references scrap_piece (id)
   on delete set null;

alter table if exists felt_stocktake_roll_or_scrap
   add constraint FKt9o0rifn4ht2tyaq8i4mwm2j0
   foreign key (item_id)
   references felt_stocktake_item (id)
   on delete cascade;

alter table if exists felt_stocktake_scan
   add constraint FKr9k20qf3scjhov9wqc7l0ptr0
   foreign key (scanned_storage_id)
   references storage (id);

alter table if exists felt_stocktake_scan
   add constraint FKqltpewjt5t1x6m5emke2c6elj
   foreign key (stocktake_id)
   references felt_stocktake (id)
   on delete cascade;

alter table if exists felt_stocktake_scan
   add constraint FKtb6xxw7ok3cpuuix2f6dns0py
   foreign key (stocktake_item_id)
   references felt_stocktake_item (id)
   on delete cascade;

alter table if exists felt_stocktake_storage
   add constraint FKl0xa5h1e4770u4ux2lyqc2snh
   foreign key (felt_stocktake_id)
   references felt_stocktake (id);

alter table if exists felt_stocktake_storage
   add constraint FK6hc0tptklxo0qyenpuobu7qyv
   foreign key (storage_id)
   references storage (id);

alter table if exists offer
   add constraint FK7q19foaf7sqdpu52lt0c9aq4f
   foreign key (customer_id)
   references offer_customer (id);

alter table if exists product
   add constraint FK1mtsbur82frn64de7balymq9s
   foreign key (category_id)
   references category (id);

alter table if exists product_attribute
   add constraint FKlefs59y5kmsbu017n1wp10gf2
   foreign key (product_id)
   references product (id);

alter table if exists product_attribute_value
   add constraint FKqgk2xbdl46wt0h9i5uheps5ke
   foreign key (product_attribute_id)
   references product_attribute (id);

alter table if exists product_attribute_value
   add constraint FKoky4ukccdsibaajrxksxcgvtc
   foreign key (product_variant_id)
   references product_variant (id);

alter table if exists product_inventory
   add constraint FKe3jvn3aayrikc9ndfb1xt5yvb
   foreign key (product_variant_id)
   references product_variant (id);

alter table if exists product_inventory
   add constraint FKlxehub2pomen6r6vv1b1vtrmv
   foreign key (storage_id)
   references storage (id);

alter table if exists product_variant
   add constraint FKgrbbs9t374m9gg43l6tq1xwdj
   foreign key (product_id)
   references product (id);

alter table if exists scrap_piece
   add constraint FKsdfr5xu32ga23pdjcinjr302i
   foreign key (batch_id)
   references batch (id);

alter table if exists scrap_piece
   add constraint FKqlgvmq071udf6ttmphee6h8j9
   foreign key (felt_id)
   references felt (id);

alter table if exists scrap_piece
   add constraint FK6y5lhvuyyojakgx42ks83c5fl
   foreign key (storage_id)
   references storage (id);
