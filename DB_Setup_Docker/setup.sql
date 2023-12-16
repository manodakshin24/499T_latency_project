CREATE USER anish;

CREATE DATABASE socialnetwork;

GRANT ALL ON DATABASE socialnetwork TO anish;

CREATE TABLE socialnetwork.tmptable (userID INT PRIMARY KEY, serverID INT, name VARCHAR(255));

CREATE TABLE socialnetwork.messages (userID INT, serverID INT, msg VARCHAR(1000));

-- Create the Message table with the "serverId" field
CREATE TABLE socialnetwork.message (
    creationDate timestamp with time zone NOT NULL,
    id bigint PRIMARY KEY,
    language varchar(80),
    content varchar(2000),
    imageFile varchar(80),
    locationIP varchar(80) NOT NULL,
    browserUsed varchar(80) NOT NULL,
    length int NOT NULL,
    CreatorPersonId bigint NOT NULL,
    ContainerForumId bigint,
    LocationCountryId bigint NOT NULL,
    ParentMessageId bigint,
    serverId int NOT NULL, -- Add the "serverId" field of type INT
    INDEX (LocationCountryId),
    INDEX (CreatorPersonId),
    INDEX (ContainerForumId),
    INDEX (ParentMessageId)
);

-- Create the Person table with the "serverId" field
CREATE TABLE socialnetwork.person (
    creationDate timestamp with time zone NOT NULL,
    id bigint PRIMARY KEY,
    firstName varchar(80) NOT NULL,
    lastName varchar(80) NOT NULL,
    gender varchar(80) NOT NULL,
    birthday date NOT NULL,
    locationIP varchar(80) NOT NULL,
    browserUsed varchar(80) NOT NULL,
    LocationCityId bigint NOT NULL,
    speaks varchar(640) NOT NULL,
    email varchar(8192) NOT NULL,
    serverId int NOT NULL, -- Add the "serverId" field of type INT
    INDEX (LocationCityId)
);

INSERT INTO socialnetwork.tmptable VALUES (65, 1, 'Bob'), (66, 2, 'Charlie'), (67, 3, 'Duncan'), (68, 4, 'Emily'), (69, 5, 'Frank'), (70, 6, 'George');

INSERT INTO socialnetwork.messages VALUES (65, 1, 'Hello, I am your debt collector'), (65, 1, 'You cant escape me'), (66, 2, 'You still owe me $2000'), (66, 2, 'I know where you live');

-- Insert toy data into the Message table
INSERT INTO socialnetwork.message (creationDate, id, language, content, imageFile, locationIP, browserUsed, length, CreatorPersonId, ContainerForumId, LocationCountryId, ParentMessageId, serverId)
VALUES
    ('2010-01-03 20:20:36.28+00', 3, 'fa', 'About Wolfgang Amadeus Mozart, financial security. DuringAbout Thomas Jefferson, al slave trade, and adAbout Hen', NULL, '77.245.239.11', 'Firefox', 108, 14, 0, 80, NULL, 1),
    ('2010-02-27 20:05:54.095+00', 545, 'pl', 'About Shania Twain, ed 72nd on Billboards Artists of the decAbout Plato, Modern world. Along with his mentor, ', NULL, '31.182.127.125', 'Internet Explorer', 121, 27, 38, 92, NULL, 2),
    ('2010-02-02 13:34:05.094+00', 3612, 'ta', 'About Maria Theresa, Lorraine, Grand Duchess of Tuscany and Holy Roman Empress. She started her 40-year rAbout', NULL, '91.214.100.136', 'Internet Explorer', 247, 113, 187, 17, NULL, 3),
    ('2010-02-19 12:13:02.81+00', 4818, 'en', 'About Edvard Munch, f the main tenets of late 19th-century Symbolism and greatly influenced German Expression', NULL, '186.10.61.96', 'Firefox', 121, 137, 188, 69, NULL, 3),
    ('2010-02-11 09:32:45.423+00', 8316, 'es', 'About Franz Schubert, Peter Schubert (31 January 1797 –About Noël Coward, tribute to the late Sir', NULL, '24.53.139.223', 'Internet Explorer', 98, 218, 230, 57, NULL, 4),
    ('2010-02-18 22:03:33.634+00', 8852, 'en', 'About Josip Bronze Tito, from World War II until 1991. Despite being one of the fAbout Benny Goodman, Mexican jazz a', NULL, '91.198.217.1', 'Chrome', 111, 251, 232, 86, NULL, 2);

-- Insert toy person data into the Person table
INSERT INTO socialnetwork.person (creationDate, id, firstName, lastName, gender, birthday, locationIP, browserUsed, LocationCityId, speaks, email, serverId)
VALUES
    ('2010-01-03 15:10:31.499+00', 14, 'Hossein', 'Forouhar', 'male', '1984-03-11', '77.245.239.11', 'Firefox', 1166, 'fa;ku;en', 'Hossein14@hotmail.com', 2),
    ('2010-01-19 13:51:10.863+00', 27, 'Wojciech', 'Ciesla', 'male', '1985-12-07', '31.182.127.125', 'Internet Explorer', 1282, 'pl;en', 'Wojciech27@gmail.com;Wojciech27@yahoo.com;Wojciech27@gmx.com;Wojciech27@zoho.com', 3),
    ('2010-01-28 04:16:14.101+00', 113, 'Abhishek', 'Singh', 'male', '1982-12-07', '61.95.201.3', 'Internet Explorer', 161, 'hi;ta;en', 'Abhishek113@4-music-today.com', 4),
    ('2010-02-17 03:21:38.348+00', 137, 'Eduardo', 'Gonzalez', 'male', '1981-03-28', '186.10.61.96', 'Firefox', 1050, 'es;de;en', 'Eduardo137@yahoo.com;Eduardo137@gmail.com', 2),
    ('2010-01-17 08:54:00.3+00', 218, 'Mike', 'Wilson', 'female', '1984-05-23', '24.53.139.223', 'Internet Explorer', 883, 'en;es', 'Mike218@gmail.com;Mike218@yahoo.com;Mike218@yours.com;Mike218@gmx.com', 1),
    ('2010-02-01 08:24:22.124+00', 251, 'Ion', 'Bologan', 'female', '1983-11-21', '91.198.217.1', 'Chrome', 1223, 'ru;en', 'Ion251@gmx.com;Ion251@netfingers.com;Ion251@gmail.com', 4);

GRANT ALL ON TABLE socialnetwork.* TO anish;

SELECT * FROM socialnetwork.tmptable;

SELECT * FROM socialnetwork.messages;

SELECT * FROM socialnetwork.message;

SELECT * FROM socialnetwork.person;