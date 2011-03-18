Create table Voter (
	id Integer Not Null Primary Key Generated Always as Identity (Start with 1, Increment by 1),
	user_name Varchar(30) Not Null Unique,
	first_name Varchar(30) Not Null,
	last_name Varchar(30) Not Null,
	password Blob(32) Not Null
);
Create table Voting (
	id Integer Not Null Primary Key Generated Always as Identity (Start with 1, Increment by 1),
	date Date Not Null,
	test Integer,
	secret Integer
);
Create table Vote (
	id Integer Not Null Primary Key Generated Always as Identity (Start with 1, Increment by 1),
	question_id Integer,
	voter_id Integer,
	eval Integer
);
Create table Question (
	id Integer Not Null Primary Key Generated Always as Identity (Start with 1, Increment by 1),
	voting_id Integer Not Null,
	text VarChar(1000) Not Null,
	min_percent Integer,
	max_select Integer,
	min_select Integer,
	max_winners Integer,
	evaluation Integer
);
Create table Alternative (
	id Integer Not Null Primary Key Generated Always as Identity (Start with 1, Increment by 1),
	question_id Integer Not Null,
	correct Integer,
	text VarChar(1000) Not Null
);
Create table Participant (
	voter_id Integer Not Null,
	voting_id Integer Not Null,
	Constraint PK_Participant Primary Key (voting_id, voter_id)
);
Create table Checked (
	alternative_id Integer Not Null,
	vote_id Integer Not Null,
	Constraint UNQ_is_checked_1 Unique (alternative_id, vote_id)
);
Alter table Question add
	Constraint FK_Question_1 Foreign Key (voting_id) References Voting(id)
;
Alter table Alternative add
	Constraint FK_Alternative_1 Foreign Key (question_id) References Question(id)
;
Alter table Checked add
	Constraint FK_checked_1 Foreign Key (alternative_id) References Alternative(id)
;
Alter table Checked add
	Constraint FK_checked_2 Foreign Key (vote_id) References Vote(id)
;
Alter table Participant add
	Constraint FK_voter_1 Foreign Key (voter_id) References Voter(id)
;
Alter table Participant add
	Constraint FK_voter_3 Foreign Key (voting_id) References Voting(id)
;
Alter table Vote add
	Constraint FK_vote_1 Foreign Key (voter_id) References Voter(id)
;
Alter table Vote add
	Constraint FK_vote_2 Foreign Key (question_id) References Question(id)
;