To run with custom PDF skin

   mvn install

To run with built-in PDF skin

   mvn install -Dcustomization.dir=

Output is at target/dita/out/taskbook-sample.pdf

Note: this fails on unix. However, the work around is to make .external-resources/apahe-ant-*/bin's scripts executable




