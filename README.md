# JUL Log Stats

Logging frameworks provide some statistics on the amount of logs you output for ingestion but those numbers are a bit problematic. This library is a simple tool that monitors the logs and counts the number of times every log is printed. It currently supports JUL (java.util.logging) but additional Java loggers can be supported as well.

The code is trivial and so is the usage. Add the following line to your application:

```java
MeasureTopLogs.install(3000);
```

The second argument represents the frequency of printing the statistics and clearning them (in milliseconds). Then look within your logs for tables such as these:

```
Message                                  | Frequency
===========================================================
Log Number 1: {0}                        | 44
Other Log {0}, {1}                       | 73
```

Notice that nothing will be printed if no logs are printed.

## License
This code is published under the terms of the MIT license.

## Future
This is a simple demo implementation. Feel free to change and add support for additional logger types.
