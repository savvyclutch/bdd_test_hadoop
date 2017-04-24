This is source code for [BDD tests for Hadoop with Cucumber](http://www.savvyclutch.com/qa/BDD-tests-for-Hadoop-with-Cucumber-part-I/) articles

Build container:

> docker build -t dbb_test_hadoop .

Run container:

> docker run -v `pwd`:/opt/bdd_test_hadoop dbb_test_hadoop
