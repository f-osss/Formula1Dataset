use
cs3380;

DROP TABLE IF EXISTS laptime;
DROP TABLE IF EXISTS pitstop;
DROP TABLE IF EXISTS result;
DROP TABLE IF EXISTS driverStanding;
DROP TABLE IF EXISTS sprintResult;
DROP TABLE IF EXISTS qualifyingRecord;
DROP TABLE IF EXISTS constructorStanding;
DROP TABLE IF EXISTS constructorResults;
DROP TABLE IF EXISTS compete;
DROP TABLE IF EXISTS drivesFor;
DROP TABLE IF EXISTS race;
DROP TABLE IF EXISTS circuit;
DROP TABLE IF EXISTS status;
DROP TABLE IF EXISTS constructor;
DROP TABLE IF EXISTS driver;
DROP TABLE IF EXISTS city;

CREATE TABLE city
(
    cityID  INT PRIMARY KEY IDENTITY(1,1),
    name    VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL
);

create table circuit
(
    circuitID  integer primary key,
    cityID     integer references city (cityID) ON DELETE CASCADE ON UPDATE CASCADE,
    circuitRef varchar(255)  not null,
    name       text          not null,
    long       decimal(9, 6) not null,
    lat        decimal(9, 6) not null,
    altitude   integer       not null
);



create table constructor
(
    constructorID  integer primary key,
    constructorRef varchar(255) not null,
    name           text         not null,
    nationality    text         not null
);

create table driver
(
    driverID    INT PRIMARY KEY,
    driverRef   NVARCHAR(255) NOT NULL,
    forename    NVARCHAR(255) NOT NULL,
    surname     NVARCHAR(255) NOT NULL,
    number      INT NULL,
    nationality NVARCHAR(255) NULL,
    code        CHAR(3) NULL,
    dob         DATE NULL
);


-- saida
CREATE TABLE drivesFor
(
    constructorID  integer references constructor (constructorID) on delete cascade,
    driverID       integer references driver (driverID) on delete cascade,
    PRIMARY KEY (constructorID, driverID)
);

create table race
(
    raceID    integer primary key,
    year      integer not null,
    round     integer not null,
    circuitID integer references circuit (circuitID),
    name      text    not null,
    date      date    not null,
    time      time
);

-- saida
CREATE TABLE constructorResults
(
    constructorResultsID INT PRIMARY KEY,
    raceID               INT            NOT NULL,
    constructorID        INT            NOT NULL,
    points               DECIMAL(10, 2) NOT NULL,
    status               VARCHAR(255),
    FOREIGN KEY (raceID) REFERENCES race (raceID),                     -- Reference to the Race table
    FOREIGN KEY (constructorID) REFERENCES constructor (constructorID) -- Reference to the Constructor table
);

CREATE TABLE constructorStanding
(
    constructorStandingID INT PRIMARY KEY,
    raceID                INT            NOT NULL,
    wins                  INT            NOT NULL,
    points                DECIMAL(10, 2) NOT NULL,
    position              INT            NOT NULL,
    constructorID         INT            NOT NULL,                     -- ID of the constructor
    FOREIGN KEY (raceID) REFERENCES race (raceID),                     -- Reference to the Race table
    FOREIGN KEY (constructorID) REFERENCES constructor (constructorID) -- Reference to the Constructor table
);


CREATE TABLE qualifyingRecord
(
    qualifyID     INT PRIMARY KEY,
    raceID        INT            NOT NULL,
    driverID      INT            NOT NULL,
    constructorID INT            NOT NULL,
    number        INT            NOT NULL,
    position      INT            NOT NULL,
    q1            DECIMAL(10, 3) ,
    q2            DECIMAL(10, 3) ,
    q3            DECIMAL(10, 3) ,                                 -- Time for Q3 session (optional, NULL if not reached)
    FOREIGN KEY (raceID) REFERENCES race (raceID),                     -- Links to Race table
    FOREIGN KEY (driverID) REFERENCES driver (driverID),               -- Links to Driver table
    FOREIGN KEY (constructorID) REFERENCES constructor (constructorID) -- Links to Constructor table
);



CREATE TABLE compete (
    competeID     INTEGER IDENTITY(1,1) PRIMARY KEY,
    raceID        INTEGER REFERENCES race (raceID) ON DELETE CASCADE,
    driverID      INTEGER REFERENCES driver (driverID) ON DELETE CASCADE
);

create table status
(
    statusID INT PRIMARY KEY,
    status   NVARCHAR(255) NOT NULL
);

create table sprintResult
(
    resultID       integer primary key,
    raceID         integer references race (raceID) on delete cascade,
    driverID       integer references driver (driverID) on delete cascade,
    constructorID  integer references constructor (constructorID) on delete cascade,
    number         integer not null,
    grid           integer not null,
    position       integer,
    positionOrder  integer not null,
    points         integer not null,
    laps           integer not null,
    time           varchar(100),
    milliseconds   integer,
    fastestLap     integer,
    fastestLapTime varchar(10),
    statusID       integer references status (statusID)
);

create table driverStanding
(
    driverStandingID integer primary key,
    raceID           integer references race (raceID) on delete cascade,
    driverID         integer references driver (driverID) on delete cascade,
    points           decimal(5, 2) not null,
    position         integer       not null,
    wins             integer       not null
);


create table result
(
    resultID        INT PRIMARY KEY,
    raceID           integer references race (raceID) on delete cascade,
    driverID         integer references driver (driverID) on delete cascade,
    constructorID   integer references constructor (constructorID) on delete cascade,
    number          INT NULL,
    grid            INT            NOT NULL,
    positionOrder   INT            NOT NULL,
    points          DECIMAL(10, 2) NOT NULL,
    laps            INT            NOT NULL,
    time            NVARCHAR(255) NULL,
    milliseconds    INT NULL,
    fastestLap      INT NULL,
    rank            INT NULL,
    fastestLapTime  NVARCHAR(255) NULL,
    fastestLapSpeed DECIMAL(10, 3) NULL,
    statusID        INT            NOT NULL,
);

create table pitstop
(
    raceID       INT NOT NULL,
    driverID     INT NOT NULL,
    stop         INT NOT NULL,
    lap          INT NOT NULL,
    time         NVARCHAR(255) NOT NULL,
    duration     NVARCHAR(255) NOT NULL,
    milliseconds INT NOT NULL,
    PRIMARY KEY (raceID, driverID, stop),
    FOREIGN KEY (driverID) REFERENCES driver (driverID)
);

create table laptime
(
    raceID       INT NOT NULL,
    driverID     INT NOT NULL,
    lap          INT NOT NULL,
    position     INT NOT NULL,
    time         NVARCHAR(255) NOT NULL,
    milliseconds INT NOT NULL,
    PRIMARY KEY (raceID, driverID, lap),
    FOREIGN KEY (driverID) REFERENCES Driver (driverID)
);
