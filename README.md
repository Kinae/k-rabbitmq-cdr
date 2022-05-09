# K-RabbitMQ-CDR : copy/dump/restore messages

![Java](https://img.shields.io/badge/Java-%23ED8B00.svg)
[![main](https://github.com/kinae/k-rabbitmq-cdr/actions/workflows/main.yaml/badge.svg)](https://github.com/kinae/k-rabbitmq-cdr/actions/workflows/main.yaml)
![Coverage Badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Kinae/62e2df105b51624ccc775a81ed43891e/raw/k-rabbitmq-cdr.json)

K-rabbitmq-cdr allows you to copy/dump/restore messages from various sources and targets for RabbitMQ.

It satisfies a need to work with data:

- from another queue in a separated environment
- from a previous dump
- for integration tests
- and so more ...

It does not affect the source, see [Message re-queuing implementation details](https://github.com/Kinae/k-rabbitmq-cdr#Message-re-queuing-implementation-details)

## Installation

### Download a release

Precompiled binary packages can be found on the
[releases](https://github.com/Kinae/k-rabbitmq-cdr/releases) page.

### Compile from source

Clone the project, use `./gradlew shadowJar` to create an executable jar  `./build/libs/k-rabbitmq-cdr.jar`

## Usage

For help, you can use the `-h or --help` parameter that will display a list of parameters with description.

`java -jar ./build/libs/k-rabbitmq-cdr.jar --help`

### Examples

To transfer from RabbitMQ to RabbitMQ with one thread being the consumer and the producer. It's the solution that use the least
memory but is slower (same thread doing both jobs). If your messages are heavy, consider using `BUFFERED` as `transfer-type`

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AMQP --to-uri amqp://admin:admin@192.168.0.12:5672/vHost --target-queue cart-update
```

To dump messages from RabbitMQ to AWS S3 while using BUFFERED as process-type (using a FIFO queue to buffer). That means
consumer from source do not have to wait for the completion of the AWS S3 upload from consuming messages from the queue.

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AWS_S3 --region eu-west-1 --bucket mybucket --prefix cart/update/
--transfer-type BUFFERED --process-type SEQUENTIAL
```

If the target is slower than the source, you can use multiple threads to produce messages much faster. Do not overuse thread if
your CPU can not handle it. It would have the opposite effect. You can only use the `thread` parameter with `BUFFERED` and
`PARALLEL` options

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AWS_S3 --region eu-west-1 --bucket mybucket --prefix cart/update/
--transfer-type BUFFERED --process-type PARALLEL --thread 3
```

To restore message from AWS S3 to RabbitMQ queue __with the original order__ use the `sorted` parameter. It sorts messages
before loading them (usable only with source `FILE`or `AWS_S3`). It might take some times if you have a lot to process. It has
no effect if it's used with the options `PARALLEL`

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AWS_S3 --region eu-west-1 --bucket mybucket --prefix cart/update/
--transfer-type BUFFERED --process-type SEQUENTIAL --sorted true
```

Use the parameter `max-messages` in order to only consume a portion of messages. Can be combined with sorted to ensure the first
50 messages are loaded.

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AWS_S3 --region eu-west-1 --bucket mybucket --prefix cart/update/
--transfer-type BUFFERED --process-type SEQUENTIAL --max-messages 50 --sorted true
```

#### AWS credentials

See [developer guide credentials AWS SDK](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) to
provide credentials to the AWS S3 client.

### Options

| Name | Component | Description |  Example |
|---|---|---|---|
| --source-type |  | The type of the source | `AMQP / AWS_S3 / FILE`  |
| --source-uri | AMQP | The URI of the source. | `amqp://admin:admin@localhost:5672/%2F` |
| --source-queue | AMQP  | The queue of the source. |`cart-update-dlq` |
| --target-type |  | The type of the target | `AMQP / AWS_S3 / FILE` |  
| --target-uri | AMQP | The URI of the target. | `amqp://admin:admin@localhost:5672/%2F` |
| --target-queue | AMQP  | The queue of the target.|`cart-update` |
| --directory | FILE | Path of the directory to use to load/save messages | `/tmp/2022_05_10/` | 
| --region | AWS_S3 | The region of your S3 bucket | `eu-west-1` | 
| --bucket | AWS_S3 | The name of the bucket to use to load/save messages | `mybucket` | 
| --prefix | AWS_S3 | The prefix of the S3 key to use | `cart/update/` | 
| --transfer-type | ALL | The type of transfer to use. Default is DIRECT | `DIRECT / BUFFERED` | 
| --process-type | BUFFERED | Type of process to use when using BUFFERED as --transfer-type (default is SEQUENTIAL) | `SEQUENTIAL / PARALLEL` | 
| --max-messages | ALL | Maximum number of messages (default is 0 for all) | `12`
| --thread | PARALLEL | Number of threads for the target when using PARALLEL as --process-type (default is 2) | `4`
| --sorted | FILE / AWS_S3 | Sort messages listed before processing. Has no effect if --process-type is PARALLEL with more than 1 thread | false |

## Message re-queuing implementation details

k-rabbitmq-cdr use the standard [`basic.get` from the AMQP API](https://www.rabbitmq.com/amqp-0-9-1-reference.html#basic.get)
without the auto acknowledgment and does not manually acknowledge either. This means during the process time messages won't be
available to any other process. As soon as the connection is closed, RabbitMQ will return all un-acknowledged messages back to
the original queue.
