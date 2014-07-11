/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.metingear.view;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats chemical names with stylistic conventions.
 *
 * @author John May
 */
public final class ChemicalNameHtmlStyler {

    static final Pattern GLYCERALDEHYDE_STEREOHEMISTRY   = Pattern.compile("(?<=-|^|\\s|\\()[DL](?=[- ])");
    static final Pattern GLYCERALDEHYDE_D_STEREOHEMISTRY = Pattern.compile("(?<=-|^|\\s|\\()[D](?=[- ])");
    static final Pattern GLYCERALDEHYDE_L_STEREOHEMISTRY = Pattern.compile("(?<=-|^|\\s|\\()[L](?=[- ])");

    static final Pattern ANOMERIC_STEREOCHEMISTRY       = Pattern.compile("(?<=-|^|\\s)([Aa]lpha|[Bb]eta)(?=[-, ])");
    static final Pattern ANOMERIC_ALPHA_STEREOCHEMISTRY = Pattern.compile("(?<=-|^|\\s|,)[Aa]lpha(?=[-', ])");
    static final Pattern ANOMERIC_BETA_STEREOCHEMISTRY  = Pattern.compile("(?<=-|^|\\s|,)[Bb]eta(?=[-', ])");
    static final Pattern CIP_STEREOCHEMISTRY            = Pattern.compile("(?<=\\()((?:\\d*(?:[RSEZ]|cis|trans))(?:,\\d*[RSEZ])*)(?=\\))");
    static final Pattern CT_PREFIX                      = Pattern.compile("^(cis|trans)");
    static final Pattern ELEMENT                        = Pattern.compile("(?<=-|^|,|\\s)([NCOPS])(?:\\((\\d*)\\)|(\\d*))(?=\\z|[-', ])");
    static final Pattern STEREOSPECIFIC_NUMBERING       = Pattern.compile("^(sn)(?=[-', ])");
    static final Pattern SENTENCE_CASE                  = Pattern.compile("(?<![A-Z])[A-Z][a-z]{2,}");


    public static String styleHtml(String name) {
        if (name == null) return null;
        if (name.length() < 4) return name;
        String html = name;
        html = GLYCERALDEHYDE_D_STEREOHEMISTRY.matcher(html).replaceAll("\u1D05");
        html = GLYCERALDEHYDE_L_STEREOHEMISTRY.matcher(html).replaceAll("\u029F");
        html = ANOMERIC_ALPHA_STEREOCHEMISTRY.matcher(html).replaceAll("α");
        html = ANOMERIC_BETA_STEREOCHEMISTRY.matcher(html).replaceAll("β");
        html = CIP_STEREOCHEMISTRY.matcher(html).replaceAll("<i>$1</i>"); // TODO: number is not italic
        html = CT_PREFIX.matcher(html).replaceAll("<i>$1</i>");
        html = ELEMENT.matcher(html).replaceAll("<i>$1</i><sup>$2$3</sup>");
        html = STEREOSPECIFIC_NUMBERING.matcher(html).replaceAll("<i>$1</i>");

        // this could be optional
        Matcher matcher = SENTENCE_CASE.matcher(html);
        int i = 0;
        while (matcher.find()) {
            html = matcher.replaceFirst(matcher.group(0).toLowerCase(Locale.ENGLISH));
            matcher = SENTENCE_CASE.matcher(html);
        }
        
        return "<html>" + html + "</html>";
    }
}
