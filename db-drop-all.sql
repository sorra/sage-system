alter table blog drop foreign key fk_blog_author_id;
drop index ix_blog_author_id on blog;

alter table blog_tag drop foreign key fk_blog_tag_blog;
drop index ix_blog_tag_blog on blog_tag;

alter table blog_tag drop foreign key fk_blog_tag_tag;
drop index ix_blog_tag_tag on blog_tag;

alter table comment drop foreign key fk_comment_author_id;
drop index ix_comment_author_id on comment;

alter table fav drop foreign key fk_fav_owner_id;
drop index ix_fav_owner_id on fav;

alter table follow drop foreign key fk_follow_source_id;
drop index ix_follow_source_id on follow;

alter table follow drop foreign key fk_follow_target_id;
drop index ix_follow_target_id on follow;

alter table follow_tag drop foreign key fk_follow_tag_follow;
drop index ix_follow_tag_follow on follow_tag;

alter table follow_tag drop foreign key fk_follow_tag_tag;
drop index ix_follow_tag_tag on follow_tag;

alter table follow_list_heed drop foreign key fk_follow_list_heed_user_id;
drop index ix_follow_list_heed_user_id on follow_list_heed;

alter table follow_list_heed drop foreign key fk_follow_list_heed_list_id;
drop index ix_follow_list_heed_list_id on follow_list_heed;

alter table tag drop foreign key fk_tag_parent_id;
drop index ix_tag_parent_id on tag;

alter table tag_change_request drop foreign key fk_tag_change_request_tag_id;
drop index ix_tag_change_request_tag_id on tag_change_request;

alter table tag_change_request drop foreign key fk_tag_change_request_submitter_id;
drop index ix_tag_change_request_submitter_id on tag_change_request;

alter table tag_change_request drop foreign key fk_tag_change_request_transactor_id;
drop index ix_tag_change_request_transactor_id on tag_change_request;

alter table tag_heed drop foreign key fk_tag_heed_user_id;
drop index ix_tag_heed_user_id on tag_heed;

alter table tag_heed drop foreign key fk_tag_heed_tag_id;
drop index ix_tag_heed_tag_id on tag_heed;

alter table topic_post drop foreign key fk_topic_post_author_id;
drop index ix_topic_post_author_id on topic_post;

alter table topic_post drop foreign key fk_topic_post_belong_tag_id;
drop index ix_topic_post_belong_tag_id on topic_post;

alter table topic_post_tag drop foreign key fk_topic_post_tag_topic_post;
drop index ix_topic_post_tag_topic_post on topic_post_tag;

alter table topic_post_tag drop foreign key fk_topic_post_tag_tag;
drop index ix_topic_post_tag_tag on topic_post_tag;

alter table topic_reply drop foreign key fk_topic_reply_author_id;
drop index ix_topic_reply_author_id on topic_reply;

alter table tweet drop foreign key fk_tweet_author_id;
drop index ix_tweet_author_id on tweet;

alter table tweet_tag drop foreign key fk_tweet_tag_tweet;
drop index ix_tweet_tag_tweet on tweet_tag;

alter table tweet_tag drop foreign key fk_tweet_tag_tag;
drop index ix_tweet_tag_tag on tweet_tag;

drop table if exists blog;

drop table if exists blog_tag;

drop table if exists blog_stat;

drop table if exists comment;

drop table if exists fav;

drop table if exists file_item;

drop table if exists follow;

drop table if exists follow_tag;

drop table if exists follow_list_entity;

drop table if exists follow_list_heed;

drop table if exists liking;

drop table if exists login_pass;

drop table if exists message;

drop table if exists notification;

drop table if exists resource_list_entity;

drop table if exists tag;

drop table if exists tag_change_request;

drop table if exists tag_heed;

drop table if exists topic_post;

drop table if exists topic_post_tag;

drop table if exists topic_reply;

drop table if exists topic_stat;

drop table if exists tweet;

drop table if exists tweet_tag;

drop table if exists tweet_stat;

drop table if exists user;

drop table if exists user_tag;

drop index ix_blog_stat_rank on blog_stat;
drop index ix_topic_post_when_last_active on topic_post;
drop index ix_topic_stat_rank on topic_stat;
drop index ix_tweet_stat_rank on tweet_stat;
