create table users(
	userid VARCHAR(100) PRIMARY KEY, 
	username VARCHAR(100), 
	password VARCHAR(100), 
	description VARCHAR(300)
	);
create table posts(
	id VARCHAR(100) PRIMARY KEY, 
	title VARCHAR(100), 
	timestamp BIGINT, 
	content VARCHAR(500), 
	visibility VARCHAR(20)
	);
create table users_posts(
	user_userid VARCHAR(100),
	posts_id VARCHAR(100)
	);
alter table users_posts add constraint "users_posts_userid_fk" foreign key ("user_userid") references "users" ("userid");
alter table users_posts add constraint "users_posts_posts_fk" foreign key ("posts_id") references "posts" ("id");