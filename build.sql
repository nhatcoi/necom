create table blog_categories
(
    id   serial
        primary key,
    name varchar(255) not null
);

alter table blog_categories
    owner to postgres;

create table categories
(
    id        serial
        primary key,
    name      varchar(255) not null,
    image_url varchar
);

alter table categories
    owner to postgres;

create table flash_sales
(
    id         serial
        primary key,
    name       varchar(255) not null,
    start_time timestamp    not null,
    end_time   timestamp    not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp
);

alter table flash_sales
    owner to postgres;

create table products
(
    id                serial
        primary key,
    name              varchar(255) default ''::character varying not null,
    price             double precision                           not null
        constraint products_price_check
            check (price >= (0)::double precision),
    thumbnail         varchar(255) default NULL::character varying,
    description       text                                       not null,
    created_at        timestamp    default CURRENT_TIMESTAMP,
    updated_at        timestamp    default CURRENT_TIMESTAMP,
    category_id       integer
        constraint products_category_fk
            references categories,
    quantity_in_stock integer,
    weight            double precision
);

alter table products
    owner to postgres;

create table flash_sale_items
(
    id             serial
        primary key,
    flash_sale_id  integer
        references flash_sales,
    product_id     integer
        references products,
    sale_price     numeric(10, 2) not null,
    quantity_limit integer,
    created_at     timestamp default CURRENT_TIMESTAMP,
    updated_at     timestamp
);

alter table flash_sale_items
    owner to postgres;

create table product_images
(
    id         bigserial
        primary key,
    product_id integer
        constraint product_images_product_fk
            references products
            on delete cascade,
    image_url  varchar(255)
);

alter table product_images
    owner to postgres;

create table product_ratings
(
    id         bigserial not null
        constraint products_ratings_pkey
            primary key,
    product_id bigint                                                         not null
        constraint products_ratings_product_id_fkey
            references products
            on delete cascade,
    rating     integer                                                        not null
        constraint products_ratings_rating_check
            check ((rating >= 1) AND (rating <= 5)),
    created_at timestamp default CURRENT_TIMESTAMP,
    ip_address varchar(45),
    user_id    bigint,
    updated_at timestamp,
    constraint products_ratings_product_id_ip_address_key
        unique (product_id, ip_address)
);

alter table product_ratings
    owner to postgres;

create index idx_category_id
    on products (category_id);

create table roles
(
    id   serial
        primary key,
    name varchar(255)
);

alter table roles
    owner to postgres;

create table users
(
    id            serial
        primary key,
    full_name     varchar(255),
    phone_number  varchar(20)  not null,
    address       varchar(255) default ''::character varying,
    password      varchar(255) not null,
    created_at    timestamp    default CURRENT_TIMESTAMP,
    updated_at    timestamp    default CURRENT_TIMESTAMP,
    is_active     boolean      default true,
    date_of_birth timestamp,
    facebook_id   integer      default 0,
    google_id     integer      default 0,
    username      varchar(255)
);

alter table users
    owner to postgres;

create table blogs
(
    id          serial
        primary key,
    title       varchar(255) not null,
    content     text         not null,
    thumbnail   varchar,
    user_id     integer
        constraint fk_user
            references users,
    category_id integer
        constraint fk_category
            references blog_categories,
    created_at  timestamp default CURRENT_TIMESTAMP,
    updated_at  timestamp default CURRENT_TIMESTAMP
);

alter table blogs
    owner to postgres;

create table orders
(
    id               serial
        primary key,
    user_id          integer
        constraint orders_user_fk
            references users,
    full_name        varchar(255),
    email            varchar(100) default ''::character varying,
    phone_number     varchar(20)      not null,
    address          varchar(255)     not null,
    note             varchar(255) default ''::character varying,
    order_date       timestamp    default CURRENT_TIMESTAMP,
    status           varchar(50),
    total_price      double precision not null
        constraint orders_total_price_check
            check (total_price >= (0)::double precision),
    shipping_method  varchar(50)  default ''::character varying,
    shipping_address varchar(255),
    shipping_date    date,
    tracking_number  varchar(255),
    payment_method   varchar(50),
    payment_date     timestamp,
    active           boolean
);

alter table orders
    owner to postgres;

create table order_details
(
    id                 bigserial
        primary key,
    order_id           integer
        constraint order_details_order_fk
            references orders,
    product_id         integer
        constraint order_details_product_fk
            references products,
    price              double precision
        constraint order_details_price_check
            check (price >= (0)::double precision),
    number_of_products integer
        constraint order_details_number_of_products_check
            check (number_of_products > 0),
    total_price        double precision
        constraint order_details_total_price_check
            check (total_price >= (0)::double precision),
    color              varchar(255)
);

alter table order_details
    owner to postgres;

create index idx_order_id
    on order_details (order_id);

create index idx_product_id
    on order_details (product_id);

create index idx_user_id
    on orders (user_id);

create table social_accounts
(
    id          bigserial
        primary key,
    provider    varchar(50)  not null,
    provider_id varchar(50)  not null,
    email       varchar(255) not null,
    name        varchar(100) not null,
    user_id     integer
        constraint social_accounts_user_fk
            references users
);

alter table social_accounts
    owner to postgres;

create table tokens
(
    id              bigserial
        primary key,
    token           varchar(255) not null,
    token_ype       varchar(50)  not null,
    expiration_date timestamp,
    revoked         boolean      not null,
    expired         boolean      not null,
    user_id         integer
        constraint tokens_user_fk
            references users,
    token_type      varchar(50)
);

alter table tokens
    owner to postgres;

create index idx_password
    on users (password);

create table user_roles
(
    user_id integer not null
        references users
            on delete cascade,
    role_id integer not null
        references roles
            on delete cascade,
    primary key (user_id, role_id)
);

alter table user_roles
    owner to postgres;

create table carts
(
    id         serial
        primary key,
    user_id    integer
        references users
            on delete cascade,
    product_id integer
        references products
            on delete cascade,
    quantity   integer not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    unique (user_id, product_id)
);

alter table carts
    owner to postgres;

