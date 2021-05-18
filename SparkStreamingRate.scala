import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
// Create Spark Session
val spark = SparkSession
  .builder()
  .master("local")
  .appName("Rate Source")
  .getOrCreate()

// Set Spark logging level to ERROR to avoid various other logs on console.
spark.sparkContext.setLogLevel("ERROR")
val initDF = (spark
  .readStream
  .format("rate")
  .option("rowsPerSecond", 1)
  .load()
  )

println("Streaming DataFrame : " + initDF.isStreaming)
val resultDF = initDF
    .withColumn("result", col("value") + lit(1))
	
resultDF
  .writeStream
  .outputMode("append")
  .option("truncate", false)
  .format("console")
  .start()
  .awaitTermination()

// Reference: https://medium.com/expedia-group-tech/apache-spark-structured-streaming-first-streaming-example-1-of-6-e8f3219748ef
