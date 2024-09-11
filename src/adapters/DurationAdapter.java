package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        try {
            jsonWriter.value(duration.toMinutes());
        } catch (NullPointerException e) {
            jsonWriter.value("");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        try {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        } catch (NullPointerException e) {
            return null;
        }
    }
}