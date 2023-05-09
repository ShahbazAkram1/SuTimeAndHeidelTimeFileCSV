# Temporal Tagging

This program extracts temporal expressions from a CSV file of sentences using two temporal taggers: SuTime and
HeidelTime. The program outputs the original sentence and any temporal expressions found in the sentence in the TimeML
format.

# Dependencies

This program requires the following dependencies:

* Stanford CoreNLP

* Stanford Temporal Tagger (SuTime)

* HeidelTime

* ICU4J


# Usage

1. Download the SuTimeAndHeidelTimeFileCSV.zip file.

2. Ensure that all dependencies are installed and in the classpath.

3. Create a CSV file with a header row and one sentence per row. The CSV file should have the following format:

# id,sentence

1,The event will take place on August 21, 2022 at 10:00 AM.

2,The report was published on Monday, 3 May 2021.


Each row corresponds to a sentence in the input CSV file. The **id** and **sentence** columns are copied from the input CSV file. Any temporal expressions found in the sentence are included in subsequent columns, in the TimeML format.
# License

This software is developed by **Shahbaz Ali** as a freelancer for a project on Upwork. This program is licensed under the MIT License. See the LICENSE file for details.