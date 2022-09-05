insert into elimination_user (subject, elimination_code, email, forename, surname, winner,
                                              eliminated_by_subject, target_subject)
values ('subject0', 'hello-eliminate-me', 'test@gmail.com', 'testuser', 'zero', false, null, null);
insert into elimination_user (subject, elimination_code, email, forename, surname, winner,
                                              eliminated_by_subject, target_subject)
values ('subject1', 'hello-eliminate-metoo', 'test1@gmail.com', 'testuser', 'one', false, null, null);

update elimination_user set target_subject = 'subject0' where subject = 'subject1';
update elimination_user set target_subject = 'subject1' where subject = 'subject0';