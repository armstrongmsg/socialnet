create table users(
	userId VARCHAR(100), 
	username VARCHAR(100), 
	password VARCHAR(100), 
	description VARCHAR(300)
	);
create table posts(
	id VARCHAR(100), 
	title VARCHAR(100), 
	timestamp BIGINT, 
	content VARCHAR(500), 
	visibility VARCHAR(20)
	);
create table users_posts(
	user_userid VARCHAR(100),
	posts_id VARCHAR(100)
	);