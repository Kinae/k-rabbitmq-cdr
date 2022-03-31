# k-rabbitmq-cdr
RabbitMQ tools to Copy/Dump/Restore messages

[![main](https://github.com/kinae/k-rabbitmq-cdr/actions/workflows/main.yaml/badge.svg)](https://github.com/kinae/k-rabbitmq-cdr/actions/workflows/main.yaml)


TODO:
Source :

- AWS S3
- FILE

Target :

- FILE
- AWS S3

Use special connector for AMQP => AMQP => linkedList FIFO or file I/O (option ?) (// vs sequential)

Zip for FILE / AWS S3 ?

Limit number of message ? props for full header + properties (or just body)
verbose => slf4j level

more info log more debug log

info to change tmpdir in readme

Logger pattern ?

processType // ==> allow more thread to push ? 
