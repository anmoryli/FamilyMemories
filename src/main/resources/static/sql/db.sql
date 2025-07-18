drop table if exists ai_history;
drop table if exists comments;
drop table if exists invitations;
drop table if exists photo_milestones;
drop table if exists photo_family_members;
drop table if exists photo_family_milestones;
drop table if exists milestones;
drop table if exists photos;
drop table if exists family_members;
drop table if exists families;
drop table if exists user_info;

# 用户信息表，用于存储用户信息
create table if not exists user_info (
                                         user_id int not null auto_increment primary key,
                                         username varchar(255) not null,
                                         password varchar(255) not null,
                                         email varchar(255),
                                         created_at datetime not null default current_timestamp,
                                         updated_at datetime not null default current_timestamp on update current_timestamp
);

# 家庭表，用于存储家庭信息
create table if not exists families (
                                        family_id int not null auto_increment primary key,
                                        family_name varchar(255) not null,
                                        family_description text,
                                        creator_id int not null,
                                        created_at datetime not null default current_timestamp,
                                        updated_at datetime not null default current_timestamp on update current_timestamp,
                                        foreign key (creator_id) references user_info(user_id) on delete cascade
);

# 家庭成员表，用于存储家庭成员信息
create table if not exists family_members (
                                              family_member_id int not null auto_increment primary key,
                                              family_id int not null,
                                              user_id int not null,
                                              role varchar(50) not null, # e.g., 'admin', 'member'
                                              joined_at datetime not null default current_timestamp,
                                              updated_at datetime not null default current_timestamp on update current_timestamp,
                                              foreign key (family_id) references families(family_id) on delete cascade,
                                              foreign key (user_id) references user_info(user_id) on delete cascade
);

# 照片表，用于存储家庭照片
create table if not exists photos (
                                      photo_id int not null auto_increment primary key,
                                      family_id int not null,
                                      uploader_id int not null,
                                      file_path varchar(1024) not null,
                                      title varchar(255),
                                      description text,
                                      tag varchar(255) default 'none', # 标签
                                      take_time datetime,
                                      take_at varchar(255),
                                      camera_parameters text,
                                      uploaded_at datetime not null default current_timestamp,
                                      updated_at datetime not null default current_timestamp on update current_timestamp,
                                      foreign key (family_id) references families(family_id) on delete cascade,
                                      foreign key (uploader_id) references user_info(user_id) on delete cascade
);

# 里程碑表，用于记录家庭的重要事件或里程碑
create table if not exists milestones (
                                          milestone_id int not null auto_increment primary key,
                                          family_id int not null,
                                          title varchar(255) not null,
                                          description text,
                                          event_date datetime not null,
                                          created_at datetime not null default current_timestamp,
                                          updated_at datetime not null default current_timestamp on update current_timestamp,
                                          foreign key (family_id) references families(family_id) on delete cascade
);

# 关联表，用于将照片与里程碑关联
create table if not exists photo_milestones (
                                                photo_id int not null,
                                                milestone_id int not null,
                                                created_at datetime not null default current_timestamp,
                                                updated_at datetime not null default current_timestamp on update current_timestamp,
                                                primary key (photo_id, milestone_id),
                                                foreign key (photo_id) references photos(photo_id) on delete cascade,
                                                foreign key (milestone_id) references milestones(milestone_id) on delete cascade
);

create table if not exists photo_family_members (
                                                photo_id int not null,
                                                user_id int not null,
                                                created_at datetime not null default current_timestamp,
                                                updated_at datetime not null default current_timestamp on update current_timestamp,
                                                primary key (photo_id, user_id),
                                                foreign key (photo_id) references photos(photo_id) on delete cascade,
                                                foreign key (user_id) references family_members(user_id) on delete cascade
);

# 邀请表，用于存储家庭邀请信息
create table if not exists invitations (
                                           invitation_id int not null auto_increment primary key,
                                           family_id int not null,
                                           inviter_id int not null,# 邀请人 ID
                                           invitee_id int,# 可选，被邀请的用户 ID
                                           status varchar(50) not null default 'pending', # e.g., 'pending', 'accepted', 'declined'
                                           created_at datetime not null default current_timestamp,
                                           updated_at datetime not null default current_timestamp on update current_timestamp,
                                           foreign key (family_id) references families(family_id) on delete cascade,
                                           foreign key (inviter_id) references user_info(user_id) on delete cascade
);

# 评论表，用于存储用户对照片的评论
create table if not exists comments (
                                        comment_id int not null auto_increment primary key,
                                        photo_id int not null,
                                        user_id int not null,
                                        content text not null,
                                        created_at datetime not null default current_timestamp,
                                        updated_at datetime not null default current_timestamp on update current_timestamp,
                                        foreign key (photo_id) references photos(photo_id) on delete cascade,
                                        foreign key (user_id) references user_info(user_id) on delete cascade
);

# AI 历史记录表，用于存储用户与 AI 的交互历史
create table if not exists ai_history (
                                          his_id int not null auto_increment primary key,
                                          user_id int not null,
                                          query text not null,
                                          response text not null,
                                          created_at datetime not null default current_timestamp,
                                          updated_at datetime not null default current_timestamp on update current_timestamp,
                                          foreign key (user_id) references user_info(user_id) on delete cascade
);

# 索引创建
CREATE INDEX idx_photos_family_id ON photos(family_id);
CREATE INDEX idx_photos_take_time ON photos(take_time);
CREATE INDEX idx_family_members_user_id ON family_members(user_id);
CREATE INDEX idx_invitations_invitee_id ON invitations(invitee_id);