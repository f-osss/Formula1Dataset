use
cs3380;

drop table if exists circuit;
drop table if exists city;
drop table if exists constructor;
drop table if exists drivesFor;
drop table if exists driver;
drop table if exists race;
drop table if exists compete;
drop table if exists result;
drop table if exists sprintResult;
drop table if exists driverStanding;
drop table if exists constructorResults;
drop table if exists constructorStanding;
drop table if exists pitstop;
drop table if exists status;
drop table if exists qualifyingRecord;
drop table if exists laptime;
drop table if exists records;
drop table if exists completeLapTime;

create table circuit(
    circuitID  integer primary key IDENTITY(1,1),
    cityID     integer references city (cityID),
    circuitRef varchar(255)  not null,
    name       text  not null,
    long       decimal(9, 6) not null,
    lat        decimal(9, 6) not null,
    altitude   integer       not null
);

--saida
CREATE TABLE City (
    cityID INT PRIMARY KEY IDENTITY(1,1),  
    name VARCHAR(100) NOT NULL,            
    country VARCHAR(100) NOT NULL          
);

create table constructor(
    constructorID integer primary key IDENTITY(1,1),
    constructorRef varchar(255) not null,
    name text not null,
    nationality text not null
);

-- saida
CREATE TABLE DrivesFor (
constructorID INT NOT NULL,
driverID INT NOT NULL,
PRIMARY KEY (constructorID, driverID),
FOREIGN KEY (constructorID) REFERENCES Constructor(constructorID), -- Reference to Constructor table
FOREIGN KEY (driverID) REFERENCES Driver(driverID)                 -- Reference to Driver table
)

create table race(
    raceID integer primary key IDENTITY(1,1),
    year integer not null,
    round integer not null,
    circuitID integer references circuit(circuitID),
    name text not null,
    date date not null,
    time time
);

-- saida
CREATE TABLE Constructor_Results (
constructorResultsID INT PRIMARY KEY IDENTITY(1,1),
raceID INT NOT NULL,
constructorID INT NOT NULL,
points DECIMAL(10, 2) NOT NULL,
status VARCHAR(255) NOT NULL,
FOREIGN KEY (raceID) REFERENCES Race(raceID),     -- Reference to the Race table
FOREIGN KEY (constructorID) REFERENCES Constructor(constructorID) -- Reference to the Constructor table
);

--saida
CREATE TABLE ConstructorStanding (
constructorStandingID INT PRIMARY KEY IDENTITY(1,1),
raceID INT NOT NULL,
wins INT NOT NULL,
points DECIMAL(10, 2) NOT NULL,
position INT NOT NULL,
constructorID INT NOT NULL,                           -- ID of the constructor
FOREIGN KEY (raceID) REFERENCES Race(raceID),         -- Reference to the Race table
FOREIGN KEY (constructorID) REFERENCES Constructor(constructorID) -- Reference to the Constructor table
);


CREATE TABLE QualifyingRecord (
qualifyID INT PRIMARY KEY IDENTITY(1,1),
raceID INT NOT NULL,
driverID INT NOT NULL,
constructorID INT NOT NULL,
number INT NOT NULL,
position INT NOT NULL,
q1 DECIMAL(10, 3) NOT NULL,
q2 DECIMAL(10, 3) NULL,              -- Time for Q2 session (optional, NULL if not reached)
q3 DECIMAL(10, 3) NULL,                -- Time for Q3 session (optional, NULL if not reached)
FOREIGN KEY (raceID) REFERENCES Race(raceID),              -- Links to Race table
FOREIGN KEY (driverID) REFERENCES Driver(driverID),        -- Links to Driver table
FOREIGN KEY (constructorID) REFERENCES Constructor(constructorID) -- Links to Constructor table
);


CREATE TABLE CompletesLapTime (
lapID INT NOT NULL,
driverID INT NOT NULL,
lapTime DECIMAL(10, 3) NOT NULL,
PRIMARY KEY (lapID, driverID),    -- Composite primary key to ensure each driver’s lap is unique
FOREIGN KEY (driverID) REFERENCES Driver(driverID) -- Reference to Driver table
);



create table compete(
    raceID integer references race(raceID) on delete cascade,
    driverID integer references driver(driverID) on delete cascade,
    primary key (raceID, driverID)
);

create table sprintResult(
    resultID integer primary key IDENTITY(1,1),
    raceID integer references race(raceID) on delete cascade,
    driverID integer references driver(driverID) on delete cascade,
    constructorID integer references constructor(constructorID) on delete cascade ,
    number integer not null,
    grid integer not null,
    position integer,
    positionOrder integer not null,
    points integer not null,
    laps integer not null,
    time varchar(100),
    milliseconds integer,
    fastestLap integer,
    fastestLapTime time,
    statusID integer references status(statusID)
);

create table driverStanding(
    driverStandingID integer primary key IDENTITY(1,1),
    raceID integer references race(raceID) on delete cascade,
    driverID integer references driver(driverID) on delete cascade,
    points decimal(5,2) not null,
    position integer not null,
    wins integer not null
);
