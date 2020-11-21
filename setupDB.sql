create table db_info
(
    key   text not null
        constraint db_info_pk
            primary key,
    value text not null
);

create unique index db_info_key_uindex
    on db_info (key);

insert into db_info (key, value)
VALUES ('version', '1');

create table players
(
    ID       integer not null
        constraint players_pk
            primary key autoincrement,
    identity text    not null,
    name     text    null
);

create unique index players_ID_uindex
    on players (ID);

create unique index players_identity_uindex
    on players (identity);

create unique index players_name_uindex
    on players (name);

CREATE TRIGGER set_default_player_name
    AFTER INSERT
    ON players
BEGIN
    UPDATE players SET name = 'Player' || ID WHERE name IS NULL;
end;