create table orders
(
    id         uuid primary key,
    courier_id uuid default null,
    location_x integer not null,
    location_y integer not null,
    status     text    not null
);

create table couriers
(
    id           uuid primary key,
    name         text    not null,
    transport_id uuid    not null,
    location_x   integer not null,
    location_y   integer not null,
    status       text    not null
);

create table transports
(
    id           uuid primary key,
    name         text    not null,
    speed        integer not null
);

alter table orders
    add constraint fk_orders_courier_id
        foreign key (courier_id) references couriers (id)
            on update cascade;

alter table couriers
    add constraint fk_couriers_transport_id
        foreign key (transport_id) references transports (id)
            on update cascade;
