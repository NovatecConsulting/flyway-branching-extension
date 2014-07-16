create table "${releaseVersionSchemaName}"."${releaseVersionTableName}" (
	"CURRENT_RELEASE" varchar(20) not null,
	"CREATED_ON" timestamp default now() not null,
	constraint "${releaseVersionSchemaName}_${releaseVersionTableName}_PK" primary key ("CURRENT_RELEASE", "CREATED_ON")
);
create index "${releaseVersionSchemaName}_${releaseVersionTableName}_CREATED_ON_IDX" on "${releaseVersionSchemaName}"."${releaseVersionTableName}" ("CREATED_ON");

insert into "${releaseVersionSchemaName}"."${releaseVersionTableName}" ("CURRENT_RELEASE") values ('${defaultRelease}');
