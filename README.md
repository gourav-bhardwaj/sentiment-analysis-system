# Sentiment Analysis System Setup

The Sentiment Analysis System consists of three Spring Boot services. Before running these services, you need to set up Kafka and Ollama.

## 1. Setting Up Kafka

You can either download Kafka from the official website:

[Download Kafka](https://kafka.apache.org/downloads)

OR create a `docker-compose.yml` file and use Docker Compose:

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka-broker-1:
    image: confluentinc/cp-kafka:latest
    hostname: kafka-broker-1
    ports:
      - "19092:19092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-broker-1:9092,PLAINTEXT_INTERNAL://localhost:19092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Run the following command to start Kafka:
```sh
docker-compose up -d
```

## 2. Setting Up Ollama

You can either download Ollama from the official website:

[Download Ollama](https://ollama.com/)

OR create a `docker-compose.yml` file and use Docker Compose:

```yaml
version: '3'
services:
  ollama:
    image: ollama/ollama:latest
    container_name: ollama
    ports:
      - "11434:11434"
```

Run the following command to start Ollama:
```sh
docker-compose up -d
```

Then, open a terminal and run:
```sh
ollama run mistral
```

## 3. Running the Spring Boot Applications

Once Kafka and Ollama are running, start the three Spring Boot services one by one using the following commands:

### 1. Start `user-comment-producer-service`:
```sh
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DBOOTSTRAP_SERVERS=127.0.0.1:9092"
```

### 2. Start `ecom-user-sentiment-analysis-service`:
```sh
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DAPP_ID=ecom-user-sentiment-analysis-service -DBOOTSTRAP_SERVERS=127.0.0.1:9092 -DAI_BASE_URL=http://localhost:11434 -DAI_MODEL_NAME=mistral -DAI_MODEL_TEMPERATURE=0.7"
```

### 3. Start `user-sentiment-analysis-listener`:
```sh
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DGROUP_ID=sentiment-lister-group -DTOPIC_NAMES=positive-sentiment,negative-sentiment,improvement-sentiment,output-topic -DBOOTSTRAP_SERVERS=127.0.0.1:9092 -DEMAIL_PASSWORD='app password' -DEMAIL_ID=gk97727665@gmail.com"
```
