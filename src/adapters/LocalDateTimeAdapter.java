package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        try {
            jsonWriter.value(localDateTime.format(dtf));
        } catch (NullPointerException e) {
            jsonWriter.value("");
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
