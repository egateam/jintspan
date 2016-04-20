/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import java.util.ArrayList;

public class IntSpanBenchmark {

    private static class RunBenchmark {
        void runBenchmark() {
            for ( int i : new int[]{2, 3, 4, 5, 6} ) {
                System.out.print(String.format("step %d\n", i));
                long start = System.nanoTime();
                testAddRange(i);
                long end = System.nanoTime();
                System.out.println(String.format("duration %f", (end - start) / 1000.0 / 1000.0 / 1000.0));

            }
        }

        void testAddRange(int step) {
            ArrayList<Integer> vec1 = new ArrayList<>();

            for ( int i : new int[]{1, 30, 32, 149, 153, 155, 159, 247, 250, 250, 253, 464, 516, 518,
                520, 523, 582, 585, 595, 600, 622, 1679} ) {
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
    time java -jar target/jintspan-0.1.1-SNAPSHOT.jar
     */
    public static void main(String[] arg) {
        RunBenchmark run = new RunBenchmark();
        run.runBenchmark();
    }
}
