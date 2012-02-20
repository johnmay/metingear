/**
 * RegularExpressions.java
 *
 * 2011.08.17
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.benchmark;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @name    RegularExpressions – 2011.08.17
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class RegularExpressionsBenchmark {

    private static long iterations = 1000000;


    public static void stringMatch() {

        String source = "A    5";
        for (int i = 0; i < iterations; i++) {
            source.matches("^A\\s{1,4}\\d+");
        }

    }


    public static void stringMatchPattern() {


        for (int i = 0; i < iterations; i++) {
            for (String source : Arrays.asList("something here 2+", " and here 2-")) {
                boolean match = source.endsWith("+") || source.endsWith("-");
            }
        }

    }


    public static void compiledMatch() {

        Pattern pattern = Pattern.compile("[-+]$");

        for (int i = 0; i < iterations; i++) {
            for (String source : Arrays.asList("something here 2+", " and here 2-")) {
                boolean match = pattern.matcher(source).find();
            }
        }

    }


    public static void compiledMatchPattern() {

        String source = "A    5";
        Pattern pattern = Pattern.compile("\\AA\\s{1,4}(\\d+)");

        for (int i = 0; i < iterations; i++) {
            Matcher m = pattern.matcher(source);
            if (m.matches()) {
                String val = m.group(1);
            }
        }

    }


    public static void main(String[] args) {

        {
            long start = System.currentTimeMillis();
            compiledMatch();
            long end = System.currentTimeMillis();
            System.out.println("Compiled pattern" + (end - start));
        }
        {
            long start = System.currentTimeMillis();
            stringMatch();
            long end = System.currentTimeMillis();
            System.out.println("String endsWith();" + (end - start));
        }


        String line = "012345678";

        if(line.length() >= 9){
            System.out.println(line.substring(0, 10));
        }



    }
}
