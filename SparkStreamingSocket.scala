// Import Libraries
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

// Create Spark Session
val spark = SparkSession
  .builder()
  .master("local")
  .appName("Socket Source")
  .getOrCreate()

// Set Spark logging level to ERROR.
spark.sparkContext.setLogLevel("ERROR")
// Define host and port number to Listen.
val host = "127.0.0.1"
val port = "9999"

// Create Streaming DataFrame by reading data from socket.
val initDF = (spark
  .readStream
  .format("socket")
  .option("host", host)
  .option("port", port)
  .load())

// Check if DataFrame is streaming or Not.
println("Streaming DataFrame : " + initDF.isStreaming)
// Perform word count on streaming DataFrame
val wordCount = (initDF
  .select(explode(split(col("value"), " ")).alias("words"))
  .groupBy("words")
  .count()
  )

// Print Schema of DataFrame
println("Schema of DataFame wordCount.")
println(wordCount.printSchema())
wordCount
  .writeStream
  .outputMode("update") // Try "update" and "complete" mode.
  .option("truncate", false)
  .format("console")
  .start()
  .awaitTermination()

// Reference: https://medium.com/expedia-group-tech/apache-spark-structured-streaming-input-sources-2-of-6-6a72f798838c
// Download ncat here: Replacement for nc (Windows)
// https://nmap.org/ncat/
// ncat -lk 9999
