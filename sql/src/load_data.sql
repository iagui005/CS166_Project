/* Using CSV file format for load data */
\COPY usr FROM '~/Courses/cs166/CS166_Project/data/usr.csv' WITH DELIMITER ',' CSV
\COPY work_expr FROM '~/Courses/cs166/CS166_Project/data/work_expr.csv' WITH DELIMITER ',' CSV
\COPY educational_details FROM '~/Courses/cs166/CS166_Project/data/educational_details.csv' WITH DELIMITER ',' CSV
\COPY connection_usr FROM '~/Courses/cs166/CS166_Project/data/connection_usr.csv' WITH DELIMITER ',' CSV
\COPY message FROM '~/Courses/cs166/CS166_Project/data/message.csv' WITH DELIMITER ',' CSV
