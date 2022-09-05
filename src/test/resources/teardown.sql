SET REFERENTIAL_INTEGRITY FALSE;
delete from elimination_user where subject = 'subject0';
delete from elimination_user where subject = 'subject1';
SET REFERENTIAL_INTEGRITY TRUE;