-- Criar o banco de dados
CREATE DATABASE Banco;

-- Usar o banco de dados criado
USE Banco;

-- Tabela "usuarios"
CREATE TABLE  usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    senha VARCHAR(255),
    tipo ENUM('gestor', 'funcionario')
);

-- Tabela "atendimento"
CREATE TABLE  atendimento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255),
    id_equipe INT
);

-- Tabela "usuarios_autorizados"
CREATE TABLE  usuarios_autorizados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
	senha VARCHAR(255),
	tipo ENUM('gestor', 'funcionario')
        
    );

-- Tabela "equipe"
CREATE TABLE  equipe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    cargo VARCHAR(100)
);

DROP DATABASE Banco;

