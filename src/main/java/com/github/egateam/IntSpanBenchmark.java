/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class IntSpanBenchmark {

    // don't use Jackson to avoid adding dependence
    private static class RunFile {
        void run() {
            String r1 = readFile("/r1.yml");
            String r2 = readFile("/r2.yml");

            r1 = r1.replaceFirst("---\\s+1: ", "").replaceAll("\\n", "");
            r2 = r2.replaceFirst("---\\s+1: ", "").replaceAll("\\n", "");

            IntSpan set1;
            IntSpan set2;

            int step = 1;
            {
                System.out.printf("step %d create\n", step);
                step++;

                long start = System.nanoTime();
                for ( int i = 1; i <= 100; i++ ) {
                    set1 = new IntSpan(r1);
                    set2 = new IntSpan(r2);
                }
                long end = System.nanoTime();

                System.out.printf("duration %f\n", (end - start) / 1000.0 / 1000.0 / 1000.0);
            }

            {
                System.out.printf("step %d intersect\n", step);
                step++;

                long start = System.nanoTime();
                for ( int i = 1; i <= 1000; i++ ) {
                    set1 = new IntSpan(r1);
                    set2 = new IntSpan(r2);
                    set1.intersect(set2);
                }
                long end = System.nanoTime();

                System.out.printf("duration %f\n", (end - start) / 1000.0 / 1000.0 / 1000.0);
            }

            {
                System.out.printf("step %d intersect runlist\n", step);

                long start = System.nanoTime();
                for ( int i = 1; i <= 1000; i++ ) {
                    set1 = new IntSpan(r1);
                    set2 = new IntSpan(r2);
                    set1.intersect(set2).runlist();
                }
                long end = System.nanoTime();

                System.out.printf("duration %f\n", (end - start) / 1000.0 / 1000.0 / 1000.0);
            }

        }

        String readFile(String filename) {
            String content = "";
            InputStream in = getClass().getResourceAsStream(filename);

            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(in)) ) {
                String line;
                while ( (line = reader.readLine()) != null ) {
                    content += line;
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }

            return content;
        }
    }

    private static class RunBenchmark {
        void run() {
            for ( int i : new int[]{2, 3, 4, 5, 6} ) {
                System.out.printf("step %d\n", i);
                long start = System.nanoTime();
                testAddRange(i);
                long end = System.nanoTime();
                System.out.printf("duration %f\n", (end - start) / 1000.0 / 1000.0 / 1000.0);

            }
        }

        void testAddRange(int step) {
            ArrayList<Integer> vec1 = new ArrayList<>();

            for ( int i : new int[]{
                1, 30,
                32, 149,
                153, 155,
                159, 247,
                250, 250,
                253, 464,
                516, 518,
                520, 523,
                582, 585,
                595, 600,
                622, 1679
            } ) {
                vec1.add(i);
            }

            ArrayList<Integer> vec2 = new ArrayList<>();
            vec2.add(100);
            vec2.add(1000000);

            for ( int i = 1; i <= 50000; i++ ) {
                IntSpan set = new IntSpan();

                if ( step >= 2 ) {
                    set.addRange(vec1);
                }
                if ( step >= 3 ) {
                    set.addRange(vec2);
                }
                if ( step >= 4 ) {
                    set.asString();
                }
                if ( step >= 5 ) {
                    for ( int j = 1; j <= 200; j++ ) {
                        set.addPair(j, j);
                    }
                }
                if ( step >= 6 ) {
                    for ( int j = 1; j <= 200; j++ ) {
                        ArrayList<Integer> vec3 = new ArrayList<>();
                        vec3.add(j * 5);
                        vec3.add(j * 10);
                        set.addRange(vec3);
                    }
                }
            }
        }
    }

    /*
    mvn clean verify
    time java -jar target/jintspan-0.1.1-SNAPSHOT.jar benchmark
     */
    public static void main(String[] args) {
        String jarName = new java.io.File(IntSpanBenchmark.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath())
            .getName();
        String prefix = "java -jar " + jarName;
        String usage = String.format("Usage:\n    %s benchmark\n    %s file\n", prefix, prefix);

        if ( args.length == 0 ) {
            System.err.print(usage);
        } else if ( Objects.equals(args[0], "benchmark") ) {
            new RunBenchmark().run();
        } else if ( Objects.equals(args[0], "file") ) {
            new RunFile().run();
        } else {
            System.err.printf("Unrecognized command %s", args[0]);
        }
    }
}
