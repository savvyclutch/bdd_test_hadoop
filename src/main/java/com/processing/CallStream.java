package com.processing;

import cascading.flow.FlowConnector;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.operation.regex.RegexParser;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextDelimited;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.GlobHfs;

import org.apache.hadoop.mapred.JobConf;

import cascading.tap.hadoop.Hfs;
import cascading.tap.hadoop.PartitionTap;
import cascading.tap.partition.DelimitedPartition;
import cascading.tuple.Fields;


import java.util.*;

// main processor class
public class CallStream {

    private static Tap createOutputTap(String path) {
        TextDelimited scheme = new TextDelimited( true, "\t");

        Hfs hfsTap = new Hfs(scheme, path);
        DelimitedPartition partition = new DelimitedPartition(new Fields("country"), "/");
        return new PartitionTap(hfsTap, partition, SinkMode.REPLACE);
    }

    /**
     * Gets the input and output path as arguments
     */
    public static void main(String[] args) {
        final List<Pipe> pipes = new ArrayList<Pipe>();
        final Map<String, Tap> sinks = new HashMap<String, Tap>();

        String inputPath = args[0];
        String outputPath = args[1];

        Fields lines = new Fields("line");
        Tap inTap = new GlobHfs(new TextLine(lines), inputPath);

        // Declare the field names used to parse out of the log file
        Fields callstreamFields = new Fields("id", "country", "time", "duration");

        // Define the regular expression used to parse the log file
        String logRegex = "^(\\d+),([a-zA-Z]{2}?),(\\d+),(\\d+)$";

        // Declare the groups from the above regex. Each group will be given a field name from 'callstreamFields'
        int[] allGroups = {1, 2, 3, 4};

        // Create the parser
        RegexParser parser = new RegexParser(callstreamFields, logRegex, allGroups);
        Pipe streamPipe = new Each("CallStream", lines, parser, callstreamFields);

        // Add new pipe
        pipes.add(streamPipe);

        // give the partition to output
        sinks.put("CallStream", createOutputTap(outputPath));

        // configure cascading
        JobConf jobConf = new JobConf();

        // pass class to the flow connector
        Properties properties;
        properties = AppProps.appProps()
                .setName(CallStream.class.toString())
                .setJarClass(CallStream.class)
                .addTags("app:cascading")
                .buildProperties(jobConf);

        // execute flow
        FlowConnector flowConnector = new Hadoop2MR1FlowConnector(properties);
        flowConnector.connect(inTap, sinks, pipes).complete();
    }
}
