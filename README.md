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

```java
2022-05-20 13:42:10.773 | main             | initiating a direct transfer between AMQP => AMQP
2022-05-20 13:42:10.776 | main             | creating AMQP connection on amqp://admin:admin@192.168.0.1:5672/%2F targeting queue cart-update-dlq
2022-05-20 13:42:10.959 | main             | creating AMQP connection on amqp://admin:admin@localhost:5672/%2F targeting queue cart-update-dlq
2022-05-20 13:42:11.011 | main             | reading [.........................] 0/100000 (0.00%)
2022-05-20 13:42:11.011 | main             | writing [.........................] 0/100000 (0.00%)
2022-05-20 13:42:11.011 | main             | 
2022-05-20 13:42:30.777 | pool-1-thread-1  | reading [#####....................] 22096/100000 (22.10%)
2022-05-20 13:42:30.778 | pool-1-thread-1  | writing [#####....................] 22096/100000 (22.10%)
2022-05-20 13:42:30.778 | pool-1-thread-1  | 
2022-05-20 13:42:50.772 | pool-1-thread-1  | reading [###########..............] 47445/100000 (47.45%)
2022-05-20 13:42:50.773 | pool-1-thread-1  | writing [###########..............] 47445/100000 (47.45%)
2022-05-20 13:42:50.773 | pool-1-thread-1  | 
2022-05-20 13:43:10.776 | pool-1-thread-1  | reading [##################.......] 72528/100000 (72.53%)
2022-05-20 13:43:10.776 | pool-1-thread-1  | writing [##################.......] 72528/100000 (72.53%)
2022-05-20 13:43:10.776 | pool-1-thread-1  | 
2022-05-20 13:43:30.776 | pool-1-thread-1  | reading [########################.] 98387/100000 (98.39%)
2022-05-20 13:43:30.777 | pool-1-thread-1  | writing [########################.] 98387/100000 (98.39%)
2022-05-20 13:43:30.777 | pool-1-thread-1  | 
2022-05-20 13:43:32.057 | main             | reading [#########################] 100000/100000 (100.00%)
2022-05-20 13:43:32.058 | main             | writing [#########################] 100000/100000 (100.00%)
```

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
your CPU can not handle it. It would have the opposite effect. You can only use `thread` parameters with `BUFFERED` and
`PARALLEL` options

```sh
java -jar ./build/libs/k-rabbitmq-cdr.jar \
--source-type AMQP --source-uri amqp://admin:admin@localhost:5672/%2F --source-queue cart-update-dlq \
--target-type AWS_S3 --region eu-west-1 --bucket mybucket --prefix cart/update/
--transfer-type BUFFERED --process-type PARALLEL --source-thread 2 --target-thread 4
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

You can profile a profile with the option `--profile` for the ProfileCredentialsProvider.

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
| --region | AWS_S3 | The region of your bucket | `eu-west-1` | 
| --bucket | AWS_S3 | The name of the bucket to use to load/save messages | `mybucket` | 
| --prefix | AWS_S3 | The prefix of the key to use | `cart/update/` | 
| --profile | AWS_S3 | The profile to use | `prod` | 
| --transfer-type | ALL | The type of transfer to use. Default is DIRECT | `DIRECT / BUFFERED` | 
| --process-type | BUFFERED | Type of process to use when using BUFFERED as --transfer-type (default is SEQUENTIAL) | `SEQUENTIAL / PARALLEL` | 
| --max-messages | ALL | Maximum number of messages (default is 0 for all) | `12`
| --source-thread | PARALLEL | Number of threads to read data when using PARALLEL as --process-type (default is 2) | `2`
| --target-thread | PARALLEL | Number of threads to write data when using PARALLEL as --process-type (default is 2) | `2`
| --sorted | FILE / AWS_S3 | Sort messages listed before processing. Has no effect if --process-type is PARALLEL with more than 1 thread (default is false) | `false` |
| --interval | | Specify the progression update interval in milliseconds (default is 2000) | `1000` |
| --body-only | | Only use the body and discard the headers and properties of messages (default is false) | `false` |

## Message re-queuing implementation details

k-rabbitmq-cdr use the standard [`basic.get` from the AMQP API](https://www.rabbitmq.com/amqp-0-9-1-reference.html#basic.get)
without the auto acknowledgment and does not manually acknowledge either. This means during the process time messages won't be
available to any other process. As soon as the connection is closed, RabbitMQ will return all un-acknowledged messages back to
the original queue.

## Additional information about the project

I started this small project from a need at my work: I can not copy message from one RabbitMQ to another.
I have multiple needs, and currently I have found nothing that exist to fulfill them.

The project can integrate three different sources and targets (AMQP, AWS S3, File system).
What you can do:
- copy all RabbitMQ messages from a queue into a bucket S3
- transfer from one RabbitMQ queue in production env to RabbitMQ queue in debug env
- restore previous messages from AWS S3 into a RabbitMQ queue to test with thousands of messages.
- dump messages from a RabbitMQ queue to your file system (to restore them later, to analyse and use all shell commands, and more)
- and more...

It has multiple parameters to let you control how you want to do it. You currently have three modes:
- Direct: the consumer is the producer, one thread is used to consume and produce messages.
- Buffered sequential: one consumer consumes all messages first and push them in a FIFO java.util.Queue then consume this Queue to push into the target
- Buffered parallel: same as the sequential but multiple producers consume the Queue at the same time.

It really depends on your source and target but for example, if you consume from a RabbitMQ queue and you want to push in a AWS S3 Bucket, you should use the Buffered parallel mode.
Since loading from RabbitMQ is really fast, you want more threads to push into the bucket as fast as possible.


I am also using it for integration testing by creating a test case (ie: a message) that will be dynamically loaded.

Since we can not afford to use all new technologies instantly, I wanted to try and learn by myself.
If you find mistakes or bad practices, feel free to point them, so I can learn.

New technologies used for me are Gradle (instead of the good Maven), testcontainer (just awesome !) and AssertJ.
