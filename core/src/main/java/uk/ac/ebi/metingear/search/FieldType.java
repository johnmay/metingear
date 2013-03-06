/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.metingear.search;

import org.apache.lucene.index.Term;

/**
 *          FieldType – 2011.09.29 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public enum FieldType {

    ACCESSION("Accession"),
    NAME("Name"),
    ABBREVIATION("Abbreviation"),
    ANNOTATION("Annotation"),
    TYPE("Type");
    public final String name;
    public final Term term;

    private FieldType(String name) {
        this.name = name;
        this.term = new Term(name);
    }

    public String getName() {
        return name;
    }

    public Term getTerm(String text) {
        return term.createTerm(text);
    }
    private static String[] allFields;

    public static String[] getAllFields() {
        if (allFields == null) {
            allFields = getArray(values());
        }
        return allFields;
    }

    public static String[] getArray(FieldType[] types) {
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getName();
        }
        return names;
    }
}
