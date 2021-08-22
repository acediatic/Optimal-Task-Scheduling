for FILE in src/test/java/softeng/project1/full_graph_tests/inputGraphs/*; do
  if [[ $FILE == *"output"* ]]; then
    continue
  fi
  echo "*** SEQUENTIAL FOR $FILE ***"
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -v;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -v;
  echo "*** PARALLEL WITH -P 2 FOR $FILE ***"
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 2;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 2;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 2 -v;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 2 -v;
  echo "*** PARALLEL WITH -P 4 FOR $FILE ***"
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 4;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 4;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 4 -v;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 4 -v;
  echo "*** PARALLEL WITH -P 8 FOR $FILE ***"
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 8;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 8;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 2 -p 8 -v;
  java -Xmx4G -jar ./target/scheduler-2.0.jar "$FILE" 4 -p 8 -v;
done