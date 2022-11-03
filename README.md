# Defects4J Case Studies
This repo contains a pair of case studies that apply the causal metamorphic testing approach.

## Running the case studies
Within the `src/test/apache` there are two packages `lang` and `math`, each
containing a series of JUnit test classes. These were manually constructed but
the metamorphic relations (which are listed as comments) were produced using
the python code from the evaluation.

To execute these tests, simply run with JUnit. The simplest approach is to open this directory
in IntelliJ (ensuring the pom.xml is at the top-level), which should automatically install the
necessary packages once the pom.xml is recognised.

To switch between the fixed and buggy versions of each repository, uncomment/comment
the fixed/buggy import in `FormatTest.java` and `VarianceTest.java`.

For the second case study (`apache.lang`), there are several test classes. The underlying approach
is the same, however, we decided to use separate classes to avoid repeating code. Each of these
classes simply lists the metamorphic relations and sets an appropriate follow-up value.

The jar file for the instrumentation agent used for the second case study is included at the top-level, and
is named `instrumentation-agent.jar`. The source code for this jar is included
in `Agent.zip`.