create table if not exists smartwatch (
	id text not null primary key,
	name text not null
);

create table if not exists datapoint (
	id integer not null primary key autoincrement,
	watch_id text not null,
	instant integer not null default 0,
	amount unsigned integer not null,

	foreign key (watch_id) references smartwatch(id)
);

create table if not exists datapoint_entry (
	datapoint_id not null,
	i integer not null,
	type integer not null,
	value blob not null,

	foreign key (datapoint_id) references datapoint(id)
);

create table if not exists meta (
    key text not null primary key,
    value text
);
insert or ignore into meta(key, value) values("schema_version", "1");
