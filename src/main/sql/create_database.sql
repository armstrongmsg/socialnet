create table users(
	userid VARCHAR(100) PRIMARY KEY, 
	username VARCHAR(100), 
	password VARCHAR(100), 
	description VARCHAR(300),
	profile_pic_id VARCHAR(100)
	);
create table posts(
	id VARCHAR(100) PRIMARY KEY, 
	title VARCHAR(100), 
	timestamp BIGINT, 
	content VARCHAR(500), 
	visibility VARCHAR(20),
	picture_id VARCHAR(100)
	);
create table users_posts(
	user_userid VARCHAR(100),
	posts_id VARCHAR(100)
	);
alter table users_posts add constraint "users_posts_userid_fk" foreign key ("user_userid") references "users" ("userid");
alter table users_posts add constraint "users_posts_posts_fk" foreign key ("posts_id") references "posts" ("id");
create table friendships (
	id VARCHAR(100) PRIMARY KEY,
	friend1_userid VARCHAR(100),
	friend2_userid VARCHAR(100)
	);
alter table friendships add constraint "friendships_friend1_fk" foreign key ("friend1_userid") references "users" ("userid");
alter table friendships add constraint "friendships_friend2_fk" foreign key ("friend2_userid") references "users" ("userid");
create table follows (
	id VARCHAR(100) PRIMARY KEY,
	follower_userid VARCHAR(100),
	followed_userid VARCHAR(100)
	);
alter table follows add constraint "follows_follower_fk" foreign key ("follower_userid") references "users" ("userid");
alter table follows add constraint "follows_followed_fk" foreign key ("followed_userid") references "users" ("userid");
create table friendship_requests (
	id VARCHAR(100) PRIMARY KEY,
	requester_userid VARCHAR(100),
	requested_userid VARCHAR(100)
	);
alter table friendship_requests add constraint "friendship_requests_requester_fk" foreign key ("requester_userid") references "users" ("userid");
alter table friendship_requests add constraint "friendship_requests_requested_fk" foreign key ("requested_userid") references "users" ("userid");