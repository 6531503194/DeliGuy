#!/bin/bash
# Kafka Topic Initialization Script

echo "Waiting for Kafka to be ready..."
sleep 10

echo "Creating Kafka topics..."

# Order Topics
/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic order-created \
  --if-not-exists

/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic order-updated \
  --if-not-exists

# Restaurant Topics
/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic order-assigned \
  --if-not-exists

/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic delivery-assigned \
  --if-not-exists

/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic delivery-status \
  --if-not-exists

# Payment Topics
/opt/kafka/bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic payment-processed \
  --if-not-exists

echo "Listing all topics..."
/opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

echo "Kafka topics created successfully!"
