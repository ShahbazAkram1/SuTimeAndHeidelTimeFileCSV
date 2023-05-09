package org.temporal.tagging;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.*;
import com.ibm.icu.util.*;
import de.unihd.dbs.heideltime.standalone.*;

/**
 * Created by Shahbaz Ali on 06/05/2023.
 * Copyright © 2023 Shahbaz Ali
 */

/**
 * This class provides methods for extracting temporal expressions from text using SuTime and HeidelTime,
 * and then tagging them with temporal tags.
 */
public class TemporalTagging {

    // The path to the config file for HeidelTime
    private static final String CONFIG_PROPS_PATH = "C:\\Users\\shahb\\Downloads\\SuTimeAndHeidelTimeFileCSV\\src\\main\\resources\\config.props";

    // The path to the input CSV file containing the text to be tagged
    private static final String CSV_FILE_PATH = "C:\\Users\\shahb\\Downloads\\SuTimeAndHeidelTimeFileCSV\\src\\main\\resources\\chllnge_qa2_upword.csv";

    private static final String CSV_OUTPUT_FILE_PATH = "C:\\Users\\shahb\\Downloads\\SuTimeAndHeidelTimeFileCSV\\src\\main\\resources\\chllnge_qa2_upword_output.csv";


    /**
     * Extracts temporal expressions from text using SuTime and Stanford NLP pipeline.
     *
     * @param text The text to extract temporal expressions from.
     * @return A list of TimeExpressions extracted from the text.
     */
    private static List<TimeExpression> suTime(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("sutime.binders", "0");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<TimeExpression> timexes = new ArrayList<>();
        for (CoreMap sentence : sentences) {
            timexes.addAll(sentence.get(TimeAnnotations.TimexAnnotations.class));
        }
        return timexes;
    }

    /**
     * Extracts temporal expressions from text using HeidelTime.
     *
     * @param text The text to extract temporal expressions from.
     * @return A list of Timex3 objects extracted from the text.
     */
    private static List<de.unihd.dbs.heideltime.standalone.entities.Timex3> heidelTime(String text) {
        HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.ENGLISH, DocumentType.NEWS, OutputType.TIMEML,
                CONFIG_PROPS_PATH, false);
        List<de.unihd.dbs.heideltime.standalone.entities.Timex3> timexes = heidelTime.process(text, "2022-01-01 00:00:00");
        return timexes;
    }

    public static void main(String[] args) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(CSV_FILE_PATH));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                // split each line by comma to extract the text to be tagged
                String[] sentence = line.split(cvsSplitBy);

                // extract temporal expressions using SuTime and HeidelTime
                List<TimeExpression> suTimeResults = suTime(sentence[1]);
                List<de.unihd.dbs.heideltime.standalone.entities.Timex3> heidelTimeResults = heidelTime(sentence[1]);
                // add temporal expression to output
                sb.append(String.format("%s,\"%s\"", sentence[0], sentence[1]));

                // iterate through the temporal expressions extracted using SuTime
                for (TimeExpression timeExpression : suTimeResults) {
                    sb.append(String.format(",\"<TIMEX3 tid=\"%s\" type=\"%s\" value=\"%s\">%s</TIMEX3>\"", timeExpression.getTemporal().toString(), timeExpression.getTemporal().getTimexType(), timeExpression.getTemporal().getValue(), timeExpression.getText()));
                }
                // iterate through the temporal expressions extracted using HeidelTime
                for (de.unihd.dbs.heideltime.standalone.entities.Timex3 timex : heidelTimeResults) {
                    sb.append(String.format(",\"<TIMEX3 tid=\"%s\" type=\"%s\" value=\"%s\">%s</TIMEX3>\"", timex.getId(), timex.getType(), timex.getValue(), timex.getText()));
                }
                // add new line character at the end of each line in the output
                sb.append("\n");
            }

            // create a new file to write the output
            File outputFile = new File(CSV_OUTPUT_FILE_PATH);

            // create a new FileWriter to write to the file
            FileWriter writer = new FileWriter(outputFile);

            // write the output to the file
            writer.write(sb.toString());

            // close the writer
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
