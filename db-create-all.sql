create table blog (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  content                       TEXT,
  author_id                     bigint not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_blog primary key (id)
);

create table blog_tag (
  blog_id                       bigint not null,
  tag_id                        bigint not null,
  constraint pk_blog_tag primary key (blog_id,tag_id)
);

create table blog_stat (
  id                            bigint auto_increment not null,
  when_created                  datetime(6),
  rank                          double,
  float_up                      double,
  tune                          integer,
  likes                         integer,
  views                         integer,
  comments                      integer,
  when_modified                 datetime(6) not null,
  constraint pk_blog_stat primary key (id)
);

create table comment (
  id                            bigint auto_increment not null,
  content                       TEXT,
  author_id                     bigint not null,
  source_type                   smallint,
  source_id                     bigint,
  reply_user_id                 bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_comment primary key (id)
);

create table fav (
  id                            bigint auto_increment not null,
  link                          varchar(255),
  owner_id                      bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_fav primary key (id)
);

create table file_item (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  web_path                      varchar(255),
  store_path                    varchar(255),
  owner_id                      bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_file_item primary key (id)
);

create table follow (
  id                            bigint auto_increment not null,
  source_id                     bigint not null,
  target_id                     bigint not null,
  reason                        varchar(255),
  is_include_new                tinyint(1) default 0,
  is_include_all                tinyint(1) default 0,
  user_tag_offset               bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_follow primary key (id)
);

create table follow_tag (
  follow_id                     bigint not null,
  tag_id                        bigint not null,
  constraint pk_follow_tag primary key (follow_id,tag_id)
);

create table follow_list_entity (
  id                            bigint auto_increment not null,
  owner_id                      bigint,
  name                          varchar(255),
  list_json                     TEXT,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_follow_list_entity primary key (id)
);

create table follow_list_heed (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  list_id                       bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_follow_list_heed primary key (id)
);

create table liking (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  like_type                     smallint,
  like_id                       bigint,
  when_created                  datetime(6) not null,
  constraint uq_liking_user_id_like_type_like_id unique (user_id,like_type,like_id),
  constraint pk_liking primary key (id)
);

create table login_pass (
  pass_id                       varchar(255) not null,
  user_id                       bigint,
  when_to_expire                datetime(6),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_login_pass primary key (pass_id)
);

create table message (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  from_user                     bigint,
  to_user                       bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_message primary key (id)
);

create table notification (
  id                            bigint auto_increment not null,
  owner_id                      bigint,
  sender_id                     bigint,
  type                          integer,
  source_id                     bigint,
  is_read                       tinyint(1) default 0,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint ck_notification_type check (type in (0,1,2,3,4,5,6,7,8,9,10)),
  constraint pk_notification primary key (id)
);

create table resource_list_entity (
  id                            bigint auto_increment not null,
  owner_id                      bigint,
  name                          varchar(255),
  list_json                     TEXT,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_resource_list_entity primary key (id)
);

create table tag (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  is_core                       tinyint(1) default 0,
  intro                         TEXT,
  parent_id                     bigint,
  creator_id                    bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_tag primary key (id)
);

create table tag_change_request (
  id                            bigint auto_increment not null,
  tag_id                        bigint,
  submitter_id                  bigint,
  type                          integer,
  transactor_id                 bigint,
  status                        integer,
  parent_id                     bigint,
  name                          varchar(255),
  intro                         varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint ck_tag_change_request_type check (type in (0,1,2)),
  constraint ck_tag_change_request_status check (status in (0,1,2,3)),
  constraint pk_tag_change_request primary key (id)
);

create table tag_heed (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  tag_id                        bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_tag_heed primary key (id)
);

create table topic_post (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  content                       TEXT,
  reference                     varchar(255),
  author_id                     bigint not null,
  belong_tag_id                 bigint,
  max_floor_number              integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  when_last_active              datetime(6) not null,
  constraint pk_topic_post primary key (id)
);

create table topic_post_tag (
  topic_post_id                 bigint not null,
  tag_id                        bigint not null,
  constraint pk_topic_post_tag primary key (topic_post_id,tag_id)
);

create table topic_reply (
  id                            bigint auto_increment not null,
  content                       TEXT,
  author_id                     bigint not null,
  topic_post_id                 bigint,
  to_user_id                    bigint,
  to_reply_id                   bigint,
  floor_number                  integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_topic_reply primary key (id)
);

create table topic_stat (
  id                            bigint auto_increment not null,
  when_created                  datetime(6),
  rank                          double,
  float_up                      double,
  tune                          integer,
  likes                         integer,
  views                         integer,
  when_last_replied             datetime(6),
  replies                       integer,
  when_modified                 datetime(6) not null,
  constraint pk_topic_stat primary key (id)
);

create table tweet (
  id                            bigint auto_increment not null,
  content                       TEXT,
  rich_elements_json            TEXT,
  mid_forwards_json             TEXT,
  author_id                     bigint not null,
  origin_id                     bigint not null,
  blog_id                       bigint,
  deleted                       tinyint(1) default 0,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_tweet primary key (id)
);

create table tweet_tag (
  tweet_id                      bigint not null,
  tag_id                        bigint not null,
  constraint pk_tweet_tag primary key (tweet_id,tag_id)
);

create table user (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  name                          varchar(255),
  password                      varchar(255),
  intro                         varchar(255),
  avatar                        varchar(255),
  authority                     integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint ck_user_authority check (authority in (0,1,2)),
  constraint pk_user primary key (id)
);

create table user_tag (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  tag_id                        bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_user_tag_user_id_tag_id unique (user_id,tag_id),
  constraint pk_user_tag primary key (id)
);

create index ix_blog_stat_rank on blog_stat (rank);
create index ix_topic_post_when_last_active on topic_post (when_last_active);
create index ix_topic_stat_rank on topic_stat (rank);
alter table blog add constraint fk_blog_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_blog_author_id on blog (author_id);

alter table blog_tag add constraint fk_blog_tag_blog foreign key (blog_id) references blog (id) on delete restrict on update restrict;
create index ix_blog_tag_blog on blog_tag (blog_id);

alter table blog_tag add constraint fk_blog_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_blog_tag_tag on blog_tag (tag_id);

alter table comment add constraint fk_comment_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_comment_author_id on comment (author_id);

alter table fav add constraint fk_fav_owner_id foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_fav_owner_id on fav (owner_id);

alter table follow add constraint fk_follow_source_id foreign key (source_id) references user (id) on delete restrict on update restrict;
create index ix_follow_source_id on follow (source_id);

alter table follow add constraint fk_follow_target_id foreign key (target_id) references user (id) on delete restrict on update restrict;
create index ix_follow_target_id on follow (target_id);

alter table follow_tag add constraint fk_follow_tag_follow foreign key (follow_id) references follow (id) on delete restrict on update restrict;
create index ix_follow_tag_follow on follow_tag (follow_id);

alter table follow_tag add constraint fk_follow_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_follow_tag_tag on follow_tag (tag_id);

alter table follow_list_heed add constraint fk_follow_list_heed_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_follow_list_heed_user_id on follow_list_heed (user_id);

alter table follow_list_heed add constraint fk_follow_list_heed_list_id foreign key (list_id) references follow_list_entity (id) on delete restrict on update restrict;
create index ix_follow_list_heed_list_id on follow_list_heed (list_id);

alter table tag add constraint fk_tag_parent_id foreign key (parent_id) references tag (id) on delete restrict on update restrict;
create index ix_tag_parent_id on tag (parent_id);

alter table tag_change_request add constraint fk_tag_change_request_tag_id foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_tag_change_request_tag_id on tag_change_request (tag_id);

alter table tag_change_request add constraint fk_tag_change_request_submitter_id foreign key (submitter_id) references user (id) on delete restrict on update restrict;
create index ix_tag_change_request_submitter_id on tag_change_request (submitter_id);

alter table tag_change_request add constraint fk_tag_change_request_transactor_id foreign key (transactor_id) references user (id) on delete restrict on update restrict;
create index ix_tag_change_request_transactor_id on tag_change_request (transactor_id);

alter table tag_heed add constraint fk_tag_heed_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_tag_heed_user_id on tag_heed (user_id);

alter table tag_heed add constraint fk_tag_heed_tag_id foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_tag_heed_tag_id on tag_heed (tag_id);

alter table topic_post add constraint fk_topic_post_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_topic_post_author_id on topic_post (author_id);

alter table topic_post add constraint fk_topic_post_belong_tag_id foreign key (belong_tag_id) references tag (id) on delete restrict on update restrict;
create index ix_topic_post_belong_tag_id on topic_post (belong_tag_id);

alter table topic_post_tag add constraint fk_topic_post_tag_topic_post foreign key (topic_post_id) references topic_post (id) on delete restrict on update restrict;
create index ix_topic_post_tag_topic_post on topic_post_tag (topic_post_id);

alter table topic_post_tag add constraint fk_topic_post_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_topic_post_tag_tag on topic_post_tag (tag_id);

alter table topic_reply add constraint fk_topic_reply_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_topic_reply_author_id on topic_reply (author_id);

alter table tweet add constraint fk_tweet_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_tweet_author_id on tweet (author_id);

alter table tweet_tag add constraint fk_tweet_tag_tweet foreign key (tweet_id) references tweet (id) on delete restrict on update restrict;
create index ix_tweet_tag_tweet on tweet_tag (tweet_id);

alter table tweet_tag add constraint fk_tweet_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_tweet_tag_tag on tweet_tag (tag_id);

