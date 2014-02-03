# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"username" VARCHAR(254) NOT NULL,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"password" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "users";

