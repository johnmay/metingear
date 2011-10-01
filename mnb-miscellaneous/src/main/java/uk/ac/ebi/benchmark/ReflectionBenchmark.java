//
///**
// * Reflections.java
// *
// * 2011.08.17
// *
// * This file is part of the CheMet library
// *
// * The CheMet library is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * CheMet is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
// */
//package uk.ac.ebi.benchmark;
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @name    Reflections – 2011.08.17
// *          Class description
// * @version $Rev$ : Last Changed $Date$
// * @author  johnmay
// * @author  $Author$ (this version)
// */
//public class ReflectionBenchmark {
//
//
//public void one() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void two() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void three() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void four() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void five() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void six() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void seven() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void eight() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void nine() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void ten() {
//    for ( int i = 0; i < 1000; i++ );
//}
//
//
//public void ifCascade( String value ) {
//    if ( value.equals( "one" ) ) {
//        one();
//    } else if ( value.equals( "two" ) ) {
//        four();
//    } else if ( value.equals( "three" ) ) {
//        three();
//    } else if ( value.equals( "four" ) ) {
//        four();
//    } else if ( value.equals( "five" ) ) {
//        five();
//    } else if ( value.equals( "six" ) ) {
//        six();
//    } else if ( value.equals( "seven" ) ) {
//        seven();
//    } else if ( value.equals( "eight" ) ) {
//        eight();
//    } else if ( value.equals( "nine" ) ) {
//        nine();
//    } else if ( value.equals( "ten" ) ) {
//        ten();
//    }
//}
//
//
//public void reflectionHashThrower( String value ) throws Exception {
//    if ( methodMap.containsKey( value ) ) {
//        methodMap.get( value ).invoke( this );
//    }
//}
//
//
//public void reflectionHashNoThrow( String value ) {
//    try {
//        if ( methodMap.containsKey( value ) ) {
//            methodMap.get( value ).invoke( this );
//        }
//    } catch ( Exception ex ) {
//    }
//}
//
//private Map<String , Method> methodMap = new HashMap<String , Method>();
//private long itterations = 1000000;
//private String[] values = new String[]{ "one" , "two" , "three" , "four" , "five" , "six" , "seven" ,
//                                        "eight" , "nine" , "ten" };
//
//
//public ReflectionBenchmark() throws NoSuchMethodException {
//    for ( String val : values ) {
//        methodMap.put( val , getClass().getDeclaredMethod( val ) );
//    }
//}
//
//
//public String timeReflections( int iter ) throws Exception {
//    long start = System.currentTimeMillis();
//    for ( int i = 0; i < iter; i++ ) {
//        Integer choice = ( int ) ( Math.random() * values.length );
//        String method = values[choice];
//        reflectionHashThrower( method );
//    }
//    long end = System.currentTimeMillis();
//    return Long.toString( end - start ) + " ms";
//}
//
//
//public String timeCaughtReflections( int iter ) {
//    long start = System.currentTimeMillis();
//    for ( int i = 0; i < iter; i++ ) {
//        Integer choice = ( int ) ( Math.random() * values.length );
//        String method = values[choice];
//        reflectionHashNoThrow( method );
//    }
//    long end = System.currentTimeMillis();
//    return Long.toString( end - start ) + " ms";
//}
//
//
//public String timeIfCascade( int iter ) {
//    long start = System.currentTimeMillis();
//    for ( int i = 0; i < iter; i++ ) {
//        Integer choice = ( int ) ( Math.random() * values.length );
//        String method = values[choice];
//        ifCascade( method );
//    }
//    long end = System.currentTimeMillis();
//    return Long.toString( end - start ) + " ms";
//}
//
//
//public static void main( String[] args ) throws Exception {
//
//    Reflections reflections = new Reflections();
//    for ( int i = 0; i < 10000000; i += 1000000 ) {
//        System.out.printf( "%d\t%s\t%s\t%s\n" , i ,
//                           reflections.timeReflections( i ) ,
//                           reflections.timeCaughtReflections( i ) ,
//                           reflections.timeIfCascade( i ) );
//    }
//}
//
//}
