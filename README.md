Back end do sistema cpa

Necessário java 17
Crie um arquivo env.properties com os mesmos atributos que o env.properties.example e informe sua senha do banco de dados e uma Key para o JWT

A key pode ser gerada neste site: https://www.allkeysgenerator.com/
selecione Encryption Key - 256bit e marque Yes no HEX

Crie o banco de dados cpa em seu MySQL

Quem estiver usando o vscode pode instalar a extensão Spring Boot Extension Pack

# **Plataforma CPA**
## *Plataforma web desenvolvida para atender a Comissão Própria de Avaliação do BioPark.* O projeto utiliza Spring Boot como framework do backend e React JS para o frontend.

### Configuração do ambiente de desenvolvimento ###
Para configurar o ambiente de desenvolvimento, siga os seguintes passos:

### Pré-requisitos: ###
* Java 17 instalado

### Instalação: ###
* Clone o repositório
* Abra o projeto em seu IDE
* Instale as dependências do backend executando `mvn clean package`

### Executando o projeto: ###
* Execute o comando `mvn spring-boot:run` para iniciar o backend
  