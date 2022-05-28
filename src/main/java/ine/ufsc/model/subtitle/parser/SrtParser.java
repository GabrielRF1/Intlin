/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.subtitle.parser;

import ine.ufsc.model.subtitle.Subtitle;
import ine.ufsc.model.subtitle.BadlyFomattedSubtitleFileException;
import ine.ufsc.utils.TimeStamp;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 *
 * @author Gabriel
 */
public class SrtParser {

    public LinkedList<Subtitle> parse(File srtFile) throws IOException, BadlyFomattedSubtitleFileException {
        String content = new String(Files.readAllBytes(Paths.get(srtFile.getPath())), StandardCharsets.UTF_8);
 
        final LinkedList<Subtitle> subtitles = new LinkedList<>();
        final String[] entries = Pattern.compile("(?:.+\\D)+")
                .matcher(content.stripLeading())
                .results()
                .map((match) -> {
                    return match.group();
                })
                .toArray(String[]::new);

        for (String entry : entries) {
            final Subtitle subtitle = parseEntry(entry);
            subtitles.add(subtitle);
        }

        return subtitles;
    }

    private Subtitle parseEntry(String entry) throws BadlyFomattedSubtitleFileException {
        final String[] data = entry.split("\\n");
        if (data.length < 3) {
            throw new BadlyFomattedSubtitleFileException("File is not correctly formatted");
        }

        final String[] times = data[1].split(" --> ");
        if (times.length != 2) {
            throw new BadlyFomattedSubtitleFileException("File is not correctly formatted");
        }

        final TimeStamp start = parseTime(times[0].trim());
        final TimeStamp end = parseTime(times[1].trim());

        String line = "";
        for (int i = 2; i < data.length; i++) {
            line += data[i] + '\n';
        }
        return new Subtitle(Integer.parseInt(data[0]), start, end, line.trim());
    }

    private TimeStamp parseTime(String timeStampString) throws BadlyFomattedSubtitleFileException {
        final String[] hourMinuteSeconds = timeStampString.split(":");
        if (hourMinuteSeconds.length != 3) {
            throw new BadlyFomattedSubtitleFileException("File is not correctly formatted");
        }

        final String[] secondesMillisec = hourMinuteSeconds[2].split(",");
        if (secondesMillisec.length != 2) {
            throw new BadlyFomattedSubtitleFileException("File is not correctly formatted");
        }

        final int hour = Integer.parseInt(hourMinuteSeconds[0].trim());
        final int minute = Integer.parseInt(hourMinuteSeconds[1].trim());
        final int second = Integer.parseInt(secondesMillisec[0].trim());
        final int millisecond = Integer.parseInt(secondesMillisec[1].trim());

        return new TimeStamp(hour, minute, second, millisecond);
    }
}
