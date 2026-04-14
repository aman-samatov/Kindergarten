# Создание бд
>Сначала создать новый SQL сервер с sql authentication с простым паролем 123

```sql
CREATE DATABASE shop_netbeans;
```
```sql
USE shop_netbeans;
```
---
# Таблицы
1)
```sql
CREATE TABLE dbo.children (
    id INT IDENTITY(1,1) PRIMARY KEY,
    FIO VARCHAR(20) NULL,
    dateborn VARCHAR(20) NULL,
    address VARCHAR(20) NULL,
    tel_dad VARCHAR(20) NULL,
    tel_mom VARCHAR(20) NULL,
    groups TINYINT NULL,
    kruzhok TINYINT NULL,
    nationality TINYINT NULL
);
```
---
2)
```sql
CREATE TABLE dbo.employee (
    id INT IDENTITY(1,1) PRIMARY KEY,
    FIO VARCHAR(20) NULL,
    dateborn VARCHAR(50) NULL,
    position TINYINT NULL,
    adress VARCHAR(20) NULL,
    tel_number VARCHAR(25) NULL,
    kruzhok TINYINT NULL,
    groups TINYINT NULL
);
```
---
3) 
```sql
CREATE TABLE [dbo].[group] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    groups VARCHAR(20) NULL
);
```
```sql
INSERT INTO [dbo].[group] (groups) VALUES
(N'старшая группа'),
(N'средняя группа'),
(N'младшая группа'),
(N'яселька');
```
---
4) 
```sql
CREATE TABLE dbo.kruzhok (
    id INT IDENTITY(1,1) PRIMARY KEY,
    kruzhok VARCHAR(20) NULL
);
```
---
5)
```sql
CREATE TABLE dbo.nationality (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nationality VARCHAR(20) NULL
);
```
---
6)
```sql
CREATE TABLE dbo.position (
    id INT IDENTITY(1,1) PRIMARY KEY,
    position VARCHAR(20) NULL
);
```
---
7)
```sql
CREATE TABLE dbo.table_login (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_login VARCHAR(10) NULL,
    user_password VARCHAR(10) NULL
);
```
> ### ВАЖНО
> Добавить запись в таблицу ручками свой логин и пароль, будешь потом сам/сама вводить в программе
---
# Диаграмма базы данных 

> Важно
>> Перед этим перезапустить SSMStudio


```sql
USE shop_netbeans;
GO

ALTER TABLE dbo.employee ALTER COLUMN position INT NULL;
ALTER TABLE dbo.employee ALTER COLUMN groups INT NULL;
ALTER TABLE dbo.employee ALTER COLUMN kruzhok INT NULL;
GO

ALTER TABLE dbo.children ALTER COLUMN groups INT NULL;
ALTER TABLE dbo.children ALTER COLUMN kruzhok INT NULL;
ALTER TABLE dbo.children ALTER COLUMN nationality INT NULL;
GO

ALTER TABLE dbo.employee
ADD CONSTRAINT FK_employee_position
FOREIGN KEY (position) REFERENCES dbo.position(id);
GO

ALTER TABLE dbo.employee
ADD CONSTRAINT FK_employee_group
FOREIGN KEY (groups) REFERENCES [dbo].[group](id);
GO

ALTER TABLE dbo.employee
ADD CONSTRAINT FK_employee_kruzhok
FOREIGN KEY (kruzhok) REFERENCES dbo.kruzhok(id);
GO

ALTER TABLE dbo.children
ADD CONSTRAINT FK_children_group
FOREIGN KEY (groups) REFERENCES [dbo].[group](id);
GO

ALTER TABLE dbo.children
ADD CONSTRAINT FK_children_kruzhok
FOREIGN KEY (kruzhok) REFERENCES dbo.kruzhok(id);
GO

ALTER TABLE dbo.children
ADD CONSTRAINT FK_children_nationality
FOREIGN KEY (nationality) REFERENCES dbo.nationality(id);
GO
```
---
# Представления
1)
```sql
CREATE VIEW dbo.View_children
AS
SELECT 
    c.id,
    c.FIO,
    c.dateborn,
    c.address,
    c.tel_dad,
    c.tel_mom,
    n.nationality,
    k.kruzhok,
    g.groups
FROM dbo.children c
INNER JOIN dbo.nationality n ON n.id = c.nationality
INNER JOIN dbo.kruzhok k ON k.id = c.kruzhok
INNER JOIN dbo.[group] g ON g.id = c.groups;
```
---
2)
```sql
CREATE VIEW dbo.View_employee
AS
SELECT 
    e.id,
    e.FIO,
    e.dateborn,
    p.position,
    e.adress,
    e.tel_number,
    k.kruzhok,
    g.groups
FROM dbo.employee e
INNER JOIN dbo.position p ON p.id = e.position
INNER JOIN dbo.kruzhok k ON k.id = e.kruzhok
INNER JOIN dbo.[group] g ON g.id = e.groups;
```
---
3)
```sql
CREATE VIEW dbo.View_position
AS
SELECT 
    id,
    position
FROM dbo.position;
```
---
4)
```sql
CREATE VIEW dbo.View_id1
AS
SELECT 
    c.id,
    c.FIO,
    c.dateborn,
    c.address,
    c.tel_dad,
    c.tel_mom,
    n.nationality,
    k.kruzhok,
    g.groups
FROM dbo.children c
INNER JOIN dbo.nationality n ON n.id = c.nationality
INNER JOIN dbo.kruzhok k ON k.id = c.kruzhok
INNER JOIN dbo.[group] g ON g.id = c.groups
WHERE g.id = 1;
GO

CREATE VIEW dbo.View_id2
AS
SELECT 
    c.id,
    c.FIO,
    c.dateborn,
    c.address,
    c.tel_dad,
    c.tel_mom,
    n.nationality,
    k.kruzhok,
    g.groups
FROM dbo.children c
INNER JOIN dbo.nationality n ON n.id = c.nationality
INNER JOIN dbo.kruzhok k ON k.id = c.kruzhok
INNER JOIN dbo.[group] g ON g.id = c.groups
WHERE g.id = 2;
GO

CREATE VIEW dbo.View_id3
AS
SELECT 
    c.id,
    c.FIO,
    c.dateborn,
    c.address,
    c.tel_dad,
    c.tel_mom,
    n.nationality,
    k.kruzhok,
    g.groups
FROM dbo.children c
INNER JOIN dbo.nationality n ON n.id = c.nationality
INNER JOIN dbo.kruzhok k ON k.id = c.kruzhok
INNER JOIN dbo.[group] g ON g.id = c.groups
WHERE g.id = 3;
GO

CREATE VIEW dbo.View_id4
AS
SELECT 
    c.id,
    c.FIO,
    c.dateborn,
    c.address,
    c.tel_dad,
    c.tel_mom,
    n.nationality,
    k.kruzhok,
    g.groups
FROM dbo.children c
INNER JOIN dbo.nationality n ON n.id = c.nationality
INNER JOIN dbo.kruzhok k ON k.id = c.kruzhok
INNER JOIN dbo.[group] g ON g.id = c.groups
WHERE g.id = 4;
GO
```
---
# Создание всех процедур (7)
```sql
CREATE PROCEDURE [dbo].[insert_employee]
    @FIO VARCHAR(20),
    @dateborn VARCHAR(20),
    @position VARCHAR(20),
    @adress VARCHAR(20),
    @tel_number VARCHAR(25),
    @kruzhok VARCHAR(20),
    @group_name VARCHAR(20)
AS
BEGIN
    DECLARE @posid INT;
    DECLARE @kruzhid INT;
    DECLARE @grid INT;

    SELECT @posid = id FROM dbo.position WHERE position = @position;
    SELECT @kruzhid = id FROM dbo.kruzhok WHERE kruzhok = @kruzhok;
    SELECT @grid = id FROM dbo.[group] WHERE groups = @group_name;

    INSERT INTO dbo.employee
    (FIO, dateborn, position, adress, tel_number, kruzhok, groups)
    VALUES
    (@FIO, @dateborn, @posid, @adress, @tel_number, @kruzhid, @grid);
END
GO

CREATE PROCEDURE [dbo].[update_employee]
    @id INT,
    @FIO VARCHAR(20),
    @dateborn VARCHAR(20),
    @position VARCHAR(20),
    @adress VARCHAR(20),
    @tel_number VARCHAR(25),
    @kruzhok VARCHAR(20),
    @group_name VARCHAR(20)
AS
BEGIN
    DECLARE @posid INT;
    DECLARE @kruzhid INT;
    DECLARE @grid INT;

    SELECT @posid = id FROM dbo.position WHERE position = @position;
    SELECT @kruzhid = id FROM dbo.kruzhok WHERE kruzhok = @kruzhok;
    SELECT @grid = id FROM dbo.[group] WHERE groups = @group_name;

    UPDATE dbo.employee
    SET
        FIO = @FIO,
        dateborn = @dateborn,
        position = @posid,
        adress = @adress,
        tel_number = @tel_number,
        kruzhok = @kruzhid,
        groups = @grid
    WHERE id = @id;
END
GO

CREATE PROCEDURE [dbo].[delete_employee]
    @id INT
AS
BEGIN
    DELETE FROM dbo.employee
    WHERE id = @id;
END
GO

CREATE PROCEDURE [dbo].[insert_child]
    @FIO VARCHAR(20),
    @dateborn VARCHAR(20),
    @address VARCHAR(20),
    @tel_dad VARCHAR(25),
    @tel_mom VARCHAR(25),
    @groups VARCHAR(20),
    @kruzhok VARCHAR(20),
    @nationality VARCHAR(20)
AS
BEGIN
    DECLARE @grid INT;
    DECLARE @kruzhid INT;
    DECLARE @natid INT;

    SELECT @grid = id FROM dbo.[group] WHERE groups = @groups;
    SELECT @kruzhid = id FROM dbo.kruzhok WHERE kruzhok = @kruzhok;
    SELECT @natid = id FROM dbo.nationality WHERE nationality = @nationality;

    INSERT INTO dbo.children
    (FIO, dateborn, address, tel_dad, tel_mom, groups, kruzhok, nationality)
    VALUES
    (@FIO, @dateborn, @address, @tel_dad, @tel_mom, @grid, @kruzhid, @natid);
END
GO

CREATE PROCEDURE [dbo].[update_child]
    @id INT,
    @FIO VARCHAR(20),
    @dateborn VARCHAR(20),
    @address VARCHAR(20),
    @tel_dad VARCHAR(25),
    @tel_mom VARCHAR(25),
    @groups VARCHAR(20),
    @kruzhok VARCHAR(20),
    @nationality VARCHAR(20)
AS
BEGIN
    DECLARE @grid INT;
    DECLARE @kruzhid INT;
    DECLARE @natid INT;

    SELECT @grid = id FROM dbo.[group] WHERE groups = @groups;
    SELECT @kruzhid = id FROM dbo.kruzhok WHERE kruzhok = @kruzhok;
    SELECT @natid = id FROM dbo.nationality WHERE nationality = @nationality;

    UPDATE dbo.children
    SET
        FIO = @FIO,
        dateborn = @dateborn,
        address = @address,
        tel_dad = @tel_dad,
        tel_mom = @tel_mom,
        groups = @grid,
        kruzhok = @kruzhid,
        nationality = @natid
    WHERE id = @id;
END
GO

CREATE PROCEDURE [dbo].[delete_child]
    @id INT
AS
BEGIN
    DELETE FROM dbo.children
    WHERE id = @id;
END
GO

CREATE PROCEDURE dbo.select_login
    @user_login VARCHAR(10),
    @user_password VARCHAR(10)
AS
BEGIN
SELECT *
FROM dbo.table_login
WHERE user_login = @user_login
  AND user_password = @user_password;
END
GO
```
---
# Polikek_FM
> Возьми телефон, детка
>> Я знаю, ты хочешь позвонить мне сегодня
> 
> Я слил на тебя сотня и наверх ещё сотня
>> Искал тебя долго, ты плеила много
