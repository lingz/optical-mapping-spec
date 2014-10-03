#!/bin/bash
java -cp out/production/optical-map-spec/:deps/commons-codec-1.9.jar:deps/uncommons-maths-1.2.3.jar edu.nyu.opticalMapping.ExperimentGenerator $@
