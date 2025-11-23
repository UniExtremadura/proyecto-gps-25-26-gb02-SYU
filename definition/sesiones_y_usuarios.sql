-- Script de creación de la base de datos del microservicio de sesiones y usuarios.
-- Ejecutar en una base de datos limpia. O en una que ya haya sido usada con este script
-- para resetearla.

drop table if exists Usuarios cascade;
drop table if exists Sesiones;
drop table if exists ArtistasFav;
drop table if exists CancionesFav;
drop table if exists AlbumesFav;
drop table if exists MerchFav;

CREATE TABLE Usuarios (
    idUsuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nick VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(30) NOT NULL,
    apellido1 VARCHAR(30) NOT NULL,
    apellido2 VARCHAR(30),
    biografia VARCHAR(400),
    fechaReg DATE DEFAULT current_date,
    email VARCHAR(30) NOT NULL UNIQUE,
    contrasena VARCHAR(64) NOT NULL, 
    imagen VARCHAR(255),
    idArtista INT
);

CREATE TABLE Sesiones (
    idSesion INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    fechaValidez DATE DEFAULT current_date,
    idUsuario Int,
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE ArtistasFav (
    idArtista INT,
    idUsuario INT,
    PRIMARY KEY (idArtista, idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE CancionesFav (
    idCancion INT,
    idUsuario INT,
    PRIMARY KEY (idCancion, idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE AlbumesFav(
    idAlbum INT,
    idUsuario INT,
    PRIMARY KEY (idAlbum, idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE MerchFav(
    idMerch INT,
    idUsuario INT,
    PRIMARY KEY (idMerch, idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

-- INSERT INTO Usuarios VALUES (
-- 	0, 'Jaimito', 'Jaime', 'Manzanos', null, current_date, 'jaimin@gmail.com', 'asdaadsdiuquibiuasbdas', null 
-- );

-- SELECT * FROM Usuarios;

-- DELETE FROM Usuarios;

-- DESCOMENTAR PARA BORRAR TODO
-- drop table if exists Usuarios cascade;
-- drop table if exists Sesiones;
-- drop table if exists ArtistasFav;
-- drop table if exists CancionesFav;
-- drop table if exists AlbumesFav;
-- drop table if exists MerchFav;