for FILE in src/test/java/softeng/project1/full_graph_tests/inputGraphs/*; do
  echo "*** SEQUENTIAL FOR $FILE ***"
  java -Xmx4G -jar ./target/scheduler-0.0-jar-with-dependencies.jar "$FILE" 2;
  java -Xmx4G -jar ./target/scheduler-0.0-jar-with-dependencies.jar "$FILE" 4;
done