package devarea.fr.discord.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import discord4j.common.util.Snowflake;

import java.io.IOException;

public class SnowflakeModuleSerializer {
    /*
        Add this module to your ObjectMapper to serialize and deserialize Snowflake.

        -> mapper.registerModule(SnowflakeModuleSerializer.snowflakeModule);

     */
    public static SimpleModule snowflakeModule;

    static {
        snowflakeModule = new SimpleModule();
        snowflakeModule.addSerializer(Snowflake.class, new SnowflakeSerializer());
        snowflakeModule.addDeserializer(Snowflake.class, new SnowflakeDeserializer());
    }

    private static class SnowflakeSerializer extends StdSerializer<Snowflake> {

        public SnowflakeSerializer(Class<Snowflake> t) {
            super(t);
        }

        public SnowflakeSerializer() {
            this(null);
        }

        @Override
        public void serialize(Snowflake snowflake, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(snowflake.asString());
        }
    }

    private static class SnowflakeDeserializer extends StdDeserializer<Snowflake> {

        public SnowflakeDeserializer(Class<Snowflake> t) {
            super(t);
        }

        public SnowflakeDeserializer() {
            this(null);
        }

        @Override
        public Snowflake deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return Snowflake.of(jsonParser.getValueAsString());
        }
    }
}
